package com.kogasoftware.odt.invehicledevice.test;

import java.util.concurrent.Semaphore;

import org.json.JSONException;
import org.json.JSONObject;

import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.AsyncPostJSONTask;
import com.kogasoftware.odt.invehicledevice.AsyncPostJSONTask.OnJSONResultListener;

public class LoginTestCase extends
		ActivityInstrumentationTestCase2<EmptyActivity> {
	public LoginTestCase() {
		super("com.kogasoftware.odt.invehicledevice", EmptyActivity.class);
	}

	public void testValidLogin() throws JSONException, InterruptedException {
		String authUrl = "http://10.1.10.161/operators/sign_in.json";

		JSONObject user = new JSONObject();
		user.put("login", "i_mogi");
		user.put("password", "i_mogi");

		JSONObject req = new JSONObject();
		req.put("operator", user);

		final StringBuilder authToken = new StringBuilder();
		final Semaphore s = new Semaphore(0);

		AsyncPostJSONTask task = new AsyncPostJSONTask(authUrl, req.toString());
		task.setOnJSONResultListener(new OnJSONResultListener() {
			@Override
			public void onJSONResult(JSONObject result) {
				try {
					authToken.append(result.getString("authentication_token"));
				} catch (JSONException e) {
				}
				s.release();
			}
		});

		task.execute();

		s.acquire();
		assertFalse(authToken.toString().isEmpty());
	}

	public void testInvalidUserLogin() {

	}

	public void testInvalidPasswordLogin() {

	}
}
