package com.ruochu.edata.custom;

import com.ruochu.edata.read.validator.ICustomValidator;

import java.util.Map;

/**
 * {描述}
 *
 * @author RanPengCheng
 * @date 2019/7/13 16:18
 */
public class AssetValueValidator implements ICustomValidator {
    @Override
    public boolean validate(String value, Map<String, String> values) {
        return false;
    }

}
