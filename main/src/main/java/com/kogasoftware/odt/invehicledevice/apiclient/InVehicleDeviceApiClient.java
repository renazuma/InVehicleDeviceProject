package com.kogasoftware.odt.invehicledevice.apiclient;

import java.io.Closeable;
import java.util.List;

import android.graphics.Bitmap;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public interface InVehicleDeviceApiClient extends Closeable {
	/**
	 * 到着時のサーバへの通知
	 *
	 * @param os
	 *            運行スケジュールオブジェクト
	 */
	int arrivalOperationSchedule(final OperationSchedule os,
			final ApiClientCallback<OperationSchedule> callback);

	/**
	 * 出発時のサーバへの通知
	 *
	 * @param os
	 *            運行スケジュールオブジェクト @
	 */
	int departureOperationSchedule(final OperationSchedule os,
			final ApiClientCallback<OperationSchedule> callback);

	/**
	 * 降車のサーバへの通知
	 *
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト @
	 */
	int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback);

	/**
	 * 乗車のサーバへの通知
	 *
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト @
	 */
	int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback);

	/**
	 * 乗車のキャンセル
	 *
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト @
	 */
	int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback);

	/**
	 * 降車のキャンセル
	 *
	 * @param operationSchedule
	 *            運行スケジュールオブジェクト @
	 */
	int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback);

	/**
	 * 運行情報を取得する
	 */
	int getOperationSchedules(
			ApiClientCallback<List<OperationSchedule>> callback);

	/**
	 * 自車への通知を取得
	 *
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	int getVehicleNotifications(
			ApiClientCallback<List<VehicleNotification>> callback);

	/**
	 * OperatorWebへログインしてauthorization_tokenを取得
	 *
	 * @param login
	 *            　ログイン情報(login, password のみ設定必要)
	 * @param callback
	 *            処理完了時のコールバック
	 * @return reqkey
	 */
	int login(InVehicleDevice login,
			final ApiClientCallback<InVehicleDevice> callback);

	/**
	 * 自車への通知への応答
	 *
	 * @param vn
	 *            通知オブジェクト
	 * @param response
	 *            応答 @
	 */
	int responseVehicleNotification(VehicleNotification vn, int response,
			ApiClientCallback<VehicleNotification> callback);

	/**
	 * 車載器状態の通知
	 */
	int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			ApiClientCallback<ServiceUnitStatusLog> callback);

	/**
	 * サービスプロバイダの取得
	 */
	int getServiceProvider(ApiClientCallback<ServiceProvider> callback);

	/**
	 * 接続先を変更する
	 */
	void setServerHost(String serverHost);

	/**
	 * リクエストを中断する
	 */
	void abort(int reqkey);

	/**
	 * 地図画像を取得
	 */
	int getMapTile(LatLng center, Integer zoom,
			ApiClientCallback<Bitmap> callback);

	/**
	 * 動作設定
	 */
	InVehicleDeviceApiClient withSaveOnClose(boolean saveOnClose);
	InVehicleDeviceApiClient withSaveOnClose();
	InVehicleDeviceApiClient withRetry(boolean retry);
}
