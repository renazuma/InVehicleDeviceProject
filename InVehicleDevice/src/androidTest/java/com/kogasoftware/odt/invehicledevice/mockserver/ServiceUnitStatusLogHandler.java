package com.kogasoftware.odt.invehicledevice.mockserver;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.databind.JsonNode;

public class ServiceUnitStatusLogHandler extends MockServerRequestHandler {
	public static String PATH = "/in_vehicle_devices/service_unit_status_logs";

	public ServiceUnitStatusLogHandler(MockServer server,
			String authenticationToken) {
		super(server, authenticationToken);
	}

	@Override
	protected void handlePost(HttpEntityEnclosingRequest request,
			HttpResponse response, HttpContext context, JsonNode node)
			throws HttpException, IOException {
		node = node.path("service_unit_status_log");
		if (node.isObject()) {
			try {
				ServiceUnitStatusLogJson suslj = JSON.convertValue(node,
						ServiceUnitStatusLogJson.class);
				server.serviceUnitStatusLogs.add(suslj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.setStatusCode(201);
		} else {
			response.setStatusCode(422);
		}
	}
}
