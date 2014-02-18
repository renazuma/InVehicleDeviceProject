package com.kogasoftware.odt.invehicledevice.preference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.DefaultInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.broadcast.Broadcasts;
import com.kogasoftware.odt.invehicledevice.service.startupservice.IStartupService;
import com.kogasoftware.odt.invehicledevice.service.startupservice.Intents;
import com.kogasoftware.odt.invehicledevice.service.voicedownloaderservice.VoiceDownloaderService;

public class InVehicleDevicePreferenceActivity extends PreferenceActivity
		implements OnSharedPreferenceChangeListener, IDownloaderClient {
	private static final String DEFAULT_URL = "http://127.0.0.1";
	private static final String LOGIN_KEY = "login";
	private static final String PASSWORD_KEY = "password";
	private static final String TAG = InVehicleDevicePreferenceActivity.class
			.getSimpleName();

	private final InVehicleDeviceApiClient apiClient = new DefaultInVehicleDeviceApiClient(
			DEFAULT_URL);
	private final List<Dialog> dialogs = Lists.newLinkedList();
	private final ServiceConnection serviceConnection = new ServiceConnection() {
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

	private SharedPreferences preferences = null;
	private IStartupService startupService = null;
	private Boolean destroyed = false;
	private IDownloaderService downloaderService = null;
	private IStub downloaderClientStub = null;

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

	void startVoiceDownloaderServiceIfRequired() {
		downloaderClientStub = DownloaderClientMarshaller.CreateStub(this,
				VoiceDownloaderService.class);
		downloaderClientStub.connect(this);

		// Build an Intent to start this activity from the Notification
		Intent notifierIntent = new Intent(this, getClass());
		notifierIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notifierIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Start the download service (if required)
		try {
			DownloaderClientMarshaller.startDownloadServiceIfRequired(this,
					pendingIntent, VoiceDownloaderService.class);
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e); // Fatal exception
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startVoiceDownloaderServiceIfRequired();
		destroyed = false;

		setContentView(R.layout.main);

		addPreferencesFromResource(R.xml.preference);

		// see
		// "http://stackoverflow.com/questions/3907830/android-checkboxpreference-default-value"
		PreferenceManager.setDefaultValues(this, R.xml.preference, false);

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
		downloaderClientStub.disconnect(this);
		dismissAllDialogs();

		preferences.unregisterOnSharedPreferenceChangeListener(this);
		try {
			apiClient.close();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}

	private void onExceptionOnUiThread(int reqKey, ApiClientException ex) {
		String message = getString(R.string.error_connection) + "\n"
				+ ex.getMessage();
		Log.w(TAG, message, ex);
		dismissAllDialogs();
		showAlertDialog(message);
	}

	private void onFailedOnUiThread(int reqKey, int statusCode, String response) {
		StringBuilder message = new StringBuilder(
				getString(R.string.error_invalid_login_or_password));
		if (statusCode != 401) {
			message.append("\n---\n" + statusCode + ":" + response);
		}
		Log.w(TAG, message.toString());
		dismissAllDialogs();
		showAlertDialog(message.toString());
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
		String url = preferences
				.getString(SharedPreferencesKeys.SERVER_URL, "");

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(SharedPreferencesKeys.SERVER_URL, url);
		intent.putExtra(SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN,
				token);
		intent.putExtra(SharedPreferencesKeys.ROTATE_MAP,
				preferences.getBoolean(SharedPreferencesKeys.ROTATE_MAP, true));
		intent.putExtra(SharedPreferencesKeys.EXTRA_ROTATION_DEGREES_CLOCKWISE,
				preferences.getInt(
						SharedPreferencesKeys.EXTRA_ROTATION_DEGREES_CLOCKWISE,
						0));
		intent.putExtra(SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE,
				Integer.parseInt(preferences.getString(
						SharedPreferencesKeys.LOCATION_RECEIVE_MIN_DISTANCE,
						"1")));
		intent.putExtra(
				SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME,
				Integer.parseInt(preferences
						.getString(
								SharedPreferencesKeys.LOCATION_RECEIVE_MIN_TIME,
								"1000")));
		intent.putExtra(SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT,
				Integer.parseInt(preferences.getString(
						SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT,
						"90000")));
		String packageName = "com.kogasoftware.odt.invehicledevice";
		intent.setClassName(packageName, packageName
				+ ".ui.activity.SavePreferencesActivity");
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			showAlertDialog(getString(R.string.error_invehicledevice_application_not_found));
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
		List<String> errors = check();
		if (!errors.isEmpty()) {
			String li = errors.size() == 1 ? "" : "- ";
			showAlertDialog(li + Joiner.on("\n" + li).join(errors));
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

	private Boolean checkAscii(String value, Integer id, List<String> errors) {
		if (Strings.isNullOrEmpty(value)) {
			errors.add(String.format(Locale.US,
					getString(R.string.error_null_or_empty), getString(id)));
			return false;
		} else if (!CharMatcher.ASCII.matchesAllOf(value)) {
			errors.add(String.format(Locale.US,
					getString(R.string.error_non_ascii), getString(id)));
			return false;
		} else {
			return true;
		}
	}

	private List<String> check() {
		List<String> errors = Lists.newLinkedList();
		String url = preferences
				.getString(SharedPreferencesKeys.SERVER_URL, "");
		if (checkAscii(url, R.string.server_url, errors)) {
			Uri uri = Uri.parse(url);
			if (Strings.isNullOrEmpty(uri.getScheme())
					|| Strings.isNullOrEmpty(uri.getHost())) {
				errors.add(String.format(Locale.US,
						getString(R.string.error_invalid_uri),
						getString(R.string.server_url)));
			}
		}

		checkAscii(preferences.getString(LOGIN_KEY, ""), R.string.login, errors);
		checkAscii(preferences.getString(PASSWORD_KEY, ""), R.string.password,
				errors);
		return errors;
	}

	@Override
	public void onServiceConnected(Messenger m) {
		downloaderService = DownloaderServiceMarshaller.CreateProxy(m);
		downloaderService.onClientUpdated(downloaderClientStub.getMessenger());
		downloaderService
				.setDownloadFlags(IDownloaderService.FLAGS_DOWNLOAD_OVER_CELLULAR);
		downloaderService.requestContinueDownload();
	}

	@Override
	public void onDownloadStateChanged(int newState) {
		Log.e(TAG, "state: " + newState);
		switch (newState) {
		case IDownloaderClient.STATE_IDLE:
			break;
		case IDownloaderClient.STATE_CONNECTING:
			break;
		case IDownloaderClient.STATE_FETCHING_URL:
			break;
		case IDownloaderClient.STATE_DOWNLOADING:
			break;
		case IDownloaderClient.STATE_FAILED_CANCELED:
			break;
		case IDownloaderClient.STATE_FAILED:
			break;
		case IDownloaderClient.STATE_FAILED_FETCHING_URL:
			break;
		case IDownloaderClient.STATE_FAILED_UNLICENSED:
			break;
		case IDownloaderClient.STATE_PAUSED_NEED_CELLULAR_PERMISSION:
			break;
		case IDownloaderClient.STATE_PAUSED_WIFI_DISABLED_NEED_CELLULAR_PERMISSION:
			break;
		case IDownloaderClient.STATE_PAUSED_BY_REQUEST:
			break;
		case IDownloaderClient.STATE_PAUSED_ROAMING:
			break;
		case IDownloaderClient.STATE_PAUSED_SDCARD_UNAVAILABLE:
			break;
		case IDownloaderClient.STATE_COMPLETED:
			break;
		default:
		}
	}

	@Override
	public void onDownloadProgress(DownloadProgressInfo progress) {
	}

	boolean isVoiceFileDownloaded() {
		String fileName = Helpers.getExpansionAPKFileName(this, true, 100);
		if (!Helpers.doesFileExist(this, fileName, 12345, false))
			return false;
		return true;
	}
}
