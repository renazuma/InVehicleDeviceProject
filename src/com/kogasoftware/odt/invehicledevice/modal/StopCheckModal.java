package com.kogasoftware.odt.invehicledevice.modal;

import android.view.View;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;

public class StopCheckModal extends Modal {
	public StopCheckModal(final InVehicleDeviceActivity inVehicleDeviceActivity) {
		super(inVehicleDeviceActivity, R.layout.stop_check_modal);
		setId(R.id.stop_check_overlay);

		Button stopButton = (Button) findViewById(R.id.stop_button);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				inVehicleDeviceActivity.showStopModal();
			}
		});
	}

	@Override
	public void show() {
		super.show();
	}
}
