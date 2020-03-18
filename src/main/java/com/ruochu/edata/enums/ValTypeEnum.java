package com.ruochu.edata.enums;

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
    NUMBER(RuleTypeEnum.NUMBER.getType())
    ;
    String type;

    ValTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static boolean exist(String type){
        for (ValTypeEnum valType : values()){
            if (valType.type.equals(type)){
                return true;
            }
        }
        return false;
    }
}
