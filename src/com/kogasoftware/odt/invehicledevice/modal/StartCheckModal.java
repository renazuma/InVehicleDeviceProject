package com.kogasoftware.odt.invehicledevice.modal;

import android.view.View;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;


public class StartCheckModal extends Modal {
	public StartCheckModal(final InVehicleDeviceActivity activity) {
		super(activity, R.layout.start_check_modal);
		setId(R.id.check_start_layout);

		Button startButton = (Button) findViewById(R.id.start_button);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				activity.enterDriveStatus();
			}
		});
	}

	@Override
	public void show() {
		super.show();
	}
}

