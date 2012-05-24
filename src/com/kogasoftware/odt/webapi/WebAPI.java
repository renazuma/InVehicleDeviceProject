package com.kogasoftware.odt.webapi;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationRecord;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier.SerializableHttpDeleteSupplier;
import com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier.SerializableHttpGetSupplier;
import com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier.SerializableHttpPostSupplier;
import com.kogasoftware.odt.webapi.serializablehttprequestbasesupplier.SerializableHttpPutSupplier;

public class WebAPI implements Closeable {
	public static class EmptyWebAPICallback<T> implements WebAPICallback<T> {
		@Override
		public void onException(int reqkey, WebAPIException ex) {
		}

		@Override
		public void onFailed(int reqkey, int statusCode, String response) {
		}

		@Override
		public void onSucceed(int reqkey, int statusCode, T result) {
		}
	}

	public interface ResponseConverter<T> {
		public T convert(byte[] rawResponse) throws Exception;
	}

	public interface WebAPICallback<T> {
		/**
		 * 例外発生時のコールバック
		 * 
		 * @param reqkey
		 *            リクエスト時の reqkey
		 * @param ex
		 *            例外オブジェクト
		 */
		public void onException(int reqkey, WebAPIException ex);

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
	}

	class WebAPISessionRunner implements Runnable {
		@Override
		public void run() {
			try {
				doWebAPISession();
			} catch (InterruptedException e) {
				// do nothing
			}
		}
	}

	public static final Integer REQUEST_EXPIRE_DAYS = 3;
	private static final String TAG = WebAPI.class.getSimpleName();
	protected static final int NUM_THREADS = 3;

	protected static final String PATH_PREFIX = "/in_vehicle_devices";
	public static final String PATH_LOGIN = PATH_PREFIX + "/sign_in";
	public static final String PATH_NOTIFICATIONS = PATH_PREFIX
			+ "/vehicle_notifications";
	public static final String PATH_SCHEDULES = PATH_PREFIX
			+ "/operation_schedules";
	public static final String PATH_STATUSLOGS = PATH_PREFIX
			+ "/service_unit_status_logs";
	public static final String PATH_RESERVATIONS = PATH_PREFIX
			+ "/reservations";

	protected static String decodeByteArray(byte[] byteArray) {
		return Charsets.ISO_8859_1.decode(ByteBuffer.wrap(byteArray))
				.toString();
	}

	protected final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	protected final WebAPIRequestQueue requests;
	protected volatile String serverHost = "http://127.0.0.1"; // 複数スレッドから参照の書きかえがありうるためvolatile
	protected volatile String authenticationToken = ""; // 複数スレッドから参照の書きかえがありうるためvolatile

	public WebAPI(String serverHost) {
		this(serverHost, "");
	}

	public WebAPI(String serverHost, String authenticationToken) {
		this(serverHost, authenticationToken, new WebAPIRequestQueue());
	}

	public WebAPI(String serverHost, String authenticationToken, File backupFile) {
		this(serverHost, authenticationToken,
				new WebAPIRequestQueue(backupFile));
	}

