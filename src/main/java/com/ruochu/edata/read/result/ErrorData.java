package com.ruochu.edata.read.result;

import com.ruochu.edata.enums.ErrorDataTypeEnum;
import com.ruochu.edata.util.CoordinateUtil;

import java.io.Serializable;

/**
 * 错误信息
 *
 * @author RanPengCheng
 * @date 2019/7/14 17:13
 */
public class ErrorData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer rowIndex;
    private Integer colIndex;
    private String title;
    private String value;
    private String position;
    private String errorMsg;
    private String sheetName;
    private String errorType;


    public ErrorData(int rowIndex,
                     int colIndex,
                     String title,
                     String value,
                     String errorMsg,
                     String sheetName,
                     ErrorDataTypeEnum errorType) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.title = title;
        this.value = value;
        this.position = CoordinateUtil.toExcelPosition(colIndex, rowIndex);
        this.errorMsg = errorMsg;
        this.sheetName = sheetName;
        this.errorType = errorType.getType();
    }

    public ErrorData(String sheetName, String errorMsg, ErrorDataTypeEnum errorType) {
        this.errorMsg = errorMsg;
        this.errorType = errorType.getType();
        this.sheetName = sheetName;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public String getPosition() {
        return position;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getSheetName() {
        return sheetName;
    }

    public String getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return "ErrorData{" +
                "rowIndex=" + rowIndex +
                ", colIndex=" + colIndex +
                ", title='" + title + '\'' +
                ", value='" + value + '\'' +
                ", postion='" + position + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", sheetName='" + sheetName + '\'' +
                ", errorType='" + errorType + '\'' +
                '}';
    }
}
