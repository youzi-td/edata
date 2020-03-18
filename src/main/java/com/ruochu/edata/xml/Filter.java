package com.ruochu.edata.xml;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 过滤器
 *
 * @author RanPengCheng
 * @date 2019/7/6 16:57
 */
public class Filter implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 需要过滤的值，逗号隔开 */
    @XStreamAsAttribute
    private String values;

    /** 需要过滤的坐标(excel坐标，形如:d3)，逗号隔开 */
    @XStreamAsAttribute
    private String positions;


    /** 值集 */
    private Set<String> valueSet = new HashSet<>();
    /** 坐标集 */
    private Set<String> positionSet = new HashSet<>();


    public boolean isIgnore(int rowIndex, int colIndex, String value){
        return valueSet.contains(value) || positionSet.contains(rowIndex + "," + colIndex);
    }

    public boolean isIgnore(CellConf cell, String value){
        return isIgnore(cell.getRowIndex(), cell.getColIndex(), value);
    }

    public void addFilterVaule(String value){
        this.valueSet.add(value);
    }

    public void addFilterPosition(String position){
        this.positionSet.add(position);
    }

    public String getValues() {
        return values;
    }

    public String getPositions() {
        return positions;
    }


}
