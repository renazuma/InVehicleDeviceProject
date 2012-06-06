package com.kogasoftware.odt.invehicledevice.ui.activity;

import java.io.InvalidClassException;
import java.io.Serializable;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTask;
import com.kogasoftware.odt.invehicledevice.logic.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.ServiceProvider;

public class SavePreferencesActivity extends Activity {
	private static final String TAG = SavePreferencesActivity.class
			.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish(); // 必ずfinishする

		Intent intent = getIntent();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Bundle bundle = Objects.firstNonNull(intent.getExtras(), new Bundle());
		InVehicleDevice inVehicleDevice = new InVehicleDevice();

		Serializable maybeInVehicleDevice = null;
		try {
			maybeInVehicleDevice = bundle
				.getSerializable(SharedPreferencesKey.IN_VEHICLE_DEVICE);
		} catch (RuntimeException e) {
			Log.e(TAG, e.toString(), e);
			if (e.getCause() instanceof InvalidClassException) {
				Toast.makeText(this, "エラーが発生しました。設定アプリケーションのバージョンと車載器アプリケーションのバージョンが適合しません。", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "不明なエラーが発生しました。デバイスのログを参照してください。", Toast.LENGTH_LONG).show();
			}
			return;
		}
		if (maybeInVehicleDevice instanceof InVehicleDevice) {
			inVehicleDevice = (InVehicleDevice) maybeInVehicleDevice;
		} else {
			Log.e(TAG,
					"!(bundle.getSerializable(SharedPreferencesKey.IN_VEHICLE_DEVICE) instanceof InVehicleDevice)");
		}

		ServiceProvider serviceProvider = new ServiceProvider();
		if (inVehicleDevice.getServiceProvider().isPresent()) {
			serviceProvider = inVehicleDevice.getServiceProvider().get();
		} else {
			Log.e(TAG, "!(inVehicleDevice.getServiceProvider().isPresent())");
		}

		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		editor.putString(SharedPreferencesKey.SERVER_URL, Objects.firstNonNull(
				bundle.getString(SharedPreferencesKey.SERVER_URL),
				WebAPIDataSource.DEFAULT_URL));
		editor.putString(
				SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				Strings.nullToEmpty(bundle
						.getString(SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN)));
		try {
			editor.putString(SharedPreferencesKey.SERVICE_PROVIDER,
					serviceProvider.toJSONObject().toString());
		} catch (JSONException e) {
			Log.e(TAG, "toJSONObject() failed", e);
		}
		try {
			editor.putString(SharedPreferencesKey.IN_VEHICLE_DEVICE,
					inVehicleDevice.toJSONObject().toString());
		} catch (JSONException e) {
			Log.e(TAG, "toJSONObject() failed", e);
		}
		editor.putBoolean(SharedPreferencesKey.CLEAR_STATUS_BACKUP, true);
		editor.putBoolean(SharedPreferencesKey.CLEAR_WEBAPI_BACKUP, true);
		editor.putBoolean(SharedPreferencesKey.CLEAR_VOICE_CACHE, true);
		editor.commit();
		Toast.makeText(this, "設定を保存しました", Toast.LENGTH_LONG).show();
		
		Intent exitIntent = new Intent();
		exitIntent.setAction(BackgroundTask.ACTION_EXIT);
		getApplicationContext().sendBroadcast(exitIntent);
	}
}
