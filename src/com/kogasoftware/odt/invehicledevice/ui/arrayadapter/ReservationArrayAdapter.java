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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.PassengerRecords;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;
import com.kogasoftware.odt.webapi.model.Users;

public class ReservationArrayAdapter extends ArrayAdapter<Reservation> {
	public static enum ItemType {
		RIDING_AND_NO_GET_OFF, FUTURE_GET_ON, MISSED,
	}

	private static final String TAG = ReservationArrayAdapter.class
			.getSimpleName();

	private final LayoutInflater layoutInflater = (LayoutInflater) getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	private static final Integer RESOURCE_ID = R.layout.reservation_list_row;
	private final CommonLogic commonLogic;

	private final List<Reservation> reservations = new LinkedList<Reservation>();

	private final List<OperationSchedule> remainingOperationSchedules = new LinkedList<OperationSchedule>();
	private final OperationSchedule operationSchedule;
	private final EnumSet<ItemType> visibleItemTypes = EnumSet
			.noneOf(ItemType.class);
	// private final EnumSet<PayTiming> payTiming;
	private final Boolean isLastOperationSchedule;

	public ReservationArrayAdapter(Context context, CommonLogic commonLogic) {
		super(context, RESOURCE_ID);
		this.commonLogic = commonLogic;
		// payTiming = commonLogic.getPayTiming();
		remainingOperationSchedules.addAll(commonLogic
				.getRemainingOperationSchedules());
		reservations.addAll(commonLogic.getReservations());

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
	public void add(Reservation reservation) {
		if (!reservation.getPassengerRecord().isPresent()) {
			return;
		}
		if (canGetOn(reservation) && isGetOnScheduled(reservation)) {
			super.add(reservation);
			return;
		}
		if (canGetOff(reservation) && isGetOffScheduled(reservation)) {
			super.add(reservation);
			return;
		}
		if (visibleItemTypes.contains(ItemType.FUTURE_GET_ON)
				&& isFutureGetOn(reservation)) {
			super.add(reservation);
			return;
		}
		if (visibleItemTypes.contains(ItemType.RIDING_AND_NO_GET_OFF)
				&& isRidingAndNoGetOff(reservation)) {
			super.add(reservation);
			return;
		}
		if (visibleItemTypes.contains(ItemType.MISSED) && isMissed(reservation)) {
			super.add(reservation);
			return;
		}
	}

	public List<Reservation> getNoGettingOffReservations() {
		List<Reservation> getNoGettingOffReservations = new LinkedList<Reservation>();
		for (Reservation reservation : reservations) {
			if (isSelected(reservation) || !canGetOff(reservation)) {
				continue;
			}
			if (isLastOperationSchedule || isGetOffScheduled(reservation)) {
				getNoGettingOffReservations.add(reservation);
			}
		}
		return getNoGettingOffReservations;
	}

	public List<Reservation> getNoGettingOnReservations() {
		List<Reservation> getNoGettingOnReservations = new LinkedList<Reservation>();
		for (Reservation reservation : reservations) {
			if (canGetOn(reservation) && isGetOnScheduled(reservation)
					&& !isSelected(reservation)) {
				getNoGettingOnReservations.add(reservation);
			}
		}
		return getNoGettingOnReservations;
	}

	public List<Reservation> getNoPaymentReservations() {
		List<Reservation> noPaymentReservations = new LinkedList<Reservation>();
		return noPaymentReservations;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		}

		final Reservation reservation = getItem(position);
		TextView passengerCountTextView = (TextView) convertView
				.findViewById(R.id.passenger_count_text_view);
		passengerCountTextView.setText(reservation.getPassengerCount() + "名");

		// メモボタン
		Button memoButton = (Button) convertView.findViewById(R.id.memo_button);
		if (reservation.getMemo().isPresent()
				|| !Users.getMemo(reservation).isEmpty()) {
			memoButton.setVisibility(View.VISIBLE);
			memoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					commonLogic.postEvent(new MemoModalView.ShowEvent(
							reservation));
				}
			});
		} else {
			memoButton.setVisibility(View.GONE);
		}

		// 行の表示
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isSelected(reservation)) {
					unselect(reservation);
				} else {
					select(reservation);
				}
				notifyDataSetChanged();
			}
		});
		TextView userNameView = (TextView) convertView
				.findViewById(R.id.user_name);
		if (reservation.getUser().isPresent()) {
			User user = reservation.getUser().get();
			userNameView.setText(user.getLastName() + " " + user.getFirstName()
					+ " 様");
		} else {
			userNameView.setText("ID:" + reservation.getUserId() + " 様");
		}

		String text = "";
		if (canGetOn(reservation)) {
			if (isGetOnScheduled(reservation)) {
				text += "[乗]";
			} else {
				text += "[*乗]";
			}
		} else {
			if (isGetOffScheduled(reservation)) {
				text += "[降]";
			} else {
				text += "[*降]";
			}
		}

		text += " 予約番号 " + reservation.getId();
		TextView reservationIdView = (TextView) convertView
				.findViewById(R.id.reservation_id);
		reservationIdView.setText(text);

		if (isSelected(reservation)) {
			if (canGetOn(reservation)) {
				convertView.setBackgroundColor(Color.parseColor("#FF69B4")); // TODO テーマ
			} else {
				convertView.setBackgroundColor(Color.parseColor("#40E0D0")); // TODO テーマ
			}
		} else {
			if (canGetOn(reservation)) {
				convertView.setBackgroundColor(Color.parseColor("#F9D9D8"));// TODO テーマ
			} else {
				convertView.setBackgroundColor(Color.parseColor("#D5E9F6"));// TODO テーマ
			}
		}
		return convertView;
	}

	public void hide(ItemType itemType) {
		visibleItemTypes.remove(itemType);
		updateDataSet();
	}

	private Boolean isFutureGetOn(Reservation reservation) {
		if (!canGetOn(reservation)) {
			return false;
		}
		if (remainingOperationSchedules.size() <= 1) {
			return false;
		}
		for (OperationSchedule remainingOperationSchedule : remainingOperationSchedules
				.subList(1, remainingOperationSchedules.size())) {
			if (reservation.getDepartureScheduleId().isPresent()
					&& reservation.getDepartureScheduleId().get()
							.equals(remainingOperationSchedule.getId())) {
				return true;
			}
		}
		return false;
	}

	private Boolean canGetOn(Reservation reservation) {
		if (isSelected(reservation)) {
			if (PassengerRecords.isRiding(reservation)) {
				return true;
			}
		} else {
			if (PassengerRecords.isUnhandled(reservation)) {
				return true;
			}
		}
		return false;
	}

	private Boolean canGetOff(Reservation reservation) {
		if (isSelected(reservation)) {
			if (PassengerRecords.isGotOff(reservation)) {
				return true;
			}
		} else {
			if (PassengerRecords.isRiding(reservation)) {
				return true;
			}
		}
		return false;
	}

	private Boolean isGetOffScheduled(Reservation reservation) {
		// 降車予定かどうかを返す
		return reservation.getArrivalScheduleId().isPresent()
				&& reservation.getArrivalScheduleId().get()
						.equals(operationSchedule.getId());
	}

	private Boolean isGetOnScheduled(Reservation reservation) {
		// 乗車予定かどうか
		return reservation.getDepartureScheduleId().isPresent()
				&& reservation.getDepartureScheduleId().get()
						.equals(operationSchedule.getId());
	}

	private Boolean isMissed(Reservation reservation) {
		if (!canGetOn(reservation)) {
			return false;
		}
		// 現在以降で乗車予定の場合はfalse
		for (OperationSchedule remainingOperationSchedule : remainingOperationSchedules) {
			if (reservation.getDepartureScheduleId().isPresent()
					&& reservation.getDepartureScheduleId().get()
							.equals(remainingOperationSchedule.getId())) {
				return false;
			}
		}
		return true;
	}

	private Boolean isRidingAndNoGetOff(Reservation reservation) {
		return canGetOff(reservation) && !isGetOffScheduled(reservation);
	}

	public Boolean isSelected(Reservation reservation) {
		for (PassengerRecord passengerRecord : reservation.getPassengerRecord()
				.asSet()) {
			if (PassengerRecords.isRiding(passengerRecord)) {
				return operationSchedule.getId().equals(
						passengerRecord.getDepartureOperationScheduleId()
								.orNull());
			} else if (PassengerRecords.isGotOff(passengerRecord)) {
				return operationSchedule.getId().equals(
						passengerRecord.getArrivalOperationScheduleId()
								.orNull());
			}
		}
		return false;
	}

	public void select(Reservation reservation) {
		reservation.setPassengerRecord(reservation.getPassengerRecord().or(
				new PassengerRecord()));
		for (PassengerRecord passengerRecord : reservation.getPassengerRecord()
				.asSet()) {
			passengerRecord.setPassengerCount(reservation.getPassengerCount());
			if (PassengerRecords.isUnhandled(passengerRecord)) {
				passengerRecord.setGetOnTime(new Date());
				passengerRecord
						.setDepartureOperationSchedule(operationSchedule);
				passengerRecord
						.setDepartureOperationScheduleId(operationSchedule
								.getId());
				commonLogic.getDataSource().getOnPassenger(operationSchedule,
						reservation, passengerRecord,
						new EmptyWebAPICallback<PassengerRecord>());
			} else if (PassengerRecords.isRiding(passengerRecord)) {
				passengerRecord.setGetOffTime(new Date());
				passengerRecord.setArrivalOperationSchedule(operationSchedule);
				passengerRecord.setArrivalOperationScheduleId(operationSchedule
						.getId());
				commonLogic.getDataSource().getOffPassenger(operationSchedule,
						reservation, passengerRecord,
						new EmptyWebAPICallback<PassengerRecord>());
			}
		}
	}

	public void unselect(Reservation reservation) {
		reservation.setPassengerRecord(reservation.getPassengerRecord().or(
				new PassengerRecord()));
		for (PassengerRecord passengerRecord : reservation.getPassengerRecord()
				.asSet()) {
			if (PassengerRecords.isRiding(passengerRecord)) {
				passengerRecord.clearGetOnTime();
				passengerRecord.clearDepartureOperationSchedule();
				passengerRecord.clearDepartureOperationScheduleId();
				commonLogic.getDataSource().cancelGetOnPassenger(
						operationSchedule, reservation,
						new EmptyWebAPICallback<PassengerRecord>());
			} else if (PassengerRecords.isGotOff(passengerRecord)) {
				passengerRecord.clearGetOffTime();
				passengerRecord.clearArrivalOperationSchedule();
				passengerRecord.clearArrivalOperationScheduleId();
				commonLogic.getDataSource().cancelGetOffPassenger(
						operationSchedule, reservation,
						new EmptyWebAPICallback<PassengerRecord>());
			}
		}
	}

	public void show(ItemType itemType) {
		visibleItemTypes.add(itemType);
		updateDataSet();
	}

	private void updateDataSet() {
		clear();
		for (Reservation reservation : reservations) {
			add(reservation);
		}
		notifyDataSetChanged();
	}
}
