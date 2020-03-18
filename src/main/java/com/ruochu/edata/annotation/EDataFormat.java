package com.ruochu.edata.annotation;

import com.ruochu.edata.constant.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : RanPengCheng
 * @date : 2020/3/16 13:12
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EDataFormat {

    /**
     * 日期格式化
     */
    String format() default "";

    /**
     * 集合分割符
     */
    String split() default Constants.SEPARATOR;

}
