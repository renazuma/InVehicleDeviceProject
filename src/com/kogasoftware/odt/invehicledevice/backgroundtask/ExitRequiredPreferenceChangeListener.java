package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.CommonLogic;
import com.kogasoftware.odt.invehicledevice.event.ExitEvent;

public class ExitRequiredPreferenceChangeListener implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = ExitRequiredPreferenceChangeListener.class
			.getSimpleName();
	private final CommonLogic commonLogic;

	public ExitRequiredPreferenceChangeListener(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("update") && sharedPreferences.getBoolean(key, false)) { // TODO
			// 文字列定数
			Log.i(TAG, "SharedPreferences changed, exit!"); // TODO
			commonLogic.getEventBus().post(new ExitEvent());
		}
	}
}
