package com.kogasoftware.odt.invehicledevice.logic.event;

import java.util.List;

import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceivedEvent {
	public final List<VehicleNotification> vehicleNotifications;

	public VehicleNotificationReceivedEvent(
			List<VehicleNotification> vehicleNotifications) {
		this.vehicleNotifications = vehicleNotifications;
	}
}
