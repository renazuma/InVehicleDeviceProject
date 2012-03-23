package com.kogasoftware.odt.invehicledevice.datasource;

import java.util.Date;
import java.util.List;

import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public interface DataSource {
	InVehicleDevice getInVehicleDevice() throws WebAPIException;

	List<OperationSchedule> getOperationSchedules() throws WebAPIException;

	List<VehicleNotification> getVehicleNotifications() throws WebAPIException;

	void putVehicleNotificationReadAt(Long id, Date readAt)
			throws WebAPIException;
}
