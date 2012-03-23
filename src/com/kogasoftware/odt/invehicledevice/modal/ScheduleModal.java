package com.kogasoftware.odt.invehicledevice.modal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;

class OperationScheduleArrayAdapter extends ArrayAdapter<OperationSchedule> {
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
			getOnHeads += reservation.getHead();
		}

		Long getOffHeads = 0L;
		for (Reservation reservation : operationSchedule
				.getReservationsAsDeparture()) {
			getOffHeads += reservation.getHead();
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

public class ScheduleModal extends Modal {

	final List<OperationSchedule> operationSchedules;

	public ScheduleModal(Context context, List<OperationSchedule> operationSchedules) {
		super(context, R.layout.schedule_modal);
		this.operationSchedules = operationSchedules;
	}

	@Deprecated
	public ScheduleModal(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.schedule_modal);

		operationSchedules = new LinkedList<OperationSchedule>();

		createTestData();

	}

	@Deprecated
	private void createTestData() {
		List<OperationSchedule> l = operationSchedules;
		try {
			JSONObject j1 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: 'コガソフトウェア前'}, "
					+ "reservations_as_arrival: [{head: 5}, {head: 6}, {head: 7}] ,"
					+ "reservations_as_departure: [{head: 15}, {head: 16}, {head: 17}]}");
			l.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T03:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T04:00:00.000+09:00', "
					+ "platform: {name: '上野御徒町駅前'}, "
					+ "reservations_as_arrival: [{head: 5}]}");
			l.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T05:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T06:00:00.000+09:00', "
					+ "platform: {name: '上野動物園前'}, "
					+ "reservations_as_departure: [{head: 5}, {head: 6}, {head: 7}]}");
			l.add(new OperationSchedule(j3));

			JSONObject j4 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T07:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T08:00:00.000+09:00', "
					+ "platform: {name: '上野広小路前'}, "
					+ "reservations_as_arrival: [] ,"
					+ "reservations_as_departure: [{head: 7}]}");
			l.add(new OperationSchedule(j4));

			JSONObject j5 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T09:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T09:01:00.000+09:00', "
					+ "platform: {name: '湯島天神前'}}");
			l.add(new OperationSchedule(j5));

			JSONObject j6 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T09:03:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T09:03:30.000+09:00', "
					+ "platform: {name: 'コガソフトウェア前'}, "
					+ "reservations_as_arrival: [{head: 50}, {head: 60}, {head: 70}] ,"
					+ "reservations_as_departure: [{head: 150}, {head: 160}, {head: 170}]}");
			l.add(new OperationSchedule(j6));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();


		OperationScheduleArrayAdapter adapter = new OperationScheduleArrayAdapter(
				getContext(), R.layout.operation_schedule_list_row, operationSchedules);
		ListView operationScheduleListView = (ListView) findViewById(R.id.operation_schedule_list_view);
		operationScheduleListView.setAdapter(adapter);
	}
}
