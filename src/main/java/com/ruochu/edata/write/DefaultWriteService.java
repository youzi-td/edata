package com.ruochu.edata.write;

import com.ruochu.edata.Image;
import com.ruochu.edata.enums.TableTypeEnum;
import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.xml.BodyConf;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.DynamicRow;
import com.ruochu.edata.xml.SheetConf;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ruochu.edata.util.EmptyChecker.isEmpty;
import static com.ruochu.edata.util.EmptyChecker.notEmpty;

/**
 * @author : RanPengCheng
 * @date : 2020/3/16 21:19
 */
public class DefaultWriteService extends AbstractWriteService {
    private static final int MAX_AUTO_WIDTH = 20000;

    private Object bgKeyPreValue = null;
    private int bgAlterCount = 0;

    /** 画图的顶级管理器，一个sheet只能获取一个 */
    private Map<Sheet, Drawing<?>> sheetDrawingMap = new HashMap<>();

    public DefaultWriteService(String xmlPath) {
        super(xmlPath);
    }

    @Override
    public void write(String templateExcelPath, OutputStream out) throws IOException {
        // 防止重复使用
        checkUsed(out);

        wb = getTemplateWorkbook(templateExcelPath);

        // 1.先写header
        if (notEmpty(headerDataMap)) {
            writeHeader();
        }

        if (isEmpty(bodyDataMap)) {
            writeAndClose(out);
            return;
        }

        // 2.写竖表
        for (SheetConf sheetConf : excelConf.getSheets()) {
            String sheetCode = sheetConf.getSheetCode();
            if (TableTypeEnum.VERTICAL.equals(sheetConf.getTableType()) && notEmpty(bodyDataMap.get(sheetCode))) {
                Sheet sheet = getSheet(sheetConf.getSheetName());
                BodyConf verticalBody = sheetConf.getVerticalBody();
                Map<String, Object> data = bodyDataMap.get(sheetCode).get(0);
                writeVerticalCells(sheet, data, verticalBody.getCells(), verticalBody.getDynamicRows());
            }
        }

        // 3.写横表
        if (!offXlsxHorizontalCacheWrite && wb instanceof XSSFWorkbook) {
            // 转换为07版高速写
            wb = new SXSSFWorkbook((XSSFWorkbook) wb);
        }
        for (SheetConf sheetConf : excelConf.getSheets()) {
            String sheetCode = sheetConf.getSheetCode();
            if (TableTypeEnum.HORIZONTAL.equals(sheetConf.getTableType()) && notEmpty(bodyDataMap.get(sheetCode))) {
                Sheet sheet = getSheet(sheetConf.getSheetName());
                writeBodyHorizontalBody(sheet, sheetConf.getHorizontalBody(),null, sheetCode);
            }
        }
        // 释放资源
        writeAndClose(out);
    }


    @Override
    public void writeWithNoneTemplate(OutputStream out) throws IOException {
        // 防止重复使用
        checkUsed(out);

        this.wb = getWorkbook();

        if (wb instanceof XSSFWorkbook) {
            wb = new SXSSFWorkbook((XSSFWorkbook) wb);
        }

        for (String sheetCode : bodyDataMap.keySet()) {
            SheetConf sheetConf = excelConf.getSheetBySheetCode(sheetCode);
            if (isEmpty(sheetConf)) {
                throw new ERuntimeException("未找到sheetCode[%s]对应的xml配置", sheetCode);
            }
            if (!TableTypeEnum.HORIZONTAL.equals(sheetConf.getTableType())) {
                throw new ERuntimeException("sheetCode[%s]对应的xml配置不为横表", sheetCode);
            }

            List<CellConf> cells = sheetConf.getHorizontalBody().getCells();
            CellStyle cellStyle = getDefaultStyle();

            Sheet sheet = wb.createSheet(sheetConf.getSheetName());
            // 最大的列宽缓存，用于宽度自适应
            Map<Integer, Integer> colWidthMap = new HashMap<>(cells.size());

            // 写标题行
            this.writeTitle4NoneTemplate(sheet, cells, cellStyle, colWidthMap);

            List<Map<String, Object>> data = bodyDataMap.get(sheetCode);
            if (notEmpty(data)) {
                writeBodyHorizontalBody(sheet, sheetConf.getHorizontalBody(), colWidthMap, sheetCode);
            }

            // 宽度自适应
            for (Integer i : colWidthMap.keySet()) {
                sheet.setColumnWidth(i, colWidthMap.get(i));
            }
        }

        // 释放资源
        writeAndClose(out);
    }

