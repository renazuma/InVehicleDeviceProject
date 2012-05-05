package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.logic.Identifiables;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class NotificationModalView extends ModalView {
	public static class ShowEvent {
		public ShowEvent() {
		}
	}

	private VehicleNotification currentVehicleNotification = new VehicleNotification();
	private final List<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();

	public NotificationModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.notification_modal_view);
		findViewById(R.id.reply_yes_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						currentVehicleNotification.setResponse(1);
						reply();
						hide();
					}
				});
		findViewById(R.id.reply_no_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						currentVehicleNotification.setResponse(0);
						reply();
						hide();
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
		if (vehicleNotifications.isEmpty()) {
			return;
		}
		currentVehicleNotification = vehicleNotifications.get(0);
		TextView bodyTextView = (TextView) findViewById(R.id.notification_text_view);
		bodyTextView.setText(currentVehicleNotification.getBody());
		getCommonLogic().postEvent(
				new SpeakEvent(currentVehicleNotification.getBody()));
	}

	private void reply() {
		vehicleNotifications.remove(currentVehicleNotification);
		getCommonLogic().getStatusAccess().write(new Writer() {
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
		getCommonLogic().getStatusAccess().read(new VoidReader() {
			@Override
			public void read(Status status) {
				Identifiables.merge(vehicleNotifications,
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
