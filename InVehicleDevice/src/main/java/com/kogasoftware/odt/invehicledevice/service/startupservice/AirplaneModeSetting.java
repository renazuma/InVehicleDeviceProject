package com.kogasoftware.odt.invehicledevice.service.startupservice;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class AirplaneModeSetting {
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static void set(Context context, Boolean enable) throws IOException {
		if (get(context).equals(enable)) {
			return;
		}
		int value = enable ? 1 : 0;
		Boolean success = false;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			success = Settings.System.putInt(context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, value);
		} else {
			try {
				success = Settings.Global.putInt(context.getContentResolver(),
						Settings.Global.AIRPLANE_MODE_ON, value);
			} catch (SecurityException e) {
				throw new IOException(e);
			}
		}
		if (!success) {
			throw new IOException("Airplane mode can't be changed");
		}
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", enable.booleanValue());
		context.sendBroadcast(intent);
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static Boolean get(Context context) {
		Integer value = 0;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			value = Settings.System.getInt(context.getContentResolver(),
					Settings.System.AIRPLANE_MODE_ON, 0);
		} else {
			value = Settings.Global.getInt(context.getContentResolver(),
					Settings.Global.AIRPLANE_MODE_ON, 0);
		}
		return !value.equals(0);
	}
}
