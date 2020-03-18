package com.ruochu.edata.xml.init;

import com.ruochu.edata.xml.BodyConf;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.util.EmptyChecker;

/**
 * 横表表体Initiator
 *
 * @author RanPengCheng
 * @date 2019/7/10 12:39
 */
public class HorizontalBodyInitiator {

    private BodyConf body;
    private boolean isRead;
    private String sheetCode;

    HorizontalBodyInitiator(BodyConf body, String sheetCode, boolean isRead) {
        this.body = body;
        this.isRead = isRead;
        this.sheetCode = sheetCode;
    }

    void init(){
        checkAttr();
        initCellPosition();

        new CellInitiator(body.getCells(), sheetCode, isRead).init();
    }

    private void initCellPosition() {
        String firstValuePosition = body.getFirstValuePosition();
        int[] position = CoordinateUtil.toNumberPosition(firstValuePosition);
        int row = position[1];
        int col = position[0];
        for (CellConf cell : body.getCells()){
            cell.setRowIndex(row);
            cell.setColIndex(col++);
        }
    }

    private void checkAttr() {
        String firstValuePosition = body.getFirstValuePosition();
        if (EmptyChecker.isEmpty(firstValuePosition)){
            throw new XmlConfigException("horizontalBody必须指定第一个value的坐标firstValuePosition");
        }else if (!CoordinateUtil.isExcelPosition(firstValuePosition)){
            throw new XmlConfigException("firstValuePosition[".concat(firstValuePosition).concat("]不是一个excel坐标！"));
        }

        if (EmptyChecker.isEmpty(body.getCells())){
            throw new XmlConfigException("body里的cell元素不能为空!");
        }

    }
}
