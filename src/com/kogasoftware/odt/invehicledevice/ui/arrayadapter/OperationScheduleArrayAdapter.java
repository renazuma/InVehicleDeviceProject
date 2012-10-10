package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;

public class OperationScheduleArrayAdapter extends
		ArrayAdapter<OperationSchedule> {
	private static final Integer RESOURCE_ID = R.layout.operation_schedule_list_row;
	private final LayoutInflater layoutInflater;
	private final OperationScheduleLogic operationScheduleLogic;

	public OperationScheduleArrayAdapter(Context context,
			InVehicleDeviceService service) {
		super(context, RESOURCE_ID);
		operationScheduleLogic = new OperationScheduleLogic(service);
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (OperationSchedule operationSchedule : operationScheduleLogic
				.getOperationSchedules()) {
			add(operationSchedule);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(RESOURCE_ID, null);
		}
		DateFormat displayDateFormat = new SimpleDateFormat("HH:mm");

		OperationSchedule operationSchedule = getItem(position);
		TextView platformNameView = (TextView) convertView
				.findViewById(R.id.platform_name);
		if (operationSchedule.getPlatform().isPresent()) {
			platformNameView.setText(operationSchedule.getPlatform().get()
					.getName());
		} else {
			platformNameView.setText("ID:" + operationSchedule.getId());
		}

		Integer getOnPassengerCount = 0;
		for (Reservation reservation : operationSchedule
				.getReservationsAsDeparture()) {
			getOnPassengerCount += reservation.getPassengerCount();
		}

		Integer getOffPassengerCount = 0;
		for (Reservation reservation : operationSchedule
				.getReservationsAsArrival()) {
			getOffPassengerCount += reservation.getPassengerCount();
		}

		TextView getOnPassengerCountTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_get_on_passenger_count_text_view);
		getOnPassengerCountTextView.setText("乗車"
				+ String.format("%3d", getOnPassengerCount) + "名");
		getOnPassengerCountTextView
				.setVisibility(getOnPassengerCount.equals(0) ? View.INVISIBLE
						: View.VISIBLE);

		TextView getOffPassengerCountTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_get_off_passenger_count_text_view);
		getOffPassengerCountTextView.setText("降車"
				+ String.format("%3d", getOffPassengerCount) + "名");
		getOffPassengerCountTextView.setVisibility(getOffPassengerCount
				.equals(0) ? View.INVISIBLE : View.VISIBLE);

		TextView arrivalEstimateTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_arrival_estimate_text_view);
		TextView departureEstimateTextView = (TextView) convertView
				.findViewById(R.id.operation_schedule_departure_estimate_text_view);

		arrivalEstimateTextView.setText("");
		departureEstimateTextView.setText("");

		for (Date arrivalEstimate : operationSchedule.getArrivalEstimate()
				.asSet()) {
			arrivalEstimateTextView.setText(displayDateFormat
					.format(arrivalEstimate) + " 着");
		}

		if (getCount() != position + 1) {
			for (Date departureEstimate : operationSchedule
					.getDepartureEstimate().asSet()) {
				departureEstimateTextView.setText(displayDateFormat
						.format(departureEstimate) + " 発");
			}
		}

		if (operationScheduleLogic.getRemainingOperationSchedules()
				.contains(operationSchedule)) {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		} else {
			convertView.setBackgroundColor(Color.LTGRAY);
		}
		return convertView;
	}
}
