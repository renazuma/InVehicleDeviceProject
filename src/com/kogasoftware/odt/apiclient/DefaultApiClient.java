package com.kogasoftware.odt.apiclient;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializableDeleteLoader;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializableGetLoader;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializablePostLoader;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializablePutLoader;

public class DefaultApiClient implements ApiClient {
	protected class SessionRunner implements Runnable {
		@Override
		public void run() {
			try {
				runSession();
			} catch (InterruptedException e) {
			}
		}
	}

	static final String TAG = DefaultApiClient.class.getSimpleName();

	public static final Integer REQUEST_EXPIRE_DAYS = 3;
	public static final ResponseConverter<Void> VOID_RESPONSE_CONVERTER = new ResponseConverter<Void>() {
		@Override
		public Void convert(byte[] rawResponse) throws Exception {
			return null;
		}
	};

	protected static final int NUM_THREADS = 5;
	protected static final String UNIQUE_GROUP = DefaultApiClientRequestQueue.UNIQUE_GROUP;

	protected final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	protected final DefaultApiClientRequestQueue requests;
	protected volatile String serverHost = "http://127.0.0.1"; // 複数スレッドから参照の書きかえがありうるためvolatile
	protected volatile String authenticationToken = ""; // 複数スレッドから参照の書きかえがありうるためvolatile
	protected final Object requestConfigsLock = new Object();
	protected final WeakHashMap<Thread, DefaultApiClientRequestConfig> requestConfigs = new WeakHashMap<Thread, DefaultApiClientRequestConfig>();

	protected <T> int handleIOException(IOException e,
			ApiClientCallback<T> callback) {
		int reqkey = -1;
		Log.e(TAG, "fatal IOException", e);
		callback.onException(reqkey, new ApiClientException(e));
		return reqkey;
	}

	public DefaultApiClient(String serverHost) {
		this(serverHost, "");
	}

	public DefaultApiClient(String serverHost, String authenticationToken) {
		this(serverHost, authenticationToken,
				new DefaultApiClientRequestQueue());
	}

	public DefaultApiClient(String serverHost, String authenticationToken,
			File backupFile) {
		this(serverHost, authenticationToken, new DefaultApiClientRequestQueue(
				backupFile));
	}

	protected DefaultApiClient(String serverHost, String authenticationToken,
			DefaultApiClientRequestQueue requests) {
		this.requests = requests;
		this.authenticationToken = authenticationToken;
		setServerHost(serverHost);
		for (int i = 0; i < NUM_THREADS; ++i) {
			executorService.scheduleWithFixedDelay(new SessionRunner(), 0, 10,
					TimeUnit.SECONDS);
		}
	}

	@Override
	public void close() {
		executorService.shutdownNow();
	}

	@Override
	public <T> int delete(String path, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv) {
		SerializableDeleteLoader loader = new SerializableDeleteLoader(
				getServerHost(), path, authenticationToken);
		DefaultApiClientRequest<?> request = new DefaultApiClientRequest<T>(
				callback, conv, loader);
		addRequest(request);
		return request.getReqKey();
	}

	protected boolean runHttpSessionAndCallback(
			DefaultApiClientRequest<?> request) {
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
				responseString = ApiClients.decodeByteArray(response);
				Log.d(TAG,
						"response body:"
								+ responseString.substring(0, Math.min(
										responseString.length(), 512 * 1024)));
			}

			if (statusCode / 100 == 4 || statusCode / 100 == 5) {
				Log.w(TAG, "request failed statusCode=" + statusCode);
				request.onFailed(statusCode, responseString);
				return false;
			}

