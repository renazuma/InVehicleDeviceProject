package com.kogasoftware.odt.invehicledevice.logic;

import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class ScheduleChangedReceiver extends LogicUser implements Runnable {
	@Override
	public void run() {
		if (!getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();
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