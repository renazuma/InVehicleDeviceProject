package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.CommonLogic;
import com.kogasoftware.odt.invehicledevice.Status;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.Utility;
import com.kogasoftware.odt.invehicledevice.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.event.StartOperationScheduleUpdateEvent;
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
						if (Utility.containsById(
								status.sendLists.repliedVehicleNotifications,
								vehicleNotification)) {
							continue;
						}
						if (vehicleNotification
								.getNotificationType()
								.equals(CommonLogic.VEHICLE_NOTIFICATION_TYPE_SCHEDULE_CHANGED)) {
							if (Utility
									.mergeById(
											status.operationScheduleChangedVehicleNotifications,
											vehicleNotification)) {
								operationScheduleChanged.set(true);
							}
							iterator.remove();
						}
					}
				}
			});

			if (operationScheduleChanged.get()) {
				commonLogic.getEventBus().post(
						new StartOperationScheduleUpdateEvent());
			}

			// 一般通知の処理
			commonLogic.getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					for (VehicleNotification vehicleNotification : vehicleNotifications) {
						if (Utility.containsById(
								status.sendLists.repliedVehicleNotifications,
								vehicleNotification)) {
							continue;
						}
						Utility.mergeById(status.vehicleNotifications,
								vehicleNotification);
					}
				}
			});
			commonLogic.showNotificationModalView(vehicleNotifications);
		} catch (RejectedExecutionException e) {
		} catch (WebAPIException e) {
		}
	}

	@Subscribe
	public void showAllNotificationModalView(
			final CommonLogicLoadCompleteEvent event) {
		event.commonLogic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				if (status.vehicleNotifications.isEmpty()) {
					return;
				}
				event.commonLogic
						.showNotificationModalView(status.vehicleNotifications);
			}
		});
	}
}