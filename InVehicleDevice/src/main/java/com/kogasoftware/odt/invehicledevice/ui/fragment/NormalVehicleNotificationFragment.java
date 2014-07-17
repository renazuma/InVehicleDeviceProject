package com.kogasoftware.odt.invehicledevice.ui.fragment;

import org.joda.time.DateTime;

import android.app.Fragment;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;
import com.kogasoftware.odt.invehicledevice.utils.FragmentUtils;

public class NormalVehicleNotificationFragment extends Fragment {
	private static final String VEHICLE_NOTIFICATION_KEY = "vehicle_notification";
	private VehicleNotification vehicleNotification;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vehicleNotification = (VehicleNotification) getArguments()
				.getSerializable(VEHICLE_NOTIFICATION_KEY);
		VoiceService.speak(getActivity(), vehicleNotification.bodyRuby);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		TextView bodyTextView = (TextView) view
				.findViewById(R.id.notification_text_view);
		bodyTextView.setText(vehicleNotification.body);
		view.findViewById(R.id.reply_yes_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						submit(VehicleNotification.Response.YES);
					}
				});
		view.findViewById(R.id.reply_no_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						submit(VehicleNotification.Response.NO);
					}
				});
	}

	public static NormalVehicleNotificationFragment newInstance(
			VehicleNotification vehicleNotification) {
		NormalVehicleNotificationFragment fragment = new NormalVehicleNotificationFragment();
		Bundle args = new Bundle();
		args.putSerializable(VEHICLE_NOTIFICATION_KEY, vehicleNotification);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.vehicle_notification_fragment,
				container, false);
	}

	private void submit(final Long response) {
		vehicleNotification.response = response;
		vehicleNotification.readAt = DateTime.now();
		if (!isAdded()) {
			return;
		}
		final ContentResolver contentResolver = getActivity()
				.getContentResolver();
		new Thread() {
			@Override
			public void run() {
				contentResolver.insert(VehicleNotifications.CONTENT.URI,
						vehicleNotification.toContentValues());
			}
		}.start();
		FragmentUtils.hide(this);
	}
}
