package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// 设置日期格式
		return (df.format(new Date()));// new Date()为获取当前系统时间
	}

	public static String TimestamptoDate(int time) {
		long t = time * (long) 10000L;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return (df.format(t));
	}

}
