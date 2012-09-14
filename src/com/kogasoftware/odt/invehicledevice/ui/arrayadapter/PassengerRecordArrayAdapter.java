package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

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
			if (service.isSelected(passengerRecord)) {
				service.unselect(passengerRecord);
			} else {
				service.select(passengerRecord);
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

		updateDataSet();
	}

	@Override
	public void add(PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()
				|| !passengerRecord.getUser().isPresent()) {
			return;
		}
		if (service.isGetOnScheduled(passengerRecord)) {
			super.add(passengerRecord);
			return;
		}
		if (service.isGetOffScheduled(passengerRecord)) {
			super.add(passengerRecord);
			return;
		}
		if (service.getGetOffScheduledAndUnhandledPassengerRecords()
				.contains(passengerRecord)) {
			super.add(passengerRecord);
			return;
		}
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
		memoButton.setTag(passengerRecord);
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
		convertView.setTag(passengerRecord);
		convertView.setOnClickListener(onClickViewListener);
		TextView userNameView = (TextView) convertView
				.findViewById(R.id.user_name);
		userNameView.setText(user.getLastName() + " " + user.getFirstName()
				+ " 様");

		String text = "";
		if (service.isGetOffScheduled(passengerRecord)) {
			text += "[降]";
		} else if (service.isGetOnScheduled(passengerRecord)) {
			text += "[乗]";
		} else if (service.canGetOff(passengerRecord)) {
			text += "[*降]";
		} else {
			text += "[*乗]";
		}

		text += " 予約番号 " + reservation.getId();
		TextView reservationIdView = (TextView) convertView
				.findViewById(R.id.reservation_id);
		reservationIdView.setText(text);

		if (service.isSelected(passengerRecord)) {
			if (service.isGetOnScheduled(passengerRecord)) {
				convertView.setBackgroundColor(Color.parseColor("#FF69B4")); // TODO
																				// テーマ
			} else {
				convertView.setBackgroundColor(Color.parseColor("#40E0D0")); // TODO
																				// テーマ
			}
		} else {
			if (service.isGetOnScheduled(passengerRecord)) {
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

	public void show(ItemType itemType) {
		visibleItemTypes.add(itemType);
		updateDataSet();
	}

	private void updateDataSet() {
		clear();
		for (PassengerRecord passengerRecord : passengerRecords) {
			add(passengerRecord);
		}
		notifyDataSetChanged();
	}
}
