package com.ruochu.edata.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * cell的valType
 *
 * @author RanPengCheng
 * @date 2019/7/6 22:37
 */
public enum ValTypeEnum {
    /** 日期 */
    DATE(RuleTypeEnum.DATE.getType()),
    /** 数字 */
    NUMBER(RuleTypeEnum.NUMBER.getType()),
    /** 枚举 */
    ENUM("enum"),
    /** 图片 */
    IMAGE("image")
    ;
    String type;

    ValTypeEnum(String type) {
        this.type = type;
    }

    private static final Map<String, ValTypeEnum> MAP;

    static {
        MAP = new HashMap<>();
        for (ValTypeEnum typeEnum : values()) {
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
