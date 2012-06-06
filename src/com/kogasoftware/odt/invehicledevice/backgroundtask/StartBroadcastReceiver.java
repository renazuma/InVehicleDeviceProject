package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class StartBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
        Intent startIntent = new Intent(context, InVehicleDeviceActivity.class);
        startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
	}
}
