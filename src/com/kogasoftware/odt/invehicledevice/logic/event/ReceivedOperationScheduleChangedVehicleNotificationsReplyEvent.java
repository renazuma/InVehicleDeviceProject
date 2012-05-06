package com.kogasoftware.odt.invehicledevice.logic.event;

import java.util.List;

import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * TODO: 長すぎ
 */
public class ReceivedOperationScheduleChangedVehicleNotificationsReplyEvent {
	public final List<VehicleNotification> vehicleNotifications;

	public ReceivedOperationScheduleChangedVehicleNotificationsReplyEvent(
			List<VehicleNotification> vehicleNotifications) {
		this.vehicleNotifications = vehicleNotifications;
	}
}
