package com.kogasoftware.odt.invehicledevice.logic;

import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class ScheduleChangedReceiver implements Runnable {
	private final Logic logic;

	public ScheduleChangedReceiver(Logic logic) {
		this.logic = logic;
	}

	@Override
	public void run() {
		try {
			final List<VehicleNotification> vehicleNotification = logic
					.getDataSource().getVehicleNotifications();
			logic.getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					status.vehicleNotifications.addAll(vehicleNotification);
				}
			});

		} catch (WebAPIException e) {
		}
	}
}