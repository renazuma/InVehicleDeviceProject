package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.VehicleNotificationLogic;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;

public class ScheduleChangedModalView extends ModalView implements
		EventDispatcher.OnMergeOperationSchedulesListener {
	protected final VehicleNotificationLogic vehicleNotificationLogic;
	protected final TextView scheduleChangedTextView;
	protected final ScheduleModalView scheduleModalView;

	public ScheduleChangedModalView(Context context,
			InVehicleDeviceService service,
			final ScheduleModalView scheduleModalView) {
		super(context, service);
		vehicleNotificationLogic = new VehicleNotificationLogic(service);
		this.scheduleModalView = scheduleModalView;
		setContentView(R.layout.schedule_changed_modal_view);
		setCloseOnClick(R.id.schedule_changed_close_button);

		scheduleChangedTextView = (TextView) findViewById(R.id.schedule_changed_text_view);

		Button scheduleConfirmButton = (Button) findViewById(R.id.schedule_confirm_button);
		scheduleConfirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scheduleModalView.show();
				hide();
			}
		});
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		service.getEventDispatcher().addOnMergeOperationSchedulesListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		service.getEventDispatcher().removeOnMergeOperationSchedulesListener(this);
	}

	@Override
	public void hide() {
		super.hide();
		scheduleChangedTextView.setText("");
	}

	@Override
	public void onMergeOperationSchedules(
			final List<VehicleNotification> vehicleNotifications) {
		if (vehicleNotifications.isEmpty()) {
			return;
		}

		super.show();
		scheduleModalView.hide();

		StringBuilder message = new StringBuilder(Objects.firstNonNull(
				scheduleChangedTextView.getText(), ""));
		for (VehicleNotification vehicleNotification : vehicleNotifications) {
			service.speak(vehicleNotification.getBodyRuby().or(""));
			message.append(vehicleNotification.getBody());
			message.append('\n');
		}

		scheduleChangedTextView.setText(message);

		// 表示したスケジュール変更通知を、responseを指定して返信リストへ追加
		for (VehicleNotification vehicleNotification : vehicleNotifications) {
			vehicleNotification.setResponse(VehicleNotification.Response.YES); // TODO
		}

		vehicleNotificationLogic
				.replyUpdatedOperationScheduleVehicleNotifications(vehicleNotifications);
	}
}
