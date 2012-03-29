package com.kogasoftware.odt.invehicledevice;

import java.io.Serializable;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceStatus implements Serializable {
	private static final long serialVersionUID = 5617948505743182174L;
	public final ConcurrentLinkedQueue<VehicleNotification> vehicleNotifications = new ConcurrentLinkedQueue<VehicleNotification>();
	public final ConcurrentLinkedQueue<OperationSchedule> operationSchedules = new ConcurrentLinkedQueue<OperationSchedule>();
	public Integer currentOperationScheduleIndex = 0;

	public enum Status {
		DRIVE, PLATFORM, INITIAL
	};

	public Status status = Status.INITIAL;
}
