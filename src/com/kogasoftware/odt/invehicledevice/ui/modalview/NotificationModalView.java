package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.model.VehicleNotifications;

public class NotificationModalView extends ModalView implements
		InVehicleDeviceService.OnAlertVehicleNotificationReceiveListener {
	private VehicleNotification currentVehicleNotification = new VehicleNotification();

	public NotificationModalView(Context context, InVehicleDeviceService service) {
		super(context, service);
		setContentView(R.layout.notification_modal_view);
		findViewById(R.id.reply_yes_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						currentVehicleNotification
								.setResponse(VehicleNotifications.Response.YES);
						reply();
					}
				});
		findViewById(R.id.reply_no_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						currentVehicleNotification
								.setResponse(VehicleNotifications.Response.NO);
						reply();
					}
				});
		show();
	}

	@Override
	public void onAlertVehicleNotificationReceive() {
		getHandler().postDelayed(new Runnable() {
			@Override
			public void run() {
				show();
			}
		}, 5000);
	}

	private void reply() {
		hide();
		service.replyVehicleNotification(currentVehicleNotification);
		getHandler().post(new Runnable() {
			@Override
			public void run() {
				show();
			}
		});
	}

	@Override
	public void show() {
		List<VehicleNotification> vehicleNotifications = service
				.getVehicleNotifications();
		if (vehicleNotifications.isEmpty()) {
			hide();
			return;
		}
		VehicleNotification newVehicleNotification = vehicleNotifications
				.get(0);
		if (!newVehicleNotification.getId().equals(
				currentVehicleNotification.getId())) {
			service.speak(newVehicleNotification.getBodyRuby().or(
					newVehicleNotification.getBody()));
		}
		currentVehicleNotification = newVehicleNotification;
		TextView bodyTextView = (TextView) findViewById(R.id.notification_text_view);
		bodyTextView.setText(currentVehicleNotification.getBody());
		super.show();
	}
}
