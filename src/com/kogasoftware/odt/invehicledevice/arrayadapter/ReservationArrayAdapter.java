package com.kogasoftware.odt.invehicledevice.arrayadapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
	private final List<Reservation> ridingAndNoGetOutReservations = new LinkedList<Reservation>();
	private final List<Reservation> futureReservations = new LinkedList<Reservation>();
	private final List<Reservation> missedReservations = new LinkedList<Reservation>();
	private final OperationSchedule operationSchedule;

	public ReservationArrayAdapter(Context context, int resourceId,
			InVehicleDeviceLogic logic) {
		super(context, resourceId);
		this.resourceId = resourceId;
		this.logic = logic;
		this.remainingOperationSchedules.addAll(logic
				.getRemainingOperationSchedules());
		if (!remainingOperationSchedules.isEmpty()) {
			operationSchedule = this.remainingOperationSchedules.get(0);
		} else {
			operationSchedule = new OperationSchedule();
			Log.e(TAG, "remainingOperationSchedules.isEmpty()",
					new RuntimeException());
			return;
		}
		for (OperationSchedule remainingOperationSchedule : remainingOperationSchedules) {
			if (remainingOperationSchedule == remainingOperationSchedules
					.get(0)) {
				continue;
			}
			futureReservations.addAll(remainingOperationSchedule
					.getReservationsAsDeparture());
		}
		for (Reservation ridingReservation : logic.getRidingReservations()) {
			Boolean isGetOut = false;
			for (Reservation departureReservation : operationSchedule
					.getReservationsAsDeparture()) {
				if (departureReservation.getId().equals(
						ridingReservation.getId())) {
					isGetOut = true;
					break;
				}
			}
			if (!isGetOut) {
				ridingAndNoGetOutReservations.add(ridingReservation);
			}
		}

		for (List<Reservation> reservations : Lists
				.<List<Reservation>> newArrayList(
						operationSchedule.getReservationsAsDeparture(),
						operationSchedule.getReservationsAsArrival(),
						missedReservations, ridingAndNoGetOutReservations,
						futureReservations)) {
			for (Reservation reservation : reservations) {
				add(reservation);
			}
		}

		for (Reservation unexpectedReservation : logic
				.getUnexpectedReservations()) {
			if (operationSchedule.getId().equals(
					unexpectedReservation.getArrivalScheduleId())) {
				add(unexpectedReservation);
			}
		}

		hideFutureReservations();
		hideMissedReservations();
		hideRidingAndNotGetOutReservations();

		notifyDataSetChanged();
	}

	public void addUnexpectedReservation(Reservation reservation) {
		if (reservation.getArrivalScheduleId().isPresent()
				&& reservation.getArrivalScheduleId().get()
						.equals(operationSchedule.getId())) {
			List<Reservation> reservations = operationSchedule
					.getReservationsAsArrival();
			reservations.add(reservation);
			operationSchedule.setReservationsAsArrival(reservations);
		} else if (reservation.getDepartureScheduleId().isPresent()
				&& reservation.getDepartureScheduleId().get()
						.equals(operationSchedule.getId())) {
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
		if (convertView == null) {
			convertView = layoutInflater.inflate(resourceId, null);
		}

		final Reservation reservation = getItem(position);
		if (remainingOperationSchedules.isEmpty()) {
			Log.w(TAG, "remainingOperationSchedules.isEmpty()");
			return convertView;
		}
		OperationSchedule operationSchedule = remainingOperationSchedules
				.get(0);

		Spinner spinner = (Spinner) convertView
				.findViewById(R.id.change_head_spinner);

		List<String> passengerCounts = new LinkedList<String>();
		Integer max = reservation.getPassengerCount() + 10;
		for (Integer i = 1; i <= max; ++i) {
			passengerCounts.add(i + "名");
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_item, passengerCounts);
		spinner.setAdapter(adapter);
		spinner.setSelection(reservation.getPassengerCount() - 1, true);

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
		Boolean getOn = false;
		for (Reservation reservationAsArrival : operationSchedule
				.getReservationsAsArrival()) {
			if (reservationAsArrival.getId().equals(reservation.getId())) {
				getOn = true;
				break;
			}
		}

		if (isFuture(reservation)) {
			text += "[＊乗]";
		} else if (isMissed(reservation)) {
			text += "[＊乗]";
		} else if (isRidingAndNotGetOut(reservation)) {
			text += "[＊降]";
		} else if (getOn) {
			text += "[乗]";
		} else {
			text += "[降]";
		}

		text += " 予約番号 " + reservation.getId();

		TextView reservationIdView = (TextView) convertView
				.findViewById(R.id.reservation_id);
		reservationIdView.setText(text);
		Button memoButton = (Button) convertView.findViewById(R.id.memo_button);
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
		Button returnPathButton = (Button) convertView
				.findViewById(R.id.return_path_button);
		returnPathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logic.showReturnPathModal(reservation);
			}
		});

		return convertView;
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
		hideReservations(ridingAndNoGetOutReservations);
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
		for (Reservation ridingAndNoGetOutReservation : ridingAndNoGetOutReservations) {
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

	public void showRidingAndNotGetOutReservations() {
		showReservations(ridingAndNoGetOutReservations);
	}
}