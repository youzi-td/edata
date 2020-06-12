package com.ruochu.edata.xml.init;

import com.ruochu.edata.xml.Condition;
import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.Rule;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 条件Initiator
 *
 * @author RanPengCheng
 * @date 2019/7/10 12:40
 */
public class ConditionInitiator {

    private List<Condition> conditions;
    private Condition currentCondition;

    private String cellTitle;

    ConditionInitiator(List<Condition> conditions, String cellTitle) {
        this.conditions = conditions;
        this.cellTitle = cellTitle;
    }

    void init(){
        for (Condition condition : conditions){
            this.currentCondition = condition;
            checkAttr();
            initCondition();
            new RuleInitiator(condition.getRules(), cellTitle);
        }

    }

    private void initCondition() {
        currentCondition.setValuesSet(new HashSet<>(Arrays.asList(currentCondition.getValues().split(Constants.SEPARATOR))));
        String target = currentCondition.getTarget();
        currentCondition.setTarget(StringUtils.substringBetween(target, Constants.OPENT, Constants.CLOSE));

        String errorMsg = currentCondition.getErrorMsg();
        if (EmptyChecker.isEmpty(currentCondition.getErrorMsg())) {
            errorMsg = "校验不通过";
        }
        for (Rule rule : currentCondition.getRules()) {
            if (EmptyChecker.isEmpty(rule.getErrorMsg())) {
                rule.setErrorMsg(errorMsg);
            }
        }
    }

    private void checkAttr() {
        String target = currentCondition.getTarget();
        String values = currentCondition.getValues();
        List<Rule> rules = currentCondition.getRules();


        if (EmptyChecker.isEmpty(target)){
            throw new XmlConfigException("condition的target不能为空！cell:" + cellTitle);
        }
        if (!target.startsWith(Constants.OPENT) || !target.endsWith(Constants.CLOSE)){
            throw new XmlConfigException("condition的target[" + target + "]不合法，应该开始于[ ${ ],结束于[ } ]! cell:" + cellTitle);
        }
        if (EmptyChecker.isEmpty(values)){
            throw new XmlConfigException("condition的values属性不能为空！cell:" + cellTitle);
        }
        if (EmptyChecker.isEmpty(rules)){
            throw new XmlConfigException("condition的rule元素不能为空！cell:" + cellTitle);
        }
    }

}
