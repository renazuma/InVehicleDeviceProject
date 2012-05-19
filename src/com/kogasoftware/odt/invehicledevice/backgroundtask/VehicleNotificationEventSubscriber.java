package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.logic.event.ReceivedOperationScheduleChangedVehicleNotificationsReplyEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceiveStartEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationReceivedAlertEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationRepliedEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.webapi.Identifiables;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.model.VehicleNotifications;

/**
 * 通知に関する内部データ処理
 */
@UiEventBus.HighPriority
public class VehicleNotificationEventSubscriber {
	private static final String TAG = VehicleNotificationEventSubscriber.class
			.getSimpleName();

	private final CommonLogic commonLogic;
	private final StatusAccess statusAccess;

	public VehicleNotificationEventSubscriber(CommonLogic commonLogic,
			StatusAccess statusAccess) {
		this.commonLogic = commonLogic;
		this.statusAccess = statusAccess;
	}

	/**
	 * 受信したVehicleNotificationを内部にマージ
	 */
	@Subscribe
	public void mergeVehicleNotification(VehicleNotificationReceivedEvent e) {
		// 古いVehicleNotificationを削除
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				final Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DATE, -3); // TODO:定数
				for (VehicleNotification vehicleNotification : new LinkedList<VehicleNotification>(
						status.repliedVehicleNotifications)) {
					if (vehicleNotification.getCreatedAt().before(
							calendar.getTime())) {
						status.repliedVehicleNotifications
								.remove(vehicleNotification);
					}
				}
			}
		});
		final List<VehicleNotification> scheduleChangedVehicleNotifications = new LinkedList<VehicleNotification>();
		final List<VehicleNotification> normalVehicleNotifications = new LinkedList<VehicleNotification>();
		for (VehicleNotification vehicleNotification : e.vehicleNotifications) {
			if (vehicleNotification.getNotificationKind().equals(
					VehicleNotifications.NotificationKind.SCHEDULE_CHANGED)) {
				scheduleChangedVehicleNotifications.add(vehicleNotification);
			} else {
				normalVehicleNotifications.add(vehicleNotification);
			}
		}

		final AtomicBoolean operationScheduleChanged = new AtomicBoolean(false);
		// スケジュール変更通知の処理
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				for (VehicleNotification vehicleNotification : scheduleChangedVehicleNotifications) {
					if (Identifiables.contains(
							status.repliedVehicleNotifications,
							vehicleNotification)) {
						continue;
					}
					if (Identifiables
							.contains(
									status.receivedOperationScheduleChangedVehicleNotifications,
									vehicleNotification)) {
						continue;
					}
					if (Identifiables
							.merge(status.receivingOperationScheduleChangedVehicleNotifications,
									vehicleNotification)) {
						operationScheduleChanged.set(true);
					}
				}
			}
		});
		if (operationScheduleChanged.get()) {
			commonLogic
					.postEvent(new UpdatedOperationScheduleReceiveStartEvent());
		}

		// 一般通知の処理
		final AtomicBoolean normalsMerged = new AtomicBoolean(false);
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				for (VehicleNotification vehicleNotification : normalVehicleNotifications) {
					if (Identifiables.contains(
							status.repliedVehicleNotifications,
							vehicleNotification)) {
						continue;
					}
					if (Identifiables.merge(status.vehicleNotifications,
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
				commonLogic
						.postEvent(new VehicleNotificationReceivedAlertEvent());
				commonLogic.postEvent(new SpeakEvent("管理者から連絡があります"));
				try {
					Thread.sleep(5000);
					commonLogic
							.postEvent(new NotificationModalView.ShowEvent());
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	/**
	 * VehicleNotificationをReply用リストへ移動
	 */
	@Subscribe
	public void setVehicleNotificationReplied(
			final ReceivedOperationScheduleChangedVehicleNotificationsReplyEvent e) {
		for (VehicleNotification vehicleNotification : e.vehicleNotifications) {
			commonLogic.getDataSource().responseVehicleNotification(
					vehicleNotification, VehicleNotifications.Response.YES,
					new EmptyWebAPICallback<VehicleNotification>());
		}

		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.receivedOperationScheduleChangedVehicleNotifications
						.removeAll(e.vehicleNotifications);
				status.repliedVehicleNotifications
						.addAll(e.vehicleNotifications);
			}
		});
	}

	/**
	 * VehicleNotificationをReply用リストへ移動
	 * 未replyのVehicleNotificationが存在する場合はNotificationModalView.ShowEvent送信
	 */
	@Subscribe
	public void setVehicleNotificationReplied(
			final VehicleNotificationRepliedEvent e) {
		for (Integer response : e.vehicleNotification.getResponse().asSet()) {
			commonLogic.getDataSource().responseVehicleNotification(
					e.vehicleNotification, response,
					new EmptyWebAPICallback<VehicleNotification>());
		}
		final AtomicBoolean empty = new AtomicBoolean(false);
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.vehicleNotifications.remove(e.vehicleNotification);
				status.repliedVehicleNotifications.add(e.vehicleNotification);
				empty.set(status.vehicleNotifications.isEmpty());
			}
		});
		if (!empty.get()) {
			commonLogic.postEvent(new NotificationModalView.ShowEvent());
		}
	}

}
