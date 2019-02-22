package com.kogasoftware.odt.invehicledevice.view.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;

import java.util.LinkedList;

/**
 * 順番に運行を進める画面。上部に「InformationBarFragment」右部に「ControlBarFragment」中心に「**PhaseFragment」を配置する
 */
public class OrderedOperationFragment extends OperationSchedulesAndPassengerRecordsFragment {

	private static final String LOGGING_TAG = OrderedOperationFragment.class.getSimpleName();

	// TODO: Activityは一つしかないので、InVehicleDeviceActivityの指定は不要では？
	private static final String FRAGMENT_TAG = InVehicleDeviceActivity.class + "/" + OrderedOperationFragment.class;

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
			LinkedList<PassengerRecord> passengerRecords, Boolean phaseChanged) {
		Log.i(LOGGING_TAG, "phase=" + phase + " phaseChanged=" + phaseChanged);
		if (!phaseChanged) {
			return;
		}
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

	// TODO: 共通処理のshowModalFragmentと、customAnimation以外は変わらない。共通処理を使っていないのはそこが理由なのかを確認。
	// TODO: 既存に合わせるためにstaticにしている。出来れば変えたい。
	public static void showModal(FragmentManager fragmentManager) {
		if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) != null) { return; }

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.modal_fragment_container,
						OrderedOperationFragment.newInstance(),
						FRAGMENT_TAG);
		fragmentTransaction.commitAllowingStateLoss();
	}

	// TODO: 共通処理のhideは使えない？確認する。
	// TODO: 既存に合わせるためにstaticにしている。出来れば変えたい。
	public static void hideModal(FragmentManager fragmentManager) {
		if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) { return; }

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.remove(fragmentManager.findFragmentByTag(FRAGMENT_TAG));
		fragmentTransaction.commitAllowingStateLoss();
	}
}
