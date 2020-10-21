package com.pattonsoft.pattonutil1_0.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密管理
 * 
 * @author Administrator
 *
 */
public class MD5Manager {

	/**
	 * 对密码进行MD5加密
	 * 
	 * @param string 明文
	 *            
	 * @return re_md5 32位密�?
	 */
	public static String getMd5Code(String string) {

		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(string.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			re_md5 = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return re_md5;

	}

}
