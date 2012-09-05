package com.kogasoftware.odt.invehicledevice.service.logservice;

import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class UpdateCredentialsBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = UpdateCredentialsBroadcastReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent == null) {
			Log.w(TAG, "onReceive intent == null");
			return;
		}
		final Bundle extras = intent.getExtras();
		if (extras == null) {
			Log.w(TAG, "onReceive intent.getExtras() == null");
			return;
		}
		Thread saveThread = new Thread() {
			@Override
			public void run() {
				SharedPreferences.Editor editor = context
						.getSharedPreferences(UploadThread.SHARED_PREFERENCES_NAME,
								Context.MODE_PRIVATE).edit();
				editor.putString(
						SharedPreferencesKeys.AWS_ACCESS_KEY_ID,
						Strings.nullToEmpty(extras
								.getString(SharedPreferencesKeys.AWS_ACCESS_KEY_ID)));
				editor.putString(
						SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY,
						Strings.nullToEmpty(extras
								.getString(SharedPreferencesKeys.AWS_SECRET_ACCESS_KEY)));
				editor.apply();
			}
		};
		saveThread.start();
	}
}
