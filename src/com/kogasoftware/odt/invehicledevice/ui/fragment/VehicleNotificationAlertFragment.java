package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.ui.fragment.VehicleNotificationAlertFragment.State;

public class VehicleNotificationAlertFragment extends
		ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final List<VehicleNotification> vehicleNotifications;

		public State(List<VehicleNotification> vehicleNotifications) {
			this.vehicleNotifications = Lists
					.newArrayList(vehicleNotifications);
		}

		public List<VehicleNotification> getVehicleNotifications() {
			return Lists.newArrayList(vehicleNotifications);
		}
	}

	private static final Integer ALERT_SHOW_INTERVAL_MILLIS = 500;
	private final Handler handler = new Handler();
	private Integer count = 0;
	private final Runnable blinkAlertAndShowNextFragment = new Runnable() {
		@Override
		public void run() {
			if (isRemoving()) {
				return;
			}
			if (count <= 10) { // TODO 定数
				count++;
				getView().findViewById(R.id.alert_image_view).setVisibility(
						count % 2 == 0 ? View.VISIBLE : View.GONE);
				handler.postDelayed(this, ALERT_SHOW_INTERVAL_MILLIS);
				return;
			}
			FragmentTransaction fragmentTransaction = setCustomAnimation(getFragmentManager()
					.beginTransaction());
			for (VehicleNotification vehicleNotification : getState()
					.getVehicleNotifications()) {
				fragmentTransaction.add(R.id.modal_fragment_container,
						VehicleNotificationFragment
								.newInstance(vehicleNotification));
			}
			fragmentTransaction.remove(VehicleNotificationAlertFragment.this);
			fragmentTransaction.commitAllowingStateLoss();
		}
	};

	public static VehicleNotificationAlertFragment newInstance(
			List<VehicleNotification> vehicleNotifications) {
		return newInstance(new VehicleNotificationAlertFragment(), new State(
				vehicleNotifications));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler.post(blinkAlertAndShowNextFragment);
		getService().speak("管理者から連絡があります");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.vehicle_notification_alert_fragment,
				container, false);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		handler.removeCallbacks(blinkAlertAndShowNextFragment);
	}
}
