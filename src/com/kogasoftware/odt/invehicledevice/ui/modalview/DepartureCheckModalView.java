package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.User;

public class DepartureCheckModalView extends ModalView {
	private static String getUserName(PassengerRecord passengerRecord) {
		if (passengerRecord.getUser().isPresent()) {
			User user = passengerRecord.getUser().get();
			return user.getLastName() + user.getFirstName();
		} else {
			return "「予約ID: " + passengerRecord.getReservationId().or(0) + "」";
		}
	}

	public DepartureCheckModalView(Context context,
			InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.departure_check_modal_view);
		setCloseOnClick(R.id.departure_check_close_button);
	}

	public void show() {
		Button departureButton = (Button) findViewById(R.id.departure_button);
		Button departureWithErrorButton = (Button) findViewById(R.id.departure_with_error_button);
		View departureButtonLayout = findViewById(R.id.departure_button_layout);
		FlickUnneededListView errorUserListView = (FlickUnneededListView) findViewById(R.id.error_reservation_list_view);
		List<String> messages = new LinkedList<String>();

		for (PassengerRecord passengerRecord : service
				.getNoGettingOnPassengerRecords()) {
			messages.add(" ※ " + getUserName(passengerRecord) + "様が未乗車です");
		}

		for (PassengerRecord passengerRecord : service
				.getNoGettingOffPassengerRecords()) {
			messages.add(" ※ " + getUserName(passengerRecord) + "様が未降車です");
		}

		for (PassengerRecord passengerRecord : service
				.getNoPaymentPassengerRecords()) {
			messages.add(" ※ " + getUserName(passengerRecord) + "様が料金未払いです");
		}

		errorUserListView.getListView().setAdapter(
				new ArrayAdapter<String>(getContext(),
						R.layout.error_user_list_row, messages));

		String buttonMessage = "出発する";
		if (service.getRemainingOperationSchedules().size() <= 1) {
			buttonMessage = "確定する";
		}

		OnClickListener onClickDepartureButtonListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				service.enterDrivePhase();
				hide();
			}
		};

		// 警告データが存在するため「やめる」ボタンの形状を変更
		if (!messages.isEmpty()) {
			departureButtonLayout.setVisibility(GONE);
			departureWithErrorButton.setVisibility(VISIBLE);
			departureWithErrorButton.setEnabled(true);
			errorUserListView.setVisibility(VISIBLE);
			departureWithErrorButton
					.setOnClickListener(onClickDepartureButtonListener);
			departureWithErrorButton.setText(buttonMessage);
		} else {
			departureButtonLayout.setVisibility(VISIBLE);
			departureWithErrorButton.setText("");
			departureWithErrorButton.setEnabled(false);
			errorUserListView.setVisibility(GONE);
			departureButton.setOnClickListener(onClickDepartureButtonListener);
			departureButton.setText(buttonMessage);
		}

		super.show();
	}
}
