package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String getDate() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");// �������ڸ�ʽ
		return (df.format(new Date()));// new Date()Ϊ��ȡ��ǰϵͳʱ��
	}

	public static String TimestamptoDate(int time) {
		long t = time * (long) 10000L;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return (df.format(t));
	}

}
