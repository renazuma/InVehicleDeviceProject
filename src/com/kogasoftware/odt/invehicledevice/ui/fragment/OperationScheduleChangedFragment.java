package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.VehicleNotificationLogic;
import com.kogasoftware.odt.invehicledevice.ui.fragment.OperationScheduleChangedFragment.State;

public class OperationScheduleChangedFragment extends
		ApplicationFragment<State> implements
		EventDispatcher.OnMergeOperationSchedulesListener {
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

	public static OperationScheduleChangedFragment newInstance(
			List<VehicleNotification> vehicleNotifications) {
		return newInstance(new OperationScheduleChangedFragment(), new State(
				vehicleNotifications));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.operation_schedule_changed_fragment,
				container, false);
	}

	private void updateView() {
		TextView textView = (TextView) getView().findViewById(
				R.id.schedule_changed_text_view);

		StringBuilder message = new StringBuilder();
		for (VehicleNotification vehicleNotification : getState()
				.getVehicleNotifications()) {
			for (String bodyRuby : vehicleNotification.getBodyRuby().asSet()) {
				getService().speak(bodyRuby);
			}
			message.append(vehicleNotification.getBody());
			message.append(SystemUtils.LINE_SEPARATOR);
		}

		textView.setText(message);

		// 表示したスケジュール変更通知にresponseを指定
		for (VehicleNotification vehicleNotification : getState()
				.getVehicleNotifications()) {
			vehicleNotification.setResponse(VehicleNotification.Response.YES); // TODO
		}

		new VehicleNotificationLogic(getService())
				.replyUpdatedOperationScheduleVehicleNotifications(getState()
						.getVehicleNotifications());
	}

	public static void addIfNecessary(FragmentManager fragmentManager,
			FragmentTransaction fragmentTransaction, int fragmentContainer,
			List<VehicleNotification> vehicleNotifications) {
		String tag = OperationScheduleChangedFragment.class.getName();
		if (fragmentManager.findFragmentByTag(tag) == null) {
			fragmentTransaction.add(fragmentContainer,
					OperationScheduleChangedFragment
							.newInstance(vehicleNotifications));
		}
	}

	private void showOperationScheduleFragment() {
		getService().getLocalStorage().read(
				new BackgroundReader<ArrayList<OperationSchedule>>() {
					@Override
					public ArrayList<OperationSchedule> readInBackground(
							LocalData localData) {
						return Lists.newArrayList(localData.operationSchedules);
					}

					@Override
					public void onRead(
							ArrayList<OperationSchedule> operationSchedules) {
						if (isRemoving()) {
							return;
						}
						setCustomAnimation(
								getFragmentManager().beginTransaction())
								.remove(OperationScheduleChangedFragment.this)
								.add(R.id.modal_fragment_container,
										OperationScheduleListFragment
												.newInstance(operationSchedules))
								.commitAllowingStateLoss();
					}
				});
	}

	@Override
	public void onMergeOperationSchedules(
			List<VehicleNotification> triggerVehicleNotifications) {
		List<VehicleNotification> vehicleNotifications = Lists.newLinkedList();
		vehicleNotifications.addAll(getState().getVehicleNotifications());
		vehicleNotifications.addAll(triggerVehicleNotifications);
		setState(new State(vehicleNotifications));
		updateView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		final OperationScheduleLogic operationScheduleLogic = new OperationScheduleLogic(
				getService());
		Button scheduleConfirmButton = (Button) getView().findViewById(
				R.id.schedule_confirm_button);
		scheduleConfirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showOperationScheduleFragment();
				operationScheduleLogic.requestUpdatePhase();
			}
		});
		Button hideButton = (Button) getView().findViewById(
				R.id.schedule_changed_close_button);
		hideButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hide();
				operationScheduleLogic.requestUpdatePhase();
			}
		});
		updateView();

		getService().getEventDispatcher().addOnMergeOperationSchedulesListener(
				this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getService().getEventDispatcher()
				.removeOnMergeOperationSchedulesListener(this);
	}
}
