package com.kogasoftware.odt.invehicledevice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.Logic;
import com.kogasoftware.odt.invehicledevice.logic.LogicLoadThread;
import com.kogasoftware.odt.invehicledevice.modalview.NavigationModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceActivity extends Activity {
	private static final String TAG = InVehicleDeviceActivity.class
			.getSimpleName();

	private static final int UPDATE_TIME_INTERVAL = 5000;

	private static final int WAIT_FOR_INITIALIZE_DIALOG_ID = 10;

	private static final int PLATFORM_PHASE_COLOR = Color.rgb(0xAA, 0xAA, 0xFF);
	private static final int FINISH_PHASE_COLOR = Color.rgb(0xAA, 0xAA, 0xAA);
	private static final int DRIVE_PHASE_COLOR = Color.rgb(0xAA, 0xFF, 0xAA);

	private final Handler handler = new Handler();
	private final CountDownLatch waitForStartUiLatch = new CountDownLatch(1);
	private final List<View> phaseColoredViews = new LinkedList<View>();
	private Thread logicLoadThread = new EmptyThread();
	private Logic logic = new Logic();
	private final Runnable updateTime = new Runnable() {
		@Override
		public void run() {
			Date now = Logic.getDate();
			DateFormat f = new SimpleDateFormat(getResources().getString(
					R.string.present_time_format));
			presentTimeTextView.setText(f.format(now));
			updateMinutesRemaining();

			handler.postDelayed(this, UPDATE_TIME_INTERVAL);
		}
	};

	private final PhoneStateListener updateSignalStrength = new PhoneStateListener() {
		private Integer getImageResourceId(SignalStrength signalStrength) { // TODO
			NetworkInfo networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (!networkInfo.isAvailable()) {
				return R.drawable.network_strength_0;
			}
			if (signalStrength.isGsm()) {
				Integer value = signalStrength.getGsmSignalStrength();
				if (value == 99 || value <= 2) {
					return R.drawable.network_strength_0;
				} else if (value <= 4) {
					return R.drawable.network_strength_1;
				} else if (value <= 7) {
					return R.drawable.network_strength_2;
				} else if (value <= 11) {
					return R.drawable.network_strength_3;
				}
				return R.drawable.network_strength_4;
			}
			return R.drawable.network_strength_0;
		}

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			networkStrengthImageView
					.setImageResource(getImageResourceId(signalStrength));
		};
	};

	// nullables
	private TextView statusTextView = null;
	private Button configButton = null;
	private View contentView = null;
	private Button mapButton = null;
	private TextView minutesRemainingTextView = null;
	private NavigationModalView navigationModalView = null;
	private ImageView networkStrengthImageView = null;
	private TextView presentTimeTextView = null;
	private Button scheduleButton = null;
	private ConnectivityManager connectivityManager = null;
	private TelephonyManager telephonyManager = null;
	private LocationManager locationManager = null;
	private Button changePhaseButton = null;
	private View waitingLayout = null;
	private OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key.equals("update")
					&& sharedPreferences.getBoolean(key, false)) { // TODO
				// 文字列定数
				Log.e(TAG, "SharedPreferences changed finish!"); // TODO
				finish();
			}
		}
	};

	@Subscribe
	public void enterDrivePhase(EnterDrivePhaseEvent event) {
		statusTextView.setText("走行中");
		setPhaseColor(DRIVE_PHASE_COLOR);

		changePhaseButton.setEnabled(true);
		changePhaseButton.setText("到着しました");
		changePhaseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logic.enterPlatformPhase();
			}
		});
	}

	@Subscribe
	public void enterFinishPhase(EnterFinishPhaseEvent event) {
		statusTextView.setText("");
		changePhaseButton.setEnabled(false);
		setPhaseColor(FINISH_PHASE_COLOR);
	}

	@Subscribe
	public void enterPlatformPhase(EnterPlatformPhaseEvent event) {
		updateMinutesRemaining();
		List<OperationSchedule> operationSchedules = logic
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			logic.enterFinishPhase();
			return;
		}

		statusTextView.setText("停車中");

		if (operationSchedules.size() > 1) {
			changePhaseButton.setText("出発する");
		} else {
			changePhaseButton.setText("確定する");
		}

		changePhaseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logic.showStartCheckModalView();
			}
		});
		changePhaseButton.setEnabled(true);
		setPhaseColor(PLATFORM_PHASE_COLOR); // TODO 定数
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// if (BuildConfig.DEBUG) {
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectAll().penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectAll().penaltyLog().penaltyDeath().build());
		// }
		setContentView(R.layout.in_vehicle_device);

		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		contentView = findViewById(android.R.id.content);
		presentTimeTextView = (TextView) findViewById(R.id.present_time_text_view);
		statusTextView = (TextView) findViewById(R.id.phase_text_view);
		changePhaseButton = (Button) findViewById(R.id.change_phase_button);
		mapButton = (Button) findViewById(R.id.map_button);
		configButton = (Button) findViewById(R.id.config_button);
		scheduleButton = (Button) findViewById(R.id.schedule_button);
		waitingLayout = findViewById(R.id.platform_phase_view);
		navigationModalView = (NavigationModalView) findViewById(R.id.navigation_modal_view);
		minutesRemainingTextView = (TextView) findViewById(R.id.minutes_remaining);
		networkStrengthImageView = (ImageView) findViewById(R.id.network_strength_image_view);

		contentView.setVisibility(View.GONE); // InVehicleDeviceLogicの準備が終わるまでcontentViewを非表示
		getWindow().getDecorView().setBackgroundColor(Color.BLACK); // ProgressDialogと親和性の高い色にする

		TypedArray typedArray = obtainStyledAttributes(new int[] { android.R.attr.background });
		Integer backgroundColor = typedArray.getColor(0, Color.WHITE);

		contentView.setBackgroundColor(backgroundColor);
		waitingLayout.setBackgroundColor(backgroundColor); // TODO

		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				navigationModalView.show();
			}
		});
		configButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logic.showConfigModalView();
			}
		});
		scheduleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logic.showScheduleModalView();
			}
		});

		View test = findViewById(R.id.phase_text_view);
		test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				VehicleNotification n = new VehicleNotification();
				n.setBody("通知です");
				List<VehicleNotification> l = new LinkedList<VehicleNotification>();
				l.add(n);
				logic.showNotificationModalView(l);
			}
		});
		View test2 = findViewById(R.id.icon_text_view);
		test2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				VehicleNotification n = new VehicleNotification();
				n.setBody("通知です");
				List<VehicleNotification> l = new LinkedList<VehicleNotification>();
				l.add(n);
				logic.showScheduleChangedModalView(l);
			}
		});

		for (Integer resourceId : new Integer[] { R.id.icon_text_view,
				R.id.operation_phase_layout, R.id.present_time_layout,
				R.id.side_button_view }) {
			View view = findViewById(resourceId);
			if (view != null) {
				phaseColoredViews.add(findViewById(resourceId));
			} else {
				Log.w(TAG, "view != null, resourceId=" + resourceId);
			}
		}

		handler.post(updateTime);

		telephonyManager.listen(updateSignalStrength,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(
						onSharedPreferenceChangeListener);

		logicLoadThread = new LogicLoadThread(this);
		logicLoadThread.start();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case WAIT_FOR_INITIALIZE_DIALOG_ID: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("運行情報を取得しています");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialogInterface) {
					if (!logic.isInitialized()) {
						finish();
					}
				}
			});
			return dialog;
		}

		default:
			return null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		handler.removeCallbacks(updateTime);

		logic.shutdown();
		logicLoadThread.interrupt();

		waitForStartUiLatch.countDown();

		telephonyManager.listen(updateSignalStrength,
				PhoneStateListener.LISTEN_NONE);

		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(
						onSharedPreferenceChangeListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		navigationModalView.onPauseActivity();
		try {
			dismissDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		} catch (IllegalArgumentException e) {
			// Dialogが表示されていない場合はこの例外が発生
			// Log.w(TAG, e);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		navigationModalView.onResumeActivity();
		if (!isFinishing() && waitForStartUiLatch.getCount() > 0) {
			showDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		}
	}

	private void setPhaseColor(Integer color) {
		for (View view : phaseColoredViews) {
			view.setBackgroundColor(color);
		}
	}

	@Subscribe
	public void startUi(LogicLoadThread.CompleteEvent event) {
		try {
			dismissDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		} catch (IllegalArgumentException e) {
			// Dialogが表示されていない場合はこの例外が発生
			// Log.w(TAG, e);
		}
		logic.shutdown();
		logic = event.logic;
		if (isFinishing()) {
			logic.shutdown();
			return;
		}
		logic.restoreStatus();
		contentView.setVisibility(View.VISIBLE);
		waitForStartUiLatch.countDown();
	}

	private void updateMinutesRemaining() {
		Date now = Logic.getDate();
		List<OperationSchedule> operationSchedules = logic
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			minutesRemainingTextView.setText("");
		} else {
			Date departure = operationSchedules.get(0).getDepartureEstimate();
			Long milliGap = departure.getTime() - now.getTime();
			minutesRemainingTextView.setText("" + (milliGap / 1000 / 60));
		}
	}

	public void waitForStartUi() throws InterruptedException {
		waitForStartUiLatch.await();
	}

}
