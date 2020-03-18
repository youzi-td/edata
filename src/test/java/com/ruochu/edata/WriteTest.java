package com.ruochu.edata;

import com.ruochu.edata.enums.ExcelType;
import com.ruochu.edata.model.AssetModel;
import com.ruochu.edata.model.UseIntentionEnum;
import com.ruochu.edata.model.ValueTypeEnum;
import com.ruochu.edata.util.BeanToMapUtil;
import com.ruochu.edata.util.MapToBeanUtil;
import com.ruochu.edata.util.XmlUtil;
import com.ruochu.edata.write.WriteService;
import com.ruochu.edata.xml.CellConf;
import com.ruochu.edata.xml.ExcelConf;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author : RanPengCheng
 * @date : 2020/3/16 10:57
 */
public class WriteTest {

    @Test
    public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime.format(formatter));

        LocalDate localDate = LocalDate.now();
        System.out.println(localDate.format(formatter));

//        LocalTime localTime = LocalTime.now();
//        System.out.println(localTime.format(formatter));

        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        System.out.println(zonedDateTime.format(formatter));


        Object format = localDate.getClass().getMethod("format", DateTimeFormatter.class).invoke(localDate, formatter);
        System.out.println(format);
    }


    @Test
    public void testBeanUtil() {
        AssetModel assetModel = new AssetModel();
        assetModel.setAmount(123);
        assetModel.setAssetCode("asdf");
        assetModel.setAssetName("name");
        assetModel.setAssetValue(new BigDecimal("10000000000.99"));
        assetModel.setObtainDate(new Date());
        List<UseIntentionEnum> list = new ArrayList<>();
        list.add(UseIntentionEnum.IDLE);
        list.add(UseIntentionEnum.BORROW);
        assetModel.setUseIntention(list);
        assetModel.setValueType(ValueTypeEnum.HAS_VALUE);
        assetModel.setLocalDate(LocalDate.now());
        assetModel.setLocalDateTime(LocalDateTime.now());
        assetModel.setZonedDateTime(ZonedDateTime.now());



        ExcelConf excelConf = XmlUtil.parseXmlConfig("xml/write.xml", false);

        Map<String, String> stringStringMap = BeanToMapUtil.transformToStringMap(assetModel, excelConf.getSheets().get(0).getHorizontalBody().getCells());
        System.out.println(stringStringMap);
    }

    @Test
    public void testWrite4NoneTemplate() throws IOException {
        int dataSize = 60000;

        List<AssetModel> list = new ArrayList<>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            list.add(mockAssetModel());
        }

        long l = System.currentTimeMillis();
        WriteService writeService = EdataFactory.getWriteService("xml/write-4-none-template.xml");
        System.out.println("xml:" + (System.currentTimeMillis() - l));

        l = System.currentTimeMillis();
        writeService.addBodyData("assetInfo", list);
        System.out.println("data:" + (System.currentTimeMillis() - l));

        writeService.excelType(ExcelType.XLS);
//        writeService.offXlsxHorizontalCacheWrite();

        l = System.currentTimeMillis();
        writeService.writeWithNoneTemplate(new FileOutputStream(new File("/Users/ranpengcheng/Desktop/asset.xls")));
        System.out.println("" + (System.currentTimeMillis() - l));
    }

    @Test
    public void testWrite() throws IOException {
        int dataSize = 6000;

        List<AssetModel> list = new ArrayList<>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            list.add(mockAssetModel());
        }

        Map<String, Object> head1 = new HashMap<>();
        head1.put("unitName", 123);
        head1.put("unitCode", 123.8888);
        head1.put("fillDate", new Date());
        head1.put("filler", "ranPC");

        Map<String, Object> body2 = new LinkedHashMap<>();
        for (int i = 1; i < 25; i++) {
            body2.put("f" + i, RANDOM.nextInt(100000000));
        }


        long l = System.currentTimeMillis();
        WriteService writeService = EdataFactory.getWriteService("xml/read.xml");
        System.out.println("xml:" + (System.currentTimeMillis() - l));

        l = System.currentTimeMillis();
        writeService.addBodyData("assetInfo", list);
        writeService.addHeaderData("assetInfo", head1);
        writeService.addHeaderData("balanceSheet", head1)
                .addBodyData("balanceSheet", body2);
        System.out.println("data:" + (System.currentTimeMillis() - l));

