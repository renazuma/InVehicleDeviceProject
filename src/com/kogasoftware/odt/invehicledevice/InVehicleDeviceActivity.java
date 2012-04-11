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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.WrapperListAdapter;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic.AddUnexpectedReservationEvent;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic.EnterDriveStatusEvent;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic.EnterFinishStatusEvent;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceLogic.EnterPlatformStatusEvent;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.modal.NavigationModal;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceActivity extends Activity {
	private static final String TAG = InVehicleDeviceActivity.class
			.getSimpleName();

	private static final int WAIT_FOR_INITIALIZE_DIALOG_ID = 10;
	private static final int ADD_UNEXPECTED_RESERVATION_DIALOG_ID = 11;

	private static final Integer TOGGLE_DRIVING_VIEW_INTERVAL = 5000;

	private static final Integer POLL_VEHICLE_NOTIFICATION_INTERVAL = 10000;

	protected static File getSavedStatusFile(Context context) {
		return new File(context.getFilesDir() + File.separator
				+ InVehicleDeviceStatus.class.getCanonicalName()
				+ ".serialized");
	}

	private final BlockingQueue<String> voices = new LinkedBlockingQueue<String>();

	final Handler handler = new Handler();

	private static final Integer UPDATE_TIME_INTERVAL = 5000;
	private final Runnable updateTime = new Runnable() {
		@Override
		public void run() {
			DateFormat f = new SimpleDateFormat(getResources().getString(
					R.string.present_time_format));
			presentTimeTextView.setText(f.format(new Date()));
			handler.postDelayed(this, UPDATE_TIME_INTERVAL);
		}
	};

	private final Runnable pollVehicleNotification = new Runnable() {
		@Override
		public void run() {
			List<VehicleNotification> vehicleNotifications = logic
					.pollVehicleNotifications();
			if (!vehicleNotifications.isEmpty()) {
				logic.showNotificationModal(vehicleNotifications);
			}

			handler.postDelayed(this, POLL_VEHICLE_NOTIFICATION_INTERVAL);
		}
	};

	private final Runnable toggleDrivingView = new Runnable() {
		@Override
		public void run() {
			if (drivingView2Layout.getVisibility() == View.VISIBLE) {
				drivingView2Layout.setVisibility(View.GONE);
			} else {
				drivingView2Layout.setVisibility(View.VISIBLE);
			}
			handler.postDelayed(this, TOGGLE_DRIVING_VIEW_INTERVAL);
		}
	};
	private InVehicleDeviceLogic logic = new InVehicleDeviceLogic();

	private Thread logicLoadThread = new EmptyThread();
	private Thread voiceThread = new EmptyThread();
	// nullables
	private View contentView = null;

	private Button changeStatusButton = null;
	private Button configButton = null;
	private Button mapButton = null;
	private Button scheduleButton = null;
	private Button reservationScrollDownButton = null;
	private Button reservationScrollUpButton = null;
	private ListView reservationListView = null;
	private View reservationListFooterView = null;
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
	private ToggleButton showAllRidingReservationsButton = null;
	private ToggleButton showFutureReservationsButton = null;
	private ToggleButton showMissedReservationsButton = null;
	private Button addUnexpectedReservationButton = null;

	@Subscribe
	public void addUnexpectedReservation(AddUnexpectedReservationEvent event) {
		ListAdapter adapter = reservationListView.getAdapter();
		if (!(adapter instanceof WrapperListAdapter)) {
			Log.w(TAG, "!(adapter instanceof WrapperListAdapter)");
			return;
		}
		if (!(((WrapperListAdapter) adapter).getWrappedAdapter() instanceof ReservationArrayAdapter)) {
			Log.w(TAG,
					"!(((WrapperListAdapter) adapter).getWrappedAdapter() instanceof ReservationArrayAdapter)");
			return;
		}
		((ReservationArrayAdapter) ((WrapperListAdapter) adapter)
				.getWrappedAdapter())
				.addUnexpectedReservation(event.reservation);
	}

	@Subscribe
	public void enterDriveStatus(EnterDriveStatusEvent event) {
		List<OperationSchedule> operationSchedules = logic
				.getRemainingOperationSchedules();
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
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			logic.enterFinishStatus();
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		if (!operationSchedule.getPlatform().isPresent()) {
			return;
		}
		DateFormat dateFormat = new SimpleDateFormat("H時m分"); // TODO
		final Platform platform = operationSchedule.getPlatform().get();
		platformNameTextView.setText(platform.getName());
		platformDepartureTimeTextView.setText(dateFormat
				.format(operationSchedule.getDepartureEstimate()));

		final ReservationArrayAdapter adapter = new ReservationArrayAdapter(
				this, R.layout.reservation_list_row, logic);
		reservationListView.setAdapter(adapter);

		statusTextView.setText("停車中");
		if (operationSchedules.size() > 1) {
			changeStatusButton.setText("出発する");
		} else {
			changeStatusButton.setText("確定する");
		}

		showAllRidingReservationsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							adapter.showRidingAndNotGetOutReservations();
						} else {
							adapter.hideRidingAndNotGetOutReservations();
						}
					}
				});
		showFutureReservationsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							adapter.showFutureReservations();
						} else {
							adapter.hideFutureReservations();
						}
					}
				});
		showMissedReservationsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							adapter.showMissedReservations();
						} else {
							adapter.hideMissedReservations();
						}
					}
				});

		addUnexpectedReservationButton
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (!isFinishing()) {
							showDialog(ADD_UNEXPECTED_RESERVATION_DIALOG_ID);
						}
					}
				});

		waitingLayout.setVisibility(View.VISIBLE);
		drivingLayout.setVisibility(View.GONE);
		finishLayout.setVisibility(View.GONE);
		changeStatusButton.setEnabled(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (!isFinishing()) {
			showDialog(WAIT_FOR_INITIALIZE_DIALOG_ID);
		}

		super.onCreate(savedInstanceState);

		// if (BuildConfig.DEBUG) {
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectAll().penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectAll().penaltyLog().penaltyDeath().build());
		// }
		setContentView(R.layout.in_vehicle_device);

		TypedArray typedArray = obtainStyledAttributes(new int[] { android.R.attr.background });
		int backgroundColor = typedArray.getColor(0, Color.WHITE);

		contentView = findViewById(android.R.id.content);
		contentView.setVisibility(View.GONE); // InVehicleDeviceLogicの準備が終わるまでcontentViewを非表示
		contentView.setBackgroundColor(backgroundColor);
		getWindow().getDecorView().setBackgroundColor(Color.BLACK); // ProgressDialogと親和性の高い色にする

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
		navigationModal = (NavigationModal) findViewById(R.id.navigation_modal);
		drivingView1Layout = findViewById(R.id.driving_view1);
		drivingView2Layout = findViewById(R.id.driving_view2);
		drivingView1Layout.setBackgroundColor(backgroundColor); // TODO XMLで指定
		drivingView2Layout.setBackgroundColor(backgroundColor); // TODO
		waitingLayout.setBackgroundColor(backgroundColor); // TODO
		reservationListView = (ListView) findViewById(R.id.reservation_list_view);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		reservationListFooterView = layoutInflater.inflate(
				R.layout.reservation_list_footer, null);
		reservationListView.addFooterView(reservationListFooterView);

		showAllRidingReservationsButton = (ToggleButton) reservationListFooterView
				.findViewById(R.id.show_all_riding_reservations_button);
		showFutureReservationsButton = (ToggleButton) reservationListFooterView
				.findViewById(R.id.show_future_reservations_button);
		showMissedReservationsButton = (ToggleButton) reservationListFooterView
				.findViewById(R.id.show_missed_reservations_button);
		addUnexpectedReservationButton = (Button) findViewById(R.id.add_unexpected_reservation_button);

		changeStatusButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				logic.enterNextStatus();
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
		reservationScrollUpButton = (Button) findViewById(R.id.reservation_scroll_up_button);
		reservationScrollUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = reservationListView
						.getFirstVisiblePosition();
				reservationListView.smoothScrollToPosition(position);
			}
		});
		reservationScrollDownButton = (Button) findViewById(R.id.reservation_scroll_down_button);
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
		case WAIT_FOR_INITIALIZE_DIALOG_ID: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage("運行情報を取得しています");
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
		}
		case ADD_UNEXPECTED_RESERVATION_DIALOG_ID: {
			final List<OperationSchedule> operationSchedules = logic
					.getRemainingOperationSchedules();
			if (operationSchedules.size() <= 1) {
				Log.w(TAG,
						"can't add unexpected reservation, !(operationSchedulfes.isEmpty())",
						new Exception());
				return null;
			}
			operationSchedules.remove(operationSchedules.get(0));
			final String[] operationScheduleSelections = new String[operationSchedules
					.size()];
			for (Integer i = 0; i < operationScheduleSelections.length; ++i) {
				OperationSchedule operationSchedule = operationSchedules.get(i);
				String selection = "";
				if (operationSchedule.getPlatform().isPresent()) {
					selection += operationSchedule.getPlatform().get()
							.getName();
				} else {
					// TODO
					selection += "Operation Schedule ID: "
							+ operationSchedules.get(i).getId();
				}
				operationScheduleSelections[i] = selection;
			}
			return new AlertDialog.Builder(this).setItems(
					operationScheduleSelections,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (which >= operationSchedules.size()) {
								// TODO warning
								return;
							}
							OperationSchedule arrivalOperationSchedule = operationSchedules
									.get(which);
							logic.addUnexpectedReservation(arrivalOperationSchedule
									.getId());
						}
					}).create();
		}
		default:
			return null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		handler.removeCallbacks(toggleDrivingView);
		handler.removeCallbacks(pollVehicleNotification);
		handler.removeCallbacks(updateTime);

		voiceThread.interrupt();
		logic.shutdown();
		logicLoadThread.interrupt();

		navigationModal.onPauseActivity();

		super.onPause();

		contentView.setVisibility(View.GONE); // InVehicleDeviceLogicの準備が終わるまでcontentViewを非表示
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
		navigationModal.onResumeActivity();

		handler.post(toggleDrivingView);
		handler.post(pollVehicleNotification);
		handler.post(updateTime);

		voiceThread.interrupt();
		voiceThread = new VoiceThread(this, voices);
		voiceThread.start();

		logicLoadThread.interrupt();
		logicLoadThread = new InVehicleDeviceLogic.LoadThread(this);
		logicLoadThread.start();

		logic.shutdown();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Subscribe
	public void startUi(InVehicleDeviceLogic.LoadThread.CompleteEvent event) {
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
	}
}
