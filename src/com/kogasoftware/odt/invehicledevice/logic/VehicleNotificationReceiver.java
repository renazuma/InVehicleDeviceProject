package com.kogasoftware.odt.invehicledevice.logic;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceiver implements Runnable {
	private final Logic logic;

	public VehicleNotificationReceiver(final Logic logic) {
		this.logic = logic;
		logic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				if (status.vehicleNotifications.isEmpty()) {
					return;
				}
				logic.showNotificationModalView(new LinkedList<VehicleNotification>(
						status.vehicleNotifications));
				status.vehicleNotifications.clear();
			}
		});
	}

	@Override
	public void run() {
		try {
			final List<VehicleNotification> vehicleNotifications = logic
					.getDataSource().getVehicleNotifications();
			if (vehicleNotifications.isEmpty()) {
				return;
			}
			logic.getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					status.vehicleNotifications.addAll(vehicleNotifications);
				}
			});
			logic.showNotificationModalView(vehicleNotifications);
		} catch (WebAPIException e) {
		}
	}
}