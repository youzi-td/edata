package com.ruochu.edata.util;

import com.ruochu.edata.EDataBaseEnum;
import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.xml.CellConf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.ruochu.edata.util.EmptyChecker.isEmpty;
import static com.ruochu.edata.util.EmptyChecker.notEmpty;

/**
 * @author : RanPengCheng
 * @date : 2020/3/18 15:53
 */
public class MapToBeanUtil {

    private MapToBeanUtil(){}


    public static <T> T transfer(Map<String, String> map, Class<T> clazz, List<CellConf> cells) throws ReflectiveOperationException {
        T instance = clazz.newInstance();
        if (isEmpty(cells)) {
            return instance;
        }
        for (CellConf cell : cells) {
            String fieldName = cell.getField();
            Method setter = ReflectUtil.getSetter(clazz, fieldName);
            if (null != setter && EmptyChecker.notEmpty(map.get(fieldName))) {
                Class<?> parameterType = setter.getParameterTypes()[0];
                Field field = ReflectUtil.getField(clazz, fieldName);

                Object value = transformType(map.get(fieldName), parameterType, cell, field);

                setter.invoke(instance, value);
            }
        }

        return instance;
    }

    private static Object transformType(String s, Class<?> parameterType, CellConf cell, Field field) {
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
                value = toDate(s, cell);
            } else if (BigDecimal.class.isAssignableFrom(parameterType)) {
                value = new BigDecimal(s);
            } else if (Collection.class.isAssignableFrom(parameterType)) {
                value = toCollection(s, cell, parameterType, field);
            } else if (Enum.class.isAssignableFrom(parameterType) && EDataBaseEnum.class.isAssignableFrom(parameterType)) {
                value = toEnum(s, parameterType);
            } else if ("java.time".equals(parameterType.getPackage().getName())) {
                value = toTime(s, cell, parameterType);
            }

        } catch (Exception e) {
            return null;
        }

        return value;
    }

    @SuppressWarnings("unchecked")
    private static Object toCollection(String s, CellConf cell, Class<?> parameterType, Field field) throws ReflectiveOperationException {

        if (field == null) {
            return null;
        }

        String split = cell.getSplit();
        if (isEmpty(split)) {
            split = Constants.SEPARATOR;
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
                value.add(transformType(v, clazz, cell, null));
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

    private static Object toTime(String s, CellConf cell, Class<?> parameterType) throws ReflectiveOperationException {

        if (notEmpty(cell.getFormat())) {
            DateTimeFormatter dateTimeFormatter = Context.getDateTimeFormatter(cell.getFormat());

            Method parse = parameterType.getMethod("parse", CharSequence.class, DateTimeFormatter.class);
            return parse.invoke(null, s, dateTimeFormatter);
        }

        Method parse = parameterType.getMethod("parse", CharSequence.class);
        return parse.invoke(null, s);
    }

    private static Date toDate(String s, CellConf cell) throws ParseException {
        String format = cell.getFormat();
        if (isEmpty(format)) {
            format = Constants.DEFAULT_DATE_FORMAT;
        } else {
            format = format.split(Constants.SEPARATOR)[0];
        }

        return Context.getDateFormat(format).parse(s);
    }

}
