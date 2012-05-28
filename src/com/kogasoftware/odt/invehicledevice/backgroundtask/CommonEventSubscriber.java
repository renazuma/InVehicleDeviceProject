package com.kogasoftware.odt.invehicledevice.backgroundtask;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.Status.Phase;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OperationScheduleInitializedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.TemperatureChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleMergedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceivedEvent;
import com.kogasoftware.odt.webapi.model.OperationRecord;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.PassengerRecords;
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
					commonLogic.getDataSource().departureOperationSchedule(
							operationSchedule,
							new EmptyWebAPICallback<OperationSchedule>());
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
				if (status.remainingOperationSchedules.isEmpty()) {
					commonLogic.postEvent(new EnterFinishPhaseEvent());
					return;
				}
				if (status.phase == Status.Phase.DRIVE) {
					OperationSchedule operationSchedule = status.remainingOperationSchedules
							.get(0);
					commonLogic.getDataSource().arrivalOperationSchedule(
						operationSchedule,
						new EmptyWebAPICallback<OperationSchedule>());
				}
				status.phase = Status.Phase.PLATFORM;
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

		// 新規の場合Reservationは削除
		if (!commonLogic.isOperationScheduleInitialized()) {
			status.reservations.clear();
		}

		// 未乗車のReservationは削除
		for (Reservation reservation : new LinkedList<Reservation>(
				status.reservations)) {
			if (!reservation.getPassengerRecord().isPresent()) {
				status.reservations.remove(reservation);
				continue;
			}
			if (PassengerRecords.isUnhandled(reservation)) {
				status.reservations.remove(reservation);
			}
		}

		// 予約の追加、書き換え
		for (OperationSchedule operationSchedule : newOperationSchedules) {
			operationSchedule.setOperationRecord(operationSchedule
					.getOperationRecord().or(new OperationRecord()));
			for (Reservation reservation : operationSchedule
					.getReservationsAsDeparture()) {
				mergeUpdatedReservationOnWriteLock(status, reservation);
				// マージ完了後もPassengerRecordが存在しない予約には空のPassengerRecordを割り当て
				reservation.setPassengerRecord(reservation.getPassengerRecord()
						.or(new PassengerRecord()));
			}
		}

		// OperationScheduleの再生
		LinkedList<OperationSchedule> newRemainingOperationSchedules = new LinkedList<OperationSchedule>(
				newOperationSchedules);
		List<OperationSchedule> newFinishedOperationSchedules = new LinkedList<OperationSchedule>();
		for (OperationSchedule operationSchedule : new LinkedList<OperationSchedule>(
				newRemainingOperationSchedules)) {
			if (!operationSchedule.getOperationRecord().isPresent()) {
				break;
			}
			OperationRecord operationRecord = operationSchedule
					.getOperationRecord().get();
			if (!operationRecord.getDepartedAt().isPresent()) {
				break;
			}
			newFinishedOperationSchedules.add(operationSchedule);
			newRemainingOperationSchedules.remove(operationSchedule);
		}

		if (newRemainingOperationSchedules.isEmpty()) {
			status.phase = Phase.FINISH;
		} else {
			Optional<OperationRecord> operationRecord = newRemainingOperationSchedules
					.get(0).getOperationRecord();
			if (!operationRecord.isPresent()) {
				status.phase = Phase.FINISH;
			} else if (operationRecord.get().getArrivedAt().isPresent()) {
				status.phase = Phase.PLATFORM;
			} else {
				status.phase = Phase.DRIVE;
			}
		}

		status.remainingOperationSchedules.clear();
		status.remainingOperationSchedules
				.addAll(newRemainingOperationSchedules);
		status.finishedOperationSchedules.clear();
		status.finishedOperationSchedules.addAll(newFinishedOperationSchedules);

		if (commonLogic.isOperationScheduleInitialized()) {
			commonLogic.postEvent(new UpdatedOperationScheduleMergedEvent());
		} else {
			commonLogic.postEvent(new OperationScheduleInitializedEvent());
		}
	}

	/**
	 * 更新されたReservationを現在のものにマージする処理(Statusがロックされた状態内)
	 */
	private void mergeUpdatedReservationOnWriteLock(Status status,
			Reservation serverReservation) {
		// 乗車済み、降車済みのPassengerRecordの中に既に対応するものが存在する場合、それのReservationを交換
		for (Reservation localReservation : new LinkedList<Reservation>(
				status.reservations)) {
			if (!localReservation.getId().equals(serverReservation.getId())) {
				continue;
			}

			// IDが一致した場合、予約を交換
			status.reservations.remove(localReservation);
			// 予約の削除
			if (serverReservation.getDeletedAt().isPresent()) {
				return;
			}
			status.reservations.add(serverReservation);

			// 新しい乗車実績を反映
			if (!localReservation.getPassengerRecord().isPresent()) {
				return;
			}
			if (!serverReservation.getPassengerRecord().isPresent()) {
				serverReservation.setPassengerRecord(localReservation
						.getPassengerRecord());
				return;
			}
			Date serverUpdatedAt = serverReservation.getPassengerRecord().get()
					.getUpdatedAt();
			Date localUpdatedAt = localReservation.getPassengerRecord().get()
					.getUpdatedAt();
			if (serverUpdatedAt.before(localUpdatedAt)) {
				serverReservation.setPassengerRecord(localReservation
						.getPassengerRecord());
			}
			return;
		}

		// IDが一致した予約が存在しない場合、無変更で追加する
		status.reservations.add(serverReservation);
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
	 * 温度を保存
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
