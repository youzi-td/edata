package com.ruochu.edata.read;

import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.enums.ErrorDataTypeEnum;
import com.ruochu.edata.enums.RuleTypeEnum;
import com.ruochu.edata.enums.TableTypeEnum;
import com.ruochu.edata.excel.model.ESheet;
import com.ruochu.edata.read.result.DataMap;
import com.ruochu.edata.read.result.ErrorData;
import com.ruochu.edata.read.result.ReadResult;
import com.ruochu.edata.read.result.SheetData;
import com.ruochu.edata.read.validator.RuleValidatorFactory;
import com.ruochu.edata.read.validator.TemplateValidator;
import com.ruochu.edata.util.Context;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.*;

import java.util.*;

import static com.ruochu.edata.constant.Constants.NUMBER_PATTERN;

/**
 * 导入的实现类
 *
 * @author RanPengCheng
 * @date 2019/7/22 18:27
 */
public class DefaultReadService extends AbstractReadService {

    private List<ErrorData> errorDataList = new LinkedList<>();
    private List<SheetData> sheetDataList;

    private String cSheetName;
    private String cSheetCode;
    private SheetConf cSheetConf;

    private ESheet uSheetData;

    public DefaultReadService(String templatePath, String xmlPath) {
        super(templatePath, xmlPath);
        this.sheetDataList = new ArrayList<>(excelConf.getSheets().size());
    }

    @Override
    protected ReadResult read() {
        // 校验模板
        if (excelConf.getCheckTemplate()) {
            errorDataList = new TemplateValidator(excelConf, templateWorkbook, userWorkbook).validate();
            if (EmptyChecker.notEmpty(errorDataList)) {
                return new ReadResult(errorDataList, null, true);
            }
        }

        // 校验数据
        validate();
        return new ReadResult(errorDataList, sheetDataList, false);
    }

    private void validate() {
        for (SheetConf sheetConf : excelConf.getSheets()) {
            this.cSheetConf = sheetConf;
            this.cSheetName = sheetConf.getSheetName();
            this.cSheetCode = sheetConf.getSheetCode();

            this.uSheetData = userWorkbook.get(cSheetName);
            if (EmptyChecker.isEmpty(uSheetData)) {
                // 如果用户上传的excel不包含该sheet，有两种可能
                // 1.xml的sheetName配置错误，应该在开发测试阶段能够很快速的检查出来
                // 2.应用允许这种情况，否则应该进行模板校验
                continue;
            }

            sheetValidate();
        }

        // 清除校验器的残留数据
        RuleValidatorFactory.clearValidator();
    }

    private void sheetValidate() {
        HeaderConf header = cSheetConf.getHeader();
        DataMap headerData = null;
        List<DataMap> bodyData = null;
        if (EmptyChecker.notEmpty(header)) {
            headerData = cellsValidate(header.getCells());
        }


        if (TableTypeEnum.HORIZONTAL.equals(cSheetConf.getTableType())){
            bodyData = horizontalBodyValidate();
        }else if (TableTypeEnum.VERTICAL.equals(cSheetConf.getTableType())){
            bodyData = new ArrayList<>(1);
            bodyData.add(cellsValidate(cSheetConf.getVerticalBody().getCells()));
        }

        SheetData sheetData = new SheetData(headerData, bodyData, cSheetCode, cSheetName);
        this.sheetDataList.add(sheetData);
    }

    private List<DataMap> horizontalBodyValidate() {
        List<CellConf> cells = cSheetConf.getHorizontalBody().getCells();
        int currentRow = cells.get(0).getRowIndex();
        int maxRow = uSheetData.getMaxRowIndex();
        List<DataMap> bodyData = new ArrayList<>(maxRow - currentRow + 1);

        int serialBlankRow = 0;
        while (serialBlankRow < maxHorizontalSerialBlankRow && currentRow <= maxRow){
            if (isBlankRow(cells)){
                currentRow++;
                serialBlankRow++;
                continue;
            }
            DataMap dataMap = new DataMap(cSheetCode, currentRow, cells);
            for (CellConf cell : cells){
                cell.setRowIndex(currentRow);
                String value = cellValidate(cell);
                dataMap.put(cell.getField(), value);
            }
            // 一行校验完成后再增加行号
            for (CellConf cell : cells) {
                cell.setRowIndex(cell.getRowIndex() + 1);
            }
            currentRow++;
            serialBlankRow = 0;
            bodyData.add(dataMap);
        }
        return bodyData;
    }

    private boolean isBlankRow(List<CellConf> cells) {
        for (CellConf cell : cells){
            String value = uSheetData.getValue(cell.getRowIndex(), cell.getColIndex());
            if (EmptyChecker.notEmpty(value)){
                return false;
            }
        }
        return true;
    }

