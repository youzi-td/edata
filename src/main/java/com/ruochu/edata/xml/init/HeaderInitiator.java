package com.ruochu.edata.xml.init;

import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.HeaderConf;
import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.util.EmptyChecker;

/**
 * 表头Initiator
 *
 * @author RanPengCheng
 * @date 2019/7/10 12:39
 */
public class HeaderInitiator {

    private HeaderConf header;
    private String sheetCode;
    private boolean isRead;

    HeaderInitiator(HeaderConf header,String sheetCode, boolean isRead) {
        this.header = header;
        this.isRead = isRead;
        this.sheetCode = sheetCode;
    }

    void init(){
        checkAttr();
        initCellPosition();

        new CellInitiator(header.getCells(),sheetCode, isRead).init();
    }

    private void initCellPosition() {
        for (CellConf cell : header.getCells()){
            String position = cell.getPosition();
            if (EmptyChecker.isEmpty(position)){
                throw new XmlConfigException("header里的cell必须指定坐标position");
            }
            if (!CoordinateUtil.isExcelPosition(position)){
                throw new XmlConfigException("[".concat(position).concat("]不是一个合法的excel坐标"));
            }
            int[] xy = CoordinateUtil.toNumberPosition(position);
            cell.setColIndex(xy[0]);
            cell.setRowIndex(xy[1]);
        }
    }

    private void checkAttr() {
        if (EmptyChecker.isEmpty(header.getCells())){
            throw new XmlConfigException("header里的cell元素不能为空！");
        }
    }
}
