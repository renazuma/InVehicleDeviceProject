package com.kogasoftware.odt.invehicledevice.logic;

import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.Utility;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceiver extends LogicUser implements Runnable {

	@Override
	public void run() {
		if (getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();
		try {
			final List<VehicleNotification> vehicleNotifications = logic
					.getDataSource().getVehicleNotifications();
			if (vehicleNotifications.isEmpty()) {
				return;
			}
			logic.getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					for (VehicleNotification vehicleNotification : vehicleNotifications) {
						if (!Utility.contains(
								status.repliedVehicleNotifications,
								vehicleNotification)) {
							Utility.mergeById(status.vehicleNotifications,
									vehicleNotification);
						}
					}
				}
			});
			logic.showNotificationModalView(vehicleNotifications);
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