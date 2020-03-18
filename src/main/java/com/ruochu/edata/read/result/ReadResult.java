package com.ruochu.edata.read.result;

import com.ruochu.edata.util.EmptyChecker;

import java.io.Serializable;
import java.util.List;

/**
 * 导入的结果
 *
 * @author RanPengCheng
 * @date 2019/7/14 17:11
 */
public class ReadResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ErrorData> errorDatas;

    private List<SheetData> sheetDatas;

    private boolean success = Boolean.TRUE;
    private boolean templateError;

    public ReadResult(List<ErrorData> errorDatas, List<SheetData> sheetDatas, boolean templateError) {
        this.errorDatas = errorDatas;
        this.sheetDatas = sheetDatas;
        this.templateError = templateError;

        if (templateError || EmptyChecker.notEmpty(errorDatas)){
            success = Boolean.FALSE;
        }
    }

    public SheetData getSheetData(String sheetCode){
        if (EmptyChecker.notEmpty(sheetDatas)){
            for (SheetData sheetData : sheetDatas){
                if (sheetData.getSheetCode().equals(sheetCode)){
                    return sheetData;
                }
            }
        }
        return null;
    }

    public <T> List<T> getBodyData(String sheetCode, Class<T> clazz) {
        return getSheetData(sheetCode).getBodyData(clazz);
    }

    public List<DataMap> getBodyData(String sheetCode) {
        return getSheetData(sheetCode).getBodyData();
    }

    public <T> T getHeaderData(String sheetCode, Class<T> clazz) {
        return getSheetData(sheetCode).getHeaderData(clazz);
    }

    public DataMap getHeaderData(String sheetCode) {
        return getSheetData(sheetCode).getHeaderData();
    }

    public List<ErrorData> getErrorDatas() {
        return errorDatas;
    }

    public List<SheetData> getSheetDatas() {
        return sheetDatas;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isTemplateError() {
        return templateError;
    }
}
