package com.ruochu.edata;

/**
 *
 * 导入导出需要进行枚举转换的时候，枚举类实现该接口
 *
 * @author : RanPengCheng
 * @date : 2020/3/18 15:12
 */
public interface EDataBaseEnum {

    /**
     * 枚举的描述，导入时据此转换为枚举，导出时写入excel
     */
    String getDescription();
}
