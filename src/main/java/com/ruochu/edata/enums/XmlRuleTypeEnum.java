package com.ruochu.edata.enums;

/**
 * 校验规则类型
 *
 * @author RanPengCheng
 * @date 2019/7/6 16:13
 */
public enum XmlRuleTypeEnum {
    /** 长度 */
    MAX_LENGTH(RuleTypeEnum.MAX_LENGTH.getType()),
    /** 必填 */
    REQUIRED(RuleTypeEnum.REQUIRED.getType()),
    /** 单选 */
    SELECTION(RuleTypeEnum.SELECTION.getType()),
    /** 多选 */
    SELECTIONS(RuleTypeEnum.SELECTIONS.getType()),
    /** 必空 */
    BLANK(RuleTypeEnum.BLANK.getType()),
    /** 正则 */
    REGEX(RuleTypeEnum.REGEX.getType()),
    /** 公式 */
    BOOLEAN(RuleTypeEnum.BOOLEAN.getType()),
    /** 自定义 */
    CUSTOM(RuleTypeEnum.CUSTOM.getType()),
    /** 唯一 */
    UNIQUE(RuleTypeEnum.UNIQUE.getType()),
    ;
    private String type;

    XmlRuleTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static boolean exist(String type){
        for (XmlRuleTypeEnum rule : values()){
            if (rule.type.equals(type)){
                return true;
            }
        }
        return false;
    }

}
