package com.ruochu.edata.read.validator;

import java.util.Map;

/**
 * 自定义校验
 *
 * @author RanPengCheng
 * @date 2019/7/13 15:01
 */
public interface ICustomValidator {

    /**
     * 自定义的校验逻辑
     * @param cellValue 当前单元格的值（所有值都解析成了string类型）
     * @param values 传入的值
     * @return 校验是否通过
     */
    boolean validate(String cellValue, final Map<String, String> values);

}
