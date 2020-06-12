package com.ruochu.edata.model;

import com.ruochu.edata.EdataBaseDisplay;

/**
 * @author : RanPengCheng
 * @date : 2020/3/16 16:29
 */
public enum UseIntentionDisplay implements EdataBaseDisplay {
    SELF(1, "自用"),
    BORROW(2, "出借"),
    RENT(3, "出租"),
    IDLE(4, "闲置")
    ;
    private Integer code;
    private String desc;

    UseIntentionDisplay(Integer code, String desc) {
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
    public String display() {
        return desc;
    }
}
