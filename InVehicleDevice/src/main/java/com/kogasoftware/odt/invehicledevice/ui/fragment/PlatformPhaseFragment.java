package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
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
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.contentprovider.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceService;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;

public class PlatformPhaseFragment extends Fragment
		implements
			LoaderCallbacks<Cursor> {
	private static final String OPERATION_SCHEDULES_KEY = "operation_schedules";

	public static PlatformPhaseFragment newInstance(
			LinkedList<OperationSchedule> operationSchedules) {
		PlatformPhaseFragment fragment = new PlatformPhaseFragment();
		Bundle args = new Bundle();
		args.putSerializable(OPERATION_SCHEDULES_KEY, operationSchedules);
		fragment.setArguments(args);
		return fragment;
	}

	private static final Integer BLINK_MILLIS = 500;
	private static final Integer UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS = 2000;
	private static final String TAG = PlatformPhaseFragment.class
			.getSimpleName();
	private static final int LOADER_ID = 1;
	private Handler handler;
	private TextView minutesRemainingTextView;
	private TextView currentPlatformNameTextView;
	private FlickUnneededListView passengerRecordListView;
	private Integer lastMinutesRemaining = Integer.MAX_VALUE;
	private PassengerRecordArrayAdapter adapter;

	private Runnable blink = new Runnable() {
		@Override
		public void run() {
			if (adapter != null) {
				adapter.toggleBlink();
			}
			handler.postDelayed(this, BLINK_MILLIS);
		}
	};

	private List<OperationSchedule> operationSchedules;

	private final Runnable updateMinutesRemaining = new Runnable() {
		@Override
		public void run() {
			handler.postDelayed(updateMinutesRemaining,
					UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS);
			OperationSchedule nextOperationSchedule = OperationSchedule
					.getCurrentOffset(operationSchedules, 1);
			if (nextOperationSchedule == null) {
				return;
			}
			DateTime now = DateTime.now();
			minutesRemainingTextView.setText("");
			OperationSchedule operationSchedule = OperationSchedule
					.getCurrent(operationSchedules);
			if (operationSchedule.departureEstimate == null) {
				return;
			}
			Integer minutesRemaining = (int) (operationSchedule.departureEstimate
					.getMillis() / 1000 / 60 - now.getMillis() / 1000 / 60);
			DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
			String dateString = dateFormat
					.format(operationSchedule.departureEstimate.toDate());
			minutesRemainingTextView.setText(Html.fromHtml(String.format(
					getResources().getString(
							R.string.minutes_remaining_to_depart_html),
					dateString, minutesRemaining)));
			if (lastMinutesRemaining >= 3 && minutesRemaining < 3) {
				String message = minutesRemaining <= 0 ? "出発時刻になりました" : String
						.format(Locale.JAPAN, "あと%d分で出発時刻です", minutesRemaining);
				VoiceService.speak(getActivity(), message);
			}
			lastMinutesRemaining = minutesRemaining;
		}
	};
	private final LinkedList<PassengerRecord> passengerRecords = Lists
			.newLinkedList();

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
		Bundle args = getArguments();
		operationSchedules = (LinkedList<OperationSchedule>) args
				.getSerializable(OPERATION_SCHEDULES_KEY);
		View view = getView();
		minutesRemainingTextView = (TextView) view
				.findViewById(R.id.minutes_remaining_text_view);
		currentPlatformNameTextView = (TextView) view
				.findViewById(R.id.now_platform_text_view);
		passengerRecordListView = ((FlickUnneededListView) view
				.findViewById(R.id.reservation_list_view));
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getLoaderManager().destroyLoader(LOADER_ID);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG,
				"platform: "
						+ Objects.firstNonNull(
								currentPlatformNameTextView.getText(), "(None)"));
		handler.post(updateMinutesRemaining);
	}

	@Override
	public void onPause() {
		super.onPause();
		handler.removeCallbacks(updateMinutesRemaining);
		handler.removeCallbacks(blink);
	}

	private void updateView() {
		Log.i(TAG, "updateView");
		Boolean last = OperationSchedule
				.getCurrentOffset(operationSchedules, 1) == null;
		if (last) {
			minutesRemainingTextView.setVisibility(View.GONE);
		} else {
			minutesRemainingTextView.setVisibility(View.VISIBLE);
		}

		currentPlatformNameTextView.setText("");
		OperationSchedule currentOperationSchedule = OperationSchedule
				.getCurrent(operationSchedules);
		if (last) {
			currentPlatformNameTextView.setText("現在最終乗降場です");
			Log.i(TAG, "last platform");
		} else {
			Log.i(TAG, "platform id=" + currentOperationSchedule.platformId
					+ " name=" + currentOperationSchedule.name);
			currentPlatformNameTextView.setText(Html.fromHtml(String.format(
					getResources().getString(R.string.now_platform_is_html),
					currentOperationSchedule.name)));
		}
		Phase phase = OperationSchedule.getPhase(operationSchedules,
				passengerRecords);
		adapter = new PassengerRecordArrayAdapter(
				getActivity(),
				currentOperationSchedule,
				phase == Phase.PLATFORM_GET_OFF
						? currentOperationSchedule
								.getGetOffScheduledPassengerRecords(passengerRecords)
						: currentOperationSchedule
								.getGetOnScheduledPassengerRecords(passengerRecords));
		ListView listView = new ListView(getActivity());
		listView.setAdapter(adapter);
		handler.removeCallbacks(blink);
		handler.postDelayed(blink, 500);

		passengerRecordListView.replaceListView(listView);
		return;
	}
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), PassengerRecords.CONTENT.URI,
				null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		passengerRecords.clear();
		passengerRecords.addAll(PassengerRecord.getAll(cursor));
		updateView();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}
}