    private int writeDynamicRow(Sheet sheet, DynamicRow dynamicRow, Map<String, Object> data, int rowAdd) throws IOException {
        CellStyle cellStyle = newDefaultCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        Object obj = data.get(dynamicRow.getField());
        if (isEmpty(obj)) {
            return 0;
        }
        List<Map<String, Object>> list = (List) obj;
        List<CellConf> cells = dynamicRow.getCells();
        int startRow = cells.get(0).getRowIndex() - 1;
        int sequence = 1;
        for (Map<String, Object> map : list) {
            // 插入空行
            sheet.shiftRows(startRow + rowAdd, sheet.getLastRowNum(), 1);
            // 写数据
            writeDynamicRowCells(sheet, startRow + rowAdd, dynamicRow, map, sequence, cellStyle);
            rowAdd++;
            sequence++;
        }

        return list.size();
    }

    private void writeDynamicRowCells(Sheet sheet, int rowIndex, DynamicRow dynamicRow, Map<String, Object> map, int sequence, CellStyle cellStyle) throws IOException {
        Row row = sheet.createRow(rowIndex);
        Integer rowHeight = dynamicRow.getRowHeight();
        if (notEmpty(rowHeight) && rowHeight > 10) {
            row.setHeight((short) rowHeight.intValue());
        }

        List<CellConf> cells = dynamicRow.getCells();
        for (CellConf cellConf : cells) {
            Integer mergeCellFromRight = cellConf.getMergeCellFromRight();
            int colIndex = cellConf.getColIndex() - 1;
            for (int i = 0; i <= mergeCellFromRight; i ++) {
                row.createCell(colIndex + i).setCellStyle(cellStyle);
            }
            if (mergeCellFromRight > 0) {
                sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, colIndex, colIndex + mergeCellFromRight));
            }

            Cell cell = row.getCell(colIndex);
            if (cellConf.getAutoSequence()) {
                cell.setCellValue(sequence);
                continue;
            }
            Object value = map.get(cellConf.getField());
            this.writeCellValue(sheet, cell, value);

