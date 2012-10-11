package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Reader;
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
	protected final VehicleNotificationLogic vehicleNotificationLogic;

	public OperationScheduleLogic(InVehicleDeviceService service) {
		this.service = service;
		vehicleNotificationLogic = new VehicleNotificationLogic(service);
	}

	/**
	 * 走行フェーズへ移行
	 */
	public void enterDrivePhase() {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				List<OperationSchedule> remainingOperationSchedules = getRemainingOperationSchedules();
				if (remainingOperationSchedules.isEmpty()) {
					service.getEventDispatcher().dispatchEnterFinishPhase();
					return;
				}
				if (localData.phase == LocalData.Phase.PLATFORM) {
					depart();
				}
				localData.phase = LocalData.Phase.DRIVE;
			}
		});
		service.getEventDispatcher().dispatchEnterDrivePhase();
	}

	/**
	 * 終了フェーズへ移行
	 */
	public void enterFinishPhase() {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				depart();
				localData.phase = LocalData.Phase.FINISH;
			}
		});
		service.getEventDispatcher().dispatchEnterFinishPhase();
	}

	/**
	 * 乗降場フェーズへ移行
	 */
	public void enterPlatformPhase() {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				List<OperationSchedule> remainingOperationSchedules = getRemainingOperationSchedules();
				if (remainingOperationSchedules.isEmpty()) {
					service.getEventDispatcher().dispatchEnterFinishPhase();
					return;
				}
				if (localData.phase == LocalData.Phase.DRIVE) {
					arrive();
				}
				localData.phase = LocalData.Phase.PLATFORM;
			}
		});
		service.getEventDispatcher().dispatchEnterPlatformPhase();
	}

	private void arrive() {
		for (OperationSchedule operationSchedule : getCurrentOperationSchedule()
				.asSet()) {
			OperationRecord operationRecord = operationSchedule
					.getOperationRecord().or(new OperationRecord());
			operationRecord.setArrivedAt(InVehicleDeviceService.getDate());
			operationSchedule.setOperationRecord(operationRecord);
			DataSource dataSource = service.getRemoteDataSource();
			dataSource.withSaveOnClose().arrivalOperationSchedule(
					operationSchedule,
					new EmptyWebAPICallback<OperationSchedule>());
		}
	}

	private void depart() {
		for (OperationSchedule operationSchedule : getCurrentOperationSchedule()
				.asSet()) {
			OperationRecord operationRecord = operationSchedule
					.getOperationRecord().or(new OperationRecord());
			operationRecord.setDepartedAt(InVehicleDeviceService.getDate());
			operationSchedule.setOperationRecord(operationRecord);
			DataSource dataSource = service.getRemoteDataSource();
			dataSource.withSaveOnClose().departureOperationSchedule(
					operationSchedule,
					new EmptyWebAPICallback<OperationSchedule>());
		}
	}

	/**
	 * OperationScheduleをマージする(LocalDataがロックされた状態内)
	 */
	public void mergeOperationSchedulesWithWriteLock(LocalData localData,
			List<OperationSchedule> newOperationSchedules) {
		// 新規の場合PassengerRecordはすべて削除
		if (!service.isOperationInitialized()) {
			localData.passengerRecords.clear();
		}

		// OperationRecordのマージ
		for (Integer i = 0; i < localData.operationSchedules.size(); ++i) {
			if (i >= newOperationSchedules.size()) {
				break;
			}
			OperationSchedule remoteOperationSchedule = newOperationSchedules
					.get(i);
			OperationSchedule localOperationSchedule = localData.operationSchedules
					.get(i);
			if (!remoteOperationSchedule.getId().equals(
					localOperationSchedule.getId())) {
				break;
			}
			if (!localOperationSchedule.getOperationRecord().isPresent()) {
				continue;
			} else if (!remoteOperationSchedule.getOperationRecord()
					.isPresent()) {
				remoteOperationSchedule
						.setOperationRecord(localOperationSchedule
								.getOperationRecord());
				continue;
			}
			OperationRecord localOperationRecord = localOperationSchedule
					.getOperationRecord().get();
			OperationRecord remoteOperationRecord = remoteOperationSchedule
					.getOperationRecord().or(new OperationRecord());
			OperationRecord newOperationRecord = localOperationRecord
					.getUpdatedAt().after(remoteOperationRecord.getUpdatedAt()) ? localOperationRecord
					: remoteOperationRecord;
			remoteOperationSchedule.setOperationRecord(newOperationRecord);
		}

		// OperationRecordが、マージ後に存在しない場合は新規作成
		for (OperationSchedule operationSchedule : newOperationSchedules) {
			if (!operationSchedule.getOperationRecord().isPresent()) {
				operationSchedule.setOperationRecord(new OperationRecord());
			}
		}

		// PassengerRecordのマージ
		for (OperationSchedule operationSchedule : newOperationSchedules) {
			for (Reservation reservation : operationSchedule
					.getReservationsAsDeparture()) {
				for (User user : reservation.getFellowUsers()) {
					for (PassengerRecord passengerRecord : reservation
							.getPassengerRecords()) {
						if (passengerRecord.getUserId().equals(
								Optional.of(user.getId()))) {
							mergePassengerRecordsWithWriteLock(localData,
									passengerRecord, reservation, user);
							break;
						}
					}
				}
				// 循環参照にならないようにする
				// TODO:不要になる予定
				reservation.clearFellowUsers();
				reservation.clearPassengerRecords();
			}
		}

		if (newOperationSchedules.isEmpty()) {
			localData.phase = Phase.FINISH;
		} else {
			Optional<OperationRecord> operationRecord = newOperationSchedules
					.get(0).getOperationRecord();
			if (!operationRecord.isPresent()) {
				localData.phase = Phase.FINISH;
			} else if (operationRecord.get().getArrivedAt().isPresent()) {
				localData.phase = Phase.PLATFORM;
			} else {
				localData.phase = Phase.DRIVE;
			}
		}

		localData.operationSchedules.clear();
		localData.operationSchedules.addAll(newOperationSchedules);

		localData.updatedDate = InVehicleDeviceService.getDate();

		if (!service.isOperationInitialized()) {
			localData.operationScheduleInitializedSign.release();
		}
	}

	/**
	 * PassengerRecordをマージする(LocalDataがロックされた状態内)
	 *
	 * @param reservation
	 */
	public void mergePassengerRecordsWithWriteLock(LocalData localData,
			PassengerRecord serverPassengerRecord, Reservation reservation,
			User user) {
		// 循環参照を防ぐ
		// TODO:不要になる予定
		user.clearPassengerRecords();

		// マージ対象を探す
		for (PassengerRecord localPassengerRecord : Lists
				.newArrayList(localData.passengerRecords)) {
			if (!serverPassengerRecord.getId().equals(
					localPassengerRecord.getId())) {
				continue;
			}

			if (serverPassengerRecord.getUpdatedAt().before(
					localPassengerRecord.getUpdatedAt())) {
				localPassengerRecord.setUser(user);
				localPassengerRecord.setReservation(reservation);
				return;
			}

			localData.passengerRecords.remove(localPassengerRecord);
			break;
		}

		// PassengerRecordを更新
		localData.passengerRecords.add(serverPassengerRecord);
		serverPassengerRecord.setUser(user);
		serverPassengerRecord.setReservation(reservation);
	}

	/**
	 * OperationScheduleをマージする
	 */
	public void mergeOperationSchedules(
			final List<OperationSchedule> operationSchedules,
			final List<VehicleNotification> triggerVehicleNotifications) {
		LocalDataSource localDataSource = service.getLocalDataSource();
		// 通知を受信済みリストに移動
		vehicleNotificationLogic.setVehicleNotificationStatus(
				triggerVehicleNotifications,
				VehicleNotificationStatus.OPERATION_SCHEDULE_RECEIVED);
		// マージ
		localDataSource.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				mergeOperationSchedulesWithWriteLock(localData,
						operationSchedules);
			}
		});
		refreshPhase();
		service.getEventDispatcher().dispatchMergeOperationSchedules(operationSchedules,
				triggerVehicleNotifications);
	}

	/**
	 * 現在の運行情報を破棄して新しい運行を開始する
	 */
	public void startNewOperation() {
		service.getLocalDataSource().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationScheduleInitializedSign.drainPermits();
				localData.operationSchedules.clear();
				localData.vehicleNotifications.clear();
				localData.phase = Phase.INITIAL;
				localData.passengerRecords.clear();
			}
		});
		service.getEventDispatcher().dispatchStartNewOperation();
	}

	/**
	 * 現在の運行スケジュールを得る
	 */
	public Optional<OperationSchedule> getCurrentOperationSchedule() {
		return service.getLocalDataSource().withReadLock(
				new Reader<Optional<OperationSchedule>>() {
					@Override
					public Optional<OperationSchedule> read(LocalData localData) {
						for (OperationSchedule operationSchedule : localData.operationSchedules) {
							if (!operationSchedule.getOperationRecord()
									.isPresent()
									|| !operationSchedule.getOperationRecord()
											.get().getDepartedAt().isPresent()) {
								return Optional.of(operationSchedule);
							}
						}
						return Optional.absent();
					}
				});
	}

	public List<OperationSchedule> getRemainingOperationSchedules() {
		return service.getLocalDataSource().withReadLock(
				new Reader<List<OperationSchedule>>() {
					@Override
					public List<OperationSchedule> read(LocalData localData) {
						List<OperationSchedule> operationSchedules = Lists
								.newLinkedList();
						for (OperationSchedule operationSchedule : localData.operationSchedules) {
							for (OperationRecord operationRecord : operationSchedule
									.getOperationRecord().asSet()) {
								if (!operationRecord.getDepartedAt()
										.isPresent()) {
									operationSchedules.add(operationSchedule);
								}
							}
						}
						return operationSchedules;
					}
				});
	}

	public List<OperationSchedule> getOperationSchedules() {
		return service.getLocalDataSource().withReadLock(
				new Reader<List<OperationSchedule>>() {
					@Override
					public List<OperationSchedule> read(LocalData localData) {
						return Lists.newLinkedList(localData.operationSchedules);
					}
				});
	}

	public Phase getPhase() {
		return service.getLocalDataSource().withReadLock(new Reader<Phase>() {
			@Override
			public Phase read(LocalData status) {
				return status.phase;
			}
		});
	}

	public void refreshPhase() {
		switch (getPhase()) {
		case INITIAL:
			enterDrivePhase();
			break;
		case DRIVE:
			enterDrivePhase();
			break;
		case PLATFORM:
			enterPlatformPhase();
			break;
		case FINISH:
			enterFinishPhase();
			break;
		}
	}
}
