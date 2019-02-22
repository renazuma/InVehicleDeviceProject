package com.kogasoftware.odt.invehicledevice.mockserver;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.PassengerRecordJson;

public class PassengerRecordsHandler extends MockServerRequestHandler {
	public static String PATH = "/in_vehicle_devices/passenger_records*";

	public PassengerRecordsHandler(MockServer server, String authenticationToken) {
		super(server, authenticationToken);
	}

	@Override
	protected void handlePatch(HttpEntityEnclosingRequest request,
			HttpResponse response, HttpContext context, JsonNode node)
			throws JsonProcessingException, IOException {
		JsonNode prNode = node.path("passenger_record");
		Long id = getId(request);
		for (PassengerRecordJson pr : server.passengerRecords) {
			if (!pr.id.equals(id)) {
				continue;
			}

			JsonNode getOnTime = prNode.path("get_on_time");
			if (getOnTime.isTextual()) {
				pr.getOnTime = ISODateTimeFormat.dateTimeParser()
						.parseDateTime(getOnTime.asText());
			} else {
				pr.getOnTime = null;
			}

			JsonNode getOffTime = prNode.path("get_off_time");
			if (getOffTime.isTextual()) {
				pr.getOffTime = ISODateTimeFormat.dateTimeParser()
						.parseDateTime(getOffTime.asText());
			} else {
				pr.getOffTime = null;
			}

			response.setStatusCode(204);
			return;
		}
		response.setStatusCode(404);
	}
}
