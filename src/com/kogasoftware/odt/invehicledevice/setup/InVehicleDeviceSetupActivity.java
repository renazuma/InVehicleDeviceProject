package com.kogasoftware.odt.invehicledevice.setup;

import org.json.JSONException;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class InVehicleDeviceSetupActivity extends Activity implements
		WebAPICallback<InVehicleDevice> {
	private int latestReqKey = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WebAPI api = new WebAPI();
		InVehicleDevice ivd = new InVehicleDevice();
		ivd.setLogin("ivd1");
		ivd.setPassword("ivdpass");
		try {
			latestReqKey = api.login(ivd, this);
		} catch (WebAPIException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			finish();
		} catch (JSONException e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void onSucceed(int reqKey, int statusCode, InVehicleDevice result) {
		if (reqKey != latestReqKey) {
			return;
		}
		if (!result.getAuthenticationToken().isPresent()) {
			Toast.makeText(this, "token not found", Toast.LENGTH_LONG).show();
			finish();
		}
		String token = result.getAuthenticationToken().get();
		String message = "reqKey=" + reqKey + " token="
				+ token;
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();

		Intent intent = new Intent();
		intent.putExtra("url", "http://localhost");
		intent.putExtra("token", token);
		String packageName = "com.kogasoftware.odt.invehicledevice";
		intent.setClassName(packageName, packageName + ".InVehicleDeviceActivity");
		startActivity(intent);
	}

	@Override
	public void onFailed(int reqKey, int statusCode, String response) {
		if (reqKey != latestReqKey) {
			return;
		}
		String message = "onFailed: reqKey=" + reqKey + ", statusCode="
				+ statusCode + " response=" + response;
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onException(int reqKey, WebAPIException ex) {
		if (reqKey != latestReqKey) {
			return;
		}
		String message = "onException: reqKey=" + reqKey + ", exception=" + ex;
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
