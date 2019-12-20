package com.kogasoftware.odt.invehicledevice.mockserver;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import android.net.Uri;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.InVehicleDeviceContentProvider;

public class MockServerRequestHandler implements HttpRequestHandler {
	final String authenticationToken;
	final Boolean authenticationRequired;
	final MockServer server;
	static final ObjectMapper JSON = InVehicleDeviceContentProvider.JSON;

	public MockServerRequestHandler(MockServer server,
			String authenticationToken) {
		this(server, authenticationToken, true);
	}

	public MockServerRequestHandler(MockServer server,
			String authenticationToken, Boolean authenticationRequired) {
		this.server = server;
		this.authenticationToken = authenticationToken;
		this.authenticationRequired = authenticationRequired;
	}

	void handleHttpEntityEnclosingRequest(HttpEntityEnclosingRequest request,
			HttpResponse response, HttpContext context) throws ParseException,
			IOException, HttpException {
		String entityString = EntityUtils.toString(request.getEntity());
		JsonNode node = JSON.readTree(entityString);
		if (authenticationRequired
				&& !authenticationToken.equals(node
						.path("authentication_token").asText())) {
			response.setStatusCode(403);
			return;
		}
		String method = request.getRequestLine().getMethod();
		if (method.equals("PUT")) {
			handlePut(request, response, context, node);
		} else if (method.equals("POST")) {
			handlePost(request, response, context, node);
		} else if (method.equals("PATCH")) {
			handlePatch(request, response, context, node);
		} else {
			response.setStatusCode(400);
		}
	}

	void handleHttpRequest(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		if (authenticationRequired
				&& !authenticationToken.equals(Uri.parse(
						request.getRequestLine().getUri()).getQueryParameter(
						"authentication_token"))) {
			response.setStatusCode(403);
			return;
		}
		String method = request.getRequestLine().getMethod();
		if (method.equals("GET")) {
			handleGet(request, response, context);
		} else {
			response.setStatusCode(400);
		}
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		if (request instanceof HttpEntityEnclosingRequest) {
			handleHttpEntityEnclosingRequest(
					(HttpEntityEnclosingRequest) request, response, context);
		} else {
			handleHttpRequest(request, response, context);
		}
	}

	protected void handlePost(HttpEntityEnclosingRequest request,
			HttpResponse response, HttpContext context, JsonNode node)
			throws HttpException, IOException {
		response.setStatusCode(400);
	}

	protected void handlePut(HttpEntityEnclosingRequest request,
			HttpResponse response, HttpContext context, JsonNode node)
			throws HttpException, IOException {
		response.setStatusCode(400);
	}

	protected void handleGet(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		response.setStatusCode(400);
	}

	protected void handlePatch(HttpEntityEnclosingRequest request,
			HttpResponse response, HttpContext context, JsonNode node)
			throws HttpException, IOException {
		response.setStatusCode(400);
	}

	protected Long getId(HttpRequest request) {
		Iterable<String> paths = Splitter.on("/").split(
				request.getRequestLine().getUri());
		return Long.parseLong(Iterables.getLast(paths));
	}
}
