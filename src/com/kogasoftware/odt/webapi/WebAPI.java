package com.kogasoftware.odt.webapi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebAPI {
	private static final String TAG = WebAPI.class.getSimpleName();
	protected static final int NUM_THREADS = 3;
	protected final String host;
	protected final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private String authenticationToken = "";
	
	protected static String decodeByteArray(byte[] byteArray) {
		return Charsets.ISO_8859_1.decode(ByteBuffer.wrap(byteArray)).toString();
	}

	public interface ResponseConverter<T> {
		public T convert(byte[] rawResponse) throws Exception;
	}

	public WebAPI(String host) {
		this(host, "");
	}

	public WebAPI(String host, String authenticationToken) {
		this.host = host;
		this.authenticationToken = authenticationToken;
		executorService.schedule(new Runnable() {
			@Override
			public void run() {
				doWebAPISession();
			}
		}, 10, TimeUnit.SECONDS);
	}

	protected void doHttpSession(WebAPIRequest request) throws WebAPIException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(request.getRequest());
		} catch (ClientProtocolException e) {
			throw new WebAPIException(true, e);
		} catch (IOException e) {
			throw new WebAPIException(true, e);
		}

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		byte[] response = new byte[] {};
		String responseString = "";
		try {
			HttpEntity entity = httpResponse.getEntity();
			if (entity != null) {
				response = ByteStreams.toByteArray(entity.getContent());
				responseString = decodeByteArray(response);
				Log.d(TAG, "response body:" + responseString);
			}
		} catch (IOException e) {
			throw new WebAPIException(true, e);
		}

		if (statusCode / 100 == 4 || statusCode / 100 == 5) {
			request.onFailed(statusCode, responseString);
			return;
		}

		try {
			request.onSucceed(statusCode, response);
		} catch (Exception e) {
			throw new WebAPIException(false, e);
		}
	}

	protected void doWebAPISession() {
		WebAPIRequest request = requests.peek();
		try {
			doHttpSession(request);
			if (request.isSucceed()) {
				requests.remove(request);
			}
		} catch (WebAPIException e) {
			request.onException(e);
		}
	}

	public String getAuthenticationToken() {
		return authenticationToken;
	}

	protected static final String PATH_PREFIX = "/in_vehicle_devices";
	public static final String PATH_LOGIN = PATH_PREFIX + "/sign_in";
	public static final String PATH_NOTIFICATIONS = PATH_PREFIX
			+ "/vehicle_notifications";
	public static final String PATH_SCHEDULES = PATH_PREFIX
			+ "/operation_schedules";
	public static final String PATH_STATUSLOGS = PATH_PREFIX
			+ "/service_unit_status_logs";

	public interface WebAPICallback<T> {
		/**
		 * リクエスト成功時のコールバック
		 * 
		 * @param reqkey
		 *            リクエスト時の reqkey
		 * @param statusCode
		 *            HTTPステータス
		 * @param result
		 *            結果のオブジェクト
		 */
		public void onSucceed(int reqkey, int statusCode, T result);

		/**
		 * リクエスト失敗時のコールバック
		 * 
		 * @param reqkey
		 *            リクエスト時の reqkey
		 * @param statusCode
		 *            HTTPステータス
		 */
		public void onFailed(int reqkey, int statusCode, String response);

		/**
		 * 例外発生時のコールバック
		 * 
		 * @param reqkey
		 *            リクエスト時の reqkey
		 * @param ex
		 *            例外オブジェクト
		 */
		public void onException(int reqkey, WebAPIException ex);
	}

	static class EmptyWebAPICallback<T> implements WebAPICallback<T> {
		@Override
		public void onSucceed(int reqkey, int statusCode, T result) {
		}

		@Override
		public void onFailed(int reqkey, int statusCode, String response) {
		}

		@Override
		public void onException(int reqkey, WebAPIException ex) {
		}
	}

	protected final ConcurrentLinkedQueue<WebAPIRequest> requests = new ConcurrentLinkedQueue<WebAPIRequest>();

	protected String getServerHost() {
		return host;
	}

	protected <T> int get(String path, WebAPICallback<T> callback,
			ResponseConverter<T> conv) throws WebAPIException {
		WebAPIRequest request = WebAPIRequestFactory.newInstance(
				getServerHost(), path, null, new HttpGet(), callback, conv);
		requests.add(request);
		return request.getReqKey();
	}

	protected <T> int delete(String path, WebAPICallback<T> callback,
			ResponseConverter<T> conv) throws WebAPIException {
		WebAPIRequest request = WebAPIRequestFactory.newInstance(
				getServerHost(), path, null, new HttpDelete(), callback, conv);
		requests.add(request);
		return request.getReqKey();
	}

	protected <T> int post(String path, JSONObject param,
			WebAPICallback<T> callback, ResponseConverter<T> conv)
			throws WebAPIException {
		WebAPIRequest request = WebAPIRequestFactory.newInstance(
				getServerHost(), path, param, new HttpPost(), callback, conv);
		requests.add(request);
		return request.getReqKey();
	}

	protected <T> int put(String path, JSONObject param,
			WebAPICallback<T> callback, ResponseConverter<T> conv)
			throws WebAPIException {
		WebAPIRequest request = WebAPIRequestFactory.newInstance(
				getServerHost(), path, param, new HttpPut(), callback, conv);
		requests.add(request);
		return request.getReqKey();
	}

	protected JSONObject parseJSONObject(byte[] rawResponse)
			throws JSONException {
		String json = decodeByteArray(rawResponse);
		Log.d(TAG + "#parseJSONObject", json);
		return new JSONObject(json);
	}

	protected JSONArray parseJSONArray(byte[] rawResponse) throws JSONException {
		String json = decodeByteArray(rawResponse);
		Log.d(TAG + "#parseJSONArray", json);
		return new JSONArray(json);
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
			String key = (String) it.next();
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
	 * 
	 * @param login
	 *            　ログイン情報(login, password のみ設定必要)
	 * @param callback
	 *            処理完了時のコールバック
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException
	 */
	public int login(InVehicleDevice login,
			final WebAPICallback<InVehicleDevice> callback)
			throws WebAPIException, JSONException {
		JSONObject ivd = filterJSONKeys(login.toJSONObject(), new String[] {
				"login", "password" });
		JSONObject param = new JSONObject();
		param.put("in_vehicle_device", ivd);

		return post(PATH_LOGIN, param, new WebAPICallback<InVehicleDevice>() {

			@Override
			public void onSucceed(int reqkey, int statusCode,
					InVehicleDevice result) {
				if (result.getAuthenticationToken().isPresent()) {
					WebAPI.this.authenticationToken = result
							.getAuthenticationToken().get();
					Log.d(TAG, "onSucceed : "
							+ WebAPI.this.authenticationToken);
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
			public InVehicleDevice convert(byte[] rawResponse) throws Exception {
				return InVehicleDevice.parse(parseJSONObject(rawResponse))
						.orNull();
			}
		});
	}

	/**
	 * 自車への通知を取得
	 * 
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getVehicleNotifications(
			WebAPICallback<List<VehicleNotification>> callback)
			throws WebAPIException {
		return get(PATH_NOTIFICATIONS, callback,
				new ResponseConverter<List<VehicleNotification>>() {
					@Override
					public List<VehicleNotification> convert(byte[] rawResponse)
							throws Exception {
						return VehicleNotification
								.parseList(parseJSONArray(rawResponse));
					}
				});
	}

	/**
	 * 自車への通知への応答
	 * 
	 * @param vn
	 *            通知オブジェクト
	 * @param response
	 *            応答
	 * @throws JSONException
	 */
	public int responseVehicleNotification(VehicleNotification vn,
			int response, WebAPICallback<VehicleNotification> callback)
			throws WebAPIException, JSONException {
		vn.setResponse(response);
		JSONObject vnJson = filterJSONKeys(vn.toJSONObject(), new String[] {
				"id", "response" });
		vnJson.put("id", vn.getId());
		JSONObject param = new JSONObject();
		param.put("vehicle_notification", vnJson);

		return put(PATH_NOTIFICATIONS + "/" + vn.getId(), param, callback,
				new ResponseConverter<VehicleNotification>() {
					@Override
					public VehicleNotification convert(byte[] rawResponse)
							throws Exception {
						return VehicleNotification.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}

	/**
	 * 運行情報を取得する
	 */
	public int getOperationSchedules(
			WebAPICallback<List<OperationSchedule>> callback)
			throws WebAPIException {
		return get(PATH_SCHEDULES, callback,
				new ResponseConverter<List<OperationSchedule>>() {
					@Override
					public List<OperationSchedule> convert(byte[] rawResponse)
							throws Exception {
						List<OperationSchedule> operationSchedules = OperationSchedule
								.parseList(parseJSONArray(rawResponse));
						Map<Integer, Reservation> reservations = new HashMap<Integer, Reservation>();
						for (OperationSchedule os : operationSchedules) {
							for (ListIterator<Reservation> rit = os
									.getReservationsAsArrival().listIterator(); rit
									.hasNext();) {
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
	 * 
	 * @param os
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) throws WebAPIException,
			JSONException {
		return put(PATH_SCHEDULES + "/" + os.getId() + "/arrival", null,
				callback, new ResponseConverter<OperationSchedule>() {
					@Override
					public OperationSchedule convert(byte[] rawResponse)
							throws Exception {
						return OperationSchedule.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}

	/**
	 * 出発時のサーバへの通知
	 * 
	 * @param os
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) throws WebAPIException,
			JSONException {
		return put(PATH_SCHEDULES + "/" + os.getId() + "/departure", null,
				callback, new ResponseConverter<OperationSchedule>() {
					@Override
					public OperationSchedule convert(byte[] rawResponse)
							throws Exception {
						return OperationSchedule.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}

	/**
	 * 乗車のサーバへの通知
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) throws WebAPIException,
			JSONException {
		JSONObject vnJson = filterJSONKeys(passengerRecord.toJSONObject(),
				new String[] { "id", "payment", "passenger_count" });
		vnJson.put("id", passengerRecord.getId());
		JSONObject param = new JSONObject();
		param.put("passenger_record", vnJson);
		return put(PATH_SCHEDULES + "/" + operationSchedule.getId()
				+ "/reservations/" + reservation.getId() + "/geton", param,
				callback, new ResponseConverter<PassengerRecord>() {
					@Override
					public PassengerRecord convert(byte[] rawResponse)
							throws Exception {
						return PassengerRecord.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}

	/**
	 * 降車のサーバへの通知
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) throws WebAPIException,
			JSONException {
		JSONObject vnJson = filterJSONKeys(passengerRecord.toJSONObject(),
				new String[] { "id", "payment", "passenger_count" });
		vnJson.put("id", passengerRecord.getId());
		JSONObject param = new JSONObject();
		param.put("passenger_record", vnJson);
		return put(PATH_SCHEDULES + "/" + operationSchedule.getId()
				+ "/reservations/" + reservation.getId() + "/getoff", param,
				callback, new ResponseConverter<PassengerRecord>() {
					@Override
					public PassengerRecord convert(byte[] rawResponse)
							throws Exception {
						return PassengerRecord.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}

	/**
	 * 車載器状態の通知
	 */
	/**
	 * 降車のサーバへの通知
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback)
			throws WebAPIException, JSONException {
		JSONObject logJson = log.toJSONObject();
		JSONObject param = new JSONObject();
		param.put("service_unit_status_log", logJson);
		return post(PATH_STATUSLOGS, param, callback,
				new ResponseConverter<ServiceUnitStatusLog>() {
					@Override
					public ServiceUnitStatusLog convert(byte[] rawResponse)
							throws Exception {
						return ServiceUnitStatusLog.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}

	public void shutdown() {
		executorService.shutdownNow();
	}
}
