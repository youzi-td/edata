package com.ruochu.edata.xml.init;

import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.ExcelConf;

/**
 * excel Initiator
 *
 * @author RanPengCheng
 * @date 2019/7/10 12:38
 */
public class ExcelInitiator {

    private ExcelConf excelConf;
    private boolean isRead;

    public ExcelInitiator(ExcelConf excelConf, boolean isRead){
        this.excelConf = excelConf;
        this.isRead = isRead;
    }

    public void init(){
        // 1.检查属性
        checkAttr();
        // 2.初始化sheet
        new SheetInitiator(excelConf.getSheets(), isRead).init();
        if (isRead){
            // 3.初始化全局过滤器
            new FilterInitiator(excelConf.getGlobalFilter(), false).init();
        }

    }

    private void checkAttr() {
        if (EmptyChecker.isEmpty(excelConf.getSheets())){
            throw new XmlConfigException("excel标签缺少元素:sheet");
        }
    }

}
