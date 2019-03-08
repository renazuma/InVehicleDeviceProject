package com.kogasoftware.odt.invehicledevice.mockserver;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.json.VehicleNotificationJson;

public class VehicleNotificationsHandler extends MockServerRequestHandler {
	public static String PATH = "/in_vehicle_devices/vehicle_notifications*";

	public VehicleNotificationsHandler(MockServer server,
			String authenticationToken) {
		super(server, authenticationToken);
	}

	@Override
	protected void handleGet(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		List<VehicleNotificationJson> vns = Lists.newLinkedList();
		for (VehicleNotificationJson vn : server.vehicleNotifications) {
			if (vn.readAt != null && vn.response != null) {
				vns.add(vn);
			}
		}
		response.setEntity(new StringEntity(JSON
				.writeValueAsString(server.vehicleNotifications), "UTF-8"));
		response.setStatusCode(200);
	}

	@Override
	protected void handlePatch(HttpEntityEnclosingRequest request,
			HttpResponse response, HttpContext context, JsonNode node)
			throws JsonProcessingException, IOException {
		Long id = getId(request);
		JsonNode vnNode = node.get("vehicle_notification");
		for (VehicleNotificationJson vn : server.vehicleNotifications) {
			if (!vn.id.equals(id)) {
				continue;
			}
			vn.readAt = ISODateTimeFormat.dateTimeParser().parseDateTime(
					vnNode.path("read_at").asText());
			vn.response = vnNode.path("response").asLong();
			response.setStatusCode(204);
			return;
		}
		response.setStatusCode(404);
	}
}
