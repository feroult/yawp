package io.yawp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static final String TIMESTAMP_FORMAT = "yyyy/MM/dd HH:mm:ss";

	public static final String DATE_FORMAT = "yyyy/MM/dd";

	public static String fromTimestamp(Date timestamp) {
		return format(timestamp, TIMESTAMP_FORMAT);
	}

	public static Date toTimestamp(String source) {
		return parse(source, TIMESTAMP_FORMAT);
	}

	public static String fromDate(Date timestamp) {
		return format(timestamp, DATE_FORMAT);
	}

	public static Date toDate(String source) {
		return parse(source, DATE_FORMAT);
	}

	private static String format(Date timestamp, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(timestamp);
	}

	private static Date parse(String source, String format) {
		SimpleDateFormat parser = new SimpleDateFormat(format);
		try {
			return parser.parse(source);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
