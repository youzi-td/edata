package com.ruochu.edata.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author RanPengCheng
 */
public class EmptyChecker {

    private EmptyChecker() {
    }


    public static boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        } else if (obj instanceof String) {
            return "".equals(((String) obj).trim());
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }

        return false;
    }

    public static boolean notEmpty(Object obj) {
        return !isEmpty(obj);
    }

}
