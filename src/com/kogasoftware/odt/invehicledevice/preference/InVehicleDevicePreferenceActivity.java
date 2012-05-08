package com.kogasoftware.odt.invehicledevice.preference;

import org.json.JSONException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.logic.SharedPreferencesKey;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;

public class InVehicleDevicePreferenceActivity extends PreferenceActivity
		implements WebAPICallback<InVehicleDevice>,
		OnSharedPreferenceChangeListener {
	private static final int CONNECTING_DIALOG_ID = 100;
	private int latestReqKey = 0;
	private SharedPreferences preferences = null;
	private Button saveConfigButton = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		addPreferencesFromResource(R.xml.preference);

		preferences = PreferenceManager
				.getDefaultSharedPreferences(InVehicleDevicePreferenceActivity.this);

		preferences.registerOnSharedPreferenceChangeListener(this);

		saveConfigButton = (Button) findViewById(R.id.save_config_button);
		saveConfigButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isFinishing()) {
					return;
				}
				showDialog(CONNECTING_DIALOG_ID);
				Context context = InVehicleDevicePreferenceActivity.this;
				String url = preferences.getString("connection_url",
						"http://localhost");
				WebAPI api = new WebAPI(url);
				InVehicleDevice ivd = new InVehicleDevice();
				ivd.setLogin(preferences.getString("login", "ivd1"));
				ivd.setPassword(preferences.getString("password", "ivdpass"));

				try {
					latestReqKey = api.login(ivd,
							InVehicleDevicePreferenceActivity.this);
				} catch (WebAPIException e) {
					Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
							.show();
				} catch (JSONException e) {
					Toast.makeText(context, e.toString(), Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		updateSummary();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case CONNECTING_DIALOG_ID: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("接続確認をしています");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			return dialog;
		}
		}
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		preferences.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onException(int reqKey, WebAPIException ex) {
		if (reqKey != latestReqKey) {
			return;
		}
		final String message = "onException: reqKey=" + reqKey + ", exception="
				+ ex;

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					dismissDialog(CONNECTING_DIALOG_ID);
				} catch (IllegalArgumentException e) {
				}
				Toast.makeText(InVehicleDevicePreferenceActivity.this,
						getResources().getString(R.string.an_error_occurred),
						Toast.LENGTH_LONG).show();
				Toast.makeText(InVehicleDevicePreferenceActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onFailed(int reqKey, int statusCode, String response) {
		if (reqKey != latestReqKey) {
			return;
		}
		final String message = "onFailed: reqKey=" + reqKey + ", statusCode="
				+ statusCode + " response=" + response;
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					dismissDialog(CONNECTING_DIALOG_ID);
				} catch (IllegalArgumentException e) {
				}
				Toast.makeText(InVehicleDevicePreferenceActivity.this,
						getResources().getString(R.string.an_error_occurred),
						Toast.LENGTH_LONG).show();
				Toast.makeText(InVehicleDevicePreferenceActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updateSummary();
	}

	@Override
	public void onSucceed(int reqKey, int statusCode, InVehicleDevice result) {
		if (reqKey != latestReqKey) {
			return;
		}
		try {
			dismissDialog(CONNECTING_DIALOG_ID);
		} catch (IllegalArgumentException e) {
		}
		if (!result.getAuthenticationToken().isPresent()) {
			Toast.makeText(this, "token not found", Toast.LENGTH_LONG).show();
			finish();
		}
		String token = result.getAuthenticationToken().get();
		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(InVehicleDevicePreferenceActivity.this);
		String url = preference.getString("connection_url", "");

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(SharedPreferencesKey.SERVER_URL, url);
		intent.putExtra(SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				token);
		String packageName = "com.kogasoftware.odt.invehicledevice";
		intent.setClassName(packageName, packageName
				+ ".ui.activity.SavePreferencesActivity");
		startActivity(intent);
		finish();
	}

	private void updateSummary() {
		EditTextPreference connectionUrl = (EditTextPreference) findPreference("connection_url");
		connectionUrl.setSummary(preferences.getString("connection_url", ""));

		EditTextPreference login = (EditTextPreference) findPreference("login");
		login.setSummary(preferences.getString("login", ""));
	}
}
