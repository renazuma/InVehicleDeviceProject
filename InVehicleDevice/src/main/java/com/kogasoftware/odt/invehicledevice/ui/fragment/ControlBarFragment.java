package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.util.LinkedList;

import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;
import com.kogasoftware.odt.invehicledevice.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.utils.ViewDisabler;

public class ControlBarFragment
		extends
			OperationSchedulesAndPassengerRecordsFragment {
	private static final String TAG = ControlBarFragment.class.getSimpleName();
	public static final String OPERATION_LIST_FRAGMENT_TAG = ControlBarFragment.class
			+ "/" + OperationSchedulesAndPassengerRecordsFragment.class;
	private ContentResolver contentResolver;
	private Button mapButton;

	public static ControlBarFragment newInstance() {
		ControlBarFragment fragment = new ControlBarFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.control_bar_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		contentResolver = getActivity().getContentResolver();
		mapButton = (Button) getView().findViewById(R.id.map_button);
		Button operationScheduleListButton = (Button) getView().findViewById(
				R.id.operation_schedule_list_button);
		operationScheduleListButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewDisabler.disable(v);
				showOperationScheduleListFragment();
			}
		});
	}

	public void showNavigation(Phase phase,
			LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
		if (!isAdded()) {
			return;
		}
		// 停車中の場合、次の運行を選択
		Boolean driving = phase.equals(Phase.DRIVE);
		OperationSchedule operationSchedule = (driving ? OperationSchedule
				.getCurrent(operationSchedules) : OperationSchedule
				.getCurrentOffset(operationSchedules, 1));
		if (operationSchedule != null) {
			operationSchedule.startNavigation(getActivity());
		}
	}

	public void showOperationScheduleListFragment() {
		if (!isAdded()) {
			return;
		}
		Fragments.showModalFragment(getFragmentManager(),
				OperationListFragment.newInstance(true),
				OPERATION_LIST_FRAGMENT_TAG);
	}

	public void showArrivalCheckFragment(Phase phase,
			LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
		if (!isAdded()) {
			return;
		}
		OperationSchedule operationSchedule = OperationSchedule
				.getCurrent(operationSchedules);
		if (operationSchedule == null) {
			return;
		}
		getFragmentManager()
				.beginTransaction()
				.add(R.id.modal_fragment_container,
						ArrivalCheckFragment.newInstance(operationSchedule))
				.commitAllowingStateLoss();
	}

	public void showDepartureCheckFragment(Phase phase,
			LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
		if (!isAdded()) {
			return;
		}
		final OperationSchedule operationSchedule = OperationSchedule
				.getCurrent(operationSchedules);
		if (operationSchedule == null) {
			return;
		}

		// エラーがない場合
		if (phase.equals(Phase.PLATFORM_GET_OFF)
				&& operationSchedule.getNoGetOffErrorPassengerRecords(
						passengerRecords).isEmpty()) {
			if (operationSchedule.getGetOnScheduledPassengerRecords(
					passengerRecords).isEmpty()) {
				Fragments.showModalFragment(getFragmentManager(),
						DepartureCheckFragment
								.newInstance(operationSchedule.id));
			} else {
				operationSchedule.completeGetOff = true;
				new Thread() {
					@Override
					public void run() {
						contentResolver.insert(OperationSchedules.CONTENT.URI,
								operationSchedule.toContentValues());
					}
				}.start();
			}
			return;
		} else if (phase.equals(Phase.PLATFORM_GET_ON)
				&& operationSchedule.getNoGetOnErrorPassengerRecords(
						passengerRecords).isEmpty()) {
			Fragments.showModalFragment(getFragmentManager(),
					DepartureCheckFragment.newInstance(operationSchedule.id));
			return;
		}

		// エラーがある場合
		Fragments.showModalFragment(getFragmentManager(),
				PassengerRecordErrorFragment.newInstance(operationSchedule.id));
	}

	@Override
	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			final Phase phase,
			final LinkedList<OperationSchedule> operationSchedules,
			final LinkedList<PassengerRecord> passengerRecords) {
		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewDisabler.disable(v);
				showNavigation(phase, operationSchedules, passengerRecords);
			}
		});
		Button changePhaseButton = (Button) getView().findViewById(
				R.id.change_phase_button);
		// getView().setBackgroundColor(getPhaseColor(phase));
		// getView().setBackgroundColor(Color.BLACK);
		getView().setBackgroundColor(Color.WHITE);
		// getView().setBackgroundColor(Color.GRAY);
		// getView().setBackgroundColor(Color.LTGRAY);
		switch (OperationSchedule
				.getPhase(operationSchedules, passengerRecords)) {
			case DRIVE :
				changePhaseButton.setEnabled(true);
				changePhaseButton
						.setText(getString(R.string.it_arrives_button_text));
				changePhaseButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ViewDisabler.disable(v);
						showArrivalCheckFragment(phase, operationSchedules,
								passengerRecords);
					}
				});
				break;
			case FINISH :
				changePhaseButton.setEnabled(false);
				changePhaseButton.setText("");
				break;
			case PLATFORM_GET_OFF :
				changePhaseButton.setEnabled(true);
				changePhaseButton.setText("確認\nする");
				changePhaseButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ViewDisabler.disable(v);
						showDepartureCheckFragment(phase, operationSchedules,
								passengerRecords);
					}
				});
				break;
			case PLATFORM_GET_ON :
				changePhaseButton.setEnabled(true);
				changePhaseButton.setText("確認\nする");
				changePhaseButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ViewDisabler.disable(v);
						showDepartureCheckFragment(phase, operationSchedules,
								passengerRecords);
					}
				});
				break;
		}
	}
}
