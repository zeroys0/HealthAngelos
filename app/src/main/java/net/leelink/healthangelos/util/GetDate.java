package net.leelink.healthangelos.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GetDate {
	
	public static String convertTimeOrDate(String str) {
		if (str.length() == 1) {
			str = "0" + str;
		}
		return str;
	}

	public static String MillsToDate(long mills) {
		Date dat = new Date(mills);
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(dat);
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String sb = format.format(gc.getTime());
		return sb;
	}

	// n天前的日期
	public static String getDateBefore(int day) {
		Date date = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(now
				.getTime());
		return dateString;
	}
	// n天后的日期
	public static String getDateAfter(int day) {
		Date date = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		String dateString = new SimpleDateFormat("yyyy-MM-dd").format(now
				.getTime());
		return dateString;
	}

	public static String currentFullTime() {
		String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
				.format(new Date());
		return dateString;
	}
	
	public static String currentTime() {
		String dateString = new SimpleDateFormat("HH:mm:ss")
				.format(new Date());
		return dateString;
	}
	public static String currentHM(){
		String dateString = new SimpleDateFormat("HH:mm")
				.format(new Date());
		return dateString;
	}
	
	public static String currentDate() {
		String dateString = new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date());
		return dateString;
	}

	public static String lastDay() {
		String dateString = new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date());
		return dateString;
	}

	// ���һ��ǰ������
	public static String lastWeek() {
		Date date = new Date();

		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
		int day = Integer.parseInt(new SimpleDateFormat("dd").format(date)) - 6;

		if (day < 1) {
			month -= 1;
			if (month == 0) {
				year -= 1;
				month = 12;
			}
			if (month == 4 || month == 6 || month == 9 || month == 11) {
				day = 30 + day;
			} else if (month == 1 || month == 3 || month == 5 || month == 7
					|| month == 8 || month == 10 || month == 12) {
				day = 31 + day;
			} else if (month == 2) {
				if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
					day = 29 + day;
				else
					day = 28 + day;
			}
		}
		String y = year + "";
		String m = "";
		String d = "";
		if (month < 10)
			m = "0" + month;
		else
			m = month + "";
		if (day < 10)
			d = "0" + day;
		else
			d = day + "";

		return y + "-" + m + "-" + d;
	}

	// ���һ��ǰ������
	public static String lastMonth() {
		Date date = new Date();

		int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
		int month = Integer.parseInt(new SimpleDateFormat("MM").format(date)) - 1;
		int day = Integer.parseInt(new SimpleDateFormat("dd").format(date));
		if (month == 0) {
			year -= 1;
			month = 12;
		} else if (day > 28) {
			if (month == 2) {
				if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
					day = 29;
				} else
					day = 28;
			} else if ((month == 4 || month == 6 || month == 9 || month == 11)
					&& day == 31) {
				day = 30;
			}
		}
		String y = year + "";
		String m = "";
		String d = "";
		if (month < 10)
			m = "0" + month;
		else
			m = month + "";
		if (day < 10)
			d = "0" + day;
		else
			d = day + "";

		return y + "-" + m + "-" + d;
	}
	/*
	 * //���һ��ǰ������ public String lastYear(){ Date date = new Date(); int
	 * year=Integer.parseInt(new SimpleDateFormat("yyyy").format(date))-1; int
	 * month=Integer.parseInt(new SimpleDateFormat("MM").format(date)); int
	 * day=Integer.parseInt(new SimpleDateFormat("dd").format(date));
	 * if(month==0){ year-=1;month=12; } else if(day>28){ if(month==2){
	 * if(year%400==0||(year %4==0&&year%100!=0))day=29+day; day=29; }else
	 * day=28; } } String y = year+"";String m ="";String d =""; if(month<10) m
	 * = "0"+month; else m=month+""; if(day<10) d = "0"+day; else d = day+"";
	 * 
	 * return y+"-"+m+"-"+d; }
	 */

}
