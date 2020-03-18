package com.ruochu.edata.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 校验规则
 *
 * @author RanPengCheng
 * @date 2019/7/6
 */
public class Rule implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 规则类型 */
    @XStreamAsAttribute
    private String type;

    /** 表达式 */
    @XStreamAlias("expression")
    @XStreamAsAttribute
    private String xmlExpression;

    /** 值集，逗号隔开 */
    @XStreamAlias("values")
    @XStreamAsAttribute
    private String xmlValues;

    @XStreamAsAttribute
    private String errorMsg;


    private String expression;
    private String values;
    private List<String> expFields;
    private List<String> valFields;
    private String uniqueKey;

    public String getUniqueKey() {
        if (uniqueKey == null) {
            uniqueKey = UUID.randomUUID().toString();
        }
        return uniqueKey;
    }

    private Map<String, String> customValuesMap;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getXmlExpression() {
        return xmlExpression;
    }

    public void setXmlExpression(String xmlExpression) {
        this.xmlExpression = xmlExpression;
    }

    public String getXmlValues() {
        return xmlValues;
    }

    public void setXmlValues(String xmlValues) {
        this.xmlValues = xmlValues;
    }

    public Map<String, String> getCustomValuesMap() {
        return customValuesMap;
    }

    public void setCustomValuesMap(Map<String, String> customValuesMap) {
        this.customValuesMap = customValuesMap;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public List<String> getExpFields() {
        return expFields;
    }

    public void setExpFields(List<String> expFields) {
        this.expFields = expFields;
    }

    public List<String> getValFields() {
        return valFields;
    }

    public void setValFields(List<String> valFields) {
        this.valFields = valFields;
    }
}
