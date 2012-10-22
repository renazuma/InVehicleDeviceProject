package com.kogasoftware.odt.apiclient;

import java.io.Closeable;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.base.Charsets;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializableDeleteLoader;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializableGetLoader;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializablePostLoader;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializablePutLoader;

public class ApiClient implements Closeable {
	public interface ResponseConverter<T> {
		T convert(byte[] rawResponse) throws Exception;
	}

	protected class SessionRunner implements Runnable {
		@Override
		public void run() {
			try {
				runSession();
			} catch (InterruptedException e) {
			}
		}
	}

	private static final String TAG = ApiClient.class.getSimpleName();

	public static final Integer REQUEST_EXPIRE_DAYS = 3;
	public static final ResponseConverter<Void> VOID_RESPONSE_CONVERTER = new ResponseConverter<Void>() {
		@Override
		public Void convert(byte[] rawResponse) throws Exception {
			return null;
		}
	};

	protected static final int NUM_THREADS = 5;
	protected static final String UNIQUE_GROUP = ApiClientRequestQueue.UNIQUE_GROUP;

	protected static String decodeByteArray(byte[] byteArray) {
		return Charsets.ISO_8859_1.decode(ByteBuffer.wrap(byteArray))
				.toString();
	}

	protected final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	protected final ApiClientRequestQueue requests;
	protected volatile String serverHost = "http://127.0.0.1"; // 複数スレッドから参照の書きかえがありうるためvolatile
	protected volatile String authenticationToken = ""; // 複数スレッドから参照の書きかえがありうるためvolatile
	protected final Object requestConfigsLock = new Object();
	protected final WeakHashMap<Thread, ApiClientRequestConfig> requestConfigs = new WeakHashMap<Thread, ApiClientRequestConfig>();

	protected <T> int handleJSONException(JSONException e,
			ApiClientCallback<T> callback) {
		int reqkey = -1;
		callback.onException(reqkey, new ApiClientException(e));
		return reqkey;
	}

	public ApiClient(String serverHost) {
		this(serverHost, "");
	}

	public ApiClient(String serverHost, String authenticationToken) {
		this(serverHost, authenticationToken, new ApiClientRequestQueue());
	}

	public ApiClient(String serverHost, String authenticationToken, File backupFile) {
		this(serverHost, authenticationToken,
				new ApiClientRequestQueue(backupFile));
	}

	protected ApiClient(String serverHost, String authenticationToken,
			ApiClientRequestQueue requests) {
		this.requests = requests;
		this.authenticationToken = authenticationToken;
		setServerHost(serverHost);
		for (int i = 0; i < NUM_THREADS; ++i) {
			executorService.scheduleWithFixedDelay(new SessionRunner(),
					0, 10, TimeUnit.SECONDS);
		}
	}

	@Override
	public void close() {
		executorService.shutdownNow();
	}

	protected <T> int delete(String path, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv) {
		SerializableDeleteLoader loader = new SerializableDeleteLoader(
				getServerHost(), path, authenticationToken);
		ApiClientRequest<?> request = new ApiClientRequest<T>(callback, conv, loader);
		addRequest(request);
		return request.getReqKey();
	}

	protected boolean runHttpSessionAndCallback(ApiClientRequest<?> request) {
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient
					.execute(request.getRequest());
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			byte[] response = new byte[] {};
			String responseString = "";
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				response = EntityUtils.toByteArray(entity);
				if (response.length < 1024) {
					responseString = decodeByteArray(response);
				} else {
					responseString = "response length=" + response.length;
				}
				Log.d(TAG, "response body:" + responseString);
			}

			if (statusCode / 100 == 4 || statusCode / 100 == 5) {
				request.onFailed(statusCode, responseString);
				return false;
			}

