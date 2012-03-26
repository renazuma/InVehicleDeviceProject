package com.kogasoftware.odt.invehicledevice.arrayadapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;

public class OperationScheduleArrayAdapter extends ArrayAdapter<OperationSchedule> {
	private final LayoutInflater layoutInflater;
	private final int resourceId;

	private final DateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("H時m分"); // staticにはしない

	public OperationScheduleArrayAdapter(Context context, int resourceId,
			List<OperationSchedule> items) {
		super(context, resourceId, items);
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resourceId = resourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(resourceId, null);
		}

		OperationSchedule operationSchedule = getItem(position);
		TextView platformNameView = (TextView) convertView
				.findViewById(R.id.platform_name);
		if (operationSchedule.getPlatform().isPresent()) {
			platformNameView.setText(operationSchedule.getPlatform().get()
					.getName());
		} else {
			platformNameView.setText("ID:" + operationSchedule.getId());
		}
		TextView getOnHeadsTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_get_on_heads_text_view);
		Long getOnHeads = 0L;
		for (Reservation reservation : operationSchedule
				.getReservationsAsArrival()) {
			getOnHeads += reservation.getPassengerCount();
		}

		Long getOffHeads = 0L;
		for (Reservation reservation : operationSchedule
				.getReservationsAsDeparture()) {
			getOffHeads += reservation.getPassengerCount();
		}

		TextView arrivalEstimateTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_arrival_estimate_text_view);
		getOnHeadsTextView.setText(getOnHeads.toString());
		TextView getOffHeadsTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_get_off_heads_text_view);
		getOffHeadsTextView.setText(getOffHeads.toString());
		arrivalEstimateTextView.setText(DISPLAY_DATE_FORMAT
				.format(operationSchedule.getArrivalEstimate()));
		TextView departureEstimateTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_departure_estimate_text_view);
		departureEstimateTextView.setText(DISPLAY_DATE_FORMAT
				.format(operationSchedule.getDepartureEstimate()));
		return convertView;
	}
}
