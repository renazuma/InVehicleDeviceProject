package com.kogasoftware.odt.invehicledevice;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

public class OperatorApi {
	abstract class JSONRequest<ResultType> {

		abstract public HttpRequestBase createHttpRequest() throws Exception;

		abstract public ResultType parseResponse(JSONObject response)
				throws Exception;
	}

	abstract class JSONGETRequest<ResultType> extends JSONRequest<ResultType> {
		final protected String uri;

		public JSONGETRequest(String uri) {
			this.uri = uri;
		}

		@Override
		public HttpRequestBase createHttpRequest() throws Exception {
			HttpGet request = new HttpGet();
			String fullUri = baseUri + "/" + uri;
			if (!authenticationToken.isEmpty()) {
				fullUri += "?authentication_token=" + authenticationToken;
			}
			request.setURI(new URI(fullUri));
			return request;
		}
	}

	abstract class JSONPOSTRequest<ResultType> extends JSONRequest<ResultType> {
		protected final String controller;
		protected final String action;

		public JSONPOSTRequest(String controller, String action) {
			this.controller = controller;
			this.action = action;
		}

		abstract protected JSONObject createRequest() throws Exception;

		@Override
		public HttpRequestBase createHttpRequest() throws Exception {
			HttpPost request = new HttpPost();
			JSONObject requestJSON = createRequest();
			if (!authenticationToken.isEmpty()) {
				requestJSON.put("authentication_token", authenticationToken);
			}
			request.setURI(new URI(baseUri + "/" + controller + "/" + action
					+ ".json"));
			StringEntity entity = new StringEntity(requestJSON.toString(),
					"UTF-8");

			request.setEntity(entity);
			return request;
		}
	}

	public static class BadStatusCodeException extends RuntimeException {
		private static final long serialVersionUID = -375497023051947223L;
		public final Integer statusCode;
		public final String body;

		public BadStatusCodeException(Integer statusCode, String body) {
			this.statusCode = statusCode;
			this.body = body;
		}
	}

	public static class AsyncCallback<ResultType> {
		public void onError(Exception exception) {
		}

		public void onSuccess(ResultType result) {
		}
	}

	private final String authenticationToken;
	private final String T = LogTag.get(OperatorApi.class);
	private final Object threadLock = new Object();
	private final String baseUri = "http://10.1.10.161";
	private Thread thread = new EmptyThread();

	public OperatorApi() {
		this("");
	}

	public OperatorApi(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	public void join() throws InterruptedException {
		synchronized (threadLock) {
			thread.join();
		}
	}

	public void getAuthenticationToken(final String login,
			final String password, final AsyncCallback<String> callback) {

		startJSONRequest(new JSONPOSTRequest<String>("operators", "sign_in") {
			@Override
			protected JSONObject createRequest() throws Exception {
				JSONObject postJSON = new JSONObject();
				JSONObject operator = new JSONObject();
				operator.put("login", login);
				operator.put("password", password);
				postJSON.put("operator", operator);
				return postJSON;
			}

			@Override
			public String parseResponse(JSONObject response) throws Exception {
				return response.getString("authentication_token");
			}
		}, callback);
	}

	public void getInVehicleDevice(Integer id,
			AsyncCallback<JSONObject> callback) {
		startJSONRequest(new JSONGETRequest<JSONObject>("in_vehicle_devices/"
				+ id + ".json") {
			@Override
			public JSONObject parseResponse(JSONObject response)
					throws Exception {
				return response;
			}
		}, callback);
	}

	private <ResultType> void startJSONRequest(
			final JSONRequest<ResultType> jsonRequest,
			final AsyncCallback<ResultType> callback) {
		synchronized (threadLock) {
			thread.interrupt();
			thread = new Thread() {
				@Override
				public void run() {
					try {
						HttpRequestBase request = jsonRequest
								.createHttpRequest();
						request.setHeader("Content-type", "application/json");
						HttpClient httpClient = new DefaultHttpClient();
						HttpResponse httpResponse = httpClient.execute(request);
						int statusCode = httpResponse.getStatusLine()
								.getStatusCode();
						Log.d(T, "Status:" + statusCode);
						String responseString = new String(
								ByteStreams.toByteArray(httpResponse
										.getEntity().getContent()),
								Charsets.UTF_8);
						JSONObject response = new JSONObject(responseString);
						if (statusCode / 100 != 2) {
							throw new BadStatusCodeException(statusCode,
									responseString);
						}

						ResultType result = jsonRequest.parseResponse(response);
						callback.onSuccess(result);
					} catch (Exception e) {
						callback.onError(e);
					}
				}
			};
			thread.start();
		}
	}
}
