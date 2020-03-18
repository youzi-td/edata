package com.ruochu.edata.util;

/**
 * @author RanPengCheng
 */
public class CoordinateUtil {

    private CoordinateUtil() {
    }


    /**
     * 将以字母表示的Excel列数转换成数字表示
     *
     * @param letter 以字母表示的列数，不能为空且只允许包含字母字符
     * @return 返回转换的数字，转换失败返回-1
     * @author ranpengcheng
     */
    public static int letterToNumber(String letter) {
        // 检查字符串是否为空
        if (letter == null || letter.isEmpty()) {
            return -1;
        }
        // 转为大写字符串
        String upperLetter = letter.toUpperCase();
        // 检查是否符合，不能包含非字母字符
        if (!upperLetter.matches("[A-Z]+")) {
            return -1;
        }
        // 存放结果数值
        long num = 0;
        long base = 1;
        // 从字符串尾部开始向头部转换
        for (int i = upperLetter.length() - 1; i >= 0; i--) {
            char ch = upperLetter.charAt(i);
            num += (ch - 'A' + 1) * base;
            base *= 26;
            // 防止内存溢出
            if (num > Integer.MAX_VALUE) {
                return -1;
            }
        }
        return (int) num;
    }

    /**
     * 将数字转换成以字母表示的Excel列数
     *
     * @param num 表示列数的数字
     * @return 返回转换的字母字符串，转换失败返回null
     * @author ranpengcheng
     */
    public static String numberToLetter(int num) {
        // 检测列数是否正确
        if (num <= 0) {
            return null;
        }
        StringBuilder letter = new StringBuilder();
        do {
            --num;
            // 取余
            int mod = num % 26;
            // 组装字符串
            letter.append((char) (mod + 'A'));
            // 计算剩下值
            num = (num - mod) / 26;
        } while (num > 0);
        // 返回反转后的字符串
        return letter.reverse().toString();
    }

    /**
     * excel坐标转换为数字坐标
     * @param coordinate excel坐标
     * @return 列号：下标为0，行号：下标为1
     */
    public static int[] toNumberPosition(String coordinate) {

        int[] numCoordinate = new int[2];

        String letter = coordinate.toUpperCase();
        int index = 0;
        // 存放结果数值
        int column = 0;
        int base = 1;
        for (; index < letter.length(); index++) {
            char ch = letter.charAt(index);
            if (ch >= 'A') {
                column += (ch - 'A' + 1) * base;
                base *= 26;
            }else {
                break;
            }
        }

        numCoordinate[0] = column;
        numCoordinate[1] = Integer.valueOf(letter.substring(index));

        return numCoordinate;
    }


    /**
     * 数字坐标转excel坐标
     * @param colIndex 列坐标
     * @param rowIdex 行坐标
     * @return
     */
    public static String toExcelPosition(int colIndex, int rowIdex) {
        return numberToLetter(colIndex) + rowIdex;
    }


    public static boolean isExcelPosition(String position){
        if (EmptyChecker.isEmpty(position)){
            return false;
        }
        return position.toUpperCase().matches("[A-Z]+[0-9]+");
    }

}
