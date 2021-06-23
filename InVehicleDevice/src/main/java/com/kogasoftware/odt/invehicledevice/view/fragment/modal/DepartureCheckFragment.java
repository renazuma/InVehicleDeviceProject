package com.kogasoftware.odt.invehicledevice.view.fragment.modal;

import android.app.Fragment;
import android.content.ContentResolver;
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
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationScheduleChunk;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * 出発チェック画面
 */
public class DepartureCheckFragment extends Fragment {
	private static final String TAG = DepartureCheckFragment.class.getSimpleName();
	private static final String PHASE_KEY = "phase";
	private static final String OPERATION_SCHEDULES_KEY = "operation_schedules";
	private static final String PASSENGER_RECORDS_KEY = "passenger_records";

	public static Fragment newInstance(Phase phase, List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
		DepartureCheckFragment fragment = new DepartureCheckFragment();
		Bundle args = new Bundle();
		args.putSerializable(PHASE_KEY, phase);
		args.putSerializable(OPERATION_SCHEDULES_KEY, (Serializable) operationSchedules);
		args.putSerializable(PASSENGER_RECORDS_KEY, (Serializable) passengerRecords);
		fragment.setArguments(args);
		return fragment;
	}

	private ContentResolver contentResolver;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.departure_check_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		contentResolver = getActivity().getContentResolver();

		Bundle args = getArguments();
		final Phase phase = (Phase) args.getSerializable(PHASE_KEY);

		List<OperationSchedule> operationSchedules = (List<OperationSchedule>) args.getSerializable(OPERATION_SCHEDULES_KEY);
		List<PassengerRecord> passengerRecords = (List<PassengerRecord>) args.getSerializable(PASSENGER_RECORDS_KEY);
		final List<OperationSchedule> currentChunk = OperationScheduleChunk.getCurrentChunk(operationSchedules, passengerRecords);

		View view = getView();

		Button departureButton = (Button) view.findViewById(R.id.departure_button);
		if (OperationScheduleChunk.isExistNextChunk(operationSchedules, passengerRecords) ) {
			departureButton.setText("出発する");
		} else {
			departureButton.setText("確定する");
		}

		Button closeButton = (Button) view.findViewById(R.id.departure_check_close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragments.hide(DepartureCheckFragment.this);
			}
		});
		if (phase == Phase.PLATFORM_GET_OFF) {
			closeButton.setText("降車一覧に戻る");
		} else {
			closeButton.setText("乗車一覧に戻る");
		}

		departureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragments.hide(DepartureCheckFragment.this);
				Thread tt = new Thread() {
					@Override
					public void run() {
						for (OperationSchedule operationSchedule : currentChunk) {
							operationSchedule.departedAt = DateTime.now();
							contentResolver.insert(OperationSchedule.CONTENT.URI, operationSchedule.toContentValues());
						}
					}
				};
				tt.start();
				try {
					tt.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
