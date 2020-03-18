package com.ruochu.edata.read.validator.impl;

import com.ruochu.edata.exception.XmlConfigException;
import com.ruochu.edata.read.validator.ICustomValidator;
import com.ruochu.edata.read.validator.IRuleValidator;
import com.ruochu.edata.xml.Rule;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义校验器
 *
 * @author RanPengCheng
 * @date 2019/7/14 18:06
 */
public class CustomValidator implements IRuleValidator {
    private final static ThreadLocal<Map<String, ICustomValidator>> VALIDATOR_LOCAL = new ThreadLocal<>();

    @Override
    public boolean validate(String value, Rule rule) {

        String customClassName = rule.getXmlExpression();

        Map<String, ICustomValidator> customValidatorMap = VALIDATOR_LOCAL.get();
        if (customValidatorMap == null) {
            customValidatorMap = new HashMap<>();
            VALIDATOR_LOCAL.set(customValidatorMap);
        }

        ICustomValidator validator = customValidatorMap.get(customClassName);
        if (null == validator){
            try {
                Class clazz = Class.forName(customClassName);
                validator = (ICustomValidator) clazz.newInstance();
                customValidatorMap.put(customClassName, validator);
            } catch (ClassNotFoundException e) {
                throw new XmlConfigException("自定义校验类[" + customClassName + "]不存在！", e);
            } catch (IllegalAccessException | InstantiationException e) {
                throw new XmlConfigException(e);
            } catch (ClassCastException e) {
                throw new XmlConfigException("自定义校验类[" + customClassName + "]未实现ICustomValidator接口", e);
            }
        }

        return validator.validate(value, new HashMap<>(rule.getCustomValuesMap()));
    }

    @Override
    public void clear() {
        VALIDATOR_LOCAL.remove();
    }
}
