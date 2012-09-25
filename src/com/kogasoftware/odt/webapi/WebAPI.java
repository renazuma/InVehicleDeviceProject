package com.kogasoftware.odt.webapi;

import java.io.Closeable;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.common.base.Charsets;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationRecord;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.User;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.serializablerequestloader.SerializableDeleteLoader;
import com.kogasoftware.odt.webapi.serializablerequestloader.SerializableGetLoader;
import com.kogasoftware.odt.webapi.serializablerequestloader.SerializablePostLoader;
import com.kogasoftware.odt.webapi.serializablerequestloader.SerializablePutLoader;
import com.kogasoftware.odt.webapi.serializablerequestloader.SerializableRequestLoader;

public class WebAPI implements Closeable {
	public interface ResponseConverter<T> {
		T convert(byte[] rawResponse) throws Exception;
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
		void onException(int reqkey, WebAPIException ex);

		/**
		 * リクエスト失敗時のコールバック
		 * 
		 * @param reqkey
		 *            リクエスト時の reqkey
		 * @param statusCode
		 *            HTTPステータス
		 */
		void onFailed(int reqkey, int statusCode, String response);

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
		void onSucceed(int reqkey, int statusCode, T result);
	}

	protected class WebAPISessionRunner implements Runnable {
		@Override
		public void run() {
			try {
				doWebAPISession();
			} catch (InterruptedException e) {
			}
		}
	}

	private static final String TAG = WebAPI.class.getSimpleName();

	public static final Integer REQUEST_EXPIRE_DAYS = 3;
	public static final ResponseConverter<Void> VOID_RESPONSE_CONVERTER = new ResponseConverter<Void>() {
		@Override
		public Void convert(byte[] rawResponse) throws Exception {
			return null;
		}
	};

	protected static final int NUM_THREADS = 5;
	protected static final String UNIQUE_GROUP = WebAPIRequestQueue.UNIQUE_GROUP;

	protected static final String PATH_PREFIX = "/in_vehicle_devices";
	public static final String PATH_LOGIN = PATH_PREFIX + "/sign_in";
	public static final String PATH_VEHICLE_NOTIFICATIONS = PATH_PREFIX
			+ "/vehicle_notifications";
	public static final String PATH_OPERATION_SCHEDULES = PATH_PREFIX
			+ "/operation_schedules";
	public static final String PATH_SERVICE_UNIT_STATUS_LOGS = PATH_PREFIX
			+ "/service_unit_status_logs";
	public static final String PATH_RESERVATIONS = PATH_PREFIX
			+ "/reservations";
	public static final String PATH_SERVICE_PRIVIDER = PATH_PREFIX
			+ "/service_provider";

	protected static String decodeByteArray(byte[] byteArray) {
		return Charsets.ISO_8859_1.decode(ByteBuffer.wrap(byteArray))
				.toString();
	}

	protected final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	protected final WebAPIRequestQueue requests;
	protected volatile String serverHost = "http://127.0.0.1"; // 複数スレッドから参照の書きかえがありうるためvolatile
	protected volatile String authenticationToken = ""; // 複数スレッドから参照の書きかえがありうるためvolatile
	protected final Object requestConfigsLock = new Object();
	protected final WeakHashMap<Thread, WebAPIRequestConfig> requestConfigs = new WeakHashMap<Thread, WebAPIRequestConfig>();

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
		OperationRecord retryOr = or.cloneByJSON();
		retryOr.setArrivedAtOffline(true);

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("operation_record", or.toJSONObject());
		retryParam.put("operation_record", retryOr.toJSONObject());

