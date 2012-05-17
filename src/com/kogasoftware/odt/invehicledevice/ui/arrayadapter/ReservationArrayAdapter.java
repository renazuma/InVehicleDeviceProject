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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic.PayTiming;
import com.kogasoftware.odt.invehicledevice.logic.event.SelectedReservationsUpdateEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ReturnPathModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.PassengerRecords;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

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
	private final List<Reservation> selectedReservations = new LinkedList<Reservation>();

	private final List<OperationSchedule> remainingOperationSchedules = new LinkedList<OperationSchedule>();
	private final OperationSchedule operationSchedule;
	private final EnumSet<ItemType> visibleItemTypes = EnumSet
			.noneOf(ItemType.class);
	private final EnumSet<PayTiming> payTiming;
	private final Boolean isLastOperationSchedule;

	public ReservationArrayAdapter(Context context, CommonLogic commonLogic) {
		super(context, RESOURCE_ID);
		this.commonLogic = commonLogic;
		payTiming = commonLogic.getPayTiming();
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
		if (isGetOn(reservation)) {
			super.add(reservation);
			return;
		}
		if (isGetOff(reservation)) {
			super.add(reservation);
			return;
		}
		if (visibleItemTypes.contains(ItemType.FUTURE_GET_ON)
				&& isFutureGetOn(reservation)) {
			super.add(reservation);
			return;
		}
		if (visibleItemTypes.contains(ItemType.RIDING_AND_NO_GET_OFF)
				&& isRidingAndNotGetOff(reservation)) {
			super.add(reservation);
			return;
		}
		if (visibleItemTypes.contains(ItemType.MISSED) && isMissed(reservation)) {
			super.add(reservation);
			return;
		}
	}

	public void clearSelectedReservations() {
		selectedReservations.clear();
		commonLogic.postEvent(new SelectedReservationsUpdateEvent(
				selectedReservations));
	}

	public List<Reservation> getNoGettingOffReservations() {
		List<Reservation> getNoGettingOffReservations = new LinkedList<Reservation>();
		for (Reservation reservation : reservations) {
			if (isSelected(reservation)
					|| !PassengerRecords.isRiding(reservation)) {
				continue;
			}
			if (isLastOperationSchedule || isGetOff(reservation)) {
				getNoGettingOffReservations.add(reservation);
			}
		}
		return getNoGettingOffReservations;
	}

	public List<Reservation> getNoGettingOnReservations() {
		List<Reservation> getNoGettingOnReservations = new LinkedList<Reservation>();
		for (Reservation reservation : reservations) {
			if (isGetOn(reservation) && !isSelected(reservation)) {
				getNoGettingOnReservations.add(reservation);
			}
		}
		return getNoGettingOnReservations;
	}

	public List<Reservation> getNoPaymentReservations() {
		List<Reservation> noPaymentReservations = new LinkedList<Reservation>();
		if (!payTiming.contains(PayTiming.GET_OFF)
				&& payTiming.contains(PayTiming.GET_ON)) {
			return noPaymentReservations;
		}
		for (Reservation reservation : getSelectedRidingReservations()) {
			if (reservation.getPassengerRecord().isPresent()
					&& !reservation.getPassengerRecord().get().getPayment()
							.isPresent()) {
				noPaymentReservations.add(reservation);
			}
		}
		return noPaymentReservations;
	}

	public List<Reservation> getSelectedGetOnReservations() {
		List<Reservation> selectedGetOnreservations = new LinkedList<Reservation>();
		for (Reservation reservation : selectedReservations) {
			if (PassengerRecords.isUnhandled(reservation)) {
				selectedGetOnreservations.add(reservation);
			}
		}
		return selectedGetOnreservations;
	}

	public List<Reservation> getSelectedRidingReservations() {
		List<Reservation> selectedGetOffReservations = new LinkedList<Reservation>();
		for (Reservation reservation : selectedReservations) {
			if (PassengerRecords.isRiding(reservation)) {
				selectedGetOffReservations.add(reservation);
			}
		}
		return selectedGetOffReservations;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView != null ? convertView : layoutInflater.inflate(
				RESOURCE_ID, null);
		final Reservation reservation = getItem(position);
		if (!reservation.getPassengerRecord().isPresent()) {
			return view;
		}
		final PassengerRecord passengerRecord = reservation
				.getPassengerRecord().get();

		TextView passengerCountTextView = (TextView) view
				.findViewById(R.id.passenger_count_text_view);
		passengerCountTextView.setText(reservation.getPassengerCount() + "名");

		// 人数変更UI
		// Spinner spinner = (Spinner) view
		// .findViewById(R.id.change_passenger_count_spinner);
		// List<String> passengerCounts = new LinkedList<String>();
		// Integer max = reservation.getPassengerCount() + 10;
		// for (Integer i = 1; i <= max; ++i) {
		// passengerCounts.add(i + "名");
		// }
		// spinner.setAdapter(new ArrayAdapter<String>(getContext(),
		// android.R.layout.simple_list_item_1, passengerCounts));
		// try {
		// Integer count = passengerRecord.getPassengerCount() - 1;
		// spinner.setSelection(count);
		// } catch (RuntimeException e) {
		// Log.w(TAG, e);
		// }
		// spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view,
		// int position, long id) {
		// passengerRecord.setPassengerCount(position + 1);
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> parent) {
		// }
		// });

		// メモボタン
		Button memoButton = (Button) view.findViewById(R.id.memo_button);
		if (reservation.getMemo().isPresent()) {
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

		// 復路ボタン
		Button returnPathButton = (Button) view
				.findViewById(R.id.return_path_button);
		returnPathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				commonLogic.postEvent(new ReturnPathModalView.ShowEvent(
						reservation));
			}
		});

		// 支払いボタン
		final ToggleButton paidButton = (ToggleButton) view
				.findViewById(R.id.paid_button);
		if (reservation.getPayment().equals(0)) {
			paidButton.setVisibility(View.GONE);
		} else if (payTiming.contains(PayTiming.GET_OFF)
				&& payTiming.contains(PayTiming.GET_ON)) {
			paidButton.setVisibility(View.VISIBLE);
		} else if (payTiming.contains(PayTiming.GET_OFF)
				&& PassengerRecords.isRiding(reservation)) {
			paidButton.setVisibility(View.VISIBLE);
		} else {
			paidButton.setVisibility(View.GONE);
		}

		if (passengerRecord.getPayment().isPresent()) {
			paidButton.setChecked(true);
		} else {
			paidButton.setChecked(false);
		}
		paidButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					passengerRecord.setPayment(reservation.getPayment());
				} else {
					passengerRecord.clearPayment();
				}
				notifyDataSetChanged();
			}
		});

		// 行の表示
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isSelected(reservation)) {
					unselect(reservation);
					if (paidButton.isShown()) {
						paidButton.setChecked(false);
					}
				} else {
					select(reservation);
					if (paidButton.isShown()) {
						paidButton.setChecked(true);
					}
				}
				notifyDataSetChanged();
			}
		});
		TextView userNameView = (TextView) view.findViewById(R.id.user_name);
		if (reservation.getUser().isPresent()) {
			User user = reservation.getUser().get();
			userNameView.setText(user.getLastName() + " " + user.getFirstName()
					+ " 様");
		} else {
			userNameView.setText("ID:" + reservation.getUserId() + " 様");
		}

		String text = "";
		if (isFutureGetOn(reservation) || isMissed(reservation)) {
			text += "[*乗]";
		} else if (isGetOn(reservation)) {
			text += "[乗]";
		} else if (isRidingAndNotGetOff(reservation)) {
			text += "[*降]";
		} else {
			text += "[降]";
		}

		text += " 予約番号 " + reservation.getId();
		TextView reservationIdView = (TextView) view
				.findViewById(R.id.reservation_id);
		reservationIdView.setText(text);

		if (isSelected(reservation)) {
			view.setBackgroundColor(Color.CYAN); // TODO テーマ
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
		}
		return view;
	}

	public void hide(ItemType itemType) {
		visibleItemTypes.remove(itemType);
		updateDataSet();
	}

	private Boolean isFutureGetOn(Reservation reservation) {
		if (remainingOperationSchedules.size() <= 1) {
			return false;
		}
		for (OperationSchedule operationSchedule : remainingOperationSchedules
				.subList(1, remainingOperationSchedules.size())) {
			if (reservation.getDepartureScheduleId().isPresent()
					&& reservation.getDepartureScheduleId().get()
							.equals(operationSchedule.getId())) {
				return true;
			}
		}
		return false;
	}

	private Boolean isGetOff(Reservation reservation) {
		// 乗車中でない場合false
		if (!PassengerRecords.isRiding(reservation)) {
			return false;
		}
		// 乗車中の場合は、乗車予定かどうかを返す
		return reservation.getArrivalScheduleId().isPresent()
				&& reservation.getArrivalScheduleId().get()
						.equals(operationSchedule.getId());
	}

	private Boolean isGetOn(Reservation reservation) {
		// 未乗車では無い場合はfalse
		if (!PassengerRecords.isUnhandled(reservation)) {
			return false;
		}
		// 未乗車の場合は、乗車予定かどうかを返す
		return reservation.getDepartureScheduleId().isPresent()
				&& reservation.getDepartureScheduleId().get()
						.equals(operationSchedule.getId());
	}

	private Boolean isMissed(Reservation reservation) {
		if (!reservation.getPassengerRecord().isPresent()) {
			return false;
		}
		// 未乗車では無い場合はfalse
		PassengerRecord passengerRecord = reservation.getPassengerRecord()
				.get();
		if (!passengerRecord.getStatus().equals(
				PassengerRecords.Status.UNHANDLED)) {
			return false;
		}
		// 未乗車の場合かつ、現在以降で乗車予定の場合はfalse
		for (OperationSchedule operationSchedule : remainingOperationSchedules) {
			if (reservation.getDepartureScheduleId().isPresent()
					&& reservation.getDepartureScheduleId().get()
							.equals(operationSchedule.getId())) {
				return false;
			}
		}
		return true;
	}

	private Boolean isRidingAndNotGetOff(Reservation reservation) {
		return PassengerRecords.isRiding(reservation) && !isGetOff(reservation);
	}

	public Boolean isSelected(Reservation reservation) {
		return selectedReservations.contains(reservation);
	}

	public void select(Reservation reservation) {
		selectedReservations.add(reservation);
		commonLogic.postEvent(new SelectedReservationsUpdateEvent(
				selectedReservations));
	}

	public void show(ItemType itemType) {
		visibleItemTypes.add(itemType);
		updateDataSet();
	}

	public void unselect(Reservation reservation) {
		selectedReservations.remove(reservation);
		commonLogic.postEvent(new SelectedReservationsUpdateEvent(
				selectedReservations));
	}

	private void updateDataSet() {
		clear();
		for (Reservation reservation : reservations) {
			add(reservation);
		}
		notifyDataSetChanged();
	}
}
