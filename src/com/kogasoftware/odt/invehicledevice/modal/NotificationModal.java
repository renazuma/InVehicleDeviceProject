package com.kogasoftware.odt.invehicledevice.modal;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class NotificationModal extends Modal {
	public NotificationModal(Context context, AttributeSet attrs) {
		super(context, attrs, R.layout.notification_modal);

		VehicleNotification vehicleNotification = new VehicleNotification();
		vehicleNotification.setBody("鈴木さんは乗車されましたか？");
		// 
		TextView bodyTextView = (TextView)findViewById(R.id.vehicle_notification_body);
		if (vehicleNotification.getBody().isPresent()) {
			bodyTextView.setText(vehicleNotification.getBody().get());
		}
	}
}
