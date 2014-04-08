package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.empty.EmptyRunnable;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.PassengerRecordLogic;
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
	private final Activity activity;
	private final InVehicleDeviceService service;
	private final TreeSet<PassengerRecord> passengerRecords = new TreeSet<PassengerRecord>(PassengerRecord.DEFAULT_COMPARATOR);
	private Boolean showPassengerRecords = false;
	private Boolean operationScheduleArrivalDepartureChanged = false;
	private final PassengerRecordLogic passengerRecordLogic;
	private final OperationScheduleLogic operationScheduleLogic;

	static abstract class OnRowTouchListener<T> implements OnTouchListener {
		private final Class<T> rowClass;

		public OnRowTouchListener(Class<T> rowClass) {
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

			Boolean result = event.getAction() == MotionEvent.ACTION_CANCEL ? true
					: onTap(view, event, tag);
			view.setBackgroundColor(getDefaultColor(tag));
			return result;
		}

		protected abstract boolean onTap(View view, MotionEvent event, T tag);

		protected int getDefaultColor(T tag) {
			return DEFAULT_COLOR;
		}

		protected int getSelectedColor(T tag) {
			return SELECTED_COLOR;
		}
	}

	protected final OnTouchListener onOperationScheduleTouchListener = new OnRowTouchListener<OperationSchedule>(
			OperationSchedule.class) {
		@Override
		protected boolean onTap(View view, MotionEvent event,
				final OperationSchedule operationSchedule) {
			Runnable nop = new EmptyRunnable();
			operationScheduleArrivalDepartureChanged = true;
			if (operationSchedule.isDeparted()) {
				operationScheduleLogic.cancelArrive(operationSchedule, nop);
			} else {
				if (!operationSchedule.isArrived()) {
					operationScheduleLogic.arrive(operationSchedule, nop);
				}
				operationScheduleLogic.depart(operationSchedule, nop);
			}
			return false;
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

	static class PassengerRecordRowTag {
		public final PassengerRecord passengerRecord;
		public final OperationSchedule operationSchedule;
		public final Boolean getOn;

		public PassengerRecordRowTag(PassengerRecord passengerRecord,
				OperationSchedule operationSchedule, Boolean getOn) {
			this.passengerRecord = passengerRecord;
			this.operationSchedule = operationSchedule;
			this.getOn = getOn;
		}
	}

	protected final OnRowTouchListener<PassengerRecordRowTag> onPassengerRecordTouchListener = new OnRowTouchListener<PassengerRecordRowTag>(
			PassengerRecordRowTag.class) {
		@Override
		protected int getDefaultColor(
				PassengerRecordRowTag passengerRecordRowTag) {
			return getColor(passengerRecordRowTag, false);
		}

		@Override
		protected int getSelectedColor(PassengerRecordRowTag passengerRecordRowTag) {
			return getColor(passengerRecordRowTag, true);
		}

		private int getColor(PassengerRecordRowTag passengerRecordRowTag, boolean invert) {
			PassengerRecord passengerRecord = passengerRecordRowTag.passengerRecord;
			if (passengerRecordRowTag.getOn) {
				if (passengerRecord.getGetOnTime().isPresent() ^ invert) {
					return PassengerRecordLogic.SELECTED_GET_ON_COLOR;
				} else {
					return PassengerRecordLogic.GET_ON_COLOR;
				}
			} else {
				if (passengerRecord.getGetOffTime().isPresent() ^ invert) {
					return PassengerRecordLogic.SELECTED_GET_OFF_COLOR;
				} else {
					return PassengerRecordLogic.GET_OFF_COLOR;
				}
			}
		}

		@Override
		protected boolean onTap(View view, MotionEvent event,
				PassengerRecordRowTag passengerRecordRowTag) {
			OperationSchedule operationSchedule = passengerRecordRowTag.operationSchedule;
			PassengerRecord passengerRecord = passengerRecordRowTag.passengerRecord;
			if (passengerRecordRowTag.getOn) {
				if (passengerRecord.getGetOnTime().isPresent()) {
					passengerRecordLogic.cancelGetOn(operationSchedule,
							passengerRecord);
				} else {
					passengerRecordLogic.getOn(operationSchedule,
							passengerRecord);
				}
			} else {
				if (passengerRecord.getGetOffTime().isPresent()) {
					passengerRecordLogic.cancelGetOff(operationSchedule,
							passengerRecord);
				} else {
					passengerRecordLogic.getOff(operationSchedule,
							passengerRecord);
				}
			}
			return false;
		}
	};

	protected final OnClickListener onUserMemoButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				return;
			}
			PassengerRecord passengerRecord = (PassengerRecord) tag;
			FragmentManager fragmentManager = activity.getFragmentManager();
			if (fragmentManager == null) {
				return;
			}
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			ApplicationFragment.setCustomAnimation(fragmentTransaction);
			fragmentTransaction.add(R.id.modal_fragment_container,
					PassengerRecordMemoFragment.newInstance(passengerRecord));

			fragmentTransaction.commitAllowingStateLoss();
		}
	};

	protected final OnClickListener onOperationScheduleMapButtonClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Object tag = view.getTag();
			if (!(tag instanceof OperationSchedule)) {
				return;
			}
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
									.getFragmentManager();
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
		}
	};

	public OperationScheduleArrayAdapter(Activity activity,
			InVehicleDeviceService service,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		super(activity, RESOURCE_ID, operationSchedules);
		this.activity = activity;
		this.service = service;
		this.passengerRecordLogic = new PassengerRecordLogic(service);
		this.operationScheduleLogic = new OperationScheduleLogic(service);
		this.passengerRecords.addAll(passengerRecords);
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
		Button mapButton = (Button) convertView
				.findViewById(R.id.operation_schedule_list_map_button);
		mapButton.setTag(operationSchedule);
		mapButton.setOnClickListener(onOperationScheduleMapButtonClickListener);
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
								operationSchedule, passengerRecord, false));
					}
					getOffPassengerCount += passengerRecord.getPassengerCount();
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
								operationSchedule, passengerRecord, true));
					}
					getOnPassengerCount += passengerRecord.getPassengerCount();
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

	private View createPassengerRecordRow(OperationSchedule operationSchedule,
			PassengerRecord passengerRecord, Boolean getOn) {
		View row = layoutInflater.inflate(
				R.layout.small_passenger_record_list_row, null);
		row.setBackgroundColor(DEFAULT_COLOR);
		ImageView selectMarkImageView = (ImageView) row
				.findViewById(R.id.select_mark_image_view);
		selectMarkImageView.setImageResource(getOn ? R.drawable.get_on
				: R.drawable.get_off);

		TextView userNameView = (TextView) row.findViewById(R.id.user_name);
		userNameView.setText(passengerRecord.getDisplayName());
		PassengerRecordRowTag tag = new PassengerRecordRowTag(passengerRecord,
				operationSchedule, getOn);
		row.setTag(tag);
		row.setOnTouchListener(onPassengerRecordTouchListener);

		TextView countView = (TextView) row
				.findViewById(R.id.passenger_count_text_view);
		countView.setText(passengerRecord.getPassengerCount() + "名");
		Button userMemoButton = (Button) row
				.findViewById(R.id.user_memo_button);
		userMemoButton.setTag(passengerRecord);
		userMemoButton.setOnClickListener(onUserMemoButtonClickListener);
		row.setBackgroundColor(onPassengerRecordTouchListener
				.getDefaultColor(tag));
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

	public void updatePassengerRecord(PassengerRecord newPassengerRecord) {
		for (PassengerRecord oldPassengerRecord : Lists.newArrayList(passengerRecords)) {
			if (oldPassengerRecord.getId().equals(newPassengerRecord.getId())) {
				passengerRecords.remove(oldPassengerRecord);
				passengerRecords.add(newPassengerRecord);
				notifyDataSetChanged();
				break;
			}
		}
	}
	
	public Boolean isOperationScheduleArrivalDepartureChanged() {
		return operationScheduleArrivalDepartureChanged;
	}
}
