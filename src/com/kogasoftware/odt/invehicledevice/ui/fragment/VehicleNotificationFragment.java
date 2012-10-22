package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.VehicleNotificationLogic;
import com.kogasoftware.odt.invehicledevice.ui.fragment.VehicleNotificationFragment.State;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final VehicleNotification vehicleNotification;

		public State(VehicleNotification vehicleNotification) {
			this.vehicleNotification = vehicleNotification;
		}

		public VehicleNotification getVehicleNotification() {
			return vehicleNotification;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		String message = getState().getVehicleNotification().getBodyRuby()
				.or(getState().getVehicleNotification().getBody());
		getService().speak(message);
	}

	public static VehicleNotificationFragment newInstance(
			VehicleNotification vehicleNotification) {
		return newInstance(new VehicleNotificationFragment(), new State(
				vehicleNotification));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.vehicle_notification_fragment,
				container, false);
		TextView bodyTextView = (TextView) view
				.findViewById(R.id.notification_text_view);
		bodyTextView.setText(getState().getVehicleNotification().getBody());

		view.findViewById(R.id.reply_yes_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						reply(getState().getVehicleNotification(),
								VehicleNotification.Response.YES);
					}
				});
		view.findViewById(R.id.reply_no_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View view) {
						reply(getState().getVehicleNotification(),
								VehicleNotification.Response.NO);
					}
				});
		return view;
	}

	private void reply(VehicleNotification vehicleNotification, Integer response) {
		hide();
		vehicleNotification.setResponse(response);
		new VehicleNotificationLogic(getService())
				.replyVehicleNotification(vehicleNotification);
	}
}
