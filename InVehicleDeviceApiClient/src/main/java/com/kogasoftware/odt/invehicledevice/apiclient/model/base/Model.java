package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.apiclient.ApiClients;
import com.kogasoftware.odt.apiclient.identifiable.Identifiable;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.externalizable.ExternalizableInput;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.externalizable.ExternalizableOutput;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsonview.AssociationView;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsonview.DefaultView;

public abstract class Model implements Externalizable, Identifiable, Cloneable {
	private static final String TAG = Model.class.getSimpleName();
	private static final long serialVersionUID = -5513333240346057624L;
	private static final ObjectMapper OBJECT_MAPPER;
	private static final ObjectWriter OBJECT_WRITER;
	private static final ObjectWriter NO_ASSOCIATION_OBJECT_WRITER;
	protected static final Date DEFAULT_DATE;
	protected static final Date DEFAULT_DATE_TIME;
	public static final TimeZone TIME_ZONE = TimeZone.getDefault();
	public static final String JACKSON_IDENTITY_INFO_PROPERTY = "@jackson_identitiy_info";

	public static class CloneException extends RuntimeException {
		private static final long serialVersionUID = 2637422692046440762L;

		public CloneException(Throwable cause) {
			super(cause);
		}
	}

	static {
		Calendar defaultDateCalendar = Calendar.getInstance(TIME_ZONE);
		defaultDateCalendar.clear();
		defaultDateCalendar.set(1800, 1, 1);
		DEFAULT_DATE = defaultDateCalendar.getTime();
		DEFAULT_DATE_TIME = DEFAULT_DATE;

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new GuavaModule());
		objectMapper.disable(MapperFeature.AUTO_DETECT_GETTERS);
		objectMapper.disable(MapperFeature.AUTO_DETECT_IS_GETTERS);
		objectMapper.disable(MapperFeature.AUTO_DETECT_SETTERS);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
				true);
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper
				.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		OBJECT_WRITER = objectMapper.writer().withView(DefaultView.class)
				.withView(AssociationView.class);
		NO_ASSOCIATION_OBJECT_WRITER = objectMapper.writer().withView(
				DefaultView.class);
		OBJECT_MAPPER = objectMapper;
	}

	public static ObjectMapper getObjectMapper() {
		return OBJECT_MAPPER;
	}

	public static ObjectWriter getObjectWriter(Boolean withAssociation) {
		return withAssociation ? OBJECT_WRITER : NO_ASSOCIATION_OBJECT_WRITER;
	}

	protected static void errorIfNull(Object value) {
		if (value != null) {
			return;
		}
		NullPointerException e = new NullPointerException();
		Log.e(TAG, "unexpected null", e);
		// throw e;
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

	protected static String wrapNull(String value) {
		return Objects.firstNonNull(value, "");
	}

	protected static <T extends Model> LinkedList<T> wrapNull(Iterable<T> value) {
		return Lists.newLinkedList(Objects.firstNonNull(value,
				new LinkedList<T>()));
	}

	protected static <T extends Serializable> Optional<T> wrapNull(
			Optional<T> value) {
		return Objects.firstNonNull(value, Optional.<T> absent());
	}

	protected static <T> ResponseConverter<List<T>> getListResponseConverter(
			final Class<T> responseClass) {
		return new ResponseConverter<List<T>>() {
			@Override
			public List<T> convert(byte[] rawResponse) throws Exception {
				return parseList(ApiClients.decodeByteArray(rawResponse),
						responseClass);
			}
		};
	}

	protected static <T> ResponseConverter<T> getResponseConverter(
			final Class<T> responseClass) {
		return new ResponseConverter<T>() {
			@Override
			public T convert(byte[] rawResponse) throws Exception {
				return parse(ApiClients.decodeByteArray(rawResponse),
						responseClass);
			}
		};
	}

	protected static <T> T parse(String jsonString, Class<T> modelClass)
			throws IOException {
		return getObjectMapper().readValue(jsonString, modelClass);
	}

	protected static <T> List<T> parseList(String jsonString,
			Class<T> modelClass) throws IOException {
		List<T> list = Lists.newLinkedList();
		ArrayNode arrayNode = getObjectMapper().readValue(jsonString,
				ArrayNode.class);
		for (JsonNode jsonNode : arrayNode) {
			if (jsonNode.isObject()) {
				list.add(getObjectMapper().readValue(jsonNode.toString(),
						modelClass));
			} else {
				Log.e(TAG, "`" + jsonNode + "' is not ObjectNode");
			}
		}
		return list;
	}

	@Override
	public abstract Model clone();

	public abstract Model clone(Boolean withAssociation);

	protected <T extends Model> T clone(Class<T> childClass) {
		return clone(childClass, true);
	}

	protected ByteArrayInputStream toByteArrayInputStream(Boolean withAssociation) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			writeExternal(objectOutputStream, withAssociation);
		} catch (IOException e) {
			throw new CloneException(e);
		} finally {
			Closeables.closeQuietly(objectOutputStream);
			Closeables.closeQuietly(byteArrayOutputStream);
		}
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream = null; // enable GC
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
		byteArray = null; // enable GC
		return byteArrayInputStream;
	}

	protected <T extends Model> T clone(Class<T> childClass,
			Boolean withAssociation) {
		ByteArrayInputStream byteArrayInputStream = toByteArrayInputStream(withAssociation);

		// 空のclone結果のオブジェクトを準備
		T model = null;
		try {
			model = childClass.cast(super.clone());
		} catch (ClassCastException e) {
			throw new CloneException(e);
		} catch (CloneNotSupportedException e) {
			throw new CloneException(e);
		}

		// clone結果のメンバを書き込み
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			model.readExternal(objectInputStream);
			return model;
		} catch (IOException e) {
			throw new CloneException(e);
		} catch (ClassNotFoundException e) {
			throw new CloneException(e);
		} finally {
			Closeables.closeQuietly(objectInputStream);
			Closeables.closeQuietly(byteArrayInputStream);
		}
	}

	public ObjectNode toJsonNode(Boolean withAssociation) throws IOException {
		String jsonString = getObjectWriter(withAssociation)
				.writeValueAsString(this);
		return getObjectMapper().readValue(jsonString, ObjectNode.class);
	}

	public ObjectNode toJsonNode() throws IOException {
		return toJsonNode(true).remove(
				Lists.newArrayList(JACKSON_IDENTITY_INFO_PROPERTY));
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException,
			ClassNotFoundException {
		getObjectMapper().readerForUpdating(this).readValue(
				new ExternalizableInput(objectInput));
	}

	public void writeExternal(ObjectOutput objectOutput, Boolean withAssociation)
			throws IOException {
		getObjectWriter(withAssociation).writeValue(
				new ExternalizableOutput(objectOutput), this);
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		writeExternal(objectOutput, true);
	}
}
