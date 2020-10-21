package net.leelink.healthangelos.activity.home;

public class AlarmWayManager {


    /**
     * 添加jDoctor任务
     *
     * @param num
     * @return
     */
    public static boolean ifjDoctorHave(int num) {

        if (num < 16 && num >= 8) {
            return true;
        }
        return false;
    }

    /**
     * 文本推送
     *
     * @param num
     * @return
     */
    public static boolean ifTextHave(int num) {

        if (num > 8) {
            num = num - 8;
        }

        if (num < 8 && num >= 4) {
            return true;
        }
        return false;
    }

    /**
     * 邮件
     *
     * @param num
     * @return
     */
    public static boolean ifEmailHave(int num) {

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
     * 短信
     *
     * @param num
     * @return
     */
    public static boolean ifMessageHave(int num) {

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

}
