package com.ruochu.edata.util;

import com.ruochu.edata.excel.XlsParser;
import com.ruochu.edata.excel.XlsxParser;
import com.ruochu.edata.excel.model.EWorkbook;
import com.ruochu.edata.exception.UnknownFileTypeException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author RanPengCheng
 */
public class ExcelParseUtil {

    private ExcelParseUtil(){}


    private static final Map<String, EWorkbook> TEMPLATE_EXCEL_CACHE = new HashMap<>();

    private static final int LOCK_POOL_SIZE = 16;
    private static final ReentrantReadWriteLock[] LOCK_POOL = new ReentrantReadWriteLock[LOCK_POOL_SIZE];

    static {
        for (int i = 0; i < LOCK_POOL_SIZE; i++) {
            LOCK_POOL[i] = new ReentrantReadWriteLock();
        }
    }


    /**
     *
     * @param excelAbsPath excel绝对路径
     * @return
     */
    public static EWorkbook parse(String excelAbsPath) throws IOException, OpenXML4JException, SAXException, UnknownFileTypeException {
        return parse(new BufferedInputStream(new FileInputStream(excelAbsPath)));
    }

    public static EWorkbook parse(File file) throws IOException, OpenXML4JException, SAXException, UnknownFileTypeException {
        return parse(new BufferedInputStream(new FileInputStream(file)));
    }

    public static EWorkbook parse(InputStream inp) throws IOException, OpenXML4JException, SAXException, UnknownFileTypeException {
        if (!inp.markSupported()) {
            inp = new BufferedInputStream(inp);
        }
        if(FileMagic.valueOf(inp) == FileMagic.OLE2) {
            return new XlsParser().read(inp);
        }

        if(FileMagic.valueOf(inp) == FileMagic.OOXML) {
            return new XlsxParser().read(inp);
        }

        throw new UnknownFileTypeException("未知的文件类型！");
    }


    /**
     * 读取模板excel数据
     * @param templateExcelPath 模板excel的路径（classpath下的相对路径）
     */
    public static EWorkbook readTemplateExcel(String templateExcelPath) throws OpenXML4JException, SAXException, IOException, UnknownFileTypeException {
        ReentrantReadWriteLock readWriteLock = LOCK_POOL[Math.abs(templateExcelPath.hashCode()) % LOCK_POOL_SIZE];
        readWriteLock.readLock().lock();
        try {
            if (TEMPLATE_EXCEL_CACHE.containsKey(templateExcelPath)){
                return TEMPLATE_EXCEL_CACHE.get(templateExcelPath);
            }
        }finally {
            readWriteLock.readLock().unlock();
        }

        readWriteLock.writeLock().lock();
        try {
            if (TEMPLATE_EXCEL_CACHE.containsKey(templateExcelPath)){
                return TEMPLATE_EXCEL_CACHE.get(templateExcelPath);
            }

            EWorkbook workbook = parse(ExcelParseUtil.class.getClassLoader().getResourceAsStream(templateExcelPath));
            TEMPLATE_EXCEL_CACHE.put(templateExcelPath, workbook);
            return workbook;

        }finally {
            readWriteLock.writeLock().unlock();
        }
    }

}
