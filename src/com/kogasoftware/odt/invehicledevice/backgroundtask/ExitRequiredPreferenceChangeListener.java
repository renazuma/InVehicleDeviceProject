package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.ExitEvent;

public class ExitRequiredPreferenceChangeListener implements
		OnSharedPreferenceChangeListener {
	public static final String EXIT_REQUIRED_SHARED_PREFERENCE_KEY = "exit_required";
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
		if (key.equals(EXIT_REQUIRED_SHARED_PREFERENCE_KEY)
				&& sharedPreferences.getBoolean(key, false)) { // TODO
			// 文字列定数
			Log.i(TAG, "SharedPreferences changed, exit!"); // TODO
			commonLogic.getEventBus().post(new ExitEvent());
		}
	}
}
