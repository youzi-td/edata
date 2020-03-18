package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.util.Context;
import com.ruochu.edata.xml.Rule;
import com.ruochu.edata.util.EmptyChecker;

import java.text.ParseException;

/**
 * 日期校验器
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:06
 */
public class DateValidator implements IRuleValidator {
    @Override
    public boolean validate(String value, Rule rule) {
        if (EmptyChecker.isEmpty(value)) {
            return true;
        }

        String[] formats = rule.getExpression().split(Constants.SEPARATOR);
        for (String format : formats) {
            try {
                Context.getDateFormat(format).parse(value);
                return true;
            } catch (ParseException e) {

            }
        }

        return false;
    }
}
