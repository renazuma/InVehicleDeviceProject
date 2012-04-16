package com.kogasoftware.odt.invehicledevice.preference;

import org.json.JSONException;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class InVehicleDevicePreferenceActivity extends PreferenceActivity
		implements WebAPICallback<InVehicleDevice> {
	private final int CONNECTING_DIALOG_ID = 100;
	private int latestReqKey = 0;
	private Button saveConfigButton = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		addPreferencesFromResource(R.xml.preference);

		saveConfigButton = (Button) findViewById(R.id.save_config_button);
		saveConfigButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isFinishing()) {
					return;
				}
				showDialog(CONNECTING_DIALOG_ID);
				Context context = InVehicleDevicePreferenceActivity.this;
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(InVehicleDevicePreferenceActivity.this);
				WebAPI api = new WebAPI();
				InVehicleDevice ivd = new InVehicleDevice();

				String url = preferences.getString("connection_url",
						"http://localhost");
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
		String message = "reqKey=" + reqKey + " token=" + token;
		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(InVehicleDevicePreferenceActivity.this);
		String url = preference.getString("connection_url", "");

		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra("url", url);
		intent.putExtra("token", token);
		String packageName = "com.kogasoftware.odt.invehicledevice";
		intent.setClassName(packageName, packageName
				+ ".SavePreferencesActivity");
		startActivity(intent);
		finish();
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
	public void onFailed(int reqKey, int statusCode, String response) {
		if (reqKey != latestReqKey) {
			return;
		}
		final String message = "onFailed: reqKey=" + reqKey + ", statusCode="
				+ statusCode + " response=" + response;
		runOnUiThread(new Runnable() {
			public void run() {
				try {
					dismissDialog(CONNECTING_DIALOG_ID);
				} catch (IllegalArgumentException e) {
				}
				Toast.makeText(InVehicleDevicePreferenceActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onException(int reqKey, WebAPIException ex) {
		if (reqKey != latestReqKey) {
			return;
		}
		final String message = "onException: reqKey=" + reqKey + ", exception="
				+ ex;

		runOnUiThread(new Runnable() {
			public void run() {
				try {
					dismissDialog(CONNECTING_DIALOG_ID);
				} catch (IllegalArgumentException e) {
				}
				Toast.makeText(InVehicleDevicePreferenceActivity.this, message,
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
