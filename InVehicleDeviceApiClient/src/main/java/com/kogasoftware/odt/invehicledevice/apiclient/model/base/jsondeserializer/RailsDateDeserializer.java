package com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsondeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.Model;

public class RailsDateDeserializer extends StdDeserializer<Date> {
	private static final long serialVersionUID = 2088665151150582773L;
	public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
	private final DateFormat dateFormat = new SimpleDateFormat(
			DATE_FORMAT_STRING, Locale.US);

	protected RailsDateDeserializer() {
		super(Date.class);
		dateFormat.setTimeZone(Model.TIME_ZONE);
	}

	@Override
	public Date deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException,
			JsonProcessingException {
		String dateString = jsonParser.getText();
		try {
			synchronized (dateFormat) {
				Date date = dateFormat.parse(dateString);
				return date;
			}
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}
}
