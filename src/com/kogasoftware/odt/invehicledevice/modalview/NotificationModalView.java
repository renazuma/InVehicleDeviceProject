package com.kogasoftware.odt.invehicledevice.modalview;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.Status;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Writer;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class NotificationModalView extends ModalView {
	public static class ShowEvent {
		public final List<VehicleNotification> vehicleNotifications;

		public ShowEvent(List<VehicleNotification> vehicleNotifications) {
			Preconditions.checkNotNull(vehicleNotifications);
			this.vehicleNotifications = vehicleNotifications;
		}
	}

	private VehicleNotification currentVehicleNotification = new VehicleNotification();
	private final Queue<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();

	public NotificationModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.notification_modal_view);
		findViewById(R.id.reply_yes_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						currentVehicleNotification.setResponse(1);
						reply();
					}
				});
		findViewById(R.id.reply_no_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						currentVehicleNotification.setResponse(0);
						reply();
					}
				});
	}

	@Override
	public void hide() {
		if (!vehicleNotifications.isEmpty()) {
			refresh();
		} else {
			super.hide();
		}
	}

	private void refresh() {
		currentVehicleNotification = vehicleNotifications.poll();
		TextView bodyTextView = (TextView) findViewById(R.id.notification_text_view);
		bodyTextView.setText(currentVehicleNotification.getBody());
	}

	private void reply() {
		getLogic().getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.sendLists.repliedVehicleNotifications
						.add(currentVehicleNotification);
			}
		});
	}

	@Subscribe
	public void show(ShowEvent event) {
		vehicleNotifications.addAll(event.vehicleNotifications);
		if (vehicleNotifications.isEmpty()) {
			hide();
			return;
		}
		if (!isShown()) {
			refresh();
			super.show();
		}
	}
}
