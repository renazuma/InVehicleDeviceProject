package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.ui.fragment.InVehicleDeviceFragment.State;

public class InVehicleDeviceFragment extends ApplicationFragment<State>
		implements EventDispatcher.OnUpdatePhaseListener {

	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final Phase phase;
		private final List<OperationSchedule> operationSchedules;
		private final List<PassengerRecord> passengerRecords;

		public State(Phase phase, List<OperationSchedule> operationSchedules,
				List<PassengerRecord> passengerRecords) {
			this.phase = phase;
			this.operationSchedules = Lists.newArrayList(operationSchedules);
			this.passengerRecords = Lists.newArrayList(passengerRecords);
		}

		public Phase getPhase() {
			return phase;
		}

		public List<OperationSchedule> getOperationSchedules() {
			return operationSchedules;
		}

		public List<PassengerRecord> getPassengerRecords() {
			return passengerRecords;
		}
	}

	public static Fragment newInstance(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		return newInstance(new InVehicleDeviceFragment(), new State(phase,
				operationSchedules, passengerRecords));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Optional<OperationSchedule> currentOperationSchedule = OperationSchedule
				.getCurrent(getState().getOperationSchedules());
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(R.id.information_fragment_container,
				InformationBarFragment.newInstance(getState().getPhase(),
						currentOperationSchedule));
		fragmentTransaction.add(R.id.control_fragment_container,
				ControlBarFragment.newInstance(getState().getPhase(),
						getState().getOperationSchedules(), getState()
								.getPassengerRecords()));
		fragmentTransaction.commitAllowingStateLoss();
		getService().getEventDispatcher().addOnUpdatePhaseListener(this);
		updateView(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.in_vehicle_device_fragment, container,
				false);
	}

	private static String getPhaseFragmentTag(Phase phase,
			Optional<OperationSchedule> optionalOperationSchedule) {
		StringBuilder tag = new StringBuilder("tag:" + phase);
		for (OperationSchedule operationSchedule : optionalOperationSchedule
				.asSet()) {
			tag.append(":" + operationSchedule.getId());
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				tag.append(":" + platform.getId());
			}
		}
		return tag.toString();
	}

	public void updateView(Boolean first) {
		Optional<OperationSchedule> currentOperationSchedule = OperationSchedule
				.getCurrent(getState().getOperationSchedules());
		String tag = getPhaseFragmentTag(getState().getPhase(),
				currentOperationSchedule);
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		Boolean speakPlatform = first;
		if (fragmentManager.findFragmentByTag(tag) == null && !first) {
			fragmentTransaction.setCustomAnimations(R.anim.show_phase_fragment,
					R.anim.hide_phase_fragment, R.anim.show_phase_fragment,
					R.anim.hide_phase_fragment);
			speakPlatform = true;
		}
		if (getState().getPhase() != Phase.DRIVE) {
			speakPlatform = false;
		}
		if (speakPlatform) {
			for (OperationSchedule operationSchedule : currentOperationSchedule
					.asSet()) {
				for (Platform platform : operationSchedule.getPlatform()
						.asSet()) {
					String message = "出発します。次は、" + platform.getNameRuby() + "。"
							+ platform.getNameRuby();
					getService().speak(message);
				}
			}
		}

		switch (getState().getPhase()) {
		case DRIVE:
			fragmentTransaction.replace(R.id.phase_fragment_container,
					DrivePhaseFragment.newInstance(getState()
							.getOperationSchedules()), tag);
			break;
		case FINISH:
			fragmentTransaction.replace(R.id.phase_fragment_container,
					FinishPhaseFragment.newInstance(), tag);
			break;
		case PLATFORM_GET_OFF:
		case PLATFORM_GET_ON:
			// PLATFORM_GET_OFFかPLATFORM_GET_ONのフラグメントが既に存在する場合は交換しない
			if (fragmentManager.findFragmentByTag(getPhaseFragmentTag(
					Phase.PLATFORM_GET_OFF, currentOperationSchedule)) != null
					|| fragmentManager.findFragmentByTag(getPhaseFragmentTag(
							Phase.PLATFORM_GET_ON, currentOperationSchedule)) != null) {
				break;
			}
			fragmentTransaction.replace(R.id.phase_fragment_container,
					PlatformPhaseFragment.newInstance(getState().getPhase(),
							getState().getOperationSchedules(), getState()
									.getPassengerRecords()), tag);
			break;
		}
		fragmentTransaction.commitAllowingStateLoss();
	}

	@Override
	public void onUpdatePhase(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		setState(new State(phase, operationSchedules, passengerRecords));
		if (isRemoving()) {
			return;
		}
		updateView(false);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getService().getEventDispatcher().removeOnUpdatePhaseListener(this);
	}
}
