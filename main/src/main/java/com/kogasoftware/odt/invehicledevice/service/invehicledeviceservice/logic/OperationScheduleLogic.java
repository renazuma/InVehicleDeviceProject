package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import org.joda.time.DateTimeUtils;

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
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation.Phase;
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
	 * remoteOperationSchedulesに、
	 * localOperationSchedulesから対応するOperationRecordをマージする。 clone
	 * はコストがかかるため、新しいリストを作らずremoteOperationSchedulesを直接書き換える実装にした。
	 */
	public static void mergeOperationSchedules(
			List<OperationSchedule> remoteOperationSchedules,
			List<OperationSchedule> localOperationSchedules) {
		Log.i(TAG, "mergeOperationSchedules() start");
		for (OperationSchedule operationSchedule : remoteOperationSchedules) {
			Log.i(TAG, "mergeOperationSchedules() remoteId="
					+ operationSchedule.getId());
		}
		for (OperationSchedule localOperationSchedule : localOperationSchedules) {
			Log.i(TAG, "mergeOperationSchedules() localId="
					+ localOperationSchedule.getId());
			for (OperationSchedule remoteOperationSchedule : remoteOperationSchedules) {
				if (!localOperationSchedule.getId().equals(
						remoteOperationSchedule.getId())) {
					continue;
				}
				// localに無い場合、次へ進む
				if (!localOperationSchedule.getOperationRecord().isPresent()) {
					Log.i(TAG,
							"mergeOperationSchedules() localOperationSchedule.operationRecord not found");
					break;
				}
				OperationRecord localOperationRecord = localOperationSchedule
						.getOperationRecord().get();

				// remoteに無い場合、localのを適用して、次へ進む
				if (!remoteOperationSchedule.getOperationRecord().isPresent()) {
					Log.i(TAG,
							"mergeOperationSchedules() remoteOperationSchedule.operationRecord not found / use localOperationRecord");
					remoteOperationSchedule
							.setOperationRecord(localOperationRecord);
					break;
				}
				OperationRecord remoteOperationRecord = remoteOperationSchedule
						.getOperationRecord().get();

				// どちらにも存在する場合、新しいほうを適用
				if (localOperationRecord.getUpdatedAt().before(
						remoteOperationRecord.getUpdatedAt())) {
					Log.i(TAG,
							"mergeOperationSchedules() localUpdatedAt.before(remoteUpdatedAt)");
					break;
				}
				remoteOperationSchedule
						.setOperationRecord(localOperationRecord);
				Log.i(TAG,
						"mergeOperationSchedules() !localUpdatedAt.before(remoteUpdatedAt) / use localOperationRecord");
				break;
			}
		}

		// OperationRecordがマージ後に存在しない場合は新規作成
		for (OperationSchedule operationSchedule : remoteOperationSchedules) {
			if (!operationSchedule.getOperationRecord().isPresent()) {
				operationSchedule.setOperationRecord(new OperationRecord());
				Log.i(TAG,
						"mergeOperationSchedules() id="
								+ operationSchedule.getId()
								+ " set new OperationRecord");
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
		Log.i(TAG, "mergePassengerRecords() start");
		// マージ対象を探す
		for (PassengerRecord localPassengerRecord : localPassengerRecords) {
			Log.i(TAG, "mergePassengerRecords() localId="
					+ localPassengerRecord.getId());
			for (PassengerRecord remotePassengerRecord : Lists // ループ内でリスト変更があるため、コピーする
					.newArrayList(remotePassengerRecords)) {
				Log.i(TAG, "mergePassengerRecords() remoteId="
						+ remotePassengerRecord.getId());
				// IDが一致しない場合、次のPassengerRecordで試す
				if (!remotePassengerRecord.getId().equals(
						localPassengerRecord.getId())) {
					continue;
				}

				// remoteが新しい場合、そのまま
				if (localPassengerRecord.getUpdatedAt().before(
						remotePassengerRecord.getUpdatedAt())) {
					Log.i(TAG,
							"mergePassengerRecords() localUpdatedAt.before(remoteUpdatedAt)");
					break;
				}

				// localが新しい場合、乗車実績はlocalを使い、関連はremoteのもので更新
				Log.i(TAG,
						"mergeOperationSchedules() !localUpdatedAt.before(remoteUpdatedAt) / use localPassengerRecord");
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
				localData.operation.operationSchedules);
		List<PassengerRecord> remotePassengerRecords = getPassengerRecordsWithReservationAndUser(remoteOperationSchedules);
		if (service.isOperationInitialized()) {
			mergePassengerRecords(remotePassengerRecords,
					localData.operation.passengerRecords);
		}
		removeUnusedAssociations(remoteOperationSchedules,
				remotePassengerRecords);
		Collections.sort(remotePassengerRecords,
				PassengerRecord.DEFAULT_COMPARATOR);

		localData.updatedDate = new Date(DateTimeUtils.currentTimeMillis());
		localData.operation.operationScheduleReceiveSequence += 1;
		localData.operation.operationSchedules.clear();
		localData.operation.operationSchedules.addAll(remoteOperationSchedules);
		localData.operation.passengerRecords.clear();
		localData.operation.passengerRecords.addAll(remotePassengerRecords);

		Log.i(TAG, "mergeWithWriteLock operationSchedules:");
		for (OperationSchedule operationSchedule : localData.operation.operationSchedules) {
			Log.i(TAG, "id=" + operationSchedule.getId() + " isArrived="
					+ operationSchedule.isArrived() + " isDeparted="
					+ operationSchedule.isDeparted());
		}
		Log.i(TAG, "mergeWithWriteLock passengerRecords:");
		for (PassengerRecord passengerRecord : localData.operation.passengerRecords) {
			Log.i(TAG, "id=" + passengerRecord.getId() + " getOnTime="
					+ passengerRecord.getGetOnTime() + " getOffTime="
					+ passengerRecord.getGetOffTime());
		}
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
	public void startNewOperation(final Boolean always) {
		service.getLocalStorage().write(new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData localData) {
				if (!always && service.isOperationInitialized(localData)) {
					return;
				}
				localData.operation.operationScheduleReceiveSequence = 0;
				localData.operation.operationSchedules.clear();
				localData.vehicleNotifications.clear();
				localData.operation.passengerRecords.clear();
			}

			@Override
			public void onWrite() {
				service.getEventDispatcher().dispatchStartNewOperation();
			}
		});
	}

	/**
	 * 現在の状態を得る
	 */
	public Phase getPhaseWithReadLock(LocalData localData) {
		return localData.operation.getPhase();
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
				localData.operation.completeGetOff = false;
				for (OperationSchedule operationSchedule : localData.operation.operationSchedules) {
					if (!operationSchedule.getId().equals(id)) {
						continue;
					}
					for (OperationRecord operationRecord : operationSchedule
							.getOperationRecord().asSet()) {
						operationRecord.setArrivedAt(new Date(DateTimeUtils.currentTimeMillis()));
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
		Phase phase = localData.operation.getPhase();
		if (phase != Phase.PLATFORM_GET_ON) {
			localData.operation.completeGetOff = false;
		}
		service.getEventDispatcher().dispatchUpdatePhase(phase,
				Lists.newArrayList(localData.operation.operationSchedules),
				Lists.newArrayList(localData.operation.passengerRecords));
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
				localData.operation.completeGetOff = false;
				for (OperationSchedule operationSchedule : localData.operation.operationSchedules) {
					if (!operationSchedule.getId().equals(id)) {
						continue;
					}
					for (OperationRecord operationRecord : operationSchedule
							.getOperationRecord().asSet()) {
						operationRecord.setDepartedAt(new Date(DateTimeUtils.currentTimeMillis()));
						service.getApiClient()
								.withSaveOnClose()
								.departureOperationSchedule(
										operationSchedule,
										new EmptyApiClientCallback<OperationSchedule>());
						Log.i(TAG, "depart -> " + localData.operation.getPhase());
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
