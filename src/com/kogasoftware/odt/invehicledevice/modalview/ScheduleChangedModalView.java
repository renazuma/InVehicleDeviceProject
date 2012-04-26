package com.kogasoftware.odt.invehicledevice.modalview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.Status;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.event.UpdateOperationScheduleCompleteEvent;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class ScheduleChangedModalView extends ModalView {
	public ScheduleChangedModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.schedule_changed_modal_view);
		Button scheduleConfirmButton = (Button) findViewById(R.id.schedule_confirm_button);
		scheduleConfirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getLogic().showScheduleModalView();
				hide();
			}
		});
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Subscribe
	public void show(final UpdateOperationScheduleCompleteEvent event) {
		super.show();

		StringBuilder message = new StringBuilder();
		for (VehicleNotification vehicleNotification : event.vehicleNotifications) {
			message.append(vehicleNotification.getBody());
			message.append("\n");
		}

		TextView scheduleChangedTextView = (TextView) findViewById(R.id.schedule_changed_text_view);
		scheduleChangedTextView.setText(message);

		// 表示したスケジュール変更通知を、responseを指定して返信リストへ追加
		for (VehicleNotification vehicleNotification : event.vehicleNotifications) {
			vehicleNotification.setResponse(0); // TODO
		}
		getLogic().getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.operationScheduleChangedVehicleNotifications
						.removeAll(event.vehicleNotifications);
				status.sendLists.repliedVehicleNotifications
						.addAll(event.vehicleNotifications);
			}
		});
	}
}
