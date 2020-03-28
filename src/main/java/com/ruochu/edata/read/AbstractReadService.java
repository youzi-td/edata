package com.ruochu.edata.read;

import com.ruochu.edata.excel.model.EWorkbook;
import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.exception.UnknownFileTypeException;
import com.ruochu.edata.read.result.ReadResult;
import com.ruochu.edata.util.Context;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.util.ExcelParseUtil;
import com.ruochu.edata.util.XmlUtil;
import com.ruochu.edata.xml.ExcelConf;
import org.apache.poi.poifs.filesystem.FileMagic;

import java.io.*;

/**
 * 导入
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:11
 */
public abstract class AbstractReadService implements ReadService {

    protected Integer maxErrorCount = Integer.MAX_VALUE;
    protected EWorkbook templateWorkbook;
    protected EWorkbook userWorkbook;
    protected ExcelConf excelConf;
    protected Integer maxHorizontalSerialBlankRow = Integer.MAX_VALUE;


    public AbstractReadService(String templatePath, String xmlPath) {
        if (EmptyChecker.isEmpty(templatePath)) {
            throw new ERuntimeException("templatePath不能为空");
        }
        if (EmptyChecker.isEmpty(xmlPath)) {
            throw new ERuntimeException("xmlPath不能为空");
        }
        try {
            templateWorkbook = ExcelParseUtil.readTemplateExcel(templatePath);
        } catch (Exception e) {
            throw new ERuntimeException(e);
        }
        excelConf = XmlUtil.parseXmlConfig(xmlPath, true);
    }

    @Override
    public ReadResult read(String excelAbsPath) throws IOException, UnknownFileTypeException {
        if (EmptyChecker.isEmpty(excelAbsPath)) {
            throw new ERuntimeException("excelAbsPath不能为空！");
        }
        return read(new File(excelAbsPath));
    }

    @Override
    public ReadResult read(File file) throws IOException, UnknownFileTypeException {
        return read(new BufferedInputStream(new FileInputStream(file)));
    }

    @Override
    public ReadResult read(InputStream is) throws IOException, UnknownFileTypeException {
        if(!(FileMagic.valueOf(is) == FileMagic.OLE2 || FileMagic.valueOf(is) == FileMagic.OOXML)) {
            throw new UnknownFileTypeException("未知的文件类型");
        }
        try {
            this.userWorkbook = ExcelParseUtil.parse(is);
            return read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Context.remove();
        }
    }


    protected abstract ReadResult read();


    @Override
    public ReadService setMaxErrorCount(Integer maxErrorCount) {
        this.maxErrorCount = maxErrorCount;
        return this;
    }

    @Override
    public ReadService setMaxHorizontalSerialBlankRow(Integer maxHorizontalSerialBlankRow) {
        this.maxHorizontalSerialBlankRow = maxHorizontalSerialBlankRow;
        return this;
    }
}
