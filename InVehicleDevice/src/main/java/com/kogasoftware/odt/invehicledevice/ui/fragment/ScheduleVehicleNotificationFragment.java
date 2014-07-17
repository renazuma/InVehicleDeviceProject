package com.kogasoftware.odt.invehicledevice.ui.fragment;

import org.apache.commons.lang3.SystemUtils;
import org.joda.time.DateTime;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;
import com.kogasoftware.odt.invehicledevice.utils.FragmentUtils;

public class ScheduleVehicleNotificationFragment extends Fragment
		implements
			LoaderCallbacks<Cursor> {
	private static final Integer LOADER_ID = 1;
	private static String OPERATION_LIST_BUTTON_VISIBLE_KEY = "operation_list_button_visible";
	private TextView detailTextView;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle args = getArguments();
		Boolean operationListButtonVisible = args
				.getBoolean(OPERATION_LIST_BUTTON_VISIBLE_KEY);
		View view = getView();
		Button showOperationListButton = (Button) view
				.findViewById(R.id.schedule_vehicle_notification_operation_list_button);
		if (operationListButtonVisible) {
			showOperationListButton.setVisibility(View.VISIBLE);
		} else {
			showOperationListButton.setVisibility(View.GONE);
		}
		showOperationListButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showOperationListFragment();
				hide();
			}
		});
		Button closeButton = (Button) view
				.findViewById(R.id.schedule_vehicle_notification_close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				hide();
			}
		});
		detailTextView = (TextView) view
				.findViewById(R.id.schedule_vehicle_notification_detail_text_view);
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	public static ScheduleVehicleNotificationFragment newInstance(
			Boolean operationListButtonVisible) {
		ScheduleVehicleNotificationFragment fragment = new ScheduleVehicleNotificationFragment();
		Bundle args = new Bundle();
		args.putBoolean(OPERATION_LIST_BUTTON_VISIBLE_KEY,
				operationListButtonVisible);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(
				R.layout.schedule_vehicle_notification_fragment, container,
				false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(
				getActivity(),
				VehicleNotifications.CONTENT.URI,
				null,
				VehicleNotification.WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
				null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (!cursor.moveToFirst()) {
			hide();
			return;
		}
		StringBuilder content = new StringBuilder();
		StringBuilder speakContent = new StringBuilder();
		do {
			VehicleNotification vehicleNotification = new VehicleNotification(
					cursor);
			content.append(vehicleNotification.body);
			content.append(SystemUtils.LINE_SEPARATOR);
			speakContent.append(vehicleNotification.bodyRuby);
			speakContent.append(SystemUtils.LINE_SEPARATOR);
		} while (cursor.moveToNext());
		detailTextView.setText(content);
		VoiceService.speak(getActivity(), speakContent.toString());
	}

	private void hide() {
		final ContentResolver contentResolver = getActivity()
				.getContentResolver();
		new Thread() {
			@Override
			public void run() {
				Cursor cursor = getActivity()
						.getContentResolver()
						.query(VehicleNotifications.CONTENT.URI,
								null,
								VehicleNotification.WHERE_SCHEDULE_VEHICLE_NOTIFICATION_FRAGMENT_CONTENT,
								null, null);
				try {
					if (cursor.moveToFirst()) {
						do {
							VehicleNotification vehicleNotification = new VehicleNotification(
									cursor);
							vehicleNotification.response = VehicleNotification.Response.YES;
							vehicleNotification.readAt = DateTime.now();
							contentResolver.insert(
									VehicleNotifications.CONTENT.URI,
									vehicleNotification.toContentValues());
						} while (cursor.moveToNext());
					}
				} finally {
					cursor.close();
				}
			}
		}.start();
		FragmentUtils.hide(this);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private void showOperationListFragment() {
		if (!isAdded()) {
			return;
		}
		FragmentUtils.showModalFragment(getFragmentManager(),
				OperationListFragment.newInstance(true));
	}
}
