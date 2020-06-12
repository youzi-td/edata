package com.ruochu.edata;

/**
 *
 * 导入导出需要进行枚举、嵌套数据、集合等转换的时候，实现该接口
 *
 * @author : RanPengCheng
 * @date : 2020/3/18 15:12
 */
public interface EdataBaseDisplay {

    /**
     * excel里展示的数据
     */
    String display();
}
