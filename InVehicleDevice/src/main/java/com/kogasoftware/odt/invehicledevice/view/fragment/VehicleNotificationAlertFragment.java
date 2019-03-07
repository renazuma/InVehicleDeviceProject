package com.kogasoftware.odt.invehicledevice.view.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;

/**
 * 通知受信時のびっくりマーク
 */
public class VehicleNotificationAlertFragment extends Fragment {
	private static final Integer ALERT_SHOW_INTERVAL_MILLIS = 500;

	// TODO: Activityは一つしかないので、InVehicleDeviceActivityの指定は不要では？
	private static final String FRAGMENT_TAG = InVehicleDeviceActivity.class + "/" + VehicleNotificationAlertFragment.class;

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
				getView().findViewById(R.id.alert_image_view).setVisibility(count % 2 == 0 ? View.VISIBLE : View.GONE);
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.vehicle_notification_alert_fragment, container, false);
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

	// TODO: 既存に合わせるためにstaticにしている。出来れば変えたい。
	public static void showModal(InVehicleDeviceActivity inVehicleDeviceActivity) {
		if (inVehicleDeviceActivity.destroyed || inVehicleDeviceActivity.serviceProvider == null) { return; }

		FragmentManager fragmentManager = inVehicleDeviceActivity.getFragmentManager();

		if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) != null) { return; }

		// TODO: 画面上に表示するだけなので、Modalではないのでは？
		Fragments.showModalFragment(fragmentManager, VehicleNotificationAlertFragment.newInstance(), FRAGMENT_TAG);
	}
}
