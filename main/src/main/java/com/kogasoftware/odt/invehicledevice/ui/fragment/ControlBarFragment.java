package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundWriter;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.ui.ViewDisabler;
import com.kogasoftware.odt.invehicledevice.ui.fragment.ControlBarFragment.State;

public class ControlBarFragment extends AutoUpdateOperationFragment<State> {
	private static final String TAG = ControlBarFragment.class.getSimpleName();

	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final Operation operation;

		public State(Operation operation) {
			this.operation = operation;
		}
		
		public Phase getPhase() {
			return operation.getPhase();
		}

		public List<PassengerRecord> getPassengerRecords() {
			return operation.passengerRecords;
		}

		public List<OperationSchedule> getOperationSchedules() {
			return operation.operationSchedules;
		}

		public Operation getOperation() {
			return operation;
		}
	}

	public static ControlBarFragment newInstance(Operation operation) {
		return newInstance(new ControlBarFragment(), new State(operation));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.control_bar_fragment, container,
				false);
		Button mapButton = (Button) view.findViewById(R.id.map_button);
		mapButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewDisabler.disable(v);
				showNavigationFragment();
			}
		});

		Button operationScheduleListButton = (Button) view
				.findViewById(R.id.operation_schedule_list_button);
		operationScheduleListButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewDisabler.disable(v);
				showOperationScheduleListFragment();
			}
		});

		getService().getLocalStorage().read(
				new BackgroundReader<Pair<Phase, Boolean>>() {
					@Override
					public Pair<Phase, Boolean> readInBackground(
							LocalData localData) {
						Phase phase = localData.operation.getPhase();
						Boolean isLast = !OperationSchedule.getRelative(
								localData.operation.operationSchedules, 1).isPresent();
						return Pair.of(phase, isLast);
					}

					@Override
					public void onRead(Pair<Phase, Boolean> result) {
						updateView(result.getLeft(), result.getRight());
					}
				});
		return view;
	}

	public void showNavigationFragment() {
		if (isRemoving()) {
			return;
		}
		String tag = "tag:" + NavigationFragment.class.getSimpleName();
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			Fragment old = fragmentManager.findFragmentByTag(tag);
			if (old != null) {
				setCustomAnimation(fragmentManager.beginTransaction()).remove(
						old).commit();
			}
			getService().getLocalStorage().read(new BackgroundReader<Pair<ServiceUnitStatusLog, Pair<Boolean, Integer>>>() {
				@Override
				public Pair<ServiceUnitStatusLog, Pair<Boolean, Integer>> readInBackground(LocalData localData) {
					return Pair.of( // データが増えてきたら専用の型を作るようにする
							localData.serviceUnitStatusLog,
							Pair.of(localData.rotateMap,
									localData.extraRotationDegreesClockwise));
				}

				@Override
				public void onRead(
						Pair<ServiceUnitStatusLog, Pair<Boolean, Integer>> read) {
					ServiceUnitStatusLog serviceUnitStatusLog = read
							.getLeft();
					Boolean rotateMap = read.getRight().getLeft();
					Integer extraRotationDegreesClockwise = read
							.getRight().getRight();
					for (FragmentManager fragmentManager : getOptionalFragmentManager()
							.asSet()) {
						setCustomAnimation(fragmentManager.beginTransaction())
							.add(R.id.modal_fragment_container,
									NavigationFragment.newInstance(
											getState().getOperation(),
											serviceUnitStatusLog,
											rotateMap,
											extraRotationDegreesClockwise))
							.commitAllowingStateLoss();
					}
				}
			});
		}
	}

	public void showOperationScheduleListFragment() {
		String tag = "tag:"
				+ OperationScheduleListFragment.class.getSimpleName();
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			Fragment old = fragmentManager.findFragmentByTag(tag);
			if (old != null) {
				setCustomAnimation(fragmentManager.beginTransaction()).remove(
						old).commit();
			}
			setCustomAnimation(fragmentManager.beginTransaction()).add(
					R.id.modal_fragment_container,
					OperationScheduleListFragment.newInstance(getState()
							.getOperationSchedules())).commit();
		}
	}

	public void showArrivalCheckFragment() {
		for (OperationSchedule operationSchedule : OperationSchedule
				.getCurrent(getState().getOperationSchedules()).asSet()) {
			for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
				String tag = "tag:" + ArrivalCheckFragment.class.getSimpleName();
				Fragment old = fragmentManager.findFragmentByTag(tag);
				if (old != null) {
					setCustomAnimation(fragmentManager.beginTransaction())
						.remove(old).commit();
				}
				setCustomAnimation(fragmentManager.beginTransaction()).add(
					R.id.modal_fragment_container,
					ArrivalCheckFragment.newInstance(operationSchedule))
						.commit();
			}
		}
	}

	public void showDepartureCheckFragment() {
		if (isRemoving()) {
			return;
		}
		showDepartureCheckFragment(getState().getPhase(), getState()
				.getOperationSchedules(), getState().getPassengerRecords());
	}

	public void showDepartureCheckFragment(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		if (!OperationSchedule.getCurrent(operationSchedules).isPresent()) {
			Log.e(TAG, "current operation schedule not found");
			return;
		}
		OperationSchedule operationSchedule = OperationSchedule.getCurrent(
				operationSchedules).get();
		final OperationScheduleLogic operationScheduleLogic = new OperationScheduleLogic(
				getService());

		for (FragmentManager fragmentManager : getOptionalFragmentManager()
				.asSet()) {
			if (phase == Phase.PLATFORM_GET_OFF
					&& operationSchedule.getNoGetOffErrorPassengerRecords(
							passengerRecords).isEmpty()) {
				if (operationSchedule.getGetOnScheduledPassengerRecords(
						passengerRecords).isEmpty()) {
					setCustomAnimation(fragmentManager.beginTransaction()).add(
							R.id.modal_fragment_container,
							DepartureCheckFragment.newInstance(phase,
									operationSchedules))
							.commitAllowingStateLoss();
				} else {
					getService().getLocalStorage().write(
							new BackgroundWriter() {
								@Override
								public void writeInBackground(LocalData ld) {
									ld.operation.completeGetOff = true;
								}

								@Override
								public void onWrite() {
									operationScheduleLogic.requestUpdateOperation();
								}
							});
				}
				return;
			} else if (phase == Phase.PLATFORM_GET_ON
					&& operationSchedule.getNoGetOnErrorPassengerRecords(
							passengerRecords).isEmpty()) {
				setCustomAnimation(fragmentManager.beginTransaction()).add(
						R.id.modal_fragment_container,
						DepartureCheckFragment.newInstance(phase,
								operationSchedules)).commitAllowingStateLoss();
				return;
			}

			// エラーがある場合
			String tag = "tag:"
					+ PassengerRecordErrorFragment.class.getSimpleName();
			Fragment old = fragmentManager.findFragmentByTag(tag);
			if (old != null) {
				setCustomAnimation(fragmentManager.beginTransaction()).remove(
						old).commitAllowingStateLoss();
			}
			setCustomAnimation(fragmentManager.beginTransaction()).add(
					R.id.modal_fragment_container,
					PassengerRecordErrorFragment.newInstance(phase,
							operationSchedules, passengerRecords))
					.commitAllowingStateLoss();
		}
	}

	public void updateView(Phase phase, Boolean isLast) {
		Button changePhaseButton = (Button) getView().findViewById(
				R.id.change_phase_button);
		// getView().setBackgroundColor(getPhaseColor(phase));
		// getView().setBackgroundColor(Color.BLACK);
		getView().setBackgroundColor(Color.WHITE);
		// getView().setBackgroundColor(Color.GRAY);
		// getView().setBackgroundColor(Color.LTGRAY);
		switch (phase) {
		case DRIVE:
			changePhaseButton.setEnabled(true);
			changePhaseButton
					.setText(getString(R.string.it_arrives_button_text));
			changePhaseButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewDisabler.disable(v);
					showArrivalCheckFragment();
				}
			});
			break;
		case FINISH:
			changePhaseButton.setEnabled(false);
			changePhaseButton.setText("");
			break;
		case PLATFORM_GET_OFF:
			changePhaseButton.setEnabled(true);
			changePhaseButton.setText("確認\nする");
			changePhaseButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewDisabler.disable(v);
					showDepartureCheckFragment();
				}
			});
			break;
		case PLATFORM_GET_ON:
			changePhaseButton.setEnabled(true);
			changePhaseButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewDisabler.disable(v);
					showDepartureCheckFragment();
				}
			});
			// changePhaseButton.setText("乗車\n完了");
			changePhaseButton.setText("確認\nする");
			break;
		}
	}

	@Override
	public void onUpdateOperation(Operation operation) {
		setState(new State(operation));
		updateView(operation.getPhase(), !OperationSchedule.getRelative(operation.operationSchedules, 1)
				.isPresent());
	}

	@Override
	protected Integer getOperationSchedulesReceiveSequence() {
		return getState().getOperation().operationScheduleReceiveSequence;
	}
}
