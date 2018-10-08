package sunlightMQ.jms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
	public static String getCurrentTime() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		return df.format(date);
	}

	public static String getCurrentDate() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return df.format(date);
	}

	public static String getNowYear() {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);// 获取年份
		return String.valueOf(year);
	}

	public static String getNowMonth() {
		Calendar ca = Calendar.getInstance();
		int month = ca.get(Calendar.MONTH) + 1;// 获取月份
		return String.valueOf(month);
	}

	public static String getNowDate() {
		Calendar ca = Calendar.getInstance();
		int day = ca.get(Calendar.DATE);// 获取当前日
		return String.valueOf(day);
	}

	public static String getLastDayOfNowMonth() {
		Calendar ca = Calendar.getInstance();
		int lastDay = ca.getActualMaximum(Calendar.DAY_OF_MONTH);// 获取当前月最后一天
		return String.valueOf(lastDay);
	}

	public static String getLastDayOfMonth(int year, int month) {
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.YEAR, year);
		ca.set(Calendar.MONTH, month - 1);
		int lastDay = ca.getActualMaximum(Calendar.DAY_OF_MONTH);// 获取当前月最后一天
		return String.valueOf(lastDay);
	}

	public static boolean isBeforeToday(String date) {
		return date.compareTo(getCurrentDate()) < 0;
	}

	public static int getDayBetween(String startDate, String endDate) throws Exception {
		Calendar start = Calendar.getInstance();
		Date sDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
		start.setTime(sDate);
		Calendar end = Calendar.getInstance();
		Date eDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
		end.setTime(eDate);

		int dayCount = (int) ((end.getTimeInMillis() - start.getTimeInMillis() + 24 * 60 * 60 * 1000) / 24 / 60 / 60
				/ 1000);

		return dayCount;
	}
}
