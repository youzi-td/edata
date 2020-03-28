package com.ruochu.edata.constant;

import java.util.regex.Pattern;

/**
 * 常量类
 *
 * @author RanPengCheng
 * @date 2019/7/12 21:45
 */
public class Constants {

    private Constants(){}

    public static final String SEPARATOR = ",";

    public static final String NUMBER_FORMAT = "(0|[1-9]\\d*),[0-9]+";
    public static final String NUMBER_REGEX = "([-+])?(0|[1-9]\\d+)(\\.\\d+)?";
    public static final String LENGTH_FORMAT = "[1-9]\\d*";

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public static final String OPENT = "${";
    public static final String CLOSE = "}";

    public static final String FIELD_FLAG = "${}";

    public static final Pattern NUMBER_PATTERN = Pattern.compile("([-+])?(0|[1-9]\\d+)(\\.\\d+)?");

    public static final Pattern RGB_PATTERN = Pattern.compile("#[0-9a-fA-F]{6}");

}
