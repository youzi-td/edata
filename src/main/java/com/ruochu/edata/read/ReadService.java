package com.ruochu.edata.read;

import com.ruochu.edata.exception.UnknownFileTypeException;
import com.ruochu.edata.read.result.ReadResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author : RanPengCheng
 * @date : 2020/3/14 18:34
 */
public interface ReadService {

    /**
     * 导入
     * @param excelAbsPath 导入的excel的绝对路径
     */
    ReadResult read(String excelAbsPath) throws IOException, UnknownFileTypeException ;

    /**
     * 导入
     * @param file 导入的Excel文件
     */
    ReadResult read(File file) throws IOException, UnknownFileTypeException ;

    /**
     * 导入
     * @param is 导入的excel文件流
     */
    ReadResult read(InputStream is) throws IOException, UnknownFileTypeException;


    /**
     * 设置最大错误记录数，满足了则导入停止，直接返回结果
     * 适用于大量的数据导入操作，设置一个错误数上限，有助于快速返回，并保护程序
     * 默认为Integer.MAX_VALUE（不限制）
     */
    ReadService setMaxErrorCount(Integer maxErrorCount);

    /**
     * 设置横表的最大连续空行数
     * 某些特殊的情况，用户导入的数据与数据之间存在空行，通过此参数调整允许的最大连续空行数
     * 默认为Integer.MAX_VALUE（不限制）
     */
    ReadService setMaxHorizontalSerialBlankRow(Integer maxHorizontalSerialBlankRow);
}
