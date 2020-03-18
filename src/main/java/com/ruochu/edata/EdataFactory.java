package com.ruochu.edata;

import com.ruochu.edata.read.DefaultReadService;
import com.ruochu.edata.read.ReadService;
import com.ruochu.edata.write.DefaultWriteService;
import com.ruochu.edata.write.WriteService;

/**
 * @author : RanPengCheng
 * @date : 2020/3/12 10:52
 */
public class EdataFactory {

    private EdataFactory(){}

    /**
     * 获取导入服务
     * @param templatePath excel模板路径（相对路径）
     * @param xmlPath xml配置路径（相对路径）
     */
    public static ReadService getReadService(String templatePath, String xmlPath) {
        return new DefaultReadService(templatePath, xmlPath);
    }


    public static WriteService getWriteService(String xmlPath) {
        return new DefaultWriteService(xmlPath);
    }


}
