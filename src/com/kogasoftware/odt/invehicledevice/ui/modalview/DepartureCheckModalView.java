package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;
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

	private final AlphaAnimation animation = new AlphaAnimation(1, 0.3f);

	public DepartureCheckModalView(Context context,
			InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.departure_check_modal_view);
		setCloseOnClick(R.id.departure_check_close_button);
	}

	public void show(PassengerRecordArrayAdapter adapter) {
		Button startButton = (Button) findViewById(R.id.departure_button);
		Button closeButton = (Button) findViewById(R.id.departure_check_close_button);
		ListView errorReservationListView = ((FlickUnneededListView) findViewById(R.id.error_reservation_list_view))
				.getListView();
		List<String> messages = new LinkedList<String>();

		for (PassengerRecord passengerRecord : adapter.getNoGettingOnPassengerRecords()) {
			messages.add(" ※ " + getUserName(passengerRecord) + "様が未乗車です");
		}

		for (PassengerRecord passengerRecord : adapter.getNoGettingOffPassengerRecords()) {
			messages.add(" ※ " + getUserName(passengerRecord) + "様が未降車です");
		}

		for (PassengerRecord passengerRecord : adapter.getNoPaymentPassengerRecords()) {
			messages.add(" ※ " + getUserName(passengerRecord) + "様が料金未払いです");
		}

		errorReservationListView.setAdapter(new ArrayAdapter<String>(
				getContext(), android.R.layout.simple_list_item_1, messages));

		if (service.getRemainingOperationSchedules().size() <= 1) {
			startButton.setText("確定する");
		} else {
			startButton.setText("出発する");
		}

		// 警告データが存在するため「やめる」ボタンの形状を変更
		if (!messages.isEmpty()) {
			animation.setDuration(1000);
			animation.setRepeatCount(Animation.INFINITE);
			animation.setRepeatMode(Animation.REVERSE);
			closeButton.startAnimation(animation);

			closeButton.setBackgroundColor(Color.parseColor("#66FF66"));
			closeButton.setTextColor(Color.parseColor("#000000"));

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			lp.weight = 0.5f;
			lp.setMargins(10, 1, 10, 10);
			closeButton.setLayoutParams(lp);
			closeButton.setShadowLayer(1.0f, 1.5f, 1.5f,
					Color.parseColor("#FFFFFF"));

		} else {
			closeButton.setBackgroundDrawable(getContext().getResources()
					.getDrawable(android.R.drawable.btn_default));
			closeButton.clearAnimation();

			closeButton.setTextColor(Color.BLACK);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			lp.setMargins(0, 0, 0, 0);

			lp.weight = 1f;
			closeButton.setLayoutParams(lp);
			closeButton.setShadowLayer(0.0f, 0.0f, 0.0f,
					Color.parseColor("#FFFFFF"));
		}

		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				service.enterDrivePhase();
				hide();
			}
		});
		super.show();
	}
}
