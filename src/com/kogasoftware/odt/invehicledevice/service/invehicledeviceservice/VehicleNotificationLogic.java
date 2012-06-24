package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.webapi.Identifiables;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.model.VehicleNotifications;

/**
 * 通知に関する内部データ処理
 */
public class VehicleNotificationLogic {
	protected final InVehicleDeviceService service;

	public VehicleNotificationLogic(InVehicleDeviceService service) {
		this.service = service;
	}

	public void receiveVehicleNotification(
			List<VehicleNotification> vehicleNotifications) {
		// 古いVehicleNotificationを削除
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				final Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -3); // TODO:定数
				for (VehicleNotification vehicleNotification : new LinkedList<VehicleNotification>(
						localData.repliedVehicleNotifications)) {
					if (vehicleNotification.getCreatedAt().before(
							calendar.getTime())) {
						localData.repliedVehicleNotifications
								.remove(vehicleNotification);
					}
				}
			}
		});
		final List<VehicleNotification> scheduleChangedVehicleNotifications = new LinkedList<VehicleNotification>();
		final List<VehicleNotification> normalVehicleNotifications = new LinkedList<VehicleNotification>();
		for (VehicleNotification vehicleNotification : vehicleNotifications) {
			if (vehicleNotification.getNotificationKind().equals(
					VehicleNotifications.NotificationKind.SCHEDULE_CHANGED)) {
				scheduleChangedVehicleNotifications.add(vehicleNotification);
			} else {
				normalVehicleNotifications.add(vehicleNotification);
			}
		}

		final AtomicBoolean operationScheduleChanged = new AtomicBoolean(false);
		// スケジュール変更通知の処理
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				for (VehicleNotification vehicleNotification : scheduleChangedVehicleNotifications) {
					if (Identifiables.contains(
							localData.repliedVehicleNotifications,
							vehicleNotification)) {
						continue;
					}
					if (Identifiables
							.contains(
									localData.receivedOperationScheduleChangedVehicleNotifications,
									vehicleNotification)) {
						continue;
					}
					if (Identifiables
							.merge(localData.receivingOperationScheduleChangedVehicleNotifications,
									vehicleNotification)) {
						operationScheduleChanged.set(true);
					}
				}
			}
		});
		if (operationScheduleChanged.get()) {
			service.startReceiveUpdatedOperationSchedule();
		}

		// 一般通知の処理
		final AtomicBoolean normalsMerged = new AtomicBoolean(false);
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				for (VehicleNotification vehicleNotification : normalVehicleNotifications) {
					if (Identifiables.contains(
							localData.repliedVehicleNotifications,
							vehicleNotification)) {
						continue;
					}
					if (Identifiables.merge(localData.vehicleNotifications,
							vehicleNotification)) {
						normalsMerged.set(true);
					}
				}
			}
		});
		if (!normalsMerged.get()) {
			return;
		}

		// 一般通知がマージされた場合別スレッドでUIに対して通知処理
		(new Thread() {
			@Override
			public void run() {
				service.alertVehicleNotificationReceive();
				service.speak("管理者から連絡があります");
			}
		}).start();

	}

	public void replyUpdatedOperationScheduleVehicleNotifications(
			final List<VehicleNotification> vehicleNotifications) {
		for (VehicleNotification vehicleNotification : vehicleNotifications) {
			service.getDataSource().responseVehicleNotification(
					vehicleNotification, VehicleNotifications.Response.YES,
					new EmptyWebAPICallback<VehicleNotification>());
		}

		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.receivedOperationScheduleChangedVehicleNotifications
						.removeAll(vehicleNotifications);
				localData.repliedVehicleNotifications
						.addAll(vehicleNotifications);
			}
		});
	}

	/**
	 * VehicleNotificationをReply用リストへ移動
	 */
	public void replyVehicleNotification(
			final VehicleNotification vehicleNotification) {
		for (Integer response : vehicleNotification.getResponse().asSet()) {
			service.getDataSource().responseVehicleNotification(
					vehicleNotification, response,
					new EmptyWebAPICallback<VehicleNotification>());
		}
		final AtomicBoolean empty = new AtomicBoolean(false);
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.vehicleNotifications.remove(vehicleNotification);
				localData.repliedVehicleNotifications.add(vehicleNotification);
				empty.set(localData.vehicleNotifications.isEmpty());
			}
		});
	}
}
