package com.kogasoftware.odt.invehicledevice.map;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RunApplicationBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent runApplicationIntent = new Intent(context, MainActivity.class);
		runApplicationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(runApplicationIntent);
	}
}
