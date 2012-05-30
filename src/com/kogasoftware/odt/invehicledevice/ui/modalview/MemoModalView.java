package com.kogasoftware.odt.invehicledevice.ui.modalview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.Users;

public class MemoModalView extends ModalView {
	public static class ShowEvent {
		public final Reservation reservation;

		public ShowEvent(Reservation reservation) {
			Preconditions.checkNotNull(reservation);
			this.reservation = reservation;
		}
	}

	public MemoModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.memo_modal_view);
		setCloseOnClick(R.id.memo_close_button);
	}

	@Subscribe
	public void show(ShowEvent event) {
		Reservation reservation = event.reservation;
		TextView reservationMemoTextView = (TextView) findViewById(R.id.reservation_memo_text_view);
		reservationMemoTextView.setText(reservation.getMemo().or(""));

		TextView userMemoTextView = (TextView) findViewById(R.id.user_memo_text_view);
		userMemoTextView.setText(Joiner.on('\n').join(Users.getMemo(event.reservation)));
		super.show();
	}
}
