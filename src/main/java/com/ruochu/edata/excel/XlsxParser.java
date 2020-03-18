package com.ruochu.edata.excel;

import com.ruochu.edata.excel.model.ESheet;
import com.ruochu.edata.excel.model.EWorkbook;
import com.ruochu.edata.util.CoordinateUtil;
import com.ruochu.edata.util.EmptyChecker;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author RanPengCheng
 */
public class XlsxParser extends DefaultHandler implements ExcelParser {

    private SharedStringsTable sst;
    private String lastContents;
    private boolean nextIsString;

    private String col = "";

    private EWorkbook eWorkbook = new EWorkbook();
    private ESheet currentESheet;


    /**
     * 读取工作簿的入口方法
     *
     * @param inp
     */
    @Override
    public EWorkbook read(InputStream inp) throws IOException, OpenXML4JException, SAXException {
        OPCPackage pkg = OPCPackage.open(inp);
        XSSFReader reader = new XSSFReader(pkg);
        SharedStringsTable sst = reader.getSharedStringsTable();

        XMLReader parser = XMLReaderFactory.createXMLReader();
        this.sst = sst;
        parser.setContentHandler(this);

        XSSFReader.SheetIterator sheets = (XSSFReader.SheetIterator) reader.getSheetsData();
        while (sheets.hasNext()) {
            InputStream sheetstream = sheets.next();

            currentESheet = new ESheet(sheets.getSheetName());
            eWorkbook.addSheet(currentESheet);

            InputSource sheetSource = new InputSource(sheetstream);
            try {
                parser.parse(sheetSource);
            } finally {
                sheetstream.close();
            }
        }

        return eWorkbook;
    }


    @Override
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {
        // c => 单元格
        if ("c".equals(name)) {
            col = attributes.getValue("r");
            // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
            String cellType = attributes.getValue("t");
            if (cellType != null && "s".equals(cellType)) {
                nextIsString = true;
            } else {
                nextIsString = false;
            }
        }

        // 置空
        lastContents = "";
    }


    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        // 根据SST的索引值的到单元格的真正要存储的字符串
        // 这时characters()方法可能会被调用多次
        if (nextIsString) {
            int idx = Integer.parseInt(lastContents);
            lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
            nextIsString = false;
        }

        // v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
        // 将单元格内容加入eSheet中，在这之前先去掉字符串前后的空白符
        if ("v".equals(name)) {
            String value = lastContents.trim();
            if (EmptyChecker.notEmpty(value)){
                int[] xys = CoordinateUtil.toNumberPosition(col);
                currentESheet.addData(xys[1], xys[0], value);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // 得到单元格内容的值
        lastContents += new String(ch, start, length);
    }
}
