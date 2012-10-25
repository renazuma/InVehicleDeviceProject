package com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsondeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class RailsDateSerializer extends StdSerializer<Date> {
	private final DateFormat dateFormat = new SimpleDateFormat(
			RailsDateDeserializer.DATE_FORMAT_STRING);

	protected RailsDateSerializer() {
		super(Date.class);
		dateFormat.setTimeZone(RailsDateDeserializer.TIME_ZONE);
	}

	@Override
	public void serialize(Date date, JsonGenerator jsonGenerator,
			SerializerProvider serializerProvider) throws IOException,
			JsonGenerationException {
		String dateString;
		synchronized (dateFormat) {
			dateString = dateFormat.format(date);
		}
		jsonGenerator.writeString(dateString);
	}
}
