package com.kogasoftware.odt.webapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.google.common.base.Objects;
import com.google.common.io.ByteStreams;

public class WebAPI {
	private static final Integer MAX_REQUEST_RETRY = 5;
	private static final Integer REQUEST_RETRY_EXPIRE_MINUTES = 5;
	private static final String URI_SCHEME = "http";
	private static final String URI_AUTHORITY = "10.1.10.161";

	private final String authenticationToken;

	public interface ResponseConverter<T> {
		public T convert(byte[] rawResponse) throws Exception;
	}

	public class JSONObjectResponseConverter implements
	ResponseConverter<JSONObject> {
		@Override
		public JSONObject convert(byte[] rawResponse) throws JSONException {
			return new JSONObject(new String(rawResponse));
		}
	}

	public class JSONArrayResponseConverter implements
	ResponseConverter<JSONArray> {
		@Override
		public JSONArray convert(byte[] rawResponse) throws JSONException {
			return new JSONArray(new String(rawResponse));
		}
	}

	public WebAPI() {
		this("");
	}

	public WebAPI(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}

	protected static class CacheKey {
		protected final String method;
		protected final String uri;
		protected final byte[] entity;

		public CacheKey(String method, String uri, byte[] entity) {
			this.method = method;
			this.uri = uri;
			this.entity = entity;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(method, uri, entity);
		}

		@Override
		public boolean equals(final Object object) {
			if (!(object instanceof CacheKey)) {
				return false;
			}
			CacheKey other = (CacheKey) object;
			return Objects.equal(method, other.method)
					&& Objects.equal(uri, other.uri)
					&& Objects.equal(entity, other.entity);
		}
	}

	protected static class RetryStatus {
		private final Date lastRetry = new Date();
		private final Integer retryCount;

		protected RetryStatus(Integer retryCount) {
			if (retryCount.intValue() >= MAX_REQUEST_RETRY) {
				retryCount = MAX_REQUEST_RETRY;
			}
			this.retryCount = retryCount;
		}

		public RetryStatus() {
			this(0);
		}

		public Boolean isRetryable() {
			return !isExpired() && retryCount.intValue() < MAX_REQUEST_RETRY;
		}

		public Boolean isExpired() {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -REQUEST_RETRY_EXPIRE_MINUTES);
			return calendar.getTime().after(lastRetry);
		}

