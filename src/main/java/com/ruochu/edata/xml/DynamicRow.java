package com.ruochu.edata.xml;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.List;

/**
 * @author : RanPengCheng
 * @date : 2020/6/9 16:10
 */
public class DynamicRow implements Serializable {
    private static final long serialVersionUID = 1L;


    @XStreamAsAttribute
    private String firstValuePosition;

    @XStreamAsAttribute
    private String field;

    /** 单元格 */
    @XStreamImplicit(itemFieldName = "cell")
    private List<CellConf> cells;

    /**
     * 行高
     */
    @XStreamAsAttribute
    private Integer rowHeight;

    private Integer startRow;


    public String getFirstValuePosition() {
        return firstValuePosition;
    }

    public List<CellConf> getCells() {
        return cells;
    }

    public String getField() {
        return field;
    }

    public Integer getRowHeight() {
        return rowHeight;
    }

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }
}
