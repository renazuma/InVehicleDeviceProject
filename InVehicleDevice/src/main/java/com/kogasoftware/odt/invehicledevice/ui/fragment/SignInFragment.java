package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.contentprovider.task.SignInErrorBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceDownloadStateBroadcastIntent;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceDownloaderClientThread;
import com.kogasoftware.odt.invehicledevice.utils.Fragments;

public class SignInFragment extends PreferenceFragment
		implements
			LoaderCallbacks<Cursor>,
			OnSharedPreferenceChangeListener {
	private static final int LOADER_ID = 1;
	private static final String FIRST_LOAD_KEY = "first_load";
	private final List<Dialog> dialogs = Lists.newLinkedList();
	private LoaderManager loaderManager;
	private SharedPreferences preferences;
	private ExecutorService executor;
	private Boolean firstLoad;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sign_in_fragment, container, false);
	}

	private final BroadcastReceiver errorReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			showAlertDialog(SignInErrorBroadcastIntent.of(intent).getMessage());
		}
	};

	private final BroadcastReceiver voiceDownloadStateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			voiceDownloadStatePreference
					.setTitle(VoiceDownloadStateBroadcastIntent.of(intent)
							.getMessage());
		}
	};

	private Preference voiceDownloadStatePreference;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		addPreferencesFromResource(R.xml.sign_in_preference);
		if (savedInstanceState != null) {
			firstLoad = savedInstanceState.getBoolean(FIRST_LOAD_KEY, true);
		} else {
			firstLoad = true;
		}
		loaderManager = getLoaderManager();
		executor = Executors.newFixedThreadPool(1);
		preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		preferences.registerOnSharedPreferenceChangeListener(this);
		getActivity().registerReceiver(errorReceiver,
				new IntentFilter(SignInErrorBroadcastIntent.ACTION));
		loaderManager.initLoader(LOADER_ID, null, this);
		Button saveConfigButton = (Button) getView().findViewById(
				R.id.save_config_button);
		saveConfigButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onSaveConfigButtonClick();
			}
		});

		voiceDownloadStatePreference = findPreference("voice_download_state");
		voiceDownloadStatePreference.setTitle("未インストール");
		try {
			if (VoiceDownloaderClientThread.getVoiceOutputDir().isDirectory()) {
				// FIXME: Broadcastのタイミングによっては「インストール済み」にならない
				voiceDownloadStatePreference.setTitle("インストール済");
			}
		} catch (IOException e) {
		}
		getActivity().registerReceiver(voiceDownloadStateReceiver,
				new IntentFilter(VoiceDownloadStateBroadcastIntent.ACTION));
		updateSummary();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(FIRST_LOAD_KEY, firstLoad);
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
		String url = preferences.getString(InVehicleDevice.Columns.URL, "");
		if (checkAscii(url, R.string.server_url, errors)) {
			Uri uri = Uri.parse(url);
			if (Strings.isNullOrEmpty(uri.getScheme())
					|| Strings.isNullOrEmpty(uri.getHost())) {
				errors.add(String.format(Locale.US,
						getString(R.string.error_invalid_uri),
						getString(R.string.server_url)));
			}
		}
		checkAscii(preferences.getString(InVehicleDevice.Columns.LOGIN, ""),
				R.string.login, errors);
		checkAscii(
				preferences.getString(InVehicleDevice.Columns.PASSWORD, ""),
				R.string.password, errors);
		return errors;
	}

	private void onSaveConfigButtonClick() {
		List<String> errors = check();
		if (!errors.isEmpty()) {
			String li = errors.size() == 1 ? "" : "- ";
			showAlertDialog(li + Joiner.on("\n" + li).join(errors));
			return;
		}
		showProgressDialog();

		final ContentResolver contentResolver = getActivity()
				.getContentResolver();
		executor.submit(new Runnable() {
			@Override
			public void run() {
				ContentValues values = new ContentValues();
				for (String key : new String[]{InVehicleDevice.Columns._ID,
						InVehicleDevice.Columns.URL,
						InVehicleDevice.Columns.LOGIN,
						InVehicleDevice.Columns.PASSWORD}) {
					values.put(key, preferences.getString(key, null));
				}
				contentResolver.insert(InVehicleDevice.CONTENT.URI, values);
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		loaderManager.destroyLoader(LOADER_ID);
		executor.shutdownNow();
		getActivity().unregisterReceiver(errorReceiver);
		getActivity().unregisterReceiver(voiceDownloadStateReceiver);
		dismissAllDialogs();
	}

	public static SignInFragment newInstance() {
		SignInFragment fragment = new SignInFragment();
		return fragment;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), InVehicleDevice.CONTENT.URI,
				null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (!cursor.moveToFirst()) {
			firstLoad = false;
			return;
		}
		if (cursor.getString(cursor
				.getColumnIndexOrThrow("authentication_token")) != null
				&& !firstLoad) {
			Fragments.hide(this);
		}
		firstLoad = false;
		Editor editor = preferences.edit();
		for (String key : new String[]{InVehicleDevice.Columns._ID,
				InVehicleDevice.Columns.URL, InVehicleDevice.Columns.LOGIN,
				InVehicleDevice.Columns.PASSWORD}) {
			String value = cursor.getString(cursor.getColumnIndexOrThrow(key));
			editor.putString(key, value);
		}
		editor.apply();
	}

	private void showAlertDialog(String message) {
		if (!isAdded()) {
			return;
		}
		dismissAllDialogs();
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.an_error_occurred))
				.setPositiveButton(android.R.string.ok, null)
				.setMessage(message).show();
		dialogs.add(alertDialog);
	}

	private void showProgressDialog() {
		if (!isAdded()) {
			return;
		}
		dismissAllDialogs();
		ProgressDialog progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(Html
				.fromHtml(getString(R.string.checking_connection_config)));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
		dialogs.add(progressDialog);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private void updateSummary() {
		EditTextPreference url = (EditTextPreference) findPreference("url");
		url.setSummary(url.getText());
		EditTextPreference login = (EditTextPreference) findPreference("login");
		login.setSummary(login.getText());
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		updateSummary();
	}

	private void dismissAllDialogs() {
		for (Dialog dialog : dialogs) {
			dialog.dismiss();
		}
		dialogs.clear();
	}
}
