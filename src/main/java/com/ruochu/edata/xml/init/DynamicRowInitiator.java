package com.ruochu.edata.xml.init;

import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.DynamicRow;

import java.util.Comparator;
import java.util.List;

import static com.ruochu.edata.util.EmptyChecker.isEmpty;
import static com.ruochu.edata.util.EmptyChecker.notEmpty;

/**
 * @author : RanPengCheng
 * @date : 2020/6/9 17:03
 */
public class DynamicRowInitiator {

    private String sheetCode;
    private List<DynamicRow> dynamicRows;

    public DynamicRowInitiator(String sheetCode, List<DynamicRow> dynamicRows) {
        this.sheetCode = sheetCode;
        this.dynamicRows = dynamicRows;
    }

    void init() {
        checkAttr();
        for (DynamicRow dynamicRow : dynamicRows) {
            initCellPosition(dynamicRow);
            new CellInitiator(dynamicRow.getCells(), sheetCode, false).init();
        }
        dynamicRows.sort((Comparator.comparingInt(o -> CoordinateUtil.toNumberPosition(o.getFirstValuePosition())[1])));
    }

    private void initCellPosition(DynamicRow dynamicRow) {
        String firstValuePosition = dynamicRow.getFirstValuePosition();
        int[] position = CoordinateUtil.toNumberPosition(firstValuePosition);
        int row = position[1];
        int col = position[0];
        for (CellConf cell : dynamicRow.getCells()){
            cell.setRowIndex(row);
            if (notEmpty(cell.getColIndex())) {
                col = cell.getColIndex();
            }
            cell.setColIndex(col);
            col = col + cell.getMergeCellFromRight() + 1;
        }
        dynamicRow.setStartRow(row);
    }

    private void checkAttr() {
        for (DynamicRow dynamicRow : dynamicRows) {
            String firstValuePosition = dynamicRow.getFirstValuePosition();
            if (isEmpty(firstValuePosition)) {
                throw new XmlConfigException("动态行dynamicRow必须指定firstValuePosition");
            }
            if (!CoordinateUtil.isExcelPosition(firstValuePosition)) {
                throw new XmlConfigException("动态行dynamicRow的firstValuePosition格式错误");
            }
            if (isEmpty(dynamicRow.getCells())) {
                throw new XmlConfigException("动态行dynamicRow必须包含元素cell");
            }

            String field = dynamicRow.getField();
            if (isEmpty(field)){
                throw new XmlConfigException("动态行dynamicRow的属性field不能为空");
            }else if (SheetInitiator.FIELDS_MAP.get().get(sheetCode).contains(field)){
                throw new XmlConfigException("动态行dynamicRow的属性field重复,field：%s", field);
            }
            SheetInitiator.FIELDS_MAP.get().get(sheetCode).add(field);
        }
    }
}
