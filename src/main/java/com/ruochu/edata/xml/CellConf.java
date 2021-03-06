package com.ruochu.edata.xml;

import com.ruochu.edata.constant.Constants;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 单元格
 *
 * @author RanPengCheng
 * @date 2019/7/6
 */
public class CellConf implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 行号 */
    @XStreamAsAttribute
    private Integer rowIndex;

    /** 列号 */
    @XStreamAsAttribute
    private Integer colIndex;

    /** excel坐标，如第2行第4列：D2(或d2) */
    @XStreamAsAttribute
    private String position;

    /** 单元格的标题(名称) */
    @XStreamAsAttribute
    private String title;

    @XStreamAsAttribute
    private String field;

    /** 值类型 */
    @XStreamAsAttribute
    private String valType;

    /** 是否必填 */
    @XStreamAsAttribute
    private Boolean required = Boolean.TRUE;

    /** 数据格式 */
    @XStreamAsAttribute
    private String format;

    /** 校验规则 */
    @XStreamImplicit(itemFieldName = "rule")
    private List<Rule> rules;

    /** 条件校验 */
    @XStreamImplicit(itemFieldName = "condition")
    private List<Condition> conditions;

    /** 单元格的值，是否与标题处于同一个单元格，且值在标题后面 */
    @XStreamAsAttribute
    private Boolean followTitle = Boolean.FALSE;

    @XStreamAsAttribute
    private Integer maxLength;

    @XStreamAsAttribute
    private Boolean unique = Boolean.FALSE;

    @XStreamAsAttribute
    private Boolean autoSequence = Boolean.FALSE;

    @XStreamAsAttribute
    private String split = Constants.SEPARATOR;

    @XStreamAsAttribute
    private String writeDefault = "";

    @XStreamAsAttribute
    private String writeFormat;

    @XStreamAsAttribute
    private Integer mergeCellFromRight = 0;

    private boolean isDate = Boolean.FALSE;
    /** 是否为百分比的数字 */
    private boolean isPercentNumber;

    private boolean isEnum = Boolean.FALSE;

    private Map<String, String> enumKVMap;
    private Map<String, String> enumVKMap;

    public String getWriteDefault() {
        return writeDefault;
    }

    public boolean isPercentNumber() {
        return isPercentNumber;
    }

    public void setPercentNumber(boolean percentNumber) {
        isPercentNumber = percentNumber;
    }

    public String getSplit() {
        return split;
    }

    public Boolean getAutoSequence() {
        return autoSequence;
    }

    public Boolean getUnique() {
        return unique;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public Integer getColIndex() {
        return colIndex;
    }

    public String getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getValType() {
        return valType;
    }

    public Boolean getRequired() {
        return required;
    }

    public String getFormat() {
        return format;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setColIndex(Integer colIndex) {
        this.colIndex = colIndex;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getField() {
        return field;
    }

    public Boolean getFollowTitle() {
        return followTitle;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public boolean isDate() {
        return isDate;
    }

    public void setIsDate(boolean date) {
        isDate = date;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public void setEnum(boolean anEnum) {
        isEnum = anEnum;
    }

    public Map<String, String> getEnumKVMap() {
        return enumKVMap;
    }

    public void setEnumKVMap(Map<String, String> enumKVMap) {
        this.enumKVMap = enumKVMap;
    }

    public Map<String, String> getEnumVKMap() {
        return enumVKMap;
    }

    public void setEnumVKMap(Map<String, String> enumVKMap) {
        this.enumVKMap = enumVKMap;
    }

    public String getWriteFormat() {
        return writeFormat;
    }

    public Integer getMergeCellFromRight() {
        return mergeCellFromRight;
    }
}
