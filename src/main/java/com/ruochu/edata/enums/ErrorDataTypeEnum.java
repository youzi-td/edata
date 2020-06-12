package com.ruochu.edata.enums;

/**
 * 错误类型
 *
 * @author RanPengCheng
 * @date 2019/7/21 11:43
 */
public enum ErrorDataTypeEnum {
    TEMPLATE("template"),
    DATA("data")
    ;
    private String type;

    ErrorDataTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
