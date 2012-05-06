package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceiver implements Runnable {
	private final CommonLogic commonLogic;

	public VehicleNotificationReceiver(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void run() {
		try {
			final List<VehicleNotification> vehicleNotifications = commonLogic
					.getDataSource().getVehicleNotifications();
			if (vehicleNotifications.isEmpty()) {
				return;
			}
		} catch (WebAPIException e) {
		}
	}
}
