package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.xml.Rule;
import com.ruochu.edata.util.EmptyChecker;

import java.util.*;

/**
 * 多选
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:06
 */
public class SelectionsValidator implements IRuleValidator {
    private final Map<String, Set<String>> selectionsMap = new HashMap<>();

    @Override
    public boolean validate(String value, Rule rule) {
        if (EmptyChecker.isEmpty(value)){
            return true;
        }
        String selectionsStr = rule.getValues();
        Set<String> selectionSet = selectionsMap.get(selectionsStr);
        if (null == selectionSet){
            selectionSet = new HashSet<>(Arrays.asList(selectionsStr.split(Constants.SEPARATOR)));
            selectionsMap.put(selectionsStr, selectionSet);
        }
        for (String val : value.split(Constants.SEPARATOR)){
            if (!selectionSet.contains(val)){
                return false;
            }
        }

        return true;
    }
}
