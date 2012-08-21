package com.kogasoftware.odt.invehicledevice.ui.activity;

import java.io.InvalidClassException;
import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.datasource.WebAPIDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.Broadcasts;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;

public class SavePreferencesActivity extends Activity {
	private static final String TAG = SavePreferencesActivity.class
			.getSimpleName();

	private Pair<Boolean, String> saveInBackground() {
		Intent intent = getIntent();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Bundle bundle = Objects.firstNonNull(intent.getExtras(), new Bundle());
		InVehicleDevice inVehicleDevice = new InVehicleDevice();

		Serializable maybeInVehicleDevice = null;
		try {
			maybeInVehicleDevice = bundle
					.getSerializable(SharedPreferencesKeys.IN_VEHICLE_DEVICE);
		} catch (RuntimeException e) {
			Log.e(TAG, e.toString(), e);
			String message = "";
			if (e.getCause() instanceof InvalidClassException) {
				message = "エラーが発生しました。設定アプリケーションのバージョンと車載器アプリケーションのバージョンが適合しません。";
			} else {
				message = "不明なエラーが発生しました。デバイスのログを参照してください。";
			}
			return Pair.of(false, message);
		}

		if (maybeInVehicleDevice instanceof InVehicleDevice) {
			inVehicleDevice = (InVehicleDevice) maybeInVehicleDevice;
		} else {
			String message = "!(bundle.getSerializable(SharedPreferencesKey.IN_VEHICLE_DEVICE) instanceof InVehicleDevice)";
			Log.e(TAG, message);
		}

		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.putBoolean(SharedPreferencesKeys.INITIALIZED, true);
		editor.putString(SharedPreferencesKeys.SERVER_URL, Objects
				.firstNonNull(
						bundle.getString(SharedPreferencesKeys.SERVER_URL),
						WebAPIDataSource.DEFAULT_URL));
		editor.putString(
				SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				Strings.nullToEmpty(bundle
						.getString(SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN)));
		try {
			editor.putString(SharedPreferencesKeys.IN_VEHICLE_DEVICE,
					inVehicleDevice.toJSONObject().toString());
		} catch (JSONException e) {
			Log.e(TAG, "toJSONObject() failed", e);
		}

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish(); // 必ずfinishする
		AsyncTask<Void, Void, Pair<Boolean, String>> asyncTask = new AsyncTask<Void, Void, Pair<Boolean, String>>() {
			@Override
			protected void onPostExecute(Pair<Boolean, String> result) {
				if (isCancelled() || result == null) {
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
