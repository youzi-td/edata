package com.ruochu.edata.xml.init;

import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.enums.RuleTypeEnum;
import com.ruochu.edata.enums.ValTypeEnum;
import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 单元格Initiator
 *
 * @author RanPengCheng
 * @date 2019/7/10 12:40
 */
public class CellInitiator {

    private List<CellConf> cells;
    private CellConf currentCell;
    private boolean isRead;
    private String sheetCode;

    CellInitiator(List<CellConf> cells, String sheetCode, boolean isRead){
        this.cells = cells;
        this.isRead = isRead;
        this.sheetCode = sheetCode;
    }


    void init(){
        for (CellConf cell : cells){
            this.currentCell = cell;
            checkAttr();
            if (isRead) {
                // 整理校验规则
                tidyRules();

                if (EmptyChecker.notEmpty(cell.getRules())){
                    new RuleInitiator(cell.getRules(), cell.getTitle()).init();
                }
                if (EmptyChecker.notEmpty(cell.getConditions())){
                    new ConditionInitiator(cell.getConditions(), cell.getTitle()).init();
                }
            }
        }
    }

    private void tidyRules() {
        String valType = currentCell.getValType();
        List<Rule> rules = currentCell.getRules();
        if (EmptyChecker.isEmpty(rules)){
            rules = new ArrayList<>();
        }
        if (EmptyChecker.notEmpty(valType)){
            Rule rule = new Rule();
            rule.setType(valType);
            rule.setXmlExpression(currentCell.getFormat());

            if (currentCell.isPercentNumber()) {
                String format = currentCell.getFormat();
                format = format.substring(0, format.length() - 1);
                String[] split = format.split(Constants.SEPARATOR);
                format = String.format("%s,%s", Integer.parseInt(split[0]) - 2, Integer.parseInt(split[1]) + 2);
                rule.setXmlExpression(format);
            }

            rules.add(rule);
        }

        if (currentCell.getRequired()){
            boolean flag = true;
            for (Rule r : rules){
                // 如果rule里有对cell的必填非必填进行了描述，则忽略cell的required属性
                if (RuleTypeEnum.REQUIRED.getType().equals(r.getType()) || RuleTypeEnum.BLANK.getType().equals(r.getType())) {
                    flag = false;
                    break;
                }
            }

            if (flag){
                Rule rule = new Rule();
                rule.setType(RuleTypeEnum.REQUIRED.getType());
                rules.add(rule);
            }
        }

        if (currentCell.getUnique()) {
            Rule uniqueRule = new Rule();
            uniqueRule.setType(RuleTypeEnum.UNIQUE.getType());
            rules.add(uniqueRule);
        }

        if (EmptyChecker.notEmpty(currentCell.getMaxLength())){
            Rule rule = new Rule();
            rule.setType(RuleTypeEnum.MAX_LENGTH.getType());
            rule.setXmlExpression(currentCell.getMaxLength().toString());
            rules.add(rule);
        }

        if (!rules.isEmpty()){
            currentCell.setRules(rules);
            for (Rule rule : rules){
                if (RuleTypeEnum.NUMBER.getType().equals(rule.getType())){
                    currentCell.setIsNumber(Boolean.TRUE);
                    return;
                }
                if (RuleTypeEnum.DATE.getType().equals(rule.getType())){
                    currentCell.setIsDate(Boolean.TRUE);
                }
                if (RuleTypeEnum.SELECTIONS.getType().equals(rule.getType())) {
                    rule.setSplit(currentCell.getSplit());
                }
            }
        }
    }

    private void checkAttr() {
        String title = currentCell.getTitle();
        if (EmptyChecker.isEmpty(title)){
            throw new XmlConfigException("cell的title不能为空！");
        }

        String field = currentCell.getField();
        if (EmptyChecker.isEmpty(field)){
            throw new XmlConfigException("cell的field不能为空，cell:" + title + "");
        }else if (SheetInitiator.FIELDS_MAP.get().get(sheetCode).contains(field)){
            throw new XmlConfigException("cell的field重复,field：" + field);
        }
        SheetInitiator.FIELDS_MAP.get().get(sheetCode).add(field);

        // 坐标校验
        Integer rowIndex = currentCell.getRowIndex();
        Integer colIndex = currentCell.getColIndex();
        if (EmptyChecker.isEmpty(rowIndex) || EmptyChecker.isEmpty(colIndex)){
            throw new XmlConfigException("坐标异常！cell:" + title);
        }
        String position = CoordinateUtil.toExcelPosition(colIndex, rowIndex);
        Map<String, String> xyMap = SheetInitiator.XYS_MAP.get().get(sheetCode);
        if (xyMap.containsKey(position)){
            throw new XmlConfigException("cell[" + title + "]与cell[" + xyMap.get(position) + "]的坐标重复！");
        }
        xyMap.put(position, title);


        String valType = currentCell.getValType();
        if (null != valType){
            if (!ValTypeEnum.exist(valType)){
                throw new XmlConfigException("不合法的valType[" + valType + "]");
            }

            String format = currentCell.getFormat();
            if (ValTypeEnum.NUMBER.getType().equals(valType)){
                String newFormat = format;
                if (format.endsWith("%")) {
                    currentCell.setPercentNumber(true);
                    newFormat = format.substring(0, format.length() - 1);
                }

                if (EmptyChecker.isEmpty(newFormat)){
                    throw new XmlConfigException("number类型必须指定数字格式format，格式为:[整数位,小数位]");
                }

                if (!newFormat.matches(Constants.NUMBER_FORMAT)){
                    throw new XmlConfigException("数字格式format[" + format + "]不合法，格式为:[整数位,小数位]");
                }

                if (currentCell.isPercentNumber()) {
                    String[] split = newFormat.split(Constants.SEPARATOR);
                    newFormat = String.format("%s,%s", Integer.parseInt(split[0]) - 2, Integer.parseInt(split[1]) + 2);
                    if (!newFormat.matches(Constants.NUMBER_FORMAT)){
                        throw new XmlConfigException("数字格式format[" + format + "]不合法，格式为:[整数位,小数位]");
                    }
                }
            }

            if (ValTypeEnum.DATE.getType().equals(format) && EmptyChecker.isEmpty(format)){
                currentCell.setFormat(Constants.DEFAULT_DATE_FORMAT);
            }
        }
    }
}
