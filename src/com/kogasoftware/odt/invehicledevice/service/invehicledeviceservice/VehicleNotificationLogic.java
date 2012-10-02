package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.VoidReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

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
		final List<VehicleNotification> scheduleChangedVehicleNotifications = new LinkedList<VehicleNotification>();
		final List<VehicleNotification> normalVehicleNotifications = new LinkedList<VehicleNotification>();
		for (VehicleNotification vehicleNotification : vehicleNotifications) {
			if (vehicleNotification.getNotificationKind().equals(
					VehicleNotification.NotificationKind.RESERVATION_CHANGED)) {
				scheduleChangedVehicleNotifications.add(vehicleNotification);
			} else {
				normalVehicleNotifications.add(vehicleNotification);
			}
		}

		// スケジュール変更通知の処理
		if (setVehicleNotificationStatus(scheduleChangedVehicleNotifications,
				VehicleNotificationStatus.UNHANDLED)) {
			service.startReceiveUpdatedOperationSchedule();
		}

		// 一般通知の処理
		if (setVehicleNotificationStatus(normalVehicleNotifications,
				VehicleNotificationStatus.UNHANDLED)) {
			// 一般通知がマージされた場合別スレッドでUIに対して通知処理
			(new Thread() {
				@Override
				public void run() {
					service.alertVehicleNotificationReceive();
					service.speak("管理者から連絡があります");
				}
			}).start();
		}
	}

	public void replyUpdatedOperationScheduleVehicleNotifications(
			final List<VehicleNotification> vehicleNotifications) {
		DataSource dataSource = service.getRemoteDataSource();
		for (VehicleNotification vehicleNotification : vehicleNotifications) {
			dataSource.withSaveOnClose().responseVehicleNotification(
					vehicleNotification, VehicleNotification.Response.YES,
					new EmptyWebAPICallback<VehicleNotification>());
		}
		setVehicleNotificationStatus(vehicleNotifications,
				VehicleNotificationStatus.REPLIED);
	}

	/**
	 * VehicleNotificationをReply用リストへ移動
	 */
	public void replyVehicleNotification(
			final VehicleNotification vehicleNotification) {
		DataSource dataSource = service.getRemoteDataSource();
		for (Integer response : vehicleNotification.getResponse().asSet()) {
			dataSource.withSaveOnClose().responseVehicleNotification(
					vehicleNotification, response,
					new EmptyWebAPICallback<VehicleNotification>());
		}
		setVehicleNotificationStatus(vehicleNotification,
				VehicleNotificationStatus.REPLIED);
	}

	public Boolean setVehicleNotificationStatus(
			VehicleNotification vehicleNotification,
			VehicleNotificationStatus status) {
		return setVehicleNotificationStatus(
				Lists.newArrayList(vehicleNotification), status);
	}

	public Boolean setVehicleNotificationStatus(
			final List<VehicleNotification> vehicleNotifications,
			final VehicleNotificationStatus status) {
		final AtomicBoolean updated = new AtomicBoolean(false);
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				updated.set(setVehicleNotificationStatus(
						localData.vehicleNotifications, vehicleNotifications,
						status));
			}
		});
		return updated.get();
	}

	private static Boolean setVehicleNotificationStatus(
			Multimap<VehicleNotificationStatus, VehicleNotification> vehicleNotifications,
			List<VehicleNotification> newVehicleNotifications,
			VehicleNotificationStatus status) {
		Boolean updated = false;
		for (VehicleNotification newVehicleNotification : newVehicleNotifications) {
			Boolean hasNewer = false;
			for (Entry<VehicleNotificationStatus, VehicleNotification> entry : Lists
					.newArrayList(vehicleNotifications.entries())) {
				if (!entry.getValue().getId()
						.equals(newVehicleNotification.getId())) {
					continue;
				}
				if (entry.getKey().ordinal() >= status.ordinal()) {
					hasNewer = true;
				} else {
					vehicleNotifications.remove(entry.getKey(),
							entry.getValue());
				}
			}
			if (!hasNewer) {
				vehicleNotifications.put(status, newVehicleNotification);
				updated = true;
			}
		}
		return updated;
	}

	public List<VehicleNotification> getVehicleNotifications(
			final Integer notificationKind,
			final VehicleNotificationStatus status) {
		final List<VehicleNotification> vehicleNotifications = Lists
				.newLinkedList();
		service.getLocalDataSource().withReadLock(new VoidReader() {
			@Override
			public void read(LocalData localData) {
				for (VehicleNotification vehicleNotification : localData.vehicleNotifications
						.get(status)) {
					if (vehicleNotification.getNotificationKind().equals(notificationKind)) {
						vehicleNotifications.add(vehicleNotification);
					}
				}
			}
		});
		return vehicleNotifications;
	}

	public Multimap<VehicleNotificationStatus, VehicleNotification> getVehicleNotifications() {
		return service
				.getLocalDataSource()
				.withReadLock(
						new Reader<Multimap<VehicleNotificationStatus, VehicleNotification>>() {
							@Override
							public Multimap<VehicleNotificationStatus, VehicleNotification> read(
									LocalData localData) {
								return LinkedHashMultimap
										.create(localData.vehicleNotifications);
							}
						});
	}
}
