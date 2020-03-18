package com.ruochu.edata.enums;

/**
 * 校验规则类型
 *
 * @author RanPengCheng
 * @date 2019/7/6 16:13
 */
public enum RuleTypeEnum {
    /** 数字 */
    NUMBER("number", "格式错误，整数位不超过%s位，小数位不超过%s位"),
    /** 日期 */
    DATE("date", "日期格式错误，正确格式：%s"),
    /** 长度 */
    MAX_LENGTH("maxLength", "长度不能超过%s位"),
    /** 必填 */
    REQUIRED("required", "必填校验失败"),
    /** 单选 */
    SELECTION("selection", "单选，范围为[%s]"),
    /** 多选 */
    SELECTIONS("selections", "多选，范围为[%s]"),
    /** 必空 */
    BLANK("blank", "必空校验失败"),
    /** 正则 */
    REGEX("regex", "校验不通过"),
    /** 公式 */
    BOOLEAN("boolean", "公式校验不通过"),
    /** 自定义 */
    CUSTOM("custom", "校验不通过"),
    /** 唯一 */
    UNIQUE("unique", "唯一性校验失败"),
    ;
    private String type;

    private String defaultErrorMsg;

    RuleTypeEnum(String type, String defaultErrorMsg) {
        this.type = type;
        this.defaultErrorMsg = defaultErrorMsg;
    }

    public String getType() {
        return type;
    }

    public static boolean exist(String type){
        for (RuleTypeEnum rule : values()){
            if (rule.type.equals(type)){
                return true;
            }
        }
        return false;
    }

    public static String defaultErrorMsg(String type) {
        for (RuleTypeEnum rule : values()) {
            if (rule.type.equals(type)) {
                return rule.defaultErrorMsg;
            }
        }
        return null;
    }
}
