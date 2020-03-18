package com.ruochu.edata.excel;

import com.ruochu.edata.excel.model.EWorkbook;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RanPengCheng
 */
public interface ExcelParser {
    EWorkbook read(InputStream is) throws IOException, OpenXML4JException, SAXException;
}
