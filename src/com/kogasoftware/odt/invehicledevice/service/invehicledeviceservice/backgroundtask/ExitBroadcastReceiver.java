package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class ExitBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = ExitBroadcastReceiver.class
			.getSimpleName();
	private final InVehicleDeviceService service;

	public ExitBroadcastReceiver(InVehicleDeviceService service) {
		this.service = service;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "EXIT");
		service.exit();
	}
}
