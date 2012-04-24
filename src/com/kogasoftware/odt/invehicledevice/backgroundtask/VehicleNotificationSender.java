package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.CommonLogic;
import com.kogasoftware.odt.invehicledevice.Status;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationSender implements Runnable {
	private final CommonLogic commonLogic;

	public VehicleNotificationSender(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void run() {
		List<VehicleNotification> repliedVehicleNotifications = commonLogic
				.getStatusAccess().read(
						new Reader<List<VehicleNotification>>() {
							@Override
							public List<VehicleNotification> read(Status status) {
								return new LinkedList<VehicleNotification>(
										status.sendLists.repliedVehicleNotifications);
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
				commonLogic.getDataSource().responseVehicleNotification(
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
								commonLogic.getStatusAccess().write(new Writer() {
									@Override
									public void write(Status status) {
										status.sendLists.repliedVehicleNotifications
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
