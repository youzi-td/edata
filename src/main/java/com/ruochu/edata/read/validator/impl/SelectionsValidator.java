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
    private static final ThreadLocal<Map<String, Set<String>>> PASSED_CACHE = new ThreadLocal<>();

    @Override
    public boolean validate(String value, Rule rule) {
        if (EmptyChecker.isEmpty(value)){
            return true;
        }

        if (checked(value, rule.getUniqueKey())) {
            return true;
        }

        String selectionsStr = rule.getValues();
        Set<String> selectionSet = selectionsMap.get(selectionsStr);
        if (null == selectionSet){
            selectionSet = new HashSet<>(Arrays.asList(selectionsStr.split(Constants.SEPARATOR)));
            selectionsMap.put(selectionsStr, selectionSet);
        }
        for (String val : value.split(rule.getSplit())){
            if (!selectionSet.contains(val)){
                return false;
            }
        }

        PASSED_CACHE.get().get(rule.getUniqueKey()).add(value);

        return true;
    }

    private boolean checked(String value, String uniqueKey) {
        Map<String, Set<String>> map = PASSED_CACHE.get();
        if (map == null) {
            map = new HashMap<>();
            PASSED_CACHE.set(map);
        }

        Set<String> set = map.get(uniqueKey);
        if (set == null) {
            set = new HashSet<>();
            map.put(uniqueKey, set);
        }

        return set.contains(value);
    }

    @Override
    public void clear() {
        PASSED_CACHE.remove();
    }
}
