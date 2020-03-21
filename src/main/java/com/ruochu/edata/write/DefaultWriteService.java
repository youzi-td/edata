package com.ruochu.edata.write;

import com.ruochu.edata.enums.TableTypeEnum;
import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.SheetConf;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    public DefaultWriteService(String xmlPath) {
        super(xmlPath);
    }

    @Override
    public void write(String templateExcelPath, OutputStream out) throws IOException {
        Workbook wb = getTemplateWorkbook(templateExcelPath);
        // 1.先写header
        if (notEmpty(headerDataMap)) {
            writeHeader(wb);
        }

        if (notEmpty(bodyDataMap)) {
            // 2.写竖表
            for (SheetConf sheetConf : excelConf.getSheets()) {
                String sheetCode = sheetConf.getSheetCode();
                if (TableTypeEnum.VERTICAL.equals(sheetConf.getTableType()) && notEmpty(bodyDataMap.get(sheetCode))) {
                    Sheet sheet = getSheet(wb, sheetConf.getSheetName());
                    writeCells(sheet, bodyDataMap.get(sheetCode).get(0), sheetConf.getVerticalBody().getCells());
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
                    Sheet sheet = getSheet(wb, sheetConf.getSheetName());
                    writeBodyHorizontalBody(sheet, bodyDataMap.get(sheetCode), sheetConf.getHorizontalBody().getCells(), getCellStyle(wb), null);
                }
            }
        }

        try {
            wb.write(out);
        } finally {
            wb.close();
            if (out != null) {
                out.close();
            }
        }
    }

    private Sheet getSheet(Workbook wb, String sheetName) {
        Sheet sheet = wb.getSheet(sheetName);
        if (isEmpty(sheet)) {
            throw new ERuntimeException("在模板中未找到表：sheetName=%s", sheetName);
        }
        return sheet;
    }

    private void writeHeader(Workbook wb) {
        for (String sheetCode : headerDataMap.keySet()) {

            SheetConf sheetConf = excelConf.getSheetBySheetCode(sheetCode);
            Sheet sheet = getSheet(wb, sheetConf.getSheetName());
            if (notEmpty(sheetConf.getHeader())) {
                writeCells(sheet, headerDataMap.get(sheetCode), sheetConf.getHeader().getCells());
            }
        }
    }

    private void writeCells(Sheet sheet, Map<String, String> dataMap, List<CellConf> cells) {
        if (isEmpty(dataMap)) {
            return;
        }
        for (CellConf cellConf : cells) {
            int rowIndex = cellConf.getRowIndex() - 1;
            int colIndex = cellConf.getColIndex() - 1;

            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                row = sheet.createRow(rowIndex);
            }
            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                cell = row.createCell(colIndex);
            }
            cell.setCellValue(dataMap.get(cellConf.getField()));
        }
    }

    @Override
    public void writeWithNoneTemplate(OutputStream out) throws IOException {
        Workbook wb = getWorkbook();

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
            CellStyle cellStyle = getCellStyle(wb);

            Sheet sheet = wb.createSheet(sheetConf.getSheetName());
            // 最大的列宽缓存，用于宽度自适应
            Map<Integer, Integer> colWidthMap = new HashMap<>(cells.size());

            // 写标题行
            this.writeTitle4NoneTemplate(sheet, cells, cellStyle, colWidthMap);

            List<Map<String, String>> data = bodyDataMap.get(sheetCode);
            if (notEmpty(data)) {
                writeBodyHorizontalBody(sheet, data, cells, cellStyle, colWidthMap);
            }

            // 宽度自适应
            for (Integer i : colWidthMap.keySet()) {
                sheet.setColumnWidth(i, colWidthMap.get(i));
            }
        }

        try{
            wb.write(out);
        } finally {
            wb.close();
            if (out != null) {
                out.close();
            }
        }
    }

    private void writeBodyHorizontalBody(Sheet sheet, List<Map<String, String>> data, List<CellConf> cells, CellStyle cellStyle, Map<Integer, Integer> colWidthMap) {
        if (isEmpty(data)) {
            return;
        }
        int rowIndex = cells.get(0).getRowIndex() - 1;
        int sequence = 1;

        for (Map<String, String> map : data) {
            Row row = sheet.createRow(rowIndex);
            for (CellConf cellConf : cells) {
                int colIndex = cellConf.getColIndex() - 1;
                Cell cell = row.createCell(colIndex);
                if (cellConf.getAutoSequence()) {
                    cell.setCellValue(sequence);
                } else {
                    String value = map.get(cellConf.getField());
                    if (notEmpty(colWidthMap)) {
                        this.storeColumnMaxWidth(colIndex, value.getBytes().length, colWidthMap);
                    }
                    cell.setCellValue(value);
                }
                cell.setCellStyle(cellStyle);
            }
            rowIndex++;
            sequence++;
        }
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
            colWidthMap.put(colIndex, length);
        }
    }


    private CellStyle getCellStyle(Workbook wb) {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        return cellStyle;
    }
}
