package com.kogasoftware.odt.invehicledevice.ui.phaseview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
import com.kogasoftware.odt.invehicledevice.logic.event.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationArrayAdapter.ItemType;
import com.kogasoftware.odt.invehicledevice.ui.modalview.DepartureCheckModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class PlatformPhaseView extends PhaseView {
	public static class DepartureCheckEvent {
	}

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

	private ReservationArrayAdapter adapter = new ReservationArrayAdapter(
			getContext(), getCommonLogic());
	private final Handler handler = new Handler();

	private Optional<AlertDialog> dialog = Optional.absent();
	private Integer lastMinutesRemaining = Integer.MAX_VALUE;
	private final Runnable updateMinutesRemaining = new Runnable() {
		@Override
		public void run() {
			handler.postDelayed(updateMinutesRemaining,
					UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS);
			Date now = CommonLogic.getDate();
			minutesRemainingTextView.setText("");
			for (OperationSchedule operationSchedule : getCommonLogic()
					.getCurrentOperationSchedule().asSet()) {
				Date departureEstimate = operationSchedule
						.getDepartureEstimate();
				Integer minutesRemaining = (int) (departureEstimate.getTime() / 1000 / 60 - now
						.getTime() / 1000 / 60);
				DateFormat dateFormat = new SimpleDateFormat("HH:mm");
				String dateString = dateFormat.format(departureEstimate);
				minutesRemainingTextView.setText(Html.fromHtml(String.format(
						getResources().getString(
								R.string.minutes_remaining_to_depart_html),
						dateString, minutesRemaining)));
				if (lastMinutesRemaining >= 2 && minutesRemaining == 1) {
					getCommonLogic().postEvent(new SpeakEvent("あと1分で出発時刻です"));
				}
				lastMinutesRemaining = minutesRemaining;
			}
		}
	};

	public PlatformPhaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.platform_phase_view);

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

		reservationListView.addFooterView(reservationListFooterView);
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

		adapter = new ReservationArrayAdapter(getContext(), commonLogic);
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

	// 飛び乗り予約ダイアログ表示
	// 飛び乗り予約機能復活時のすぐに参考可能なようにコメントアウトにしておく
	// private void showAddUnexpectedReservationDialog() {
	// if (!isShown()) {
	// return;
	// }
	//
	// final List<OperationSchedule> operationSchedules = getCommonLogic()
	// .getRemainingOperationSchedules();
	// if (operationSchedules.size() <= 1) {
	// Log.w(TAG,
	// "can't add unexpected reservation, !(operationSchedulfes.isEmpty())",
	// new Exception());
	// return;
	// }
	// operationSchedules.remove(operationSchedules.get(0));
	// final String[] operationScheduleSelections = new
	// String[operationSchedules
	// .size()];
	// DateFormat displayDateFormat = new SimpleDateFormat("H時m分");
	// for (Integer i = 0; i < operationScheduleSelections.length; ++i) {
	// OperationSchedule operationSchedule = operationSchedules.get(i);
	// String selection = "";
	// selection += displayDateFormat.format(operationSchedule
	// .getArrivalEstimate()) + "着予定 / ";
	//
	// if (operationSchedule.getPlatform().isPresent()) {
	// selection += operationSchedule.getPlatform().get().getName();
	// } else {
	// // TODO
	// selection += "Operation Schedule ID: "
	// + operationSchedules.get(i).getId();
	// }
	// operationScheduleSelections[i] = selection;
	// }
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
	// builder.setTitle("降車予定の乗降場を選択してください");
	// builder.setItems(operationScheduleSelections,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// dialog.dismiss();
	// if (which >= operationSchedules.size()) {
	// // TODO warning
	// return;
	// }
	// OperationSchedule arrivalOperationSchedule = operationSchedules
	// .get(which);
	// getCommonLogic().postEvent(
	// new UnexpectedReservationAddedEvent(
	// arrivalOperationSchedule.getId()));
	// }
	// });
	// dialog = Optional.of(builder.create());
	// dialog.get().show();
	// }

	@Subscribe
	public void showDepartureCheckModalView(DepartureCheckEvent e) {
		getCommonLogic().postEvent(
				new DepartureCheckModalView.ShowEvent(adapter));
	}
}
