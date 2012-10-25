package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundWriter;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.ViewDisabler;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordErrorArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PassengerRecordErrorFragment.State;

public class PassengerRecordErrorFragment extends ApplicationFragment<State>
		implements EventDispatcher.OnUpdatePhaseListener,
		PassengerRecordErrorArrayAdapter.OnPassengerRecordChangeListener {

	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final List<OperationSchedule> operationSchedules;
		private final List<PassengerRecord> passengerRecords;
		private final Phase phase;

		public State(Phase phase, List<OperationSchedule> operationSchedules,
				List<PassengerRecord> passengerRecords) {
			this.phase = phase;
			this.operationSchedules = operationSchedules;
			this.passengerRecords = passengerRecords;
		}

		public List<OperationSchedule> getOperationSchedules() {
			return operationSchedules;
		}

		public List<PassengerRecord> getPassengerRecords() {
			return passengerRecords;
		}

		public Phase getPhase() {
			return phase;
		}
	}

	private static final String TAG = PassengerRecordErrorFragment.class
			.getSimpleName();
	private Button completeWithErrorButton;

	public static PassengerRecordErrorFragment newInstance(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		return newInstance(new PassengerRecordErrorFragment(), new State(phase,
				operationSchedules, passengerRecords));
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getService().getEventDispatcher().removeOnUpdatePhaseListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getService().getEventDispatcher().addOnUpdatePhaseListener(this);
		View view = onCreateViewHelper(inflater, container,
				R.layout.passenger_record_error_fragment,
				R.id.get_off_check_close_button);

		Button closeButton = (Button) view
				.findViewById(R.id.get_off_check_close_button);
		FlickUnneededListView errorUserListView = (FlickUnneededListView) view
				.findViewById(R.id.error_reservation_list_view);

		for (final OperationSchedule operationSchedule : OperationSchedule
				.getCurrent(getState().getOperationSchedules()).asSet()) {
			List<PassengerRecord> errorPassengerRecords = Lists.newLinkedList();
			if (getState().getPhase() == Phase.PLATFORM_GET_OFF) {
				errorPassengerRecords.addAll(operationSchedule
						.getNoGetOffErrorPassengerRecords(getState()
								.getPassengerRecords()));
			} else {
				errorPassengerRecords.addAll(operationSchedule
						.getNoGetOnErrorPassengerRecords(getState()
								.getPassengerRecords()));
			}

			completeWithErrorButton = (Button) view
					.findViewById(R.id.complete_with_error_button);

			if (getState().getPhase() == Phase.PLATFORM_GET_ON) {
				// String caption = getString(R.string.it_leaves);
				// completeWithErrorButton.setText(caption);
				completeWithErrorButton.setText("次へ");
				closeButton.setText("乗車一覧に戻る");
			} else {
				// String caption =
				// getString(R.string.it_completes_getting_off);
				// completeWithErrorButton.setText(caption);
				completeWithErrorButton.setText("次へ");
				closeButton.setText("降車一覧に戻る");
			}

			completeWithErrorButton.setTextColor(Color.GRAY);
			completeWithErrorButton.setEnabled(false);
			completeWithErrorButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					ViewDisabler.disable(view);
					complete(getState().getPhase(), operationSchedule,
							getState().getOperationSchedules(), getState()
									.getPassengerRecords());
				}
			});

			PassengerRecordErrorArrayAdapter adapter = new PassengerRecordErrorArrayAdapter(
					getActivity(), getService(), getFragmentManager(),
					operationSchedule, errorPassengerRecords, this);
			errorUserListView.getListView().setAdapter(adapter);
			onPassengerRecordChange(adapter);
			return view;
		}

		// error
		Log.e(TAG, "no current OperationSchedule");
		new OperationScheduleLogic(getService()).requestUpdatePhase();
		hide();
		return view;
	}

	private void complete(Phase phase,
			OperationSchedule currentOperationSchedule,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		final OperationScheduleLogic operationScheduleLogic = new OperationScheduleLogic(
				getService());
		if (phase == Phase.PLATFORM_GET_ON
				|| currentOperationSchedule.getGetOnScheduledPassengerRecords(
						passengerRecords).isEmpty()) {
			setCustomAnimation(getFragmentManager().beginTransaction()).add(
					R.id.modal_fragment_container,
					DepartureCheckFragment.newInstance(phase,
							operationSchedules)).commitAllowingStateLoss();
			return;
		}
		getService().getLocalStorage().write(new BackgroundWriter() {
			@Override
			public void writeInBackground(LocalData ld) {
				ld.completeGetOff = true;
			}

			@Override
			public void onWrite() {
				hide();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						operationScheduleLogic.requestUpdatePhase();
					}
				}, 300);
			}
		});
	}

	@Override
	public void onPassengerRecordChange(PassengerRecordErrorArrayAdapter adapter) {
		if (adapter.hasError()) {
			completeWithErrorButton.setTextColor(Color.GRAY);
			completeWithErrorButton.setEnabled(false);
		} else {
			completeWithErrorButton.setTextColor(Color.BLACK);
			completeWithErrorButton.setEnabled(true);
		}
	}

	@Override
	public void onUpdatePhase(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		getFragmentManager().beginTransaction().remove(this)
				.commitAllowingStateLoss();
	}
}
