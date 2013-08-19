package com.kogasoftware.odt.invehicledevice.preference;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.DefaultInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.broadcast.Broadcasts;
import com.kogasoftware.odt.invehicledevice.service.startupservice.IStartupService;
import com.kogasoftware.odt.invehicledevice.service.startupservice.Intents;

public class InVehicleDevicePreferenceActivity extends PreferenceActivity
		implements OnSharedPreferenceChangeListener {
	private static final String DEFAULT_URL = "http://127.0.0.1";
	private static final String LOGIN_KEY = "login";
	private static final String PASSWORD_KEY = "password";
	private static final String TAG = InVehicleDevicePreferenceActivity.class
			.getSimpleName();

	private final InVehicleDeviceApiClient apiClient = new DefaultInVehicleDeviceApiClient(
			DEFAULT_URL);
	private final List<Dialog> dialogs = Lists.newLinkedList();

	private SharedPreferences preferences = null;
	private IStartupService startupService = null;
	private Boolean destroyed = false;

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			Log.i(TAG, "onServiceConnected");
			startupService = IStartupService.Stub.asInterface(service);
			disableMainApplication();
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			Log.i(TAG, "onServiceDisconnected");
		}
	};

	private void disableMainApplication() {
		if (startupService != null) {
			try {
				startupService.disable();
			} catch (RemoteException e) {
				Log.w(TAG, e);
			}
		}
		Intent exitIntent = new Intent();
		exitIntent.setAction(Broadcasts.ACTION_EXIT);
		getApplicationContext().sendBroadcast(exitIntent);
	}

	private void dismissAllDialogs() {
		for (Dialog dialog : dialogs) {
			dialog.dismiss();
		}
		dialogs.clear();
	}

	private void showProgressDialog() {
		ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(Html
				.fromHtml(getString(R.string.checking_connection_config)));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
		dialogs.add(progressDialog);
	}

	private void showAlertDialog(String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(getString(R.string.an_error_occurred))
				.setPositiveButton(android.R.string.ok, null)
				.setMessage(message).show();
		dialogs.add(alertDialog);
	}

	@Override
	public void onResume() {
		super.onResume();
		disableMainApplication();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		destroyed = false;

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
	public void onStart() {
		super.onStart();
		Intent intent = new Intent(IStartupService.class.getName());
		intent.putExtra(Intents.EXTRA_BOOLEAN_ENABLED, false);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		unbindService(serviceConnection);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		destroyed = true;
		dismissAllDialogs();

		preferences.unregisterOnSharedPreferenceChangeListener(this);
		Closeables.closeQuietly(apiClient);
	}

	private void onExceptionOnUiThread(int reqKey, ApiClientException ex) {
		String message = "onException: reqKey=" + reqKey + ", exception=" + ex;
		Log.w(TAG, message, ex);
		dismissAllDialogs();
		showAlertDialog(message);
	}

	private void onFailedOnUiThread(int reqKey, int statusCode, String response) {
		String message = "onFailed: reqKey=" + reqKey + ", statusCode="
				+ statusCode + " response=" + response;
		Log.w(TAG, message);
		dismissAllDialogs();
		showAlertDialog(message);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updateSummary();
	}

	private void onSucceedOnUiThread(int reqKey, int statusCode,
			InVehicleDevice inVehicleDevice) {
		dismissAllDialogs();
		if (!inVehicleDevice.getAuthenticationToken().isPresent()) {
			String message = "token not found";
			Log.e(TAG, message);
			showAlertDialog(message);
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
		intent.putExtra(SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE,
				Integer.parseInt(preference.getString(
						SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE,
						"1")));
		intent.putExtra(
				SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME,
				Integer.parseInt(preference
						.getString(
								SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME,
								"1000")));
		intent.putExtra(SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT,
				Integer.parseInt(preference.getString(
						SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT,
						"90000")));
		String packageName = "com.kogasoftware.odt.invehicledevice";
		intent.setClassName(packageName, packageName
				+ ".ui.activity.SavePreferencesActivity");
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			showAlertDialog("車載器アプリケーションがインストールされていません");
			return;
		}
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
		showProgressDialog();
		apiClient.setServerHost(preferences.getString(
				SharedPreferencesKeys.SERVER_URL, DEFAULT_URL));

		InVehicleDevice ivd = new InVehicleDevice();
		ivd.setLogin(preferences.getString(LOGIN_KEY, ""));
		ivd.setPassword(preferences.getString(PASSWORD_KEY, ""));
		apiClient.withRetry(false).login(ivd,
				new ApiClientCallback<InVehicleDevice>() {
					@Override
					public void onException(final int reqKey,
							final ApiClientException exception) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (destroyed) {
									return;
								}
								onExceptionOnUiThread(reqKey, exception);
							}
						});
					}

					@Override
					public void onFailed(final int reqKey,
							final int statusCode, final String message) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (destroyed) {
									return;
								}
								onFailedOnUiThread(reqKey, statusCode, message);
							}
						});
					}

					@Override
					public void onSucceed(final int reqKey,
							final int statusCode,
							final InVehicleDevice inVehicleDevice) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if (destroyed) {
									return;
								}
								onSucceedOnUiThread(reqKey, statusCode,
										inVehicleDevice);
							}
						});
					}
				});
	}
}
