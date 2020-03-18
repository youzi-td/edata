package com.ruochu.edata.util;

import com.ruochu.edata.EDataBaseEnum;
import com.ruochu.edata.annotation.EDataFormat;
import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.xml.CellConf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author : RanPengCheng
 * @date : 2020/3/16 12:10
 */
public class BeanToMapUtil {

    private BeanToMapUtil(){}

    public static List<Map<String, String>> transformToStringMap(List<?> datas, List<CellConf> cells) {
        if (EmptyChecker.isEmpty(datas)) {
            return new ArrayList<>();
        }
        List<Map<String, String>> list = new LinkedList<>();
        for (Object data : datas) {
            list.add(transformToStringMap(data, cells));
        }

        return list;
    }

    public static Map<String, String> transformToStringMap(Object data, List<CellConf> cells) {
        if (EmptyChecker.isEmpty(data)) {
            return null;
        }
        Map<String, String> result = new HashMap<>(cells.size());
        if (data instanceof Map) {
            Map map = (Map) data;
            for (CellConf cell : cells) {
                String key = cell.getField();
                result.put(key, transformToStringValue(map.get(key), null));
            }
        } else {
            Map<String, Field> fieldMap = ReflectUtil.getClassFields(data.getClass());

            for (CellConf cell : cells) {
                String key = cell.getField();
                if (fieldMap.containsKey(key)) {
                    result.put(key, transformToStringValue(getValue(data, key), fieldMap.get(key)));
                } else {
                    result.put(key, "");
                }
            }
        }

        return result;
    }


    private static Object getValue(Object obj, String field) {
        Method getter = ReflectUtil.getGetter(obj.getClass(), field);
        if (getter != null) {
            try {
                return getter.invoke(obj);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private static String transformToStringValue(Object value, Field field) {
        String result = "";
        if (EmptyChecker.notEmpty(value)) {
            if (value instanceof Double) {
                result = doubleToString((Double) value);
            }else if ("java.lang".equals(value.getClass().getPackage().getName())) {
                result = value.toString();
            }else if (value instanceof Collection) {
                result = collectionToString((Collection) value, field);
            }else if (value instanceof Date) {
                result = dateToString((Date) value, field);
            }else if ("java.time".equals(value.getClass().getPackage().getName())) {
                result = javaTimeToString(value, field);
            }else if ((value instanceof Enum) && (value instanceof EDataBaseEnum)) {
                result = ((EDataBaseEnum) value).getDescription();
            }else {
                result = value.toString();
            }
        }

        return result;
    }


    private static String collectionToString(Collection value, Field field) {
        String split = Constants.SEPARATOR;
        if (null != field && field.isAnnotationPresent(EDataFormat.class)) {
            EDataFormat eDataFormat = field.getAnnotation(EDataFormat.class);
            split = eDataFormat.split();

        }
        split = ("".equals(split)) ? Constants.SEPARATOR : split;
        StringBuilder sb = new StringBuilder();
        for (Object obj : value) {
            sb.append(transformToStringValue(obj, field)).append(split);
        }
        sb.setLength(sb.length() - split.length());
        return sb.toString();
    }


    private static String dateToString(Date value, Field field) {
        String format = Constants.DEFAULT_DATE_FORMAT;
        if (null != field && field.isAnnotationPresent(EDataFormat.class)) {
            EDataFormat eDataFormat = field.getAnnotation(EDataFormat.class);
            format = eDataFormat.format();
        }

        return Context.getDateFormat(format).format(value);
    }

    private static String javaTimeToString(Object value, Field field) {
        if (null != field && field.isAnnotationPresent(EDataFormat.class)) {
            EDataFormat eDataFormat = field.getAnnotation(EDataFormat.class);
            String format = eDataFormat.format();
            if (EmptyChecker.notEmpty(format)) {
                DateTimeFormatter formatter = Context.getDateTimeFormatter(format);
                try {
                    Method method = value.getClass().getMethod("format", DateTimeFormatter.class);
                    if (null != method) {
                        return method.invoke(value, formatter).toString();
                    }
                } catch (Exception e) {
                    return value.toString();
                }
            }
        }
        return value.toString();
    }


    /**
     * double to String 防止科学计数法
     * @param value
     * @return
     */
    public static String doubleToString(Double value) {
        String temp = value.toString();
        if (temp.contains("E")) {
            BigDecimal bigDecimal = new BigDecimal(temp);
            temp = bigDecimal.toPlainString();
        }
        return temp;
    }
}
