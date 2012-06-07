package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.logic.event.ReceivedOperationScheduleChangedVehicleNotificationsReplyEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleMergedEvent;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.model.VehicleNotifications;

public class ScheduleChangedModalView extends ModalView {
	private final TextView scheduleChangedTextView;

	public ScheduleChangedModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.schedule_changed_modal_view);
		setCloseOnClick(R.id.schedule_changed_close_button);

		scheduleChangedTextView = (TextView) findViewById(R.id.schedule_changed_text_view);

		Button scheduleConfirmButton = (Button) findViewById(R.id.schedule_confirm_button);
		scheduleConfirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getCommonLogic().postEvent(new ScheduleModalView.ShowEvent());
				hide();
			}
		});
	}

	@Override
	public void hide() {
		super.hide();
		scheduleChangedTextView.setText("");
	}

	@Subscribe
	public void show(UpdatedOperationScheduleMergedEvent event) {
		final List<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
		getCommonLogic().getStatusAccess().read(new VoidReader() {
			@Override
			public void read(Status status) {
				vehicleNotifications
						.addAll(status.receivedOperationScheduleChangedVehicleNotifications);
			}
		});
		if (vehicleNotifications.isEmpty()) {
			return;
		}

		super.show();
		getCommonLogic().postEvent(new ScheduleModalView.HideEvent());

		StringBuilder message = new StringBuilder(Objects.firstNonNull(
				scheduleChangedTextView.getText(), ""));
		for (VehicleNotification vehicleNotification : vehicleNotifications) {
			getCommonLogic().postEvent(
					new SpeakEvent(vehicleNotification.getBody()));
			message.append(vehicleNotification.getBody());
			message.append('\n');
		}

		scheduleChangedTextView.setText(message);

		// 表示したスケジュール変更通知を、responseを指定して返信リストへ追加
		for (VehicleNotification vehicleNotification : vehicleNotifications) {
			vehicleNotification.setResponse(VehicleNotifications.Response.YES); // TODO
		}
		getCommonLogic()
				.postEvent(
						new ReceivedOperationScheduleChangedVehicleNotificationsReplyEvent(
								vehicleNotifications));
	}
}
