package com.pattonsoft.pattonutil1_0.util;

import java.util.Map;

/**
 * Created by zhao on 2017/4/27.
 */

public class MapUtil {
    public static int getInt(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Integer) return (Integer) value;
        else if (value instanceof Double)
            try {
                double a = (Double) map.get(key);
                return (int) a;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        else return 0;
    }

    public static float getFloat(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Float) return (Float) value;
        else if (value instanceof Double)
            try {
                double a = (Double) map.get(key);
                return (float) a;
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        else return 0;
    }

    public static double getDouble(Map<String, Object> map, String key) {

        try {
            return (double) (Double) map.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public static String getString(Map<String, Object> map, String key) {
        try {
            return (String) map.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            double d = (Double) map.get(key);
            return d + "";
        }
    }

    /**
     * 判断字符串是否为空
     * @param map
     * @return  boolean:map为空,返回真.map有值,返回假
     */
    public static boolean isEmpty(Map<String,Object> map){
        if(map == null || map.size()==0){
            return false;
        }
        return true;
    }
}
