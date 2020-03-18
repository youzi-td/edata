package com.ruochu.edata.excel.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RanPengCheng
 */
public class ESheet implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<Integer, ERow> datas = new HashMap<>();

    private int maxRowIndex = 0;

    private String sheetName;

    public ESheet(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getValue(int rowIndex, int colIndex) {
        ERow eRow = datas.get(rowIndex);
        if (null == eRow) {
            return "";
        }
        String value = eRow.getData(colIndex);
        return value == null ? "" : value;
    }

    public int getMaxColumnIndex(int rowIndex) {
        if (datas.containsKey(rowIndex)){
            return datas.get(rowIndex).getMaxColumnIndex();
        }

        return -1;
    }

    public void addData(int rowIndex, int colIndex, String data) {
        ERow eRow = datas.get(rowIndex);
        if (null == eRow){
            eRow = new ERow();
            datas.put(rowIndex, eRow);
            if (maxRowIndex < rowIndex){
                maxRowIndex = rowIndex;
            }
        }
        eRow.addData(colIndex, data);
    }

    public String getSheetName() {
        return sheetName;
    }

    public int getMaxRowIndex() {
        return maxRowIndex;
    }

}
