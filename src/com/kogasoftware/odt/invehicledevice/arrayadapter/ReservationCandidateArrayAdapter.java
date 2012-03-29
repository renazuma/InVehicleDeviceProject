package com.kogasoftware.odt.invehicledevice.arrayadapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;

public class ReservationCandidateArrayAdapter extends
ArrayAdapter<ReservationCandidate> {
	private final LayoutInflater layoutInflater;
	private final int resourceId;

	public ReservationCandidateArrayAdapter(InVehicleDeviceActivity inVehicleDeviceActivity, int resourceId,
			List<ReservationCandidate> items) {
		super(inVehicleDeviceActivity, resourceId, items);
		this.layoutInflater = (LayoutInflater) inVehicleDeviceActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resourceId = resourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(resourceId, null);
		}
		TextView v = (TextView)convertView.findViewById(R.id.reservation_candidate_text_view);
		ReservationCandidate c = getItem(position);
		v.setText(c.getDepartureTime() + " → " + c.getArrivalTime());
		return convertView;
	}
}