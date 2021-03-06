package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.xml.Rule;
import com.ruochu.edata.util.EmptyChecker;

/**
 * 必空校验器
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:06
 */
public class BlankValidator implements IRuleValidator {
    @Override
    public boolean validate(String value, Rule rule) {
        return EmptyChecker.isEmpty(value);
    }
}
