package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.ui.fragment.ArrivalCheckFragment.State;

public class ArrivalCheckFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final OperationSchedule operationSchedule;

		public State(OperationSchedule operationSchedule) {
			this.operationSchedule = operationSchedule;
		}

		public OperationSchedule getOperationSchedule() {
			return operationSchedule;
		}
	}

	public ArrivalCheckFragment() {
		setRemoveOnUpdatePhase(true);
	}

	public static Fragment newInstance(OperationSchedule operationSchedule) {
		return newInstance(new ArrivalCheckFragment(), new State(
				operationSchedule));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final OperationScheduleLogic operationScheduleLogic = new OperationScheduleLogic(
				getService());
		View view = onCreateViewHelper(inflater, container,
				R.layout.arrival_check_fragment,
				R.id.arrival_check_close_button);
		TextView commentTextView = (TextView) view
				.findViewById(R.id.arrival_check_comment_text_view);
		Button arrivalButton = (Button) view.findViewById(R.id.arrival_button);
		arrivalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				operationScheduleLogic.arrive(
						getState().getOperationSchedule(), new Runnable() {
							@Override
							public void run() {
								hide();
								operationScheduleLogic.requestUpdatePhase();
							}
						});
			}
		});
		for (Platform platform : getState().getOperationSchedule()
				.getPlatform().asSet()) {
			commentTextView.setText(platform.getName());
		}

		return view;
	}
}
