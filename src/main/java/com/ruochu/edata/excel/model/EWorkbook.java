package com.ruochu.edata.excel.model;

import com.ruochu.edata.exception.ERuntimeException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RanPengCheng
 */
public class EWorkbook implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<ESheet> sheets = new ArrayList<>();

    public boolean contain(String sheetName) {
        for (ESheet sheet : sheets) {
            if (sheet.getSheetName().equals(sheetName)) {
                return true;
            }
        }
        return false;
    }

    public List<ESheet> getSheets() {
        return sheets;
    }

    public ESheet get(String sheetName){
        for (ESheet sheet : sheets) {
            if (sheet.getSheetName().equals(sheetName)) {
                return sheet;
            }
        }
        return null;
    }

    public ESheet get(int index){
        return sheets.get(index);
    }

    public void addSheet(ESheet sheet){
        if (contain(sheet.getSheetName())){
            throw new ERuntimeException("sheet重复");
        }
        sheets.add(sheet);
    }

    public int size(){
        return sheets.size();
    }


}
