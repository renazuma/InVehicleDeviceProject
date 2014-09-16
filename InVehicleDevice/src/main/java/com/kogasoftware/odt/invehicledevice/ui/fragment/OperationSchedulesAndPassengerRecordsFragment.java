package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

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
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public abstract class OperationSchedulesAndPassengerRecordsFragment
		extends
			Fragment {
	private static final int PASSENGER_RECORDS_LOADER_ID = 5000;
	private static final int OPEATION_SCHEDULES_LOADER_ID = 5001;
	protected Handler handler;
	protected ContentResolver contentResolver;
	private final LinkedList<OperationSchedule> operationSchedules = Lists
			.newLinkedList();
	private Long currentOperationScheduleId;
	private Phase currentPhase;

	private final LoaderCallbacks<Cursor> operationSchedulesLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(getActivity(),
					OperationSchedule.CONTENT.URI, null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			final LinkedList<OperationSchedule> newOperationSchedules = OperationSchedule
					.getAll(cursor);
			final Runnable task = new Runnable() {
				@Override
				public void run() {
					if (!isAdded()) {
						return;
					}
					operationSchedules.clear();
					operationSchedules.addAll(newOperationSchedules);
					getLoaderManager().initLoader(PASSENGER_RECORDS_LOADER_ID,
							null, passengerRecordsLoaderCallbacks);
				}
			};
			new Thread() {
				@Override
				public void run() {
					// 運行予定変更の通知がある場合、「運行予定変更」フラグメントが表示されるまで更新を遅らせる
					Cursor cursor = contentResolver
							.query(VehicleNotification.CONTENT.URI,
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
						Uninterruptibles.sleepUninterruptibly(delayMillis,
								TimeUnit.MILLISECONDS);
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
			return new CursorLoader(getActivity(),
					PassengerRecord.CONTENT.URI, null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			final LinkedList<PassengerRecord> passengerRecords = Lists
					.newLinkedList(PassengerRecord.getAll(cursor));
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (!isAdded()) {
						return;
					}
					Phase phase = OperationSchedule.getPhase(
							operationSchedules, passengerRecords);
					OperationSchedule operationSchedule = OperationSchedule
							.getCurrent(operationSchedules);
					Boolean phaseChanged = false;
					if (operationSchedule == null) {
						if (currentOperationScheduleId != null
								|| !phase.equals(currentPhase)) {
							phaseChanged = true;
						}
						currentOperationScheduleId = null;
					} else {
						if (!phase.equals(currentPhase)
								|| !operationSchedule.id
										.equals(currentOperationScheduleId)) {
							phaseChanged = true;
						}
						currentOperationScheduleId = operationSchedule.id;
					}
					currentPhase = phase;

					onOperationSchedulesAndPassengerRecordsLoadFinished(phase,
							operationSchedules, passengerRecords, phaseChanged);
				}
			});
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			Phase phase, LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords, Boolean phaseChanged) {
		onOperationSchedulesAndPassengerRecordsLoadFinished(phase,
				operationSchedules, passengerRecords);
	}

	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			Phase phase, LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler = new Handler();
		contentResolver = getActivity().getContentResolver();
		getLoaderManager().initLoader(OPEATION_SCHEDULES_LOADER_ID, null,
				operationSchedulesLoaderCallbacks);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getLoaderManager().destroyLoader(PASSENGER_RECORDS_LOADER_ID);
		getLoaderManager().destroyLoader(OPEATION_SCHEDULES_LOADER_ID);
	}
}
