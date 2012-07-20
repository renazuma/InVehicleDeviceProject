package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.VoidReader;
import com.kogasoftware.odt.webapi.Identifiables;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class ScheduleChangedModalView extends ModalView implements
		InVehicleDeviceService.OnMergeUpdatedOperationScheduleListener {
	protected final TextView scheduleChangedTextView;
	protected final ScheduleModalView scheduleModalView;

	public ScheduleChangedModalView(Context context,
			InVehicleDeviceService service,
			final ScheduleModalView scheduleModalView) {
		super(context, service);
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
		service.addOnMergeUpdatedOperationScheduleListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		service.removeOnMergeUpdatedOperationScheduleListener(this);
	}

	@Override
	public void hide() {
		super.hide();
		scheduleChangedTextView.setText("");
	}

	@Override
	public void onMergeUpdatedOperationSchedule(
			final List<VehicleNotification> vehicleNotifications) {

		service.getLocalDataSource().withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				Identifiables
						.merge(vehicleNotifications,
								status.receivedOperationScheduleChangedVehicleNotifications);
			}
		});
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

		service.replyUpdatedOperationScheduleVehicleNotifications(vehicleNotifications);
	}
}
