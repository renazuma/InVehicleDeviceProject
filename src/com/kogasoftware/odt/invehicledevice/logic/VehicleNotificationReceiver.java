package com.kogasoftware.odt.invehicledevice.logic;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.Utility;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceiver extends LogicUser implements Runnable {

	@Override
	public void run() {
		if (!getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();
		try {
			final List<VehicleNotification> vehicleNotifications = logic
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

			logic.getStatusAccess().write(new Writer() {
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
			logic.showNotificationModalView(vehicleNotifications);
			if (scheduleChanged) {
				logic.getExecutorService().submit(
						new OperationScheduleReceiver(logic));
			}
		} catch (RejectedExecutionException e) {
		} catch (WebAPIException e) {
		}
	}

	@Subscribe
	public void showAllNotificationModalView(
			final LogicLoadThread.CompleteEvent event) {
		event.logic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				if (status.vehicleNotifications.isEmpty()) {
					return;
				}
				event.logic
						.showNotificationModalView(status.vehicleNotifications);
			}
		});
	}
}