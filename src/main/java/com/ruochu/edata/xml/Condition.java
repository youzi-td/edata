package com.ruochu.edata.xml;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 条件校验
 *
 * @author RanPengCheng
 * @date 2019/7/6
 */
public class Condition implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 目标 */
    @XStreamAsAttribute
    private String target;

    /** 目标的值范围 */
    @XStreamAsAttribute
    private String values;

    /** 满足条件后，要进行的校验 */
    @XStreamImplicit(itemFieldName = "rule")
    private List<Rule> rules;

    @XStreamAsAttribute
    private String errorMsg;

    private Set<String> valuesSet;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getTarget() {
        return target;
    }

    public String getValues() {
        return values;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Set<String> getValuesSet() {
        return valuesSet;
    }

    public void setValuesSet(Set<String> valuesSet) {
        this.valuesSet = valuesSet;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
