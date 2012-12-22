package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.thread;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification.NotificationKind;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.VehicleNotificationLogic;

public class OperationScheduleReceiveThread extends Thread implements
		EventDispatcher.OnStartNewOperationListener,
		EventDispatcher.OnStartReceiveUpdatedOperationScheduleListener {
	private static final String TAG = OperationScheduleReceiveThread.class
			.getSimpleName();
	public static final Integer VOICE_DELAY_MILLIS = 5000;
	public static final Integer RETRY_DELAY_MILLIS = 5000;
	public static final Long RECEIVE_INTERVAL_MINUTES = 3L;
	protected final InVehicleDeviceService service;
	protected final OperationScheduleLogic operationScheduleLogic;
	protected final VehicleNotificationLogic vehicleNotificationLogic;
	protected final Semaphore startUpdatedOperationScheduleReceiveSemaphore = new Semaphore(
			0);

	public OperationScheduleReceiveThread(InVehicleDeviceService service) {
		this.service = service;
		operationScheduleLogic = new OperationScheduleLogic(service);
		vehicleNotificationLogic = new VehicleNotificationLogic(service);
	}

	@Override
	public void onStartNewOperation() {
		startNewOperationScheduleReceive();
	}

	@Override
	public void onStartReceiveUpdatedOperationSchedule() {
		startNewOperationScheduleReceive();
	}

	public void receive(
			final List<VehicleNotification> triggerVehicleNotifications) {
		service.getApiClient().getOperationSchedules(
				new ApiClientCallback<List<OperationSchedule>>() {
					@Override
					public void onException(int reqkey, ApiClientException ex) {
						service.getEventDispatcher()
								.dispatchOperationScheduleReceiveFail();
						Uninterruptibles.sleepUninterruptibly(
								RETRY_DELAY_MILLIS, TimeUnit.MILLISECONDS);
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						service.getEventDispatcher()
								.dispatchOperationScheduleReceiveFail();
						Uninterruptibles.sleepUninterruptibly(
								RETRY_DELAY_MILLIS, TimeUnit.MILLISECONDS);
					}

					@Override
					public void onSucceed(int reqkey, int statusCode,
							List<OperationSchedule> operationSchedules) {
						operationScheduleLogic
								.mergeWithWriteLock(operationSchedules,
										triggerVehicleNotifications);
					}
				});
	}

	@Override
	public void run() {
		try {
			// 起動時に一度受信する
			startUpdatedOperationScheduleReceiveSemaphore.release();
			while (true) {
				// スケジュール変更通知があるまで待つ
				if (!startUpdatedOperationScheduleReceiveSemaphore.tryAcquire(
						RECEIVE_INTERVAL_MINUTES, TimeUnit.MINUTES)) {
					Log.i(TAG, "tryAcquire timeout");
				}
				receive(vehicleNotificationLogic.getWithReadLock(
						NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.UNHANDLED));
				Thread.sleep(10 * 1000);
			}
		} catch (InterruptedException e) {
			// 正常終了
		}
	}

	public void startNewOperationScheduleReceive() {
		startUpdatedOperationScheduleReceiveSemaphore.release();
	}
}
