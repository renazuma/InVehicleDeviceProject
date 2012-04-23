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
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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
import com.kogasoftware.odt.invehicledevice.event.ExitEvent;
import com.kogasoftware.odt.invehicledevice.event.SignalStrengthChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.Logic;
import com.kogasoftware.odt.invehicledevice.logic.LogicLoadThread;
import com.kogasoftware.odt.invehicledevice.modalview.NavigationModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceActivity extends Activity {
	private static final String TAG = InVehicleDeviceActivity.class
			.getSimpleName();

	private static final int UPDATE_TIME_INTERVAL = 3000;

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
			handler.postDelayed(this, UPDATE_TIME_INTERVAL);
		}
	};

	// nullables
	private TextView statusTextView = null;
	private ImageView networkStrengthImageView = null;
	private TextView presentTimeTextView = null;
	private View contentView = null;
	private Button mapButton = null;
	private Button configButton = null;
	private Button scheduleButton = null;
	private Button changePhaseButton = null;
	private NavigationModalView navigationModalView = null;

	@Subscribe
	public void changeNetworkStrengthImage(SignalStrengthChangedEvent e) {
		int imageResourceId = R.drawable.network_strength_4;
		if (e.signalStrengthPercentage == 0) {
			imageResourceId = R.drawable.network_strength_0;
		} else if (e.signalStrengthPercentage <= 25) {
			imageResourceId = R.drawable.network_strength_1;
		} else if (e.signalStrengthPercentage <= 50) {
			imageResourceId = R.drawable.network_strength_2;
		} else if (e.signalStrengthPercentage <= 75) {
			imageResourceId = R.drawable.network_strength_3;
		}
		networkStrengthImageView.setImageResource(imageResourceId);
	}

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

	@Subscribe
	public void finish(ExitEvent exitEvent) {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog().penaltyDeath().build());
		}
		setContentView(R.layout.in_vehicle_device);

		contentView = findViewById(android.R.id.content);
		presentTimeTextView = (TextView) findViewById(R.id.present_time_text_view);
		statusTextView = (TextView) findViewById(R.id.phase_text_view);
		changePhaseButton = (Button) findViewById(R.id.change_phase_button);
		mapButton = (Button) findViewById(R.id.map_button);
		configButton = (Button) findViewById(R.id.config_button);
		scheduleButton = (Button) findViewById(R.id.schedule_button);
		navigationModalView = (NavigationModalView) findViewById(R.id.navigation_modal_view);
		networkStrengthImageView = (ImageView) findViewById(R.id.network_strength_image_view);

		contentView.setVisibility(View.GONE); // InVehicleDeviceLogicの準備が終わるまでcontentViewを非表示
		getWindow().getDecorView().setBackgroundColor(Color.BLACK); // ProgressDialogと親和性の高い色にする

		TypedArray typedArray = obtainStyledAttributes(new int[] { android.R.attr.background });
		Integer backgroundColor = typedArray.getColor(0, Color.WHITE);

		contentView.setBackgroundColor(backgroundColor);

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

	public void waitForStartUi() throws InterruptedException {
		waitForStartUiLatch.await();
	}

}
