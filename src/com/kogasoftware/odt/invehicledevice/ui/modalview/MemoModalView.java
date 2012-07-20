package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class MemoModalView extends ModalView {
	public MemoModalView(Context context, InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.memo_modal_view);
		setCloseOnClick(R.id.memo_close_button);
	}

	public void show(Reservation reservation, User user) {
		TextView titleTextView = (TextView) findViewById(R.id.memo_title_text_view);
		StringBuilder title = new StringBuilder();

		title.append(user.getLastName() + " " + user.getFirstName() + " 様 ");

		title.append("  予約番号：" + reservation.getId());

		titleTextView.setText(title);

		TextView reservationMemoTextView = (TextView) findViewById(R.id.reservation_memo_text_view);
		reservationMemoTextView.setText(reservation.getMemo().or(""));

		TextView userMemoTextView = (TextView) findViewById(R.id.user_memo_text_view);
		userMemoTextView.setText(Joiner.on('\n').join(user.getNotes()));
		super.show();
	}

	public void show(PassengerRecord passengerRecord) {
		if (passengerRecord.getReservation().isPresent()
				&& passengerRecord.getUser().isPresent()) {
			show(passengerRecord.getReservation().get(), passengerRecord
					.getUser().get());
		}
	}
}
