package com.kogasoftware.odt.invehicledevice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class InVehicleDeviceActivity extends Activity {
	private final String T = LogTag.get(InVehicleDeviceActivity.class);
	Thread speakAlertThread = new Thread();

	private Button changeStatusButton = null;
	private TextView statusTextView = null;
	private Integer status = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.in_vehicle_device);

		Log.v(T, "onCreate");

		changeStatusButton = (Button) findViewById(R.id.change_status_button);
		statusTextView = (TextView) findViewById(R.id.status_text_view);

		changeStatusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (status.equals(0)) {
					statusTextView.setText("停車中");
					changeStatusButton.setText("出発します");
					status = 1;
				} else {
					findViewById(R.id.check_start_layout).setVisibility(
							View.VISIBLE);
				}
			}
		});

		((Button) findViewById(R.id.start_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						status = 0;
						statusTextView.setText("走行中");
						changeStatusButton.setText("到着しました");
						findViewById(R.id.check_start_layout).setVisibility(
								View.GONE);
					}
				});
		((Button) findViewById(R.id.start_cancel_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						findViewById(R.id.check_start_layout).setVisibility(
								View.GONE);
					}
				});
	}

	private String authToken = "";

	@Override
	public void onResume() {
		super.onResume();
		// if (authToken.isEmpty()) {
		// Intent intent = new Intent(this, LoginActivity.class);
		// startActivityForResult(intent, 0);
		// }
	}

	@Override
	protected void onActivityResult(int req, int res, Intent data) {
		super.onActivityResult(req, res, data);
		if (res == RESULT_OK) {
			authToken = data.getStringExtra("authToken");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
