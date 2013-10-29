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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.ui.fragment.ApplicationFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PassengerRecordMemoFragment;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PlatformNavigationFragment;

public class OperationScheduleArrayAdapter extends
		ArrayAdapter<OperationSchedule> {
	private static final String TAG = OperationScheduleArrayAdapter.class
			.getSimpleName();
	private static final Integer SELECTED_COLOR = Color.parseColor("#D5E9F6");
	private static final Integer DEPARTED_COLOR = Color.LTGRAY;
	private static final Integer DEFAULT_COLOR = Color.parseColor("#FFFFFF");
	private static final Integer RESOURCE_ID = R.layout.operation_schedule_list_row;
	private final LayoutInflater layoutInflater;
	private final FragmentActivity activity;
	private final InVehicleDeviceService service;
	private final List<PassengerRecord> passengerRecords;
	private Boolean showPassengerRecords = false;

	static abstract class onRowTouchListener<T> implements OnTouchListener {
		private final Class<T> rowClass;

		public onRowTouchListener(Class<T> rowClass) {
			this.rowClass = rowClass;
		}

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			Object tag = view.getTag();
			if (rowClass.isInstance(tag)) {
				return onTouch(view, event, rowClass.cast(tag));
			} else {
				Log.e(TAG, "\"" + view + "\".getTag() (" + tag
						+ ") is not instanceof " + rowClass);
			}
			return false;
		}

		private boolean onTouch(View view, MotionEvent event, T tag) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				view.setBackgroundColor(getSelectedColor(tag));
				return true;
			}

			if (event.getAction() != MotionEvent.ACTION_UP
					&& event.getAction() != MotionEvent.ACTION_CANCEL) {
				return false;
			}

			view.setBackgroundColor(getDefaultColor(tag));

			if (event.getAction() == MotionEvent.ACTION_CANCEL) {
				return true;
			}

			return onTap(view, event, tag);
		}

		protected abstract boolean onTap(View view, MotionEvent event, T tag);

		protected int getDefaultColor(T tag) {
			return Color.TRANSPARENT;
		}

		protected int getSelectedColor(T tag) {
			return SELECTED_COLOR;
		}
	}

	protected final OnTouchListener onOperationScheduleTouchListener = new onRowTouchListener<OperationSchedule>(
			OperationSchedule.class) {
		@Override
		protected boolean onTap(View view, MotionEvent event,
				final OperationSchedule operationSchedule) {
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

		@Override
		protected int getDefaultColor(OperationSchedule operationSchedule) {
			if (operationSchedule.isDeparted()) {
				return DEPARTED_COLOR;
			} else {
				return DEFAULT_COLOR;
			}
		}
	};

	protected final OnTouchListener onPassengerRecordTouchListener = new onRowTouchListener<PassengerRecord>(
			PassengerRecord.class) {
		@Override
		protected boolean onTap(View view, MotionEvent event,
				PassengerRecord passengerRecord) {
			FragmentManager fragmentManager = activity
					.getSupportFragmentManager();
			if (fragmentManager == null) {
				return true;
			}
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			ApplicationFragment.setCustomAnimation(fragmentTransaction);
			fragmentTransaction.add(R.id.modal_fragment_container,
					PassengerRecordMemoFragment.newInstance(passengerRecord));

			fragmentTransaction.commitAllowingStateLoss();
			return true;
		}
	};

	public OperationScheduleArrayAdapter(FragmentActivity activity,
			InVehicleDeviceService service,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		super(activity, RESOURCE_ID, operationSchedules);
		this.activity = activity;
		this.service = service;
		this.passengerRecords = passengerRecords;
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

		ViewGroup passengerRecordsView = (ViewGroup) convertView
				.findViewById(R.id.operation_schedule_list_passenger_records);
		passengerRecordsView.removeAllViews();
		passengerRecordsView.setVisibility(showPassengerRecords ? View.VISIBLE
				: View.GONE);

		Integer getOffPassengerCount = 0;
		for (PassengerRecord passengerRecord : passengerRecords) {
			for (Reservation reservation : passengerRecord.getReservation()
					.asSet()) {
				if (reservation.getArrivalScheduleId().equals(
						Optional.of(operationSchedule.getId()))) {
					if (showPassengerRecords) {
						passengerRecordsView.addView(createPassengerRecordRow(
								passengerRecord, false));
					}
					getOffPassengerCount += 1;
				}
			}
		}

		Integer getOnPassengerCount = 0;
		for (PassengerRecord passengerRecord : passengerRecords) {
			for (Reservation reservation : passengerRecord.getReservation()
					.asSet()) {
				if (reservation.getDepartureScheduleId().equals(
						Optional.of(operationSchedule.getId()))) {
					if (showPassengerRecords) {
						passengerRecordsView.addView(createPassengerRecordRow(
								passengerRecord, true));
					}
					getOnPassengerCount += 1;
				}
			}
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

		if (operationSchedule.isDeparted()) {
			convertView.setBackgroundColor(DEPARTED_COLOR);
		} else {
			convertView.setBackgroundColor(DEFAULT_COLOR);
		}
		convertView.setTag(operationSchedule);
		convertView.setOnTouchListener(onOperationScheduleTouchListener);
		return convertView;
	}

	private View createPassengerRecordRow(PassengerRecord passengerRecord,
			Boolean getOn) {
		View row = layoutInflater.inflate(
				R.layout.small_passenger_record_list_row, null);
		ImageView selectMarkImageView = (ImageView) row
				.findViewById(R.id.select_mark_image_view);
		selectMarkImageView.setImageResource(getOn ? R.drawable.get_on
				: R.drawable.get_off);

		TextView userNameView = (TextView) row.findViewById(R.id.user_name);
		userNameView.setText(passengerRecord.getDisplayName());

		row.setTag(passengerRecord);
		row.setOnTouchListener(onPassengerRecordTouchListener);

		TextView countView = (TextView) row
				.findViewById(R.id.passenger_count_text_view);
		countView.setText(passengerRecord.getPassengerCount() + "名");
		return row;
	}

	public void showPassengerRecords() {
		showPassengerRecords = true;
		notifyDataSetChanged();
	}

	public void hidePassengerRecords() {
		showPassengerRecords = false;
		notifyDataSetChanged();
	}
}
