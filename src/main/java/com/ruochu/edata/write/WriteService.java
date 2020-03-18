package com.ruochu.edata.write;

import com.ruochu.edata.enums.ExcelType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author : RanPengCheng
 * @date : 2020/3/14 18:56
 */
public interface WriteService {

    /**
     * 一次添加多条body数据
     * 多次调用会根据sheetCode归类
     *
     * @param datas 数据对象列表，可以为list<Map>
     */
    WriteService addBodyData(String sheetCode, List<?> datas);

    /**
     * 添加一条body数据
     * 多次调用会根据sheetCode归类
     *
     * @param data 数据对象，可以为Map
     */
    WriteService addBodyData(String sheetCode, Object data);

    /**
     * 添加header数据
     * 多次调用会根据sheetCode归类，同一个sheetCode的数据，后者会覆盖前者
     */
    WriteService addHeaderData(String sheetCode, Object headerData);

    /**
     * 设置导出的Excel类型，默认为07版：XLSX
     */
    WriteService excelType(ExcelType excelType);

    /**
     * 关闭xlsx横表的缓存高速写功能
     * 该功能默认为打开状态，建议不关闭，但使用该功能有个限制条件需要特别注意：模板excel中开始填充数据的那一行及其以下，不能有任何内容
     */
    WriteService offXlsxHorizontalCacheWrite();

    /**
     * 有模板导出
     * 注：流会自动关闭
     */
    void write(String templateExcelPath, OutputStream out) throws IOException;

    /**
     * 无模板导出（仅支持横表，无header）
     * 注：流会自动关闭
     */
    void writeWithNoneTemplate(OutputStream out) throws IOException;

}
