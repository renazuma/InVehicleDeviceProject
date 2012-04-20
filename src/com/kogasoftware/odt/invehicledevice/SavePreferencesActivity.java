package com.kogasoftware.odt.invehicledevice;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.datasource.WebAPIDataSource;

public class SavePreferencesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Bundle bundle = intent.getExtras();
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("url", Objects.firstNonNull(bundle.getString("url"),
				WebAPIDataSource.DEFAULT_URL)); // TODO // 定数文字列?
		editor.putString("token",
				Strings.nullToEmpty(bundle.getString("token")));
		editor.putBoolean("update", true);
		editor.commit();
		Toast.makeText(this, "設定を保存しました", Toast.LENGTH_LONG).show();
		finish();
	}
}
