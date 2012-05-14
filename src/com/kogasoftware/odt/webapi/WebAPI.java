package com.kogasoftware.odt.webapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpEntity;
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
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Objects;
import com.google.common.io.ByteStreams;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebAPI {
	private String authenticationToken;
	private String host;

	public interface ResponseConverter<T> {
		public T convert(byte[] rawResponse) throws Exception;
	}

	public WebAPI(String host) {
		this(host, "");
	}

	public WebAPI(String host, String authenticationToken) {
		this.host = host;
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

	protected static final String PATH_PREFIX = "/in_vehicle_devices";
	public static final String PATH_LOGIN = PATH_PREFIX + "/sign_in";
	public static final String PATH_NOTIFICATIONS = PATH_PREFIX + "/vehicle_notifications";
	public static final String PATH_SCHEDULES = PATH_PREFIX + "/operation_schedules";
	public static final String PATH_STATUSLOGS = PATH_PREFIX + "/service_unit_status_logs";
	protected static int reqkeyCounter = 0;
	
	public interface WebAPICallback<T> {
		/** 
		 * リクエスト成功時のコールバック
		 * @param reqkey リクエスト時の reqkey
		 * @param statusCode HTTPステータス
		 * @param result 結果のオブジェクト
		 */
		public void onSucceed(int reqkey, int statusCode, T result);

		/**
		 * リクエスト失敗時のコールバック
		 * @param reqkey リクエスト時の reqkey
		 * @param statusCode HTTPステータス
		 */
		public void onFailed(int reqkey, int statusCode, String response);
		
		/**
		 * 例外発生時のコールバック
		 * @param reqkey リクエスト時の reqkey
		 * @param ex 例外オブジェクト
		 */
		public void onException(int reqkey, WebAPIException ex);
	}
	
	class WebAPITask<T> extends AsyncTask<Void, Integer, T> {
		private WebAPICallback<T> callback;
		private int reqkey;
		private ResponseConverter<T> responseConverter;
		private HttpRequestBase request;
		private int statusCode = -1;
		private boolean succeed = false;
		private String responseString;

		// Constructor for GET, DELETE
		public WebAPITask(String host, String path, Map<String, String> params,
				ResponseConverter<T> responseConverter, WebAPICallback<T> callback, HttpRequestBase request) throws WebAPIException {
			setHttpRequestBase(host, path, params, request);
			this.responseConverter = responseConverter;
			this.callback = callback;
			this.request = request;
			
			synchronized(WebAPI.class) {
				this.reqkey = reqkeyCounter++;
			}
		}

		// Constructor for PUT, POST
		public WebAPITask(String host, String path,
				JSONObject entityJSON, ResponseConverter<T> responseConverter, WebAPICallback<T> callback, HttpEntityEnclosingRequestBase request) throws WebAPIException {
			setEntityEnclosingRequestBase(host, path, entityJSON, request);
			this.responseConverter = responseConverter;
			this.callback = callback;
			this.request = request;

			synchronized(WebAPI.class) {
				this.reqkey = reqkeyCounter++;
			}
		}
		
		protected T doHttpSession(HttpRequestBase request,
				ResponseConverter<T> responseConverter) throws WebAPIException {

			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse;
			try {
				httpResponse = httpClient.execute(request);
			} catch (ClientProtocolException e) {
				throw new WebAPIException(true, e);
			} catch (IOException e) {
				throw new WebAPIException(true, e);
			}
			
			statusCode = httpResponse.getStatusLine().getStatusCode();
			byte[] response = new byte[] {};
			try {
				HttpEntity entity = httpResponse.getEntity();
				if (entity != null) {
					response = ByteStreams.toByteArray(entity.getContent());
					responseString = new String(response, "iso8859-1");
					Log.d("WebAPI", "response body:" + responseString);
				}
			} catch (IOException e) {
				throw new WebAPIException(true, e);
			}

			if (statusCode / 100 == 4 || statusCode / 100 == 5) {
				return null;
			}
			
			try {
				succeed = true;
				return responseConverter.convert(response);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new WebAPIException(false, e);
			} catch (Exception e) {
				throw new WebAPIException(false, e);
			}
		}

		private Uri.Builder buildUri(String host, String path) {
			Uri.Builder uriBuilder = Uri.parse(host).buildUpon();
			uriBuilder.path(path + ".json");

			return uriBuilder;
		}

		protected void setHttpRequestBase(String host, String path, Map<String, String> params,
				HttpRequestBase request)
						throws WebAPIException {

			Uri.Builder uriBuilder = buildUri(host, path);
			
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
			String uri = buildUri(host, path).toString();
			try {
				if (entityJSON == null) {
					entityJSON = new JSONObject();
				}
				
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
				if (callback != null) {
					callback.onException(reqkey, e);
				}
				return null;
			}
		}

		@Override
		protected void onPostExecute(T result) {
			super.onPostExecute(result);

			if (callback != null) {
				if (succeed) {
					callback.onSucceed(reqkey, statusCode, result);
				} else {
					if (statusCode > 0) {
						callback.onFailed(reqkey, statusCode, responseString);
					} else {
						callback.onException(reqkey, new WebAPIException(false, "Illegal status"));
					}
				}
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		public int getReqKey() {
			return reqkey;
		}
		
	}

	protected String getServerHost() {
		return host;
	}
	
	protected <T> int get(String path, WebAPICallback<T> callback, ResponseConverter<T> conv) throws WebAPIException {
		WebAPITask<T> task = new WebAPITask<T>(getServerHost(), path, null, conv, callback, new HttpGet());
		task.execute();
		return task.getReqKey();
	}

	protected <T> int delete(String path, WebAPICallback<T> callback, ResponseConverter<T> conv) throws WebAPIException {
		WebAPITask<T> task = new WebAPITask<T>(getServerHost(), path, null, conv, callback, new HttpDelete());
		task.execute();
		return task.getReqKey();
	}

	protected <T> int post(String path, JSONObject param, WebAPICallback<T> callback, ResponseConverter<T> conv)  throws WebAPIException {
		WebAPITask<T> task = new WebAPITask<T>(getServerHost(), path, param, conv, callback, new HttpPost());
		task.execute();		
		return task.getReqKey();
	}

	protected <T> int put(String path, JSONObject param, WebAPICallback<T> callback, ResponseConverter<T> conv)  throws WebAPIException {
		WebAPITask<T> task = new WebAPITask<T>(getServerHost(), path, param, conv, callback, new HttpPut());
		task.execute();		
		return task.getReqKey();
	}

	protected JSONObject parseJSONObject(byte[] rawResponse) throws JSONException {
		try {
			String json = new String(rawResponse, "iso-8859-1");
			Log.d("WebAPI#parseJSONObject", json);
			return new JSONObject(json);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected JSONArray parseJSONArray(byte[] rawResponse) throws JSONException {
		try {
			String json = new String(rawResponse, "iso-8859-1");
			Log.d("WebAPI#parseJSONArray", json);
			return new JSONArray(json);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected JSONObject filterJSONKeys(JSONObject jsonObject, String[] keys) {
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

	protected JSONObject removeJSONKeys(JSONObject jsonObject, String[] keys) {
		JSONObject res = new JSONObject();

		Iterator<?> it = jsonObject.keys();
		while (it.hasNext()) {
			String key = (String)it.next();
			try {
				res.put(key, jsonObject.get(key));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (String key : keys) {
			res.remove(key);
		}
		
		return res;
	}

	/**
	 * OperatorWeb へログインして authorization_token を取得
	 * @param login　ログイン情報(login, password のみ設定必要)
	 * @param callback 処理完了時のコールバック
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException
	 */
	public int login(InVehicleDevice login, final WebAPICallback<InVehicleDevice> callback) throws WebAPIException, JSONException {
		JSONObject ivd = filterJSONKeys(login.toJSONObject(), new String[] { "login", "password" });
		JSONObject param = new JSONObject();
		param.put("in_vehicle_device", ivd);

		return post(PATH_LOGIN, param, new WebAPICallback<InVehicleDevice>() {

			@Override
			public void onSucceed(int reqkey, int statusCode, InVehicleDevice result) {
				if (result.getAuthenticationToken().isPresent()) {
					WebAPI.this.authenticationToken = result.getAuthenticationToken().get();
					Log.d("WebAPI", "onSucceed : " + WebAPI.this.authenticationToken);
					callback.onSucceed(reqkey, statusCode, result);
				}
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				callback.onFailed(reqkey, statusCode, response);
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				callback.onException(reqkey, ex);
			}			
			
		}, new ResponseConverter<InVehicleDevice>() {
			@Override
			public InVehicleDevice convert(byte[] rawResponse)
					throws Exception {
				return InVehicleDevice.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}
	
	/**
	 * 自車への通知を取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getVehicleNotifications(WebAPICallback<List<VehicleNotification>> callback) throws WebAPIException {
		return get(PATH_NOTIFICATIONS, callback, new ResponseConverter<List<VehicleNotification>>() {
			@Override
			public List<VehicleNotification> convert(byte[] rawResponse)
					throws Exception {
				return VehicleNotification.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 自車への通知への応答
	 * @param vn 通知オブジェクト
	 * @param response 応答
	 * @throws JSONException 
	 */
	public int responseVehicleNotification(VehicleNotification vn, int response, WebAPICallback<VehicleNotification> callback) throws WebAPIException, JSONException {
		vn.setResponse(response);
		JSONObject vnJson = filterJSONKeys(vn.toJSONObject(), new String[] { "id", "response" });
		vnJson.put("id", vn.getId());
		JSONObject param = new JSONObject();
		param.put("vehicle_notification", vnJson);
		
		return put(PATH_NOTIFICATIONS + "/" + vn.getId(), param, callback, new ResponseConverter<VehicleNotification>() {
			@Override
			public VehicleNotification convert(byte[] rawResponse)
					throws Exception {
				return VehicleNotification.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}
	
	/**
	 * 運行情報を取得する
	 */
	public int getOperationSchedules(WebAPICallback<List<OperationSchedule>> callback) throws WebAPIException {
		return get(PATH_SCHEDULES, callback, new ResponseConverter<List<OperationSchedule>>() {
			@Override
			public List<OperationSchedule> convert(byte[] rawResponse)
					throws Exception {
				List<OperationSchedule> operationSchedules = OperationSchedule.parseList(parseJSONArray(rawResponse));
				Map<Integer, Reservation> reservations = new HashMap<Integer, Reservation>();
				for (OperationSchedule os : operationSchedules) {
					for (ListIterator<Reservation> rit = os.getReservationsAsArrival().listIterator(); rit.hasNext(); ) {
						Reservation r = rit.next();
						if (reservations.containsKey(r.getId())) {
							rit.set(reservations.get(r.getId()));
						} else {
							reservations.put(r.getId(), r);
						}
					}
				}
				return operationSchedules;
			}
		});		
	}
	
	/**
	 * 到着時のサーバへの通知
	 * @param os 運行スケジュールオブジェクト
	 * @throws JSONException 
	 */
	public int arrivalOperationSchedule(OperationSchedule os, WebAPICallback<OperationSchedule> callback) throws WebAPIException, JSONException {
		return put(PATH_SCHEDULES + "/" + os.getId() + "/arrival", null, callback, new ResponseConverter<OperationSchedule>() {
			@Override
			public OperationSchedule convert(byte[] rawResponse)
					throws Exception {
				return OperationSchedule.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}
	
	/**
	 * 出発時のサーバへの通知
	 * @param os 運行スケジュールオブジェクト
	 * @throws JSONException 
	 */
	public int departureOperationSchedule(OperationSchedule os, WebAPICallback<OperationSchedule> callback) throws WebAPIException, JSONException {
		return put(PATH_SCHEDULES + "/" + os.getId() + "/departure", null, callback, new ResponseConverter<OperationSchedule>() {
			@Override
			public OperationSchedule convert(byte[] rawResponse)
					throws Exception {
				return OperationSchedule.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}
	
	/**
	 * 乗車のサーバへの通知
	 * @param operationSchedule 運行スケジュールオブジェクト
	 * @throws JSONException 
	 */
	public int getOnPassenger(OperationSchedule operationSchedule, Reservation reservation, PassengerRecord passengerRecord, WebAPICallback<PassengerRecord> callback) throws WebAPIException, JSONException {
		JSONObject vnJson = filterJSONKeys(passengerRecord.toJSONObject(), new String[] { "id", "payment", "passenger_count" });
		vnJson.put("id", passengerRecord.getId());
		JSONObject param = new JSONObject();
		param.put("passenger_record", vnJson);
		return put(PATH_SCHEDULES + "/" + operationSchedule.getId() + "/reservations/" + reservation.getId() + "/geton", param, callback, new ResponseConverter<PassengerRecord>() {
			@Override
			public PassengerRecord convert(byte[] rawResponse)
					throws Exception {
				return PassengerRecord.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}
	
	/**
	 * 降車のサーバへの通知
	 * @param operationSchedule 運行スケジュールオブジェクト
	 * @throws JSONException 
	 */
	public int getOffPassenger(OperationSchedule operationSchedule, Reservation reservation, PassengerRecord passengerRecord, WebAPICallback<PassengerRecord> callback) throws WebAPIException, JSONException {
		JSONObject vnJson = filterJSONKeys(passengerRecord.toJSONObject(), new String[] { "id", "payment", "passenger_count" });
		vnJson.put("id", passengerRecord.getId());
		JSONObject param = new JSONObject();
		param.put("passenger_record", vnJson);
		return put(PATH_SCHEDULES + "/" + operationSchedule.getId() + "/reservations/" + reservation.getId() + "/getoff", param, callback, new ResponseConverter<PassengerRecord>() {
			@Override
			public PassengerRecord convert(byte[] rawResponse)
					throws Exception {
				return PassengerRecord.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}
	
	/**
	 * 車載器状態の通知
	 */
	/**
	 * 降車のサーバへの通知
	 * @param operationSchedule 運行スケジュールオブジェクト
	 * @throws JSONException 
	 */
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log, WebAPICallback<ServiceUnitStatusLog> callback) throws WebAPIException, JSONException {
		JSONObject logJson = log.toJSONObject();
		JSONObject param = new JSONObject();
		param.put("service_unit_status_log", logJson);
		return post(PATH_STATUSLOGS, param, callback, new ResponseConverter<ServiceUnitStatusLog>() {
			@Override
			public ServiceUnitStatusLog convert(byte[] rawResponse)
					throws Exception {
				return ServiceUnitStatusLog.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}
	
	
}
