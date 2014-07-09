package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.util.LinkedList;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.PassengerRecord;

public class OrderedOperationFragment
		extends
			OperationSchedulesAndPassengerRecordsFragment {
	public static OrderedOperationFragment newInstance() {
		OrderedOperationFragment fragment = new OrderedOperationFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.ordered_operation_fragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction.add(R.id.control_fragment_container,
				ControlBarFragment.newInstance());
		fragmentTransaction.add(R.id.information_fragment_container,
				InformationBarFragment.newInstance());
		fragmentTransaction.commitAllowingStateLoss();
	}

	@Override
	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			Phase phase, LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		switch (phase) {
			case FINISH :
				fragmentTransaction.replace(R.id.phase_fragment_container,
						FinishPhaseFragment.newInstance());
				break;
			case DRIVE :
				fragmentTransaction.replace(R.id.phase_fragment_container,
						DrivePhaseFragment.newInstance(operationSchedules));
				break;
			case PLATFORM_GET_ON :
				fragmentTransaction.replace(R.id.phase_fragment_container,
						PlatformPhaseFragment.newInstance(operationSchedules));
				break;
			case PLATFORM_GET_OFF :
				fragmentTransaction.replace(R.id.phase_fragment_container,
						PlatformPhaseFragment.newInstance(operationSchedules));
				break;
		}
		fragmentTransaction.commitAllowingStateLoss();
	}
}