package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.BackgroundWriter;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.ui.ViewDisabler;
import com.kogasoftware.odt.invehicledevice.ui.fragment.ControlBarFragment.State;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;

public class ControlBarFragment extends ApplicationFragment<State> implements
		EventDispatcher.OnUpdatePhaseListener {

	private static final String TAG = ControlBarFragment.class.getSimpleName();

	@SuppressWarnings("serial")
	protected static class State implements Serializable {
	}

	public static ControlBarFragment newInstance() {
		return newInstance(new ControlBarFragment(), new State());
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

		getService().getEventDispatcher().addOnUpdatePhaseListener(this);
		getService().getLocalStorage().read(
				new BackgroundReader<Pair<Phase, Boolean>>() {
					@Override
					public Pair<Phase, Boolean> readInBackground(
							LocalData localData) {
						Phase phase = OperationScheduleLogic.getPhase(
								localData.operationSchedules,
								localData.passengerRecords,
								localData.completeGetOff);
						Boolean isLast = !OperationSchedule
								.getRelativeOperationSchedule(
										localData.operationSchedules, 1)
								.isPresent();
						return Pair.create(phase, isLast);
					}

					@Override
					public void onRead(Pair<Phase, Boolean> result) {
						updateView(result.first, result.second);
					}
				});
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getService().getEventDispatcher().removeOnUpdatePhaseListener(this);
	}

	public void showNavigationFragment() {
		String tag = "tag:" + NavigationFragment.class.getSimpleName();
		Fragment old = getFragmentManager().findFragmentByTag(tag);
		if (old != null) {
			setCustomAnimation(getFragmentManager().beginTransaction()).remove(
					old).commit();
		}
		getService().getLocalStorage().read(
				new BackgroundReader<Pair<Phase, List<OperationSchedule>>>() {
					@Override
					public Pair<Phase, List<OperationSchedule>> readInBackground(
							LocalData localData) {
						Phase phase = OperationScheduleLogic.getPhase(
								localData.operationSchedules,
								localData.passengerRecords,
								localData.completeGetOff);
						return Pair.create(phase, localData.operationSchedules);
					}

					@Override
					public void onRead(
							Pair<Phase, List<OperationSchedule>> result) {
						setCustomAnimation(
								getFragmentManager().beginTransaction()).add(
								R.id.modal_fragment_container,
								NavigationFragment.newInstance(result.first,
										result.second)).commit();
					}
				});
	}

	public void showOperationScheduleListFragment() {
		getService().getLocalStorage().read(
				new BackgroundReader<List<OperationSchedule>>() {
					@Override
					public List<OperationSchedule> readInBackground(
							LocalData localData) {
						return localData.operationSchedules;
					}

					@Override
					public void onRead(
							List<OperationSchedule> operationSchedules) {
						String tag = "tag:"
								+ OperationScheduleListFragment.class
										.getSimpleName();
						Fragment old = getFragmentManager().findFragmentByTag(
								tag);
						if (old != null) {
							setCustomAnimation(
									getFragmentManager().beginTransaction())
									.remove(old).commit();
						}
						setCustomAnimation(
								getFragmentManager().beginTransaction()).add(
								R.id.modal_fragment_container,
								OperationScheduleListFragment
										.newInstance(operationSchedules))
								.commit();
					}
				});
	}

	public void showArrivalCheckFragment() {
		getService().getLocalStorage().read(
				new BackgroundReader<Optional<OperationSchedule>>() {
					@Override
					public Optional<OperationSchedule> readInBackground(
							LocalData localData) {
						return OperationSchedule
								.getCurrentOperationSchedule(localData.operationSchedules);
					}

					@Override
					public void onRead(Optional<OperationSchedule> result) {
						for (OperationSchedule operationSchedule : result
								.asSet()) {
							String tag = "tag:"
									+ ArrivalCheckFragment.class
											.getSimpleName();
							Fragment old = getFragmentManager()
									.findFragmentByTag(tag);
							if (old != null) {
								setCustomAnimation(
										getFragmentManager().beginTransaction())
										.remove(old).commit();
							}
							setCustomAnimation(
									getFragmentManager().beginTransaction())
									.add(R.id.modal_fragment_container,
											ArrivalCheckFragment
													.newInstance(operationSchedule))
									.commit();
						}
					}
				});
	}

	public void showDepartureCheckFragment() {
		getService()
				.getLocalStorage()
				.read(new BackgroundReader<Pair<Phase, Pair<List<OperationSchedule>, List<PassengerRecord>>>>() {
					@Override
					public Pair<Phase, Pair<List<OperationSchedule>, List<PassengerRecord>>> readInBackground(
							LocalData localData) {
						Phase phase = OperationScheduleLogic.getPhase(
								localData.operationSchedules,
								localData.passengerRecords,
								localData.completeGetOff);
						return Pair.create(phase, Pair.create(
								localData.operationSchedules,
								localData.passengerRecords));
					}

					@Override
					public void onRead(
							Pair<Phase, Pair<List<OperationSchedule>, List<PassengerRecord>>> result) {
						showDepartureCheckFragment(result.first,
								result.second.first, result.second.second);
					}
				});
	}

	public void showDepartureCheckFragment(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		if (!OperationSchedule.getCurrentOperationSchedule(operationSchedules)
				.isPresent()) {
			Log.e(TAG, "current operation schedule not found");
			return;
		}
		OperationSchedule operationSchedule = OperationSchedule
				.getCurrentOperationSchedule(operationSchedules).get();
		final OperationScheduleLogic operationScheduleLogic = new OperationScheduleLogic(
				getService());

		if (phase == Phase.PLATFORM_GET_OFF
				&& operationSchedule.getNoGetOffErrorPassengerRecords(
						passengerRecords).isEmpty()) {
			if (operationSchedule.getGetOnScheduledPassengerRecords(
					passengerRecords).isEmpty()) {
				setCustomAnimation(getFragmentManager().beginTransaction())
						.add(R.id.modal_fragment_container,
								DepartureCheckFragment.newInstance(phase,
										operationSchedules))
						.commitAllowingStateLoss();
			} else {
				getService().getLocalStorage().write(new BackgroundWriter() {
					@Override
					public void writeInBackground(LocalData ld) {
						ld.completeGetOff = true;
					}

					@Override
					public void onWrite() {
						operationScheduleLogic.requestUpdatePhase();
					}
				});
			}
			return;
		} else if (phase == Phase.PLATFORM_GET_ON
				&& operationSchedule.getNoGetOnErrorPassengerRecords(
						passengerRecords).isEmpty()) {
			setCustomAnimation(getFragmentManager().beginTransaction()).add(
					R.id.modal_fragment_container,
					DepartureCheckFragment.newInstance(phase,
							operationSchedules)).commitAllowingStateLoss();
			return;
		}

		// エラーがある場合
		String tag = "tag:"
				+ PassengerRecordErrorFragment.class.getSimpleName();
		Fragment old = getFragmentManager().findFragmentByTag(tag);
		if (old != null) {
			setCustomAnimation(getFragmentManager().beginTransaction()).remove(
					old).commitAllowingStateLoss();
		}
		setCustomAnimation(getFragmentManager().beginTransaction()).add(
				R.id.modal_fragment_container,
				PassengerRecordErrorFragment.newInstance(phase,
						operationSchedules, passengerRecords))
				.commitAllowingStateLoss();
	}

	public void updateView(Phase phase, Boolean isLast) {
		Button changePhaseButton = (Button) getView().findViewById(
				R.id.change_phase_button);
		getView().setBackgroundColor(getPhaseColor(phase));
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
			if (isLast) {
				// changePhaseButton.setText("降車\n完了\n(終)");
				changePhaseButton.setText("確認\nする");
				// changePhaseButton
				// .setText(getString(R.string.it_leaves_last_platform_button_text));
			} else {
				changePhaseButton.setText("確認\nする");
				// changePhaseButton.setText("降車\n完了");
				// changePhaseButton
				// .setText(getString(R.string.it_leaves_button_text));
			}
			// changePhaseButton
			// .setText(getString(R.string.it_leaves_last_platform_button_text));
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
	public void onUpdatePhase(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		updateView(
				phase,
				!OperationSchedule.getRelativeOperationSchedule(
						operationSchedules, 1).isPresent());
	}
}
