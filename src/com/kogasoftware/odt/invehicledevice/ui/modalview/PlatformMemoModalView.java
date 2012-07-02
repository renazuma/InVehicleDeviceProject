package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class PlatformMemoModalView extends ModalView {
	public PlatformMemoModalView(Context context, InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.platform_memo_modal_view);
		setCloseOnClick(R.id.platform_memo_close_button);
	}

	@Override
	public void show() {
		for (OperationSchedule operationSchedule : service
				.getCurrentOperationSchedule().asSet()) {
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				for (String memo : platform.getMemo().asSet()) {
					TextView memoTextView = (TextView) findViewById(R.id.platform_memo_text_view);
					memoTextView.setText(memo);
					super.show();
				}
			}
		}
	}
}
