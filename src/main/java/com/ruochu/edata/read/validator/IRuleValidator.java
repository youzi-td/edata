package com.ruochu.edata.read.validator;


import com.ruochu.edata.xml.Rule;

/**
 * 校验器
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:04
 */
public interface IRuleValidator {

    boolean validate(String value, Rule rule);

    /**
     * 清理缓存等
     */
    default void clear() {}
}
