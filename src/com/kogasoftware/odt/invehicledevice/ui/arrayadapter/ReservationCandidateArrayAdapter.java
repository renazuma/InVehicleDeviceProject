package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

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
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;

public class ReservationCandidateArrayAdapter extends
		ArrayAdapter<ReservationCandidate> {
	private final LayoutInflater layoutInflater;
	private static final Integer RESOURCE_ID = R.layout.reservation_candidate_list_row;

	private Optional<Integer> selectedPosition = Optional.absent();

	public ReservationCandidateArrayAdapter(Context context,
			List<ReservationCandidate> items) {
		super(context, RESOURCE_ID, items);
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setNotifyOnChange(true);
	}

	public Optional<Integer> getSelectedPosition() {
		return selectedPosition;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = convertView != null ? convertView : layoutInflater
				.inflate(RESOURCE_ID, null);

		if (selectedPosition.isPresent()
				&& selectedPosition.get().equals(position)) {
			// convertView.setBackgroundDrawable(context.getResources()
			// .getDrawable(android.R.drawable.list_selector_background));
			view.setBackgroundColor(Color.CYAN); // TODO テーマ
		} else {
			view.setBackgroundColor(Color.TRANSPARENT);
		}

		TextView v = (TextView) view
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
		s += " / " + d.getHours() + "時" + d.getMinutes() + "分 → "
				+ a.getHours() + "時" + a.getMinutes() + "分";
		v.setText(s);
		return view;
	}

	public void setSelectedPosition(Optional<Integer> selectedPosition) {
		this.selectedPosition = selectedPosition;
		notifyDataSetChanged();
	}
}