package com.kogasoftware.odt.invehicledevice.logic;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.Utility;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationReceiver extends LogicUser implements Runnable {

	@Override
	public void run() {
		if (getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();
		try {
			final List<VehicleNotification> vehicleNotifications = logic
					.getDataSource().getVehicleNotifications();
			if (vehicleNotifications.isEmpty()) {
				return;
			}
			final AtomicBoolean updateOperationSchedule = new AtomicBoolean(
					false);
			logic.getStatusAccess().write(new Writer() {
				@Override
				public void write(Status status) {
					for (VehicleNotification vehicleNotification : vehicleNotifications) {
						if (Utility.contains(
								status.sendLists.repliedVehicleNotifications,
								vehicleNotification)) {
							continue;
						}
						Utility.mergeById(status.vehicleNotifications,
								vehicleNotification);
						// if (false) {
						// updateOperationSchedule.set(true);
						// }
					}
				}
			});
			logic.showNotificationModalView(vehicleNotifications);
			if (updateOperationSchedule.get()) {
				logic.getExecutorService().submit(
						new OperationScheduleReceiver(logic));
			}
		} catch (RejectedExecutionException e) {
		} catch (WebAPIException e) {
		}
	}

	@Subscribe
	public void showAllNotificationModalView(
			final LogicLoadThread.CompleteEvent event) {
		event.logic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				if (status.vehicleNotifications.isEmpty()) {
					return;
				}
				event.logic
						.showNotificationModalView(status.vehicleNotifications);
			}
		});
	}
}