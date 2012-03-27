package com.kogasoftware.odt.invehicledevice.modal;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class ScheduleChangedModal extends Modal {
	public ScheduleChangedModal(final InVehicleDeviceActivity inVehicleDeviceActivity) {
		super(inVehicleDeviceActivity, R.layout.schedule_changed_modal);
		setId(R.id.schedule_changed_modal);
		Button scheduleConfirmButton = (Button)findViewById(R.id.schedule_confirm_button);
		scheduleConfirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				inVehicleDeviceActivity.showScheduleModal();
				hide();
			}
		});
	}

	private final Queue<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
	private VehicleNotification currentVehicleNotification = new VehicleNotification();

	private void refresh() {
		TextView bodyTextView = (TextView)findViewById(R.id.schedule_changed_text_view);
		currentVehicleNotification = vehicleNotifications.poll();
		if (currentVehicleNotification.getBody().isPresent()) {
			bodyTextView.setText(currentVehicleNotification.getBody().get());
		}
	}

	@Override
	public void show() {
		show(new LinkedList<VehicleNotification>());
	}

	public void show(List<VehicleNotification> additionalVehicleNotifications) {
		vehicleNotifications.addAll(additionalVehicleNotifications);
		if (vehicleNotifications.isEmpty()) {
			hide();
			return;
		}
		if (!isShown()) {
			refresh();
			super.show();
		}
	}

	@Override
	public void hide() {
		if (!vehicleNotifications.isEmpty()) {
			refresh();
		} else {
			super.hide();
		}
	}
}
