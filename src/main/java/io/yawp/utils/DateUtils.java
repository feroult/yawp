package io.yawp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	public static final String TIMESTAMP_FORMAT = "yyyy/MM/dd HH:mm:ss";

	public static String fromTimestamp(Date timestamp) {
		SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
		return formatter.format(timestamp);
	}

	public static Date toTimestamp(String source) {
		SimpleDateFormat parser = new SimpleDateFormat(TIMESTAMP_FORMAT);
		try {
			return parser.parse(source);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
