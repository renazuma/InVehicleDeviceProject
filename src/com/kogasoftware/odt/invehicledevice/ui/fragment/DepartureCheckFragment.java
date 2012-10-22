package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;

import com.kogasoftware.odt.invehicledevice.ui.fragment.DepartureCheckFragment.State;

public class DepartureCheckFragment extends ApplicationFragment<State> {
	private static final String TAG = DepartureCheckFragment.class
			.getSimpleName();

	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final List<OperationSchedule> operationSchedules;
		private final Phase phase;

		public State(Phase phase, List<OperationSchedule> operationSchedules) {
			this.phase = phase;
			this.operationSchedules = Lists.newArrayList(operationSchedules);
		}

		public Phase getPhase() {
			return phase;
		}

		public List<OperationSchedule> getOperationSchedules() {
			return operationSchedules;
		}
	}

	public static DepartureCheckFragment newInstance(Phase phase,
			List<OperationSchedule> operationSchedules) {
		return newInstance(new DepartureCheckFragment(), new State(phase,
				operationSchedules));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = onCreateViewHelper(inflater, container,
				R.layout.departure_check_fragment,
				R.id.departure_check_close_button);
		Button departureButton = (Button) view
				.findViewById(R.id.departure_button);
		Button closeButton = (Button) view
				.findViewById(R.id.departure_check_close_button);
		if (getState().getPhase() == Phase.PLATFORM_GET_OFF) {
			closeButton.setText("降車一覧に戻る");
		} else {
			closeButton.setText("乗車一覧に戻る");
		}

		if (OperationSchedule.getRelativeOperationSchedule(
				getState().getOperationSchedules(), 1).isPresent()) {
			departureButton.setText("出発する");
		} else {
			departureButton.setText("確定する");
		}
		final OperationScheduleLogic operationScheduleLogic = new OperationScheduleLogic(
				getService());
		for (final OperationSchedule operationSchedule : OperationSchedule
				.getCurrentOperationSchedule(getState().getOperationSchedules())
				.asSet()) {
			departureButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					operationScheduleLogic.depart(operationSchedule,
							new Runnable() {
								@Override
								public void run() {
									operationScheduleLogic.requestUpdatePhase();
									hide();
								}
							});
				}
			});
			return view;
		}
		// error
		Log.e(TAG, "no current OperationSchedule");
		new OperationScheduleLogic(getService()).requestUpdatePhase();
		hide();
		return view;
	}
}
