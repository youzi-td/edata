package com.ruochu.edata.enums;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, TableTypeEnum> MAP;

    static {
        MAP = new HashMap<>();
        for (TableTypeEnum typeEnum : values()) {
            MAP.put(typeEnum.getType(), typeEnum);
        }
    }

    public String getType() {
        return type;
    }

    public static boolean exist(String type){
        return MAP.containsKey(type);
    }

}
