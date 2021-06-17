package com.kogasoftware.odt.invehicledevice.view.fragment.phasecontent;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 運行中画面
 */
public class DrivePhaseFragment extends Fragment {
	private static final String TAG = DrivePhaseFragment.class.getSimpleName();
	private static final String OPERATION_SCHEDULES_KEY = "operation_schedules";
	private static final String PASSENGER_RECORDS_KEY = "passenger_records";

	public static DrivePhaseFragment newInstance(LinkedList<OperationSchedule> operationSchedules, LinkedList<PassengerRecord> passengerRecords) {
		DrivePhaseFragment fragment = new DrivePhaseFragment();
		Bundle args = new Bundle();
		args.putSerializable(OPERATION_SCHEDULES_KEY, operationSchedules);
		args.putSerializable(PASSENGER_RECORDS_KEY, passengerRecords);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.drive_phase_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();

		// style修正
		TypedArray typedArray = getActivity().obtainStyledAttributes(new int[]{android.R.attr.background});
		Integer backgroundColor = typedArray.getColor(0, Color.WHITE);
		view.setBackgroundColor(backgroundColor);

		//文言表示
		TextView nextPlatformNameTextView = (TextView) view.findViewById(R.id.next_platform_name_text_view);
		TextView platformArrivalTimeTextView2 = (TextView) view.findViewById(R.id.platform_arrival_time_text_view2);
		TextView platformName1BeyondTextView = (TextView) view.findViewById(R.id.platform_name_1_beyond_text_view);

		Bundle args = getArguments();
		List<OperationSchedule> operationSchedules = (List<OperationSchedule>) args.getSerializable(OPERATION_SCHEDULES_KEY);
		List<PassengerRecord> passengerRecords = (List<PassengerRecord>) args.getSerializable(PASSENGER_RECORDS_KEY);

		if (OperationSchedule.isExistCurrentChunk(operationSchedules, passengerRecords)) {
			OperationSchedule representativeOS = OperationSchedule.getCurrentChunkRepresentativeOS(operationSchedules, passengerRecords);
			nextPlatformNameTextView.setText("");
			Log.i(TAG, "next platform id=" + representativeOS.platformId + " name=" + representativeOS.name);
			nextPlatformNameTextView.setText(representativeOS.name);
			platformArrivalTimeTextView2.setText("");
			platformArrivalTimeTextView2.setText(getEstimateTimeForView(operationSchedules, passengerRecords));
		}

		if (OperationSchedule.isExistNextChunk(operationSchedules, passengerRecords)) {
			platformName1BeyondTextView.setText("▼ " + OperationSchedule.getNextChunkRepresentativeOS(operationSchedules, passengerRecords).name);
		}
	}

	@Nullable
	private String getEstimateTimeForView(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
		List<OperationSchedule> targetOperationSchedules = OperationSchedule.getCurrentChunk(operationSchedules, passengerRecords);

		OperationSchedule currentOS = OperationSchedule.getCurrentChunkRepresentativeOS(operationSchedules, passengerRecords);
		OperationSchedule nextOS = OperationSchedule.getNextChunkRepresentativeOS(operationSchedules, passengerRecords);

		if (OperationSchedule.isExistNextChunk(operationSchedules, passengerRecords) && nextOS.platformId.equals(currentOS.platformId)) {
			targetOperationSchedules.addAll(OperationSchedule.getNextChunk(operationSchedules, passengerRecords));
		}

		OperationSchedule estimateOS = null;
		for (OperationSchedule operationSchedule : targetOperationSchedules) {
			if (null == estimateOS || operationSchedule.arrivalEstimate.isBefore(estimateOS.arrivalEstimate)) {
				estimateOS = operationSchedule;
			}
		}

		DateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.platform_arrival_time_format), Locale.US);
		return dateFormat.format(estimateOS.arrivalEstimate.toDate());
	}
}
