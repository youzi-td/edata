package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.xml.Rule;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * 布尔表达式校验器
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:06
 */
public class BooleanValidator implements IRuleValidator {
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
    @Override
    public boolean validate(String value, Rule rule) {
        String expression = rule.getExpression();
        try {
            return (Boolean) engine.eval(expression);
        } catch (Exception e) {
            throw new ERuntimeException("布尔表达式异常！expression:" + expression, e);
        }
    }

}
