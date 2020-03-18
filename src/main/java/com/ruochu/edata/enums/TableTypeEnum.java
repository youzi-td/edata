package com.ruochu.edata.enums;

/**
 * 表格类型
 *
 * @author RanPengCheng
 * @date 2019/7/6 19:06
 */
public enum TableTypeEnum {
    /** 横表 */
    HORIZONTAL("horizontal"),
    /** 竖表 */
    VERTICAL("vertical")
    ;
    String type;

    TableTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static boolean exist(String type){
        for (TableTypeEnum table : values()){
            if (table.type.equals(type)){
                return true;
            }
        }
        return false;
    }
}
