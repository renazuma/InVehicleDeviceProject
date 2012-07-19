package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class ArrivalCheckModalView extends ModalView {
	public ArrivalCheckModalView(Context context, InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.arrival_check_modal_view);
		setCloseOnClick(R.id.arrival_check_close_button);
	}

	@Override
	public void show() {
		Button arrivalButton = (Button) findViewById(R.id.arrival_button);
		for (OperationSchedule operationSchedule : service
				.getCurrentOperationSchedule().asSet()) {
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				TextView commentTextView = (TextView) findViewById(R.id.arrival_check_comment_text_view);
				commentTextView.setText(platform.getName());
			}
		}

		arrivalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				service.enterPlatformPhase();
				hide();
			}
		});
		super.show();
	}
}
