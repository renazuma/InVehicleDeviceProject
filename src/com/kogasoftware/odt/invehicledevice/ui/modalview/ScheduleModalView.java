package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class ScheduleModalView extends ModalView {
	final ListView operationScheduleListView;

	private final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();

	public ScheduleModalView(Context context, InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.schedule_modal_view);
		setCloseOnClick(R.id.schedule_close_button);
		operationScheduleListView = ((FlickUnneededListView) findViewById(R.id.operation_schedule_list_view))
				.getListView();
	}

	@Override
	public void show() {
		super.hide();
		operationSchedules.clear();
		operationSchedules.addAll(service.getFinishedOperationSchedules());
		operationSchedules.addAll(service.getRemainingOperationSchedules());
		OperationScheduleArrayAdapter adapter = new OperationScheduleArrayAdapter(
				service, operationSchedules);
		operationScheduleListView.setAdapter(adapter);
		Integer extraItems = 1;
		for (OperationSchedule currentOperationSchedule : service
				.getCurrentOperationSchedule().asSet()) {
			for (Integer i = 1; i < (adapter.getCount() - extraItems); ++i) {
				if (adapter.getItem(i).getId()
						.equals(currentOperationSchedule.getId())) {
					operationScheduleListView.smoothScrollToPosition(i
							+ extraItems);
					break;
				}
			}
		}
		super.show();
	}
}