            if (value instanceof Number) {
                cell.setCellStyle(getStyle(cellConf.getWriteFormat()));
            }
        }
    }


    private void writeHeader() throws IOException {
        for (String sheetCode : headerDataMap.keySet()) {

            SheetConf sheetConf = excelConf.getSheetBySheetCode(sheetCode);
            Sheet sheet = getSheet(sheetConf.getSheetName());
            if (notEmpty(sheetConf.getHeader())) {
                writeVerticalCells(sheet, headerDataMap.get(sheetCode), sheetConf.getHeader().getCells(), null);
            }
        }
    }

    private void writeVerticalCells(Sheet sheet, Map<String, Object> dataMap, List<CellConf> cells, List<DynamicRow> dynamicRows) throws IOException {
        if (isEmpty(dataMap)) {
            return;
        }

        int rowAdd = 0;
        int dynamicIndex = 0;

        for (CellConf cellConf : cells) {
            int rowIndex = cellConf.getRowIndex() - 1 + rowAdd;
            int colIndex = cellConf.getColIndex() - 1;

            if (notEmpty(dynamicRows)) {
                int size = dynamicRows.size();
                if (size > dynamicIndex && dynamicRows.get(dynamicIndex).getStartRow() + rowAdd - 1 <= rowIndex) {
                    // 先写动态行
                    int add = writeDynamicRow(sheet, dynamicRows.get(dynamicIndex), dataMap, rowAdd);
                    rowAdd += add;
                    rowIndex += add;
                    dynamicIndex++;
                }
            }

            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                cell = row.createCell(colIndex);
            }
            Object value = dataMap.get(cellConf.getField());
            this.writeCellValue(sheet, cell, value);

            if (notEmpty(cellConf.getWriteFormat())) {
                CellStyle cellStyle = cell.getCellStyle();
                cellStyle.setDataFormat(wb.createDataFormat().getFormat(cellConf.getWriteFormat()));
                cell.setCellStyle(cellStyle);
            }
        }
        if (notEmpty(dynamicRows) && dynamicRows.size() > dynamicIndex) {
            for (; dynamicIndex < dynamicRows.size(); dynamicIndex++) {
                rowAdd += writeDynamicRow(sheet, dynamicRows.get(dynamicIndex), dataMap, rowAdd);
            }
        }
    }

    private void writeBodyHorizontalBody(Sheet sheet, BodyConf horizontalBody, Map<Integer, Integer> colWidthMap, String sheetCode) throws IOException {
        List<Map<String, Object>> data = bodyDataMap.get(sheetCode);
        if (isEmpty(data)) {
            return;
        }
        List<CellConf> cells = horizontalBody.getCells();
        int rowIndex = cells.get(0).getRowIndex() - 1;
        int sequence = 1;
        this.bgKeyPreValue = null;
        this.bgAlterCount = 0;

        for (Map<String, Object> map : data) {
            Row row = sheet.createRow(rowIndex);
            Integer rowHeight = horizontalBody.getRowHeight();
            if (notEmpty(rowHeight) && rowHeight > 10) {
                row.setHeight((short) rowHeight.intValue());
            }

            Short bgIndex = null;
            if (bgAlterMap.get(sheetCode)) {
                bgIndex = getBgIndex(sheetCode, map);
            }
            CellStyle cellStyle = getStyle(bgIndex);
            for (CellConf cellConf : cells) {
                int colIndex = cellConf.getColIndex() - 1;
                Cell cell = row.createCell(colIndex);
                if (cellConf.getAutoSequence()) {
                    cell.setCellValue(sequence);
                } else {
                    Object value = map.get(cellConf.getField());
                    if (notEmpty(colWidthMap)) {
                        this.storeColumnMaxWidth(colIndex, value.toString().getBytes().length, colWidthMap);
                    }

                    this.writeCellValue(sheet, cell, value);
                    if (value instanceof Number) {
                        cellStyle = getStyle(cellConf.getWriteFormat(), bgIndex);
                    } else {
                        cellStyle = getStyle(bgIndex);
                    }
                }
                cell.setCellStyle(cellStyle);
            }
            rowIndex++;
            sequence++;
        }
    }

    private void writeCellValue(Sheet sheet, Cell cell, Object value) throws IOException {
        if (isEmpty(value)) {
            cell.setCellValue("");
            return;
        }

        if (value instanceof Number) {
            // 数字
            cell.setCellValue(((Number)value).doubleValue());
        } else if (value instanceof Image) {
            // 图片
            Image image = (Image) value;
            int firstRow = cell.getRowIndex();
            int lastRow = cell.getRowIndex() + 1;
            int firstCol = cell.getColumnIndex();
            int lastCol = cell.getColumnIndex() + 1;

            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            for (CellRangeAddress region : mergedRegions) {
                if (region.isInRange(cell)) {
                    firstRow = region.getFirstRow();
                    firstCol = region.getFirstColumn();
                    lastRow = region.getLastRow() + 1;
                    lastCol = region.getLastColumn() + 1;
                    break;
                }
            }
            try(ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream()) {
                ImageIO.write(image.getBufferedImage(), image.getType().name(), byteArrayOut);
                //画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
                Drawing<?> patriarch = sheetDrawingMap.computeIfAbsent(sheet, k -> sheet.createDrawingPatriarch());
                //anchor主要用于设置图片的属性
                ClientAnchor anchor = patriarch.createAnchor(50, 14, 0, 0,(short) firstCol, firstRow, (short) lastCol, lastRow);
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
                //插入图片
                patriarch.createPicture(anchor, wb.addPicture(byteArrayOut.toByteArray(), image.getType().getCode()));
            }

        } else {
            // 其他
            cell.setCellValue(value.toString());
        }
    }

    private Short getBgIndex(String sheetCode, Map<String, Object> rowData) {
        List<IndexedColors> indexedColors = colorMap.get(sheetCode);
        if (isEmpty(indexedColors)) {
            return null;
        }
        int size = indexedColors.size();
        String key = bgAlterFieldMap.get(sheetCode);
        if (isEmpty(key) || (rowData.containsKey(key) && !rowData.get(key).equals(bgKeyPreValue))) {
            bgAlterCount++;
            bgKeyPreValue = rowData.get(key);
            int i = bgAlterCount % size;
            return indexedColors.get(i).getIndex();
        }
        return null;
    }

    private void writeTitle4NoneTemplate(Sheet sheet, List<CellConf> cells, CellStyle cellStyle, Map<Integer, Integer> colWidthMap) {
        Row row = sheet.createRow(0);

        for (CellConf cellConf : cells) {
            int colIndex = cellConf.getColIndex() - 1;

            Cell cell = row.createCell(colIndex);
            cell.setCellValue(cellConf.getTitle());
            cell.setCellStyle(cellStyle);

            cellConf.setRowIndex(2);

            storeColumnMaxWidth(colIndex, cellConf.getTitle().getBytes().length, colWidthMap);
        }
    }

    private void storeColumnMaxWidth(int colIndex, int length, Map<Integer, Integer> colWidthMap) {
        length = length * 256 + 512;
        Integer maxWidth = colWidthMap.get(colIndex);
        maxWidth = maxWidth == null ? 0 : maxWidth;
        if (length > maxWidth) {
            colWidthMap.put(colIndex, Math.min(length, MAX_AUTO_WIDTH));
        }
    }

}
