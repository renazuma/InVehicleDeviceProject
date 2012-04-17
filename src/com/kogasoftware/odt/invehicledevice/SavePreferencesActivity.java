package com.kogasoftware.odt.invehicledevice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.common.base.Strings;

public class SavePreferencesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Bundle bundle = intent.getExtras();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("url", Strings.nullToEmpty(bundle.getString("url"))); // TODO
																				// 定数文字列?
		editor.putString("token",
				Strings.nullToEmpty(bundle.getString("token")));
		editor.putBoolean("update", true);
		editor.commit();
		Toast.makeText(this, "設定を保存しました", Toast.LENGTH_LONG).show();
		finish();
	}
}
