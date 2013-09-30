package com.kogasoftware.odt.invehicledevice.ui.activity;

import org.apache.commons.lang3.tuple.Pair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.broadcast.Broadcasts;

public class SavePreferencesActivity extends Activity {
	@SuppressWarnings("unused")
	private static final String TAG = SavePreferencesActivity.class
			.getSimpleName();

	private Pair<Boolean, String> saveInBackground() {
		Intent intent = getIntent();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Bundle bundle = Objects.firstNonNull(intent.getExtras(), new Bundle());

		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.putBoolean(SharedPreferencesKeys.INITIALIZED, true);
		editor.putString(SharedPreferencesKeys.SERVER_URL, Objects
				.firstNonNull(
						bundle.getString(SharedPreferencesKeys.SERVER_URL),
						InVehicleDeviceService.DEFAULT_URL));
		editor.putString(
				SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				Strings.nullToEmpty(bundle
						.getString(SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN)));
		editor.putBoolean(SharedPreferencesKeys.ROTATE_MAP,
				bundle.getBoolean(SharedPreferencesKeys.ROTATE_MAP, true));
		editor.putInt(SharedPreferencesKeys.EXTRA_ROTATION_DEGREES_CLOCKWISE,
				bundle.getInt(
						SharedPreferencesKeys.EXTRA_ROTATION_DEGREES_CLOCKWISE,
						0));
		editor.putInt(
				SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE,
				bundle.getInt(SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE));
		editor.putInt(SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME,
				bundle.getInt(SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME));
		editor.putInt(
				SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT,
				bundle.getInt(SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT));
		editor.putBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP, true);
		editor.putBoolean(SharedPreferencesKeys.CLEAR_WEBAPI_BACKUP, true);
		editor.putBoolean(SharedPreferencesKeys.CLEAR_VOICE_CACHE, true);
		editor.commit();
		return Pair.of(true, "");
	}

	@Override
	protected void onStart() {
		super.onStart();
		AsyncTask<Void, Void, Pair<Boolean, String>> asyncTask = new AsyncTask<Void, Void, Pair<Boolean, String>>() {
			@Override
			protected void onCancelled() {
				if (!isFinishing()) {
					finish();
				}
			}

			@Override
			protected void onPostExecute(Pair<Boolean, String> result) {
				if (!isFinishing()) {
					finish();
				}
				if (result == null) {
					return;
				} else if (!result.getKey()) {
					Toast.makeText(SavePreferencesActivity.this,
							result.getValue(), Toast.LENGTH_LONG).show();
					return;
				}
				Toast.makeText(SavePreferencesActivity.this, "設定を保存しました",
						Toast.LENGTH_LONG).show();
				Intent exitIntent = new Intent();
				exitIntent.setAction(Broadcasts.ACTION_EXIT);
				getApplicationContext().sendBroadcast(exitIntent);
			}

			@Override
			protected Pair<Boolean, String> doInBackground(Void... params) {
				return saveInBackground();
			}
		};
		asyncTask.execute();
	}
}
