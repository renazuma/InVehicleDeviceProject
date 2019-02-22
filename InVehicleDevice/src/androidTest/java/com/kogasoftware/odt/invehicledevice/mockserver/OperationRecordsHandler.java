package com.kogasoftware.odt.invehicledevice.mockserver;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.json.OperationRecordJson;

public class OperationRecordsHandler extends MockServerRequestHandler {
	public static String PATH = "/in_vehicle_devices/operation_records/*";

	public OperationRecordsHandler(MockServer server, String authenticationToken) {
		super(server, authenticationToken);
	}

	@Override
	protected void handlePatch(HttpEntityEnclosingRequest request,
			HttpResponse response, HttpContext context, JsonNode node)
			throws JsonProcessingException, IOException {
		JsonNode orNode = node.path("operation_record");
		Long id = getId(request);
		for (OperationRecordJson or : server.operationRecords) {
			if (!or.id.equals(id)) {
				continue;
			}

			JsonNode departedAt = orNode.path("departed_at");
			if (departedAt.isTextual()) {
				or.departedAt = ISODateTimeFormat.dateTimeParser()
						.parseDateTime(departedAt.asText());
			} else if (departedAt.isNull()) {
				or.departedAt = null;
			}

			JsonNode arrivedAt = orNode.path("arrived_at");
			if (arrivedAt.isTextual()) {
				or.arrivedAt = ISODateTimeFormat.dateTimeParser()
						.parseDateTime(arrivedAt.asText());
			} else if (departedAt.isNull()) {
				or.arrivedAt = null;
			}

			response.setStatusCode(204);
			return;
		}
		response.setStatusCode(404);
	}
}
