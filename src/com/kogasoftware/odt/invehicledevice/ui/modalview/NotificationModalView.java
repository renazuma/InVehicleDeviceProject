package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationRepliedEvent;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.model.VehicleNotifications;

public class NotificationModalView extends ModalView {
	public static class ShowEvent {
	}

	private VehicleNotification currentVehicleNotification = new VehicleNotification();

	public NotificationModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
	}

	private void reply() {
		hide();
		getCommonLogic()
				.postEvent(
						new VehicleNotificationRepliedEvent(
								currentVehicleNotification));
	}

	@Override
	public void show() {
		List<VehicleNotification> vehicleNotifications = getCommonLogic()
				.getVehicleNotifications();
		if (vehicleNotifications.isEmpty()) {
			hide();
			return;
		}
		VehicleNotification newVehicleNotification = vehicleNotifications
				.get(0);
		if (!newVehicleNotification.getId().equals(
				currentVehicleNotification.getId())) {
			getCommonLogic().postEvent(
					new SpeakEvent(newVehicleNotification.getBody()));
		}
		currentVehicleNotification = newVehicleNotification;
		TextView bodyTextView = (TextView) findViewById(R.id.notification_text_view);
		bodyTextView.setText(currentVehicleNotification.getBody());
		super.show();
	}

	@Subscribe
	public void show(ShowEvent event) {
		show();
	}
}
