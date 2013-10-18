package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.ui.fragment.ApplicationFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PlatformNavigationFragment;

public class OperationScheduleArrayAdapter extends
		ArrayAdapter<OperationSchedule> {
	private static final String TAG = OperationScheduleArrayAdapter.class
			.getSimpleName();
	private static final Integer SELECTED_COLOR = Color.parseColor("#D5E9F6");
	private static final Integer DEFAULT_COLOR = Color.parseColor("#FFFFFF");
	private static final Integer RESOURCE_ID = R.layout.operation_schedule_list_row;
	private final LayoutInflater layoutInflater;
	private final FragmentActivity activity;
	private final InVehicleDeviceService service;
	protected final OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent event) {
			Object tag = view.getTag();
			if (!(tag instanceof OperationSchedule)) {
				Log.e(TAG, "\"" + view + "\".getTag() (" + tag
						+ ") is not instanceof OperationSchedule");
				return false;
			}

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				view.setBackgroundColor(SELECTED_COLOR);
				return true;
			}

			if (event.getAction() == MotionEvent.ACTION_CANCEL) {
				view.setBackgroundColor(DEFAULT_COLOR);
				return true;
			}

			if (event.getAction() != MotionEvent.ACTION_UP) {
				return false;
			}

			view.setBackgroundColor(DEFAULT_COLOR);
			final OperationSchedule operationSchedule = (OperationSchedule) tag;
			service.getLocalStorage().read(
					new BackgroundReader<ServiceUnitStatusLog>() {
						@Override
						public ServiceUnitStatusLog readInBackground(
								LocalData localData) {
							return localData.serviceUnitStatusLog;
						}

						@Override
						public void onRead(ServiceUnitStatusLog result) {
							FragmentManager fragmentManager = activity
									.getSupportFragmentManager();
							if (fragmentManager == null) {
								return;
							}
							FragmentTransaction fragmentTransaction = fragmentManager
									.beginTransaction();
							ApplicationFragment
									.setCustomAnimation(fragmentTransaction);
							fragmentTransaction.add(
									R.id.modal_fragment_container,
									PlatformNavigationFragment.newInstance(
											operationSchedule,
											result.getLatitude(),
											result.getLongitude()));
							fragmentTransaction.commitAllowingStateLoss();
						}
					});
			return true;
		}
	};

	public OperationScheduleArrayAdapter(FragmentActivity activity,
			InVehicleDeviceService service,
			List<OperationSchedule> operationSchedules) {
		super(activity, RESOURCE_ID, operationSchedules);
		this.activity = activity;
		this.service = service;
		this.layoutInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		} else {
			convertView.setBackgroundColor(DEFAULT_COLOR);
		}
		DateFormat displayDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

		OperationSchedule operationSchedule = getItem(position);
		TextView platformNameView = (TextView) convertView
				.findViewById(R.id.platform_name);
		if (operationSchedule.getPlatform().isPresent()) {
			platformNameView.setText(operationSchedule.getPlatform().get()
					.getName());
		} else {
			platformNameView.setText("ID:" + operationSchedule.getId());
		}

		Integer getOnPassengerCount = 0;
		for (Reservation reservation : operationSchedule
				.getReservationsAsDeparture()) {
			getOnPassengerCount += reservation.getPassengerCount();
		}

		Integer getOffPassengerCount = 0;
		for (Reservation reservation : operationSchedule
				.getReservationsAsArrival()) {
			getOffPassengerCount += reservation.getPassengerCount();
		}

		TextView getOnPassengerCountTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_get_on_passenger_count_text_view);
		getOnPassengerCountTextView.setText("乗車"
				+ String.format("%3d", getOnPassengerCount) + "名");
		getOnPassengerCountTextView
				.setVisibility(getOnPassengerCount.equals(0) ? View.INVISIBLE
						: View.VISIBLE);

		TextView getOffPassengerCountTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_get_off_passenger_count_text_view);
		getOffPassengerCountTextView.setText("降車"
				+ String.format("%3d", getOffPassengerCount) + "名");
		getOffPassengerCountTextView.setVisibility(getOffPassengerCount
				.equals(0) ? View.INVISIBLE : View.VISIBLE);

		TextView arrivalEstimateTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_arrival_estimate_text_view);
		TextView departureEstimateTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_departure_estimate_text_view);

		arrivalEstimateTextView.setText("");
		departureEstimateTextView.setText("");

		for (Date arrivalEstimate : operationSchedule.getArrivalEstimate()
				.asSet()) {
			arrivalEstimateTextView.setText(displayDateFormat
					.format(arrivalEstimate) + " 着");
		}

		if (getCount() != position + 1) {
			for (Date departureEstimate : operationSchedule
					.getDepartureEstimate().asSet()) {
				departureEstimateTextView.setText(displayDateFormat
						.format(departureEstimate) + " 発");
			}
		}

		if (!operationSchedule.isDeparted()) {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		} else {
			convertView.setBackgroundColor(Color.LTGRAY);
		}
		convertView.setTag(operationSchedule);
		convertView.setOnTouchListener(onTouchListener);
		return convertView;
	}
}
