package com.ruochu.edata.util;

import java.text.DateFormat;
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

    public static void remove() {
        DATE_FORMAT_LOCAL.remove();
    }
}
