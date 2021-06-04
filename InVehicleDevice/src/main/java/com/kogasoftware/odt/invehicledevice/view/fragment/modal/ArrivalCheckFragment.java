package com.kogasoftware.odt.invehicledevice.view.fragment.modal;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationScheduleChunk;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.List;

/**
 * 到着チェック画面
 */
public class ArrivalCheckFragment extends Fragment {
	private static final String OPERATION_SCHEDULES_KEY = "operation_schedules";
	private static final String PASSENGER_RECORDS_KEY = "passenger_records";

	public static Fragment newInstance(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
		ArrivalCheckFragment fragment = new ArrivalCheckFragment();
		Bundle args = new Bundle();
		args.putSerializable(OPERATION_SCHEDULES_KEY, (Serializable) operationSchedules);
		args.putSerializable(PASSENGER_RECORDS_KEY, (Serializable) passengerRecords);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		final ContentResolver contentResolver = getActivity().getContentResolver();
		final List<OperationSchedule> operationSchedules = (List<OperationSchedule>) getArguments().getSerializable(OPERATION_SCHEDULES_KEY);
		final List<PassengerRecord> passengerRecords = (List<PassengerRecord>) getArguments().getSerializable(PASSENGER_RECORDS_KEY);

		TextView commentTextView = (TextView) view.findViewById(R.id.arrival_check_comment_text_view);

		Button closeButton = (Button) view.findViewById(R.id.arrival_check_close_button);
		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Fragments.hide(ArrivalCheckFragment.this);
			}
		});

		Button arrivalButton = (Button) view.findViewById(R.id.arrival_button);
		arrivalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Fragments.hide(ArrivalCheckFragment.this);
				Thread tt = new Thread() {
					@Override
					public void run() {
						for (OperationSchedule operationSchedule : OperationScheduleChunk.getCurrentChunk(operationSchedules, passengerRecords)) {
							operationSchedule.arrivedAt = DateTime.now();
							ContentValues values = operationSchedule.toContentValues();
							contentResolver.insert(OperationSchedule.CONTENT.URI, values);
						}
					};
				};
				tt.start();
				try {
					tt.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		commentTextView.setText(OperationScheduleChunk.getCurrentChunkRepresentativeOS(operationSchedules, passengerRecords).name);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.arrival_check_fragment, container, false);
	}
}