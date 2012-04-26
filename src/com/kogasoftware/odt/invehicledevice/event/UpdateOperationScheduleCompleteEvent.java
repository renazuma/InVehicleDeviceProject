package com.kogasoftware.odt.invehicledevice.event;

import java.util.List;

import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class UpdateOperationScheduleCompleteEvent {
	public final List<VehicleNotification> vehicleNotifications;

	public UpdateOperationScheduleCompleteEvent(
			List<VehicleNotification> vehicleNotifications) {
		this.vehicleNotifications = vehicleNotifications;
	}
}
