package com.ruochu.edata.write;

import com.ruochu.edata.enums.ExcelType;
import com.ruochu.edata.enums.TableTypeEnum;
import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.util.BeanToMapUtil;
import com.ruochu.edata.util.ReflectUtil;
import com.ruochu.edata.util.XmlUtil;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.DynamicRow;
import com.ruochu.edata.xml.ExcelConf;
import com.ruochu.edata.xml.SheetConf;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ruochu.edata.util.EmptyChecker.isEmpty;
import static com.ruochu.edata.util.EmptyChecker.notEmpty;

/**
 * @author : RanPengCheng
 * @date : 2020/3/14 18:58
 */
public abstract class AbstractWriteService implements WriteService {

    protected ExcelConf excelConf;
    protected Map<String, List<Map<String, Object>>> bodyDataMap;
    protected Map<String, Map<String, Object>> headerDataMap;
    protected ExcelType excelType4NoneTemplate;
    protected boolean offXlsxHorizontalCacheWrite;
    protected boolean wrapText;

    protected Map<String, Boolean> bgAlterMap;
    protected Map<String, String> bgAlterFieldMap;
    protected Map<String, List<IndexedColors>> colorMap;

    private CellStyle defaultStyle;
    private Map<String, CellStyle> formatStyleMap;
    private Map<Short, CellStyle>  bgStyleMap;
    private Map<String, Map<Short, CellStyle>> formatBgStyleMap;

    protected Workbook wb;

    /**
     * 限制只能写一次
     */
    private AtomicBoolean used = new AtomicBoolean(false);


    public AbstractWriteService(String xmlPath) {
        this.excelConf = XmlUtil.parseXmlConfig(xmlPath, false);
        this.bodyDataMap = new HashMap<>();
        this.headerDataMap = new HashMap<>();
        this.excelType4NoneTemplate = ExcelType.XLSX;

        this.bgAlterMap = new HashMap<>(excelConf.getSheets().size());
        this.bgAlterFieldMap = new HashMap<>(excelConf.getSheets().size());
        for (SheetConf sheet : excelConf.getSheets()) {
            bgAlterMap.put(sheet.getSheetCode(), false);
        }

        this.offXlsxHorizontalCacheWrite = false;
        this.wrapText = true;

        this.formatStyleMap = new HashMap<>();
        this.bgStyleMap = new HashMap<>();
        this.formatBgStyleMap = new HashMap<>();
    }


    @Override
    public WriteService addBodyData(String sheetCode, List<?> datas) {
        checkUsed();
        if (isEmpty(excelConf.getSheetBySheetCode(sheetCode))) {
            throw new ERuntimeException("xml配置中未找到sheetCode：%s", sheetCode);
        }
        if (notEmpty(datas)) {
            List<Map<String, Object>> list = BeanToMapUtil.transformToStringMap(datas, getCells(sheetCode, Type.BODY));
            List<Map<String, Object>> bodyData = bodyDataMap.computeIfAbsent(sheetCode, k -> new ArrayList<>(list.size()));
            bodyData.addAll(list);
        }

        return this;
    }

    @Override
    public WriteService addBodyData(String sheetCode, Object data) {
        checkUsed();
        if (data instanceof List) {
            return addBodyData(sheetCode, (List<?>) data);
        }
        SheetConf sheetConf = excelConf.getSheetBySheetCode(sheetCode);
        if (isEmpty(sheetConf)) {
            throw new ERuntimeException("xml配置中未找到sheetCode：%s", sheetCode);
        }
        if (notEmpty(data)) {
            Map<String, Object> row = BeanToMapUtil.transformToStringMap(data, getCells(sheetCode, Type.BODY));
            if (TableTypeEnum.VERTICAL.equals(sheetConf.getTableType())) {
                List<DynamicRow> dynamicRows = sheetConf.getVerticalBody().getDynamicRows();
                if (notEmpty(dynamicRows)) {
                    addDynamicData(row, dynamicRows, data);
                }
            }
            List<Map<String, Object>> bodyData = bodyDataMap.computeIfAbsent(sheetCode, k -> new LinkedList<>());
            bodyData.add(row);
        }
        return this;
    }

    private void addDynamicData(Map<String, Object> row, List<DynamicRow> dynamicRows, Object data) {
        for (DynamicRow dynamicRow : dynamicRows) {
            String dynamicRowField = dynamicRow.getField();
            Object value = ReflectUtil.getValue(data, dynamicRowField);
            if (notEmpty(value) && !(value instanceof Collection)) {
                throw new ERuntimeException("动态行的数据应为集合类型！");
            }
            row.put(dynamicRowField, BeanToMapUtil.transformToStringMap((Collection<?>) value, dynamicRow.getCells()));
        }
    }

    @Override
    public WriteService addHeaderData(String sheetCode, Object headerData) {
        checkUsed();
        if (isEmpty(excelConf.getSheetBySheetCode(sheetCode))) {
            throw new ERuntimeException("xml配置中未找到sheetCode：%s", sheetCode);
        }
        List<CellConf> cells = getCells(sheetCode, Type.HEADER);
        if (notEmpty(headerData) && notEmpty(cells)) {
            headerDataMap.put(sheetCode, BeanToMapUtil.transformToStringMap(headerData, cells));
        }
        return this;
    }

    @Override
    public WriteService excelType4NoneTemplate(ExcelType excelType) {
        checkUsed();
        if (notEmpty(excelType)) {
            this.excelType4NoneTemplate = excelType;
        }
        return this;
    }

