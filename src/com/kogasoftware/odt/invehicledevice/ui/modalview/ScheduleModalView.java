package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class ScheduleModalView extends ModalView {
	final ListView operationScheduleListView;

	public static class ShowEvent {
	}

	private final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();

	public ScheduleModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.schedule_modal_view);
		setCloseOnClick(R.id.schedule_close_button);
		operationScheduleListView = ((FlickUnneededListView) findViewById(R.id.operation_schedule_list_view))
				.getListView();
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.hide();
		operationSchedules.clear();
		operationSchedules.addAll(getCommonLogic()
				.getFinishedOperationSchedules());
		operationSchedules.addAll(getCommonLogic()
				.getRemainingOperationSchedules());
		OperationScheduleArrayAdapter adapter = new OperationScheduleArrayAdapter(
				getContext(), operationSchedules, getCommonLogic());
		operationScheduleListView.setAdapter(adapter);
		super.show();
	}
}
