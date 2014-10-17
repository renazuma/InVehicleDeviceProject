package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;

/**
 * 運行中画面
 */
public class DrivePhaseFragment extends Fragment {
	private static final String TAG = DrivePhaseFragment.class.getSimpleName();
	private static final String OPERATION_SCHEDULES_KEY = "operation_schedules";

	public static DrivePhaseFragment newInstance(
			LinkedList<OperationSchedule> operationSchedules) {
		DrivePhaseFragment fragment = new DrivePhaseFragment();
		Bundle args = new Bundle();
		args.putSerializable(OPERATION_SCHEDULES_KEY, operationSchedules);
		fragment.setArguments(args);
		return fragment;
	}

	private OperationSchedule operationSchedule;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.drive_phase_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		TextView nextPlatformNameTextView = (TextView) view
				.findViewById(R.id.next_platform_name_text_view);
		TextView platformArrivalTimeTextView2 = (TextView) view
				.findViewById(R.id.platform_arrival_time_text_view2);
		TextView platformName1BeyondTextView = (TextView) view
				.findViewById(R.id.platform_name_1_beyond_text_view);
		TypedArray typedArray = getActivity().obtainStyledAttributes(
				new int[]{android.R.attr.background});
		Integer backgroundColor = typedArray.getColor(0, Color.WHITE);
		view.setBackgroundColor(backgroundColor);
		Bundle args = getArguments();
		List<OperationSchedule> operationSchedules = (List<OperationSchedule>) args
				.getSerializable(OPERATION_SCHEDULES_KEY);
		operationSchedule = OperationSchedule.getCurrent(operationSchedules);
		if (operationSchedule != null) {
			nextPlatformNameTextView.setText("");
			Log.i(TAG, "next platform id=" + operationSchedule.platformId
					+ " name=" + operationSchedule.name);
			nextPlatformNameTextView.setText(operationSchedule.name);
			platformArrivalTimeTextView2.setText("");
			DateFormat dateFormat = new SimpleDateFormat(getResources()
					.getString(R.string.platform_arrival_time_format),
					Locale.US);
			platformArrivalTimeTextView2.setText(dateFormat
					.format(operationSchedule.arrivalEstimate.toDate()));
			String message = "出発します 次は " + operationSchedule.nameRuby + " "
					+ operationSchedule.nameRuby;
			VoiceService.speak(getActivity(), message);
		}
		OperationSchedule nextOperationSchedule = OperationSchedule
				.getCurrentOffset(operationSchedules, 1);
		if (nextOperationSchedule != null) {
			platformName1BeyondTextView.setText("▼ "
					+ nextOperationSchedule.name);
		}
	}
}
