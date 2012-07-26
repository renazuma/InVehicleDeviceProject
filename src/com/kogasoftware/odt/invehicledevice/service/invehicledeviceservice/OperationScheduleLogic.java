package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.webapi.model.OperationRecord;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;
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
					DataSource dataSource = service.getRemoteDataSource();
					dataSource.saveOnClose(dataSource
							.departureOperationSchedule(
									operationSchedule,
									new EmptyWebAPICallback<OperationSchedule>()));
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
					DataSource dataSource = service.getRemoteDataSource();
					dataSource.saveOnClose(dataSource.arrivalOperationSchedule(
							operationSchedule,
							new EmptyWebAPICallback<OperationSchedule>()));
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

		// 新規の場合PassengerRecordはすべて削除
		if (!service.isOperationScheduleInitialized()) {
			localData.passengerRecords.clear();
		}

		// 未乗車のPassengerRecordは削除
		for (PassengerRecord passengerRecord : new LinkedList<PassengerRecord>(
				localData.passengerRecords)) {
			if (passengerRecord.isUnhandled()) {
				localData.passengerRecords.remove(passengerRecord);
			}
		}

		// 予約の追加、書き換え
		for (OperationSchedule operationSchedule : newOperationSchedules) {
			operationSchedule.setOperationRecord(operationSchedule
					.getOperationRecord().or(new OperationRecord()));
			for (Reservation reservation : operationSchedule
					.getReservationsAsDeparture()) {
				for (User user : reservation.getFellowUsers()) {
					mergeUpdatedPassengerRecordWithWriteLock(localData,
							reservation, user);
				}
				// 循環参照にならないようにする
				reservation.clearFellowUsers();
				reservation.clearPassengerRecords();
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
	 * 更新されたPassengerRecordを現在のものにマージする処理(LocalDataがロックされた状態内)
	 */
	protected void mergeUpdatedPassengerRecordWithWriteLock(
			LocalData localData, Reservation serverReservation, User serverUser) {
		// サーバーから送られるPassengerRecordを取得
		Boolean passengerRecordReceived = false;
		PassengerRecord serverPassengerRecord = new PassengerRecord();
		for (PassengerRecord passengerRecord : serverReservation
				.getPassengerRecords()) {
			if (passengerRecord.getUserId().isPresent()
					&& passengerRecord.getUserId().get()
							.equals(serverUser.getId())) {
				serverPassengerRecord = passengerRecord;
				passengerRecordReceived = true;
				break;
			}
		}
		serverPassengerRecord.setReservation(serverReservation);
		serverPassengerRecord.setReservationId(serverReservation.getId());
		serverPassengerRecord.setUser(serverUser);
		serverPassengerRecord.setUserId(serverUser.getId());

		// 循環参照にならないようにする
		serverUser.clearPassengerRecords();

		// 乗車済み、降車済みのPassengerRecordの中に既に対応するものが存在する場合、それのPassengerRecordを交換
		for (PassengerRecord localPassengerRecord : new LinkedList<PassengerRecord>(
				localData.passengerRecords)) {
			// IDが一致するかを調べる
			// ただし、localPassengerRecordにはIDが振られていないことがあるため、ReservationとUserのIDの一致を調べる
			if (!localPassengerRecord.getReservationId().isPresent()
					|| !localPassengerRecord.getReservationId().get()
							.equals(serverReservation.getId())) {
				continue;
			}
			if (!localPassengerRecord.getUserId().isPresent()
					|| !localPassengerRecord.getUserId().get()
							.equals(serverUser.getId())) {
				continue;
			}

			// IDが一致した場合

			// サーバーのPassengerRecordが存在しローカルよりも新しい場合、ローカルのものをサーバーのもので置き換え
			if (passengerRecordReceived
					&& serverPassengerRecord.getUpdatedAt().after(
							localPassengerRecord.getUpdatedAt())) {
				localData.passengerRecords.remove(localPassengerRecord);
				localData.passengerRecords.add(serverPassengerRecord);
				return;
			}

			// ReservationとUserはサーバーから送られたものにする
			localPassengerRecord.setReservation(serverReservation);
			localPassengerRecord.setUser(serverUser);
			return;
		}

		// IDが一致したPassengerRecordが存在しない場合、無変更で追加する
		localData.passengerRecords.add(serverPassengerRecord);
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
				localData.passengerRecords.clear();
			}
		});
	}
}
