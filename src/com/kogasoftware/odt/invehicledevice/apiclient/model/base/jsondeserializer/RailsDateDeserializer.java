package com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsondeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class RailsDateDeserializer extends StdDeserializer<Date> {
	private static final long serialVersionUID = 2088665151150582773L;
	public static final TimeZone TIME_ZONE = TimeZone.getDefault();
	public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
	private final DateFormat dateFormat = new SimpleDateFormat(
			DATE_FORMAT_STRING);

	public static IOException newIOException(ParseException e) {
		return new IOException(e.toString() + SystemUtils.LINE_SEPARATOR
				+ ExceptionUtils.getStackTrace(e));
	}

	protected RailsDateDeserializer() {
		super(Date.class);
		dateFormat.setTimeZone(TIME_ZONE);
	}

	@Override
	public Date deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException,
			JsonProcessingException {
		String dateString = jsonParser.getText();
		try {
			synchronized (dateFormat) {
				return dateFormat.parse(dateString);
			}
		} catch (ParseException e) {
			throw newIOException(e);
		}
	}
}
