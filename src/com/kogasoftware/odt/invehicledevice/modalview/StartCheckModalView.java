package com.kogasoftware.odt.invehicledevice.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.logic.Logic;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class StartCheckModalView extends ModalView {
	public static class ShowEvent {
		public final ReservationArrayAdapter reservationArrayAdapter;

		public ShowEvent(ReservationArrayAdapter reservationArrayAdapter) {
			this.reservationArrayAdapter = reservationArrayAdapter;
		}
	}

	private static Optional<String> getUserName(PassengerRecord passengerRecord) {
		if (!passengerRecord.getReservation().isPresent()) {
			return Optional.absent();
		}
		Reservation reservation = passengerRecord.getReservation().get();
		if (reservation.getUser().isPresent()) {
			User user = reservation.getUser().get();
			return Optional.of(user.getLastName() + user.getFirstName());
		} else {
			return Optional.of("「予約ID: " + reservation.getId() + "」");
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
		for (PassengerRecord passengerRecord : adapter
				.getNoGettingOnPassengerRecords()) {
			Optional<String> userName = getUserName(passengerRecord);
			if (userName.isPresent()) {
				messages.add(userName.get() + "様が未乗車です");
			}
		}
		for (PassengerRecord passengerRecord : adapter
				.getNoGettingOffPassengerRecords()) {
			Optional<String> userName = getUserName(passengerRecord);
			if (userName.isPresent()) {
				messages.add(userName.get() + "様が未降車です");
			}
		}
		for (PassengerRecord passengerRecord : adapter
				.getNoPaymentReservations()) {
			Optional<String> userName = getUserName(passengerRecord);
			if (userName.isPresent()) {
				messages.add(userName.get() + "様が料金未払いです");
			}
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
				Optional<OperationSchedule> operationSchedule = logic
						.getCurrentOperationSchedule();
				if (operationSchedule.isPresent()) {
					logic.getOnPassengerRecords(operationSchedule.get(),
							adapter.getSelectedGetOnPassengerRecords());
					logic.getOffPassengerRecords(operationSchedule.get(),
							adapter.getSelectedGetOffPassengerRecords());
				}
				logic.enterDrivePhase();
			}
		});
		super.show();
	}
}
