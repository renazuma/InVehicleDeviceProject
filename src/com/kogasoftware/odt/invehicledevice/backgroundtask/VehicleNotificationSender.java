package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 通知既読情報を送信
 * 
 * @deprecated WebAPIのリトライ機能により不必要になる予定
 */
@Deprecated
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

		for (final VehicleNotification vehicleNotification : repliedVehicleNotifications) {
			if (!vehicleNotification.getResponse().isPresent()) {
				continue;
			}
			commonLogic.getDataSource().responseVehicleNotification(
					vehicleNotification,
					vehicleNotification.getResponse().get(),
					new WebAPICallback<VehicleNotification>() {
						@Override
						public void onException(int reqkey, WebAPIException ex) {
						}

						@Override
						public void onFailed(int reqkey, int statusCode,
								String response) {
						}

						@Override
						public void onSucceed(int reqkey, int statusCode,
								VehicleNotification result) {
							commonLogic.getStatusAccessDeprecated().write(
									new Writer() {
										@Override
										public void write(Status status) {
											status.sendLists.repliedVehicleNotifications
													.remove(vehicleNotification);
										}
									});
						}
					});
		}
	}
}
