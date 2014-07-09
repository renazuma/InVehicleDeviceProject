package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.util.concurrent.TimeUnit;

import android.app.Fragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.invehicledevice.utils.FragmentUtils;

public class OperationListFragment extends Fragment {
	private static final String CLOSEABLE_KEY = "closeable";
	private static final Integer OPERATION_SCHEDULE_LOADER_ID = 1;
	private static final Integer PASSENGER_RECORD_LOADER_ID = 2;

	public static OperationListFragment newInstance(Boolean closeable) {
		OperationListFragment fragment = new OperationListFragment();
		Bundle args = new Bundle();
		args.putBoolean(CLOSEABLE_KEY, closeable);
		fragment.setArguments(args);
		return fragment;
	}

	private LoaderManager loaderManager;
	private OperationScheduleArrayAdapter adapter;

	private LoaderCallbacks<Cursor> operationScheduleLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(getActivity(),
					OperationSchedules.CONTENT.URI, null, null, null, null) {
				@Override
				public Cursor loadInBackground() {
					// 運行予定変更の通知がある場合、「運行予定変更」フラグメントが表示されるまで更新を遅らせる
					Cursor cursor = getActivity()
							.getContentResolver()
							.query(VehicleNotifications.CONTENT.URI,
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
					return super.loadInBackground();
				}
			};
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			adapter.setOperationSchedules(OperationSchedule.getAll(cursor));
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	private LoaderCallbacks<Cursor> passengerRecordLoaderCallbacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			return new CursorLoader(getActivity(),
					PassengerRecords.CONTENT.URI, null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			adapter.setPassengerRecords(PassengerRecord.getAll(cursor));
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.operation_list_fragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Boolean closeable = getArguments().getBoolean(CLOSEABLE_KEY);
		loaderManager = getLoaderManager();
		loaderManager.initLoader(OPERATION_SCHEDULE_LOADER_ID, null,
				operationScheduleLoaderCallbacks);
		loaderManager.initLoader(PASSENGER_RECORD_LOADER_ID, null,
				passengerRecordLoaderCallbacks);
		View view = getView();
		final Button closeButton = (Button) view
				.findViewById(R.id.operation_list_close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentUtils.hide(OperationListFragment.this);
			}
		});
		if (closeable) {
			closeButton.setVisibility(View.VISIBLE);
		} else {
			closeButton.setVisibility(View.GONE);
		}
		adapter = new OperationScheduleArrayAdapter(getActivity());
		((FlickUnneededListView) view.findViewById(R.id.operation_list_view))
				.getListView().setAdapter(adapter);
		final Button showPassengerButton = (Button) view
				.findViewById(R.id.operation_list_show_passengers_button);
		final Button hidePassengerButton = (Button) view
				.findViewById(R.id.operation_list_hide_passengers_button);
		showPassengerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				adapter.showPassengerRecords();
				showPassengerButton.setVisibility(View.GONE);
				hidePassengerButton.setVisibility(View.VISIBLE);
				closeButton.setVisibility(View.GONE);
			}
		});
		hidePassengerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adapter.hidePassengerRecords();
				hidePassengerButton.setVisibility(View.GONE);
				showPassengerButton.setVisibility(View.VISIBLE);
				if (closeable) {
					closeButton.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		loaderManager.destroyLoader(OPERATION_SCHEDULE_LOADER_ID);
		loaderManager.destroyLoader(PASSENGER_RECORD_LOADER_ID);
	}
}
