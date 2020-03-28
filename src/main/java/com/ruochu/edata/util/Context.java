package com.ruochu.edata.util;

import com.ruochu.edata.constant.Constants;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : RanPengCheng
 * @date : 2020/3/16 15:53
 */
public class Context {
    private Context(){}

    private static final ThreadLocal<Map<String, DateFormat>> DATE_FORMAT_LOCAL = new ThreadLocal<>();
    private static final Map<String, DateTimeFormatter> DATE_TIME_FORMATTER_MAP = new HashMap<>();
    private static final ThreadLocal<Map<String, NumberFormat>> NUMBER_FORMAT_LOCAL = new ThreadLocal<>();



    public static DateFormat getDateFormat(String format) {
        Map<String, DateFormat> formatMap = DATE_FORMAT_LOCAL.get();
        if (formatMap == null) {
            formatMap = new HashMap<>();
            DATE_FORMAT_LOCAL.set(formatMap);
        }
        DateFormat dateFormat = formatMap.get(format);
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat(format);
            formatMap.put(format, dateFormat);
        }
        return dateFormat;
    }

    public static DateTimeFormatter getDateTimeFormatter(String format) {
        DateTimeFormatter formatter = DATE_TIME_FORMATTER_MAP.get(format);
        if (formatter == null) {
            formatter = DateTimeFormatter.ofPattern(format);
            DATE_TIME_FORMATTER_MAP.put(format, formatter);
        }
        return formatter;
    }

    public static NumberFormat getNumberFormat(String format) {
        Map<String, NumberFormat> formatMap = NUMBER_FORMAT_LOCAL.get();
        if (null == formatMap) {
            formatMap = new HashMap<>();
            NUMBER_FORMAT_LOCAL.set(formatMap);
        }

        NumberFormat numberFormat = formatMap.get(format);
        if (numberFormat == null) {
            String newFormat = format;
            if (format.endsWith("%")) {
                numberFormat = NumberFormat.getPercentInstance();
                newFormat = format.substring(0, format.length() - 1);
            } else {
                numberFormat = NumberFormat.getInstance();
            }

            String[] split = newFormat.split(Constants.SEPARATOR);
            numberFormat.setMaximumFractionDigits(Integer.parseInt(split[1]));

            formatMap.put(format, numberFormat);
        }

        return numberFormat;
    }

    public static void remove() {
        DATE_FORMAT_LOCAL.remove();
        NUMBER_FORMAT_LOCAL.remove();
    }
}
