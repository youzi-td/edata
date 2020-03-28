package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.util.EmptyChecker;
import com.ruochu.edata.xml.Rule;

import java.util.*;

/**
 * 单选
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:06
 */
public class SelectionValidator implements IRuleValidator {
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
        return selectionSet.contains(value);
    }
}
