package com.enableets.edu.filestorage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	// 时间工具类计算总时间
	public static String friendDuration(long ms) {
		short ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;
		long day = ms / (long) dd;
		long hour = (ms - day * (long) dd) / (long) hh;
		long minute = (ms - day * (long) dd - hour * (long) hh) / (long) mi;
		long second = (ms - day * (long) dd - hour * (long) hh - minute * (long) mi) / (long) ss;
		long milliSecond = ms - day * (long) dd - hour * (long) hh - minute * (long) mi - second * (long) ss;
		StringBuilder str = new StringBuilder();
		if (day > 0L) {
			str.append(day).append("天,");
		}
		if (hour > 0L) {
			str.append(hour).append("小时,");
		}
		if (minute > 0L) {
			str.append(minute).append("分钟,");
		}
		if (second > 0L) {
			str.append(second).append("秒,");
		}
		if (milliSecond > 0L) {
			str.append(milliSecond).append("毫秒,");
		}
		if (str.length() > 0) {
			str = str.deleteCharAt(str.length() - 1);
		}
		return str.toString();
	}

	public static void main(String[] args) {
		long time = 1531890785810L;
		System.out.println(timeStampToDate(time));
	}

	public static Date dateFormat(String dateString, String format) {
		Date result = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try {
			result = dateFormat.parse(dateString);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 时间戳转化为Date
	 * 
	 * @param time
	 * @return
	 */
	public static Date timeStampToDate(long time) {
		Date date = new Date(time);
		return date;
	}
}
