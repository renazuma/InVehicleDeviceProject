package com.kogasoftware.odt.invehicledevice.preference;

import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.service.startupservice.IStartupService;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;

public class InVehicleDevicePreferenceActivity extends PreferenceActivity
		implements WebAPICallback<InVehicleDevice>,
		OnSharedPreferenceChangeListener {
	private static final int CONNECTING_DIALOG_ID = 100;
	private static final String DEFAULT_URL = "http://127.0.0.1";
	private static final String LOGIN_KEY = "login";
	private static final String PASSWORD_KEY = "password";
	private static final String TAG = InVehicleDevicePreferenceActivity.class
			.getSimpleName();

	private final AtomicBoolean callbackReceived = new AtomicBoolean(false);
	private int latestReqKey = 0;

	private SharedPreferences preferences = null;
	private WebAPI api = null;

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			Log.i(TAG, "onServiceConnected");
			IStartupService startupService = IStartupService.Stub.asInterface(service);
			try {
				startupService.disable();
			} catch (RemoteException e) {
				Log.w(TAG, e);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.i(TAG, "onServiceDisconnected");
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.main);
		api = new WebAPI(DEFAULT_URL);

		addPreferencesFromResource(R.xml.preference);

		preferences = PreferenceManager
				.getDefaultSharedPreferences(InVehicleDevicePreferenceActivity.this);

		preferences.registerOnSharedPreferenceChangeListener(this);

		Button saveConfigButton = (Button) findViewById(R.id.save_config_button);
		saveConfigButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onSaveConfigButtonClick();
			}
		});

		updateSummary();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case CONNECTING_DIALOG_ID: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(Html
					.fromHtml(getString(R.string.checking_connection_config)));
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			return dialog;
		}
		}
		return null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		bindService(new Intent(IStartupService.class.getName()),
                serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unbindService(serviceConnection);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		preferences.unregisterOnSharedPreferenceChangeListener(this);
		Closeables.closeQuietly(api);
	}

	@Override
	public void onException(int reqKey, WebAPIException ex) {
		if (reqKey != latestReqKey) {
			return;
		}
		if (callbackReceived.getAndSet(true)) {
			return;
		}
		final String message = "onException: reqKey=" + reqKey + ", exception="
				+ ex;
		Log.w(TAG, message, ex);
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
		if (callbackReceived.getAndSet(true)) {
			return;
		}
		final String message = "onFailed: reqKey=" + reqKey + ", statusCode="
				+ statusCode + " response=" + response;
		Log.w(TAG, message);
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
	public void onSucceed(int reqKey, int statusCode,
			InVehicleDevice inVehicleDevice) {
		if (reqKey != latestReqKey) {
			return;
		}
		if (callbackReceived.getAndSet(true)) {
			return;
		}
		try {
			dismissDialog(CONNECTING_DIALOG_ID);
		} catch (IllegalArgumentException e) {
		}
		if (!inVehicleDevice.getAuthenticationToken().isPresent()) {
			final String message = "token not found";
			Log.e(TAG, message);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							InVehicleDevicePreferenceActivity.this,
							getResources()
									.getString(R.string.an_error_occurred),
							Toast.LENGTH_LONG).show();
					Toast.makeText(InVehicleDevicePreferenceActivity.this,
							message, Toast.LENGTH_LONG).show();
				}
			});
			finish();
			return;
		}
		String token = inVehicleDevice.getAuthenticationToken().get();
		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(InVehicleDevicePreferenceActivity.this);
		String url = preference.getString(SharedPreferencesKey.SERVER_URL, "");

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(SharedPreferencesKey.SERVER_URL, url);
		intent.putExtra(SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				token);
		intent.putExtra(SharedPreferencesKey.IN_VEHICLE_DEVICE, inVehicleDevice);
		intent.putExtra(SharedPreferencesKey.SERVICE_PROVIDER,
				inVehicleDevice.getServiceProvider());
		String packageName = "com.kogasoftware.odt.invehicledevice";
		intent.setClassName(packageName, packageName
				+ ".ui.activity.SavePreferencesActivity");
		startActivity(intent);
		finish();
	}

	private void updateSummary() {
		EditTextPreference connectionUrl = (EditTextPreference) findPreference(SharedPreferencesKey.SERVER_URL);
		connectionUrl.setSummary(preferences.getString(
				SharedPreferencesKey.SERVER_URL, ""));

		EditTextPreference login = (EditTextPreference) findPreference(LOGIN_KEY);
		login.setSummary(preferences.getString(LOGIN_KEY, ""));
	}

	private void onSaveConfigButtonClick() {
		if (isFinishing()) {
			return;
		}
		showDialog(CONNECTING_DIALOG_ID);
		api.setServerHost(preferences.getString(
				SharedPreferencesKey.SERVER_URL, DEFAULT_URL));

		InVehicleDevice ivd = new InVehicleDevice();
		ivd.setLogin(preferences.getString(LOGIN_KEY, ""));
		ivd.setPassword(preferences.getString(PASSWORD_KEY, ""));
		callbackReceived.set(false);
		try {
			latestReqKey = api.login(ivd,
					InVehicleDevicePreferenceActivity.this);
		} catch (WebAPIException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		} catch (JSONException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
		}
	}
}
