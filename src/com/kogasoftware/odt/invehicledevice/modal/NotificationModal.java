package com.kogasoftware.odt.invehicledevice.modal;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.Activity;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.VehicleNotification;


public class NotificationModal extends Modal {
	private final Queue<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
	private VehicleNotification currentVehicleNotification = new VehicleNotification();

	public NotificationModal(Activity activity) {
		super(activity, R.layout.notification_modal);
		setId(R.id.vehicle_notification_overlay);
	}

	private void refresh() {
		currentVehicleNotification = vehicleNotifications.poll();
		TextView bodyTextView = (TextView)findViewById(R.id.vehicle_notification_body);
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
