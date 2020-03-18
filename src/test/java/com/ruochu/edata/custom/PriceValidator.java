package com.ruochu.edata.custom;

import com.ruochu.edata.read.validator.ICustomValidator;

import java.util.Map;

/**
 * {描述}
 *
 * @author RanPengCheng
 * @date 2019/7/13 16:31
 */
public class PriceValidator implements ICustomValidator {
    @Override
    public boolean validate(String value, Map<String, String> values) {
        System.out.println("自定义的PriceValidator校验器， value：" + value + ",  values：" + values);
        return true;
    }

}
