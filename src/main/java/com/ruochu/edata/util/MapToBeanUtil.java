package com.ruochu.edata.util;

import com.ruochu.edata.EDataBaseEnum;
import com.ruochu.edata.annotation.EDataFormat;
import com.ruochu.edata.constant.Constants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author : RanPengCheng
 * @date : 2020/3/18 15:53
 */
public class MapToBeanUtil {

    private MapToBeanUtil(){}


    public static <T> T transfer(Map<String, String> map, Class<T> clazz) throws ReflectiveOperationException {
        T instance = clazz.newInstance();
        for (String fieldName : map.keySet()) {
            Method setter = ReflectUtil.getSetter(clazz, fieldName);
            if (null != setter && EmptyChecker.notEmpty(map.get(fieldName))) {
                Class<?> parameterType = setter.getParameterTypes()[0];
                Field field = ReflectUtil.getField(clazz, fieldName);

                Object value = transformType(map.get(fieldName), parameterType, field);

                setter.invoke(instance, value);
            }
        }

        return instance;
    }

    private static Object transformType(String s, Class<?> parameterType, Field field) {
        String parameterTypeName = parameterType.getName();
        if("java.lang.String".equals(parameterTypeName)) {
            return s;
        }

        Object value = null;

        try {
            if ("java.lang".equals(parameterType.getPackage().getName())) {
                return parameterType.getConstructor(String.class).newInstance(s);
            }

            if (Date.class.isAssignableFrom(parameterType)) {
                value = toDate(s, field);
            } else if (BigDecimal.class.isAssignableFrom(parameterType)) {
                value = new BigDecimal(s);
            } else if (Collection.class.isAssignableFrom(parameterType)) {
                value = toCollection(s, field, parameterType);
            } else if (Enum.class.isAssignableFrom(parameterType) && EDataBaseEnum.class.isAssignableFrom(parameterType)) {
                value = toEnum(s, parameterType);
            } else if ("java.time".equals(parameterType.getPackage().getName())) {
                value = toTime(s, field, parameterType);
            }

        } catch (Exception e) {
            return null;
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private static Object toCollection(String s, Field field, Class<?> parameterType) throws ReflectiveOperationException {

        if (field == null) {
            return null;
        }

        String split = Constants.SEPARATOR;
        if (field.isAnnotationPresent(EDataFormat.class)) {
            EDataFormat eDataFormat = field.getAnnotation(EDataFormat.class);
            split = eDataFormat.split();
        }
        String[] values = s.split(split);

        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            Collection value = null;
            try{
                value = (Collection) parameterType.newInstance();
            } catch (Exception e) {
                if (List.class.isAssignableFrom(parameterType)) {
                    value = new ArrayList();
                } else if (Set.class.isAssignableFrom(parameterType)) {
                    value = new HashSet();
                }
            }
            if (value == null) {
                return null;
            }

            ParameterizedType pt = (ParameterizedType) genericType;
            Class<?> clazz = (Class<?>) pt.getActualTypeArguments()[0];

            for (String v : values) {
                value.add(transformType(v, clazz, null));
            }

            return value;
        }
        return null;
    }

    private static Object toEnum(String s, Class<?> parameterType) throws ReflectiveOperationException {
        Method values = parameterType.getMethod("values");
        EDataBaseEnum[] enums = (EDataBaseEnum[])values.invoke(null);
        for (EDataBaseEnum eDataBaseEnum : enums) {
            if (eDataBaseEnum.getDescription().equals(s)) {
                return eDataBaseEnum;
            }
        }
        return null;
    }

    private static Object toTime(String s, Field field, Class<?> parameterType) throws ReflectiveOperationException {

        if (field != null && field.isAnnotationPresent(EDataFormat.class)) {
            EDataFormat eDataFormat = field.getAnnotation(EDataFormat.class);
            DateTimeFormatter dateTimeFormatter = Context.getDateTimeFormatter(eDataFormat.format());

            Method parse = parameterType.getMethod("parse", CharSequence.class, DateTimeFormatter.class);
            return parse.invoke(null, s, dateTimeFormatter);
        }

        Method parse = parameterType.getMethod("parse", CharSequence.class);
        return parse.invoke(null, s);
    }

    private static Date toDate(String s, Field field) throws ParseException {
        String format = "";
        if (field != null && field.isAnnotationPresent(EDataFormat.class)) {
            EDataFormat eDataFormat = field.getAnnotation(EDataFormat.class);
            format = eDataFormat.format();
        }
        if (EmptyChecker.isEmpty(format)) {
            format = Constants.DEFAULT_DATE_FORMAT;
        }

        return Context.getDateFormat(format).parse(s);
    }

}
