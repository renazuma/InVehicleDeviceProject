package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.logic.event.ExitEvent;

public class ExitRequiredPreferenceChangeListener implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = ExitRequiredPreferenceChangeListener.class
			.getSimpleName();
	private final CommonLogic commonLogic;

	public ExitRequiredPreferenceChangeListener(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	/**
	 * EXIT_REQUIRED_KEYがtrueの場合、ExitEventを発生させる
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(SharedPreferencesKey.EXIT_REQUIRED)
				&& sharedPreferences.getBoolean(key, false)) {
			// 文字列定数
			Log.i(TAG, "SharedPreferences changed, exit!");
			commonLogic.postEvent(new ExitEvent());
			sharedPreferences.edit()
					.putBoolean(SharedPreferencesKey.EXIT_REQUIRED, true)
					.commit();
		}
	}
}
