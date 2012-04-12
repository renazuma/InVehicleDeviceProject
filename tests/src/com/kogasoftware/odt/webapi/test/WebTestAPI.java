package com.kogasoftware.odt.webapi.test;

import java.util.List;

import org.json.JSONException;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebTestAPI extends WebAPI {

	protected static final String TEST_SERVER_HOST = "http://192.168.104.63:3333";
	protected static final String TEST_PATH_CLEAN = "/clean";
	protected static final String TEST_PATH_NOTIFICATIONS = "/vehicle_notifications";

	@Override
	protected String getServerHost() {
		return TEST_SERVER_HOST;
	}

	/**
	 * DatabaseCleaner を呼び出してDBを全クリアする
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int cleanDatabase(WebAPICallback<Void> callback) throws WebAPIException {
		return post(TEST_PATH_CLEAN, null, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}	

	/**
	 * 車載器への通知をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllVehicleNotifications(WebAPICallback<List<VehicleNotification>> callback) throws WebAPIException {
		return get(TEST_PATH_NOTIFICATIONS, callback, new ResponseConverter<List<VehicleNotification>>() {
			@Override
			public List<VehicleNotification> convert(byte[] rawResponse)
					throws Exception {
				return VehicleNotification.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 車載器への通知をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getVehicleNotification(int id, WebAPICallback<VehicleNotification> callback) throws WebAPIException {
		return get(TEST_PATH_NOTIFICATIONS + "/" + id, callback, new ResponseConverter<VehicleNotification>() {
			@Override
			public VehicleNotification convert(byte[] rawResponse)
					throws Exception {
				return VehicleNotification.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 車載器への通知を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createVehicleNotification(VehicleNotification obj, WebAPICallback<VehicleNotification> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_NOTIFICATIONS, obj.toJSONObject(), callback, new ResponseConverter<VehicleNotification>() {
			@Override
			public VehicleNotification convert(byte[] rawResponse)
					throws Exception {
				return VehicleNotification.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 車載器への通知をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteVehicleNotification(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_NOTIFICATIONS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}

}
