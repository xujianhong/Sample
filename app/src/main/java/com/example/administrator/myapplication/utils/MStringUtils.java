package com.example.administrator.myapplication.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@SuppressLint("SimpleDateFormat")
public class MStringUtils {

    private static SimpleDateFormat format = new SimpleDateFormat();

    public static boolean existEmpty(String[] strings) {
        for (String str : strings) {
            if (str == null || "".equals(str.trim())) {
                return true;
            }
        }
        return false;
    }

    public static String getTimeStr(String pattern) {
        if (pattern == null) {
            format.applyPattern("yyyy-MM-dd HH:mm:ss");
        } else {
            format.applyPattern(pattern);
        }
        return format.format(new Date());
    }

    public static String getRandomName(String fix) {
        format.applyPattern("MMddHHmmss");
        Random r = new Random();
        return format.format(new Date()) + (r.nextInt(89) + 10) + "." + fix;
    }


    /**
     * 日期格式字符串转换成时间戳
     *
     * @param date   字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime() / 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
