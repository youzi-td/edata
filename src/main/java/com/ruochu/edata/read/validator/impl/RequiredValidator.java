package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.Rule;

/**
 * 必填
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:06
 */
public class RequiredValidator implements IRuleValidator {
    @Override
    public boolean validate(String value, Rule rule) {
        return EmptyChecker.notEmpty(value);
    }
}
