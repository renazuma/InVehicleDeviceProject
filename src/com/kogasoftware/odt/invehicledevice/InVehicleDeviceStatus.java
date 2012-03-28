package com.kogasoftware.odt.invehicledevice;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class InVehicleDeviceStatus implements Serializable {
	private static final long serialVersionUID = 5617948505743182174L;
	private final LinkedList<OperationSchedule> vehicleNotifications = new LinkedList<OperationSchedule>();
	private final LinkedList<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
	private Integer currentOperationScheduleIndex = 0;
	public enum Status {
		DRIVE, PLATFORM, INITIAL
	};
	private Status status = Status.INITIAL;
	public Status getStatus() {
		return status;
	}

	//public void update(DataSource dataSource) {
	//	vehicleNotifications.addAll(collection);
	//}

	public Boolean hasCurrentOperationSchedule() {
		return currentOperationScheduleIndex < operationSchedules.size();
	}

	public OperationSchedule getCurrentOperationSchedule() {
		return operationSchedules.get(currentOperationScheduleIndex);
	}

	public List<OperationSchedule> getOperationSchedules() {
		return new LinkedList<OperationSchedule>(operationSchedules);
	}

	public Boolean enterPlatformStatus() {
		if (status == Status.PLATFORM) {
			return false;
		}
		status = Status.PLATFORM;
		return true;
	}

	public Boolean enterDriveStatus() {
		if (status == Status.DRIVE) {
			return false;
		}
		status = Status.DRIVE;
		return true;
	}

	public void update(DataSource dataSource) {
		try {
			operationSchedules.addAll(dataSource.getOperationSchedules());
		} catch (WebAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
