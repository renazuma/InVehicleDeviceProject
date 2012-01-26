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
	private Button scheduleToggleButton = null;
	private Button mapButton = null;
	private Button configButton = null;
	private Button stopCheckButton = null;
	private Button stopButton = null;
	private Button pauseButton = null;
	private Button memoButton = null;
	private Button returnPathButton = null;

	private TextView statusTextView = null;

	private Integer status = 0;

	private void showScheduleLayout() {
		scheduleToggleButton.setText("予定を隠す");
		findViewById(R.id.schedule_layout).setVisibility(View.VISIBLE);
	}

	private void hideScheduleLayout() {
		scheduleToggleButton.setText("予定を表示");
		findViewById(R.id.schedule_layout).setVisibility(View.GONE);
	}

	private void toggleScheduleLayout() {
		if (findViewById(R.id.schedule_layout).getVisibility() == View.GONE) {
			showScheduleLayout();
		} else {
			hideScheduleLayout();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.in_vehicle_device);

		Log.v(T, "onCreate");

		statusTextView = (TextView) findViewById(R.id.status_text_view);

		scheduleToggleButton = (Button) findViewById(R.id.schedule_toggle_button);
		scheduleToggleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				toggleScheduleLayout();
			}
		});

		changeStatusButton = (Button) findViewById(R.id.change_status_button);
		changeStatusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (status.equals(0)) {
					findViewById(R.id.waiting_layout).setVisibility(
							View.VISIBLE);
					hideScheduleLayout();
					statusTextView.setText("停車中");
					changeStatusButton.setText("出発します");
					status = 1;
				} else {
					findViewById(R.id.check_start_layout).setVisibility(
							View.VISIBLE);
				}
			}
		});

		mapButton = (Button) findViewById(R.id.map_button);
		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.map_overlay).setVisibility(View.VISIBLE);
			}
		});

		configButton = (Button) findViewById(R.id.config_button);
		configButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.config_overlay).setVisibility(View.VISIBLE);
			}
		});

		stopCheckButton = (Button) findViewById(R.id.stop_check_button);
		stopCheckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.stop_check_overlay).setVisibility(
						View.VISIBLE);
			}
		});

		stopButton = (Button) findViewById(R.id.stop_button);
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.stop_overlay).setVisibility(View.VISIBLE);
			}
		});

		pauseButton = (Button) findViewById(R.id.pause_button);
		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.pause_overlay).setVisibility(View.VISIBLE);
			}
		});

		memoButton = (Button) findViewById(R.id.memo_button);
		memoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.memo_overlay).setVisibility(View.VISIBLE);
			}
		});

		returnPathButton = (Button) findViewById(R.id.return_path_button);
		returnPathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.return_path_overlay).setVisibility(
						View.VISIBLE);
			}
		});

		((Button) findViewById(R.id.start_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						hideScheduleLayout();
						status = 0;
						statusTextView.setText("走行中");
						changeStatusButton.setText("到着しました");
						findViewById(R.id.check_start_layout).setVisibility(
								View.GONE);
						findViewById(R.id.waiting_layout).setVisibility(
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
