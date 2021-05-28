package com.kogasoftware.odt.invehicledevice.view.fragment.controlbar;

import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.ArrivalCheckFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.DepartureCheckFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.OperationListFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.PassengerRecordErrorFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationSchedulesSyncFragmentAbstract;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ViewDisabler;

import java.util.LinkedList;
import java.util.List;

/**
 * 到着ボタン、地図ボタン、運行予定ボタンを表示する領域
 */
public class ControlBarFragment	extends OperationSchedulesSyncFragmentAbstract {
	private static final String TAG = ControlBarFragment.class.getSimpleName();
	public static final String OPERATION_LIST_FRAGMENT_TAG = ControlBarFragment.class
			+ "/" + OperationSchedulesSyncFragmentAbstract.class;
	private ContentResolver contentResolver;
	private Button mapButton;

	public static ControlBarFragment newInstance() {
		ControlBarFragment fragment = new ControlBarFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.control_bar_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		contentResolver = getActivity().getContentResolver();
		mapButton = (Button) getView().findViewById(R.id.map_button);
		Button operationScheduleListButton = (Button) getView().findViewById(R.id.operation_schedule_list_button);

		// TODO: 運行予定ボタンの定義。地図やphaseボタンと異なり、特別な引数や文字の変更が無いから、ここで定義されているが、わかりにくい。
		operationScheduleListButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewDisabler.disable(v);
				showOperationScheduleListFragment();
			}
		});
	}

	// 表示メソッド
	public void showNavigation(Phase phase,	LinkedList<OperationSchedule> operationSchedules, LinkedList<PassengerRecord> passengerRecords) {
		if (!isAdded()) { return; }

		// 停車中の場合、次の運行を選択
		Boolean driving = phase.equals(Phase.DRIVE);
		OperationSchedule operationSchedule = (driving ? OperationSchedule
				.getCurrent(operationSchedules) : OperationSchedule
				.getCurrentOffset(operationSchedules, 1));

		if (operationSchedule != null) { operationSchedule.startNavigation(getActivity()); }
	}

	public void showOperationScheduleListFragment() {
		if (!isAdded()) { return; }

		Fragments.showModalFragment(getFragmentManager(), OperationListFragment.newInstance(true), OPERATION_LIST_FRAGMENT_TAG);
	}

	public void showArrivalCheckFragment(LinkedList<OperationSchedule> operationSchedules, LinkedList<PassengerRecord> passengerRecords) {

		if (!isAdded()) { return; }

		OperationSchedule operationSchedule = OperationSchedule.getCurrent(operationSchedules);

		if (operationSchedule == null) { return; }

		getFragmentManager()
			.beginTransaction()
			.add(R.id.modal_fragment_container, ArrivalCheckFragment.newInstance(operationSchedule))
			.commitAllowingStateLoss();
	}

	public void showDepartureCheckFragment(Phase phase,	LinkedList<OperationSchedule> operationSchedules, LinkedList<PassengerRecord> passengerRecords) {
		if (!isAdded()) { return; }

		final OperationSchedule operationSchedule = OperationSchedule.getCurrent(operationSchedules);

		if (operationSchedule == null) { return; }

		// エラーがない場合
		if (phase.equals(Phase.PLATFORM_GET_OFF)
				&& operationSchedule.getNoGetOffErrorPassengerRecords(passengerRecords).isEmpty()) {
			if (operationSchedule.getGetOnScheduledPassengerRecords(passengerRecords).isEmpty()) {
				Fragments.showModalFragment(getFragmentManager(), DepartureCheckFragment.newInstance(phase, operationSchedules, operationSchedule.id));
			} else {
				operationSchedule.completeGetOff = true;
				new Thread() {
					@Override
					public void run() {
						contentResolver.insert(OperationSchedule.CONTENT.URI, operationSchedule.toContentValues());
					}
				}.start();
			}
			return;
		} else if (phase.equals(Phase.PLATFORM_GET_ON)
				&& operationSchedule.getNoGetOnErrorPassengerRecords(passengerRecords).isEmpty()) {
			Fragments.showModalFragment(getFragmentManager(), DepartureCheckFragment.newInstance(phase, operationSchedules, operationSchedule.id));
			return;
		}

		// エラーがある場合
		Fragments.showModalFragment(getFragmentManager(), PassengerRecordErrorFragment.newInstance(operationSchedule.id));
	}

	// 画面右部のボタンの、地図ボタン、phase変更ボタン（到着しました等）を定義する
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

		Button changePhaseButton = (Button) getView().findViewById(R.id.change_phase_button);
		getView().setBackgroundColor(Color.WHITE);
		switch (OperationSchedule.getPhase(operationSchedules, passengerRecords)) {
			case DRIVE :
				changePhaseButton.setEnabled(true);
				changePhaseButton.setText(getString(R.string.it_arrives_button_text));
				changePhaseButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ViewDisabler.disable(v);
						showArrivalCheckFragment(operationSchedules, passengerRecords);
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
						showDepartureCheckFragment(phase, operationSchedules, passengerRecords);
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
						showDepartureCheckFragment(phase, operationSchedules, passengerRecords);
					}
				});
				break;
		}
	}
}
