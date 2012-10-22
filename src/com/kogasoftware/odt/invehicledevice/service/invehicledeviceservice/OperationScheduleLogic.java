package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.List;

import android.os.Handler;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.BackgroundWriter;
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
	protected static final String TAG = OperationScheduleLogic.class
			.getSimpleName();
	protected final InVehicleDeviceService service;
	protected final VehicleNotificationLogic vehicleNotificationLogic;

	public OperationScheduleLogic(InVehicleDeviceService service) {
		this.service = service;
		vehicleNotificationLogic = new VehicleNotificationLogic(service);
	}

	public static Phase getPhase(List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords, Boolean completeGetOff) {
		Log.i(TAG, "----- getPhase -----");
		for (OperationSchedule operationSchedule : OperationSchedule
				.getCurrentOperationSchedule(operationSchedules).asSet()) {
			Log.i(TAG, "id = " + operationSchedule.getId());
			if (!operationSchedule.isArrived()) {
				Log.i(TAG, "no arrived");
				Log.i(TAG, Phase.DRIVE.toString());
				return Phase.DRIVE;
			}
			if (operationSchedule.isDeparted()) {
				Log.i(TAG, "departed");
				continue;
			}
			for (PassengerRecord passengerRecord : operationSchedule
					.getNoGetOffErrorPassengerRecords(passengerRecords)) {
				if (!passengerRecord.getIgnoreGetOffMiss()) {
					Log.i(TAG, "no get off error found");
					Log.i(TAG, Phase.PLATFORM_GET_OFF.toString());
					return Phase.PLATFORM_GET_OFF;
				}
			}
			if (operationSchedule.getGetOffScheduledPassengerRecords(
					passengerRecords).isEmpty()) {
				Log.i(TAG, "get off scheduled passengerRecords not found");
				Log.i(TAG, Phase.PLATFORM_GET_ON.toString());
				return Phase.PLATFORM_GET_ON;
			}
			if (completeGetOff) {
				Log.i(TAG, "complete get off");
				Log.i(TAG, Phase.PLATFORM_GET_ON.toString());
				return Phase.PLATFORM_GET_ON;
			} else {
				Log.i(TAG, "no complete get off");
				Log.i(TAG, Phase.PLATFORM_GET_OFF.toString());
				return Phase.PLATFORM_GET_OFF;
			}
		}
		Log.i(TAG, Phase.FINISH.toString());
		return Phase.FINISH;
	}

	/**
	 * 走行フェーズへ移行
	 */
	public void enterDrivePhase() {
		throw new RuntimeException("method deleted");
	}

	/**
	 * 終了フェーズへ移行
	 */
	public void enterFinishPhase() {
		throw new RuntimeException("method deleted");
	}

	/**
	 * 乗降場フェーズへ移行
	 */
	public void enterPlatformPhase() {
		throw new RuntimeException("method deleted");
	}

	@Deprecated
	private void arrive2() {
		throw new RuntimeException("method deleted");
	}

	@Deprecated
	private void depart2() {
		throw new RuntimeException("method deleted");
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

		Log.i("InVehicleDeviceActivity", "mergeOperationSchedules 2");
		if (localData.operationScheduleInitializedSign.availablePermits() == 0) {
			localData.operationScheduleInitializedSign.release();
			Log.i("InVehicleDeviceActivity", "mergeOperationSchedules 3");
		}
		Log.i("InVehicleDeviceActivity", "mergeOperationSchedules 4");
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
		Log.i("InVehicleDeviceActivity", "mergeOperationSchedules 1");

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
		service.getEventDispatcher().dispatchMergeOperationSchedules(
				operationSchedules, triggerVehicleNotifications);
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
	@Deprecated
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

	@Deprecated
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

	@Deprecated
	public List<OperationSchedule> getOperationSchedules() {
		return service.getLocalDataSource().withReadLock(
				new Reader<List<OperationSchedule>>() {
					@Override
					public List<OperationSchedule> read(LocalData localData) {
						return Lists
								.newLinkedList(localData.operationSchedules);
					}
				});
	}

	@Deprecated
	public Phase getPhase() {
		return service.getLocalDataSource().withReadLock(new Reader<Phase>() {
			@Override
			public Phase read(LocalData status) {
				return status.phase;
			}
		});
	}

	@Deprecated
	public void refreshPhase() {
		throw new RuntimeException("method deleted");
	}

	public void arrive(OperationSchedule currentOperationSchedule,
			final Runnable callback) {
		final Integer id = currentOperationSchedule.getId();
		Log.i(TAG, "arrive id=" + id);
		service.getLocalDataSource().write(new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData localData) {
				localData.completeGetOff = false;
				for (OperationSchedule operationSchedule : localData.operationSchedules) {
					if (!operationSchedule.getId().equals(id)) {
						continue;
					}
					for (OperationRecord operationRecord : operationSchedule
							.getOperationRecord().asSet()) {
						operationRecord.setArrivedAt(InVehicleDeviceService
								.getDate());
						service.getRemoteDataSource()
								.withSaveOnClose()
								.arrivalOperationSchedule(
										operationSchedule,
										new EmptyWebAPICallback<OperationSchedule>());
						Log.i(TAG,
								"arrive -> "
										+ getPhase(
												localData.operationSchedules,
												localData.passengerRecords,
												localData.completeGetOff));
						return;
					}
					Log.e(TAG, "OperationSchedule has no OperationRecord "
							+ operationSchedule);
				}
				Log.e(TAG, "OperationSchedule id=" + id + " not found");
			}

			@Override
			public void onWrite() {
				callback.run();
			}
		});
	}

	public void updatePhaseInBackground(LocalData localData) {
		Phase phase = getPhase(localData.operationSchedules,
				localData.passengerRecords, localData.completeGetOff);
		if (phase != Phase.PLATFORM_GET_ON) {
			localData.completeGetOff = false;
		}
		service.getEventDispatcher().dispatchUpdatePhase(phase,
				Lists.newArrayList(localData.operationSchedules),
				Lists.newArrayList(localData.passengerRecords));
	}

	public void depart(OperationSchedule currentOperationSchedule,
			final Runnable callback) {
		final Integer id = currentOperationSchedule.getId();
		Log.i(TAG, "depart id=" + id);
		service.getLocalDataSource().write(new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData localData) {
				localData.completeGetOff = false;
				for (OperationSchedule operationSchedule : localData.operationSchedules) {
					if (!operationSchedule.getId().equals(id)) {
						continue;
					}
					for (OperationRecord operationRecord : operationSchedule
							.getOperationRecord().asSet()) {
						operationRecord.setDepartedAt(InVehicleDeviceService
								.getDate());
						service.getRemoteDataSource()
								.withSaveOnClose()
								.departureOperationSchedule(
										operationSchedule,
										new EmptyWebAPICallback<OperationSchedule>());
						Log.i(TAG,
								"depart -> "
										+ getPhase(
												localData.operationSchedules,
												localData.passengerRecords,
												localData.completeGetOff));
						return;
					}
					Log.e(TAG, "OperationSchedule has no OperationRecord "
							+ operationSchedule);
				}
				Log.e(TAG, "OperationSchedule id=" + id + " not found");
			}

			@Override
			public void onWrite() {
				callback.run();
			}
		});
	}

	public void requestUpdatePhase() {
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "waiting for update phase");
				if (!service.isOperationInitialized()) {
					handler.postDelayed(this, 500);
					return;
				}
				service.getLocalDataSource().withWriteLock(new Writer() {
					@Override
					public void write(LocalData localData) {
						updatePhaseInBackground(localData);
					}
				});
			}
		});
	}
}
