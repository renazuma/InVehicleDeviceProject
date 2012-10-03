package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.os.Handler;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class ScheduleModalView extends ModalView {
	final ListView operationScheduleListView;
	final Handler handler = new Handler();

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
		OperationScheduleArrayAdapter adapter = new OperationScheduleArrayAdapter(
				getContext(), service);
		operationScheduleListView.setAdapter(adapter);

		Integer extraRows = 1;
		// 未運行の運行スケジュールまでスクロールする
		for (OperationSchedule currentOperationSchedule : service
				.getCurrentOperationSchedule().asSet()) {
			for (Integer i = 0; i < adapter.getCount(); ++i) {
				if (adapter.getItem(i).getId()
						.equals(currentOperationSchedule.getId())) {
					operationScheduleListView.setSelectionFromTop(i, 0);
					break;
				}
			}
		}
		super.show();
	}
}
