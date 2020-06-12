package com.ruochu.edata.custom;

import com.ruochu.edata.read.validator.ICustomValidator;

import java.util.Map;

/**
 * 价格校验器
 */
public class PriceValidator implements ICustomValidator {
    @Override
    public boolean validate(String cellValue, Map<String, String> values) {
        System.out.println("当前单元格的值：" + cellValue);
        System.out.println("传入的值列表：" + values);

        System.out.println("自定义校验逻辑。。。");

        // 返回校验结果
        return true;
    }
}
