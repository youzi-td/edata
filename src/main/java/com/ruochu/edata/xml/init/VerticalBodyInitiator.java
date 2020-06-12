package com.ruochu.edata.xml.init;

import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.BodyConf;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.Rectangle;

import java.util.ArrayList;
import java.util.List;

import static com.ruochu.edata.util.EmptyChecker.notEmpty;

/**
 * 竖表表体initiator
 *
 * @author RanPengCheng
 * @date 2019/7/13 8:41
 */
public class VerticalBodyInitiator {

    private BodyConf body;
    private boolean isRead;
    private String sheetCode;

    VerticalBodyInitiator(BodyConf body, String sheetCode, boolean isRead) {
        this.body = body;
        this.isRead = isRead;
        this.sheetCode = sheetCode;
    }

    void init(){
        checkAttr();

        List<CellConf> cells = body.getCells();
        if (null == cells){
            cells = new ArrayList<>();
            body.setCells(cells);
        } else {
            initCellPosition(cells);
        }

        List<Rectangle> rectangles = body.getRectangles();
        if (notEmpty(rectangles)){
            new RectangleInitiator(rectangles).init();

            for (Rectangle rectangle : rectangles){
                cells.addAll(rectangle.getCells());
            }
            body.setRectangles(null);
        }

        new CellInitiator(cells, sheetCode, isRead).init();

        if (notEmpty(body.getDynamicRows())) {
            new DynamicRowInitiator(sheetCode, body.getDynamicRows()).init();
        }
    }

    private void initCellPosition(List<CellConf> cells) {
        for (CellConf cell : cells){
            String position = cell.getPosition();
            if (EmptyChecker.isEmpty(position)){
                throw new XmlConfigException("verticalBody里的cell必须指定坐标position");
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
        if (EmptyChecker.isEmpty(body.getCells()) && EmptyChecker.isEmpty(body.getRectangles())){
            throw new XmlConfigException("verticalBody的cell和rectangle不能同时为空！");
        }
    }
}
