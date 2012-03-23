package com.kogasoftware.odt.invehicledevice;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jp.tomorrowkey.android.vtextviewer.VTextView;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.modal.ScheduleChangedModal;
import com.kogasoftware.odt.invehicledevice.navigation.NavigationView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class InVehicleDeviceActivity extends MapActivity {
	private final String TAG = InVehicleDeviceActivity.class.getSimpleName();
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

	private enum Status {
		DRIVE, PLATFORM,
	};

	private Status status = Status.DRIVE;

	private Button changeStatusButton = null;
	private Button scheduleButton = null;
	private Button mapButton = null;
	private Button configButton = null;
	private Button stopCheckButton = null;
	private Button stopButton = null;
	private Button startButton = null;
	private Button pauseButton = null;
	private TextView statusTextView = null;
	private View drivingView1Layout = null;
	private View drivingView2Layout = null;
	private NavigationView navigationView = null;
	private ListView usersListView = null;

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
		stopCheckButton = (Button) findViewById(R.id.stop_check_button);
		stopButton = (Button) findViewById(R.id.stop_button);
		pauseButton = (Button) findViewById(R.id.pause_button);
		scheduleButton = (Button) findViewById(R.id.schedule_button);

		drivingView1Layout = findViewById(R.id.driving_view1);
		drivingView2Layout = findViewById(R.id.driving_view2);

		((VTextView) findViewById(R.id.next_stop_text_view))
		.setText("次の乗降場てすとて");
		((VTextView) findViewById(R.id.next_stop_but_one_text_view))
		.setText("次の次の乗降場てすと");
		((VTextView) findViewById(R.id.next_stop_but_two_text_view))
		.setText("次の次の次の乗降場てす");

		drivingViewToggleHandler.post(drivingViewToggleRunnable);
		navigationView = (NavigationView) findViewById(R.id.navigation_view);

		changeStatusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (status == Status.DRIVE) {
					enterPlatformStatus();
				} else {
					findViewById(R.id.check_start_layout).setVisibility(
							View.VISIBLE);
				}
			}
		});

		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.map_overlay).setVisibility(View.VISIBLE);
			}
		});

		configButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.config_overlay).setVisibility(View.VISIBLE);
			}
		});

		stopCheckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.stop_check_overlay).setVisibility(
						View.VISIBLE);
			}
		});

		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.stop_overlay).setVisibility(View.VISIBLE);
			}
		});

		pauseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.pause_overlay).setVisibility(View.VISIBLE);
			}
		});

		scheduleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.schedule_layout).setVisibility(View.VISIBLE);
			}
		});

		startButton = (Button) findViewById(R.id.start_button);
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				enterDriveStatus();
			}
		});

		usersListView = (ListView) findViewById(R.id.users_list_view);

		createTestData();

		View test = findViewById(R.id.status_text_view);
		test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.vehicle_notification_overlay).setVisibility(
						View.VISIBLE);
			}
		});

		View test2 = findViewById(R.id.icon_text_view);
		test2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new ScheduleChangedModal(InVehicleDeviceActivity.this)
				.open(getWindow().getDecorView());
			}
		});
	}

	@Deprecated
	private void createTestData() {
		try {
			List<OperationSchedule> l = new LinkedList<OperationSchedule>();
			JSONObject j1 = new JSONObject(
					"{"
							+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
							+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
							+ "platform: {name: 'コガソフトウェア前'}, "
							+ "reservations_as_arrival: [{head: 5}, {head: 6}, {head: 7}] ,"
							+ "reservations_as_departure: [{head: 15}, {head: 16}, {head: 17}]}");
			l.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T03:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T04:00:00.000+09:00', "
					+ "platform: {name: '上野御徒町駅前'}, "
					+ "reservations_as_arrival: [{head: 5}]}");
			l.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject(
					"{"
							+ "arrival_estimate: '2012-01-01T05:00:00.000+09:00', "
							+ "departure_estimate: '2012-01-01T06:00:00.000+09:00', "
							+ "platform: {name: '上野動物園前'}, "
							+ "reservations_as_departure: [{head: 5}, {head: 6}, {head: 7}]}");
			l.add(new OperationSchedule(j3));

			JSONObject j4 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T07:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T08:00:00.000+09:00', "
					+ "platform: {name: '上野広小路前'}, "
					+ "reservations_as_arrival: [] ,"
					+ "reservations_as_departure: [{head: 7}]}");
			l.add(new OperationSchedule(j4));

			JSONObject j5 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T09:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T09:01:00.000+09:00', "
					+ "platform: {name: '湯島天神前'}}");
			l.add(new OperationSchedule(j5));

			JSONObject j6 = new JSONObject(
					"{"
							+ "arrival_estimate: '2012-01-01T09:03:00.000+09:00', "
							+ "departure_estimate: '2012-01-01T09:03:30.000+09:00', "
							+ "platform: {name: 'コガソフトウェア前'}, "
							+ "reservations_as_arrival: [{head: 50}, {head: 60}, {head: 70}] ,"
							+ "reservations_as_departure: [{head: 150}, {head: 160}, {head: 170}]}");
			l.add(new OperationSchedule(j6));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Reservation> l = new LinkedList<Reservation>();
		Reservation r1 = new Reservation();
		User u1 = new User();
		u1.setFamilyName("桜木");
		u1.setLastName("花道");
		r1.setId(10L);
		r1.setUser(u1);
		l.add(r1);

		User u2 = new User();
		u2.setFamilyName("流川");
		u2.setLastName("楓");
		Reservation r2 = new Reservation();
		r2.setId(11L);
		r2.setUser(u2);
		r2.setMemo("メモが存在します");
		l.add(r2);

		User u3 = new User();
		u3.setFamilyName("フリークス");
		u3.setLastName("ゴン");
		Reservation r3 = new Reservation();
		r3.setId(12L);
		r3.setUser(u3);
		l.add(r3);

		Reservation r4 = new Reservation();
		r4.setId(13L);
		try {
			r4.setUser(new User(new JSONObject(
					"{family_name: '越前', last_name: '康介'}")));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		l.add(r4);

		try {
			Reservation r5 = new Reservation(
					new JSONObject(
							"{id: 13, memo: 'メモメモ', user: {family_name: '荒木', last_name: '飛呂彦'}}"));
			l.add(r5);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		ReservationArrayAdapter usersAdapter = new ReservationArrayAdapter(
				this, R.layout.reservation_list_row, l);
		usersListView.setAdapter(usersAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		navigationView.onResumeActivity();
	}

	@Override
	public void onPause() {
		super.onPause();
		navigationView.onPauseActivity();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		drivingViewToggleHandler.removeCallbacks(drivingViewToggleRunnable);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void enterPlatformStatus() {
		status = Status.PLATFORM;
		findViewById(R.id.waiting_layout).setVisibility(View.VISIBLE);
		statusTextView.setText("停車中");
		changeStatusButton.setText("出発");
	}

	private void enterDriveStatus() {
		status = Status.DRIVE;
		statusTextView.setText("走行中");
		changeStatusButton.setText("到着しました");
		findViewById(R.id.check_start_layout).setVisibility(View.GONE);
		findViewById(R.id.waiting_layout).setVisibility(View.GONE);
		if (!voices.offer("出発します。次は、コガソフトウェア前。コガソフトウェア前。")) {
			Log.w(TAG, "!voices.offer() failed");
		}
	}
}
