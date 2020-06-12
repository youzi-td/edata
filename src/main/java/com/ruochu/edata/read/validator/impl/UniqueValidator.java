package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.xml.Rule;
import com.ruochu.edata.read.validator.IRuleValidator;

import java.util.*;

/**
 *
 * 唯一校验器
 *
 * @author : RanPengCheng
 * @date : 2020/3/11 10:11
 */
public class UniqueValidator implements IRuleValidator {
    private static final ThreadLocal<Map<String, Set<String>>> VALUES = new ThreadLocal<>();

    @Override
    public boolean validate(String value, Rule rule) {
        Map<String, Set<String>> map = VALUES.get();
        if (map == null) {
            map = new HashMap<>();
            VALUES.set(map);
        }

        Set<String> values = map.computeIfAbsent(rule.getUniqueKey(), k -> new LinkedHashSet<>());
        if (values.contains(value)) {
            return false;
        }

        values.add(value);

        return true;
    }

    @Override
    public void clear() {
        VALUES.remove();
    }
}
