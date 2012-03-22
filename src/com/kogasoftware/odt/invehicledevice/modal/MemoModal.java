package com.kogasoftware.odt.invehicledevice.modal;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.Reservation;


public class MemoModal extends Modal {
	public MemoModal(Context context, Reservation reservation) {
		super(context, R.layout.memo_modal);

		TextView textView = (TextView)findViewById(R.id.reservation_memo_text_view);
		if (reservation.getMemo().isPresent()) {
			textView.setText(reservation.getMemo().get());
		}

		Button closeButton = (Button)findViewById(R.id.memo_close_button);
		closeButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				MemoModal.this.remove();
			}});
	}
}
