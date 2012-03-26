package com.kogasoftware.odt.invehicledevice.modal;

import android.app.Activity;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;


public class ReturnPathModal extends Modal {
	private Reservation currentReservation = new Reservation();

	public ReturnPathModal(Activity activity) {
		super(activity, R.layout.return_path_modal);
		setId(R.id.return_path_modal);
	}

	public void show(Reservation reservation) {
		if (isShown()) {
			return;
		}
		currentReservation = reservation;
		TextView returnPathTitleTextView = (TextView)findViewById(R.id.return_path_title_text_view);
		String title = "予約番号 " + currentReservation.getId();
		Optional<User> user = currentReservation.getUser();
		if (user.isPresent()) {
			title += " " + user.get().getFamilyName() + " " + user.get().getLastName() + "様";
		}
		returnPathTitleTextView.setText(title);
		super.show();
	}
}

