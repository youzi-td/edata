package com.ruochu.edata.xml;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.List;

/**
 * 矩形区域（竖表）
 *
 * @author RanPengCheng
 * @date 2019/7/6 16:57
 */
public class Rectangle implements Serializable {
    private static final long serialVersionUID = 1L;

    /**  */
    @XStreamAsAttribute
    private String firstCell;

    @XStreamAsAttribute
    private String lastCell;

    @XStreamImplicit(itemFieldName = "cell")
    private List<CellConf> cells;

    public String getFirstCell() {
        return firstCell;
    }

    public String getLastCell() {
        return lastCell;
    }

    public List<CellConf> getCells() {
        return cells;
    }
}
