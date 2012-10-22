package com.kogasoftware.odt.invehicledevice.apiclient;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.apiclient.ApiClient;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.apiclient.ApiClientRequest;
import com.kogasoftware.odt.apiclient.ApiClientRequestQueue;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializableGetLoader;
import com.kogasoftware.odt.apiclient.serializablerequestloader.SerializableRequestLoader;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class InVehicleDeviceApiClient extends ApiClient {
	private static final String TAG = InVehicleDeviceApiClient.class.getSimpleName();

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

	public InVehicleDeviceApiClient(String serverHost) {
		super(serverHost, "");
	}

	public InVehicleDeviceApiClient(String serverHost, String authenticationToken) {
		super(serverHost, authenticationToken, new ApiClientRequestQueue());
	}

	public InVehicleDeviceApiClient(String serverHost, String authenticationToken, File backupFile) {
		super(serverHost, authenticationToken,
				new ApiClientRequestQueue(backupFile));
	}

	/**
	 * 到着時のサーバへの通知
	 * 
	 * @param os
	 *            運行スケジュールオブジェクト
	 */
	public int arrivalOperationSchedule(final OperationSchedule os,
			final ApiClientCallback<OperationSchedule> callback) {
		try {
			OperationRecord or = os.getOperationRecord().or(
					new OperationRecord());
			if (!or.getArrivedAt().isPresent()) {
				or.setArrivedAt(new Date());
			}
			OperationRecord retryOr;
			retryOr = or.cloneByJSON();
			retryOr.setArrivedAtOffline(true);

			JSONObject param = new JSONObject();
			JSONObject retryParam = new JSONObject();
			param.put("operation_record", or.toJSONObject());
			retryParam.put("operation_record", retryOr.toJSONObject());

			return put(
					PATH_OPERATION_SCHEDULES + "/" + os.getId() + "/arrival",
					param, retryParam, UNIQUE_GROUP, callback,
					OperationSchedule.RESPONSE_CONVERTER);
		} catch (JSONException e) {
			return handleJSONException(e, callback);
		}
	}

	/**
	 * 出発時のサーバへの通知
	 * 
	 * @param os
	 *            運行スケジュールオブジェクト @
	 */
	public int departureOperationSchedule(final OperationSchedule os,
			final ApiClientCallback<OperationSchedule> callback) {
		try {
			OperationRecord or = os.getOperationRecord().or(
					new OperationRecord());
			if (!or.getDepartedAt().isPresent()) {
				or.setDepartedAt(new Date());
			}
			OperationRecord retryOr = or.cloneByJSON();
			retryOr.setDepartedAtOffline(true);

			JSONObject param = new JSONObject();
			JSONObject retryParam = new JSONObject();
			param.put("operation_record", or.toJSONObject());
			retryParam.put("operation_record", retryOr.toJSONObject());

			return put(PATH_OPERATION_SCHEDULES + "/" + os.getId()
					+ "/departure", param, retryParam, UNIQUE_GROUP, callback,
					OperationSchedule.RESPONSE_CONVERTER);
		} catch (JSONException e) {
			return handleJSONException(e, callback);
		}
	}

	/**
	 * 降車のサーバへの通知
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト @
	 */
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback) {
		try {
			passengerRecord.setGetOffTime(new Date());
			PassengerRecord retryPassengerRecord = passengerRecord
					.cloneByJSON();
			retryPassengerRecord.setGetOffTimeOffline(true);

			String[] filter = new String[] { "id", "payment",
					"passenger_count", "get_off_time", "get_off_time_offline" };

			JSONObject param = new JSONObject();
			JSONObject retryParam = new JSONObject();
			param.put("passenger_record",
					filterJSONKeys(passengerRecord.toJSONObject(), filter));
			retryParam
					.put("passenger_record",
							filterJSONKeys(retryPassengerRecord.toJSONObject(),
									filter));

			String group = getPassengerRecordGetOnOrOffGroup(
					operationSchedule.getId(), reservation.getId(),
					user.getId());

			return put(
					PATH_OPERATION_SCHEDULES + "/" + operationSchedule.getId()
							+ "/reservations/" + reservation.getId()
							+ "/users/" + user.getId() + "/passenger_record",
					param, retryParam, group, callback, VOID_RESPONSE_CONVERTER);
		} catch (JSONException e) {
			return handleJSONException(e, callback);
		}
	}

	/**
	 * 乗車のサーバへの通知
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト @
	 */
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback) {
		try {
			passengerRecord.setGetOnTime(new Date());
			PassengerRecord retryPassengerRecord = passengerRecord
					.cloneByJSON();
			retryPassengerRecord.setGetOnTimeOffline(true);

			JSONObject param = new JSONObject();
			JSONObject retryParam = new JSONObject();

			String[] filter = new String[] { "id", "payment",
					"passenger_count", "get_on_time", "get_on_time_offline" };

			param.put("passenger_record",
					filterJSONKeys(passengerRecord.toJSONObject(), filter));
			retryParam
					.put("passenger_record",
							filterJSONKeys(retryPassengerRecord.toJSONObject(),
									filter));

			String group = getPassengerRecordGetOnOrOffGroup(
					operationSchedule.getId(), reservation.getId(),
					user.getId());

			return put(
					PATH_OPERATION_SCHEDULES + "/" + operationSchedule.getId()
							+ "/reservations/" + reservation.getId()
							+ "/users/" + user.getId() + "/passenger_record",
					param, retryParam, group, callback, VOID_RESPONSE_CONVERTER);
		} catch (JSONException e) {
			return handleJSONException(e, callback);
		}
	}

	/**
	 * 乗車のキャンセル
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト @
	 */
	public int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback) {
		try {
			PassengerRecord passengerRecord = new PassengerRecord();
			passengerRecord.setGetOnTime(new Date());
			passengerRecord.clearGetOffTime();
			JSONObject param = passengerRecord.toJSONObject();
			String group = getPassengerRecordGetOnOrOffGroup(
					operationSchedule.getId(), reservation.getId(),
					user.getId());
			return put(
					PATH_OPERATION_SCHEDULES + "/" + operationSchedule.getId()
							+ "/reservations/" + reservation.getId()
							+ "/users/" + user.getId()
							+ "/passenger_record/canceled", param, group,
					callback, VOID_RESPONSE_CONVERTER);
		} catch (JSONException e) {
			return handleJSONException(e, callback);
		}
	}

	/**
	 * 降車のキャンセル
	 * 
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト @
	 */
	public int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback) {
		try {
			PassengerRecord passengerRecord = new PassengerRecord();
			passengerRecord.clearGetOnTime();
			passengerRecord.setGetOffTime(new Date());
			JSONObject param = passengerRecord.toJSONObject();
			String group = getPassengerRecordGetOnOrOffGroup(
					operationSchedule.getId(), reservation.getId(),
					user.getId());
			return put(
					PATH_OPERATION_SCHEDULES + "/" + operationSchedule.getId()
							+ "/reservations/" + reservation.getId()
							+ "/users/" + user.getId()
							+ "/passenger_record/canceled", param, group,
					callback, VOID_RESPONSE_CONVERTER);
		} catch (JSONException e) {
			return handleJSONException(e, callback);
		}
	}

	/**
	 * 運行情報を取得する
	 */
	public int getOperationSchedules(
			ApiClientCallback<List<OperationSchedule>> callback) {
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
	 * @throws ApiClientException
	 */
	public int getVehicleNotifications(
			ApiClientCallback<List<VehicleNotification>> callback) {
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
	 */
	public int login(InVehicleDevice login,
			final ApiClientCallback<InVehicleDevice> callback) {
		try {
			JSONObject ivd = filterJSONKeys(login.toJSONObject(), new String[] {
					"login", "password" });
			JSONObject param = new JSONObject();
			param.put("in_vehicle_device", ivd);

			return post(PATH_LOGIN, param, UNIQUE_GROUP,
					new ApiClientCallback<InVehicleDevice>() {
						@Override
						public void onException(int reqkey, ApiClientException ex) {
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
								InVehicleDeviceApiClient.this.authenticationToken = authenticationToken;
								Log.d(TAG, "onSucceed : "
										+ InVehicleDeviceApiClient.this.authenticationToken);
								callback.onSucceed(reqkey, statusCode, result);
							}
						}
					}, InVehicleDevice.RESPONSE_CONVERTER);
		} catch (JSONException e) {
			return handleJSONException(e, callback);
		}
	}

	/**
	 * 自車への通知への応答
	 * 
	 * @param vn
	 *            通知オブジェクト
	 * @param response
	 *            応答 @
	 */
	public int responseVehicleNotification(VehicleNotification vn,
			int response, ApiClientCallback<VehicleNotification> callback) {
		try {
			vn.setResponse(response);
			vn.setReadAt(new Date());
			VehicleNotification retryVn = vn.cloneByJSON();
			retryVn.setOffline(true);

			String[] filter = new String[] { "id", "response", "read_at",
					"offline" };

			JSONObject param = new JSONObject();
			JSONObject retryParam = new JSONObject();
			param.put("vehicle_notification",
					filterJSONKeys(vn.toJSONObject(), filter));
			retryParam.put("vehicle_notification",
					filterJSONKeys(retryVn.toJSONObject(), filter));

			return put(PATH_VEHICLE_NOTIFICATIONS + "/" + vn.getId(), param,
					retryParam, UNIQUE_GROUP, callback,
					VehicleNotification.RESPONSE_CONVERTER);
		} catch (JSONException e) {
			return handleJSONException(e, callback);
		}
	}

	/**
	 * 車載器状態の通知
	 */
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			ApiClientCallback<ServiceUnitStatusLog> callback) {
		try {
			log.setOfflineTime(new Date());
			ServiceUnitStatusLog retryLog = log.cloneByJSON();
			retryLog.setOffline(true);

			String[] filter = new String[] { "latitude", "longitude",
					"offline", "offline_time", "orientation", "temperature" };

			JSONObject param = new JSONObject();
			JSONObject retryParam = new JSONObject();
			param.put("service_unit_status_log",
					filterJSONKeys(log.toJSONObject(), filter));
			retryParam.put("service_unit_status_log",
					filterJSONKeys(retryLog.toJSONObject(), filter));

			return post(PATH_SERVICE_UNIT_STATUS_LOGS, param, retryParam,
					"sendServiceUnitStatusLog", callback,
					ServiceUnitStatusLog.RESPONSE_CONVERTER);
		} catch (JSONException e) {
			return handleJSONException(e, callback);
		}
	}

	/**
	 * サービスプロバイダの取得
	 */
	public int getServicePrivider(ApiClientCallback<ServiceProvider> callback) {
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
			ApiClientCallback<Bitmap> callback) {
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

		ApiClientRequest<?> request = new ApiClientRequest<Bitmap>(callback,
				responseConverter, loader);
		addRequest(request);
		return request.getReqKey();
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
	
	public InVehicleDeviceApiClient withSaveOnClose(boolean saveOnClose) {
		return withSaveOnClose(this, saveOnClose);
	}

	public InVehicleDeviceApiClient withSaveOnClose() {
		return withSaveOnClose(true);
	}

	public InVehicleDeviceApiClient withRetry(boolean retry) {
		return withRetry(this, retry);
	}
}
