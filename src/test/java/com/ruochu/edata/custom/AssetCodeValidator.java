package com.ruochu.edata.custom;

import com.ruochu.edata.read.validator.ICustomValidator;

import java.util.Map;

/**
 * {描述}
 *
 * @author RanPengCheng
 * @date 2019/7/13 15:06
 */
public class AssetCodeValidator implements ICustomValidator {
    @Override
    public boolean validate(String value, Map<String, String> values) {
        System.out.println("自定义的AssetCodeValidator校验器， value：" + value + ",  values：" + values);

        return true;
    }

}
