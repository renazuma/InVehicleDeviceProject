package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.model.VehicleNotification.NotificationKind;

public class OperationScheduleReceiveThread extends Thread implements
		InVehicleDeviceService.OnStartNewOperationListener,
		InVehicleDeviceService.OnStartReceiveUpdatedOperationScheduleListener {
	public static final Integer VOICE_DELAY_MILLIS = 5000;
	public static final Integer RETRY_DELAY_MILLIS = 5000;
	protected final InVehicleDeviceService service;
	protected final Semaphore startUpdatedOperationScheduleReceiveSemaphore = new Semaphore(
			0);

	public OperationScheduleReceiveThread(InVehicleDeviceService service) {
		this.service = service;
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
		service.getRemoteDataSource().getOperationSchedules(
				new WebAPICallback<List<OperationSchedule>>() {
					@Override
					public void onException(int reqkey, WebAPIException ex) {
						service.notifyOperationScheduleReceiveFailed();
						Uninterruptibles.sleepUninterruptibly(
								RETRY_DELAY_MILLIS, TimeUnit.MILLISECONDS);
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						service.notifyOperationScheduleReceiveFailed();
						Uninterruptibles.sleepUninterruptibly(
								RETRY_DELAY_MILLIS, TimeUnit.MILLISECONDS);
					}

					@Override
					public void onSucceed(int reqkey, int statusCode,
							List<OperationSchedule> operationSchedules) {
						if (!triggerVehicleNotifications.isEmpty()) {
							service.alertUpdatedOperationSchedule();
							try {
								service.speak("運行予定が変更されました");
								Thread.sleep(VOICE_DELAY_MILLIS);
							} catch (InterruptedException e) {
								Thread.currentThread().interrupt();
							}
						}
						service.mergeOperationSchedules(operationSchedules,
								triggerVehicleNotifications);
					}
				});
	}

	@Override
	public void run() {
		try {
			service.addOnStartNewOperationListener(this);
			service.addOnStartReceiveUpdatedOperationScheduleListener(this);

			// 最初の一度は必ず受信する
			startUpdatedOperationScheduleReceiveSemaphore.release();
			while (true) {
				// スケジュール変更通知があるまで待つ
				startUpdatedOperationScheduleReceiveSemaphore.acquire();
				receive(service.getVehicleNotifications(
						NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.UNHANDLED));
				Thread.sleep(10 * 1000);
			}
		} catch (InterruptedException e) {
			// 正常終了
		} finally {
			service.removeOnStartNewOperationListener(this);
			service.removeOnStartReceiveUpdatedOperationScheduleListener(this);
		}
	}

	public void startNewOperationScheduleReceive() {
		startUpdatedOperationScheduleReceiveSemaphore.release();
	}
}
