package com.kogasoftware.odt.invehicledevice.service.startupservice;

import android.content.Intent;

public class AirplaneModeOnBroadcastIntent extends Intent {
	public static final String ACTION = AirplaneModeOnBroadcastIntent.class
			.getName();

	public AirplaneModeOnBroadcastIntent() {
		super(ACTION);
	}
}
