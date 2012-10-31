package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.scheduledtask;

import java.util.List;

import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.VehicleNotificationLogic;

public class VehicleNotificationReceiver implements Runnable {
	public static final Integer RUN_INTERVAL_MILLIS = 30 * 1000;
	protected final InVehicleDeviceService service;
	protected final VehicleNotificationLogic vehicleNotificationLogic;

	public VehicleNotificationReceiver(InVehicleDeviceService service) {
		this.service = service;
		this.vehicleNotificationLogic = new VehicleNotificationLogic(service);
	}

	@Override
	public void run() {
		service.getApiClient()
				.withRetry(false)
				.getVehicleNotifications(
						new ApiClientCallback<List<VehicleNotification>>() {
							@Override
							public void onException(int reqkey,
									ApiClientException ex) {
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
										.receive(vehicleNotifications);
							}
						});
	}
}
