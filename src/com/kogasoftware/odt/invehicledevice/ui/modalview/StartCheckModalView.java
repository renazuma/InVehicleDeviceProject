package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.GetOffEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.GetOnEvent;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
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
			return "「予約ID: " + reservation.getId() + "」";
		}
	}

	public StartCheckModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.start_check_modal_view);
		setCloseOnClick(R.id.start_check_close_button);
	}

	@Subscribe
	public void show(ShowEvent event) {
		final ReservationArrayAdapter adapter = event.reservationArrayAdapter;
		ListView errorReservationListView = (ListView) findViewById(R.id.error_reservation_list_view);
		List<String> messages = new LinkedList<String>();
		for (Reservation reservation : adapter.getNoGettingOnReservations()) {
			messages.add(getUserName(reservation) + "様が未乗車です");
		}
		for (Reservation reservation : adapter.getNoGettingOffReservations()) {
			messages.add(getUserName(reservation) + "様が未降車です");
		}
		for (Reservation reservation : adapter.getNoPaymentReservations()) {
			messages.add(getUserName(reservation) + "様が料金未払いです");
		}

		errorReservationListView.setAdapter(new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_list_item_1, messages));

		Button startButton = (Button) findViewById(R.id.start_button);
		TextView doesItLeaveTextView = (TextView) findViewById(R.id.does_it_leave_text_view);
		if (getCommonLogic().getRemainingOperationSchedules().size() <= 1) {
			doesItLeaveTextView.setText("確定しますか？");
			startButton.setText("確定する");
		} else {
			doesItLeaveTextView.setText("出発しますか？");
			startButton.setText("出発する");
		}
		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CommonLogic commonLogic = getCommonLogic();
				for (OperationSchedule operationSchedule : commonLogic
						.getCurrentOperationSchedule().asSet()) {
					commonLogic.postEvent(new GetOnEvent(operationSchedule,
							adapter.getSelectedGetOnReservations()));
					commonLogic.postEvent(new GetOffEvent(operationSchedule,
							adapter.getSelectedRidingReservations()));
					adapter.clearSelectedReservations();
				}
				commonLogic.postEvent(new EnterDrivePhaseEvent());
				hide();
			}
		});
		super.show();
	}
}
