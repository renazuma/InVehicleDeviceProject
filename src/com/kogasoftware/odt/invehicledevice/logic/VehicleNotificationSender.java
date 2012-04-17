package com.kogasoftware.odt.invehicledevice.logic;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationSender implements Runnable {
	private final Logic logic;

	public VehicleNotificationSender(final Logic logic) {
		this.logic = logic;
	}

	@Override
	public void run() {
		List<VehicleNotification> repliedVehicleNotifications = logic
				.getStatusAccess().read(
						new Reader<List<VehicleNotification>>() {
							@Override
							public List<VehicleNotification> read(Status status) {
								return new LinkedList<VehicleNotification>(
										status.repliedVehicleNotifications);
							}
						});
		if (repliedVehicleNotifications.isEmpty()) {
			return;
		}
		try {
			for (final VehicleNotification vehicleNotification : repliedVehicleNotifications) {
				if (!vehicleNotification.getResponse().isPresent()) {
					continue;
				}
				logic.getDataSource().responseVehicleNotification(
						vehicleNotification,
						vehicleNotification.getResponse().get(),
						new WebAPICallback<VehicleNotification>() {
							@Override
							public void onException(int reqkey,
									WebAPIException ex) {
							}

							@Override
							public void onFailed(int reqkey, int statusCode,
									String response) {
							}

							@Override
							public void onSucceed(int reqkey, int statusCode,
									VehicleNotification result) {
								logic.getStatusAccess().write(new Writer() {
									@Override
									public void write(Status status) {
										status.repliedVehicleNotifications
												.remove(vehicleNotification);
									}
								});
							}
						});
			}

		} catch (WebAPIException e) {
		}
	}
}
