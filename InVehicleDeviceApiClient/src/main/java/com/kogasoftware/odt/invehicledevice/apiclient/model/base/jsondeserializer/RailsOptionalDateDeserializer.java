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
import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.Model;

public class RailsOptionalDateDeserializer extends
		StdDeserializer<Optional<Date>> {
	private static final long serialVersionUID = 2088665151150582773L;
	private final DateFormat dateFormat = new SimpleDateFormat(
			RailsDateDeserializer.DATE_FORMAT_STRING, Locale.US);

	protected RailsOptionalDateDeserializer() {
		super(Optional.class);
		dateFormat.setTimeZone(Model.TIME_ZONE);
	}

	@Override
	public Optional<Date> getNullValue() {
		return Optional.absent();
	}

	@Override
	public Optional<Date> deserialize(JsonParser jsonParser,
			DeserializationContext deserializationContext) throws IOException,
			JsonProcessingException {
		String dateString = jsonParser.getText();
		try {
			synchronized (dateFormat) {
				return Optional.of(dateFormat.parse(dateString));
			}
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}
}
