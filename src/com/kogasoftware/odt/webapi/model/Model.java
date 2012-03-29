package com.kogasoftware.odt.webapi.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.base.Optional;

abstract public class Model implements Serializable {
	public static final String TAG = Model.class.getSimpleName();

	private static final long serialVersionUID = -5513333240346057624L;

	protected static final DateTimeFormatter DATE_TIME_FORMATTER = ISODateTimeFormat
			.dateTime();

	protected static void errorIfNull(Object value) {
		if (value != null) {
			return;
		}
		NullPointerException e = new NullPointerException();
		Log.e(TAG, "unexpected null", e);
		// throw e;
	}

	protected static BigDecimal parseBigDecimal(JSONObject jsonObject,
			String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(jsonObject.getString(key));
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

	protected static Float parseFloat(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return 0f;
		}
		return (float)jsonObject.getDouble(key);
	}

	protected static Integer parseInteger(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return 0;
		}
		return jsonObject.getInt(key);
	}

	protected static Optional<Boolean> parseOptionalBoolean(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Boolean>absent();
		}
		return Optional.<Boolean>of(parseBoolean(jsonObject, key));
	}

	protected static Optional<Date> parseOptionalDate(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Date>absent();
		}
		return Optional.<Date>of(parseDate(jsonObject, key));
	}

	protected static Optional<Float> parseOptionalFloat(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Float>absent();
		}
		return Optional.<Float>of(parseFloat(jsonObject, key));
	}


	protected static Optional<Integer> parseOptionalInteger(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.<Integer>absent();
		}
		return Optional.<Integer>of(parseInteger(jsonObject, key));
	}

	protected static Optional<String> parseOptionalString(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.<String>absent();
		}
		return Optional.<String>of(parseString(jsonObject, key));
	}

	protected static String parseString(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return "";
		}
		return jsonObject.getString(key);
	}

	protected static Object toJSON(BigDecimal value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value.toString();
	}

	protected static Object toJSON(Boolean value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value;
	}

	protected static Object toJSON(Date value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return DATE_TIME_FORMATTER.print(value.getTime());
	}

	protected static Object toJSON(Float value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value;
	}

	protected static Object toJSON(Integer value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value;
	}

	protected static Object toJSON(List<? extends Model> value) throws JSONException {
		if (value == null) {
			return JSONObject.NULL;
		}
		JSONArray jsonArray = new JSONArray();
		for (Model model : value) {
			jsonArray.put(model.toJSONObject());
		}
		return jsonArray;
	}

	protected static Object toJSON(Optional<? extends Model> value) throws JSONException {
		if (value == null || !value.isPresent()) {
			return JSONObject.NULL;
		}
		return value.get().toJSONObject();
	}

	protected static Object toJSON(String value) {
		if (value == null) {
			return JSONObject.NULL;
		}
		return value;
	}

	protected static BigDecimal wrapNull(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	protected static Boolean wrapNull(Boolean value) {
		return value != null ? value : false;
	}

	protected static Date wrapNull(Date value) {
		return value != null ? value : new Date();
	}

	protected static Integer wrapNull(Integer value) {
		return value != null ? value : 0;
	}

	protected static <T> List<T> wrapNull(List<T> value) {
		return value != null ? value : new LinkedList<T>();
	}

	protected static <T> Optional<T> wrapNull(Optional<T> value) {
		return value != null ? value : Optional.<T> absent();
	}

	protected static String wrapNull(String value) {
		return value != null ? value : "";
	}

	abstract public JSONObject toJSONObject() throws JSONException;
}
