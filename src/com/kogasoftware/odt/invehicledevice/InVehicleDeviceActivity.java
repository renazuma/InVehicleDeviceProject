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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.in_vehicle_device);

		Log.v(T, "onCreate");

		changeStatusButton = (Button) findViewById(R.id.change_status_button);
		statusTextView = (TextView) findViewById(R.id.status_text_view);

		changeStatusButton.setOnClickListener(new OnClickListener() {
			private Integer status = 0;

			@Override
			public void onClick(View view) {
				if (status.equals(0)) {
					statusTextView.setText("停車中");
					changeStatusButton.setText("出発");
					status = 1;
				} else {
					statusTextView.setText("走行中");
					changeStatusButton.setText("到着");
					status = 0;
				}
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
