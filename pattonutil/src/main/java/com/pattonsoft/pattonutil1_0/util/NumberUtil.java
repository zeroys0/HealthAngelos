package com.pattonsoft.pattonutil1_0.util;

public class NumberUtil {
    /**
     * 判断是否位数字
     * @param str
     * @return
     */
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {

			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
