package com.kogasoftware.odt.invehicledevice.logic.event;

import java.util.List;

import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class UpdatedOperationScheduleReceivedEvent {
	public final List<OperationSchedule> operationSchedules;
	public final List<VehicleNotification> triggerVehicleNotifications;

	public UpdatedOperationScheduleReceivedEvent(
			List<OperationSchedule> operationSchedules,
			List<VehicleNotification> triggerVehicleNotifications) {
		this.operationSchedules = operationSchedules;
		this.triggerVehicleNotifications = triggerVehicleNotifications;
	}

}
