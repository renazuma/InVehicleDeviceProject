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

	void putReservationTransferredAt(Integer id, Date transferredAt) throws WebAPIException;

	void putVehicleNotificationReadAt(Integer id, Date readAt)
			throws WebAPIException;
}
