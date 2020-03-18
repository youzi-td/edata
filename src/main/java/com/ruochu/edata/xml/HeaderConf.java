package com.ruochu.edata.xml;

import com.ruochu.edata.util.EmptyChecker;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表头
 *
 * @author RanPengCheng
 * @date 2019/7/6 16:58
 */
public class HeaderConf implements Serializable {
    private static final long serialVersionUID = 1L;

    @XStreamImplicit(itemFieldName = "cell")
    private List<CellConf> cells;

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

    public List<CellConf> getCells() {
        return cells;
    }
}