		return put(PATH_OPERATION_SCHEDULES + "/" + os.getId() + "/arrival",
				param, retryParam, UNIQUE_GROUP, callback,
				OperationSchedule.RESPONSE_CONVERTER);
	}

	@Override
	public void close() {
		executorService.shutdownNow();
	}

	protected <T> int delete(String path, WebAPICallback<T> callback,
			ResponseConverter<? extends T> conv) throws WebAPIException {
		SerializableDeleteLoader loader = new SerializableDeleteLoader(
				getServerHost(), path, authenticationToken);
		WebAPIRequest<?> request = new WebAPIRequest<T>(callback, conv, loader);
		addRequest(request);
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
		OperationRecord retryOr = or.cloneByJSON();
		retryOr.setDepartedAtOffline(true);

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("operation_record", or.toJSONObject());
		retryParam.put("operation_record", retryOr.toJSONObject());

		return put(PATH_OPERATION_SCHEDULES + "/" + os.getId() + "/departure",
				param, retryParam, UNIQUE_GROUP, callback,
				OperationSchedule.RESPONSE_CONVERTER);
	}

	protected boolean doHttpSessionAndCallback(WebAPIRequest<?> request) {
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
		} catch (WebAPIException e) {
			request.onException(e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			request.onException(new WebAPIException(e));
		} catch (Exception e) {
			request.onException(new WebAPIException(e));
		}
		return false;
	}

	protected void doWebAPISession() throws InterruptedException {
		WebAPIRequest<?> request = requests.take();
		boolean succeed = false;
		Date now = new Date();
		if (DateUtils.addDays(request.getCreatedDate(), REQUEST_EXPIRE_DAYS)
				.before(now)) {
			Log.i(TAG, "Request (" + request + ") is expired. createdDate: "
					+ request.getCreatedDate());
			requests.remove(request);
			return;
		}

		succeed = doHttpSessionAndCallback(request);
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
			String requestGroup, WebAPICallback<T> callback,
			ResponseConverter<? extends T> conv) throws WebAPIException {
		SerializableGetLoader loader = new SerializableGetLoader(
				getServerHost(), path, params, authenticationToken);
		WebAPIRequest<?> request = new WebAPIRequest<T>(callback, conv, loader);
		addRequest(request, requestGroup);
		return request.getReqKey();
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
			Reservation reservation, User user,
			PassengerRecord passengerRecord, WebAPICallback<Void> callback)
			throws WebAPIException, JSONException {
		passengerRecord.setGetOffTime(new Date());
		PassengerRecord retryPassengerRecord = passengerRecord.cloneByJSON();
		retryPassengerRecord.setGetOffTimeOffline(true);

		String[] filter = new String[] { "id", "payment", "passenger_count",
				"get_off_time", "get_off_time_offline" };

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("passenger_record",
				filterJSONKeys(passengerRecord.toJSONObject(), filter));
		retryParam.put("passenger_record",
				filterJSONKeys(retryPassengerRecord.toJSONObject(), filter));

		String group = getPassengerRecordGetOnOrOffGroup(
				operationSchedule.getId(), reservation.getId(), user.getId());

		return put(
				PATH_OPERATION_SCHEDULES + "/" + operationSchedule.getId()
						+ "/reservations/" + reservation.getId() + "/users/"
						+ user.getId() + "/passenger_record", param,
				retryParam, group, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 乗車のサーバへの通知
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, WebAPICallback<Void> callback)
			throws WebAPIException, JSONException {
		passengerRecord.setGetOnTime(new Date());
		PassengerRecord retryPassengerRecord = passengerRecord.cloneByJSON();
		retryPassengerRecord.setGetOnTimeOffline(true);

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();

		String[] filter = new String[] { "id", "payment", "passenger_count",
				"get_on_time", "get_on_time_offline" };

		param.put("passenger_record",
				filterJSONKeys(passengerRecord.toJSONObject(), filter));
		retryParam.put("passenger_record",
				filterJSONKeys(retryPassengerRecord.toJSONObject(), filter));

		String group = getPassengerRecordGetOnOrOffGroup(
				operationSchedule.getId(), reservation.getId(), user.getId());

		return put(
				PATH_OPERATION_SCHEDULES + "/" + operationSchedule.getId()
						+ "/reservations/" + reservation.getId() + "/users/"
						+ user.getId() + "/passenger_record", param,
				retryParam, group, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 乗車のキャンセル
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, WebAPICallback<Void> callback)
			throws WebAPIException, JSONException {
		PassengerRecord passengerRecord = new PassengerRecord();
		passengerRecord.setGetOnTime(new Date());
		passengerRecord.clearGetOffTime();
		JSONObject param = passengerRecord.toJSONObject();
		String group = getPassengerRecordGetOnOrOffGroup(
				operationSchedule.getId(), reservation.getId(), user.getId());
		return put(
				PATH_OPERATION_SCHEDULES + "/" + operationSchedule.getId()
						+ "/reservations/" + reservation.getId() + "/users/"
						+ user.getId() + "/passenger_record/canceled", param,
				group, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 降車のキャンセル
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト
	 * @throws JSONException
	 */
	public int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, WebAPICallback<Void> callback)
			throws WebAPIException, JSONException {
		PassengerRecord passengerRecord = new PassengerRecord();
		passengerRecord.clearGetOnTime();
		passengerRecord.setGetOffTime(new Date());
		JSONObject param = passengerRecord.toJSONObject();
		String group = getPassengerRecordGetOnOrOffGroup(
				operationSchedule.getId(), reservation.getId(), user.getId());
		return put(
				PATH_OPERATION_SCHEDULES + "/" + operationSchedule.getId()
						+ "/reservations/" + reservation.getId() + "/users/"
						+ user.getId() + "/passenger_record/canceled", param,
				group, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 運行情報を取得する
	 */
	public int getOperationSchedules(
			WebAPICallback<List<OperationSchedule>> callback)
			throws WebAPIException {
		return get(PATH_OPERATION_SCHEDULES, new TreeMap<String, String>(),
				UNIQUE_GROUP, callback,
				OperationSchedule.LIST_RESPONSE_CONVERTER);
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
		return get(PATH_VEHICLE_NOTIFICATIONS, new TreeMap<String, String>(),
				UNIQUE_GROUP, callback,
				VehicleNotification.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * OperatorWebへログインしてauthorization_tokenを取得
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

		return post(PATH_LOGIN, param, UNIQUE_GROUP,
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
				}, InVehicleDevice.RESPONSE_CONVERTER);
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
			WebAPICallback<T> callback, ResponseConverter<? extends T> conv)
			throws WebAPIException {
		SerializablePostLoader first = new SerializablePostLoader(
				getServerHost(), path, param, authenticationToken);
		SerializablePostLoader retry = new SerializablePostLoader(
				getServerHost(), path, retryParam, authenticationToken);
		WebAPIRequest<?> request = new WebAPIRequest<T>(callback, conv, first,
				retry);
		addRequest(request, requestGroup);
		return request.getReqKey();
	}

	protected <T> int post(String path, JSONObject param, String requestGroup,
			WebAPICallback<T> callback, ResponseConverter<? extends T> conv)
			throws WebAPIException {
		return post(path, param, param, requestGroup, callback, conv);
	}

	protected <T> int put(String path, JSONObject param, String requestGroup,
			WebAPICallback<T> callback, ResponseConverter<? extends T> conv)
			throws WebAPIException {
		return put(path, param, param, requestGroup, callback, conv);
	}

	protected <T> int put(String path, JSONObject param, JSONObject retryParam,
			String requestGroup, WebAPICallback<T> callback,
			ResponseConverter<? extends T> conv) throws WebAPIException {
		SerializablePutLoader first = new SerializablePutLoader(
				getServerHost(), path, param, authenticationToken);
		SerializablePutLoader retry = new SerializablePutLoader(
				getServerHost(), path, retryParam, authenticationToken);
		WebAPIRequest<?> request = new WebAPIRequest<T>(callback, conv, first,
				retry);
		addRequest(request, requestGroup);
		return request.getReqKey();
	}

	/**
	 * 予約候補を取得
	 */
	public int searchReservationCandidate(Demand demand,
			WebAPICallback<List<ReservationCandidate>> callback)
			throws JSONException, WebAPIException {
		JSONObject param = new JSONObject();
		param.put("demand", demand.toJSONObject());
		return post(PATH_RESERVATIONS + "/search", param, UNIQUE_GROUP,
				callback, ReservationCandidate.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 予約の実行
	 */
	public int createReservation(ReservationCandidate reservationCandidate,
			WebAPICallback<Reservation> callback) throws JSONException,
			WebAPIException {
		JSONObject param = new JSONObject();
		param.put("reservation_candidate_id", reservationCandidate.getId());
		return post(PATH_RESERVATIONS, param, UNIQUE_GROUP, callback,
				Reservation.RESPONSE_CONVERTER);
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
		VehicleNotification retryVn = vn.cloneByJSON();
		retryVn.setOffline(true);

		String[] filter = new String[] { "id", "response", "read_at", "offline" };

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("vehicle_notification",
				filterJSONKeys(vn.toJSONObject(), filter));
		retryParam.put("vehicle_notification",
				filterJSONKeys(retryVn.toJSONObject(), filter));

		return put(PATH_VEHICLE_NOTIFICATIONS + "/" + vn.getId(), param,
				retryParam, UNIQUE_GROUP, callback,
				VehicleNotification.RESPONSE_CONVERTER);
	}

	/**
	 * 車載器状態の通知
	 */
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback)
			throws WebAPIException, JSONException {
		log.setOfflineTime(new Date());
		ServiceUnitStatusLog retryLog = log.cloneByJSON();
		retryLog.setOffline(true);

		String[] filter = new String[] { "latitude", "longitude", "offline",
				"offline_time", "orientation", "temperature" };

		JSONObject param = new JSONObject();
		JSONObject retryParam = new JSONObject();
		param.put("service_unit_status_log",
				filterJSONKeys(log.toJSONObject(), filter));
		retryParam.put("service_unit_status_log",
				filterJSONKeys(retryLog.toJSONObject(), filter));

		return post(PATH_SERVICE_UNIT_STATUS_LOGS, param, retryParam,
				"sendServiceUnitStatusLog", callback,
				ServiceUnitStatusLog.RESPONSE_CONVERTER);
	}

	/**
	 * サービスプロバイダの取得
	 */
	public int getServicePrivider(WebAPICallback<ServiceProvider> callback)
			throws WebAPIException {
		return get(PATH_SERVICE_PRIVIDER, new TreeMap<String, String>(),
				UNIQUE_GROUP, callback, ServiceProvider.RESPONSE_CONVERTER);
	}

	/**
	 * http://ojw.dev.openstreetmap.org/StaticMap/?lat=35.214478887245&lon=
	 * 139.21875&z=5&mode=Export&show=1
	 */
	protected SerializableGetLoader getOJWOSMRequestloader(String lat,
			String lon, int zoom) {
		Map<String, String> params = new TreeMap<String, String>();
		params.put("lat", lat);
		params.put("lon", lon);
		params.put("z", "" + zoom);
		params.put("w", "" + 256);
		params.put("h", "" + 256);
		params.put("mode", "Export");
		params.put("show", "1");
		return new SerializableGetLoader("http://ojw.dev.openstreetmap.org",
				"/StaticMap/", params, "", "");
	}

	/**
	 * http://open.mapquestapi.com/staticmap/v3/getmap?size=600,200&zoom=15&
	 * center=41.862648,-87.615549
	 */
	protected SerializableGetLoader getMapQuestOSMRequestloader(String lat,
			String lon, int zoom) {
		Map<String, String> params = new TreeMap<String, String>();
		params.put("center", lat + "," + lon);
		params.put("zoom", "" + zoom);
		params.put("size", "256,256");
		return new SerializableGetLoader("http://open.mapquestapi.com",
				"/staticmap/v3/getmap", params, "", "");
	}

	/**
	 * http://dev.virtualearth.net/REST/v1/Imagery/Map/Road/47.610,-122.107/2?
	 * key=key
	 */
	protected SerializableGetLoader getBingMapsRequestloader(String lat,
			String lon, int zoom) {
		String path = "/REST/v1/Imagery/Map/Road/" + lat + "," + lon + "/"
				+ zoom;
		Map<String, String> params = new TreeMap<String, String>();
		params.put("key", "");
		params.put("mapSize", "300,300");
		params.put("culture", "ja");
		return new SerializableGetLoader("http://dev.virtualearth.net", path,
				params, "", "");
	}

	/**
	 * http://maps.google.com/maps/api/staticmap
	 */
	protected SerializableGetLoader getGoogleMapsRequestloader(String lat,
			String lon, int zoom) {
		Map<String, String> params = new TreeMap<String, String>();
		params.put("center", lat + "," + lon);
		params.put("zoom", "" + zoom);
		params.put("size", "300x300");
		params.put("sensor", "false");
		params.put("language", "ja");
		return new SerializableGetLoader("http://maps.google.com",
				"/maps/api/staticmap", params, "", "");
	}

	/**
	 * 地図画像を取得
	 */
	public int getMapTile(LatLng center, Integer zoom,
			WebAPICallback<Bitmap> callback) {
		ResponseConverter<Bitmap> responseConverter = new ResponseConverter<Bitmap>() {
			@Override
			public Bitmap convert(byte[] rawResponse) throws Exception {
				Bitmap b = BitmapFactory.decodeByteArray(rawResponse, 0,
						rawResponse.length);
				return b;
			}
		};

		String lat = String.format("%.6f", center.getLatitude());
		String lon = String.format("%.6f", center.getLongitude());

		// SerializableRequestLoader loader = getOJWOSMRequestloader(lat, lon,
		// zoom);
		SerializableRequestLoader loader = getGoogleMapsRequestloader(lat, lon,
				zoom);
		// SerializableRequestLoader loader = getMapQuestOSMRequestloader(lat,
		// lon, zoom);
		// SerializableRequestLoader loader = getBingMapsRequestloader(lat, lon,
		// zoom);

		WebAPIRequest<?> request = new WebAPIRequest<Bitmap>(callback,
				responseConverter, loader);
		addRequest(request);
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

	protected WebAPIRequestConfig getRequestConfig() {
		Thread key = Thread.currentThread();
		synchronized (requestConfigsLock) {
			if (requestConfigs.containsKey(key)) {
				return requestConfigs.get(key);
			} else {
				WebAPIRequestConfig requestConfig = new WebAPIRequestConfig();
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

	protected void addRequest(WebAPIRequest<?> request) {
		addRequest(request, UNIQUE_GROUP);
	}

	protected void addRequest(WebAPIRequest<?> request, String requestGroup) {
		WebAPIRequestConfig requestConfig = getRequestConfig();
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
	public WebAPI withSaveOnClose() {
		return withSaveOnClose(true);
	}

	public WebAPI withSaveOnClose(boolean saveOnClose) {
		getRequestConfig().setSaveOnClose(saveOnClose);
		return this;
	}

	/**
	 * 同じスレッドで次に実行するAPIの通信が、リトライするかを設定する。
	 * 
	 * @param reqkey
	 */
	public WebAPI withRetry(boolean retry) {
		getRequestConfig().setRetry(retry);
		return this;
	}
}
