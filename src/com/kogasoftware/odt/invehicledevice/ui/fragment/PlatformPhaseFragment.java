package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PlatformPhaseFragment.State;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Platform;

public class PlatformPhaseFragment extends ApplicationFragment<State> implements
		EventDispatcher.OnUpdatePhaseListener {

	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final List<OperationSchedule> operationSchedules;
		private final List<PassengerRecord> passengerRecords;
		private final Phase phase;

		public State(Phase phase, List<OperationSchedule> operationSchedules,
				List<PassengerRecord> passengerRecords) {
			this.phase = phase;
			this.operationSchedules = Lists.newArrayList(operationSchedules);
			this.passengerRecords = Lists.newArrayList(passengerRecords);
		}

		public List<OperationSchedule> getOperationSchedules() {
			return Lists.newArrayList(operationSchedules);
		}

		public List<PassengerRecord> getPassengerRecords() {
			return Lists.newArrayList(passengerRecords);
		}

		public Phase getPhase() {
			return phase;
		}
	}

	public static Fragment newInstance(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		return newInstance(new PlatformPhaseFragment(), new State(phase,
				operationSchedules, passengerRecords));
	}

	private static final Integer BLINK_MILLIS = 500;
	private static final Integer UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS = 2000;
	private static final String TAG = PlatformPhaseFragment.class
			.getSimpleName();
	private Handler handler;
	private TextView minutesRemainingTextView;
	private Integer lastMinutesRemaining = Integer.MAX_VALUE;

	private Runnable blink = new Runnable() {
		@Override
		public void run() {
			adapter.toggleBlink();
			handler.postDelayed(this, BLINK_MILLIS);
		}
	};

	private final Runnable updateMinutesRemaining = new Runnable() {
		@Override
		public void run() {
			handler.postDelayed(updateMinutesRemaining,
					UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS);
			Date now = InVehicleDeviceService.getDate();
			minutesRemainingTextView.setText("");
			for (OperationSchedule operationSchedule : OperationSchedule
					.getCurrentOperationSchedule(
							getState().getOperationSchedules()).asSet()) {
				if (!operationSchedule.getDepartureEstimate().isPresent()) {
					return;
				}
				Date departureEstimate = operationSchedule
						.getDepartureEstimate().get();
				Integer minutesRemaining = (int) (departureEstimate.getTime() / 1000 / 60 - now
						.getTime() / 1000 / 60);
				DateFormat dateFormat = new SimpleDateFormat("HH:mm");
				String dateString = dateFormat.format(departureEstimate);
				minutesRemainingTextView.setText(Html.fromHtml(String.format(
						getResources().getString(
								R.string.minutes_remaining_to_depart_html),
						dateString, minutesRemaining)));
				if (lastMinutesRemaining >= 3 && minutesRemaining == 2) {
					getService().speak("あと2分で出発時刻です");
				}
			}
		}
	};
	private PassengerRecordArrayAdapter adapter;

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
		minutesRemainingTextView = (TextView) getView().findViewById(
				R.id.minutes_remaining_text_view);

		updateView(getView());
		getService().getEventDispatcher().addOnUpdatePhaseListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
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
		getService().getEventDispatcher().removeOnUpdatePhaseListener(this);
	}

	private void updateView(View view) {
		TextView currentPlatformNameTextView = (TextView) view
				.findViewById(R.id.now_platform_text_view);
		FlickUnneededListView passengerRecordListView = ((FlickUnneededListView) view
				.findViewById(R.id.reservation_list_view));

		Boolean last = !OperationSchedule.getRelativeOperationSchedule(
				getState().getOperationSchedules(), 1).isPresent();
		if (last) {
			minutesRemainingTextView.setVisibility(View.GONE);
		} else {
			minutesRemainingTextView.setVisibility(View.VISIBLE);
		}

		currentPlatformNameTextView.setText("");
		for (OperationSchedule currentOperationSchedule : OperationSchedule
				.getCurrentOperationSchedule(getState().getOperationSchedules())
				.asSet()) {
			if (last) {
				currentPlatformNameTextView.setText("現在最終乗降場です");
			} else {
				for (Platform platform : currentOperationSchedule.getPlatform()
						.asSet()) {
					currentPlatformNameTextView.setText(Html.fromHtml(String
							.format(getResources().getString(
									R.string.now_platform_is_html),
									platform.getName())));
				}
			}

			adapter = new PassengerRecordArrayAdapter(
					getActivity(),
					getService(),
					getFragmentManager(),
					currentOperationSchedule,
					getState().getPhase() == Phase.PLATFORM_GET_OFF ? currentOperationSchedule
							.getGetOffScheduledPassengerRecords(getState()
									.getPassengerRecords())
							: currentOperationSchedule
									.getGetOnScheduledPassengerRecords(getState()
											.getPassengerRecords()));
			ListView listView = new ListView(getActivity());
			listView.setAdapter(adapter);
			handler.removeCallbacks(blink);
			handler.postDelayed(blink, 500);

			passengerRecordListView.replaceListView(listView);
			return;
		}

		// error
		Log.e(TAG, "no current OperationSchedule");
		new OperationScheduleLogic(getService()).requestUpdatePhase();
		hide();
	}

	@Override
	public void onUpdatePhase(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		setState(new State(phase, operationSchedules, passengerRecords));
		if (phase == Phase.PLATFORM_GET_OFF || phase == Phase.PLATFORM_GET_ON) {
			updateView(getView());
		}
	}
}
