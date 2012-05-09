package com.kogasoftware.odt.invehicledevice.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.logic.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource;

public class SavePreferencesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish(); // 必ずfinishする

		Intent intent = getIntent();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			return;
		}
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(SharedPreferencesKey.SERVER_URL, Objects.firstNonNull(
				bundle.getString(SharedPreferencesKey.SERVER_URL),
				WebAPIDataSource.DEFAULT_URL));
		editor.putString(
				SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				Strings.nullToEmpty(bundle
						.getString(SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN)));
		editor.putBoolean(SharedPreferencesKey.EXIT_REQUIRED, true);
		editor.putBoolean(SharedPreferencesKey.CLEAR_REQUIRED, true);
		editor.commit();
		Toast.makeText(this, "設定を保存しました", Toast.LENGTH_LONG).show();
	}
}
