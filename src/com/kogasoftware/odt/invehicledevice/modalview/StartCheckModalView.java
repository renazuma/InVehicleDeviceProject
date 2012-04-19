package com.kogasoftware.odt.invehicledevice.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.logic.Logic;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class StartCheckModalView extends ModalView {
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
			return "「ID: " + reservation.getId() + "」";
		}
	}

	public StartCheckModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.start_check_modal_view);
	}

	@Subscribe
	public void show(ShowEvent event) {
		final ReservationArrayAdapter adapter = event.reservationArrayAdapter;
		ListView errorReservationListView = (ListView) findViewById(R.id.error_reservation_list_view);
		List<String> messages = new LinkedList<String>();
		for (Reservation reservation : adapter.getNoGettingOnReservations()) {
			messages.add(getUserName(reservation) + "様が未乗車です");
		}
		for (Reservation reservation : adapter.getNoGettingOutReservations()) {
			messages.add(getUserName(reservation) + "様が未降車です");
		}
		for (Reservation reservation : adapter.getNoPaymentReservations()) {
			messages.add(getUserName(reservation) + "様が料金未払いです");
		}

		errorReservationListView.setAdapter(new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_list_item_1, messages));

		Button startButton = (Button) findViewById(R.id.start_button);
		if (getLogic().getRemainingOperationSchedules().size() <= 1) {
			startButton.setText("確定する");
		} else {
			startButton.setText("出発する");
		}
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Logic logic = getLogic();
				logic.getOnReservation(adapter.getCheckedIncomingReservations());
				logic.getOutReservation(adapter
						.getCheckedOutgoingReservations());
				logic.addMissedReservations(adapter
						.getNoGettingOnReservations());
				logic.enterDrivePhase();
			}
		});
		super.show();
	}
}
