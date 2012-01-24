package com.kogasoftware.odt.invehicledevice.test;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.kogasoftware.odt.invehicledevice.LogTag;

class OperatorApi {
	static class BadStatusCodeException extends RuntimeException {
		private static final long serialVersionUID = -375497023051947223L;
		public final Integer statusCode;
		public final String body;

		public BadStatusCodeException(Integer statusCode, String body) {
			this.statusCode = statusCode;
			this.body = body;
		}
	}

	static class AsyncCallback<ResultType> {
		public void onError(Exception exception) {
		}

		public void onSuccess(ResultType result) {
		}
	}

	private final String T = LogTag.get(OperatorApi.class);
	private final Object threadLock = new Object();
	private Thread thread = new Thread();
	private final String baseUri = "http://10.1.10.161:3004";

	public void join() throws InterruptedException {
		synchronized (threadLock) {
			thread.join();
		}
	}

	void getAuthenticationToken(final String login, final String password,
			final AsyncCallback<String> callback) {
		synchronized (threadLock) {
			thread.interrupt();
			thread = new Thread() {
				@Override
				public void run() {
					try {
						JSONObject postJSON = new JSONObject();
						JSONObject operator = new JSONObject();
						operator.put("login", login);
						operator.put("password", password);
						postJSON.put("operator", operator);

						HttpClient httpClient = new DefaultHttpClient();
						HttpPost request = new HttpPost();
						request.setHeader("Content-type", "application/json");
						StringEntity entity = new StringEntity(
								postJSON.toString());
						// entity.setContentEncoding("UTF-8");
						request.setURI(new URI(baseUri
								+ "/operators/sign_in.json"));
						request.setEntity(entity);
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
						callback.onSuccess(response
								.getString("authentication_token"));
					} catch (Exception e) {
						callback.onError(e);
					}
				}
			};
			thread.start();
		}
	}
}

public class LoginTestCase extends TestCase {
	private final String T = LogTag.get(LoginTestCase.class);

	public void testAuthenticationTokenユーザー名失敗() throws InterruptedException {
		final AtomicBoolean r = new AtomicBoolean(true);
		OperatorApi api = new OperatorApi();
		api.getAuthenticationToken("i_mogi", "8942549",
				new OperatorApi.AsyncCallback<String>() {
					@Override
					public void onError(Exception exception) {
						r.set(false);
					}
				});
		api.join();
		assertFalse(r.get());
	}

	public void testAuthenticationTokenパスワード失敗() throws InterruptedException {
		final AtomicBoolean r = new AtomicBoolean(true);
		OperatorApi api = new OperatorApi();

		api.getAuthenticationToken("i_mogi", "89423952845",
				new OperatorApi.AsyncCallback<String>() {
					@Override
					public void onError(Exception exception) {
						r.set(false);
					}
				});
		api.join();
		assertFalse(r.get());
	}

	public void testAuthenticationToken成功() throws InterruptedException {
		final AtomicBoolean r = new AtomicBoolean(false);
		OperatorApi api = new OperatorApi();
		final StringBuilder token = new StringBuilder();

		api.getAuthenticationToken("i_mogi", "i_mogi",
				new OperatorApi.AsyncCallback<String>() {
					@Override
					public void onError(Exception exception) {
						Log.e(T, "", exception);
						exception.printStackTrace();
					}

					@Override
					public void onSuccess(String asyncToken) {
						r.set(true);
						token.append(asyncToken);
					}
				});
		api.join();
		assertTrue(r.get());
		assertFalse(token.toString().isEmpty());
	}
}
