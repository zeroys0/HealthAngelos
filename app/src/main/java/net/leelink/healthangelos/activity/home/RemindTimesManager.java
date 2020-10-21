package net.leelink.healthangelos.activity.home;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class RemindTimesManager {

    /**
     * 是否周六含有
     *
     * @param num
     * @return
     */
    public static boolean ifSaturdayHave(int num) {
        if (num >= 64 && num < 128) {
            return true;
        }
        return false;
    }

    /**
     * 是否周五含有
     *
     * @param num
     * @return
     */
    public static boolean ifFridayHave(int num) {
        if (num > 64) {
            num = num - 64;
        }
        if (num < 64 && num >= 32) {
            return true;
        }
        return false;
    }

    /**
     * 是否周四含有
     *
     * @param num Thursday Wednesday Tuesday
     * @return
     */
    public static boolean ifThursdayHave(int num) {
        if (num > 64) {
            num = num - 64;
        }
        if (num > 32) {
            num = num - 32;
        }

        if (num < 32 && num >= 16) {
            return true;
        }
        return false;
    }

    /**
     * 是否周三含有
     *
     * @param num
     * @return
     */
    public static boolean ifWednesdayHave(int num) {
        if (num > 64) {
            num = num - 64;
        }
        if (num > 32) {
            num = num - 32;
        }
        if (num > 16) {
            num = num - 16;
        }

        if (num < 16 && num >= 8) {
            return true;
        }
        return false;
    }

    /**
     * 是否周二含有
     *
     * @param num
     * @return
     */
    public static boolean ifTuesdayHave(int num) {
        if (num > 64) {
            num = num - 64;
        }
        if (num > 32) {
            num = num - 32;
        }
        if (num > 16) {
            num = num - 16;
        }
        if (num > 8) {
            num = num - 8;
        }

        if (num < 8 && num >= 4) {
            return true;
        }
        return false;
    }

    /**
     * 是否周一含有
     *
     * @param num
     * @return
     */
    public static boolean ifMondayHave(int num) {
        if (num > 64) {
            num = num - 64;
        }
        if (num > 32) {
            num = num - 32;
        }
        if (num > 16) {
            num = num - 16;
        }
        if (num > 8) {
            num = num - 8;
        }
        if (num > 4) {
            num = num - 4;
        }

        if (num < 4 && num >= 2) {
            return true;
        }
        return false;
    }

    /**
     * 是否周日含有
     *
     * @param num
     * @return
     */
    public static boolean ifSundayHave(int num) {
        if (num > 64) {
            num = num - 64;
        }
        if (num > 32) {
            num = num - 32;
        }
        if (num > 16) {
            num = num - 16;
        }
        if (num > 8) {
            num = num - 8;
        }
        if (num > 4) {
            num = num - 4;
        }
        if (num > 2) {
            num = num - 2;
        }

        if (num < 2 && num >= 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断此条提醒是否今日显示
     *
     * @param num type 显示类型
     * @param day 今日起第几天 比如:0 1 2 3 4 5 6
     */
    public static boolean ifTodayRemind(int num, int day_step, boolean state, int year, int month, int day) {
        //1. 未开启提醒则影藏
//         if(!state){
//             return false;
//         }

        Calendar calendar = new GregorianCalendar();
        Date date = new Date(System.currentTimeMillis()+day_step*24*3600*1000);

        calendar.setTime(date); //将日期转化为日历
        //提醒一次
        if (num == 0) {

            int year2 = calendar.get(Calendar.YEAR);
            int month2 = calendar.get(Calendar.MONTH) + 1;
            int day2 = calendar.get(Calendar.DAY_OF_MONTH);
            return year == year2 && month == month2 && day == day2;
        }
        //每月提箱
        if (num == 255) {

            calendar.setTime(date); //将日期转化为日历
            int day2 = calendar.get(Calendar.DAY_OF_MONTH);
            return day == day2;
        }
        //每周提醒
        if (num == 128) {

            calendar.setTime(date); //将日期转化为日历
            int week2 = calendar.get(Calendar.DAY_OF_WEEK);//1234567
            Calendar calendar2 = new GregorianCalendar();
            calendar2.set(year, month, day); //将日期转化为日历
            int week = calendar.get(Calendar.DAY_OF_WEEK);//1234567
            return week == week2;
        }
        //每日提醒
        if (num == 127) return true;

           long dataTime = System.currentTimeMillis();
        calendar.setTimeInMillis(dataTime + day_step * 24 * 60 * 60 * 1000); //将日期转化为日历
        int week2 = calendar.get(Calendar.DAY_OF_WEEK);//1234567  日一二三四五六

        switch (week2) {
            case 1:
                return ifSundayHave(num);
            case 2:
                return ifMondayHave(num);
            case 3:
                return ifTuesdayHave(num);
            case 4:
                return ifWednesdayHave(num);
            case 5:
                return ifThursdayHave(num);
            case 6:
                return ifFridayHave(num);
            case 7:
                return ifSaturdayHave(num);
        }

        return false;

    }

}
