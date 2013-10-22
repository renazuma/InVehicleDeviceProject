package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.VehicleNotificationLogic;
import com.kogasoftware.odt.invehicledevice.ui.fragment.OperationScheduleChangedAlertFragment.State;

public class OperationScheduleChangedAlertFragment extends
		ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
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
						count % 2 == 0 ? View.VISIBLE : View.INVISIBLE);
				handler.postDelayed(this, ALERT_SHOW_INTERVAL_MILLIS);
				return;
			}
			showOperationScheduleChangedFragment();
		}
	};

	public static OperationScheduleChangedAlertFragment newInstance(
			List<VehicleNotification> vehicleNotifications) {
		return newInstance(new OperationScheduleChangedAlertFragment(),
				new State());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler.post(blinkAlertAndShowNextFragment);
		getService().speak("運行予定が変更されました");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(
				R.layout.operation_schedule_changed_alert_fragment, container,
				false);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		handler.removeCallbacks(blinkAlertAndShowNextFragment);
	}

	private void showOperationScheduleChangedFragment() {
		getService().getLocalStorage().read(
				new BackgroundReader<ArrayList<VehicleNotification>>() {
					@Override
					public ArrayList<VehicleNotification> readInBackground(
							LocalData localData) {
						return Lists.newArrayList(new VehicleNotificationLogic(
								getService())
								.getWithReadLock(
										VehicleNotification.NotificationKind.RESERVATION_CHANGED,
										VehicleNotificationStatus.OPERATION_SCHEDULE_RECEIVED));
					}

					@Override
					public void onRead(
							ArrayList<VehicleNotification> vehicleNotifications) {
						showOperationScheduleChangedFragment(vehicleNotifications);
					}
				});
	}

	private void showOperationScheduleChangedFragment(
			ArrayList<VehicleNotification> vehicleNotifications) {
		String tag = "tag:" + OperationScheduleChangedFragment.class.getName();
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			Fragment old = fragmentManager.findFragmentByTag(tag);
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			if (old == null) {
				setCustomAnimation(fragmentTransaction);
			} else {
				fragmentTransaction.remove(old);
			}
			fragmentTransaction.add(R.id.modal_fragment_container,
					OperationScheduleChangedFragment
							.newInstance(vehicleNotifications), tag);
			fragmentTransaction.commitAllowingStateLoss();
			hide();
		}
	}
}