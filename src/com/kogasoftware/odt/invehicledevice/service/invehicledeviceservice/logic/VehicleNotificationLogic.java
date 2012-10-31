package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.kogasoftware.odt.apiclient.EmptyApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.empty.EmptyRunnable;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundWriter;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;

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
		if (!setVehicleNotificationStatusWithWriteLock(
				scheduleChangedVehicleNotifications,
				VehicleNotificationStatus.UNHANDLED).isEmpty()) {
			service.getEventDispatcher()
					.dispatchStartReceiveUpdatedOperationSchedule();
		}

		{ // 一般通知の処理
			List<VehicleNotification> updated = setVehicleNotificationStatusWithWriteLock(
					normalVehicleNotifications,
					VehicleNotificationStatus.UNHANDLED);
			if (!updated.isEmpty()) {
				// 一般通知がマージされた場合
				service.getEventDispatcher()
						.dispatchAlertVehicleNotificationReceive(updated);
			}
		}
	}

	/**
	 * VehicleNotificationをReply用リストへ移動
	 */
	public void replyVehicleNotification(
			final VehicleNotification vehicleNotification) {
		replyVehicleNotifications(Lists.newArrayList(vehicleNotification));
		InVehicleDeviceApiClient apiClient = service.getApiClient();
		for (Integer response : vehicleNotification.getResponse().asSet()) {
			apiClient.withSaveOnClose().responseVehicleNotification(
					vehicleNotification, response,
					new EmptyApiClientCallback<VehicleNotification>());
		}
		setVehicleNotificationStatus(Lists.newArrayList(vehicleNotification),
				VehicleNotificationStatus.REPLIED, new EmptyRunnable());
	}

	public List<VehicleNotification> setVehicleNotificationStatusWithWriteLock(
			VehicleNotification vehicleNotification,
			VehicleNotificationStatus status) {
		return setVehicleNotificationStatusWithWriteLock(
				Lists.newArrayList(vehicleNotification), status);
	}

	public void setVehicleNotificationStatus(
			final List<VehicleNotification> vehicleNotifications,
			final VehicleNotificationStatus status, final Runnable onComplete) {
		service.getLocalStorage().write(new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData localData) {
				setVehicleNotificationStatusWithWriteLock(vehicleNotifications,
						status);
			}

			@Override
			public void onWrite() {
				onComplete.run();
			}
		});
	}

	public List<VehicleNotification> setVehicleNotificationStatusWithWriteLock(
			final List<VehicleNotification> vehicleNotifications,
			final VehicleNotificationStatus status) {
		final List<VehicleNotification> updated = Lists.newLinkedList();
		service.getLocalStorage().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				updated.addAll(setVehicleNotificationStatus(
						localData.vehicleNotifications, vehicleNotifications,
						status));
			}
		});
		return updated;
	}

	private static List<VehicleNotification> setVehicleNotificationStatus(
			Multimap<VehicleNotificationStatus, VehicleNotification> vehicleNotifications,
			List<VehicleNotification> newVehicleNotifications,
			VehicleNotificationStatus status) {
		List<VehicleNotification> updated = Lists.newLinkedList();
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
				updated.add(newVehicleNotification);
			}
		}
		return updated;
	}

	public List<VehicleNotification> getVehicleNotificationsWithReadLock(
			final Integer notificationKind,
			final VehicleNotificationStatus status) {
		return service.getLocalStorage().withReadLock(
				new Reader<LinkedList<VehicleNotification>>() {
					@Override
					public LinkedList<VehicleNotification> read(
							LocalData localData) {
						return getVehicleNotifications(notificationKind,
								status, localData.vehicleNotifications);
					}
				});
	}

	public static LinkedList<VehicleNotification> getVehicleNotifications(
			Integer notificationKind,
			VehicleNotificationStatus status,
			Multimap<VehicleNotificationStatus, VehicleNotification> vehicleNotifications) {
		LinkedList<VehicleNotification> result = Lists.newLinkedList();
		for (VehicleNotification vehicleNotification : vehicleNotifications
				.get(status)) {
			if (vehicleNotification.getNotificationKind().equals(
					notificationKind)) {
				result.add(vehicleNotification);
			}
		}
		return result;
	}

	public Multimap<VehicleNotificationStatus, VehicleNotification> getVehicleNotificationsWithReadLock() {
		return service
				.getLocalStorage()
				.withReadLock(
						new Reader<LinkedHashMultimap<VehicleNotificationStatus, VehicleNotification>>() {
							@Override
							public LinkedHashMultimap<VehicleNotificationStatus, VehicleNotification> read(
									LocalData localData) {
								return LinkedHashMultimap
										.create(localData.vehicleNotifications);
							}
						});
	}

	public void replyVehicleNotifications(
			List<VehicleNotification> vehicleNotifications) {
		for (VehicleNotification vehicleNotification : vehicleNotifications) {
			replyVehicleNotification(vehicleNotification);
		}
	}
}