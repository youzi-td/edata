package com.ruochu.edata.read.validator;

import com.ruochu.edata.enums.ErrorDataTypeEnum;
import com.ruochu.edata.excel.model.ESheet;
import com.ruochu.edata.excel.model.EWorkbook;
import com.ruochu.edata.read.result.ErrorData;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.ExcelConf;
import com.ruochu.edata.xml.SheetConf;

import java.util.LinkedList;
import java.util.List;

/**
 * 模板校验
 *
 * @author RanPengCheng
 * @date 2019/7/18 19:15
 */
public class TemplateValidator {

    private ExcelConf excelConf;
    private EWorkbook templateWorkbook;
    private EWorkbook userWorkbook;

    private String currentSheetName;
    private ESheet currentTempSheet;
    private ESheet currentUserSheet;
    private SheetConf currentSheetConf;


    private List<ErrorData> errorDataList = new LinkedList<>();

    public TemplateValidator(ExcelConf excelConf,
                             EWorkbook templateWorkbook,
                             EWorkbook userWorkbook) {
        this.excelConf = excelConf;
        this.templateWorkbook = templateWorkbook;
        this.userWorkbook = userWorkbook;
    }

    public List<ErrorData> validate() {
        if (!checkSheetIntegrity(excelConf.getCheckSheetSequence())) {
            return errorDataList;
        }
        for (ESheet tempSheet : templateWorkbook.getSheets()) {
            this.currentSheetName = tempSheet.getSheetName();
            this.currentUserSheet = userWorkbook.get(tempSheet.getSheetName());
            this.currentTempSheet = tempSheet;
            this.currentSheetConf = excelConf.getSheetBySheetName(currentSheetName);

            validateSheet();
        }


        return errorDataList;
    }

    private void validateSheet() {
        // 行
        for (int row = 1; row <= currentTempSheet.getMaxRowIndex(); row++) {
            // 列
            for (int col = 1; col <= currentTempSheet.getMaxColumnIndex(row); col++) {
                String tempValue = currentTempSheet.getValue(row, col);
                String userValue = currentUserSheet.getValue(row, col);

                if (EmptyChecker.isEmpty(tempValue) || isIgnore(row, col, tempValue) || tempValue.equals(userValue)) {
                    continue;
                }

                if (currentSheetConf.isFollowTitleCell(row, col) && userValue.startsWith(tempValue)){
                    continue;
                }

                String errorMsg = "模板错误";
                addError(row, col, tempValue, userValue, errorMsg, currentSheetName);
            }
        }

    }


    private boolean isIgnore(int rowIndex, int colIndex, String tempValue) {
        if (EmptyChecker.isEmpty(currentSheetConf)){
            return false;
        }
        return excelConf.getGlobalFilter().isIgnore(rowIndex, colIndex, tempValue)
                ||
                currentSheetConf.getTemplateFilter().isIgnore(rowIndex, colIndex, tempValue);
    }

    /**
     * 检查sheet序列
     */
    private boolean checkSheetIntegrity(boolean checkSheetSequence) {
        List<ESheet> sheets = templateWorkbook.getSheets();
        List<ESheet> userSheets = userWorkbook.getSheets();
        for (int i = 0; i < sheets.size(); i++) {
            String tempSheetName = sheets.get(i).getSheetName();
            if (userSheets.size() < i + 1) {
                addError(tempSheetName, "未找到表[".concat(tempSheetName).concat("]"));
                return false;
            }

            if (checkSheetSequence) {
                if (!tempSheetName.equals(userSheets.get(i).getSheetName())) {
                    addError(tempSheetName, "表名不匹配，第" + (i + 1) + "个表应该为[" + tempSheetName + "]");
                    return false;
                }
            } else {
                if (null == userWorkbook.get(tempSheetName)) {
                    addError(tempSheetName, "未找到表[".concat(tempSheetName).concat("]"));
                    return false;
                }
            }
        }
        return true;
    }


    private void addError(String sheetName, String errorMsg) {
        errorDataList.add(new ErrorData(sheetName, errorMsg, ErrorDataTypeEnum.TEMAPLATE));
    }

    private void addError(int rowIndex,
                          int colIndex,
                          String title,
                          String cellValue,
                          String errorMsg,
                          String sheetName) {
        errorDataList.add(new ErrorData(rowIndex, colIndex, title, cellValue, errorMsg, sheetName, ErrorDataTypeEnum.TEMAPLATE));
    }
}
