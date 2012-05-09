package com.kogasoftware.odt.invehicledevice.ui.phaseview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UnexpectedReservationAddedEvent;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter.ItemType;
import com.kogasoftware.odt.invehicledevice.ui.modalview.StartCheckModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class PlatformPhaseView extends PhaseView {
	public static class StartCheckEvent {
	}

	private static final String TAG = PlatformPhaseView.class.getSimpleName();
	private static final long UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS = 5;
	private final TextView platformDepartureTimeTextView;
	private final ListView reservationListView;
	private final TextView platformNameTextView;
	private final ToggleButton showAllRidingReservationsButton;
	private final ToggleButton showFutureReservationsButton;
	private final ToggleButton showMissedReservationsButton;
	private final Button addUnexpectedReservationButton;
	private final View reservationListFooterView;
	private final Button reservationScrollDownButton;
	private final Button reservationScrollUpButton;
	private final TextView minutesRemainingTextView;
	private final LinearLayout lastOperationScheduleLayout;
	private final LinearLayout nextOperationScheduleLayout;

	private PassengerRecordArrayAdapter adapter = new PassengerRecordArrayAdapter(
			getContext(), getCommonLogic());
	private final Handler handler = new Handler();

	private Optional<AlertDialog> dialog = Optional.absent();

	private final Runnable updateMinutesRemaining = new Runnable() {
		@Override
		public void run() {
			Date now = CommonLogic.getDate();
			List<OperationSchedule> operationSchedules = getCommonLogic()
					.getRemainingOperationSchedules();
			if (operationSchedules.size() <= 1) {
				minutesRemainingTextView.setText("");
			} else {
				Date departure = operationSchedules.get(0)
						.getDepartureEstimate();
				Long milliGap = departure.getTime() - now.getTime();
				minutesRemainingTextView.setText("" + (milliGap / 1000 / 60));
			}
			handler.postDelayed(updateMinutesRemaining,
					UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS);
		}
	};

	public PlatformPhaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.platform_phase_view);

		reservationScrollUpButton = (Button) findViewById(R.id.reservation_scroll_up_button);
		reservationScrollDownButton = (Button) findViewById(R.id.reservation_scroll_down_button);
		platformNameTextView = (TextView) findViewById(R.id.platform_name_text_view);
		platformDepartureTimeTextView = (TextView) findViewById(R.id.platform_departure_time_text_view);
		reservationListView = (ListView) findViewById(R.id.reservation_list_view);
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
		addUnexpectedReservationButton = (Button) reservationListFooterView
				.findViewById(R.id.add_unexpected_reservation_button);
		minutesRemainingTextView = (TextView) findViewById(R.id.minutes_remaining);
		lastOperationScheduleLayout = (LinearLayout) findViewById(R.id.last_operation_schedule_layout);
		nextOperationScheduleLayout = (LinearLayout) findViewById(R.id.next_operation_schedule_layout);

		reservationListView.addFooterView(reservationListFooterView);

		reservationScrollUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = reservationListView
						.getFirstVisiblePosition();
				reservationListView.smoothScrollToPosition(position);
			}
		});
		reservationScrollDownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Integer position = reservationListView.getLastVisiblePosition();
				reservationListView.smoothScrollToPosition(position);
			}
		});

	}

	@Subscribe
	public void addUnexpectedReservation(UnexpectedReservationAddedEvent event) {
		adapter.addUnexpectedReservation();
	}

	@Override
	public void enterPlatformPhase(EnterPlatformPhaseEvent event) {
		CommonLogic commonLogic = getCommonLogic();
		List<OperationSchedule> operationSchedules = commonLogic
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			commonLogic.postEvent(new EnterFinishPhaseEvent());
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		if (!operationSchedule.getPlatform().isPresent()) {
			return;
		}

		Boolean last = (operationSchedules.size() <= 1);
		if (last) {
			nextOperationScheduleLayout.setVisibility(GONE);
			lastOperationScheduleLayout.setVisibility(VISIBLE);
			showFutureReservationsButton.setVisibility(INVISIBLE);
			showMissedReservationsButton.setVisibility(INVISIBLE);
			addUnexpectedReservationButton.setVisibility(GONE);
		} else {
			nextOperationScheduleLayout.setVisibility(VISIBLE);
			lastOperationScheduleLayout.setVisibility(GONE);
			showFutureReservationsButton.setVisibility(VISIBLE);
			showMissedReservationsButton.setVisibility(VISIBLE);
			addUnexpectedReservationButton.setVisibility(VISIBLE);
		}

		adapter = new PassengerRecordArrayAdapter(getContext(), commonLogic);
		reservationListView.setAdapter(adapter);

		if (operationSchedules.size() > 1) {
			DateFormat dateFormat = new SimpleDateFormat("H時m分"); // TODO
			platformDepartureTimeTextView.setText(dateFormat
					.format(operationSchedule.getDepartureEstimate()));
			OperationSchedule nextOperationSchedule = operationSchedules.get(1);
			if (nextOperationSchedule.getPlatform().isPresent()) {
				platformNameTextView.setText(nextOperationSchedule
						.getPlatform().get().getName());
			} else {
				platformNameTextView.setText("「ID: "
						+ nextOperationSchedule.getId() + "」");
			}
		} else {
			platformNameTextView.setText("");
			platformDepartureTimeTextView.setText("");
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
		addUnexpectedReservationButton
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						showAddUnexpectedReservationDialog();
					}
				});
		setVisibility(View.VISIBLE);
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

	private void showAddUnexpectedReservationDialog() {
		if (!isShown()) {
			return;
		}

		final List<OperationSchedule> operationSchedules = getCommonLogic()
				.getRemainingOperationSchedules();
		if (operationSchedules.size() <= 1) {
			Log.w(TAG,
					"can't add unexpected reservation, !(operationSchedulfes.isEmpty())",
					new Exception());
			return;
		}
		operationSchedules.remove(operationSchedules.get(0));
		final String[] operationScheduleSelections = new String[operationSchedules
				.size()];
		DateFormat displayDateFormat = new SimpleDateFormat("H時m分");
		for (Integer i = 0; i < operationScheduleSelections.length; ++i) {
			OperationSchedule operationSchedule = operationSchedules.get(i);
			String selection = "";
			selection += displayDateFormat.format(operationSchedule
					.getArrivalEstimate()) + "着予定 / ";

			if (operationSchedule.getPlatform().isPresent()) {
				selection += operationSchedule.getPlatform().get().getName();
			} else {
				// TODO
				selection += "Operation Schedule ID: "
						+ operationSchedules.get(i).getId();
			}
			operationScheduleSelections[i] = selection;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle("降車予定の乗降場を選択してください");
		builder.setItems(operationScheduleSelections,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if (which >= operationSchedules.size()) {
							// TODO warning
							return;
						}
						OperationSchedule arrivalOperationSchedule = operationSchedules
								.get(which);
						getCommonLogic().postEvent(
								new UnexpectedReservationAddedEvent(
										arrivalOperationSchedule.getId()));
					}
				});
		dialog = Optional.of(builder.create());
		dialog.get().show();
	}

	@Subscribe
	public void showStartCheckModalView(StartCheckEvent e) {
		getCommonLogic().postEvent(new StartCheckModalView.ShowEvent(adapter));
	}
}
