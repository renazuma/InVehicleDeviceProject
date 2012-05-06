package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Identifiables;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleAlertEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceiveStartEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceivedEvent;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class OperationScheduleReceiveThread extends Thread {
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
			// 初回のスケジュールの受信
			if (!commonLogic.isOperationScheduleInitialized()) {
				while (true) {
					Thread.sleep(0); // interruption point
					try {
						receive(new LinkedList<VehicleNotification>());
						break;
					} catch (WebAPIException e) {
						e.printStackTrace(); // TODO
					}
				}
			}

			// 初回以降のスケジュールの受信
			while (true) {
				// スケジュール変更通知があるまで待つ
				startUpdatedOperationScheduleReceiveSemaphore.acquire();
				startUpdatedOperationScheduleReceiveSemaphore.drainPermits();

				final List<VehicleNotification> workingVehicleNotification = new LinkedList<VehicleNotification>();
				while (true) {
					try {
						Identifiables
								.merge(workingVehicleNotification,
										commonLogic
												.getReceivingOperationScheduleChangedVehicleNotifications());
						receive(workingVehicleNotification);
						break;
					} catch (WebAPIException e) {
					}
				}
			}
		} catch (InterruptedException e) {
			// 正常終了
		}
	}

	@Subscribe
	public void startUpdatedOperationScheduleReceive(
			UpdatedOperationScheduleReceiveStartEvent e) {
		startUpdatedOperationScheduleReceiveSemaphore.release();
	}
}
