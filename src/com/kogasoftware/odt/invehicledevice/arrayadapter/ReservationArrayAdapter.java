package com.kogasoftware.odt.invehicledevice.arrayadapter;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class ReservationArrayAdapter extends ArrayAdapter<Reservation> {
	private final LayoutInflater layoutInflater;
	private final int resourceId;
	private final InVehicleDeviceLogic logic;
	private final OperationSchedule operationSchedule;

	public ReservationArrayAdapter(Context context, int resourceId,
			List<Reservation> items, InVehicleDeviceLogic logic,
			OperationSchedule operationSchedule) {
		super(context, resourceId, items);
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resourceId = resourceId;
		this.logic = logic;
		this.operationSchedule = operationSchedule;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(resourceId, null);
		}

		final Reservation reservation = getItem(position);

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

		if (getOn) {
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
}