    private DataMap cellsValidate(List<CellConf> cells) {
        DataMap dataMap = new DataMap(cSheetCode, cells);
        for (CellConf cell : cells) {
            String value = this.cellValidate(cell);
            dataMap.put(cell.getField(), value);
        }

        return dataMap;
    }


    private String cellValidate(CellConf cell){
        String value = getCellValue(cell);

        List<Rule> rules = cell.getRules();
        List<Condition> conditions = cell.getConditions();

        if (EmptyChecker.notEmpty(rules)) {
            if (!ruleValidate(rules, cell, value)) {
                return value;
            }
        }

        if (EmptyChecker.notEmpty(conditions)) {
            conditionValidate(conditions, cell, value);
        }
        return value;
    }

    private String getCellValue(CellConf cell) {
        String value = uSheetData.getValue(cell.getRowIndex(), cell.getColIndex());
        if (excelConf.getGlobalFilter().isIgnore(cell, value) || cSheetConf.getValueFilter().isIgnore(cell, value)){
            value = "";
        } else if (EmptyChecker.notEmpty(value)) {
            if (cell.isDate() && NUMBER_PATTERN.matcher(value).matches()) {
                String format = "";
                for (Rule rule : cell.getRules()) {
                    if (RuleTypeEnum.DATE.getType().equals(rule.getType())) {
                        format = rule.getExpression().split(",")[0];
                    }
                }
                value = getDateStr(value, format);
            }
        }

        return value;
    }

    private String getDateStr(String value, String format) {
        if (EmptyChecker.isEmpty(format)) {
            format = Constants.DEFAULT_DATE_FORMAT;
        }

        double num = Double.parseDouble(value);
        int days = (int) num;
        if (days > 60) {
            days--;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(1900, 0, 0);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        long time = (long) ((num - days) * 1000 * 60 * 60);
        Date date = new Date(calendar.getTimeInMillis() + time);

        return Context.getDateFormat(format).format(date);
    }



    private void conditionValidate(List<Condition> conditions, CellConf cell, String value) {
        for (Condition condition : conditions) {
            if (needConditionValidate(condition)){
                ruleValidate(condition.getRules(), cell, value);
            }
        }
    }

    private boolean needConditionValidate(Condition condition) {
        String targetValue = getValueByField(condition.getTarget());
        for (String tValue : targetValue.split(Constants.SEPARATOR)){
            if (!condition.getValuesSet().contains(tValue)){
                return false;
            }
        }
        return true;
    }


    private boolean ruleValidate(List<Rule> rules, CellConf cell, String value) {
        for (Rule rule : rules) {
            if (RuleTypeEnum.CUSTOM.getType().equals(rule.getType())) {
                Map<String, String> customValuesMap = rule.getCustomValuesMap();
                for (String field : customValuesMap.keySet()) {
                    customValuesMap.put(field, getValueByField(field));
                }
            } else if (RuleTypeEnum.BOOLEAN.getType().equals(rule.getType())) {
                rule.setExpression(dealBooleanExpression(rule.getExpression(), rule.getExpFields()));
            }

            if (!RuleValidatorFactory.getValidator(rule.getType()).validate(value, rule)) {
                addErrorData(cell, value, rule.getErrorMsg());
                return false;
            }
        }

        return true;
    }

    private String dealBooleanExpression(String expression, List<String> fields) {
        if (EmptyChecker.isEmpty(expression) || EmptyChecker.isEmpty(fields)) {
            return expression;
        }
        String expr = expression;
        String[] values = new String[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            String value = getValueByField(field);
            if (EmptyChecker.isEmpty(value)) {
                value = "0";
            }
            values[i] = value;
        }

        return String.format(expr, values);
    }

    private String getValueByField(String fieldExp) {

        String field = fieldExp;
        String sheetCode = cSheetCode;
        if (fieldExp.indexOf('.') > 0) {
            field = fieldExp.split("\\.")[1];
            sheetCode = fieldExp.split("\\.")[0];
        }

        ESheet eSheet;
        CellConf cellConf;
        if (sheetCode.equals(cSheetCode)) {
            eSheet = uSheetData;
            cellConf = cSheetConf.getCell(field);
        } else {
            SheetConf sheetConf = excelConf.getSheetBySheetCode(sheetCode);
            cellConf = sheetConf.getCell(field);
            eSheet = userWorkbook.get(sheetConf.getSheetName());
        }

        return eSheet.getValue(cellConf.getRowIndex(), cellConf.getColIndex());
    }

    private void addErrorData(CellConf cellConf, String value, String errorMsg) {
        addErrorData(cellConf.getRowIndex(), cellConf, value, errorMsg);
    }

    private void addErrorData(int rowIndex, CellConf cellConf, String value, String errorMsg) {
        ErrorData errorData = new ErrorData(
                rowIndex,
                cellConf.getColIndex(),
                cellConf.getTitle(),
                value,
                errorMsg,
                cSheetName,
                ErrorDataTypeEnum.DATA);
        this.errorDataList.add(errorData);
    }

}
