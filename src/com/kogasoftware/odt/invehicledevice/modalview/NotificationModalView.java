package com.kogasoftware.odt.invehicledevice.modalview;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.Status;
import com.kogasoftware.odt.invehicledevice.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.Utility;
import com.kogasoftware.odt.invehicledevice.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class NotificationModalView extends ModalView {
	public static class ShowEvent {
		public ShowEvent() {
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
		getLogic().speak(currentVehicleNotification.getBody());
	}

	private void reply() {
		getLogic().getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.vehicleNotifications.remove(currentVehicleNotification);
				status.sendLists.repliedVehicleNotifications
						.add(currentVehicleNotification);
			}
		});
	}

	@Override
	public void show() {
		getLogic().getStatusAccess().read(new VoidReader() {
			@Override
			public void read(Status status) {
				Utility.mergeById(vehicleNotifications,
						status.vehicleNotifications);
			}
		});

		if (vehicleNotifications.isEmpty()) {
			hide();
			return;
		}
		if (!isShown()) {
			refresh();
			super.show();
		}
	}

	@Subscribe
	public void show(CommonLogicLoadCompleteEvent event) {
		show();
	}

	@Subscribe
	public void show(ShowEvent event) {
		show();
	}
}
