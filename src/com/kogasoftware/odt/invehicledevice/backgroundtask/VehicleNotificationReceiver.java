package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Identifiables;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.logic.event.StartOperationScheduleUpdateEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceiver implements Runnable {
	private final CommonLogic commonLogic;

	public VehicleNotificationReceiver(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void run() {
		try {
			final List<VehicleNotification> vehicleNotifications = commonLogic
					.getDataSource().getVehicleNotifications();
			if (vehicleNotifications.isEmpty()) {
				return;
			}

			final AtomicBoolean operationScheduleChanged = new AtomicBoolean(
					false);
			// スケジュール変更通知の処理
			commonLogic.getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					for (Iterator<VehicleNotification> iterator = vehicleNotifications
							.iterator(); iterator.hasNext();) {
						VehicleNotification vehicleNotification = iterator
								.next();
						if (!vehicleNotification
								.getNotificationType()
								.equals(VehicleNotifications.NotificationType.SCHEDULE_CHANGED)) {
							continue;
						}
						iterator.remove();
						if (Identifiables.contains(
								status.sendLists.repliedVehicleNotifications,
								vehicleNotification)) {
							continue;
						}
						if (Identifiables
								.contains(
										status.receivedOperationScheduleChangedVehicleNotifications,
										vehicleNotification)) {
							continue;
						}
						if (Identifiables
								.merge(status.receivingOperationScheduleChangedVehicleNotifications,
										vehicleNotification)) {
							operationScheduleChanged.set(true);
						}
					}
				}
			});

			if (operationScheduleChanged.get()) {
				commonLogic.postEvent(new StartOperationScheduleUpdateEvent());
			}

			// 一般通知の処理
			final AtomicBoolean added = new AtomicBoolean(false);
			commonLogic.getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					for (VehicleNotification vehicleNotification : vehicleNotifications) {
						if (Identifiables.contains(
								status.sendLists.repliedVehicleNotifications,
								vehicleNotification)) {
							continue;
						}
						if (Identifiables.merge(status.vehicleNotifications,
								vehicleNotification)) {
							added.set(true);
						}
					}
				}
			});
			if (added.get()) {
				commonLogic.postEvent(new VehicleNotificationReceivedEvent());
				commonLogic.postEvent(new SpeakEvent("管理者から連絡があります"));
				Thread.sleep(5000); // TODO 定数
				commonLogic.postEvent(new NotificationModalView.ShowEvent());
			}
		} catch (RejectedExecutionException e) {
		} catch (WebAPIException e) {
		} catch (InterruptedException e) {
		}
	}
}
