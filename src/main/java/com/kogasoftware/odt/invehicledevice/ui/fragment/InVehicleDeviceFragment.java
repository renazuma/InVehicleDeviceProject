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
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation.Phase;
import com.kogasoftware.odt.invehicledevice.ui.fragment.InVehicleDeviceFragment.State;

public class InVehicleDeviceFragment extends AutoUpdateOperationFragment<State> {

	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final Operation operation;

		public State(Operation operation) {
			this.operation = operation;
		}

		public Phase getPhase() {
			return operation.getPhase();
		}

		public List<OperationSchedule> getOperationSchedules() {
			return operation.operationSchedules;
		}

		public List<PassengerRecord> getPassengerRecords() {
			return operation.passengerRecords;
		}

		public Operation getOperation() {
			return operation;
		}
	}

	public static Fragment newInstance(Operation operation) {
		return newInstance(new InVehicleDeviceFragment(), new State(operation));
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			fragmentTransaction.add(R.id.information_fragment_container,
					InformationBarFragment.newInstance(getState().getOperation()));
			fragmentTransaction.add(R.id.control_fragment_container,
					ControlBarFragment.newInstance(getState().getOperation()));
			fragmentTransaction.commitAllowingStateLoss();
			updateView(true);
		}
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
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			updateView(first, fragmentManager);
		}
	}

	public void updateView(Boolean first, FragmentManager fragmentManager) {
		Optional<OperationSchedule> currentOperationSchedule = OperationSchedule
				.getCurrent(getState().getOperationSchedules());
		String tag = getPhaseFragmentTag(getState().getPhase(),
				currentOperationSchedule);
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
					PlatformPhaseFragment.newInstance(getState().getOperation()), tag);
			break;
		}
		fragmentTransaction.commitAllowingStateLoss();
	}

	@Override
	public void onUpdateOperation(Operation operation) {
		setState(new State(operation));
		if (isRemoving()) {
			return;
		}
		updateView(false);
	}

	@Override
	protected Integer getOperationSchedulesReceiveSequence() {
		return getState().getOperation().operationScheduleReceiveSequence;
	}
}
