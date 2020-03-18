package com.ruochu.edata.reflect;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import java.lang.reflect.Field;

/**
 * @author RanPengCheng
 */
public class FieldDefaultValueProvider extends PureJavaReflectionProvider {
    /**
     * @param object 目标类的实例
     * @param fieldName XML中显示指明的字段
     * @param definedIn 父类或者类本身
     */
    @Override
    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
        //返回存在于xml中的字段
        Field field = fieldDictionary.field(object.getClass(), fieldName, definedIn);
        //验证字段可以被访问
        validateFieldAccess(field);
        try {
            // 如果xml未配置，则使用默认配置
            if (null != value) {
                if (value instanceof String){
                    field.set(object, ((String)value).trim());
                }else {
                    field.set(object, value);
                }
			}
        } catch (ReflectiveOperationException e) {
            throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
        }
    }  
} 
