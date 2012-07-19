package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class MemoModalView extends ModalView {
	public MemoModalView(Context context, InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.memo_modal_view);
		setCloseOnClick(R.id.memo_close_button);
	}

	public void show(Reservation reservation) {
		TextView titleTextView = (TextView) findViewById(R.id.memo_title_text_view);
		StringBuilder title = new StringBuilder();
		for (User user : reservation.getUser().asSet()) {
			title.append(user.getLastName() + " " + user.getFirstName() + " 様 ");
		}
		title.append("  予約番号：" + reservation.getId());
		titleTextView.setText(title);

		TextView reservationMemoTextView = (TextView) findViewById(R.id.reservation_memo_text_view);
		reservationMemoTextView.setText(reservation.getMemo().or(""));

		TextView userMemoTextView = (TextView) findViewById(R.id.user_memo_text_view);
		userMemoTextView.setText(Joiner.on('\n').join(
				Users.getMemo(reservation)));
		super.show();
	}
}
