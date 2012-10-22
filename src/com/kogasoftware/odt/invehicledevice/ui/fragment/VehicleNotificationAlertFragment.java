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
import com.kogasoftware.odt.invehicledevice.ui.fragment.VehicleNotificationAlertFragment.State;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

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
	private Integer count = 0;

	public static VehicleNotificationAlertFragment newInstance(
			List<VehicleNotification> vehicleNotifications) {
		return newInstance(new VehicleNotificationAlertFragment(), new State(
				vehicleNotifications));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final Handler handler = new Handler();
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (count > 10) { // TODO 定数
					count = 0;
					FragmentTransaction fragmentTransaction = setCustomAnimation(getFragmentManager()
							.beginTransaction());
					for (VehicleNotification vehicleNotification : getState()
							.getVehicleNotifications()) {
						fragmentTransaction.add(R.id.modal_fragment_container,
								VehicleNotificationFragment
										.newInstance(vehicleNotification));
					}
					fragmentTransaction
							.remove(VehicleNotificationAlertFragment.this);
					fragmentTransaction.commitAllowingStateLoss();
					return;
				}
				count++;
				getView().findViewById(R.id.alert_image_view).setVisibility(
						count % 2 == 0 ? View.VISIBLE : View.GONE);
				handler.postDelayed(this, ALERT_SHOW_INTERVAL_MILLIS);
			}
		});
		getService().speak("管理者から連絡があります");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.vehicle_notification_alert_fragment,
				container, false);
	}
}
