package com.ruochu.edata.xml;

import com.ruochu.edata.util.EmptyChecker;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表体
 *
 * @author RanPengCheng
 * @date 2019/7/6 16:57
 */
public class BodyConf implements Serializable{
    private static final long serialVersionUID = 1L;

    /** 第一行数据的行号（横表） */
    @XStreamAsAttribute
    private String firstValuePosition;

    /** 单元格 */
    @XStreamImplicit(itemFieldName = "cell")
    private List<CellConf> cells;

    /** 矩形区域（竖表） */
    @XStreamImplicit(itemFieldName = "rectangle")
    private List<Rectangle> rectangles;

    private Map<String, CellConf> cellMap;

    public CellConf getCell(String field) {
        if (cellMap == null) {
            cellMap = new HashMap<>();
            if (EmptyChecker.notEmpty(cells)) {
                for (CellConf cell : cells) {
                    cellMap.put(cell.getField(), cell);
                }
            }
        }
        return cellMap.get(field);
    }

    public String getFirstValuePosition() {
        return firstValuePosition;
    }

    public List<CellConf> getCells() {
        return cells;
    }

    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    public void setRectangles(List<Rectangle> rectangles) {
        this.rectangles = rectangles;
    }

    public void setCells(List<CellConf> cells) {
        this.cells = cells;
    }
}
