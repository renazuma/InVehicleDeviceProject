package com.kogasoftware.odt.invehicledevice.mockserver;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.databind.JsonNode;

public class SignInHandler extends MockServerRequestHandler {
	public static String PATH = "/in_vehicle_devices/sign_in";

	public SignInHandler(MockServer server, String authenticationToken) {
		super(server, authenticationToken, false);
	}

	@Override
	protected void handlePost(HttpEntityEnclosingRequest request,
			HttpResponse response, HttpContext context, JsonNode node)
			throws IOException {
		JsonNode ivdNode = node.path("in_vehicle_device");
		JsonNode loginNode = ivdNode.path("login");
		JsonNode passwordNode = ivdNode.path("password");
		if (loginNode.isTextual() && loginNode.asText().equals("valid_login")
				&& passwordNode.isTextual()
				&& passwordNode.asText().equals("valid_password")) {
			// OK
			response.setStatusCode(201);
			response.setEntity(new StringEntity("{\"authentication_token\":\""
					+ authenticationToken + "\"}"));
		} else {
			// NG
			response.setStatusCode(401);
		}
	}
}
