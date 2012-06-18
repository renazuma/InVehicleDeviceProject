package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Typeface;
import android.graphics.Color;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class DepartureCheckModalView extends ModalView {

	public static class ShowEvent {
		public final ReservationArrayAdapter reservationArrayAdapter;

		public ShowEvent(ReservationArrayAdapter reservationArrayAdapter) {
			this.reservationArrayAdapter = reservationArrayAdapter;
		}
	}

	private static String getUserName(Reservation reservation) {
		if (reservation.getUser().isPresent()) {
			User user = reservation.getUser().get();
			return user.getLastName() + user.getFirstName();
		} else {
			return "「予約ID: " + reservation.getId() + "」";
		}
	}

	public DepartureCheckModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.departure_check_modal_view);
		setCloseOnClick(R.id.departure_check_close_button);
	}

	@Subscribe
	public void show(ShowEvent event) {
		final ReservationArrayAdapter adapter = new ReservationArrayAdapter(
				getContext(), getCommonLogic());

		Button startButton = (Button) findViewById(R.id.departure_button);
		Button closeButton = (Button) findViewById(R.id.departure_check_close_button);
		TextView titleTextView = (TextView) findViewById(R.id.next_platform_text_view);
		ListView errorReservationListView = ((FlickUnneededListView) findViewById(R.id.error_reservation_list_view))
				.getListView();
		List<String> messages = new LinkedList<String>();
		for (Reservation reservation : adapter.getNoGettingOnReservations()) {
			messages.add(" ※ " + getUserName(reservation) + "様が未乗車です");
		}
		for (Reservation reservation : adapter.getNoGettingOffReservations()) {
			messages.add(" ※ " + getUserName(reservation) + "様が未降車です");
		}
		for (Reservation reservation : adapter.getNoPaymentReservations()) {
			messages.add(" ※ " + getUserName(reservation) + "様が料金未払いです");
		}

		errorReservationListView.setAdapter(new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_list_item_1, messages));

		if (getCommonLogic().getRemainingOperationSchedules().size() <= 1) {
			titleTextView.setText("確定しますか？");
			startButton.setText("確定する");
		} else {

			//次の乗降場表示
			CommonLogic commonLogic = getCommonLogic();
			List<OperationSchedule> operationSchedules = commonLogic
					.getRemainingOperationSchedules();
			if (operationSchedules.isEmpty()) {
				commonLogic.postEvent(new EnterFinishPhaseEvent());
				titleTextView.setText("");
			}

			if (operationSchedules.size() > 1) {
				OperationSchedule nowOperationSchedule = operationSchedules.get(1);
				for (Platform platform : nowOperationSchedule.getPlatform()
						.asSet()) {
					titleTextView.setText(Html.fromHtml(String.format(
							getResources()
									.getString(R.string.next_platform_is_html),
							platform.getName())));
				}
			} else {
				titleTextView.setText("");
			}

			startButton.setText("出発する");
		}

		if (!messages.isEmpty()) {
			closeButton.setTextColor(Color.parseColor("#CC0000"));
			closeButton.setTypeface(Typeface.DEFAULT_BOLD);
		}

		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CommonLogic commonLogic = getCommonLogic();
				commonLogic.postEvent(new EnterDrivePhaseEvent());
				hide();
			}
		});
		super.show();
	}
}
