package com.kogasoftware.odt.invehicledevice.preference;

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
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.DefaultInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClientFactory;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.Broadcasts;
import com.kogasoftware.odt.invehicledevice.service.startupservice.IStartupService;

public class InVehicleDevicePreferenceActivity extends PreferenceActivity
		implements ApiClientCallback<InVehicleDevice>,
		OnSharedPreferenceChangeListener {
	private static final int CONNECTING_DIALOG_ID = 100;
	private static final String DEFAULT_URL = "http://127.0.0.1";
	private static final String LOGIN_KEY = "login";
	private static final String PASSWORD_KEY = "password";
	private static final String TAG = InVehicleDevicePreferenceActivity.class
			.getSimpleName();
	
	private final InVehicleDeviceApiClient apiClient = new DefaultInVehicleDeviceApiClient(DEFAULT_URL);
	private int latestReqKey = 0;

	private SharedPreferences preferences = null;

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			Log.i(TAG, "onServiceConnected");
			IStartupService startupService = IStartupService.Stub
					.asInterface(service);
			try {
				startupService.disable();
			} catch (RemoteException e) {
				Log.w(TAG, e);
			}
			Intent exitIntent = new Intent();
			exitIntent.setAction(Broadcasts.ACTION_EXIT);
			getApplicationContext().sendBroadcast(exitIntent);
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
	public void onStart() {
		super.onStart();
		bindService(new Intent(IStartupService.class.getName()),
				serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		unbindService(serviceConnection);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		preferences.unregisterOnSharedPreferenceChangeListener(this);
		Closeables.closeQuietly(apiClient);
	}

	@Override
	public void onException(int reqKey, ApiClientException ex) {
		if (reqKey != latestReqKey) {
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
		String url = preference.getString(SharedPreferencesKeys.SERVER_URL, "");

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(SharedPreferencesKeys.SERVER_URL, url);
		intent.putExtra(SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				token);
		intent.putExtra(SharedPreferencesKeys.IN_VEHICLE_DEVICE, inVehicleDevice);
		intent.putExtra(SharedPreferencesKeys.SERVICE_PROVIDER,
				inVehicleDevice.getServiceProvider());
		intent.putExtra(
				SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE,
				Integer.parseInt(preference
						.getString(
								SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE,
								"1")));
		intent.putExtra(
				SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME,
				Integer.parseInt(preference.getString(
						SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME, "1000")));
		intent.putExtra(SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT,
				Integer.parseInt(preference.getString(
						SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT,
						"90000")));
		String packageName = "com.kogasoftware.odt.invehicledevice";
		intent.setClassName(packageName, packageName
				+ ".ui.activity.SavePreferencesActivity");
		startActivity(intent);
		finish();
	}

	private void updateSummary() {
		for (String key : new String[] { LOGIN_KEY,
				SharedPreferencesKeys.SERVER_URL,
				SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE,
				SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME,
				SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT }) {
			EditTextPreference preference = (EditTextPreference) findPreference(key);
			preference.setSummary(preference.getText());
		}
	}

	private void onSaveConfigButtonClick() {
		if (isFinishing()) {
			return;
		}
		showDialog(CONNECTING_DIALOG_ID);
		apiClient.setServerHost(preferences.getString(
				SharedPreferencesKeys.SERVER_URL, DEFAULT_URL));

		InVehicleDevice ivd = new InVehicleDevice();
		ivd.setLogin(preferences.getString(LOGIN_KEY, ""));
		ivd.setPassword(preferences.getString(PASSWORD_KEY, ""));
		latestReqKey = apiClient.withRetry(false).login(ivd, this);
	}
}
