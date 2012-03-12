package com.kogasoftware.odt.webapi.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

abstract public class Model {
	abstract public JSONObject toJSONObject() throws JSONException;

	private final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.S");

	protected String toJSON(Date value) {
		if (value == null) {
			return null;
		}
		return DATE_FORMAT.format(value);
	}

	protected Integer toJSON(Integer value) {
		return value;
	}

	protected String toJSON(String value) {
		return value;
	}

	protected Boolean toJSON(Boolean value) {
		return value;
	}

	protected Boolean parseBoolean(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return false;
		}
		return jsonObject.getBoolean(key);
	}

	protected Date parseDate(JSONObject jsonObject, String key)
			throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new Date();
		}
		String dateString = jsonObject.getString(key);
		Date date = DATE_FORMAT.parse(dateString);
		return date;
	}

	protected Integer parseInteger(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return 0;
		}
		return jsonObject.getInt(key);
	}

	protected String parseString(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return "";
		}
		return jsonObject.getString(key);
	}

	protected static Boolean wrapNull(Boolean value) {
		return value != null ? value : false;
	}

	protected static Integer wrapNull(Integer value) {
		return value != null ? value : 0;
	}

	protected static Date wrapNull(Date value) {
		return value != null ? value : new Date();
	}

	protected static String wrapNull(String value) {
		return value != null ? value : "";
	}

	protected static <T> Optional<T> wrapNull(Optional<T> value) {
		return value != null ? value : Optional.<T> absent();
	}
}
