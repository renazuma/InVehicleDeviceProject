package com.kogasoftware.odt.invehicledevice;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;

import jp.tomorrowkey.android.vtextviewer.VTextView;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.maps.MapActivity;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.modal.Modal;
import com.kogasoftware.odt.invehicledevice.navigation.NavigationView;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

class ReservationArrayAdapter extends ArrayAdapter<Reservation> {
	private final LayoutInflater layoutInflater;
	private final int resourceId;

	public ReservationArrayAdapter(Context context, int resourceId,
			List<Reservation> items) {
		super(context, resourceId, items);
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resourceId = resourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(resourceId, null);
		}
		Spinner spinner = (Spinner) convertView
				.findViewById(R.id.change_head_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.simple_spinner_item, new String[] { "1名",
						"2名", "3名" });
		spinner.setAdapter(adapter);

		Reservation reservation = getItem(position);
		TextView userNameView = (TextView) convertView
				.findViewById(R.id.user_name);
		
		if (reservation.getUser().isPresent()) {
			User user = reservation.getUser().get();
			userNameView.setText(user.getFamilyName() + " " + user.getLastName() + " 様");
		} else {
			userNameView.setText("ID:" + reservation.getUserId() + " 様");
		}
		
		TextView reservationIdView = (TextView) convertView
				.findViewById(R.id.reservation_id);
		reservationIdView.setText("[乗] 予約番号 " + reservation.getId());
		Button memoButton = (Button) convertView.findViewById(R.id.memo_button);
		memoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				view.getRootView().findViewById(R.id.memo_overlay)
						.setVisibility(View.VISIBLE);
			}
		});
		Button returnPathButton = (Button) convertView
				.findViewById(R.id.return_path_button);
		returnPathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				view.getRootView().findViewById(R.id.return_path_overlay)
						.setVisibility(View.VISIBLE);
			}
		});

		return convertView;
	}
}

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
	private Integer status = 0;

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
				status = 0;
				statusTextView.setText("走行中");
				changeStatusButton.setText("到着");
				findViewById(R.id.check_start_layout).setVisibility(View.GONE);
				findViewById(R.id.waiting_layout).setVisibility(View.GONE);
				if (!voices.offer("出発します。次は、コガソフトウェア前。コガソフトウェア前。")) {
					Log.w(TAG, "!voices.offer() failed");
				}
			}
		});

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
			r4.setUser(new User(new JSONObject("{family_name: '越前', last_name: '康介'}")));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		l.add(r4);

		Reservation r5 = new Reservation();
		r5.setId(13L);
		try {
			r5.setUser(new User(new JSONObject("{family_name: '荒木', last_name: '飛呂彦'}")));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		l.add(r5);

		ReservationArrayAdapter usersAdapter = new ReservationArrayAdapter(
				this, R.layout.reservation_list_row, l);
		usersListView = (ListView) findViewById(R.id.users_list_view);
		usersListView.setAdapter(usersAdapter);

		View test = findViewById(R.id.status_text_view);
		test.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				findViewById(R.id.vehicle_notification_overlay).setVisibility(
						View.VISIBLE);
			}
		});
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode != KeyEvent.KEYCODE_BACK) {
			return super.onKeyDown(keyCode, event);
		}
		Boolean match = false;
		for (WeakReference<Modal> r : Modal.getAttachedInstances()) {
			Modal l = r.get();
			if (l == null) {
				continue;
			}
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
