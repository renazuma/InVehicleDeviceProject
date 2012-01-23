package com.kogasoftware.odt.invehicledevice;

import java.util.regex.Pattern;

public class LogTag {
	public static String get(Class<?> c) {
		String truncated = Pattern.compile("^.*\\.").matcher(c.getName())
				.replaceFirst("");
		return truncated;
	}
}