			request.onSucceed(statusCode, response);
			return true;
		} catch (ApiClientException e) {
			request.onException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			request.onException(new ApiClientException(e));
		} catch (Exception e) {
			request.onException(new ApiClientException(e));
		}
		return false;
	}

	protected void runSession() throws InterruptedException {
		ApiClientRequest<?> request = requests.take();
		boolean succeed = false;
		Date now = new Date();
		if (DateUtils.addDays(request.getCreatedDate(), REQUEST_EXPIRE_DAYS)
				.before(now)) {
			Log.i(TAG, "Request (" + request + ") is expired. createdDate: "
					+ request.getCreatedDate());
			requests.remove(request);
			return;
		}

		succeed = runHttpSessionAndCallback(request);
		if (succeed || !request.getConfig().getRetry()) {
			requests.remove(request);
		} else {
			requests.retry(request);
		}
	}

	protected JSONObject filterJSONKeys(JSONObject jsonObject, String[] keys) {
		JSONObject res = new JSONObject();

		for (String key : keys) {
			try {
				res.put(key, jsonObject.get(key));
			} catch (JSONException e) {
				Log.w(TAG, e);
			}
		}

		return res;
	}

	protected JSONObject removeJSONKeys(JSONObject jsonObject, String[] keys) {
		JSONObject res = new JSONObject();

		Iterator<?> it = jsonObject.keys();
		while (it.hasNext()) {
			String key = (String) it.next();
			try {
				res.put(key, jsonObject.get(key));
			} catch (JSONException e) {
				Log.w(TAG, e);
			}
		}

		for (String key : keys) {
			res.remove(key);
		}

		return res;
	}

	protected <T> int get(String path, Map<String, String> params,
			String requestGroup, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv) {
		SerializableGetLoader loader = new SerializableGetLoader(
				getServerHost(), path, params, authenticationToken);
		ApiClientRequest<?> request = new ApiClientRequest<T>(callback, conv, loader);
		addRequest(request, requestGroup);
		return request.getReqKey();
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	protected String getServerHost() {
		return serverHost;
	}

	public static JSONArray parseJSONArray(byte[] rawResponse)
			throws JSONException {
		String json = decodeByteArray(rawResponse);
		Log.d(TAG + "#parseJSONArray", json);
		return new JSONArray(json);
	}

	public static JSONObject parseJSONObject(byte[] rawResponse)
			throws JSONException {
		String json = decodeByteArray(rawResponse);
		Log.d(TAG + "#parseJSONObject", json);
		return new JSONObject(json);
	}

	protected <T> int post(String path, JSONObject param,
			JSONObject retryParam, String requestGroup,
			ApiClientCallback<T> callback, ResponseConverter<? extends T> conv) {
		SerializablePostLoader first = new SerializablePostLoader(
				getServerHost(), path, param, authenticationToken);
		SerializablePostLoader retry = new SerializablePostLoader(
				getServerHost(), path, retryParam, authenticationToken);
		ApiClientRequest<?> request = new ApiClientRequest<T>(callback, conv, first,
				retry);
		addRequest(request, requestGroup);
		return request.getReqKey();
	}

	protected <T> int post(String path, JSONObject param, String requestGroup,
			ApiClientCallback<T> callback, ResponseConverter<? extends T> conv) {
		return post(path, param, param, requestGroup, callback, conv);
	}

	protected <T> int put(String path, JSONObject param, String requestGroup,
			ApiClientCallback<T> callback, ResponseConverter<? extends T> conv) {
		return put(path, param, param, requestGroup, callback, conv);
	}

	protected <T> int put(String path, JSONObject param, JSONObject retryParam,
			String requestGroup, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv) {
		SerializablePutLoader first = new SerializablePutLoader(
				getServerHost(), path, param, authenticationToken);
		SerializablePutLoader retry = new SerializablePutLoader(
				getServerHost(), path, retryParam, authenticationToken);
		ApiClientRequest<?> request = new ApiClientRequest<T>(callback, conv, first,
				retry);
		addRequest(request, requestGroup);
		return request.getReqKey();
	}

	public void abort(int reqkey) {
		requests.abort(reqkey);
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	protected String getPassengerRecordGetOnOrOffGroup(
			Integer operationScheduleId, Integer reservationId, Integer userId) {
		return "PassengerRecordGetOnOrOffGroup/operationScheduleId="
				+ operationScheduleId + "/reservationId=" + reservationId
				+ "/userId=" + userId;
	}

	protected ApiClientRequestConfig getRequestConfig() {
		Thread key = Thread.currentThread();
		synchronized (requestConfigsLock) {
			if (requestConfigs.containsKey(key)) {
				return requestConfigs.get(key);
			} else {
				ApiClientRequestConfig requestConfig = new ApiClientRequestConfig();
				requestConfigs.put(key, requestConfig);
				return requestConfig;
			}
		}
	}

	protected void clearRequestConfig() {
		synchronized (requestConfigsLock) {
			requestConfigs.remove(Thread.currentThread());
		}
	}

	protected void addRequest(ApiClientRequest<?> request) {
		addRequest(request, UNIQUE_GROUP);
	}

	protected void addRequest(ApiClientRequest<?> request, String requestGroup) {
		ApiClientRequestConfig requestConfig = getRequestConfig();
		clearRequestConfig();
		request.setConfig(requestConfig);
		requests.add(request, requestGroup);
	}

	/**
	 * 同じスレッドで次に実行するAPIの通信を、WebAPIクローズ時にリクエストをファイルに保存し、次回のWebAPIのコンストラクタで復活させ、
	 * 成功するか期限が過ぎるまで通信を行うようにする。ただし、復活後のリクエストは通信時にコールバックを行わない。
	 * 
	 * @param reqkey
	 */
	protected static <T extends ApiClient> T withSaveOnClose(T apiClient, boolean saveOnClose) {
		apiClient.getRequestConfig().setSaveOnClose(saveOnClose);
		return apiClient;
	}
	
	/**
	 * 同じスレッドで次に実行するAPIの通信が、リトライするかを設定する。
	 * 
	 * @param reqkey
	 */
	protected static <T extends ApiClient> T withRetry(T apiClient, boolean retry) {
		apiClient.getRequestConfig().setRetry(retry);
		return apiClient;
	}
}
