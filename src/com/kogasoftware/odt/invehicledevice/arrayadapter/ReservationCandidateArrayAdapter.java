package com.kogasoftware.odt.invehicledevice.arrayadapter;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;

public class ReservationCandidateArrayAdapter extends
ArrayAdapter<ReservationCandidate> {
	private final LayoutInflater layoutInflater;
	private final int resourceId;
	private final Context context;

	public ReservationCandidateArrayAdapter(
			InVehicleDeviceActivity inVehicleDeviceActivity, int resourceId,
			List<ReservationCandidate> items) {
		super(inVehicleDeviceActivity, resourceId, items);
		this.layoutInflater = (LayoutInflater) inVehicleDeviceActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resourceId = resourceId;
		inVehicleDeviceActivity.getApplicationContext();
		setNotifyOnChange(true);
		selectedPosition = Optional.<Integer> of(300);
		context = inVehicleDeviceActivity;
	}

	private Optional<Integer> selectedPosition;

	public void setSelectedPosition(Optional<Integer> selectedPosition) {
		this.selectedPosition = selectedPosition;
		notifyDataSetChanged();
	}

	public Optional<Integer> getSelectedPosition() {
		return selectedPosition;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(resourceId, null);
		}

		if (selectedPosition.isPresent()
				&& selectedPosition.get().equals(position)) {
			//convertView.setBackgroundDrawable(context.getResources()
			//		.getDrawable(android.R.drawable.list_selector_background));
			convertView.setBackgroundColor(Color.CYAN); // TODO テーマ
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}

		TextView v = (TextView) convertView
				.findViewById(R.id.reservation_candidate_text_view);
		ReservationCandidate c = getItem(position);
		String s = "";
		Date d = c.getDepartureTime();
		Date a = c.getArrivalTime();
		if (c.getDeparturePlatform().isPresent()) {
			s += c.getDeparturePlatform().get().getName();
		} else {
			s += "ID:" + c.getDeparturePlatformId();
		}
		s += " → ";
		if (c.getArrivalPlatform().isPresent()) {
			s += c.getArrivalPlatform().get().getName();
		} else {
			s += "ID:" + c.getArrivalPlatformId();
		}
		s += " / " + d.getHours() + ":" + d.getMinutes() + " → " + a.getHours()
				+ ":" + a.getMinutes();
		v.setText(s);
		return convertView;
	}
}