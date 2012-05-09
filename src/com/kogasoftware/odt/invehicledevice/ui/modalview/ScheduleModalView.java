package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class ScheduleModalView extends ModalView {
	public static class ShowEvent {
	}

	private final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();

	public ScheduleModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.schedule_modal_view);
		setCloseOnClick(R.id.schedule_close_button);
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.hide();
		this.operationSchedules.clear();
		this.operationSchedules.addAll(getCommonLogic()
				.getFinishedOperationSchedules());
		this.operationSchedules.addAll(getCommonLogic()
				.getRemainingOperationSchedules());
		OperationScheduleArrayAdapter adapter = new OperationScheduleArrayAdapter(
				getContext(), operationSchedules, getCommonLogic());
		final ListView operationScheduleListView = (ListView) findViewById(R.id.operation_schedule_list_view);
		operationScheduleListView.setAdapter(adapter);

		Button operationScheduleScrollUpButton = (Button) findViewById(R.id.operation_schedule_scroll_up_button);
		operationScheduleScrollUpButton
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Integer position = operationScheduleListView
								.getFirstVisiblePosition();
						operationScheduleListView
								.smoothScrollToPosition(position);
					}
				});
		Button operationScheduleScrollDownButton = (Button) findViewById(R.id.operation_schedule_scroll_down_button);
		operationScheduleScrollDownButton
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						Integer position = operationScheduleListView
								.getLastVisiblePosition();
						operationScheduleListView
								.smoothScrollToPosition(position);
					}
				});

		super.show();
	}
}
