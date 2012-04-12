package com.kogasoftware.odt.webapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebAPI {
	private static final Integer MAX_REQUEST_RETRY = 5;
	private static final Integer REQUEST_RETRY_EXPIRE_MINUTES = 5;

	private String authenticationToken;

	public interface ResponseConverter<T> {
		public T convert(byte[] rawResponse) throws Exception;
	}

	public WebAPI() {
		this("");
	}

	public WebAPI(String authenticationToken) {
		this.authenticationToken = authenticationToken;
	}
	
	public String getAuthenticationToken() {
		return authenticationToken;
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

	protected static final String SERVER_HOST = "http://192.168.104.63:3000";
	protected static final String PATH_PREFIX = "/in_vehicle_devices";

	public static final String PATH_LOGIN = "/sign_in";
	public static final String PATH_NOTIFICATIONS = "/vehicle_notifications";
	
	public interface WebAPICallback<T> {
		public void onSucceed(int reqkey, T result);
		public void onFailed(int reqkey);
		public void onException(int reqkey, WebAPIException ex);
	}
	
	class WebAPITask<T> extends AsyncTask<Void, Integer, T> {
		private WebAPICallback<T> callback;
		private int reqkey;
		private ResponseConverter<T> responseConverter;
		private HttpRequestBase request;

		// Constructor for GET, DELETE
		public WebAPITask(String host, String path, Map<String, String> params,
				ResponseConverter<T> responseConverter, WebAPICallback<T> callback, HttpRequestBase request) throws WebAPIException {
			setHttpRequestBase(host, path, params, request);
			this.responseConverter = responseConverter;
			this.callback = callback;
			this.request = request;
		}

		// Constructor for PUT, POST
		public WebAPITask(String host, String path,
				JSONObject entityJSON, ResponseConverter<T> responseConverter, WebAPICallback<T> callback, HttpEntityEnclosingRequestBase request) throws WebAPIException {
			setEntityEnclosingRequestBase(host, path, entityJSON, request);
			this.responseConverter = responseConverter;
			this.request = request;
		}
		
		protected T doHttpSession(HttpRequestBase request,
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
				throw new WebAPIException(false, "Status Code = " + statusCode);
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
				throw new WebAPIException(false, e);
			}
		}

		protected void setHttpRequestBase(String host, String path, Map<String, String> params,
				HttpRequestBase request)
						throws WebAPIException {

			Uri.Builder uriBuilder = Uri.parse(host).buildUpon();
			uriBuilder.path(path + ".json");
			
			if (authenticationToken != null && authenticationToken.length() > 0) {
				uriBuilder.appendQueryParameter("authentication_token",
						authenticationToken);
			}
			if (params != null) {
				for (Entry<String, String> entry : (new TreeMap<String, String>(params))
						.entrySet()) {
					uriBuilder.appendQueryParameter(entry.getKey(), entry.getValue());
				}
			}
			String uri = uriBuilder.toString();
			Log.d("WebAPI", uri);
			
			try {
				request.setURI(new URI(uri));
			} catch (URISyntaxException e) {
				throw new WebAPIException(false, e);
			}
		}

		protected void setEntityEnclosingRequestBase(String host, String path,
				JSONObject entityJSON, HttpEntityEnclosingRequestBase request) throws WebAPIException {
			Uri.Builder uriBuilder = Uri.parse(host).buildUpon();
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
			} catch (JSONException e) {
				throw new WebAPIException(false, e);
			} catch (UnsupportedEncodingException e) {
				throw new WebAPIException(false, e);
			} catch (URISyntaxException e) {
				throw new WebAPIException(false, e);
			}
		}
		
		@Override
		protected T doInBackground(Void... params) {
			try {
				return doHttpSession(request, responseConverter);
			} catch (WebAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(T result) {
			super.onPostExecute(result);

			if (callback != null) {
				if (result != null) {
					callback.onSucceed(reqkey, result);
				} else {
					callback.onFailed(reqkey);
				}
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
		
	}

	private <T> void get(String path, WebAPICallback<T> callback, ResponseConverter<T> conv) throws WebAPIException {
		WebAPITask<T> task = new WebAPITask<T>(SERVER_HOST, PATH_PREFIX + path, null, conv, callback, new HttpGet());
		task.execute();
	}
	
	private <T> void post(String path, JSONObject param, WebAPICallback<T> callback, ResponseConverter<T> conv)  throws WebAPIException {
		WebAPITask<T> task = new WebAPITask<T>(SERVER_HOST, PATH_PREFIX + path, param, conv, callback, new HttpPost());
		task.execute();		
	}

	protected JSONObject parseJSONObject(byte[] rawResponse) throws JSONException {
		try {
			return new JSONObject(new String(rawResponse, "iso-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected JSONArray parseJSONArray(byte[] rawResponse) throws JSONException {
		try {
			return new JSONArray(new String(rawResponse, "iso-8859-1"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private JSONObject filterJSONKeys(JSONObject jsonObject, String[] keys) {
		JSONObject res = new JSONObject();

		for (String key : keys) {
			try {
				res.put(key, jsonObject.get(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}

	public void login(InVehicleDevice login, WebAPICallback<InVehicleDevice> callback) throws WebAPIException, JSONException {
		JSONObject ivd = filterJSONKeys(login.toJSONObject(), new String[] { "login", "password" });
		JSONObject param = new JSONObject();
		param.put("in_vehicle_device", ivd);

		post(PATH_LOGIN, param, callback, new ResponseConverter<InVehicleDevice>() {
			@Override
			public InVehicleDevice convert(byte[] rawResponse)
					throws Exception {
				return InVehicleDevice.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}
	
	public void getVehicleNotifications(WebAPICallback<List<VehicleNotification>> callback) throws WebAPIException {
		get(PATH_NOTIFICATIONS, callback, new ResponseConverter<List<VehicleNotification>>() {
			@Override
			public List<VehicleNotification> convert(byte[] rawResponse)
					throws Exception {
				return VehicleNotification.parseList(parseJSONArray(rawResponse));
			}
		});
	}

}
