package com.kogasoftware.odt.invehicledevice.modal;

import java.util.LinkedList;
import java.util.List;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;


public class ScheduleModal extends Modal {

	private final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();

	public ScheduleModal(InVehicleDeviceActivity inVehicleDeviceActivity) {
		super(inVehicleDeviceActivity, R.layout.schedule_modal);
		setId(R.id.schedule_modal);
	}

	public void show(List<OperationSchedule> operatioSchedules) {
		super.hide();
		this.operationSchedules.clear();
		this.operationSchedules.addAll(operatioSchedules);
		OperationScheduleArrayAdapter adapter = new OperationScheduleArrayAdapter(
				getContext(), R.layout.operation_schedule_list_row, operationSchedules);
		final ListView operationScheduleListView = (ListView) findViewById(R.id.operation_schedule_list_view);
		operationScheduleListView.setAdapter(adapter);

		Button operationScheduleScrollUpButton = (Button) findViewById(R.id.operation_schedule_scroll_up_button);
		operationScheduleScrollUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = operationScheduleListView.getFirstVisiblePosition();
				operationScheduleListView.smoothScrollToPosition(position);
			}
		});
		Button operationScheduleScrollDownButton = (Button) findViewById(R.id.operation_schedule_scroll_down_button);
		operationScheduleScrollDownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = operationScheduleListView.getLastVisiblePosition();
				operationScheduleListView.smoothScrollToPosition(position);
			}
		});

		super.show();
	}
}
