package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import android.util.Log;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.NewOperationStartEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleAlertEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceiveStartEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceivedEvent;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class OperationScheduleReceiveThread extends Thread {
	private static final String TAG = OperationScheduleReceiveThread.class
			.getSimpleName();
	private final CommonLogic commonLogic;
	private final Semaphore startUpdatedOperationScheduleReceiveSemaphore = new Semaphore(
			0);

	public OperationScheduleReceiveThread(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	private void receive(
			final List<VehicleNotification> triggerVehicleNotifications)
			throws WebAPIException {
		final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
		operationSchedules.addAll(commonLogic.getDataSource()
				.getOperationSchedules());

		// triggerVehicleNotificationsが存在する場合は、OperationScheduleUpdatedAlertEvent送出
		// 音声通知も行う
		if (!triggerVehicleNotifications.isEmpty()) {
			commonLogic.postEvent(new UpdatedOperationScheduleAlertEvent());
			try {
				Thread.sleep(5000); // TODO 定数
				commonLogic.postEvent(new SpeakEvent("運行予定が変更されました"));
			} catch (InterruptedException e) {
				// 割り込み状態を有効へ戻してから以降の処理を続行する。早めに終了しておきたいため音声再生はしないでおく。
				Thread.currentThread().interrupt();
			}
		}

		commonLogic.postEvent(new UpdatedOperationScheduleReceivedEvent(
				operationSchedules, triggerVehicleNotifications));
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				// スケジュール変更通知があるまで待つ
				startUpdatedOperationScheduleReceiveSemaphore.acquire();
				try {
					receive(commonLogic
							.getReceivingOperationScheduleChangedVehicleNotifications());
				} catch (WebAPIException e) {
					Log.i(TAG, "retry", e);
				}
			}
		} catch (InterruptedException e) {
			// 正常終了
		}
	}

	public void startNewOperationScheduleReceive() {
		startUpdatedOperationScheduleReceiveSemaphore.release();
	}

	@Subscribe
	public void startNewOperationScheduleReceive(
			UpdatedOperationScheduleReceiveStartEvent e) {
		startNewOperationScheduleReceive();
	}

	@Subscribe
	public void startNewOperationScheduleReceive(NewOperationStartEvent e) {
		startNewOperationScheduleReceive();
	}
}
