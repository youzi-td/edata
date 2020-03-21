package com.ruochu.edata.write;

import com.ruochu.edata.enums.ExcelType;
import com.ruochu.edata.enums.TableTypeEnum;
import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.util.BeanToMapUtil;
import com.ruochu.edata.util.ExcelParseUtil;
import com.ruochu.edata.util.XmlUtil;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.ExcelConf;
import com.ruochu.edata.xml.SheetConf;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.ruochu.edata.util.EmptyChecker.isEmpty;
import static com.ruochu.edata.util.EmptyChecker.notEmpty;

/**
 * @author : RanPengCheng
 * @date : 2020/3/14 18:58
 */
public abstract class AbstractWriteService implements WriteService {

    protected ExcelConf excelConf;
    protected Map<String, List<Map<String, String>>> bodyDataMap;
    protected Map<String, Map<String, String>> headerDataMap;
    protected ExcelType excelType;
    protected boolean offXlsxHorizontalCacheWrite;


    public AbstractWriteService(String xmlPath) {
        this.excelConf = XmlUtil.parseXmlConfig(xmlPath, false);
        this.bodyDataMap = new HashMap<>();
        this.headerDataMap = new HashMap<>();
        this.excelType = ExcelType.XLSX;
        this.offXlsxHorizontalCacheWrite = Boolean.FALSE;
    }


    @Override
    public WriteService addBodyData(String sheetCode, List<?> datas) {
        if (isEmpty(excelConf.getSheetBySheetCode(sheetCode))) {
            throw new ERuntimeException("xml配置中未找到sheetCode：%s", sheetCode);
        }
        if (notEmpty(datas)) {
            List<Map<String, String>> list = BeanToMapUtil.transformToStringMap(datas, getCells(sheetCode, Type.BODY));
            List<Map<String, String>> bodyData = bodyDataMap.get(sheetCode);
            if (bodyData == null) {
                bodyData = new ArrayList<>(list.size());
                bodyDataMap.put(sheetCode, bodyData);
            }
            bodyData.addAll(list);
        }

        return this;
    }

    @Override
    public WriteService addBodyData(String sheetCode, Object data) {
        if (isEmpty(excelConf.getSheetBySheetCode(sheetCode))) {
            throw new ERuntimeException("xml配置中未找到sheetCode：%s", sheetCode);
        }
        if (notEmpty(data)) {
            Map<String, String> row = BeanToMapUtil.transformToStringMap(data, getCells(sheetCode, Type.BODY));
            List<Map<String, String>> bodyData = bodyDataMap.get(sheetCode);
            if (bodyData == null) {
                bodyData = new LinkedList<>();
                bodyDataMap.put(sheetCode, bodyData);
            }
            bodyData.add(row);
        }
        return this;
    }

    @Override
    public WriteService addHeaderData(String sheetCode, Object headerData) {
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
    public WriteService excelType(ExcelType excelType) {
        if (notEmpty(excelType)) {
            this.excelType = excelType;
        }
        return this;
    }

    @Override
    public WriteService offXlsxHorizontalCacheWrite() {
        this.offXlsxHorizontalCacheWrite = Boolean.TRUE;
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
        if (ExcelType.XLSX.equals(excelType)) {
            return new XSSFWorkbook();
        } else {
            return new HSSFWorkbook();
        }
    }

    protected Workbook getTemplateWorkbook(String templateExcel) throws IOException {
        InputStream stream = ExcelParseUtil.class.getClassLoader().getResourceAsStream(templateExcel);
        if (null == stream) {
            throw new ERuntimeException("未找到classpath下的文件：%s", templateExcel);
        }

        if (ExcelType.XLSX.equals(excelType)) {
            return new XSSFWorkbook(stream);
        } else {
            return new HSSFWorkbook(stream);
        }
    }

    private enum Type {
        HEADER, BODY
    }
}
