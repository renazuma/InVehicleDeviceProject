package com.kogasoftware.odt.invehicledevice.view.fragment.modal;

import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationPhase;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationSchedulesSyncFragmentAbstract;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ViewDisabler;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.arrayadapter.PassengerRecordErrorArrayAdapter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 乗車予定なのに乗ってないor降車予定なのに降りてない人の確認画面
 */
public class PassengerRecordErrorFragment extends OperationSchedulesSyncFragmentAbstract {
	private static final String TAG = PassengerRecordErrorFragment.class.getSimpleName();
	private static final String OPERATION_PHASE_KEY = "operation_phase";
	private Button completeWithErrorButton;
	private ContentResolver contentResolver;
	private OperationPhase operationPhase;
	private List<OperationSchedule> operationSchedules;
	private Button closeButton;
	private FlickUnneededListView errorUserListView;
	private PassengerRecordErrorArrayAdapter adapter;

	public static PassengerRecordErrorFragment newInstance(OperationPhase operationPhase) {
		PassengerRecordErrorFragment fragment = new PassengerRecordErrorFragment();
		Bundle args = new Bundle();
		args.putSerializable(OPERATION_PHASE_KEY, (Serializable) operationPhase);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.passenger_record_error_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		contentResolver = getActivity().getContentResolver();
		Bundle args = getArguments();
		operationPhase = (OperationPhase)args.getSerializable(OPERATION_PHASE_KEY);
		operationSchedules = operationPhase.getCurrentOperationSchedules();
		View view = getView();
		closeButton = (Button) view.findViewById(R.id.get_off_check_close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragments.hide(PassengerRecordErrorFragment.this);
			}
		});
		errorUserListView = (FlickUnneededListView) view.findViewById(R.id.error_reservation_list_view);
		completeWithErrorButton = (Button) view.findViewById(R.id.complete_with_error_button);
		adapter = new PassengerRecordErrorArrayAdapter(this, operationSchedules);
		errorUserListView.getListView().setAdapter(adapter);
	}

	private void complete(Phase phase, final OperationPhase newOperationPhase) {
		if (!isAdded()) {
			return;
		}

		List<PassengerRecord> getOnSchedulePassengerRecords = Lists.newArrayList();
		for (OperationSchedule operationSchedule : newOperationPhase.getCurrentOperationSchedules()) {
			getOnSchedulePassengerRecords.addAll(operationSchedule.getGetOnScheduledPassengerRecords(newOperationPhase.passengerRecords));
		}

		if (phase == Phase.PLATFORM_GET_ON || getOnSchedulePassengerRecords.isEmpty()) {
			getFragmentManager()
					.beginTransaction()
					.add(R.id.modal_fragment_container, DepartureCheckFragment.newInstance(newOperationPhase))
					.commitAllowingStateLoss();
			return;
		}

		new Thread() {
			@Override
			public void run() {
				for (OperationSchedule operationSchedule : newOperationPhase.getCurrentOperationSchedules()) {
					operationSchedule.completeGetOff = true;
					contentResolver.insert(OperationSchedule.CONTENT.URI, operationSchedule.toContentValues());
				}
			}
		}.start();

		Fragments.hide(this);
	}

	@Override
	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			final LinkedList<OperationSchedule> operationSchedules,
			final LinkedList<PassengerRecord> passengerRecords) {

		final OperationPhase newOperationPhase = new OperationPhase(operationSchedules, passengerRecords);

		boolean existSameId = false;
		for (OperationSchedule currentOS : newOperationPhase.getCurrentOperationSchedules()) {
			for (OperationSchedule containOS : this.operationSchedules) {
				if (currentOS.id.equals(containOS.id)) {
					existSameId = true;
					break;
				}
			}
		}
		if (!newOperationPhase.isExistCurrent() || !existSameId) {
			Fragments.hide(this);
			return;
		}

		final Phase phase = OperationSchedule.getPhase(operationSchedules, passengerRecords);
		List<PassengerRecord> errorPassengerRecords = Lists.newLinkedList();
		if (phase.equals(Phase.PLATFORM_GET_OFF)) {
			for (OperationSchedule operationSchedule : newOperationPhase.getCurrentOperationSchedules()) {
				errorPassengerRecords.addAll(operationSchedule.getNoGetOffErrorPassengerRecords(passengerRecords));
			}
		} else {
			for (OperationSchedule operationSchedule : newOperationPhase.getCurrentOperationSchedules()) {
				errorPassengerRecords.addAll(operationSchedule.getNoGetOnErrorPassengerRecords(passengerRecords));
			}
		}
		adapter.update(errorPassengerRecords);

		if (phase.equals(Phase.PLATFORM_GET_ON)) {
			closeButton.setText("乗車一覧に戻る");
		} else {
			closeButton.setText("降車一覧に戻る");
		}

		completeWithErrorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewDisabler.disable(view);
				complete(phase, newOperationPhase);
			}
		});
		if (adapter.hasError()) {
			completeWithErrorButton.setEnabled(false);
		} else {
			completeWithErrorButton.setEnabled(true);
		}
	}
}
