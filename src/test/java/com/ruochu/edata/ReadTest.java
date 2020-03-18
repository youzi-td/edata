package com.ruochu.edata;

import com.ruochu.edata.excel.model.EWorkbook;
import com.ruochu.edata.exception.UnknownFileTypeException;
import com.ruochu.edata.read.ReadService;
import com.ruochu.edata.read.result.DataMap;
import com.ruochu.edata.read.result.ErrorData;
import com.ruochu.edata.read.result.ReadResult;
import com.ruochu.edata.read.result.SheetData;
import com.ruochu.edata.read.validator.TemplateValidator;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.util.ExcelParseUtil;
import com.ruochu.edata.util.XmlUtil;
import com.ruochu.edata.xml.ExcelConf;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

/**
 * {描述}
 *
 * @author RanPengCheng
 * @date 2019/7/21 15:10
 */
public class ReadTest {

    @Test
    public void testTemplateValidate() throws IOException, SAXException, OpenXML4JException, UnknownFileTypeException {

        String templateExcelUrl = "template/testTemplate.xlsx";
        String path = getClass().getClassLoader().getResource("file/test.xlsx").getPath();

        ExcelConf excelConf = XmlUtil.parseXmlConfig("xml/read.xml", true);
        EWorkbook workbook = ExcelParseUtil.readTemplateExcel(templateExcelUrl);
        EWorkbook userWorkbook = ExcelParseUtil.parse(path);

        List<ErrorData> errorDataList = new TemplateValidator(excelConf, workbook, userWorkbook).validate();
        for (ErrorData errorData : errorDataList){
            System.out.println(errorData);
        }
    }

    @Test
    public void testInput() throws IOException, UnknownFileTypeException {
        // excel模板
        String templateExcelUrl = "template/testTemplate.xlsx";
        // xml配置
        String xmlPath = "xml/read.xml";


        // 模拟用户导入excel
        String userExcelPath = getClass().getClassLoader().getResource("file/test.xlsx").getPath();


        // 获取导入服务
        ReadService readService = EdataFactory.getReadService(templateExcelUrl, xmlPath);
        // 执行导入，得到导入结果
        ReadResult readResult = readService.read(userExcelPath);

        printResult(readResult);
    }


    private void printResult(ReadResult readResult) {
        boolean success = readResult.isSuccess();
        boolean templateError = readResult.isTemplateError();
        List<ErrorData> errorDataList = readResult.getErrorDatas();
        List<SheetData> sheetDatas = readResult.getSheetDatas();

        System.out.println();
        System.out.println();

        System.out.println(String.format("success: %s,   templateError: %s", success, templateError));
        System.out.println("----------------------------------------------------------------------");
        String s = "%s   %s   %s   %s,%s   %s   %s   %s";
        System.out.println("校验未通过的数据：");
        System.out.println();
        if (EmptyChecker.notEmpty(errorDataList)) {
            for (ErrorData errorData : errorDataList) {
                System.out.println(String.format(
                        s,
                        errorData.getTitle(),
                        errorData.getValue(),
                        errorData.getPosition(),
                        errorData.getColIndex(),
                        errorData.getRowIndex(),
                        errorData.getErrorMsg(),
                        errorData.getSheetName(),
                        errorData.getErrorType()
                        ));
            }
        }
        System.out.println("----------------------------------------------------------------------");
        System.out.println("导入的数据：");
        System.out.println();
        String blank = "          ";
        if (EmptyChecker.notEmpty(sheetDatas)) {
            for (SheetData sheetData : sheetDatas) {
                System.out.println("·····························································");
                System.out.println("sheetCode: " + sheetData.getSheetCode() + "         sheetName: " +sheetData.getSheetName());
                DataMap headerData = sheetData.getHeaderData();
                if (null != headerData) {
                    System.out.println("header:");
                }
                System.out.print(blank);
                System.out.println(headerData);

                List<DataMap> bodyData = sheetData.getBodyData();
                System.out.println("body:");
                for (DataMap row : bodyData) {
                    System.out.print(blank);
                    System.out.println(row);
                }

                System.out.println();
            }
            System.out.println("·····························································");
        }
    }

}
