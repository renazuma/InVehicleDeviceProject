package com.kogasoftware.odt.invehicledevice.ui.activity;

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
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTaskThread;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.ExitEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.SignalStrengthChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleAlertEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationReceivedAlertEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ArrivalCheckModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NavigationModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleModalView;
import com.kogasoftware.odt.invehicledevice.ui.phaseview.PlatformPhaseView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class InVehicleDeviceActivity extends Activity {
	private static final String TAG = InVehicleDeviceActivity.class
			.getSimpleName();

	private static final int UPDATE_TIME_INTERVAL_MILLIS = 3000;
	private static final int ALERT_SHOW_INTERVAL_MILLIS = 500;

	private static final int WAIT_FOR_INITIALIZE_DIALOG_ID = 10;

	private static final int PLATFORM_PHASE_COLOR = Color.rgb(0xAA, 0xAA, 0xFF);
	private static final int FINISH_PHASE_COLOR = Color.rgb(0xAA, 0xAA, 0xAA);
	private static final int DRIVE_PHASE_COLOR = Color.rgb(0xAA, 0xFF, 0xAA);

	private final Handler handler = new Handler();
	private final CountDownLatch waitForStartUiLatch = new CountDownLatch(1);
	private final List<View> phaseColoredViews = new LinkedList<View>();
	private Thread backgroundThread = new EmptyThread();
	private CommonLogic commonLogic = new CommonLogic();
	private final Runnable updateTime = new Runnable() {
		@Override
		public void run() {
			Date now = CommonLogic.getDate();
			DateFormat f = new SimpleDateFormat(getResources().getString(
					R.string.present_time_format));
			presentTimeTextView.setText(f.format(now));
			handler.postDelayed(this, UPDATE_TIME_INTERVAL_MILLIS);
		}
	};

	private final Runnable alertVehicleNotification = new Runnable() {
		private Integer count = 0;

		@Override
		public void run() {
			if (count > 10) { // TODO 定数
				count = 0;
				alertImageView.setVisibility(View.GONE);
				return;
			}
			count++;
			alertImageView.setVisibility(count % 2 == 0 ? View.VISIBLE
					: View.GONE);
			handler.postDelayed(this, ALERT_SHOW_INTERVAL_MILLIS);
		}
	};

	private final Runnable alertOperationScheduleChanged = new Runnable() {
		private Integer count = 0;

		@Override
		public void run() {
			if (count > 10) { // TODO 定数
				count = 0;
				alertImageView.setVisibility(View.GONE);
				return;
			}
			count++;
			alertImageView.setVisibility(count % 2 == 0 ? View.VISIBLE
					: View.GONE);
			handler.postDelayed(this, ALERT_SHOW_INTERVAL_MILLIS);
		}
	};

	public static class ResumedEvent {
	}

	public static class PausedEvent {
	}

	// nullables
	private View contentView = null;
	private Button mapButton = null;
	private Button scheduleButton = null;
	private Button changePhaseButton = null;
	private ImageView networkStrengthImageView = null;
	private ImageView alertImageView = null;
	private NavigationModalView navigationModalView = null;
	private TextView statusTextView = null;
	private TextView presentTimeTextView = null;

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
				commonLogic.postEvent(new ArrivalCheckModalView.ShowEvent());
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
		List<OperationSchedule> operationSchedules = commonLogic
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			commonLogic.postEvent(new EnterFinishPhaseEvent());
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
				commonLogic.postEvent(new PlatformPhaseView.DepartureCheckEvent());
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
			// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
			// .detectAll().penaltyLog().build());
			// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
			// .detectAll().penaltyLog().penaltyDeath().build());
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.in_vehicle_device);

		contentView = findViewById(android.R.id.content);
		presentTimeTextView = (TextView) findViewById(R.id.present_time_text_view);
		statusTextView = (TextView) findViewById(R.id.phase_text_view);
		changePhaseButton = (Button) findViewById(R.id.change_phase_button);
		mapButton = (Button) findViewById(R.id.map_button);
		scheduleButton = (Button) findViewById(R.id.schedule_button);
		navigationModalView = (NavigationModalView) findViewById(R.id.navigation_modal_view);
		networkStrengthImageView = (ImageView) findViewById(R.id.network_strength_image_view);
		alertImageView = (ImageView) findViewById(R.id.alert_image_view);

		contentView.setVisibility(View.GONE); // InVehicleDeviceLogicの準備が終わるまでcontentViewを非表示
		getWindow().getDecorView().setBackgroundColor(Color.BLACK); // ProgressDialogと親和性の高い色にする

		TypedArray typedArray = obtainStyledAttributes(new int[] { android.R.attr.background });
		Integer backgroundColor = typedArray.getColor(0, Color.WHITE);

		contentView.setBackgroundColor(backgroundColor);

		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				commonLogic.postEvent(new NavigationModalView.ShowEvent());
			}
		});
		scheduleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				commonLogic.postEvent(new ScheduleModalView.ShowEvent());
			}
		});

		for (Integer resourceId : new Integer[] { R.id.icon_layout,
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

		backgroundThread = new BackgroundTaskThread(this);
		backgroundThread.start();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case WAIT_FOR_INITIALIZE_DIALOG_ID: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(Html.fromHtml("<big>運行情報を取得しています</big>"));
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialogInterface) {
					if (!commonLogic.isOperationScheduleInitialized()) {
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
		
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		handler.removeCallbacks(updateTime);
		handler.removeCallbacks(alertOperationScheduleChanged);
		handler.removeCallbacks(alertVehicleNotification);

		commonLogic.dispose();
		backgroundThread.interrupt();

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

		commonLogic.postEvent(new PausedEvent());
	}

	@Override
	public void onResume() {
		super.onResume();
		navigationModalView.onResumeActivity();
		if (!isFinishing() && waitForStartUiLatch.getCount() > 0) {
			showDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		}

		commonLogic.postEvent(new ResumedEvent());
	}

	private void setPhaseColor(Integer color) {
		for (View view : phaseColoredViews) {
			view.setBackgroundColor(color);
		}
	}

	@Subscribe
	public void startAlertOperationScheduleChanged(
			UpdatedOperationScheduleAlertEvent e) {
		if (!isFinishing()) {
			handler.post(alertOperationScheduleChanged);
		}
	}

	@Subscribe
	public void startAlertVehicleNotification(
			VehicleNotificationReceivedAlertEvent e) {
		if (!isFinishing()) {
			handler.post(alertVehicleNotification);
		}
	}

	@Subscribe
	public void startUi(CommonLogicLoadCompleteEvent event) {
		try {
			dismissDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		} catch (IllegalArgumentException e) {
			// Dialogが表示されていない場合はこの例外が発生
			// Log.w(TAG, e);
		}
		commonLogic.dispose();
		commonLogic = event.commonLogic;
		if (isFinishing()) {
			commonLogic.dispose();
			return;
		}
		commonLogic.restoreStatus();
		waitForStartUiLatch.countDown();

		// UI開始時に、各部品が出たり消えたりするのでそれを隠すためViewの表示を遅延させる
		(new Handler()).post(new Runnable() {
			@Override
			public void run() {
				contentView.setVisibility(View.VISIBLE);
			}
		});
	}

	public void waitForStartUi() throws InterruptedException {
		waitForStartUiLatch.await();
	}
}
