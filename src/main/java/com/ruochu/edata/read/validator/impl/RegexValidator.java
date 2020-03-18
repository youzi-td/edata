package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.xml.Rule;
import com.ruochu.edata.util.EmptyChecker;

import java.util.regex.Pattern;

/**
 * 正则校验
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:06
 */
public class RegexValidator implements IRuleValidator {
    @Override
    public boolean validate(String value, Rule rule) {
        if (EmptyChecker.isEmpty(value)){
            return true;
        }
        return Pattern.matches(rule.getExpression(), value);
    }
}
