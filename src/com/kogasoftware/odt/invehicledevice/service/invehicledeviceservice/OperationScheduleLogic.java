package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.webapi.model.OperationRecord;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.PassengerRecords;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * スケジュール関連のデータ処理
 */
public class OperationScheduleLogic {
	protected final InVehicleDeviceService service;

	public OperationScheduleLogic(InVehicleDeviceService service) {
		this.service = service;
	}

	/**
	 * 走行フェーズへ移行
	 */
	public void enterDrivePhase() {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				if (localData.remainingOperationSchedules.isEmpty()) {
					service.refreshPhase();
					return;
				}
				OperationSchedule operationSchedule = localData.remainingOperationSchedules
						.get(0);
				if (localData.phase == LocalData.Phase.PLATFORM) {
					localData.remainingOperationSchedules
							.remove(operationSchedule);
					localData.finishedOperationSchedules.add(operationSchedule);
					service.getDataSource().departureOperationSchedule(
							operationSchedule,
							new EmptyWebAPICallback<OperationSchedule>());
				}
				localData.phase = LocalData.Phase.DRIVE;
			}
		});
	}

	/**
	 * 終了フェーズへ移行
	 */
	public void enterFinishPhase() {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.phase = LocalData.Phase.FINISH;
			}
		});
	}

	/**
	 * 乗降場フェーズへ移行
	 */
	public void enterPlatformPhase() {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				if (localData.remainingOperationSchedules.isEmpty()) {
					service.refreshPhase();
					return;
				}
				if (localData.phase == LocalData.Phase.DRIVE) {
					OperationSchedule operationSchedule = localData.remainingOperationSchedules
							.get(0);
					service.getDataSource().arrivalOperationSchedule(
							operationSchedule,
							new EmptyWebAPICallback<OperationSchedule>());
				}
				localData.phase = LocalData.Phase.PLATFORM;
			}
		});
	}

	/**
	 * 更新されたOperationScheduleを現在のものにマージする(LocalDataがロックされた状態内)
	 */
	protected void mergeUpdatedOperationScheduleWithWriteLock(
			LocalData localData, List<OperationSchedule> newOperationSchedules,
			List<VehicleNotification> triggerVehicleNotifications) {
		// 通知を受信済みリストに移動
		localData.receivingOperationScheduleChangedVehicleNotifications
				.removeAll(triggerVehicleNotifications);
		localData.receivedOperationScheduleChangedVehicleNotifications
				.addAll(triggerVehicleNotifications);

		// 新規の場合Reservationは削除
		if (!service.isOperationScheduleInitialized()) {
			localData.reservations.clear();
		}

		// 未乗車のReservationは削除
		for (Reservation reservation : new LinkedList<Reservation>(
				localData.reservations)) {
			if (!reservation.getPassengerRecord().isPresent()) {
				localData.reservations.remove(reservation);
				continue;
			}
			if (PassengerRecords.isUnhandled(reservation)) {
				localData.reservations.remove(reservation);
			}
		}

		// 予約の追加、書き換え
		for (OperationSchedule operationSchedule : newOperationSchedules) {
			operationSchedule.setOperationRecord(operationSchedule
					.getOperationRecord().or(new OperationRecord()));
			for (Reservation reservation : operationSchedule
					.getReservationsAsDeparture()) {
				mergeUpdatedReservationWithWriteLock(localData, reservation);
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
			localData.phase = Phase.FINISH;
		} else {
			Optional<OperationRecord> operationRecord = newRemainingOperationSchedules
					.get(0).getOperationRecord();
			if (!operationRecord.isPresent()) {
				localData.phase = Phase.FINISH;
			} else if (operationRecord.get().getArrivedAt().isPresent()) {
				localData.phase = Phase.PLATFORM;
			} else {
				localData.phase = Phase.DRIVE;
			}
		}

		localData.remainingOperationSchedules.clear();
		localData.remainingOperationSchedules
				.addAll(newRemainingOperationSchedules);
		localData.finishedOperationSchedules.clear();
		localData.finishedOperationSchedules
				.addAll(newFinishedOperationSchedules);

		localData.updatedDate = InVehicleDeviceService.getDate();

		if (service.isOperationScheduleInitialized()) {
			service.mergeUpdatedOperationSchedule(triggerVehicleNotifications);
		} else {
			service.setInitialized();
		}
	}

	/**
	 * 更新されたReservationを現在のものにマージする処理(LocalDataがロックされた状態内)
	 */
	protected void mergeUpdatedReservationWithWriteLock(LocalData localData,
			Reservation serverReservation) {
		// 乗車済み、降車済みのPassengerRecordの中に既に対応するものが存在する場合、それのReservationを交換
		for (Reservation localReservation : new LinkedList<Reservation>(
				localData.reservations)) {
			if (!localReservation.getId().equals(serverReservation.getId())) {
				continue;
			}

			// IDが一致した場合、予約を交換
			localData.reservations.remove(localReservation);
			// 予約の削除
			if (serverReservation.getDeletedAt().isPresent()) {
				return;
			}
			localData.reservations.add(serverReservation);

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
		localData.reservations.add(serverReservation);
	}

	public void receiveUpdatedOperationSchedule(
			final List<OperationSchedule> operationSchedules,
			final List<VehicleNotification> triggerVehicleNotifications) {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				mergeUpdatedOperationScheduleWithWriteLock(localData,
						operationSchedules, triggerVehicleNotifications);
			}
		});
	}

	public void startNewOperation() {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationScheduleInitializedSign.drainPermits();
				localData.remainingOperationSchedules.clear();
				localData.finishedOperationSchedules.clear();
				localData.receivingOperationScheduleChangedVehicleNotifications
						.clear();
				localData.receivedOperationScheduleChangedVehicleNotifications
						.clear();
				localData.phase = Phase.INITIAL;
				localData.reservations.clear();
			}
		});
	}
}
