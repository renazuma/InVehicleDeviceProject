package com.kogasoftware.odt.webapi.model.base;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.webapi.Identifiable;

public abstract class Model implements Serializable, Identifiable, Cloneable {
	public static final String TAG = Model.class.getSimpleName();
	public static final Integer MAX_RECURSE_DEPTH = 11;

	private static final long serialVersionUID = -5513333240346057624L;

	protected static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat
			.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");
	protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern("yyyy-MM-dd");

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
			throws JSONException {
		Date date = new Date();
		if (!jsonObject.has(key)) {
			return new Date();
		}

		String dateString = jsonObject.getString(key);
		try {
			date = new Date(DATE_TIME_FORMATTER.parseDateTime(dateString)
					.getMillis());
		} catch (IllegalArgumentException ex) {
			try {
				date = new Date(DATE_FORMATTER.parseDateTime(dateString)
						.getMillis());
			} catch (IllegalArgumentException ex2) {
				Log.w(TAG, ex2);
			}
		}
		return date;
	}

	protected static Float parseFloat(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return 0f;
		}
		return (float) jsonObject.getDouble(key);
	}

	protected static Integer parseInteger(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return 0;
		}
		return jsonObject.getInt(key);
	}

	protected static Optional<Boolean> parseOptionalBoolean(
			JSONObject jsonObject, String key) throws JSONException {
		if (jsonObject.isNull(key)) {
			return Optional.absent();
		}
		return Optional.of(parseBoolean(jsonObject, key));
	}

	protected static Optional<Date> parseOptionalDate(JSONObject jsonObject,
			String key) throws JSONException {
		if (jsonObject.isNull(key)) {
			return Optional.absent();
		}
		return Optional.of(parseDate(jsonObject, key));
	}

	protected static Optional<Float> parseOptionalFloat(JSONObject jsonObject,
			String key) throws JSONException {
		if (jsonObject.isNull(key)) {
			return Optional.absent();
		}
		return Optional.of(parseFloat(jsonObject, key));
	}

	protected static Optional<Integer> parseOptionalInteger(
			JSONObject jsonObject, String key) throws JSONException {
		if (jsonObject.isNull(key)) {
			return Optional.absent();
		}
		return Optional.of(parseInteger(jsonObject, key));
	}

	protected static Optional<String> parseOptionalString(
			JSONObject jsonObject, String key) throws JSONException {
		if (jsonObject.isNull(key)) {
			return Optional.absent();
		}
		return Optional.of(parseString(jsonObject, key));
	}

	protected static String parseString(JSONObject jsonObject, String key)
			throws JSONException {
		if (!jsonObject.has(key)) {
			return "";
		}
		return jsonObject.getString(key);
	}

	protected static Object toJSON(Object object) {
		if (object instanceof Optional<?>) {
			Optional<?> optional = (Optional<?>) object;
			if (optional.isPresent()) {
				return toJSON(optional.get());
			} else {
				return JSONObject.NULL;
			}
		} else if (object instanceof BigDecimal) {
			return ((BigDecimal) object).toPlainString();
		} else if (object instanceof Date) {
			return DATE_TIME_FORMATTER.print(((Date) object).getTime());
		}
		return object;
	}

	protected static Object toJSON(List<? extends Model> value,
			Boolean recursive, Integer depth) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (Model model : value) {
			jsonArray.put(model.toJSONObject(recursive, depth));
		}
		return jsonArray;
	}

	protected static Object toJSON(Optional<? extends Model> value,
			Boolean recursive, Integer depth) throws JSONException {
		if (value.isPresent()) {
			return value.get().toJSONObject(recursive, depth);
		} else {
			return JSONObject.NULL;
		}
	}

	protected static BigDecimal wrapNull(BigDecimal value) {
		return Objects.firstNonNull(value, BigDecimal.ZERO);
	}

	protected static Boolean wrapNull(Boolean value) {
		return Objects.firstNonNull(value, false);
	}

	protected static Date wrapNull(Date value) {
		return Objects.firstNonNull(value, new Date());
	}

	protected static Integer wrapNull(Integer value) {
		return Objects.firstNonNull(value, 0);
	}

	protected static <T extends Model> LinkedList<T> wrapNull(Iterable<T> value) {
		return Lists.newLinkedList(Objects.firstNonNull(value, new LinkedList<T>()));
	}

	protected static <T extends Serializable> Optional<T> wrapNull(
			Optional<T> value) {
		return Objects.firstNonNull(value, Optional.<T> absent());
	}

	protected static String wrapNull(String value) {
		return Strings.nullToEmpty(value);
	}

	public JSONObject toJSONObject() throws JSONException {
		return toJSONObject(false, 0);
	}

	public JSONObject toJSONObject(Boolean recursive) throws JSONException {
		return toJSONObject(recursive, 0);
	}

	protected abstract JSONObject toJSONObject(Boolean recursive, Integer depth)
			throws JSONException;

	protected abstract Model cloneByJSON() throws JSONException;

	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException {
		try {
			objectOutputStream.writeObject(toJSONObject(true).toString());
		} catch (JSONException e) {
			throw new IOException(e.toString() + "\n"
					+ ExceptionUtils.getStackTrace(e));
		}
	}

	private void readObject(ObjectInputStream objectInputStream)
			throws IOException, ClassNotFoundException {
		Object object = objectInputStream.readObject();
		if (!(object instanceof String)) {
			return;
		}
		String jsonString = (String) object;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			fill(jsonObject);
		} catch (JSONException e) {
			throw new IOException(e.toString() + "\n"
					+ ExceptionUtils.getStackTrace(e));
		}
	}

	public abstract void fill(JSONObject jsonObject) throws JSONException;
}
