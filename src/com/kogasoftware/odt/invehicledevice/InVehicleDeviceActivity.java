package com.kogasoftware.odt.invehicledevice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jp.tomorrowkey.android.vtextviewer.VTextView;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.modal.ConfigModal;
import com.kogasoftware.odt.invehicledevice.modal.MemoModal;
import com.kogasoftware.odt.invehicledevice.modal.NavigationModal;
import com.kogasoftware.odt.invehicledevice.modal.NotificationModal;
import com.kogasoftware.odt.invehicledevice.modal.PauseModal;
import com.kogasoftware.odt.invehicledevice.modal.ReturnPathModal;
import com.kogasoftware.odt.invehicledevice.modal.ScheduleChangedModal;
import com.kogasoftware.odt.invehicledevice.modal.ScheduleModal;
import com.kogasoftware.odt.invehicledevice.modal.StartCheckModal;
import com.kogasoftware.odt.invehicledevice.modal.StopCheckModal;
import com.kogasoftware.odt.invehicledevice.modal.StopModal;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceActivity extends MapActivity {
	private static final String TAG = InVehicleDeviceActivity.class
			.getSimpleName();
	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();
	private final InVehicleDeviceLogic logic = new InVehicleDeviceLogic();
	private final DataSource dataSource = DataSourceFactory.newInstance();
	private final Integer CHECK_LOGIC_INITIALIZED_INTERVAL = 3000;
	private final Integer POLL_VEHICLE_NOTIFICATION_INTERVAL = 10000;
	private final Handler pollVehicleNotificationHandler = new Handler();
	private final Runnable pollVehicleNotification = new Runnable() {
		@Override
		public void run() {
			List<VehicleNotification> vehicleNotifications = logic
					.pollVehicleNotifications();
			if (!vehicleNotifications.isEmpty()) {
				notificationModal.show(vehicleNotifications);
			}

			pollVehicleNotificationHandler.postDelayed(this,
					POLL_VEHICLE_NOTIFICATION_INTERVAL);
		}
	};

	private final Integer TOGGLE_DRIVING_VIEW_INTERVAL = 5000;
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

	private Button changeStatusButton = null;
	private Button scheduleButton = null;
	private Button mapButton = null;
	private Button configButton = null;
	private View drivingView1Layout = null;
	private View drivingView2Layout = null;
	private ListView reservationListView = null;
	private TextView statusTextView = null;

	private NavigationModal navigationModal = null;
	private ConfigModal configModal = null;
	private NotificationModal notificationModal = null;
	private ScheduleChangedModal scheduleChangedModal = null;
	private ScheduleModal scheduleModal = null;
	private StartCheckModal startCheckModal = null;
	private MemoModal memoModal = null;
	private PauseModal pauseModal = null;
	private ReturnPathModal returnPathModal = null;
	private StopCheckModal stopCheckModal = null;
	private StopModal stopModal = null;

	private ProgressDialog waitForInitializeProgressDialog = null;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.in_vehicle_device);

		voiceThread = new VoiceThread(getApplicationContext(), voices);
		voiceThread.start();

		statusTextView = (TextView) findViewById(R.id.status_text_view);
		changeStatusButton = (Button) findViewById(R.id.change_status_button);
		mapButton = (Button) findViewById(R.id.map_button);
		configButton = (Button) findViewById(R.id.config_button);
		scheduleButton = (Button) findViewById(R.id.schedule_button);

		drivingView1Layout = findViewById(R.id.driving_view1);
		drivingView2Layout = findViewById(R.id.driving_view2);

		navigationModal = new NavigationModal(this);
		configModal = new ConfigModal(this);
		startCheckModal = new StartCheckModal(this);
		scheduleModal = new ScheduleModal(this);
		memoModal = new MemoModal(this);
		pauseModal = new PauseModal(this);
		returnPathModal = new ReturnPathModal(this);
		stopCheckModal = new StopCheckModal(this);
		stopModal = new StopModal(this);
		notificationModal = new NotificationModal(this);
		scheduleChangedModal = new ScheduleChangedModal(this);

		toggleDrivingViewHandler.post(toggleDrivingView);
		pollVehicleNotificationHandler.post(pollVehicleNotification);

		changeStatusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (logic.getRestOperationSchedules().isEmpty()) {
					return;
				}
				if (logic.getStatus() == InVehicleDeviceStatus.Status.DRIVE) {
					enterPlatformStatus();
				} else {
					startCheckModal.show();
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
				configModal.show();
			}
		});

		scheduleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				scheduleModal.show(logic.getOperationSchedules());
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
				notificationModal.show(l);
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
				scheduleChangedModal.show(l);
			}
		});

		waitForInitializeProgressDialog = new ProgressDialog(this);
		waitForInitializeProgressDialog.setMessage("運行情報を受信しています");
		waitForInitializeProgressDialog
		.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (logic.isInitialized()) {
			findViewById(R.id.root_frame_layout).setVisibility(
					View.VISIBLE);
			if (logic.getStatus() == InVehicleDeviceStatus.Status.PLATFORM) {
				enterPlatformStatus();
			} else {
				enterDriveStatus();
			}
		} else {
			waitForInitializeProgressDialog.show();
			final Handler waitForInitHandler = new Handler();
			final Runnable waitForInitRunnable = new Runnable() {
				@Override
				public void run() {
					if (!waitForInitializeProgressDialog.isShowing()) {
						finish();
						return;
					}
					if (logic.isInitialized()) {
						findViewById(R.id.root_frame_layout).setVisibility(
								View.VISIBLE);
						enterDriveStatus();
						waitForInitializeProgressDialog.dismiss();
						return;
					}
					waitForInitHandler.postDelayed(this,
							CHECK_LOGIC_INITIALIZED_INTERVAL);
				}
			};
			waitForInitHandler.post(waitForInitRunnable);
		}
		navigationModal.onResumeActivity();
	}

	@Override
	public void onPause() {
		super.onPause();
		waitForInitializeProgressDialog.dismiss();
		navigationModal.onPauseActivity();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		toggleDrivingViewHandler.removeCallbacks(toggleDrivingView);
		pollVehicleNotificationHandler.removeCallbacks(pollVehicleNotification);
		voiceThread.interrupt();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void enterPlatformStatus() {
		logic.enterPlatformStatus();

		List<OperationSchedule> operationSchedules = logic
				.getRestOperationSchedules();
		if (operationSchedules.isEmpty()) {
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		if (!operationSchedule.getPlatform().isPresent()) {
			return;
		}
		DateFormat dateFormat = new SimpleDateFormat("H時m分"); // TODO
		Platform platform = operationSchedule.getPlatform().get();
		((TextView) findViewById(R.id.platform_name_text_view))
		.setText(platform.getName());
		((TextView) findViewById(R.id.platform_departure_time_text_view))
		.setText(dateFormat.format(operationSchedule
				.getDepartureEstimate()));

		List<Reservation> reservations = new LinkedList<Reservation>();
		reservations.addAll(operationSchedule.getReservationsAsArrival());
		reservations.addAll(operationSchedule.getReservationsAsDeparture());
		ReservationArrayAdapter adapter = new ReservationArrayAdapter(this,
				R.layout.reservation_list_row, reservations, operationSchedule);
		reservationListView.setAdapter(adapter);

		findViewById(R.id.waiting_layout).setVisibility(View.VISIBLE);
		statusTextView.setText("停車中");
		if (operationSchedules.size() > 1) {
			changeStatusButton.setText("出発する");
		} else {
			changeStatusButton.setText("確定する");
		}
	}

	public void enterDriveStatus() {
		logic.enterDriveStatus();

		List<OperationSchedule> operationSchedules = logic
				.getRestOperationSchedules();
		if (operationSchedules.isEmpty()) {
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		if (!operationSchedule.getPlatform().isPresent()) {
			return;
		}

		Platform platform = operationSchedule.getPlatform().get();
		((TextView) findViewById(R.id.next_platform_name_text_view))
		.setText(platform.getName());
		((TextView) findViewById(R.id.next_platform_name_ruby_text_view))
		.setText(platform.getNameRuby());

		DateFormat dateFormat = new SimpleDateFormat("H時m分"); // TODO
		((VTextView) findViewById(R.id.platform_name_1_beyond_text_view))
		.setText(platform.getName());
		((TextView) findViewById(R.id.platform_arrival_time_text_view))
		.setText(dateFormat.format(operationSchedule
				.getArrivalEstimate()));

		VTextView platformName2BeyondTextView = ((VTextView) findViewById(R.id.platform_name_2_beyond_text_view));
		platformName2BeyondTextView.setText("");
		if (operationSchedules.size() > 1) {
			Optional<Platform> optionalPlatform = operationSchedules.get(1)
					.getPlatform();
			if (optionalPlatform.isPresent()) {
				platformName2BeyondTextView.setText(optionalPlatform.get()
						.getName());
			}
		}

		VTextView platformName3BeyondTextView = ((VTextView) findViewById(R.id.platform_name_3_beyond_text_view));
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
		findViewById(R.id.waiting_layout).setVisibility(View.GONE);
		if (!voices.offer("出発します。次は、" + platform.getNameRuby() + "。"
				+ platform.getNameRuby() + "。")) {
			Log.w(TAG, "!voices.offer() failed");
		}
	}

	public void showMemoModal(Reservation reservation) {
		memoModal.show(reservation);
	}

	public void showReturnPathModal(Reservation reservation) {
		returnPathModal.show(reservation);
	}

	public void showPauseModal() {
		pauseModal.show();
	}

	public void showStopModal() {
		stopModal.show();
	}

	public void showStopCheckModal() {
		stopCheckModal.show();
	}

	public void showScheduleModal() {
		scheduleModal.show(logic.getOperationSchedules());
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	protected void finalize() {
		voiceThread.interrupt();
		try {
			super.finalize();
		} catch (Throwable e) {
		}
	}
}
