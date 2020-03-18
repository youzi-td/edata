package com.ruochu.edata.excel.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author RanPengCheng
 */
public class ERow implements Serializable {

    private static final long serialVersionUID = 1L;

    ERow(){}

    private Map<Integer, String> rowMap = new HashMap<>();

    private int maxColumnIndex = 0;

    String getData(int columnIndex){
        return rowMap.get(columnIndex);
    }


    void addData(int colIndex, String data){
        rowMap.put(colIndex, data);
        if(maxColumnIndex < colIndex){
            maxColumnIndex = colIndex;
        }
    }

    Integer getMaxColumnIndex(){
        return maxColumnIndex;
    }
}
