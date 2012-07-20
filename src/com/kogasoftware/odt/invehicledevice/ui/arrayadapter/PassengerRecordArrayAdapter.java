package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class PassengerRecordArrayAdapter extends ArrayAdapter<PassengerRecord> {
	public static enum ItemType {
		RIDING_AND_NO_GET_OFF, FUTURE_GET_ON, MISSED,
	}

	private static final String TAG = PassengerRecordArrayAdapter.class
			.getSimpleName();
	protected static final Integer RESOURCE_ID = R.layout.reservation_list_row;
	protected final LayoutInflater layoutInflater = (LayoutInflater) getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	protected final AlphaAnimation animation = new AlphaAnimation(1, 0.1f);
	protected final List<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();
	protected final List<PassengerRecord> getOffScheduledAndUnhandledPassengerRecords = new LinkedList<PassengerRecord>();
	protected final List<OperationSchedule> remainingOperationSchedules = new LinkedList<OperationSchedule>();
	protected final OperationSchedule operationSchedule;
	protected final EnumSet<ItemType> visibleItemTypes = EnumSet
			.noneOf(ItemType.class);
	// protected final EnumSet<PayTiming> payTiming;
	protected final Boolean isLastOperationSchedule;
	protected final MemoModalView memoModalView;
	protected final InVehicleDeviceService service;
	protected final OnClickListener onClickViewListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				return;
			}
			PassengerRecord passengerRecord = (PassengerRecord) tag;
			if (isSelected(passengerRecord)) {
				unselect(passengerRecord);
			} else {
				select(passengerRecord);
			}
			notifyDataSetChanged();
		}
	};

	protected final OnClickListener onClickMemoButtonListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			Object tag = view.getTag();
			if (!(tag instanceof PassengerRecord)) {
				return;
			}
			PassengerRecord passengerRecord = (PassengerRecord) tag;
			memoModalView.show(passengerRecord);
		}
	};

	public PassengerRecordArrayAdapter(InVehicleDeviceService service,
			MemoModalView memoModalView) {
		super(service, RESOURCE_ID);
		this.service = service;
		this.memoModalView = memoModalView;
		// payTiming = commonLogic.getPayTiming();
		remainingOperationSchedules.addAll(service
				.getRemainingOperationSchedules());
		passengerRecords.addAll(service.getPassengerRecords());
		isLastOperationSchedule = (remainingOperationSchedules.size() <= 1);
		if (isLastOperationSchedule) {
			visibleItemTypes.add(ItemType.RIDING_AND_NO_GET_OFF);
		}

		if (remainingOperationSchedules.isEmpty()) {
			operationSchedule = new OperationSchedule();
		} else {
			operationSchedule = remainingOperationSchedules.get(0);
		}

		for (PassengerRecord passengerRecord : passengerRecords) {
			if (isGetOffScheduled(passengerRecord)
					&& passengerRecord.isUnhandled()) {
				getOffScheduledAndUnhandledPassengerRecords
						.add(passengerRecord);
			}
		}
		updateDataSet();
	}

	@Override
	public void add(PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()
				|| !passengerRecord.getUser().isPresent()) {
			return;
		}
		if (canGetOn(passengerRecord) && isGetOnScheduled(passengerRecord)) {
			super.add(passengerRecord);
			return;
		}
		if (canGetOff(passengerRecord) && isGetOffScheduled(passengerRecord)) {
			super.add(passengerRecord);
			return;
		}
		if (getOffScheduledAndUnhandledPassengerRecords
				.contains(passengerRecord)) {
			super.add(passengerRecord);
			return;
		}
	}

	private Boolean canGetOff(PassengerRecord passengerRecord) {
		if (isSelected(passengerRecord)) {
			if (passengerRecord.isGotOff()) {
				return true;
			}
		} else {
			if (passengerRecord.isRiding()) {
				return true;
			}
		}
		return false;
	}

	private Boolean canGetOn(PassengerRecord passengerRecord) {
		if (isSelected(passengerRecord)) {
			if (passengerRecord.isRiding()) {
				return true;
			}
		} else {
			if (passengerRecord.isUnhandled()) {
				return true;
			}
		}
		return false;
	}

	public List<PassengerRecord> getNoGettingOffPassengerRecords() {
		List<PassengerRecord> noGettingOffReservations = new LinkedList<PassengerRecord>();
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (isGetOffScheduled(passengerRecord)
					&& !isSelected(passengerRecord)) {
				noGettingOffReservations.add(passengerRecord);
			}
		}
		return noGettingOffReservations;
	}

	public List<PassengerRecord> getNoGettingOnPassengerRecords() {
		List<PassengerRecord> noGettingOnReservations = new LinkedList<PassengerRecord>();
		for (PassengerRecord passengerRecord : passengerRecords) {
			if (canGetOn(passengerRecord) && isGetOnScheduled(passengerRecord)
					&& !isSelected(passengerRecord)) {
				noGettingOnReservations.add(passengerRecord);
			}
		}
		return noGettingOnReservations;
	}

	public List<PassengerRecord> getNoPaymentPassengerRecords() {
		List<PassengerRecord> noPaymentPassengerRecords = new LinkedList<PassengerRecord>();
		return noPaymentPassengerRecords;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		}

		PassengerRecord passengerRecord = getItem(position);
		if (!passengerRecord.getReservation().isPresent()
				|| !passengerRecord.getUser().isPresent()) {
			return convertView;
		}

		Reservation reservation = passengerRecord.getReservation().get();
		User user = passengerRecord.getUser().get();

		TextView passengerCountTextView = (TextView) convertView
				.findViewById(R.id.passenger_count_text_view);
		passengerCountTextView.setText(reservation.getPassengerCount() + "名");

		// メモボタン
		Button memoButton = (Button) convertView.findViewById(R.id.memo_button);
		memoButton.setTag(reservation);
		memoButton.setOnClickListener(onClickMemoButtonListener);
		if (!reservation.getMemo().or("").isEmpty()
				|| !user.getNotes().isEmpty()) {
			memoButton.setVisibility(View.VISIBLE);
			animation.setDuration(1000);
			animation.setRepeatCount(Animation.INFINITE);
			memoButton.startAnimation(animation);
		} else {
			memoButton.setVisibility(View.GONE);
		}

		// 行の表示
		convertView.setTag(reservation);
		convertView.setOnClickListener(onClickViewListener);
		TextView userNameView = (TextView) convertView
				.findViewById(R.id.user_name);
		userNameView.setText(user.getLastName() + " " + user.getFirstName()
				+ " 様");

		String text = "";
		if (isGetOffScheduled(passengerRecord)) {
			text += "[降]";
		} else if (isGetOnScheduled(passengerRecord)) {
			text += "[乗]";
		} else if (canGetOff(passengerRecord)) {
			text += "[*降]";
		} else {
			text += "[*乗]";
		}

		text += " 予約番号 " + reservation.getId();
		TextView reservationIdView = (TextView) convertView
				.findViewById(R.id.reservation_id);
		reservationIdView.setText(text);

		if (isSelected(passengerRecord)) {
			if (isGetOnScheduled(passengerRecord)) {
				convertView.setBackgroundColor(Color.parseColor("#FF69B4")); // TODO
																				// テーマ
			} else {
				convertView.setBackgroundColor(Color.parseColor("#40E0D0")); // TODO
																				// テーマ
			}
		} else {
			if (isGetOnScheduled(passengerRecord)) {
				convertView.setBackgroundColor(Color.parseColor("#F9D9D8"));// TODO
																			// テーマ
			} else {
				convertView.setBackgroundColor(Color.parseColor("#D5E9F6"));// TODO
																			// テーマ
			}
		}
		return convertView;
	}

	public void hide(ItemType itemType) {
		visibleItemTypes.remove(itemType);
		updateDataSet();
	}

	private Boolean isGetOffScheduled(PassengerRecord passengerRecord) {
		// 降車予定かどうか
		if (!passengerRecord.getReservation().isPresent()) {
			return false;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		return reservation.getArrivalScheduleId().isPresent()
				&& reservation.getArrivalScheduleId().get()
						.equals(operationSchedule.getId());
	}

	private Boolean isGetOnScheduled(PassengerRecord passengerRecord) {
		// 乗車予定かどうか
		if (!passengerRecord.getReservation().isPresent()) {
			return false;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		return reservation.getDepartureScheduleId().isPresent()
				&& reservation.getDepartureScheduleId().get()
						.equals(operationSchedule.getId());
	}

	public Boolean isSelected(PassengerRecord passengerRecord) {
		if (passengerRecord.isRiding()) {
			return operationSchedule.getId().equals(
					passengerRecord.getDepartureOperationScheduleId().orNull());
		} else if (passengerRecord.isGotOff()) {
			return operationSchedule.getId().equals(
					passengerRecord.getArrivalOperationScheduleId().orNull());
		}
		return false;
	}

	public void select(PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			return;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		reservation.setPassengerRecord(reservation.getPassengerRecord().or(
				new PassengerRecord()));
		passengerRecord.setPassengerCount(reservation.getPassengerCount());
		if (getOffScheduledAndUnhandledPassengerRecords
				.contains(passengerRecord)) {
			passengerRecord.setGetOnTime(new Date());
			passengerRecord.setGetOffTime(new Date());
			passengerRecord.setDepartureOperationScheduleId(reservation
					.getDepartureScheduleId());
			passengerRecord.setArrivalOperationScheduleId(operationSchedule
					.getId());
			service.getDataSource().getOnPassenger(operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>());
			service.getDataSource().getOffPassenger(operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>());
		} else if (passengerRecord.isUnhandled()) {
			passengerRecord.setGetOnTime(new Date());
			passengerRecord.setDepartureOperationScheduleId(operationSchedule
					.getId());
			service.getDataSource().getOnPassenger(operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>());
		} else if (passengerRecord.isRiding()) {
			passengerRecord.setGetOffTime(new Date());
			passengerRecord.setArrivalOperationScheduleId(operationSchedule
					.getId());
			service.getDataSource().getOffPassenger(operationSchedule,
					reservation, passengerRecord,
					new EmptyWebAPICallback<PassengerRecord>());
		}
	}

	public void show(ItemType itemType) {
		visibleItemTypes.add(itemType);
		updateDataSet();
	}

	public void unselect(PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			return;
		}
		Reservation reservation = passengerRecord.getReservation().get();
		if (getOffScheduledAndUnhandledPassengerRecords
				.contains(passengerRecord)) {
			passengerRecord.clearGetOnTime();
			passengerRecord.clearGetOffTime();
			passengerRecord.clearDepartureOperationScheduleId();
			passengerRecord.clearArrivalOperationScheduleId();
			service.getDataSource().cancelGetOffPassenger(operationSchedule,
					reservation, new EmptyWebAPICallback<PassengerRecord>());
			service.getDataSource().cancelGetOnPassenger(operationSchedule,
					reservation, new EmptyWebAPICallback<PassengerRecord>());
		} else if (passengerRecord.isRiding()) {
			passengerRecord.clearGetOnTime();
			passengerRecord.clearDepartureOperationScheduleId();
			service.getDataSource().cancelGetOnPassenger(operationSchedule,
					reservation, new EmptyWebAPICallback<PassengerRecord>());
		} else if (passengerRecord.isGotOff()) {
			passengerRecord.clearGetOffTime();
			passengerRecord.clearArrivalOperationScheduleId();
			service.getDataSource().cancelGetOffPassenger(operationSchedule,
					reservation, new EmptyWebAPICallback<PassengerRecord>());
		}
	}

	private void updateDataSet() {
		clear();
		for (PassengerRecord passengerRecord : passengerRecords) {
			add(passengerRecord);
		}
		notifyDataSetChanged();
	}
}
