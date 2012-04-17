package com.kogasoftware.odt.invehicledevice.phaseview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.event.AddUnexpectedReservationEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.Logic;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class PlatformPhaseView extends PhaseView {
	public static class StartCheckEvent {
	}

	private static final String TAG = PlatformPhaseView.class.getSimpleName();
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
	private ReservationArrayAdapter adapter = new ReservationArrayAdapter(
			getContext(), R.layout.reservation_list_row, getLogic());

	private Optional<AlertDialog> dialog = Optional.<AlertDialog> absent();

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
	public void addUnexpectedReservation(AddUnexpectedReservationEvent event) {
		adapter.addUnexpectedReservation(event.reservation);
	}

	@Override
	@Subscribe
	public void enterPlatformPhase(EnterPlatformPhaseEvent event) {
		Logic logic = getLogic();
		List<OperationSchedule> operationSchedules = logic
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			logic.enterFinishPhase();
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);
		if (!operationSchedule.getPlatform().isPresent()) {
			return;
		}
		DateFormat dateFormat = new SimpleDateFormat("H時m分"); // TODO
		platformDepartureTimeTextView.setText(dateFormat
				.format(operationSchedule.getDepartureEstimate()));

		adapter = new ReservationArrayAdapter(getContext(),
				R.layout.reservation_list_row, logic);
		reservationListView.setAdapter(adapter);

		if (operationSchedules.size() > 1) {
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
		}

		showAllRidingReservationsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							adapter.showRidingAndNoOutgoingReservations();
						} else {
							adapter.hideRidingAndNotGetOutReservations();
						}
					}
				});
		showAllRidingReservationsButton.setChecked(false);

		showFutureReservationsButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							adapter.showFutureReservations();
						} else {
							adapter.hideFutureReservations();
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
							adapter.showMissedReservations();
						} else {
							adapter.hideMissedReservations();
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
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (dialog.isPresent()) {
			dialog.get().cancel();
		}
	}

	private void showAddUnexpectedReservationDialog() {
		if (!isShown()) {
			return;
		}

		final List<OperationSchedule> operationSchedules = getLogic()
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
		for (Integer i = 0; i < operationScheduleSelections.length; ++i) {
			OperationSchedule operationSchedule = operationSchedules.get(i);
			String selection = "";
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
						getLogic().addUnexpectedReservation(
								arrivalOperationSchedule.getId());
					}
				});
		dialog = Optional.of(builder.create());
		dialog.get().show();
	}

	@Subscribe
	public void showStartCheckModalView(StartCheckEvent e) {
		getLogic().showStartCheckModalView(adapter);
	}
}
