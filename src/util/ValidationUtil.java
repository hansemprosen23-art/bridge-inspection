package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 数据校验工具类
 */
public class ValidationUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    static {
        DATE_FORMAT.setLenient(false);
    }

    /**
     * 校验日期格式 yyyy-MM-dd
     */
    public static boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) return true; // 允许空
        try {
            DATE_FORMAT.parse(date.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 校验经度 -180 ~ 180
     */
    public static boolean isValidLongitude(String v) {
        if (v == null || v.trim().isEmpty()) return true;
        try {
            double lon = Double.parseDouble(v.trim());
            return lon >= -180 && lon <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 校验纬度 -90 ~ 90
     */
    public static boolean isValidLatitude(String v) {
        if (v == null || v.trim().isEmpty()) return true;
        try {
            double lat = Double.parseDouble(v.trim());
            return lat >= -90 && lat <= 90;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 校验评分 0-100
     */
    public static boolean isValidScore(int score) {
        return score >= 0 && score <= 100;
    }

    /**
     * 校验非空
     */
    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
