package com.kogasoftware.odt.webapi.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.base.Optional;

abstract public class Model implements Serializable {
	public static final String TAG = Model.class.getSimpleName();

	private static final long serialVersionUID = -5513333240346057624L;

	abstract public JSONObject toJSONObject() throws JSONException;

	protected static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat
			.dateTime();

	protected static Object toJSON(Date value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return DATE_TIME_FORMATTER.print(value.getTime());
	}

	protected static Object toJSON(BigDecimal value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value.toString();
	}

	protected static Object toJSON(Integer value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value;
	}

	protected static Object toJSON(String value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value;
	}

	protected static Object toJSON(Boolean value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value;
	}

	protected static Boolean parseBoolean(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return false;
		}
		return jsonObject.getBoolean(key);
	}

	protected static Date parseDate(JSONObject jsonObject, String key)
			throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new Date();
		}

		String dateString = jsonObject.getString(key);
		Date date = new Date(DATE_TIME_FORMATTER.parseDateTime(dateString)
				.getMillis());
		return date;
	}

	protected static Integer parseInteger(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return 0;
		}
		return jsonObject.getInt(key);
	}

	protected static BigDecimal parseBigDecimal(JSONObject jsonObject,
			String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(jsonObject.getString(key));
	}

	protected static String parseString(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return "";
		}
		return jsonObject.getString(key);
	}

	protected static void errorIfNull(Object value) {
		if (value != null) {
			return;
		}
		NullPointerException e = new NullPointerException();
		Log.e(TAG, "unexpected null", e);
		throw e;
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

	protected static BigDecimal wrapNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	protected static <T> Optional<T> wrapNull(Optional<T> value) {
		return value != null ? value : Optional.<T> absent();
	}
}
