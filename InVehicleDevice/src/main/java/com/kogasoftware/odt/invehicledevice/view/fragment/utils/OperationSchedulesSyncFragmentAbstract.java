package com.kogasoftware.odt.invehicledevice.view.fragment.utils;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 運行スケジュール同期時に処理が必要なFragmentの抽象クラス
 * 継承先クラスがインスタンス化された段階で、OperatorSchedulesとPassengerRecordsのLoaderを順次用意し、継承先で実装されたメソッドを実行する。
 * その後は継承先が表示中であれば、OperationSchedulesが更新される度に同様の処理が行われる。
 */
public abstract class OperationSchedulesSyncFragmentAbstract extends Fragment {

	private static final int PASSENGER_RECORDS_LOADER_ID = 5000;
	private static final int OPERATION_SCHEDULES_LOADER_ID = 5001;
	protected Handler handler;
	protected ContentResolver contentResolver;
	private final LinkedList<OperationSchedule> operationSchedules = Lists.newLinkedList();
	private List<OperationSchedule> currentOperationSchedules = Lists.newArrayList();
	private Phase currentPhase;

	private final LoaderCallbacks<Cursor> operationSchedulesLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(getActivity(), OperationSchedule.CONTENT.URI, null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			final LinkedList<OperationSchedule> newOperationSchedules = OperationSchedule.getAll(cursor);

			final Runnable task = new Runnable() {
				@Override
				public void run() {
					if (!isAdded()) { return; }
					operationSchedules.clear();
					operationSchedules.addAll(newOperationSchedules);
					getLoaderManager().initLoader(PASSENGER_RECORDS_LOADER_ID,null, passengerRecordsLoaderCallbacks);
				}
			};

			new Thread() {
				@Override
				public void run() {
					// 新規に運行予定変更の通知がある場合、「運行予定変更」フラグメントが表示されるまで更新を遅らせる
					Cursor cursor = contentResolver.query(VehicleNotification.CONTENT.URI,
									null,
									VehicleNotification.WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
									null, null);

					Boolean delayRequired;
					try {
						delayRequired = cursor.getCount() > 0;
					} finally {
						cursor.close();
					}

					if (delayRequired) {
						Integer delayMillis = InVehicleDeviceActivity.VEHICLE_NOTIFICATION_ALERT_DELAY_MILLIS + 1000;
						Uninterruptibles.sleepUninterruptibly(delayMillis, TimeUnit.MILLISECONDS);
					}

					handler.post(task);
				}
			}.start();
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	private final LoaderCallbacks<Cursor> passengerRecordsLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(getActivity(), PassengerRecord.CONTENT.URI, null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
			final LinkedList<PassengerRecord> passengerRecords = Lists.newLinkedList(PassengerRecord.getAll(cursor));

			handler.post(new Runnable() {
				@Override
				public void run() {
					if (!isAdded()) { return; }

					Phase newPhase = OperationSchedule.getPhase(operationSchedules, passengerRecords);

					List<OperationSchedule> currentChunk = OperationPhase.getCurrentOperationSchedules(operationSchedules, passengerRecords);

					Boolean phaseChanged = isPhaseChangedPattern(newPhase, currentChunk);

					currentPhase = newPhase;
					currentOperationSchedules = currentChunk;

					// 継承先のクラスで実装される、operation_schedule/passenger_record同期後の動作
					onOperationSchedulesAndPassengerRecordsLoadFinished(operationSchedules, passengerRecords, phaseChanged);
				}

				private boolean isPhaseChangedPattern(Phase newPhase, List<OperationSchedule> newOperationSchedules) {
					boolean changeToFinishPhase = newOperationSchedules.isEmpty() && !currentOperationSchedules.isEmpty();
					boolean operationSchedulesChanged =
									!newOperationSchedules.isEmpty() && !isSameOperationSchedules(newOperationSchedules);

					return !newPhase.equals(currentPhase) || changeToFinishPhase || operationSchedulesChanged;
				}

				private boolean isSameOperationSchedules(List<OperationSchedule> targetOperationSchedules) {
					for (OperationSchedule baseOperationSchedule : currentOperationSchedules) {
						boolean existSameOS = false;
						for (OperationSchedule targetOperationSchedule : targetOperationSchedules) {
							if (baseOperationSchedule.id.equals(targetOperationSchedule.id)) {
								existSameOS = true;
								break;
							}
						}
						if (!existSameOS) {
							return false;
						}
					}
					return true;
				}
			});
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	// override用
	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords, Boolean phaseChanged) {
		onOperationSchedulesAndPassengerRecordsLoadFinished(operationSchedules, passengerRecords);
	}

	// override用
	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler = new Handler();
		contentResolver = getActivity().getContentResolver();
		getLoaderManager().initLoader(OPERATION_SCHEDULES_LOADER_ID, null, operationSchedulesLoaderCallbacks);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getLoaderManager().destroyLoader(PASSENGER_RECORDS_LOADER_ID);
		getLoaderManager().destroyLoader(OPERATION_SCHEDULES_LOADER_ID);
	}
}
