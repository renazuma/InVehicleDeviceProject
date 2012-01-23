package com.kogasoftware.odt.invehicledevice;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kogasoftware.odt.invehicledevice.AsyncPostJSONTask.OnJSONResultListener;

public class LoginActivity extends Activity {
	private String authUrl = "http://176.32.84.9/users/sign_in.json";
	private EditText loginIdEditText;
	private EditText passwordEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.login);

		loginIdEditText = (EditText) findViewById(R.id.login_id_edit_text);
		passwordEditText = (EditText) findViewById(R.id.password_edit_text);

		Button btnLogin = (Button) findViewById(R.id.login_button);
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAuth();
			}
		});
	}

	private void submitAuth() {
		try {
			JSONObject user = new JSONObject();
			user.put("name", loginIdEditText.getText().toString());
			user.put("password", passwordEditText.getText().toString());

			JSONObject req = new JSONObject();
			req.put("user", user);

			final Context context = this;

			AsyncPostJSONTask task = new AsyncPostJSONTask(authUrl,
					req.toString());
			task.setOnJSONResultListener(new OnJSONResultListener() {
				@Override
				public void onJSONResult(JSONObject result) {
					try {
						String authToken = result
								.getString("authentication_token");

						Toast toast = new Toast(LoginActivity.this);
						LayoutInflater inflater = getLayoutInflater();
						View v = inflater.inflate(R.layout.accept_toast, null);
						TextView tv = (TextView) v
								.findViewById(R.id.login_message_text_view);
						tv.setText(R.string.accept_login_toast);
						toast.setDuration(Toast.LENGTH_LONG);
						toast.setView(v);
						toast.show();

						Intent data = new Intent();
						data.putExtra("authToken", authToken);
						setResult(RESULT_OK, data);
						finish();
					} catch (JSONException e) {
						Toast toast = new Toast(LoginActivity.this);
						LayoutInflater inflater = getLayoutInflater();
						View v = inflater.inflate(R.layout.failed_toast, null);
						TextView tv = (TextView) v
								.findViewById(R.id.login_message_text_view);
						tv.setText(R.string.fail_login_toast);
						toast.setDuration(Toast.LENGTH_LONG);
						toast.setView(v);
						toast.show();
					}
				}
			});
			task.execute();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}