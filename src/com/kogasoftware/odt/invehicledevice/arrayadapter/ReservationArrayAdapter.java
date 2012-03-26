package com.kogasoftware.odt.invehicledevice.arrayadapter;

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

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class ReservationArrayAdapter extends ArrayAdapter<Reservation> {
	private final LayoutInflater layoutInflater;
	private final int resourceId;
	private final InVehicleDeviceActivity inVehicleDeviceActivity;

	public ReservationArrayAdapter(
			InVehicleDeviceActivity inVehicleDeviceActivity, int resourceId,
			List<Reservation> items) {
		super(inVehicleDeviceActivity, resourceId, items);
		this.layoutInflater = (LayoutInflater) inVehicleDeviceActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resourceId = resourceId;
		this.inVehicleDeviceActivity = inVehicleDeviceActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(resourceId, null);
		}
		Spinner spinner = (Spinner) convertView
				.findViewById(R.id.change_head_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_item, new String[] { "1名",
			"2名", "3名" });
		spinner.setAdapter(adapter);

		final Reservation reservation = getItem(position);
		TextView userNameView = (TextView) convertView
				.findViewById(R.id.user_name);

		if (reservation.getUser().isPresent()) {
			User user = reservation.getUser().get();
			userNameView.setText(user.getFamilyName() + " "
					+ user.getLastName() + " 様");
		} else {
			userNameView.setText("ID:" + reservation.getUserId() + " 様");
		}

		TextView reservationIdView = (TextView) convertView
				.findViewById(R.id.reservation_id);
		reservationIdView.setText("[乗] 予約番号 " + reservation.getId());
		if (reservation.getMemo().isPresent()) {
			Button memoButton = (Button) convertView
					.findViewById(R.id.memo_button);
			memoButton.setVisibility(View.VISIBLE);
			memoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					inVehicleDeviceActivity.showMemoModal(reservation);
				}
			});
		}
		Button returnPathButton = (Button) convertView
				.findViewById(R.id.return_path_button);
		returnPathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				inVehicleDeviceActivity.showReturnPathModal(reservation);
			}
		});

		return convertView;
	}
}