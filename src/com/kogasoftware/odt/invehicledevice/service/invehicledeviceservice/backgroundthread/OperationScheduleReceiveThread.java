package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.util.Log;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class OperationScheduleReceiveThread extends Thread implements
		InVehicleDeviceService.OnStartNewOperationListener,
		InVehicleDeviceService.OnStartReceiveUpdatedOperationScheduleListener {
	private static final String TAG = OperationScheduleReceiveThread.class
			.getSimpleName();
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

	private void receive(
			final List<VehicleNotification> triggerVehicleNotifications)
			throws WebAPIException {
		final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
		operationSchedules.addAll(service.getRemoteDataSource()
				.getOperationSchedules());

		// triggerVehicleNotificationsが存在する場合は、OperationScheduleUpdatedAlertEvent送出
		// 音声通知も行う
		if (!triggerVehicleNotifications.isEmpty()) {
			service.alertUpdatedOperationSchedule();
			try {
				service.speak("運行予定が変更されました");
				Thread.sleep(5000); // TODO 定数
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		service.receiveUpdatedOperationSchedule(operationSchedules,
				triggerVehicleNotifications);
	}

	@Override
	public void run() {
		try {
			service.addOnStartNewOperationListener(this);
			service.addOnStartReceiveUpdatedOperationScheduleListener(this);
			
			// 最初の一度は必ず受信する
			startUpdatedOperationScheduleReceiveSemaphore.release();
			while (!Thread.currentThread().isInterrupted()) {
				// スケジュール変更通知があるまで待つ
				startUpdatedOperationScheduleReceiveSemaphore.acquire();
				while (true) {
					try {
						receive(service
								.getReceivingOperationScheduleChangedVehicleNotifications());
						break;
					} catch (WebAPIException e) {
						Log.i(TAG, "retry", e);
					}
					Thread.sleep(10 * 1000);
				}
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
