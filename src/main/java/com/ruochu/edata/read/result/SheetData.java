package com.ruochu.edata.read.result;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * {描述}
 *
 * @author RanPengCheng
 * @date 2019/7/14 17:12
 */
public class SheetData implements Serializable {

    private static final long serialVersionUID = 1L;


    private DataMap headerData;
    private List<DataMap> bodyData;

    private String sheetCode;
    private String sheetName;

    public SheetData(DataMap headerData, List<DataMap> bodyData, String sheetCode, String sheetName) {
        this.headerData = headerData;
        this.bodyData = bodyData;
        this.sheetCode = sheetCode;
        this.sheetName = sheetName;
    }

    public <T> List<T> getBodyData(Class<T> clazz) {
        List<T> list = new LinkedList<>();
        for (DataMap map : bodyData) {
            list.add(map.getObject(clazz));
        }
        return list;
    }

    public <T> T getHeaderData(Class<T> clazz) {
        if (null != headerData) {
            return headerData.getObject(clazz);
        }
        return null;
    }

    public DataMap getHeaderData() {
        return headerData;
    }

    public List<DataMap> getBodyData() {
        return bodyData;
    }

    public String getSheetCode() {
        return sheetCode;
    }

    public String getSheetName() {
        return sheetName;
    }
}
