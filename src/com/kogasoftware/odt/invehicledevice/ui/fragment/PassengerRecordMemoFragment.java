package com.kogasoftware.odt.invehicledevice.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;

import java.io.Serializable;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PassengerRecordMemoFragment.State;

public class PassengerRecordMemoFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {

		private final PassengerRecord passengerRecord;

		public State(PassengerRecord passengerRecord) {
			this.passengerRecord = passengerRecord;
		}

		public PassengerRecord getPassengerRecord() {
			return passengerRecord;
		}
	}

	public static PassengerRecordMemoFragment newInstance(
			PassengerRecord passengerRecord) {
		return newInstance(new PassengerRecordMemoFragment(), new State(passengerRecord));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = onCreateViewHelper(inflater, container,
				R.layout.passenger_record_memo_fragment,
				R.id.passenger_record_memo_close_button);
		for (Reservation reservation : getState().getPassengerRecord().getReservation().asSet()) {
			for (User user : getState().getPassengerRecord().getUser().asSet()) {
				TextView titleTextView = (TextView) view
						.findViewById(R.id.memo_title_text_view);
				StringBuilder title = new StringBuilder();
				title.append(getState().getPassengerRecord().getDisplayName());
				title.append("  予約番号：" + reservation.getId());
				titleTextView.setText(title);

				TextView reservationMemoTextView = (TextView) view
						.findViewById(R.id.reservation_memo_text_view);
				reservationMemoTextView.setText(reservation.getMemo().or(""));

				TextView userMemoTextView = (TextView) view
						.findViewById(R.id.user_memo_text_view);
				userMemoTextView.setText(Joiner.on('\n').join(user.getNotes()));
			}
		}
		return view;
	}
}
