package com.kogasoftware.odt.invehicledevice.arrayadapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class ReservationArrayAdapter extends ArrayAdapter<Reservation> {
	private static final String TAG = ReservationArrayAdapter.class.getName();
	private final LayoutInflater layoutInflater = (LayoutInflater) getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	private final int resourceId;
	private final InVehicleDeviceLogic logic;
	private final List<OperationSchedule> remainingOperationSchedules = new LinkedList<OperationSchedule>();
	private final List<Reservation> ridingAndNoOutgoingReservations = new LinkedList<Reservation>();
	private final List<Reservation> futureReservations = new LinkedList<Reservation>();
	private final List<Reservation> missedReservations = new LinkedList<Reservation>();
	private final List<Reservation> incomingReservations = new LinkedList<Reservation>();
	private final List<Reservation> outgoingReservations = new LinkedList<Reservation>();
	private final List<Reservation> checkedReservations = new LinkedList<Reservation>();
	private final List<Reservation> paidReservations = new LinkedList<Reservation>();
	private final List<Reservation> ridingReservations = new LinkedList<Reservation>();
	private final OperationSchedule operationSchedule;

	public List<Reservation> getCheckedIncomingReservations() { // TODO 名前を変える
		List<Reservation> reservations = new LinkedList<Reservation>();
		Integer count = getCount();
		for (int i = 0; i < count; ++i) {
			Reservation reservation = getItem(i);
			if (checkedReservations.contains(reservation)
					&& !ridingReservations.contains(reservation)) {
				reservations.add(reservation);
			}
		}
		return reservations;
	}

	public List<Reservation> getCheckedOutgoingReservations() {
		List<Reservation> reservations = new LinkedList<Reservation>();
		Integer count = getCount();
		for (int i = 0; i < count; ++i) {
			Reservation reservation = getItem(i);
			if (checkedReservations.contains(reservation)
					&& ridingReservations.contains(reservation)) {
				reservations.add(reservation);
			}
		}
		return reservations;
	}

	public List<Reservation> getNoPaymentReservations() {
		List<Reservation> reservations = getCheckedOutgoingReservations();
		reservations.removeAll(paidReservations);
		return reservations;
	}

	public List<Reservation> getNoGettingOnReservations() {
		List<Reservation> reservations = new LinkedList<Reservation>(
				incomingReservations);
		reservations.removeAll(checkedReservations);
		return reservations;
	}

	public List<Reservation> getNoGettingOutReservations() {
		List<Reservation> reservations = new LinkedList<Reservation>(
				outgoingReservations);
		reservations.removeAll(checkedReservations);
		return reservations;
	}

	public Boolean isOutgoingScheduledReservation(Reservation reservation) {
		// TODO: 降りる予定かの判定
		Boolean outgoing = false;
		for (Reservation arrivalReservation : operationSchedule
				.getReservationsAsArrival()) {
			if (arrivalReservation.getId().equals(reservation.getId())) {
				outgoing = true;
				break;
			}
		}
		// TODO: 降りる予定かの判定
		if (reservation.getArrivalScheduleId().isPresent()
				&& reservation.getArrivalScheduleId().get()
				.equals(operationSchedule.getId())) {
			outgoing = true;
		}
		return outgoing;
	}

	public Boolean isIncomingScheduledReservation(Reservation reservation) {
		// TODO: のる予定かの判定
		Boolean incoming = false;
		for (Reservation departureReservation : operationSchedule
				.getReservationsAsDeparture()) {
			if (departureReservation.getId().equals(reservation.getId())) {
				incoming = true;
				break;
			}
		}
		// TODO: のる予定かの判定
		if (reservation.getDepartureScheduleId().isPresent()
				&& reservation.getDepartureScheduleId().get()
				.equals(operationSchedule.getId())) {
			incoming = true;
		}
		return incoming;
	}

	public ReservationArrayAdapter(Context context, int resourceId,
			InVehicleDeviceLogic logic) {
		super(context, resourceId);
		this.resourceId = resourceId;
		this.logic = logic;

		remainingOperationSchedules.addAll(logic
				.getRemainingOperationSchedules());
		if (!remainingOperationSchedules.isEmpty()) {
			operationSchedule = this.remainingOperationSchedules.get(0);
		} else {
			operationSchedule = new OperationSchedule();
			Log.e(TAG, "remainingOperationSchedules.isEmpty()",
					new RuntimeException());
			return;
		}
		ridingReservations.addAll(logic.getRidingReservations());
		missedReservations.addAll(logic.getMissedReservations());

		for (OperationSchedule remainingOperationSchedule : remainingOperationSchedules) {
			if (remainingOperationSchedule == remainingOperationSchedules
					.get(0)) {
				continue;
			}
			futureReservations.addAll(remainingOperationSchedule
					.getReservationsAsDeparture());
		}

		for (Reservation reservation : ridingReservations) {
			if (isOutgoingScheduledReservation(reservation)) {
				outgoingReservations.add(reservation);
			} else {
				ridingAndNoOutgoingReservations.add(reservation);
			}
		}

		for (Reservation reservation : logic.getUnexpectedReservations()) {
			if (isOutgoingScheduledReservation(reservation)) {
				outgoingReservations.add(reservation);
			} else {
				ridingAndNoOutgoingReservations.add(reservation);
			}
		}

		incomingReservations.addAll(operationSchedule
				.getReservationsAsDeparture());

		for (List<Reservation> reservations : Lists
				.<List<Reservation>> newArrayList(incomingReservations,
						ridingReservations, missedReservations,
						futureReservations,
						logic.getUnexpectedReservations())) {
			for (Reservation reservation : reservations) {
				add(reservation);
			}
		}

		hideFutureReservations();
		hideMissedReservations();
		hideRidingAndNotGetOutReservations();

		notifyDataSetChanged();
	}

	public void addUnexpectedReservation(Reservation reservation) {
		if (isOutgoingScheduledReservation(reservation)) {
			List<Reservation> reservations = operationSchedule
					.getReservationsAsArrival();
			reservations.add(reservation);
			operationSchedule.setReservationsAsArrival(reservations);
		} else if (isIncomingScheduledReservation(reservation)) {
			List<Reservation> reservations = operationSchedule
					.getReservationsAsDeparture();
			reservations.add(reservation);
			operationSchedule.setReservationsAsDeparture(reservations);
		} else {
			// TODO warning
			return;
		}

		add(reservation);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = convertView != null ? convertView : layoutInflater
				.inflate(resourceId, null);
		final Reservation reservation = getItem(position);
		Spinner spinner = (Spinner) view.findViewById(R.id.change_head_spinner);

		List<String> passengerCounts = new LinkedList<String>();
		Integer max = reservation.getPassengerCount() + 10;
		for (Integer i = 1; i <= max; ++i) {
			passengerCounts.add(i + "名");
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_item, passengerCounts);
		spinner.setAdapter(adapter);
		spinner.setSelection(reservation.getPassengerCount() - 1, true);

		TextView userNameView = (TextView) view.findViewById(R.id.user_name);

		if (reservation.getUser().isPresent()) {
			User user = reservation.getUser().get();
			userNameView.setText(user.getLastName() + " " + user.getFirstName()
					+ " 様");
		} else {
			userNameView.setText("ID:" + reservation.getUserId() + " 様");
		}

		String text = "";

		if (isRidingAndNotGetOut(reservation)) {
			text += "[＊降]";
		} else if (ridingReservations.contains(reservation)) {
			text += "[降]";
		} else if (isFuture(reservation)) {
			text += "[＊乗]";
		} else if (isMissed(reservation)) {
			text += "[＊乗]";
		} else {
			text += "[乗]";
		}

		text += " 予約番号 " + reservation.getId();

		TextView reservationIdView = (TextView) view
				.findViewById(R.id.reservation_id);
		reservationIdView.setText(text);

		final ToggleButton paidButton = (ToggleButton) view
				.findViewById(R.id.paid_button);
		if (ridingReservations.contains(reservation)) {
			paidButton.setVisibility(View.VISIBLE);
		} else {
			paidButton.setVisibility(View.GONE);
		}
		if (paidReservations.contains(reservation)) {
			paidButton.setChecked(true);
		} else {
			paidButton.setChecked(false);
		}
		paidButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					paidReservations.add(reservation);
				} else {
					paidReservations.remove(reservation);
				}
				notifyDataSetChanged();
			}
		});

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (checkedReservations.contains(reservation)) {
					checkedReservations.remove(reservation);
					paidButton.setChecked(false);
				} else {
					checkedReservations.add(reservation);
					paidButton.setChecked(true);
				}
				notifyDataSetChanged();
			}
		});

		if (checkedReservations.contains(reservation)) {
			view.setBackgroundColor(Color.CYAN); // TODO テーマ
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
		}
		Button memoButton = (Button) view.findViewById(R.id.memo_button);
		if (reservation.getMemo().isPresent()) {
			memoButton.setVisibility(View.VISIBLE);
			memoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					logic.showMemoModal(reservation);
				}
			});
		} else {
			// Viewが再利用されることがあるため、明示的に消す
			memoButton.setVisibility(View.GONE);
		}
		Button returnPathButton = (Button) view
				.findViewById(R.id.return_path_button);
		returnPathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logic.showReturnPathModal(reservation);
			}
		});

		return view;
	}

	public void hideFutureReservations() {
		hideReservations(futureReservations);
	}

	public void hideMissedReservations() {
		hideReservations(missedReservations);
	}

	private void hideReservations(List<Reservation> reservations) {
		for (Reservation reservation : reservations) {
			for (Integer i = 0; i < getCount(); ++i) {
				Reservation search = getItem(i);
				if (search != null
						&& search.getId().equals(reservation.getId())) {
					remove(search);
				}
			}
		}
	}

	public void hideRidingAndNotGetOutReservations() {
		hideReservations(ridingAndNoOutgoingReservations);
	}

	private Boolean isFuture(Reservation reservation) {
		for (Reservation futureReservation : futureReservations) {
			if (futureReservation.getId().equals(reservation.getId())) {
				return true;
			}
		}
		return false;
	}

	private Boolean isMissed(Reservation reservation) {
		for (Reservation missedReservation : missedReservations) {
			if (missedReservation.getId().equals(reservation.getId())) {
				return true;
			}
		}
		return false;
	}

	private Boolean isRidingAndNotGetOut(Reservation reservation) {
		for (Reservation ridingAndNoGetOutReservation : ridingAndNoOutgoingReservations) {
			if (ridingAndNoGetOutReservation.getId()
					.equals(reservation.getId())) {
				return true;
			}
		}
		return false;
	}

	public void showFutureReservations() {
		showReservations(futureReservations);
	}

	public void showMissedReservations() {
		showReservations(missedReservations);
	}

	public void showReservations(List<Reservation> reservations) {
		for (Reservation reservation : reservations) {
			Boolean found = false;
			for (Integer i = 0; i < getCount(); ++i) {
				Reservation search = getItem(i);
				if (search != null
						&& search.getId().equals(reservation.getId())) {
					found = true;
					break;
				}
			}
			if (!found) {
				add(reservation);
			}
		}
		notifyDataSetChanged();
	}

	public void showRidingAndNoOutgoingReservations() {
		showReservations(ridingAndNoOutgoingReservations);
	}
}
