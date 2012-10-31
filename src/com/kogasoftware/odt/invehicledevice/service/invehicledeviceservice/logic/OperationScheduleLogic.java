package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic;

import java.util.List;

import android.os.Handler;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.EmptyApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundWriter;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;

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

	/**
	 * 現在の状況を得る
	 */
	public static Phase getPhase(List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords, Boolean completeGetOff) {
		for (OperationSchedule operationSchedule : OperationSchedule
				.getCurrent(operationSchedules).asSet()) {
			if (!operationSchedule.isArrived()) {
				return Phase.DRIVE;
			}
			if (operationSchedule.isDeparted()) {
				continue;
			}
			for (PassengerRecord passengerRecord : operationSchedule
					.getNoGetOffErrorPassengerRecords(passengerRecords)) {
				if (!passengerRecord.getIgnoreGetOffMiss()) {
					return Phase.PLATFORM_GET_OFF;
				}
			}
			if (operationSchedule.getGetOffScheduledPassengerRecords(
					passengerRecords).isEmpty()) {
				return Phase.PLATFORM_GET_ON;
			}
			if (completeGetOff) {
				return Phase.PLATFORM_GET_ON;
			} else {
				return Phase.PLATFORM_GET_OFF;
			}
		}
		return Phase.FINISH;
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
			}
			operationSchedule.clearReservationsAsArrival();
			operationSchedule.clearReservationsAsDeparture();
		}

		localData.operationSchedules.clear();
		localData.operationSchedules.addAll(newOperationSchedules);
		localData.updatedDate = InVehicleDeviceService.getDate();
		localData.operationScheduleInitialized = true;
	}

	/**
	 * PassengerRecordをマージする(LocalDataがロックされた状態内)
	 * 
	 * @param reservation
	 */
	public void mergePassengerRecordsWithWriteLock(LocalData localData,
			PassengerRecord serverPassengerRecord, Reservation reservation,
			User user) {
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

		LocalStorage localStorage = service.getLocalStorage();
		// 通知を受信済みリストに移動
		vehicleNotificationLogic.setVehicleNotificationStatusWithWriteLock(
				triggerVehicleNotifications,
				VehicleNotificationStatus.OPERATION_SCHEDULE_RECEIVED);
		// マージ
		localStorage.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				mergeOperationSchedulesWithWriteLock(localData,
						operationSchedules);
			}
		});
		service.getEventDispatcher().dispatchMergeOperationSchedules(
				operationSchedules, triggerVehicleNotifications);
	}

	/**
	 * 現在の運行情報を破棄して新しい運行を開始する
	 */
	public void startNewOperation() {
		service.getLocalStorage().withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationScheduleInitialized = false;
				localData.operationSchedules.clear();
				localData.vehicleNotifications.clear();
				localData.passengerRecords.clear();
			}
		});
		service.getEventDispatcher().dispatchStartNewOperation();
	}

	/**
	 * 現在の状態を得る
	 */
	public Phase getPhaseWithReadLock(LocalData localData) {
		return getPhase(localData.operationSchedules,
				localData.passengerRecords, localData.completeGetOff);
	}

	public Phase getPhaseWithReadLock() {
		return service.getLocalStorage().withReadLock(new Reader<Phase>() {
			@Override
			public Phase read(LocalData localData) {
				return getPhaseWithReadLock(localData);
			}
		});
	}

	/**
	 * 到着処理
	 */
	public void arrive(OperationSchedule currentOperationSchedule,
			final Runnable callback) {
		final Integer id = currentOperationSchedule.getId();
		Log.i(TAG, "arrive id=" + id);
		service.getLocalStorage().write(new BackgroundWriter() {
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
						service.getApiClient()
								.withSaveOnClose()
								.arrivalOperationSchedule(
										operationSchedule,
										new EmptyApiClientCallback<OperationSchedule>());
						Log.i(TAG, "arrive -> "
								+ getPhaseWithReadLock(localData));
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

	/**
	 * 発車処理
	 */
	public void depart(OperationSchedule currentOperationSchedule,
			final Runnable callback) {
		final Integer id = currentOperationSchedule.getId();
		Log.i(TAG, "depart id=" + id);
		service.getLocalStorage().write(new BackgroundWriter() {
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
						service.getApiClient()
								.withSaveOnClose()
								.departureOperationSchedule(
										operationSchedule,
										new EmptyApiClientCallback<OperationSchedule>());
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
				service.getLocalStorage().withWriteLock(new Writer() {
					@Override
					public void write(LocalData localData) {
						updatePhaseInBackground(localData);
					}
				});
			}
		});
	}
}
