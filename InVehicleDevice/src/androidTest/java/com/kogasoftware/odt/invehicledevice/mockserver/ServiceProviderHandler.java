package com.kogasoftware.odt.invehicledevice.mockserver;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

public class ServiceProviderHandler extends MockServerRequestHandler {
	public static String PATH = "/in_vehicle_devices/service_provider";

	public ServiceProviderHandler(MockServer server, String authenticationToken) {
		super(server, authenticationToken);
	}

	@Override
	protected void handleGet(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		if (server.serviceProviders.isEmpty()) {
			response.setStatusCode(404);
			return;
		}
		response.setEntity(new StringEntity(JSON
				.writeValueAsString(server.serviceProviders.get(0)), "UTF-8"));
		response.setStatusCode(200);
	}
}
