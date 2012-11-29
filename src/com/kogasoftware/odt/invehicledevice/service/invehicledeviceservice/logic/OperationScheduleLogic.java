package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import android.util.Log;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.EmptyApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
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
	 * remoteOperationSchedulesに、
	 * localOperationSchedulesから対応するOperationRecordをマージする。 clone
	 * はコストがかかるため、新しいリストを作らずremoteOperationSchedulesを直接書き換える実装にした。
	 */
	public static void mergeOperationSchedules(
			List<OperationSchedule> remoteOperationSchedules,
			List<OperationSchedule> localOperationSchedules) {
		for (OperationSchedule localOperationSchedule : localOperationSchedules) {
			for (OperationSchedule remoteOperationSchedule : remoteOperationSchedules) {
				if (!localOperationSchedule.getId().equals(
						remoteOperationSchedule.getId())) {
					continue;
				}
				// localに無い場合、次へ進む
				if (!localOperationSchedule.getOperationRecord().isPresent()) {
					break;
				}
				OperationRecord localOperationRecord = localOperationSchedule
						.getOperationRecord().get();

				// remoteに無い場合、localのを適用して、次へ進む
				if (!remoteOperationSchedule.getOperationRecord().isPresent()) {
					remoteOperationSchedule
							.setOperationRecord(localOperationRecord);
					break;
				}
				OperationRecord remoteOperationRecord = remoteOperationSchedule
						.getOperationRecord().get();

				// どちらにも存在する場合、新しいほうを適用
				if (localOperationRecord.getUpdatedAt().before(
						remoteOperationRecord.getUpdatedAt())) {
					break;
				}
				remoteOperationSchedule
						.setOperationRecord(localOperationRecord);
				break;

			}
		}

		// OperationRecordがマージ後に存在しない場合は新規作成
		for (OperationSchedule operationSchedule : remoteOperationSchedules) {
			if (!operationSchedule.getOperationRecord().isPresent()) {
				operationSchedule.setOperationRecord(new OperationRecord());
			}
		}
	}

	/**
	 * remotePassengerRecordsに、 localPassengerRecordsをマージする。 clone
	 * はコストがかかるため、新しいリストを作らずremotePassengerRecordsを直接書き換える実装にした。
	 * localPassengerRecords内のデータも書き変わることがあるのに注意。
	 */
	public static void mergePassengerRecords(
			List<PassengerRecord> remotePassengerRecords,
			List<PassengerRecord> localPassengerRecords) {
		// マージ対象を探す
		for (PassengerRecord localPassengerRecord : localPassengerRecords) {
			for (PassengerRecord remotePassengerRecord : Lists // ループ内でリスト変更があるため、コピーする
					.newArrayList(remotePassengerRecords)) {
				// IDが一致しない場合、次のPassengerRecordで試す
				if (!remotePassengerRecord.getId().equals(
						localPassengerRecord.getId())) {
					continue;
				}

				// remoteが新しい場合、そのまま
				if (localPassengerRecord.getUpdatedAt().before(
						remotePassengerRecord.getUpdatedAt())) {
					break;
				}

				// localが新しい場合、乗車実績はlocalを使い、関連はremoteのもので更新
				remotePassengerRecords.remove(remotePassengerRecord);
				localPassengerRecord.setUser(remotePassengerRecord.getUser());
				localPassengerRecord.setReservation(remotePassengerRecord
						.getReservation());
				remotePassengerRecords.add(localPassengerRecord);
			}
		}
	}

	/**
	 * ReservationとUserをメンバに持つPassengerRecordを取得する
	 */
	public static List<PassengerRecord> getPassengerRecordsWithReservationAndUser(
			List<OperationSchedule> operationSchedules) {
		List<PassengerRecord> passengerRecords = Lists.newLinkedList();
		for (OperationSchedule operationSchedule : operationSchedules) {
			passengerRecords.addAll(operationSchedule
					.getPassengerRecordsWithReservationAndUser());
		}
		return passengerRecords;
	}

	/**
	 * アプリ内で利用しない不要なアソシエーションを削除する
	 */
	public static void removeUnusedAssociations(
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		for (OperationSchedule operationSchedule : operationSchedules) {
			operationSchedule.clearReservationsAsArrival();
			operationSchedule.clearReservationsAsDeparture();
		}
		for (PassengerRecord passengerRecord : passengerRecords) {
			for (Reservation reservation : passengerRecord.getReservation()
					.asSet()) {
				reservation.clearPassengerRecords();
			}
		}
	}

	/**
	 * OperationScheduleをマージする(LocalDataがロックされた状態内)
	 */
	public void mergeWithWriteLock(LocalData localData,
			List<OperationSchedule> remoteOperationSchedules) {
		mergeOperationSchedules(remoteOperationSchedules,
				localData.operationSchedules);
		List<PassengerRecord> remotePassengerRecords = getPassengerRecordsWithReservationAndUser(remoteOperationSchedules);
		if (service.isOperationInitialized()) {
			mergePassengerRecords(remotePassengerRecords,
					localData.passengerRecords);
		}
		removeUnusedAssociations(remoteOperationSchedules,
				remotePassengerRecords);
		localData.updatedDate = InVehicleDeviceService.getDate();
		localData.operationScheduleInitialized = true;
		localData.operationSchedules.clear();
		localData.operationSchedules.addAll(remoteOperationSchedules);
		localData.passengerRecords.clear();
		localData.passengerRecords.addAll(remotePassengerRecords);

		remoteOperationSchedules.get(0).getReservationsAsArrival().get(0)
				.getPassengerRecords().get(0);
	}

	/**
	 * OperationScheduleをマージする
	 */
	public void mergeWithWriteLock(
			final List<OperationSchedule> operationSchedules,
			final List<VehicleNotification> triggerVehicleNotifications) {
		LocalStorage localStorage = service.getLocalStorage();
		// 通知を受信済みリストに移動
		vehicleNotificationLogic.setStatusWithWriteLock(
				triggerVehicleNotifications,
				VehicleNotificationStatus.OPERATION_SCHEDULE_RECEIVED);
		// マージ
		localStorage.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				mergeWithWriteLock(localData, operationSchedules);
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
		Runnable waitForOperationInitializedAndUpdatePhase = new Runnable() {
			@Override
			public void run() {
				try {
					while (!service.isOperationInitialized()) {
						Log.i(TAG, "waiting for update phase");
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {
					return;
				}
				service.getLocalStorage().withWriteLock(new Writer() {
					@Override
					public void write(LocalData localData) {
						updatePhaseInBackground(localData);
					}
				});
			}
		};
		try {
			service.getScheduledExecutorService().submit(
					waitForOperationInitializedAndUpdatePhase);
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
		}
	}
}
