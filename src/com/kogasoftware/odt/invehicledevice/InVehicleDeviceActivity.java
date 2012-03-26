package com.kogasoftware.odt.invehicledevice;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jp.tomorrowkey.android.vtextviewer.VTextView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
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
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;


public class InVehicleDeviceActivity extends MapActivity {
	private final String TAG = InVehicleDeviceActivity.class.getSimpleName();
	private final DataSource dataSource = DataSourceFactory.newInstance();
	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();
	private final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
	private final Integer POLL_VEHICLE_NOTIFICATION_INTERVAL = 10000;
	private final Handler pollVehicleNotificationHandler = new Handler();
	private final Runnable pollVehicleNotification = new Runnable() {
		@Override
		public void run() {
			try {
				List<VehicleNotification> vehicleNotifications = dataSource
						.getVehicleNotifications();
				if (!vehicleNotifications.isEmpty()) {
					notificationModal.show(vehicleNotifications);
				}
			} catch (WebAPIException e) {
				Log.e(TAG, "", e);
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

	private enum Status {
		DRIVE, PLATFORM,
	};

	private Status status = Status.DRIVE;

	private Button changeStatusButton = null;
	private Button scheduleButton = null;
	private Button mapButton = null;
	private Button configButton = null;
	private TextView statusTextView = null;
	private View drivingView1Layout = null;
	private View drivingView2Layout = null;
	private ListView usersListView = null;

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

		((VTextView) findViewById(R.id.next_stop_text_view))
		.setText("次の乗降場てすとて");
		((VTextView) findViewById(R.id.next_stop_but_one_text_view))
		.setText("次の次の乗降場てすと");
		((VTextView) findViewById(R.id.next_stop_but_two_text_view))
		.setText("次の次の次の乗降場てす");

		toggleDrivingViewHandler.post(toggleDrivingView);
		pollVehicleNotificationHandler.post(pollVehicleNotification);

		changeStatusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (status == Status.DRIVE) {
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
				scheduleModal.show(operationSchedules);
			}
		});

		usersListView = (ListView) findViewById(R.id.users_list_view);

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

		try {
			operationSchedules.addAll(dataSource.getOperationSchedules());
		} catch (WebAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		navigationModal.onResumeActivity();
	}

	@Override
	public void onPause() {
		super.onPause();
		navigationModal.onPauseActivity();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		toggleDrivingViewHandler.removeCallbacks(toggleDrivingView);
		pollVehicleNotificationHandler.removeCallbacks(pollVehicleNotification);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void enterPlatformStatus() { // TODO
		List<Reservation> rl = new LinkedList<Reservation>();
		if (!operationSchedules.isEmpty()) {
			OperationSchedule o = operationSchedules.get(0);
			rl.addAll(o.getReservationsAsArrival());
			rl.addAll(o.getReservationsAsDeparture());
		}

		ReservationArrayAdapter usersAdapter = new ReservationArrayAdapter(
				this, R.layout.reservation_list_row, rl);
		usersListView.setAdapter(usersAdapter);

		status = Status.PLATFORM;
		findViewById(R.id.waiting_layout).setVisibility(View.VISIBLE);
		statusTextView.setText("停車中");
		changeStatusButton.setText("出発する");
	}

	public void enterDriveStatus() {
		status = Status.DRIVE;
		statusTextView.setText("走行中");
		changeStatusButton.setText("到着しました");
		findViewById(R.id.waiting_layout).setVisibility(View.GONE);
		if (!voices.offer("出発します。次は、コガソフトウェア前。コガソフトウェア前。")) {
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
		scheduleModal.show(operationSchedules);
	}

	public DataSource getDataSource() {
		return dataSource;
	}
}
