package com.kogasoftware.odt.invehicledevice.logic.event;

import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationRepliedEvent {
	public final VehicleNotification vehicleNotification;

	public VehicleNotificationRepliedEvent(
			VehicleNotification vehicleNotification) {
		this.vehicleNotification = vehicleNotification;
	}
}
