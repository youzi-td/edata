package com.ruochu.edata.xml.init;

import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.SheetConf;
import com.ruochu.edata.enums.TableTypeEnum;
import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.util.EmptyChecker;

import java.util.*;

import static com.ruochu.edata.util.EmptyChecker.isEmpty;
import static com.ruochu.edata.util.EmptyChecker.notEmpty;

/**
 * sheet Initiator
 *
 * @author RanPengCheng
 * @date 2019/7/10 12:39
 */
public class SheetInitiator {

    private List<SheetConf> sheets;
    private boolean isRead;
    private SheetConf currentSheet;
    private Set<String> sheetNameSet;
    static final ThreadLocal<Map<String, Set<String>>> FIELDS_MAP = new ThreadLocal<>();
    static final ThreadLocal<Map<String, Map<String, String>>> XYS_MAP = new ThreadLocal<>();


    SheetInitiator(List<SheetConf> sheets, boolean isRead) {
        this.sheets = sheets;
        this.isRead = isRead;
        this.sheetNameSet = new HashSet<>(sheets.size());
        FIELDS_MAP.set(new HashMap<>(sheets.size()));
        XYS_MAP.set(new HashMap<>(sheets.size()));
    }

    void init(){
        for (SheetConf sheet : sheets){
            this.currentSheet = sheet;
            checkAttr();

            // header
            if (notEmpty(currentSheet.getHeader())){
                new HeaderInitiator(currentSheet.getHeader(), currentSheet.getSheetCode(), isRead).init();
            }
            // body
            if (notEmpty(currentSheet.getHorizontalBody())){
                // 横表
                new HorizontalBodyInitiator(currentSheet.getHorizontalBody(), currentSheet.getSheetCode(), isRead).init();
                currentSheet.setTableType(TableTypeEnum.HORIZONTAL);
            }else {
                // 竖表
                new VerticalBodyInitiator(currentSheet.getVerticalBody(), currentSheet.getSheetCode(), isRead).init();
                currentSheet.setTableType(TableTypeEnum.VERTICAL);
            }

            if (isRead){
                // filter
                new FilterInitiator(currentSheet.getValueFilter()).init();
                new FilterInitiator(currentSheet.getTemplateFilter()).init();

                // followTitle cell
                dealFollowTitleCell();
            }
            checkFieldAndXy();
        }


        FIELDS_MAP.remove();
        XYS_MAP.remove();
    }

    private void checkFieldAndXy() {
        if (notEmpty(currentSheet.getHeader())) {
            checkCellsFieldAndXy(currentSheet.getHeader().getCells());
        }
        if (notEmpty(currentSheet.getVerticalBody())) {
            checkCellsFieldAndXy(currentSheet.getVerticalBody().getCells());
        }
        if (notEmpty(currentSheet.getHorizontalBody())) {
            checkCellsFieldAndXy(currentSheet.getHorizontalBody().getCells());
        }
    }

    private void checkCellsFieldAndXy(List<CellConf> cells) {
        if (isEmpty(cells)) {
            return;
        }

        String sheetCode = currentSheet.getSheetCode();
        for (CellConf cell : cells) {
            String field = cell.getField();
            String title = cell.getTitle();
            if (FIELDS_MAP.get().get(sheetCode).contains(field)){
                throw new XmlConfigException("cell的field重复,field：" + field);
            }
            FIELDS_MAP.get().get(sheetCode).add(field);

            // 坐标校验
            Integer rowIndex = cell.getRowIndex();
            Integer colIndex = cell.getColIndex();
            String position = CoordinateUtil.toExcelPosition(colIndex, rowIndex);
            Map<String, String> xyMap = XYS_MAP.get().get(sheetCode);
            if (xyMap.containsKey(position)){
                throw new XmlConfigException("cell[" + title + "]与cell[" + xyMap.get(position) + "]的坐标重复！");
            }
            xyMap.put(position, title);
        }
    }

    private void dealFollowTitleCell() {
        Map<String, CellConf> followTitleCellMap = new HashMap<>();
        if (notEmpty(currentSheet.getHeader())){
            for (CellConf cell : currentSheet.getHeader().getCells()){
                if (cell.getFollowTitle()){
                    followTitleCellMap.put(CoordinateUtil.toExcelPosition(cell.getColIndex(), cell.getRowIndex()), cell);
                }
            }
        }
        if (notEmpty(currentSheet.getVerticalBody())){
            for (CellConf cell : currentSheet.getVerticalBody().getCells()){
                if (cell.getFollowTitle()){
                    followTitleCellMap.put(CoordinateUtil.toExcelPosition(cell.getColIndex(), cell.getRowIndex()), cell);
                }
            }
        }
        currentSheet.setFollowTitleCellMap(followTitleCellMap);
    }

    private void checkAttr() {
        String sheetName = currentSheet.getSheetName();
        if (EmptyChecker.isEmpty(sheetName)){
            throw new XmlConfigException("sheetName不能为空！");
        }else if (sheetNameSet.contains(sheetName)){
            throw new XmlConfigException("sheetName重复:".concat(sheetName));
        }
        sheetNameSet.add(sheetName);

        String sheetCode = currentSheet.getSheetCode();
        if (EmptyChecker.isEmpty(sheetCode)){
            throw new XmlConfigException("sheetCode不能为空！");
        }else if (FIELDS_MAP.get().containsKey(sheetCode)){
            throw new XmlConfigException("sheetCode重复:".concat(sheetCode));
        }
        FIELDS_MAP.get().put(sheetCode, new HashSet<>());
        XYS_MAP.get().put(sheetCode, new HashMap<>());

        boolean hExist = notEmpty(currentSheet.getHorizontalBody());
        boolean vExist = notEmpty(currentSheet.getVerticalBody());
        if (!hExist && !vExist){
            throw new XmlConfigException("sheet[".concat(sheetName).concat("]里应该有body元素！"));
        }else if (hExist && vExist){
            throw new XmlConfigException("sheet[".concat(sheetName).concat("]里的horizontalBody与verticalBody不能同时存在！"));
        }
    }

}
