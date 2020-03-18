package com.ruochu.edata.model;

import com.ruochu.edata.EDataBaseEnum;

/**
 * @author : RanPengCheng
 * @date : 2020/3/16 16:29
 */
public enum UseIntentionEnum implements EDataBaseEnum {
    SELF(1, "自用"),
    BORROW(2, "出借"),
    RENT(3, "出租"),
    IDLE(4, "闲置")
    ;
    private Integer code;
    private String desc;

    UseIntentionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String getDescription() {
        return desc;
    }
}
