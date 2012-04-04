package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.Reservation;

public class MemoModal extends Modal {
	public static class ShowEvent {
		public final Reservation reservation;

		public ShowEvent(Reservation reservation) {
			Preconditions.checkNotNull(reservation);
			this.reservation = reservation;
		}
	}

	public MemoModal(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.memo_modal);
	}

	@Subscribe
	public void show(ShowEvent event) {
		Reservation reservation = event.reservation;
		if (this.isShown()) {
			return;
		}
		TextView textView = (TextView) findViewById(R.id.reservation_memo_text_view);
		if (reservation.getMemo().isPresent()) {
			textView.setText(reservation.getMemo().get());
		}
		super.show();
	}
}
