package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.xml.Rule;

/**
 * {描述}
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:07
 */
public class LengthValidator implements IRuleValidator {
    @Override
    public boolean validate(String value, Rule rule) {
        int maxLength = Integer.parseInt(rule.getExpression());
        return value.length() <= maxLength;
    }
}
