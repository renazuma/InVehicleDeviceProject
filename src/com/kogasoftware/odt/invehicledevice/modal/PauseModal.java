package com.kogasoftware.odt.invehicledevice.modal;


import android.app.Activity;

import com.kogasoftware.odt.invehicledevice.R;


public class PauseModal extends Modal {
	public PauseModal(Activity activity) {
		super(activity, R.layout.pause_modal);
		setId(R.id.pause_overlay);
	}

	@Override
	public void show() {
		super.show();
	}
}
