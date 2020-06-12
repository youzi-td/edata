package com.ruochu.edata.util;

import com.ruochu.edata.EdataBaseDisplay;
import com.ruochu.edata.Image;
import com.ruochu.edata.constant.Constants;
import com.ruochu.edata.enums.ValTypeEnum;
import com.ruochu.edata.exception.ERuntimeException;
import com.ruochu.edata.xml.CellConf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.ruochu.edata.util.EmptyChecker.isEmpty;
import static com.ruochu.edata.util.EmptyChecker.notEmpty;

/**
 * @author : RanPengCheng
 * @date : 2020/3/16 12:10
 */
public class BeanToMapUtil {

    private BeanToMapUtil(){}

    public static List<Map<String, Object>> transformToStringMap(Collection<?> datas, List<CellConf> cells) {
        if (isEmpty(datas)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> list = new LinkedList<>();
        for (Object data : datas) {
            list.add(transformToStringMap(data, cells));
        }

        return list;
    }

    public static Map<String, Object> transformToStringMap(Object data, List<CellConf> cells) {
        if (isEmpty(data)) {
            return null;
        }
        Map<String, Object> result = new HashMap<>(cells.size());
        if (data instanceof Map) {
            Map<?,?> map = (Map<?,?>) data;
            for (CellConf cell : cells) {
                String key = cell.getField();
                result.put(key, transformToStringValue(map.get(key), cell));
            }
        } else {
            Map<String, Field> fieldMap = ReflectUtil.getClassFields(data.getClass());

            for (CellConf cell : cells) {
                String key = cell.getField();
                if (fieldMap.containsKey(key)) {
                    result.put(key, transformToStringValue(ReflectUtil.getValue(data, key), cell));
                } else {
                    result.put(key, cell.getWriteDefault());
                }
            }
        }

        return result;
    }




    private static Object transformToStringValue(Object value, CellConf cell) {
        // 空值直接返回默认值
        if (isEmpty(value)) {
            return cell.getWriteDefault();
        }

        if (ValTypeEnum.IMAGE.getType().equals(cell.getValType())) {
            if (!(value instanceof Image)) {
                throw new ERuntimeException("图片类型必须为cn.yzw.edata.Image");
            }
            return value;
        }

        // 数字直接返回
        if (value instanceof Number && !cell.isEnum() && !cell.isPercentNumber()) {
            return value;
        }

        String result;
        if (cell.isPercentNumber() && value instanceof Number) {
            result = Context.getNumberFormat(cell.getFormat()).format(value);
        } else if (value instanceof Collection) {
            // 集合
            result = collectionToString((Collection<?>) value, cell);
        }else if (value instanceof Date) {
            // 日期
            result = dateToString((Date) value, cell);
        }else if ("java.time".equals(value.getClass().getPackage().getName())) {
            // jdk8时间
            result = javaTimeToString(value, cell);
        }else if (value instanceof EdataBaseDisplay) {
            // 特殊格式化
            result = ((EdataBaseDisplay) value).display();
        }else {
            // 其他
            result = value.toString();
        }

        // 枚举
        if (cell.isEnum()) {
            result = cell.getEnumKVMap().get(result);
        }

        return result;
    }


    private static String collectionToString(Collection<?> value, CellConf cell) {
        String split = cell.getSplit();

        split = ("".equals(split)) ? Constants.SEPARATOR : split;
        StringBuilder sb = new StringBuilder();
        for (Object obj : value) {
            sb.append(transformToStringValue(obj, cell)).append(split);
        }
        sb.setLength(sb.length() - split.length());
        return sb.toString();
    }


    private static String dateToString(Date value, CellConf cell) {
        // 优先使用writeFormat
        String format = cell.getWriteFormat();
        format = (isEmpty(format) ? cell.getFormat() : format);

        if (isEmpty(format)) {
            format = Constants.DEFAULT_DATE_FORMAT;
        } else {
            format = format.split(Constants.SEPARATOR)[0];
        }

        return Context.getDateFormat(format).format(value);
    }

    private static String javaTimeToString(Object value, CellConf cell) {
        // 优先使用writeFormat
        String format = cell.getWriteFormat();
        format = (isEmpty(format) ? cell.getFormat() : format);

        if (notEmpty(format)) {
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
        return value.toString();
    }
}