    @Override
    public WriteService offXlsxHorizontalCacheWrite() {
        checkUsed();
        this.offXlsxHorizontalCacheWrite = Boolean.TRUE;
        return this;
    }


    @Override
    public WriteService rowsBackgroundAlternate(String sheetCode, IndexedColors... colors) {
        checkUsed();
        if (bgAlterMap.containsKey(sheetCode)
                && notEmpty(colors)
                && TableTypeEnum.HORIZONTAL.equals(excelConf.getSheetBySheetCode(sheetCode).getTableType())) {

            for (IndexedColors c : colors) {
                bgAlterMap.put(sheetCode, true);

                if (colorMap == null) {
                    colorMap = new HashMap<>();
                }
                List<IndexedColors> indexedColors = colorMap.computeIfAbsent(sheetCode, k -> new ArrayList<>());
                indexedColors.add(c);
            }
        }
        return this;
    }

    @Override
    public WriteService rowsBackgroundAlternate4Filed(String sheetCode, String field, IndexedColors... rgbColors) {
        checkUsed();
        bgAlterFieldMap.put(sheetCode, field);
        rowsBackgroundAlternate(sheetCode, rgbColors);
        return this;
    }

    @Override
    public WriteService closeWrapText() {
        checkUsed();
        this.wrapText = false;
        return this;
    }

    protected List<CellConf> getCells(String sheetCode, Type type) {
        SheetConf sheet = excelConf.getSheetBySheetCode(sheetCode);
        if (sheet == null) {
            throw new ERuntimeException("sheetCode或导出xml配置有误，请检查!");
        }
        if (Type.BODY.equals(type)) {
            if (TableTypeEnum.HORIZONTAL.equals(sheet.getTableType())) {
                return sheet.getHorizontalBody().getCells();
            }
            return sheet.getVerticalBody().getCells();
        }

        if (notEmpty(sheet.getHeader())) {
            return sheet.getHeader().getCells();
        }
        return null;
    }

    protected Workbook getWorkbook() {
        if (ExcelType.XLSX.equals(excelType4NoneTemplate)) {
            return new XSSFWorkbook();
        } else {
            return new HSSFWorkbook();
        }
    }

    protected Workbook getTemplateWorkbook(String templateExcel) throws IOException {
        InputStream stream = AbstractWriteService.class.getClassLoader().getResourceAsStream(templateExcel);
        if (null == stream) {
            throw new ERuntimeException("未找到classpath下的文件：%s", templateExcel);
        }

        return WorkbookFactory.create(stream);
    }

    protected void checkUsed() {
        if (used.get()) {
            throw new ERuntimeException("该WriteService实例已经执行过写操作，不能重复使用，请重新获取WriteService！");
        }
    }

    protected void checkUsed(OutputStream out) throws IOException {
        if (!used.compareAndSet(false, true)) {
            if (out != null) {
                out.close();
            }
            throw new ERuntimeException("该WriteService实例已经执行过写操作，不能重复使用，请重新获取WriteService！");
        }
    }

    protected void writeAndClose(OutputStream out) throws IOException {
        try{
            wb.write(out);
        } finally {
            wb.close();
            if (out != null) {
                out.close();
            }
        }
    }

    protected Sheet getSheet(String sheetName) {
        Sheet sheet = wb.getSheet(sheetName);
        if (isEmpty(sheet)) {
            throw new ERuntimeException("在模板中未找到表：sheetName=%s", sheetName);
        }
        return sheet;
    }

    protected CellStyle getStyle(String format, Short bgIndex) {
        if (isEmpty(format) && isEmpty(bgIndex)) {
            return getDefaultStyle();
        }
        if (notEmpty(format) && notEmpty(bgIndex)) {
            Map<Short, CellStyle> shortCellStyleMap = formatBgStyleMap.computeIfAbsent(format, k -> new HashMap<>());
            CellStyle cellStyle = shortCellStyleMap.get(bgIndex);
            if (isEmpty(cellStyle)) {
                cellStyle = newFormatCellStyle(format);
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                cellStyle.setFillForegroundColor(bgIndex);
                shortCellStyleMap.put(bgIndex, cellStyle);
            }
            return cellStyle;
        }
        if (notEmpty(format)) {
            return getStyle(format);
        }

        return getStyle(bgIndex);
    }

    protected CellStyle getStyle(Short bgIndex) {
        if (isEmpty(bgIndex)) {
            return getDefaultStyle();
        }
        CellStyle cellStyle = bgStyleMap.get(bgIndex);
        if (null == cellStyle) {
            cellStyle = newDefaultCellStyle();
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor(bgIndex);
            bgStyleMap.put(bgIndex, cellStyle);
        }
        return cellStyle;
    }

    protected CellStyle getStyle(String format) {
        if (isEmpty(format)) {
            return getDefaultStyle();
        }
        CellStyle cellStyle = formatStyleMap.get(format);
        if (null == cellStyle) {
            cellStyle = newFormatCellStyle(format);
            formatStyleMap.put(format, cellStyle);
        }
        return cellStyle;
    }

    private CellStyle newFormatCellStyle(String dataFormat) {
        CellStyle cellStyle = newDefaultCellStyle();
        cellStyle.setDataFormat(wb.createDataFormat().getFormat(dataFormat));
        return cellStyle;
    }

    protected CellStyle newDefaultCellStyle() {
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);

        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        cellStyle.setWrapText(wrapText);

        return cellStyle;
    }

    protected CellStyle getDefaultStyle() {
        if (defaultStyle == null) {
            defaultStyle = newDefaultCellStyle();
        }
        return defaultStyle;
    }

    private enum Type {
        HEADER, BODY
    }
}
