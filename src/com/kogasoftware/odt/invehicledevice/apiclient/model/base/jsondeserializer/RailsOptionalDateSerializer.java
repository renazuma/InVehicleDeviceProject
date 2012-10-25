package com.kogasoftware.odt.invehicledevice.apiclient.model.base.jsondeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.base.Optional;

public class RailsOptionalDateSerializer extends StdSerializer<Optional<Date>> {
	private final DateFormat dateFormat = new SimpleDateFormat(
			RailsDateDeserializer.DATE_FORMAT_STRING);

	protected RailsOptionalDateSerializer() {
		super(Optional.class, false);
		dateFormat.setTimeZone(RailsDateDeserializer.TIME_ZONE);
	}

	@Override
	public void serialize(Optional<Date> optionalDate,
			JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
			throws IOException, JsonGenerationException {
		if (optionalDate.isPresent()) {
			String dateString;
			synchronized (dateFormat) {
				dateString = dateFormat.format(optionalDate.get());
			}
			jsonGenerator.writeString(dateString);
		} else {
			serializerProvider.defaultSerializeNull(jsonGenerator);
		}
	}
}
