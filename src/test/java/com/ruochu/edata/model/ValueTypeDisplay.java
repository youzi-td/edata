package com.ruochu.edata.model;

import com.ruochu.edata.EdataBaseDisplay;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : RanPengCheng
 * @date : 2020/3/16 16:26
 */
public enum ValueTypeDisplay implements EdataBaseDisplay {
    HAS_VALUE(1, "有价值"),
    NO_VALUE(0, "无价值")

    ;

    private static final Map<String, ValueTypeDisplay> ENUM_MAP = new HashMap<>();
    static {
        for (ValueTypeDisplay typeEnum : values()) {
            ENUM_MAP.put(typeEnum.desc, typeEnum);
        }
    }

    private String desc;
    private Integer code;


    ValueTypeDisplay(Integer code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }

    public static ValueTypeDisplay getEnum(String desc) {
        return ENUM_MAP.get(desc);
    }

    @Override
    public String display() {
        return desc;
    }
}
