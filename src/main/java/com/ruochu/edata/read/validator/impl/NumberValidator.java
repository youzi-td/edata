package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.Rule;

import java.util.HashMap;
import java.util.Map;

/**
 * 数字类型
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:07
 */
public class NumberValidator implements IRuleValidator {
    private final Map<String, Integer[]> expMap = new HashMap<>();

    @Override
    public boolean validate(String value, Rule rule) {
        if (EmptyChecker.isEmpty(value)) {
            return true;
        }

        if (Constants.NUMBER_PATTERN.matcher(value).matches()){
            String exp = rule.getExpression();
            Integer[] exps = expMap.get(exp);
            if (exps == null) {
                exps = new Integer[]{
                        Integer.parseInt(exp.split(Constants.SEPARATOR)[0]),
                        Integer.parseInt(exp.split(Constants.SEPARATOR)[1]),
                };
                expMap.put(exp, exps);
            }

            int i = 0;
            for (String num : value.split("\\.")){
                if (num.length() > exps[i]){
                    return false;
                }
                i++;
            }
            return true;
        }


        return false;
    }
}
