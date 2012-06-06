package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.ExitEvent;

public class ExitBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = ExitBroadcastReceiver.class
			.getSimpleName();
	private final CommonLogic commonLogic;

	public ExitBroadcastReceiver(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "EXIT");
		commonLogic.postEvent(new ExitEvent());
	}
}
