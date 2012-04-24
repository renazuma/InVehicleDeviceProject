package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.CommonLogic;
import com.kogasoftware.odt.invehicledevice.Status;
import com.kogasoftware.odt.invehicledevice.Utility;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.event.CommonLogicLoadCompleteEvent;
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

			Boolean scheduleChanged = false;
			for (Iterator<VehicleNotification> iterator = vehicleNotifications
					.iterator(); iterator.hasNext();) {
				VehicleNotification vehicleNotification = iterator.next();
				if (vehicleNotification.getBody().isPresent()
						&& vehicleNotification.getBody().get() == "#schedule_changed") {
					scheduleChanged = true;
					iterator.remove();
				}
			}

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
			if (scheduleChanged) {
				commonLogic.getStatusAccess().write(new Writer() {
					@Override
					public void write(Status status) {
						status.operationScheduleChanged.release();
					}
				});
			}
		} catch (RejectedExecutionException e) {
		} catch (WebAPIException e) {
		}
	}

	@Subscribe
	public void showAllNotificationModalView(final CommonLogicLoadCompleteEvent event) {
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