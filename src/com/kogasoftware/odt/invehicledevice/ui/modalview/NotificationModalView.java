package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.VehicleNotificationLogic;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.model.VehicleNotification.NotificationKind;

public class NotificationModalView extends ModalView implements
		EventDispatcher.OnAlertVehicleNotificationReceiveListener {
	private final Handler handler = new Handler();
	private final VehicleNotificationLogic vehicleNotificationLogic;
	private VehicleNotification currentVehicleNotification = new VehicleNotification();

	public NotificationModalView(Context context, InVehicleDeviceService service) {
		super(context, service);
		vehicleNotificationLogic = new VehicleNotificationLogic(service);
		setContentView(R.layout.notification_modal_view);
		findViewById(R.id.reply_yes_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						currentVehicleNotification
								.setResponse(VehicleNotification.Response.YES);
						reply();
					}
				});
		findViewById(R.id.reply_no_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						currentVehicleNotification
								.setResponse(VehicleNotification.Response.NO);
						reply();
					}
				});
		show();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		service.getEventDispatcher().addOnAlertVehicleNotificationReceiveListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		service.getEventDispatcher().removeOnAlertVehicleNotificationReceiveListener(this);
	}

	@Override
	public void onAlertVehicleNotificationReceive() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				show();
			}
		}, 5000);
	}

	private void reply() {
		hide();
		vehicleNotificationLogic.replyVehicleNotification(currentVehicleNotification);
		handler.post(new Runnable() {
			@Override
			public void run() {
				show();
			}
		});
	}

	@Override
	public void show() {
		List<VehicleNotification> vehicleNotifications = vehicleNotificationLogic
				.getVehicleNotifications(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.UNHANDLED);
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
