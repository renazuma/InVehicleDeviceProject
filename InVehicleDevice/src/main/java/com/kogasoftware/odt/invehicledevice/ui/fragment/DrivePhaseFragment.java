package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import com.kogasoftware.odt.invehicledevice.ui.fragment.DrivePhaseFragment.State;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;

public class DrivePhaseFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final List<OperationSchedule> operationSchedules;

		public State(List<OperationSchedule> operationSchedules) {
			this.operationSchedules = Lists.newArrayList(operationSchedules);
		}

		public List<OperationSchedule> getOperationSchedules() {
			return operationSchedules;
		}
	}

	private static final String TAG = DrivePhaseFragment.class.getSimpleName();

	public static DrivePhaseFragment newInstance(
			List<OperationSchedule> operationSchedules) {
		return newInstance(new DrivePhaseFragment(), new State(
				operationSchedules));
	}

	private TextView nextPlatformNameTextView;
	private TextView platformArrivalTimeTextView2;
	private TextView platformName1BeyondTextView;

	private void updateView(View view) {
		Log.i(TAG, "updateView");
		TypedArray typedArray = getActivity().obtainStyledAttributes(
				new int[] { android.R.attr.background });
		Integer backgroundColor = typedArray.getColor(0, Color.WHITE);
		view.setBackgroundColor(backgroundColor);

		Optional<OperationSchedule> currentOperationSchedule = OperationSchedule
				.getCurrent(getState().getOperationSchedules());
		for (OperationSchedule operationSchedule : currentOperationSchedule
				.asSet()) {
			nextPlatformNameTextView.setText("");
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				Log.i(TAG, "next platform id=" + platform.getId() + " name=" + platform.getName());
				nextPlatformNameTextView.setText(platform.getName());
			}
			platformArrivalTimeTextView2.setText("");
			for (Date arrivalEstimate : operationSchedule.getArrivalEstimate()
					.asSet()) {
				DateFormat dateFormat = new SimpleDateFormat(getResources()
						.getString(R.string.platform_arrival_time_format), Locale.US);
				platformArrivalTimeTextView2.setText(dateFormat
						.format(arrivalEstimate));
			}
		}

		Optional<OperationSchedule> nextOperationSchedule = OperationSchedule
				.getRelative(getState()
						.getOperationSchedules(), 1);
		for (OperationSchedule operationSchedule : nextOperationSchedule
				.asSet()) {
			platformName1BeyondTextView.setText("");
			for (Platform platform1 : operationSchedule.getPlatform().asSet()) {
				platformName1BeyondTextView.setText("▼ " + platform1.getName());
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.drive_phase_fragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		nextPlatformNameTextView = (TextView) view
				.findViewById(R.id.next_platform_name_text_view);
		platformArrivalTimeTextView2 = (TextView) view
				.findViewById(R.id.platform_arrival_time_text_view2);
		platformName1BeyondTextView = (TextView) view
				.findViewById(R.id.platform_name_1_beyond_text_view);
		updateView(view);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "platform: " + Objects.firstNonNull(nextPlatformNameTextView.getText(), "(None)"));
	}
}
