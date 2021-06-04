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
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationPhase;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * 運行中画面
 */
public class DrivePhaseFragment extends Fragment {
	private static final String TAG = DrivePhaseFragment.class.getSimpleName();
	private static final String OPERATION_PHASE_KEY = "operation_phase";

	public static DrivePhaseFragment newInstance(OperationPhase operationPhase) {
		DrivePhaseFragment fragment = new DrivePhaseFragment();
		Bundle args = new Bundle();
		args.putSerializable(OPERATION_PHASE_KEY, (Serializable) operationPhase);
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
		OperationPhase operationPhase = (OperationPhase) args.getSerializable(OPERATION_PHASE_KEY);

		if (operationPhase.isExistCurrent()) {
			OperationSchedule representativeOS = operationPhase.getCurrentRepresentativeOS();
			nextPlatformNameTextView.setText("");
			Log.i(TAG, "next platform id=" + representativeOS.platformId + " name=" + representativeOS.name);
			nextPlatformNameTextView.setText(representativeOS.name);
			platformArrivalTimeTextView2.setText("");
			platformArrivalTimeTextView2.setText(getEstimateTimeForView(operationPhase));
		}

		if (operationPhase.isExistNext()) {
			platformName1BeyondTextView.setText("▼ " + operationPhase.getNextRepresentativeOS().name);
		}
	}

	@Nullable
	private String getEstimateTimeForView(OperationPhase operationPhase) {
		List<OperationSchedule> targetOperationSchedules = operationPhase.getCurrentOperationSchedules();

		OperationSchedule currentOS = operationPhase.getCurrentRepresentativeOS();
		OperationSchedule nextOS = operationPhase.getNextRepresentativeOS();

		if (operationPhase.isExistNext() && nextOS.platformId.equals(currentOS.platformId)) {
			targetOperationSchedules.addAll(operationPhase.getNextOperationSchedules(operationPhase.operationSchedules, operationPhase.passengerRecords));
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
