package com.kogasoftware.odt.invehicledevice.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.R;

/**
 * 通知受信時のびっくりマーク
 */
public class VehicleNotificationAlertFragment extends Fragment {
	private static final Integer ALERT_SHOW_INTERVAL_MILLIS = 500;
	private final Handler handler = new Handler();
	private Integer count;
	private final Runnable blinkAlertTask = new Runnable() {
		@Override
		public void run() {
			if (!isAdded()) {
				return;
			}
			if (count <= 10) { // TODO 定数
				count++;
				getView().findViewById(R.id.alert_image_view).setVisibility(
						count % 2 == 0 ? View.VISIBLE : View.GONE);
				handler.postDelayed(this, ALERT_SHOW_INTERVAL_MILLIS);
				return;
			}
			getFragmentManager().beginTransaction()
					.remove(VehicleNotificationAlertFragment.this)
					.commitAllowingStateLoss();
		}
	};

	public static VehicleNotificationAlertFragment newInstance() {
		VehicleNotificationAlertFragment fragment = new VehicleNotificationAlertFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.vehicle_notification_alert_fragment,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		count = 0;
		handler.post(blinkAlertTask);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		handler.removeCallbacks(blinkAlertTask);
	}
}
