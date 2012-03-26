package com.kogasoftware.odt.invehicledevice.modal;

import android.app.Activity;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.Reservation;


public class MemoModal extends Modal {
	public MemoModal(Activity activity) {
		super(activity, R.layout.memo_modal);
		setId(R.id.memo_overlay);
	}

	public void show(Reservation reservation) {
		if (this.isShown()) {
			return;
		}
		TextView textView = (TextView)findViewById(R.id.reservation_memo_text_view);
		if (reservation.getMemo().isPresent()) {
			textView.setText(reservation.getMemo().get());
		}
		super.show();
	}
}
