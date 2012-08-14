package com.kogasoftware.odt.invehicledevice.ui.phaseview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter.ItemType;
import com.kogasoftware.odt.invehicledevice.ui.modalview.MemoModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class PlatformPhaseView extends PhaseView {
	private static final String TAG = PlatformPhaseView.class.getSimpleName();
	private static final long UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS = 3;
	private final ListView reservationListView;
	private final TextView nowPlatformNameTextView;
	private final ToggleButton showAllRidingReservationsButton;
	private final ToggleButton showFutureReservationsButton;
	private final ToggleButton showMissedReservationsButton;
	// private final Button addUnexpectedReservationButton;
	private final View reservationListFooterView;
	private final TextView minutesRemainingTextView;
	private final LinearLayout lastOperationScheduleLayout;
	private final LinearLayout nextOperationScheduleLayout;
	private final MemoModalView memoModalView;
	private final Handler handler = new Handler();
	private Optional<AlertDialog> dialog = Optional.absent();
	private Integer lastMinutesRemaining = Integer.MAX_VALUE;
	private final Runnable updateMinutesRemaining = new Runnable() {
		@Override
		public void run() {
			handler.postDelayed(updateMinutesRemaining,
					UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS);
			Date now = InVehicleDeviceService.getDate();
			minutesRemainingTextView.setText("");
			for (OperationSchedule operationSchedule : service
					.getCurrentOperationSchedule().asSet()) {
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
				if (service.getPhase().equals(Phase.PLATFORM)
						&& service.getRemainingOperationSchedules().size() > 1
						&& lastMinutesRemaining >= 3 && minutesRemaining == 2) {
					service.speak("あと2分で出発時刻です");
				}
				lastMinutesRemaining = minutesRemaining;
			}
		}
	};

	public PlatformPhaseView(Context context, InVehicleDeviceService service,
			MemoModalView memoModalView) {
		super(context, service);
		setContentView(R.layout.platform_phase_view);

		this.memoModalView = memoModalView;
		nowPlatformNameTextView = (TextView) findViewById(R.id.now_platform_text_view);
		reservationListView = ((FlickUnneededListView) findViewById(R.id.reservation_list_view))
				.getListView();
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		reservationListFooterView = layoutInflater.inflate(
				R.layout.reservation_list_footer, null);
		showAllRidingReservationsButton = (ToggleButton) reservationListFooterView
				.findViewById(R.id.show_all_riding_reservations_button);
		showFutureReservationsButton = (ToggleButton) reservationListFooterView
				.findViewById(R.id.show_future_reservations_button);
		showMissedReservationsButton = (ToggleButton) reservationListFooterView
				.findViewById(R.id.show_missed_reservations_button);
		// addUnexpectedReservationButton = (Button) reservationListFooterView
		// .findViewById(R.id.add_unexpected_reservation_button);
		minutesRemainingTextView = (TextView) findViewById(R.id.minutes_remaining_text_view);
		lastOperationScheduleLayout = (LinearLayout) findViewById(R.id.last_operation_schedule_layout);
		nextOperationScheduleLayout = (LinearLayout) findViewById(R.id.next_operation_schedule_layout);

		// reservationListView.addFooterView(reservationListFooterView);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		handler.post(updateMinutesRemaining);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		handler.removeCallbacks(updateMinutesRemaining);
		if (dialog.isPresent()) {
			dialog.get().cancel();
		}
	}

	@Override
	public void onEnterDrivePhase() {
		super.onEnterDrivePhase();
	}

	@Override
	public void onEnterFinishPhase() {
		super.onEnterFinishPhase();
	}

	@Override
	public void onEnterPlatformPhase() {
		List<OperationSchedule> operationSchedules = service
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			service.enterFinishPhase();
			return;
		}

		Boolean last = (operationSchedules.size() <= 1);
		if (last) {
			nextOperationScheduleLayout.setVisibility(GONE);
			lastOperationScheduleLayout.setVisibility(VISIBLE);
			showFutureReservationsButton.setVisibility(INVISIBLE);
			showMissedReservationsButton.setVisibility(INVISIBLE);
			// addUnexpectedReservationButton.setVisibility(GONE);
		} else {
			nextOperationScheduleLayout.setVisibility(VISIBLE);
			lastOperationScheduleLayout.setVisibility(GONE);
			showFutureReservationsButton.setVisibility(VISIBLE);
			showMissedReservationsButton.setVisibility(VISIBLE);
			// addUnexpectedReservationButton.setVisibility(VISIBLE);
		}

		final PassengerRecordArrayAdapter adapter = new PassengerRecordArrayAdapter(
				service, memoModalView);
		reservationListView.setAdapter(adapter);

		if (operationSchedules.size() > 0) {
			OperationSchedule nowOperationSchedule = operationSchedules.get(0);
			for (Platform platform : nowOperationSchedule.getPlatform().asSet()) {
				nowPlatformNameTextView.setText(Html.fromHtml(String
						.format(getResources().getString(
								R.string.now_platform_is_html),
								platform.getName())));
			}
		} else {
			nowPlatformNameTextView.setText("");
		}

		showAllRidingReservationsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							adapter.show(ItemType.RIDING_AND_NO_GET_OFF);
						} else {
							adapter.hide(ItemType.RIDING_AND_NO_GET_OFF);
						}
					}
				});
		showAllRidingReservationsButton.setChecked(last);

		showFutureReservationsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							adapter.show(ItemType.FUTURE_GET_ON);
						} else {
							adapter.hide(ItemType.FUTURE_GET_ON);
						}
					}
				});
		showFutureReservationsButton.setChecked(false);

		showMissedReservationsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							adapter.show(ItemType.MISSED);
						} else {
							adapter.hide(ItemType.MISSED);
						}
					}
				});
		showMissedReservationsButton.setChecked(false);
		setVisibility(View.VISIBLE);
	}
}
