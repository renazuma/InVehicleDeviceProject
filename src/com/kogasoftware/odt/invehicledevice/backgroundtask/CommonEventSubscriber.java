package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Identifiables;
import com.kogasoftware.odt.invehicledevice.logic.ServiceUnitStatusLogs;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OperationScheduleInitializedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseCancelledEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.ReceivedOperationScheduleChangedVehicleNotificationsReplyEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.SelectedPassengerRecordsUpdateEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.TemperatureChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleMergedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceiveStartEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationReceivedAlertEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationRepliedEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 共通の内部データ処理
 */
@UiEventBus.HighPriority
public class CommonEventSubscriber {
	private final CommonLogic commonLogic;
	private final StatusAccess statusAccess;

	public CommonEventSubscriber(CommonLogic commonLogic,
			StatusAccess statusAccess) {
		this.commonLogic = commonLogic;
		this.statusAccess = statusAccess;
	}

	/**
	 * 停止状態をキャンセル
	 */
	@Subscribe
	public void cancelPause(PauseCancelledEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog
						.setStatus(ServiceUnitStatusLogs.Status.OPERATION);
			}
		});
	}

	/**
	 * 走行フェーズへ移行
	 */
	@Subscribe
	public void enterDrivePhase(EnterDrivePhaseEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				if (status.remainingOperationSchedules.isEmpty()) {
					commonLogic.postEvent(new EnterFinishPhaseEvent());
					return;
				}
				OperationSchedule operationSchedule = status.remainingOperationSchedules
						.get(0);
				if (status.phase == Status.Phase.PLATFORM) {
					status.remainingOperationSchedules
							.remove(operationSchedule);
					status.finishedOperationSchedules.add(operationSchedule);
					Identifiables.merge(
							status.sendLists.departureOperationSchedules,
							operationSchedule);
				}
				status.phase = Status.Phase.DRIVE;
			}
		});
	}

	/**
	 * 終了フェーズへ移行
	 */
	@Subscribe
	public void enterFinishPhase(EnterFinishPhaseEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.phase = Status.Phase.FINISH;
			}
		});
	}

	/**
	 * 乗降場フェーズへ移行
	 */
	@Subscribe
	public void enterPlatformPhase(EnterPlatformPhaseEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.phase = Status.Phase.PLATFORM;
				if (status.remainingOperationSchedules.isEmpty()) {
					commonLogic.postEvent(new EnterFinishPhaseEvent());
					return;
				}
				OperationSchedule operationSchedule = status.remainingOperationSchedules
						.get(0);
				Identifiables.merge(status.sendLists.arrivalOperationSchedules,
						operationSchedule);
			}
		});
	}

	/**
	 * 更新されたOperationScheduleを現在のものにマージする(Statusがロックされた状態内)
	 */
	private void mergeUpdatedOperationScheduleOnWriteLock(Status status,
			List<OperationSchedule> newOperationSchedules,
			List<VehicleNotification> triggerVehicleNotifications) {
		// 通知を受信済みリストに移動
		status.receivingOperationScheduleChangedVehicleNotifications
				.removeAll(triggerVehicleNotifications);
		status.receivedOperationScheduleChangedVehicleNotifications
				.addAll(triggerVehicleNotifications);

		// 飛び乗り予約以外の未乗車のPassengerRecordは削除
		status.unhandledPassengerRecords
				.retainAll(status.unexpectedPassengerRecords);

		// 予約の追加、書き換え
		for (OperationSchedule operationSchedule : newOperationSchedules) {
			for (Reservation reservation : operationSchedule
					.getReservationsAsDeparture()) {
				mergeUpdatedReservationOnWriteLock(status, reservation);
			}
		}

		// 飛び乗り予約はリストの後ろに移動
		for (PassengerRecord passengerRecord : new LinkedList<PassengerRecord>(
				status.unhandledPassengerRecords)) {
			if (status.unexpectedPassengerRecords.contains(passengerRecord)) {
				status.unhandledPassengerRecords.remove(passengerRecord);
				status.unhandledPassengerRecords.add(passengerRecord);
			}
		}

		// 選択状態のPassengerRecordの更新
		List<PassengerRecord> newSelectedPassengerRecords = new LinkedList<PassengerRecord>();
		List<PassengerRecord> selectablePassengerRecords = new LinkedList<PassengerRecord>();
		selectablePassengerRecords.addAll(status.unhandledPassengerRecords);
		selectablePassengerRecords.addAll(status.ridingPassengerRecords);
		for (PassengerRecord selectedPassengerRecord : status.selectedPassengerRecords) {
			if (!selectedPassengerRecord.getReservation().isPresent()) {
				continue;
			}
			Reservation selectedReservation = selectedPassengerRecord
					.getReservation().get();
			// 飛び乗り予約の場合は選択状態のまま
			if (commonLogic
					.isUnexpectedPassengerRecord(selectedPassengerRecord)) {
				newSelectedPassengerRecords.add(selectedPassengerRecord);
			}
			// IDが一致する予約が存在する場合は選択状態のまま
			for (PassengerRecord selectablePassengerRecord : selectablePassengerRecords) {
				if (!selectablePassengerRecord.getReservation().isPresent()) {
					continue;
				}
				Reservation selectableReservation = selectablePassengerRecord
						.getReservation().get();
				if (selectableReservation.getId().equals(
						selectedReservation.getId())) {
					newSelectedPassengerRecords.add(selectablePassengerRecord);
				}
			}
		}
		status.selectedPassengerRecords.clear();
		status.selectedPassengerRecords.addAll(newSelectedPassengerRecords);

		// 新規の場合はスケジュールを全て交換して終了
		if (!commonLogic.isOperationScheduleInitialized()) {
			status.remainingOperationSchedules.clear();
			status.finishedOperationSchedules.clear();
			status.remainingOperationSchedules.addAll(newOperationSchedules);
			commonLogic.postEvent(new OperationScheduleInitializedEvent());
			return;
		}

		// OperationScheduleの巻き戻し
		LinkedList<OperationSchedule> newRemainingOperationSchedules = new LinkedList<OperationSchedule>(
				newOperationSchedules);
		List<OperationSchedule> newFinishedOperationSchedules = new LinkedList<OperationSchedule>();
		for (OperationSchedule finishedOperationSchedule : status.finishedOperationSchedules) {
			if (newRemainingOperationSchedules.isEmpty()) {
				break;
			}
			if (!newRemainingOperationSchedules.get(0).getId()
					.equals(finishedOperationSchedule.getId())) {
				break;
			}
			newFinishedOperationSchedules.add(newRemainingOperationSchedules
					.pop());
		}

		// 現在乗降場待機画面で、現在のOperationScheduleが無効になった場合強制的に運行中に設定
		if (status.phase == Status.Phase.PLATFORM
				&& !status.remainingOperationSchedules.isEmpty()
				&& !Identifiables.contains(newRemainingOperationSchedules,
						status.remainingOperationSchedules.get(0))) {
			status.phase = Status.Phase.DRIVE;
		}

		// 現在運行終了状態で、新しい運行スケジュールが存在する場合は強制的に運行中に設定
		if (status.phase == Status.Phase.FINISH
				&& !newRemainingOperationSchedules.isEmpty()) {
			status.phase = Status.Phase.DRIVE;
		}

		status.remainingOperationSchedules.clear();
		status.remainingOperationSchedules
				.addAll(newRemainingOperationSchedules);
		status.finishedOperationSchedules.clear();
		status.finishedOperationSchedules.addAll(newFinishedOperationSchedules);

		commonLogic.postEvent(new UpdatedOperationScheduleMergedEvent());
	}

	/**
	 * 更新されたReservationを現在のものにマージする処理(Statusがロックされた状態内)
	 */
	private void mergeUpdatedReservationOnWriteLock(Status status,
			Reservation reservation) {
		// Reservationに対応するPassengerRecordを新規作成し、未乗車のものと交換
		// 乗車済み、降車済みのPassengerRecordの中に既に対応するものが存在する場合、それのReservationを交換
		for (PassengerRecord passengerRecord : status.ridingPassengerRecords) {
			if (passengerRecord.getReservation().isPresent()) {
				if (passengerRecord.getReservation().get().getId()
						.equals(reservation.getId())) {
					passengerRecord.setReservation(reservation);
					return;
				}
			}
		}
		for (PassengerRecord passengerRecord : status.finishedPassengerRecords) {
			if (passengerRecord.getReservation().isPresent()) {
				if (passengerRecord.getReservation().get().getId()
						.equals(reservation.getId())) {
					passengerRecord.setReservation(reservation);
					return;
				}
			}
		}
		PassengerRecord passengerRecord = new PassengerRecord();
		passengerRecord.setReservation(reservation);
		passengerRecord.setPassengerCount(reservation.getPassengerCount());
		status.unhandledPassengerRecords.add(passengerRecord);
	}

	/**
	 * 受信したVehicleNotificationを内部にマージ
	 */
	@Subscribe
	public void mergeVehicleNotification(VehicleNotificationReceivedEvent e) {

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
							status.sendLists.repliedVehicleNotifications,
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
							status.sendLists.repliedVehicleNotifications,
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
	 * 停止状態へ移行
	 */
	@Subscribe
	public void pause(PauseEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog
						.setStatus(ServiceUnitStatusLogs.Status.PAUSE);
			}
		});
	}

	@Subscribe
	public void restoreStatus(UpdatedOperationScheduleMergedEvent event) {
		commonLogic.restoreStatus();
	}

	/**
	 * 位置情報を保存
	 */
	@Subscribe
	public void setLocation(final LocationReceivedEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = true;
				status.serviceUnitStatusLog.setLatitude(new BigDecimal(
						e.location.getLatitude()));
				status.serviceUnitStatusLog.setLongitude(new BigDecimal(
						e.location.getLongitude()));
			}
		});
	}

	/**
	 * OperationScheduleが初期化されたということを保存
	 */
	@Subscribe
	public void setOperationScheduleInitialized(
			OperationScheduleInitializedEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.operationScheduleInitializedSign.release();
			}
		});
	}

	/**
	 * 方向情報を保存
	 */
	@Subscribe
	public void setOrientation(final OrientationChangedEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog.setOrientation(e.orientationDegree
						.intValue());
			}
		});
	}

	/**
	 * 選択済みのPassengerRecordのReservationIdを保存
	 */
	@Subscribe
	public void setSelectedPassengerRecords(
			final SelectedPassengerRecordsUpdateEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.selectedPassengerRecords.clear();
				status.selectedPassengerRecords
						.addAll(e.selectedPassengerRecords);
			}
		});
	}

	/**
	 * 温度を保存する
	 */
	@Subscribe
	public void setTemperature(final TemperatureChangedEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog.setTemperature(e.celciusTemperature
						.intValue());
			}
		});
	}

	/**
	 * VehicleNotificationをReply用リストへ移動
	 */
	@Subscribe
	public void setVehicleNotificationReplied(
			final ReceivedOperationScheduleChangedVehicleNotificationsReplyEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.receivedOperationScheduleChangedVehicleNotifications
						.removeAll(e.vehicleNotifications);
				status.sendLists.repliedVehicleNotifications
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
		final AtomicBoolean empty = new AtomicBoolean(false);
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.vehicleNotifications.remove(e.vehicleNotification);
				status.sendLists.repliedVehicleNotifications
						.add(e.vehicleNotification);
				empty.set(status.vehicleNotifications.isEmpty());
			}
		});
		if (!empty.get()) {
			commonLogic.postEvent(new NotificationModalView.ShowEvent());
		}
	}

	/**
	 * 中止状態へ移行
	 */
	@Subscribe
	public void stop(StopEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog
						.setStatus(ServiceUnitStatusLogs.Status.STOP);
			}
		});
	}

	/**
	 * 更新されたOperationScheduleを現在のものにマージする
	 */
	@Subscribe
	public void updateOperationSchedule(
			final UpdatedOperationScheduleReceivedEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				mergeUpdatedOperationScheduleOnWriteLock(status,
						e.operationSchedules, e.triggerVehicleNotifications);
			}
		});
	}
}
