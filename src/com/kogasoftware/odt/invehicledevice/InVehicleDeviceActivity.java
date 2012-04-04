package com.kogasoftware.odt.invehicledevice;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jp.tomorrowkey.android.vtextviewer.VTextView;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic.EnterDriveStatusEvent;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic.EnterFinishStatusEvent;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic.EnterPlatformStatusEvent;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.modal.Modal;
import com.kogasoftware.odt.invehicledevice.modal.NavigationModal;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceActivity extends MapActivity {
	private static final String TAG = InVehicleDeviceActivity.class
			.getSimpleName();
	private static final int WAIT_FOR_INITIALIZE_DIALOG_ID = 10;

	protected static File getSavedStatusFile(Context context) {
		return new File(context.getFilesDir() + File.separator
				+ InVehicleDeviceStatus.class.getCanonicalName()
				+ ".serialized");
	}

	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();

	private static final Integer CHECK_LOGIC_INITIALIZED_INTERVAL = 3000;

	private static final Integer UPDATE_TIME_INTERVAL = 5000;
	private final Handler updateTimeHandler = new Handler();
	private final Runnable updateTime = new Runnable() {
		@Override
		public void run() {
			DateFormat f = new SimpleDateFormat(getResources().getString(
					R.string.present_time_format));
			presentTimeTextView.setText(f.format(new Date()));
			updateTimeHandler.postDelayed(this, UPDATE_TIME_INTERVAL);
		}
	};

	private static final Integer POLL_VEHICLE_NOTIFICATION_INTERVAL = 10000;
	private final Handler pollVehicleNotificationHandler = new Handler();
	private final Runnable pollVehicleNotification = new Runnable() {
		@Override
		public void run() {
			List<VehicleNotification> vehicleNotifications = logic
					.pollVehicleNotifications();
			if (!vehicleNotifications.isEmpty()) {
				logic.showNotificationModal(vehicleNotifications);
			}

			pollVehicleNotificationHandler.postDelayed(this,
					POLL_VEHICLE_NOTIFICATION_INTERVAL);
		}
	};

	private static final Integer TOGGLE_DRIVING_VIEW_INTERVAL = 5000;
	private final Handler toggleDrivingViewHandler = new Handler();
	private final Runnable toggleDrivingView = new Runnable() {
		@Override
		public void run() {
			if (drivingView1Layout.getVisibility() == View.VISIBLE) {
				drivingView2Layout.setVisibility(View.VISIBLE);
				drivingView1Layout.setVisibility(View.GONE);
			} else {
				drivingView1Layout.setVisibility(View.VISIBLE);
				drivingView2Layout.setVisibility(View.GONE);
			}
			toggleDrivingViewHandler.postDelayed(this,
					TOGGLE_DRIVING_VIEW_INTERVAL);
		}
	};

	private Thread voiceThread = new EmptyThread();
	private InVehicleDeviceLogic logic = new InVehicleDeviceLogic();

	// nullables
	private Button changeStatusButton = null;
	private Button configButton = null;
	private Button mapButton = null;
	private Button scheduleButton = null;
	private ListView reservationListView = null;
	private TextView nextPlatformNameRubyTextView = null;
	private TextView nextPlatformNameTextView = null;
	private TextView platformArrivalTimeTextView = null;
	private TextView platformDepartureTimeTextView = null;
	private TextView platformNameTextView = null;
	private TextView presentTimeTextView = null;
	private TextView statusTextView = null;
	private View drivingView1Layout = null;
	private View drivingView2Layout = null;
	private View waitingLayout = null;
	private View drivingLayout = null;
	private View finishLayout = null;
	private NavigationModal navigationModal = null;
	private VTextView platformName1BeyondTextView = null;
	private VTextView platformName2BeyondTextView = null;
	private VTextView platformName3BeyondTextView = null;

	@Subscribe
	public void enterDriveStatus(EnterDriveStatusEvent event) {
		List<OperationSchedule> operationSchedules = logic
				.getRestOperationSchedules();
		if (operationSchedules.isEmpty()) {
			logic.enterFinishStatus();
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		if (!operationSchedule.getPlatform().isPresent()) {
			return; // TODO
		}

		Platform platform = operationSchedule.getPlatform().get();
		nextPlatformNameTextView.setText(platform.getName());
		nextPlatformNameRubyTextView.setText(platform.getNameRuby());

		DateFormat dateFormat = new SimpleDateFormat(getResources().getString(
				R.string.platform_arrival_time_format));

		platformName1BeyondTextView.setText(platform.getName());
		platformArrivalTimeTextView.setText(dateFormat.format(operationSchedule
				.getArrivalEstimate()));

		platformName2BeyondTextView.setText("");
		if (operationSchedules.size() > 1) {
			Optional<Platform> optionalPlatform = operationSchedules.get(1)
					.getPlatform();
			if (optionalPlatform.isPresent()) {
				platformName2BeyondTextView.setText(optionalPlatform.get()
						.getName());
			}
		}

		platformName3BeyondTextView.setText("");
		if (operationSchedules.size() > 2) {
			Optional<Platform> optionalPlatform = operationSchedules.get(2)
					.getPlatform();
			if (optionalPlatform.isPresent()) {
				platformName3BeyondTextView.setText(optionalPlatform.get()
						.getName());
			}
		}

		statusTextView.setText("走行中");
		changeStatusButton.setText("到着しました");
		if (!voices.offer("出発します。次は、" + platform.getNameRuby() + "。"
				+ platform.getNameRuby() + "。")) {
			Log.w(TAG, "!voices.offer() failed");
		}

		waitingLayout.setVisibility(View.GONE);
		drivingLayout.setVisibility(View.VISIBLE);
		finishLayout.setVisibility(View.GONE);
		changeStatusButton.setEnabled(true);
	}

	@Subscribe
	public void enterFinishStatus(EnterFinishStatusEvent event) {
		waitingLayout.setVisibility(View.GONE);
		drivingLayout.setVisibility(View.GONE);
		finishLayout.setVisibility(View.VISIBLE);
		statusTextView.setText("");
		changeStatusButton.setEnabled(false);
	}

	@Subscribe
	public void enterPlatformStatus(EnterPlatformStatusEvent event) {
		List<OperationSchedule> operationSchedules = logic
				.getRestOperationSchedules();
		if (operationSchedules.isEmpty()) {
			logic.enterFinishStatus();
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		if (!operationSchedule.getPlatform().isPresent()) {
			return;
		}
		DateFormat dateFormat = new SimpleDateFormat("H時m分"); // TODO
		Platform platform = operationSchedule.getPlatform().get();
		platformNameTextView.setText(platform.getName());
		platformDepartureTimeTextView.setText(dateFormat
				.format(operationSchedule.getDepartureEstimate()));

		List<Reservation> reservations = new LinkedList<Reservation>();
		reservations.addAll(operationSchedule.getReservationsAsArrival());
		reservations.addAll(operationSchedule.getReservationsAsDeparture());
		ReservationArrayAdapter adapter = new ReservationArrayAdapter(this,
				R.layout.reservation_list_row, reservations, logic,
				operationSchedule);
		reservationListView.setAdapter(adapter);

		statusTextView.setText("停車中");
		if (operationSchedules.size() > 1) {
			changeStatusButton.setText("出発する");
		} else {
			changeStatusButton.setText("確定する");
		}

		waitingLayout.setVisibility(View.VISIBLE);
		drivingLayout.setVisibility(View.GONE);
		finishLayout.setVisibility(View.GONE);
		changeStatusButton.setEnabled(true);
	}

	@Override
	protected void finalize() {
		try {
			voiceThread.interrupt();
		} finally {
			try {
				super.finalize();
			} catch (Throwable e) {
			}
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.in_vehicle_device);
		View contentView = findViewById(android.R.id.content);
		contentView.setVisibility(View.INVISIBLE);
		contentView.setBackgroundColor(Color.WHITE); // TODO XMLで指定
		getWindow().getDecorView().setBackgroundColor(Color.BLACK); // TODO XML

		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll().penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll().penaltyLog().penaltyDeath().build());
		}

		// try {
		// new AsyncTask<Void, Void, InVehicleDeviceLogic>() {
		// @Override
		// protected InVehicleDeviceLogic doInBackground(Void... arguments) {
		// return new InVehicleDeviceLogic(
		// getSavedStatusFile(InVehicleDeviceActivity.this));
		// }

		// @Override
		// protected void onPostExecute(InVehicleDeviceLogic result) {
		// logic = result;
		logic = new InVehicleDeviceLogic(getSavedStatusFile(this)); // TODO
		logic.register(InVehicleDeviceActivity.this);
		for (int resourceId : new int[] { R.id.config_modal,
				R.id.start_check_modal, R.id.schedule_modal, R.id.memo_modal,
				R.id.pause_modal, R.id.return_path_modal,
				R.id.stop_check_modal, R.id.stop_modal,
				R.id.notification_modal, R.id.schedule_changed_modal }) {
			View view = findViewById(resourceId);
			if (view instanceof Modal) {
				((Modal) view).setLogic(logic);
			} else {
				Log.e(TAG, "!(view instanceof Modal)");
			}
		}
		// }
		// }.execute().get(); // TODO これを消してきちんと非同期に処理する
		// } catch (InterruptedException e) {
		// finish();
		// } catch (ExecutionException e) {
		// finish();
		// }

		voiceThread = new VoiceThread(getApplicationContext(), voices);
		voiceThread.start();

		presentTimeTextView = (TextView) findViewById(R.id.present_time_text_view);
		nextPlatformNameTextView = (TextView) findViewById(R.id.next_platform_name_text_view);
		nextPlatformNameRubyTextView = (TextView) findViewById(R.id.next_platform_name_ruby_text_view);
		statusTextView = (TextView) findViewById(R.id.status_text_view);
		changeStatusButton = (Button) findViewById(R.id.change_status_button);
		mapButton = (Button) findViewById(R.id.map_button);
		configButton = (Button) findViewById(R.id.config_button);
		scheduleButton = (Button) findViewById(R.id.schedule_button);
		platformName1BeyondTextView = (VTextView) findViewById(R.id.platform_name_1_beyond_text_view);
		platformName2BeyondTextView = (VTextView) findViewById(R.id.platform_name_2_beyond_text_view);
		platformName3BeyondTextView = (VTextView) findViewById(R.id.platform_name_3_beyond_text_view);
		platformNameTextView = (TextView) findViewById(R.id.platform_name_text_view);
		platformDepartureTimeTextView = (TextView) findViewById(R.id.platform_departure_time_text_view);
		platformArrivalTimeTextView = (TextView) findViewById(R.id.platform_arrival_time_text_view);
		waitingLayout = findViewById(R.id.waiting_layout);
		drivingLayout = findViewById(R.id.driving_layout);
		finishLayout = findViewById(R.id.finish_layout);

		drivingView1Layout = findViewById(R.id.driving_view1);
		drivingView2Layout = findViewById(R.id.driving_view2);
		TypedArray typedArray = obtainStyledAttributes(new int[] { android.R.attr.background });
		int backgroundColor = typedArray.getColor(0, Color.WHITE);
		drivingView1Layout.setBackgroundColor(backgroundColor); // TODO XMLで指定
		drivingView2Layout.setBackgroundColor(backgroundColor); // TODO
		waitingLayout.setBackgroundColor(backgroundColor); // TODO

		navigationModal = (NavigationModal) findViewById(R.id.navigation_modal);

		toggleDrivingViewHandler.post(toggleDrivingView);
		pollVehicleNotificationHandler.post(pollVehicleNotification);
		updateTimeHandler.post(updateTime);

		changeStatusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (logic.getRestOperationSchedules().isEmpty()) {
					return;
				}
				if (logic.getStatus() == InVehicleDeviceStatus.Status.DRIVE) {
					logic.enterPlatformStatus();
				} else {
					logic.showStartCheckModal();
				}
			}
		});

		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				navigationModal.show();
			}
		});

		configButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logic.showConfigModal();
			}
		});

		scheduleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logic.showScheduleModal();
			}
		});

		reservationListView = (ListView) findViewById(R.id.reservation_list_view);
		Button reservationScrollUpButton = (Button) findViewById(R.id.reservation_scroll_up_button);
		reservationScrollUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = reservationListView
						.getFirstVisiblePosition();
				reservationListView.smoothScrollToPosition(position);
			}
		});
		Button reservationScrollDownButton = (Button) findViewById(R.id.reservation_scroll_down_button);
		reservationScrollDownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = reservationListView.getLastVisiblePosition();
				reservationListView.smoothScrollToPosition(position);
			}
		});

		View test = findViewById(R.id.status_text_view);
		test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				VehicleNotification n = new VehicleNotification();
				n.setBody("通知です");
				List<VehicleNotification> l = new LinkedList<VehicleNotification>();
				l.add(n);
				logic.showNotificationModal(l);
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
				logic.showScheduleChangedModal(l);
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case WAIT_FOR_INITIALIZE_DIALOG_ID:
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("運行情報を受信しています");
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					if (!logic.isInitialized()) {
						finish();
					}
				}
			});
			return dialog;
		default:
			return null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		toggleDrivingViewHandler.removeCallbacks(toggleDrivingView);
		pollVehicleNotificationHandler.removeCallbacks(pollVehicleNotification);
		voiceThread.interrupt();
		logic.shutdown();
	}

	@Override
	public void onPause() {
		super.onPause();
		navigationModal.onPauseActivity();
	}

	@Override
	public void onResume() {
		super.onResume();
		navigationModal.onResumeActivity();

		if (logic.isInitialized()) {
			findViewById(android.R.id.content).setVisibility(View.VISIBLE);
			if (logic.getStatus() == InVehicleDeviceStatus.Status.PLATFORM) {
				logic.enterPlatformStatus();
			} else {
				logic.enterDriveStatus();
			}
			return;
		}

		// not initialized yet
		showDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		final Handler waitForInitializeHandler = new Handler();
		final Runnable waitForInitializeRunnable = new Runnable() {
			@Override
			public void run() {
				if (!logic.isInitialized()) {
					waitForInitializeHandler.postDelayed(this,
							CHECK_LOGIC_INITIALIZED_INTERVAL);
					return;
				}
				findViewById(android.R.id.content).setVisibility(View.VISIBLE);
				logic.enterDriveStatus();
				try {
					dismissDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
				} catch (IllegalArgumentException e) {
					// Log.w(TAG, e);
				}
			}
		};
		waitForInitializeHandler.post(waitForInitializeRunnable);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