//        writeService.excelType(ExcelType.XLS);
//        writeService.offXlsxHorizontalCacheWrite();

        l = System.currentTimeMillis();
        writeService.write("template/testTemplate.xlsx", new FileOutputStream(new File("/Users/ranpengcheng/Desktop/asset2.xlsx")));
        System.out.println("" + (System.currentTimeMillis() - l));

    }

    private AssetModel mockAssetModel() {

        AssetModel assetModel = new AssetModel();
        assetModel.setAmount(RANDOM.nextInt(100000));
        assetModel.setAssetCode(randomStr());
        assetModel.setAssetName(randomStr());
        assetModel.setAssetValue(new BigDecimal("10000000000.99"));
        assetModel.setObtainDate(new Date());
        List<UseIntentionEnum> list = new ArrayList<>();
        list.add(UseIntentionEnum.IDLE);
        list.add(UseIntentionEnum.BORROW);
//        assetModel.setUseIntention(list);
//        assetModel.setValueType(ValueTypeEnum.HAS_VALUE);
        assetModel.setLocalDate(LocalDate.now());
        assetModel.setLocalDateTime(LocalDateTime.now());
        assetModel.setZonedDateTime(ZonedDateTime.now());

        return assetModel;
    }
    private static final Random RANDOM = new Random();
    private static final String STR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String randomStr() {
        int length = RANDOM.nextInt(30);
        if (length < 1) {
            length = 1;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            sb.append(STR.charAt(RANDOM.nextInt(35)));
        }

        return sb.toString();
    }

    private List<AssetModel> modelList(int size) {
        List<AssetModel> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(mockAssetModel());
        }
        return list;
    }



    @Test
    public void test2() throws ReflectiveOperationException {
        AssetModel assetModel = mockAssetModel();
        ExcelConf excelConf = XmlUtil.parseXmlConfig("xml/write-4-none-template.xml", false);
        List<CellConf> cells = excelConf.getSheets().get(0).getHorizontalBody().getCells();


        Map<String, String> map = BeanToMapUtil.transformToStringMap(assetModel, cells);

        AssetModel transfer = MapToBeanUtil.transfer(map, AssetModel.class);

        System.out.println(transfer);

    }

    @Test
    public void test3() throws ReflectiveOperationException {
        List<AssetModel> list = modelList(10000);
        ExcelConf excelConf = XmlUtil.parseXmlConfig("xml/write-4-none-template.xml", false);
        List<CellConf> cells = excelConf.getSheets().get(0).getHorizontalBody().getCells();

        List<Map<String, String>> list1 = BeanToMapUtil.transformToStringMap(list, cells);

        long l = System.currentTimeMillis();
        List<AssetModel> list2 = new ArrayList<>(list.size());
        for (Map<String, String> map : list1) {
            list2.add(MapToBeanUtil.transfer(map, AssetModel.class));
        }

        System.out.println(System.currentTimeMillis() - l);
    }

    @Test
    public void test4() {
        List<AssetModel> list = modelList(10000);
        ExcelConf excelConf = XmlUtil.parseXmlConfig("xml/write-4-none-template.xml", false);
        List<CellConf> cells = excelConf.getSheets().get(0).getHorizontalBody().getCells();

        List<Map<String, String>> list1 = BeanToMapUtil.transformToStringMap(list, cells);

        List<AssetModel> list2 = new ArrayList<>(list.size());
        long l = System.currentTimeMillis();
        for (Map<String, String> map : list1) {
//            JSON.toJSONStringWithDateFormat(map, "yyyy-MM-dd");
            AssetModel assetModel = JSON.parseObject(JSON.toJSONStringWithDateFormat(map, "yyyy-MM-dd"), AssetModel.class);
            list2.add(assetModel);
        }
        System.out.println(System.currentTimeMillis() - l);
        System.out.println(list2);
    }


}

