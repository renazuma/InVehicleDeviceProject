package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask;

import java.util.List;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.VehicleNotificationLogic;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceiver implements Runnable {
	protected final InVehicleDeviceService service;
	protected final VehicleNotificationLogic vehicleNotificationLogic;

	public VehicleNotificationReceiver(InVehicleDeviceService service) {
		this.service = service;
		this.vehicleNotificationLogic = new VehicleNotificationLogic(service);
	}

	@Override
	public void run() {
		service.getRemoteDataSource()
				.withRetry(false)
				.getVehicleNotifications(
						new WebAPICallback<List<VehicleNotification>>() {
							@Override
							public void onException(int reqkey,
									WebAPIException ex) {
							}

							@Override
							public void onFailed(int reqkey, int statusCode,
									String response) {
							}

							@Override
							public void onSucceed(
									int reqkey,
									int statusCode,
									List<VehicleNotification> vehicleNotifications) {
								vehicleNotificationLogic
										.receiveVehicleNotification(vehicleNotifications);
							}
						});
	}
}
