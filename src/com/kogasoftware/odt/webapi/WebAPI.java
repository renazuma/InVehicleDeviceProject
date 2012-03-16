package com.kogasoftware.odt.webapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

import com.google.common.base.Objects;
import com.google.common.io.ByteStreams;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.Operator;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.User;

public class WebAPI {
	private static final String TAG = WebAPI.class.getSimpleName();
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

	public <T> T get(String path, Map<String, String> params,
			ResponseConverter<T> responseConverter) throws WebAPIException {

		return doHttpRequestBase(path, params, responseConverter, new HttpGet());
	}

	public <T> T delete(String path, Map<String, String> params,
			ResponseConverter<T> responseConverter) throws WebAPIException {

		return doHttpRequestBase(path, params, responseConverter,
				new HttpDelete());
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

	public <T> T post(String path, JSONObject entityJSON,
			ResponseConverter<T> responseConverter) throws WebAPIException {
		return doEntityEnclosingRequestBase(path, entityJSON,
				responseConverter, new HttpPost());
	}

	public <T> T put(String path, JSONObject entityJSON,
			ResponseConverter<T> responseConverter) throws WebAPIException {
		return doEntityEnclosingRequestBase(path, entityJSON,
				responseConverter, new HttpPut());
	}

	public void delete(String path, Integer id) {

	}

	public JSONArray get(String path, Map<String, String> params)
			throws WebAPIException {
		return get(path, params, new JSONArrayResponseConverter());
	}

	public JSONArray get(String path) throws WebAPIException {
		return get(path, new HashMap<String, String>());
	}

	public class Platforms {
		public Platform get(Integer id) throws WebAPIException {
			return WebAPI.this.get(Platform.URL.ROOT, id,
					new Platform.ResponseConverter());
		}

		public List<Platform> get(Map<String, String> params)
				throws WebAPIException {
			return WebAPI.this.get(Platform.URL.ROOT, params,
					new Platform.ListResponseConverter());
		}

		public List<Platform> get() throws WebAPIException {
			return get(new HashMap<String, String>());
		}
	}

	public Platforms platforms = new Platforms();

	public class InVehicleDevices {
		public InVehicleDevice create(final InVehicleDevice inVehicleDevice)
				throws JSONException, URISyntaxException,
				ClientProtocolException, IOException, ParseException,
				WebAPIException {

			JSONObject postJSON = new JSONObject();
			JSONObject inVehicleDeviceJSON = inVehicleDevice.toJSONObject();
			postJSON.put(InVehicleDevice.JSON_NAME, inVehicleDeviceJSON);
			return WebAPI.this.post(InVehicleDevice.URL.CREATE, postJSON,
					new InVehicleDevice.ResponseConverter());
		}
	}

	public InVehicleDevices inVehicleDevices = new InVehicleDevices();

	public class Users {
		public List<User> index(final Map<String, String> params) {
			Callable<List<User>> callable = new Callable<List<User>>() {
				@Override
				public List<User> call() throws Exception {
					return new LinkedList<User>();
				}
			};

			try {
				return callable.call();
			} catch (Exception e) {
				e.printStackTrace();
				return new LinkedList<User>();
			}
		}

		public User show(final Integer id) {
			return new User();
		}

		public User create(final User user, final String password)
				throws WebAPIException {

			JSONObject postJSON = new JSONObject();
			try {
				JSONObject userJSON = user.toJSONObject();
				userJSON.put("password", password);
				postJSON.put(User.JSON_NAME, userJSON);
			} catch (JSONException e) {
				throw new WebAPIException(false, e);
			}
			return WebAPI.this.post(User.URL.CREATE, postJSON,
					new User.ResponseConverter());
		}

		public User update(final User user) {
			return new User();
		}

		public void destroy(final Integer id) {
		}
	}

	public Users users = new Users();

	public class Operators {
		public String signIn(final String login, final String password)
				throws WebAPIException {
			JSONObject postJSON = new JSONObject();
			try {
				JSONObject operator = new JSONObject();
				operator.put("login", login);
				operator.put("password", password);
				postJSON.put("operator", operator);
			} catch (JSONException e) {
				throw new WebAPIException(false, e);
			}
			return WebAPI.this.post(Operator.URL.ROOT + "/sign_in", postJSON,
					new ResponseConverter<String>() {
						@Override
						public String convert(byte[] rawResponse)
								throws Exception {
							JSONObject j = new JSONObject(new String(
									rawResponse));
							String s = j.getString("authentication_token");
							if (s == null) {
								throw new RuntimeException(
										"authentication_token not found");
							}
							return s;
						}
					});
		}
	}

	public Operators operators = new Operators();

	public <T> T get(String path, Integer id,
			ResponseConverter<T> responseConverter) throws WebAPIException {
		return get(path + "/" + id, new HashMap<String, String>(),
				responseConverter);
	}
}