			Log.i(TAG, "request succeed statusCode=" + statusCode);
			request.onSucceed(statusCode, response);
			return true;
		} catch (ApiClientException e) {
			Log.w(TAG, e);
			request.onException(e);
		} catch (ClientProtocolException e) {
			Log.w(TAG, e);
			request.onException(new ApiClientException(e));
		} catch (IOException e) {
			Log.i(TAG, "request failed by exception", e);
			request.onException(new ApiClientException(e));
		} catch (InterruptedException e) {
			Log.i(TAG, "request interrupted", e);
			Thread.currentThread().interrupt();
			request.onException(new ApiClientException(e));
		} catch (Exception e) {
			Log.e(TAG, "unexpected exception", e);
			request.onException(new ApiClientException(e));
		}
		return false;
	}

	protected void runSession() throws InterruptedException {
		DefaultApiClientRequest<?> request = requests.take();
		Log.i(TAG, "runSession " + request);

		Date now = new Date();
		if (DateUtils.addDays(request.getCreatedDate(), REQUEST_EXPIRE_DAYS)
				.before(now)) {
			requests.remove(request);
			Log.i(TAG, "request " + request + " is expired");
			return;
		}

		boolean succeed = runHttpSessionAndCallback(request);
		if (succeed || !request.getConfig().getRetry()) {
			requests.remove(request);
		} else {
			requests.retry(request);
		}
	}

	@Override
	public <T> int get(String path, Map<String, String> params,
			String requestGroup, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv) {
		SerializableGetLoader loader = new SerializableGetLoader(
				getServerHost(), path, params, authenticationToken);
		DefaultApiClientRequest<?> request = new DefaultApiClientRequest<T>(
				callback, conv, loader);
		addRequest(request, requestGroup);
		return request.getReqKey();
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	protected String getServerHost() {
		return serverHost;
	}

	@Override
	public <T> int post(String path, JsonNode param, JsonNode retryParam,
			String requestGroup, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv) {
		SerializablePostLoader first = new SerializablePostLoader(
				getServerHost(), path, param, authenticationToken);
		SerializablePostLoader retry = new SerializablePostLoader(
				getServerHost(), path, retryParam, authenticationToken);
		DefaultApiClientRequest<?> request = new DefaultApiClientRequest<T>(
				callback, conv, first, retry);
		addRequest(request, requestGroup);
		return request.getReqKey();
	}

	@Override
	public <T> int post(String path, JsonNode param, String requestGroup,
			ApiClientCallback<T> callback, ResponseConverter<? extends T> conv) {
		return post(path, param, param, requestGroup, callback, conv);
	}

	@Override
	public <T> int put(String path, JsonNode param, String requestGroup,
			ApiClientCallback<T> callback, ResponseConverter<? extends T> conv) {
		return put(path, param, param, requestGroup, callback, conv);
	}

	@Override
	public <T> int put(String path, JsonNode param, JsonNode retryParam,
			String requestGroup, ApiClientCallback<T> callback,
			ResponseConverter<? extends T> conv) {
		SerializablePutLoader first = new SerializablePutLoader(
				getServerHost(), path, param, authenticationToken);
		SerializablePutLoader retry = new SerializablePutLoader(
				getServerHost(), path, retryParam, authenticationToken);
		DefaultApiClientRequest<?> request = new DefaultApiClientRequest<T>(
				callback, conv, first, retry);
		addRequest(request, requestGroup);
		return request.getReqKey();
	}

	@Override
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

	protected DefaultApiClientRequestConfig getRequestConfig() {
		Thread key = Thread.currentThread();
		synchronized (requestConfigsLock) {
			if (requestConfigs.containsKey(key)) {
				return requestConfigs.get(key);
			} else {
				DefaultApiClientRequestConfig requestConfig = new DefaultApiClientRequestConfig();
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

	protected void addRequest(DefaultApiClientRequest<?> request) {
		addRequest(request, UNIQUE_GROUP);
	}

	protected void addRequest(DefaultApiClientRequest<?> request,
			String requestGroup) {
		DefaultApiClientRequestConfig requestConfig = getRequestConfig();
		clearRequestConfig();
		request.setConfig(requestConfig);
		requests.add(request, requestGroup);
	}

	protected static <T extends DefaultApiClient> T withSaveOnClose(
			T apiClient, boolean saveOnClose) {
		apiClient.getRequestConfig().setSaveOnClose(saveOnClose);
		return apiClient;
	}

	protected static <T extends DefaultApiClient> T withRetry(T apiClient,
			boolean retry) {
		apiClient.getRequestConfig().setRetry(retry);
		return apiClient;
	}

	@Override
	public ApiClient withSaveOnClose(boolean saveOnClose) {
		return withSaveOnClose(this, saveOnClose);
	}

	@Override
	public ApiClient withSaveOnClose() {
		return withSaveOnClose(true);
	}

	@Override
	public ApiClient withRetry(boolean retry) {
		return withRetry(this, retry);
	}
}
