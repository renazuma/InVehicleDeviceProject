package com.kogasoftware.odt.invehicledevice;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jp.tomorrowkey.android.vtextviewer.VTextView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

public class InVehicleDeviceActivity extends MapActivity {
	private final String T = LogTag.get(InVehicleDeviceActivity.class);
	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();
	private final Integer DRIVING_VIEW_TOGGLE_INTERVAL = 5000;
	private final Handler drivingViewToggleHandler = new Handler();
	private final Runnable drivingViewToggleRunnable = new Runnable() {
		@Override
		public void run() {
			if (drivingView1Layout.getVisibility() == View.VISIBLE) {
				drivingView2Layout.setVisibility(View.VISIBLE);
				drivingView1Layout.setVisibility(View.GONE);
			} else {
				drivingView1Layout.setVisibility(View.VISIBLE);
				drivingView2Layout.setVisibility(View.GONE);
			}
			drivingViewToggleHandler.postDelayed(this,
					DRIVING_VIEW_TOGGLE_INTERVAL);
		}
	};

	private Thread voiceThread = new EmptyThread();
	private Integer status = 0;

	private Button changeStatusButton = null;
	private Button scheduleButton = null;
	private Button mapButton = null;
	private Button configButton = null;
	private Button stopCheckButton = null;
	private Button stopButton = null;
	private Button pauseButton = null;
	private Button memoButton = null;
	private Button returnPathButton = null;
	private TextView statusTextView = null;
	private MapView mapView = null;
	private View drivingView1Layout = null;
	private View drivingView2Layout = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.in_vehicle_device);

		Log.v(T, "onCreate");

		voiceThread = new VoiceThread(this, voices);
		voiceThread.start();

		statusTextView = (TextView) findViewById(R.id.status_text_view);

		changeStatusButton = (Button) findViewById(R.id.change_status_button);
		changeStatusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (status.equals(0)) {
					findViewById(R.id.waiting_layout).setVisibility(
							View.VISIBLE);
					statusTextView.setText("停車中");
					changeStatusButton.setText("出発");
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

		scheduleButton = (Button) findViewById(R.id.schedule_button);
		scheduleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.schedule_layout).setVisibility(View.VISIBLE);
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

		mapView = new MapView(this, "0_ZIi_adDM8WHxCX0OJTfcXhHO8jOsYOjLF7xow");
		mapView.setClickable(true);

		drivingView1Layout = findViewById(R.id.driving_view1);
		drivingView2Layout = findViewById(R.id.driving_view2);

		((VTextView) findViewById(R.id.next_stop_text_view))
				.setText("次の乗降場てすとて");
		((VTextView) findViewById(R.id.next_stop_but_one_text_view))
				.setText("次の次の乗降場てすと");
		((VTextView) findViewById(R.id.next_stop_but_two_text_view))
				.setText("次の次の次の乗降場てす");

		((FrameLayout) findViewById(R.id.map_layout)).addView(mapView, 0);

		((Button) findViewById(R.id.start_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						status = 0;
						statusTextView.setText("走行中");
						changeStatusButton.setText("到着");
						findViewById(R.id.check_start_layout).setVisibility(
								View.GONE);
						findViewById(R.id.waiting_layout).setVisibility(
								View.GONE);
						if (!voices.offer("出発します。次は、コガソフトウェア前。コガソフトウェア前。")) {
							Log.w(T, "!voices.offer() failed");
						}
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

		drivingViewToggleHandler.post(drivingViewToggleRunnable);
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
		drivingViewToggleHandler.removeCallbacks(drivingViewToggleRunnable);
		voiceThread.interrupt();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_BACK) {
			return super.onKeyDown(keyCode, event);
		}
		Boolean match = false;
		for (OverlayLinearLayout l : OverlayLinearLayout.getAttachedInstances()) {
			if (l.getVisibility() == View.VISIBLE) {
				l.hide();
				match = true;
			}
		}
		if (match) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
