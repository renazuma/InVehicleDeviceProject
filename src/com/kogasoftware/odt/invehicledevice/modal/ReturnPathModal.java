package com.kogasoftware.odt.invehicledevice.modal;

import android.app.Activity;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.Reservation;


public class ReturnPathModal extends Modal {
	private Reservation currentReservation = new Reservation();

	public ReturnPathModal(Activity activity) {
		super(activity, R.layout.return_path_modal);
		setId(R.id.return_path_overlay);
	}

	public void show(Reservation reservation) {
		if (isShown()) {
			return;
		}
		currentReservation = reservation;
	}
}