		public RetryStatus update() {
			return new RetryStatus(retryCount + 1);
		}
	}

	protected Map<CacheKey, RetryStatus> retryStatuses = new HashMap<CacheKey, RetryStatus>();

	/**
	 * リトライすることでリクエストが成功するかどうかが曖昧なエラーが発生した際に、リトライ回数を制限する関数
	 * この関数が同一のキーに対して、一定時間内に一定回数以上呼ばれるとfalseを返す
	 * 。それまではtrueを返す。これらをWebAPIExceptionの引数とする
	 * 
	 * ネットワークエラーなど、リトライすることでリクエストが成功する可能性が高いエラーが発生した場合、 この関数は使わずリトライ可能決め打ちとする。
	 * 
	 * HTTP4XXが帰ってきた時のようなリトライしてもリクエストは成功しない可能性が高いエラーが発生した場合 リトライ不可の決め打ちとする
	 */
	protected Boolean calculateRetryable(CacheKey key) {
		// 古いエントリを削除
		for (Iterator<Entry<CacheKey, RetryStatus>> iterator = retryStatuses
				.entrySet().iterator(); iterator.hasNext();) {
			RetryStatus retryStatus = iterator.next().getValue();
			if (retryStatus.isExpired()) {
				iterator.remove();
			}
		}

		// リトライ可能か判断
		RetryStatus retryStatus = retryStatuses.get(key);
		if (retryStatus == null) {
			retryStatus = new RetryStatus();
		}
		retryStatuses.put(key, retryStatus.update());
		return retryStatus.isRetryable();
	}

	protected <T> T doHttpSession(HttpRequestBase request,
			ResponseConverter<T> responseConverter) throws WebAPIException {

		CacheKey cacheKey;
		{ // TODO: キャッシュ
			String method = request.getMethod();
			String uri = request.getURI().toString();
			byte[] entity = new byte[] {};
			if (request instanceof HttpEntityEnclosingRequestBase) {
				try {
					entity = ByteStreams
							.toByteArray(((HttpEntityEnclosingRequestBase) request)
									.getEntity().getContent());
				} catch (IOException e) {
					throw new WebAPIException(false, e);
				}
			}
			cacheKey = new CacheKey(method, uri, entity);
		}

		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(request);
		} catch (ClientProtocolException e) {
			throw new WebAPIException(true, e);
		} catch (IOException e) {
			throw new WebAPIException(true, e);
		}
		Integer statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode / 100 == 4) {
			throw new WebAPIException(false, "Status Code = " + statusCode);
		} else if (statusCode / 100 == 5) {
			throw new WebAPIException(calculateRetryable(cacheKey),
					"Status Code = " + statusCode);
		}
		byte[] response = new byte[] {};
		try {
			response = ByteStreams.toByteArray(httpResponse.getEntity()
					.getContent());
		} catch (IOException e) {
			throw new WebAPIException(true, e);
		}
		try {
			return responseConverter.convert(response);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		} catch (Exception e) {
			throw new WebAPIException(calculateRetryable(cacheKey), e);
		}
	}

	protected <T> T doHttpRequestBase(String path, Map<String, String> params,
			ResponseConverter<T> responseConverter, HttpRequestBase request)
					throws WebAPIException {

		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme(URI_SCHEME);
		uriBuilder.authority(URI_AUTHORITY);
		uriBuilder.path(path + ".json");
		if (authenticationToken.length() > 0) {
			uriBuilder.appendQueryParameter("authentication_token",
					authenticationToken);
		}
		for (Entry<String, String> entry : (new TreeMap<String, String>(params))
				.entrySet()) {
			uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
		}
		String uri = uriBuilder.toString();
		try {
			request.setURI(new URI(uri));
		} catch (URISyntaxException e) {
			throw new WebAPIException(false, e);
		}
		return doHttpSession(request, responseConverter);
	}

	protected <T> T doEntityEnclosingRequestBase(String path,
			JSONObject entityJSON, ResponseConverter<T> responseConverter,
			HttpEntityEnclosingRequestBase request) throws WebAPIException {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.scheme(URI_SCHEME);
		uriBuilder.authority(URI_AUTHORITY);
		uriBuilder.path(path + ".json");
		String uri = uriBuilder.toString();
		try {
			if (authenticationToken.length() > 0) {
				entityJSON.put("authentication_token", authenticationToken);
			}
			request.setURI(new URI(uri));
			String entityString = entityJSON.toString();
			StringEntity entity = new StringEntity(entityString, "UTF-8");
			entity.setContentType("application/json");
			request.setEntity(entity);
			return doHttpSession(request, responseConverter);
		} catch (JSONException e) {
			throw new WebAPIException(false, e);
		} catch (UnsupportedEncodingException e) {
			throw new WebAPIException(false, e);
		} catch (URISyntaxException e) {
			throw new WebAPIException(false, e);
		}
	}
	
	protected static final String OPERATORWEB_HOST = "192.168.56.3";
	protected static final String PATH_PREFIX = "/in_vehicle_devices";

	public static final String PATH_LOGIN = "/sign_in";
	public static final String PATH_NOTIFICATIONS = "/vehicle_notifications";
	
	public interface WebAPIListener {
		public <T> void OnSucceed(int reqkey, T result);
		public void OnFailed(int reqkey);
		public void OnException(int reqkey, WebAPIException ex);
	}
	
	public int login() {
		return -1;
	}
	
	public int get(String path) {
		return -1;
	}
	
	public int getVehicleNotifications() {
		return get(PATH_NOTIFICATIONS);
	}
	
	
}
