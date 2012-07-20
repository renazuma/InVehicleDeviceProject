package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import java.util.List;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceiver implements Runnable {
	protected final InVehicleDeviceService service;

	public VehicleNotificationReceiver(InVehicleDeviceService service) {
		this.service = service;
	}

	@Override
	public void run() {
		try {
			List<VehicleNotification> vehicleNotifications = service
					.getRemoteDataSource().getVehicleNotifications();
			if (vehicleNotifications.isEmpty()) {
				return;
			}
			service.receiveVehicleNotification(vehicleNotifications);
		} catch (WebAPIException e) {
		}
	}
}
