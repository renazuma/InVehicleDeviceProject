package com.kogasoftware.odt.invehicledevice.view.fragment;

import android.content.ContentResolver;
import android.graphics.Color;
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
import com.kogasoftware.odt.invehicledevice.view.util.ViewDisabler;
import com.kogasoftware.odt.invehicledevice.view.util.arrayadapter.PassengerRecordErrorArrayAdapter;

import java.util.LinkedList;
import java.util.List;

/**
 * 乗車予定なのに乗ってないor降車予定なのに降りてない人の確認画面
 */
public class PassengerRecordErrorFragment
		extends
        OperationSchedulesSyncFragmentAbstract {
	private static final String TAG = PassengerRecordErrorFragment.class
			.getSimpleName();
	private static final String OPERATION_SCHEDULE_ID_KEY = "operation_schedule_id";
	private Button completeWithErrorButton;
	private ContentResolver contentResolver;
	private Long operationScheduleId;
	private Button closeButton;
	private FlickUnneededListView errorUserListView;
	private PassengerRecordErrorArrayAdapter adapter;

	public static PassengerRecordErrorFragment newInstance(
			Long operationScheduleId) {
		PassengerRecordErrorFragment fragment = new PassengerRecordErrorFragment();
		Bundle args = new Bundle();
		args.putSerializable(OPERATION_SCHEDULE_ID_KEY, operationScheduleId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.passenger_record_error_fragment,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		contentResolver = getActivity().getContentResolver();
		Bundle args = getArguments();
		operationScheduleId = args.getLong(OPERATION_SCHEDULE_ID_KEY);
		View view = getView();
		closeButton = (Button) view
				.findViewById(R.id.get_off_check_close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragments.hide(PassengerRecordErrorFragment.this);
			}
		});
		errorUserListView = (FlickUnneededListView) view
				.findViewById(R.id.error_reservation_list_view);
		completeWithErrorButton = (Button) view
				.findViewById(R.id.complete_with_error_button);
		adapter = new PassengerRecordErrorArrayAdapter(this,
				operationScheduleId);
		errorUserListView.getListView().setAdapter(adapter);
	}

	private void complete(Phase phase,
			final OperationSchedule currentOperationSchedule,
			LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
		if (!isAdded()) {
			return;
		}
		if (phase == Phase.PLATFORM_GET_ON
				|| currentOperationSchedule.getGetOnScheduledPassengerRecords(
						passengerRecords).isEmpty()) {
			getFragmentManager()
					.beginTransaction()
					.add(R.id.modal_fragment_container,
							DepartureCheckFragment
									.newInstance(currentOperationSchedule.id))
					.commitAllowingStateLoss();
			return;
		}
		new Thread() {
			@Override
			public void run() {
				currentOperationSchedule.completeGetOff = true;
				contentResolver.insert(OperationSchedule.CONTENT.URI,
						currentOperationSchedule.toContentValues());
			}
		}.start();
		Fragments.hide(this);
	}

	@Override
	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			final Phase phase,
			final LinkedList<OperationSchedule> operationSchedules,
			final LinkedList<PassengerRecord> passengerRecords) {
		final OperationSchedule operationSchedule = OperationSchedule
				.getCurrent(operationSchedules);
		if (operationSchedule == null
				|| !operationSchedule.id.equals(operationScheduleId)) {
			Fragments.hide(this);
			return;
		}
		List<PassengerRecord> errorPassengerRecords = Lists.newLinkedList();
		if (phase.equals(Phase.PLATFORM_GET_OFF)) {
			errorPassengerRecords.addAll(operationSchedule
					.getNoGetOffErrorPassengerRecords(passengerRecords));
		} else {
			errorPassengerRecords.addAll(operationSchedule
					.getNoGetOnErrorPassengerRecords(passengerRecords));
		}
		adapter.update(errorPassengerRecords);

		if (phase.equals(Phase.PLATFORM_GET_ON)) {
			completeWithErrorButton.setText("次へ");
			closeButton.setText("乗車一覧に戻る");
		} else {
			completeWithErrorButton.setText("次へ");
			closeButton.setText("降車一覧に戻る");
		}

		completeWithErrorButton.setTextColor(Color.GRAY);
		completeWithErrorButton.setEnabled(false);
		completeWithErrorButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewDisabler.disable(view);
				complete(phase, operationSchedule, operationSchedules,
						passengerRecords);
			}
		});
		if (adapter.hasError()) {
			completeWithErrorButton.setTextColor(Color.GRAY);
			completeWithErrorButton.setEnabled(false);
		} else {
			completeWithErrorButton.setTextColor(Color.BLACK);
			completeWithErrorButton.setEnabled(true);
		}
	}
}