	protected WebAPI(String serverHost, String authenticationToken,
			WebAPIRequestQueue requests) {
		this.requests = requests;
		this.authenticationToken = authenticationToken;
		setServerHost(serverHost);
		for (int i = 0; i < NUM_THREADS; ++i) {
			executorService.scheduleWithFixedDelay(new WebAPISessionRunner(),
					0, 10, TimeUnit.SECONDS);
		}
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
		OperationRecord or = os.getOperationRecord().or(new OperationRecord());
		or.setArrivedAt(new Date());
		OperationRecord retryOr = or.clone();
		retryOr.setArrivedAtOffline(true);

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("operation_record", or.toJSONObject());
		retryParam.put("operation_record", retryOr.toJSONObject());

		return put(PATH_SCHEDULES + "/" + os.getId() + "/arrival", param,
				retryParam, callback,
				new ResponseConverter<OperationSchedule>() {
					@Override
					public OperationSchedule convert(byte[] rawResponse)
							throws Exception {
						return OperationSchedule.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}

	@Override
	public void close() {
		executorService.shutdownNow();
	}

	protected <T> int delete(String path, WebAPICallback<T> callback,
			ResponseConverter<T> conv) throws WebAPIException {
		SerializableHttpDeleteSupplier supplier = new SerializableHttpDeleteSupplier(
				getServerHost(), path, authenticationToken);
		WebAPIRequest<?> request = new WebAPIRequest<T>(callback, conv,
				supplier);
		requests.add(request);
		return request.getReqKey();
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
		OperationRecord or = os.getOperationRecord().or(new OperationRecord());
		or.setDepartedAt(new Date());
		OperationRecord retryOr = or.clone();
		retryOr.setDepartedAtOffline(true);

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("operation_record", or.toJSONObject());
		retryParam.put("operation_record", retryOr.toJSONObject());

		return put(PATH_SCHEDULES + "/" + os.getId() + "/departure", param,
				retryParam, callback,
				new ResponseConverter<OperationSchedule>() {
					@Override
					public OperationSchedule convert(byte[] rawResponse)
							throws Exception {
						return OperationSchedule.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}
	
	protected boolean doHttpSessionAndCallback(WebAPIRequest<?> request) {
		try {
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
				return false;
			}

			try {
				request.onSucceed(statusCode, response);
				return true;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new WebAPIException(false, e);
			} catch (Exception e) {
				throw new WebAPIException(false, e);
			}
		} catch (WebAPIException e) {
			request.onException(e);
			return false;
		}
	}

	protected void doWebAPISession() throws InterruptedException {
		WebAPIRequest<?> request = requests.take();
		boolean succeed = false;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -REQUEST_EXPIRE_DAYS);
		if (calendar.getTime().after(request.getCreatedDate())) {
			Log.i(TAG, "Request (" + request + ") is expired. createdDate: "
					+ request.getCreatedDate());
			requests.remove(request);
			return;
		}

		succeed = doHttpSessionAndCallback(request);
		if (succeed || !request.isRetryable()) {
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
				e.printStackTrace();
			}
		}

		return res;
	}

	protected <T> int get(String path, WebAPICallback<T> callback,
			ResponseConverter<T> conv, boolean retryable)
			throws WebAPIException {
		SerializableHttpGetSupplier supplier = new SerializableHttpGetSupplier(
				getServerHost(), path, authenticationToken);
		WebAPIRequest<?> request = new WebAPIRequest<T>(callback, conv,
				supplier, retryable);
		requests.add(request);
		return request.getReqKey();
	}

	protected <T> int get(String path, WebAPICallback<T> callback,
			ResponseConverter<T> conv) throws WebAPIException {
		return get(path, callback, conv, true);
	}

	public String getAuthenticationToken() {
		return authenticationToken;
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
		passengerRecord.setGetOffTime(new Date());
		PassengerRecord retryPassengerRecord = passengerRecord.clone();
		retryPassengerRecord.setGetOffTimeOffline(true);

		String[] filter = new String[] { "id", "payment", "passenger_count",
				"get_off_time", "get_off_time_offline" };

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("passenger_record",
				filterJSONKeys(passengerRecord.toJSONObject(), filter));
		retryParam.put("passenger_record",
				filterJSONKeys(retryPassengerRecord.toJSONObject(), filter));

		String group = getReservationGetOffGroup(operationSchedule.getId(),
				reservation.getId());

		return put(PATH_SCHEDULES + "/" + operationSchedule.getId()
				+ "/reservations/" + reservation.getId() + "/getoff", param,
				retryParam, callback, new ResponseConverter<PassengerRecord>() {
					@Override
					public PassengerRecord convert(byte[] rawResponse)
							throws Exception {
						return PassengerRecord.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				}, true, group);
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
		passengerRecord.setGetOnTime(new Date());
		PassengerRecord retryPassengerRecord = passengerRecord.clone();
		retryPassengerRecord.setGetOnTimeOffline(true);

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();

		String[] filter = new String[] { "id", "payment", "passenger_count",
				"get_on_time", "get_on_time_offline" };

		param.put("passenger_record",
				filterJSONKeys(passengerRecord.toJSONObject(), filter));
		retryParam.put("passenger_record",
				filterJSONKeys(retryPassengerRecord.toJSONObject(), filter));

		String group = getReservationGetOnGroup(operationSchedule.getId(),
				reservation.getId());

		return put(PATH_SCHEDULES + "/" + operationSchedule.getId()
				+ "/reservations/" + reservation.getId() + "/geton", param,
				retryParam, callback, new ResponseConverter<PassengerRecord>() {
					@Override
					public PassengerRecord convert(byte[] rawResponse)
							throws Exception {
						return PassengerRecord.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				}, true, group);
	}

	/**
	 * 乗車のキャンセル
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, WebAPICallback<PassengerRecord> callback)
			throws WebAPIException, JSONException {
		String group = getReservationGetOnGroup(operationSchedule.getId(),
				reservation.getId());
		return put(PATH_SCHEDULES + "/" + operationSchedule.getId()
				+ "/reservations/" + reservation.getId() + "/cancel_geton",
				new JSONObject(), new JSONObject(), callback,
				new ResponseConverter<PassengerRecord>() {
					@Override
					public PassengerRecord convert(byte[] rawResponse)
							throws Exception {
						return PassengerRecord.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				}, true, group);
	}

	/**
	 * 降車のキャンセル
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, WebAPICallback<PassengerRecord> callback)
			throws WebAPIException, JSONException {
		String group = getReservationGetOffGroup(operationSchedule.getId(),
				reservation.getId());
		return put(PATH_SCHEDULES + "/" + operationSchedule.getId()
				+ "/reservations/" + reservation.getId() + "/cancel_getoff",
				new JSONObject(), new JSONObject(), callback,
				new ResponseConverter<PassengerRecord>() {
					@Override
					public PassengerRecord convert(byte[] rawResponse)
							throws Exception {
						return PassengerRecord.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				}, true, group);
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

	protected String getServerHost() {
		return serverHost;
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
	 * OperatorWeb へログインして authorization_token を取得。このAPIは失敗時にリトライしない。
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

		return post(PATH_LOGIN, param, param,
				new WebAPICallback<InVehicleDevice>() {
					@Override
					public void onException(int reqkey, WebAPIException ex) {
						callback.onException(reqkey, ex);
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						callback.onFailed(reqkey, statusCode, response);
					}

					@Override
					public void onSucceed(int reqkey, int statusCode,
							InVehicleDevice result) {
						for (String authenticationToken : result
								.getAuthenticationToken().asSet()) {
							WebAPI.this.authenticationToken = authenticationToken;
							Log.d(TAG, "onSucceed : "
									+ WebAPI.this.authenticationToken);
							callback.onSucceed(reqkey, statusCode, result);
						}
					}
				}, new ResponseConverter<InVehicleDevice>() {
					@Override
					public InVehicleDevice convert(byte[] rawResponse)
							throws Exception {
						return InVehicleDevice.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				}, false);
	}

	protected JSONArray parseJSONArray(byte[] rawResponse) throws JSONException {
		String json = decodeByteArray(rawResponse);
		Log.d(TAG + "#parseJSONArray", json);
		return new JSONArray(json);
	}

	protected JSONObject parseJSONObject(byte[] rawResponse)
			throws JSONException {
		String json = decodeByteArray(rawResponse);
		Log.d(TAG + "#parseJSONObject", json);
		return new JSONObject(json);
	}

	protected <T> int post(String path, JSONObject param,
			JSONObject retryParam, WebAPICallback<T> callback,
			ResponseConverter<T> conv) throws WebAPIException {
		return post(path, param, retryParam, callback, conv, true);
	}

	protected <T> int post(String path, JSONObject param,
			JSONObject retryParam, WebAPICallback<T> callback,
			ResponseConverter<T> conv, boolean retryable)
			throws WebAPIException {
		SerializableHttpPostSupplier first = new SerializableHttpPostSupplier(
				getServerHost(), path, param, authenticationToken);
		SerializableHttpPostSupplier retry = new SerializableHttpPostSupplier(
				getServerHost(), path, retryParam, authenticationToken);
		WebAPIRequest<?> request = new WebAPIRequest<T>(callback, conv, first,
				retry, retryable);
		requests.add(request);
		return request.getReqKey();
	}

	protected <T> int post(String path, JSONObject param,
			WebAPICallback<T> callback, ResponseConverter<T> conv)
			throws WebAPIException {
		return post(path, param, param, callback, conv, true);
	}

	protected <T> int put(String path, JSONObject param, JSONObject retryParam,
			WebAPICallback<T> callback, ResponseConverter<T> conv)
			throws WebAPIException {
		return put(path, param, retryParam, callback, conv, true);
	}

	protected <T> int put(String path, JSONObject param, JSONObject retryParam,
			WebAPICallback<T> callback, ResponseConverter<T> conv,
			boolean retryable) throws WebAPIException {
		return put(path, param, retryParam, callback, conv, retryable,
				WebAPIRequestQueue.DEFAULT_GROUP);
	}

	protected <T> int put(String path, JSONObject param, JSONObject retryParam,
			WebAPICallback<T> callback, ResponseConverter<T> conv,
			boolean retryable, String requestGroup) throws WebAPIException {
		SerializableHttpPutSupplier first = new SerializableHttpPutSupplier(
				getServerHost(), path, param, authenticationToken);
		SerializableHttpPutSupplier retry = new SerializableHttpPutSupplier(
				getServerHost(), path, retryParam, authenticationToken);
		WebAPIRequest<?> request = new WebAPIRequest<T>(callback, conv, first,
				retry, retryable);
		requests.add(request, requestGroup);
		return request.getReqKey();
	}

	protected <T> int put(String path, JSONObject param,
			WebAPICallback<T> callback, ResponseConverter<T> conv)
			throws WebAPIException {
		return put(path, param, param, callback, conv, true);
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
	 * 予約候補を取得
	 * 
	 * @throws JSONException
	 */
	public int searchReservationCandidate(Demand demand,
			WebAPICallback<List<ReservationCandidate>> callback)
			throws JSONException {
		return 0;
	}

	/**
	 * 予約の実行
	 * 
	 * @throws JSONException
	 */
	public int createReservation(ReservationCandidate reservationCandidate,
			WebAPICallback<Reservation> callback) throws JSONException {
		return 0;
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
		vn.setReadAt(new Date());
		VehicleNotification retryVn = vn.clone();
		retryVn.setOffline(true);

		String[] filter = new String[] { "id", "response", "read_at", "offline" };

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("vehicle_notification",
				filterJSONKeys(vn.toJSONObject(), filter));
		retryParam.put("vehicle_notification",
				filterJSONKeys(retryVn.toJSONObject(), filter));

		return put(PATH_NOTIFICATIONS + "/" + vn.getId(), param, retryParam,
				callback, new ResponseConverter<VehicleNotification>() {
					@Override
					public VehicleNotification convert(byte[] rawResponse)
							throws Exception {
						return VehicleNotification.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}

	/**
	 * 車載器状態の通知
	 */
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback)
			throws WebAPIException, JSONException {
		log.setOfflineTime(new Date());
		ServiceUnitStatusLog retryLogJson = log.clone();
		retryLogJson.setOffline(true);

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("service_unit_status_log", log.toJSONObject());
		retryParam.put("service_unit_status_log", retryLogJson.toJSONObject());

		return post(PATH_STATUSLOGS, param, retryParam, callback,
				new ResponseConverter<ServiceUnitStatusLog>() {
					@Override
					public ServiceUnitStatusLog convert(byte[] rawResponse)
							throws Exception {
						return ServiceUnitStatusLog.parse(
								parseJSONObject(rawResponse)).orNull();
					}
				});
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	protected String getReservationGetOnGroup(Integer operationScheduleId,
			Integer reservationId) {
		return "ReservationGetOnGroup/operationScheduleId="
				+ operationScheduleId + "/reservationId=" + reservationId;
	}

	protected String getReservationGetOffGroup(Integer operationScheduleId,
			Integer reservationId) {
		return "ReservationGetOffGroup/operationScheduleId="
				+ operationScheduleId + "/reservationId=" + reservationId;
	}
}
