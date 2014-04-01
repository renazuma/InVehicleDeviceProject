package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTimeUtils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher.OnUpdatePassengerRecordListener;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PlatformPhaseFragment.State;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;

public class PlatformPhaseFragment extends AutoUpdateOperationFragment<State> implements OnUpdatePassengerRecordListener {

	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final Operation operation;

		public State(Operation operation) {
			this.operation = operation;
		}

		public List<OperationSchedule> getOperationSchedules() {
			return Lists.newArrayList(operation.operationSchedules);
		}

		public List<PassengerRecord> getPassengerRecords() {
			return Lists.newArrayList(operation.passengerRecords);
		}

		public Phase getPhase() {
			return operation.getPhase();
		}

		public Operation getOperation() {
			return operation;
		}
	}

	public static Fragment newInstance(Operation operation) {
		return newInstance(new PlatformPhaseFragment(), new State(operation));
	}

	private static final Integer BLINK_MILLIS = 500;
	private static final Integer UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS = 2000;
	private static final String TAG = PlatformPhaseFragment.class
			.getSimpleName();
	private Handler handler;
	private TextView minutesRemainingTextView;
	private TextView currentPlatformNameTextView;
	private FlickUnneededListView passengerRecordListView;
	private Integer lastMinutesRemaining = Integer.MAX_VALUE;
	private Optional<PassengerRecordArrayAdapter> optionalAdapter = Optional.absent();

	private Runnable blink = new Runnable() {
		@Override
		public void run() {
			for (PassengerRecordArrayAdapter adapter : optionalAdapter.asSet()) {
				adapter.toggleBlink();
			}
			handler.postDelayed(this, BLINK_MILLIS);
		}
	};

	private final Runnable updateMinutesRemaining = new Runnable() {
		@Override
		public void run() {
			handler.postDelayed(updateMinutesRemaining,
					UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS);
			if (!OperationSchedule.getRelative(
					getState().getOperationSchedules(), 1).isPresent()) {
				return;
			}
			Date now = new Date(DateTimeUtils.currentTimeMillis());
			minutesRemainingTextView.setText("");
			for (OperationSchedule operationSchedule : OperationSchedule
					.getCurrent(
							getState().getOperationSchedules()).asSet()) {
				if (!operationSchedule.getDepartureEstimate().isPresent()) {
					return;
				}
				Date departureEstimate = operationSchedule
						.getDepartureEstimate().get();
				Integer minutesRemaining = (int) (departureEstimate.getTime() / 1000 / 60 - now
						.getTime() / 1000 / 60);
				DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
				String dateString = dateFormat.format(departureEstimate);
				minutesRemainingTextView.setText(Html.fromHtml(String.format(
						getResources().getString(
								R.string.minutes_remaining_to_depart_html),
						dateString, minutesRemaining)));
				if (lastMinutesRemaining >= 3 && minutesRemaining < 3) {
					String message = minutesRemaining <= 0 ? "出発時刻になりました"
							: String.format(Locale.JAPAN, "あと%d分で出発時刻です",
									minutesRemaining);
					getService().speak(message);
				}
				lastMinutesRemaining = minutesRemaining;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.platform_phase_fragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler = new Handler();
		View view = getView();
		minutesRemainingTextView = (TextView) view.findViewById(
				R.id.minutes_remaining_text_view);
		currentPlatformNameTextView = (TextView) view
				.findViewById(R.id.now_platform_text_view);
		passengerRecordListView = ((FlickUnneededListView) view
				.findViewById(R.id.reservation_list_view));
		updateView(view);
		getService().getEventDispatcher().addOnUpdatePassengerRecordListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "platform: " + Objects.firstNonNull(currentPlatformNameTextView.getText(), "(None)"));
		handler.post(updateMinutesRemaining);
	}

	@Override
	public void onPause() {
		super.onPause();
		handler.removeCallbacks(updateMinutesRemaining);
		handler.removeCallbacks(blink);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getService().getEventDispatcher().removeOnUpdatePassengerRecordListener(this);
	}

	private void updateView(View view) {
		Log.i(TAG, "updateView");
		Boolean last = !OperationSchedule.getRelative(
				getState().getOperationSchedules(), 1).isPresent();
		if (last) {
			minutesRemainingTextView.setVisibility(View.GONE);
		} else {
			minutesRemainingTextView.setVisibility(View.VISIBLE);
		}

		currentPlatformNameTextView.setText("");
		for (OperationSchedule currentOperationSchedule : OperationSchedule
				.getCurrent(getState().getOperationSchedules())
				.asSet()) {
			if (last) {
				currentPlatformNameTextView.setText("現在最終乗降場です");
				Log.i(TAG, "last platform");
			} else {
				for (Platform platform : currentOperationSchedule.getPlatform()
						.asSet()) {
					Log.i(TAG, "platform id=" + platform.getId() + " name=" + platform.getName());
					currentPlatformNameTextView.setText(Html.fromHtml(String
							.format(getResources().getString(
									R.string.now_platform_is_html),
									platform.getName())));
				}
			}

			for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
				PassengerRecordArrayAdapter adapter = new PassengerRecordArrayAdapter(
						getActivity(),
						getService(),
						fragmentManager,
						currentOperationSchedule,
						getState().getPhase() == Phase.PLATFORM_GET_OFF ? currentOperationSchedule
								.getGetOffScheduledPassengerRecords(getState()
										.getPassengerRecords())
								: currentOperationSchedule
										.getGetOnScheduledPassengerRecords(getState()
												.getPassengerRecords()));
				optionalAdapter = Optional.of(adapter);
				ListView listView = new ListView(getActivity());
				listView.setAdapter(adapter);
				handler.removeCallbacks(blink);
				handler.postDelayed(blink, 500);

				passengerRecordListView.replaceListView(listView);
				return;
			}
		}

		// error
		Log.e(TAG, "no current OperationSchedule");
		new OperationScheduleLogic(getService()).requestUpdateOperation();
		hide();
	}

	@Override
	public void onUpdateOperation(Operation operation) {
		setState(new State(operation));
		Phase phase = operation.getPhase();
		if (phase == Phase.PLATFORM_GET_OFF || phase == Phase.PLATFORM_GET_ON) {
			updateView(getView());
		}
	}

	@Override
	protected Integer getOperationSchedulesReceiveSequence() {
		return getState().getOperation().operationScheduleReceiveSequence;
	}

	@Override
	public void onUpdatePassengerRecord(PassengerRecord passengerRecord) {
		for (PassengerRecordArrayAdapter adapter : optionalAdapter.asSet()) {
			adapter.updatePassengerRecord(passengerRecord);
		}
	}
}
