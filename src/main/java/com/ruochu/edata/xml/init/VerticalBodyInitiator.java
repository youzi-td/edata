package com.ruochu.edata.xml.init;

import com.ruochu.edata.xml.BodyConf;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.Rectangle;
import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.EmptyChecker;

import java.util.ArrayList;
import java.util.List;

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
        }

        List<Rectangle> rectangles = body.getRectangles();
        if (EmptyChecker.notEmpty(rectangles)){
            new RectangleInitiator(rectangles).init();

            for (Rectangle rectangle : rectangles){
                cells.addAll(rectangle.getCells());
            }
            body.setRectangles(null);
        }

        new CellInitiator(cells, sheetCode, isRead).init();

    }

    private void checkAttr() {
        if (EmptyChecker.isEmpty(body.getCells()) && EmptyChecker.isEmpty(body.getRectangles())){
            throw new XmlConfigException("verticalBody的cell和rectangle不能同时为空！");
        }
    }
}
