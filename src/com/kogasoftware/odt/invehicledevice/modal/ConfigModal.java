package com.kogasoftware.odt.invehicledevice.modal;

import android.view.View;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;


public class ConfigModal extends Modal {
	public ConfigModal(final InVehicleDeviceActivity inVehicleDeviceActivity) {
		super(inVehicleDeviceActivity, R.layout.config_modal);
		this.setId(R.id.config_overlay);

		Button stopCheckButton = (Button) findViewById(R.id.stop_check_button);
		stopCheckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				inVehicleDeviceActivity.showStopCheckModal();
			}
		});

		Button pauseButton = (Button) findViewById(R.id.pause_button);
		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				inVehicleDeviceActivity.showPauseModal();
			}
		});

	}

	@Override
	public void show() {
		super.show();
	}
}
