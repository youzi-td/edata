package com.ruochu.edata.xml.init;

import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.Rectangle;

import java.util.List;

/**
 * 矩形Initiator
 *
 * @author RanPengCheng
 * @date 2019/7/10 12:40
 */
public class RectangleInitiator {

    private List<Rectangle> rectangles;
    private Rectangle currentRectangle;

    RectangleInitiator(List<Rectangle> rectangles) {
        this.rectangles = rectangles;
    }

    void init() {
        for (Rectangle rectangle : rectangles) {
            this.currentRectangle = rectangle;

            checkAttr();
            initCellPosition();
        }
    }

    private void initCellPosition() {
        String firstCell = currentRectangle.getFirstCell();
        String lastCell = currentRectangle.getLastCell();
        int[] first = CoordinateUtil.toNumberPosition(firstCell);
        int[] last = CoordinateUtil.toNumberPosition(lastCell);

        int row = first[1];
        int col = first[0];
        int lastRow = last[1];
        int lastCol = last[0];

        if (row > lastRow || col > lastCol) {
            throw new XmlConfigException("rectangle的firstCell[".concat(firstCell).concat("与lastCell[").concat(lastCell).concat("]错乱"));
        }

        int total = (lastRow - row + 1) * (lastCol - col + 1);
        List<CellConf> cells = currentRectangle.getCells();
        if (cells.size() != total){
            throw new XmlConfigException("rectangle里应该包含" + total + "个cell,实际为" + cells.size() + "个！");
        }

        int rowNum = lastCol - col + 1;
        for (int i = 0; i < total;) {
            CellConf cell = cells.get(i);

            cell.setRowIndex(row);
            cell.setColIndex(col + (i % rowNum));
            if (++i % rowNum == 0){
                row++;
            }
        }

    }

    private void checkAttr() {
        String firstCell = currentRectangle.getFirstCell();
        String lastCell = currentRectangle.getLastCell();
        if (EmptyChecker.isEmpty(firstCell)) {
            throw new XmlConfigException("rectangle的firstCellPosition不能为空！");
        } else if (!CoordinateUtil.isExcelPosition(firstCell)) {
            throw new XmlConfigException("rectangle的firstCellPosition[".concat(firstCell).concat("]不是一个excel坐标！"));
        }

        if (EmptyChecker.isEmpty(lastCell)) {
            throw new XmlConfigException("rectangle的lastCellPosition不能为空！");
        } else if (!CoordinateUtil.isExcelPosition(lastCell)) {
            throw new XmlConfigException("rectangle的lastCellPosition[".concat(lastCell).concat("]不是一个excel坐标！"));
        }

        if (EmptyChecker.isEmpty(currentRectangle.getCells())) {
            throw new XmlConfigException("rectangle的cell元素不能为空！");
        }

    }
}
