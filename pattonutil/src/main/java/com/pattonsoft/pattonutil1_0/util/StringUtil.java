package com.pattonsoft.pattonutil1_0.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static int fragment = 0;

    public static final String EMOJI = "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
    public static final String EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    public static final String MOBILE = "^1(3|4|5|7|8|9)\\d{9}$";

    public static final String IDCARD18 = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    public static final String IDCARD15 = "^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$";


    /**
     * 邮箱验证码判断
     *
     * @param email
     * @return
     */
    public static boolean ifEmail(String email) {
        Pattern pattern = Pattern
                .compile(EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 手机号判断
     *
     * @author zhao
     */
    public static boolean isMobile(String mobile) {
        Pattern p = Pattern.compile(MOBILE);
        Matcher m = p.matcher(mobile);

        return m.matches();
    }

    /**
     * 是否为表情符号
     *
     * @param content
     * @return
     */

    public static boolean isEmote(String content) {
        Pattern pattern = Pattern.compile(EMOJI);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    /**
     * 手机号判断
     *
     * @author zhao
     */
    public static boolean isIDCard(String idcard) {
        Matcher m;

        if (idcard.length() == 18) {
            Pattern p = Pattern.compile(IDCARD18);
            m = p.matcher(idcard);
            return m.matches();
        } else if (idcard.length() == 15) {
            Pattern p = Pattern.compile(IDCARD15);
            m = p.matcher(idcard);
            return m.matches();
        } else {
            return false;
        }


    }


    /**
     * 判断接受字符串是否为空的
     *
     * @param src
     * @return
     */
    public static boolean ifNull(String src) {
        if (null != src && !"null".equals(src) && src.length() > 0) {
            return false;
        }

        return true;
    }

    /**
     * 获取当前时间
     */
    public static String getTime() {
        String time = null;
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",
                Locale.getDefault());
        time = format.format(date);
        return time;
    }

    /**
     * 获取指定格式的时间
     */
    public static String getTime(String formate) {
        String time = null;
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(formate,
                Locale.getDefault());
        time = format.format(date);
        return time;
    }


    /**
     * 获取指定格式的时间
     */
    public static String getTimeForDate(String formate, Date date) {
        String time = null;
        SimpleDateFormat format = new SimpleDateFormat(formate,
                Locale.getDefault());
        time = format.format(date);
        return time;
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        return now.getTime();
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }


    /**
     * 产生6位随机数
     *
     * @return
     */
    public static String getCheckCode() {
        String s = "000000";
        try {
            int x1 = (int) (Math.random() * 10);
            int x2 = (int) (Math.random() * 10);
            int x3 = (int) (Math.random() * 10);
            int x4 = (int) (Math.random() * 10);
            int x5 = (int) (Math.random() * 10);
            int x6 = (int) (Math.random() * 10);
            s = x1 + "" + x2 + "" + x3 + "" + x4 + "" + x5 + "" + x6;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }
}
