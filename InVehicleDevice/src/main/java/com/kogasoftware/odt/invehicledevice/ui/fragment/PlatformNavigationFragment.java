package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PlatformNavigationFragment.State;

public class PlatformNavigationFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final OperationSchedule operationSchedule;

		public State(OperationSchedule operationSchedule) {
			this.operationSchedule = operationSchedule;
		}

		public OperationSchedule getOperationSchedule() {
			return operationSchedule;
		}
	}

	public static Fragment newInstance(OperationSchedule operationSchedule) {
		return newInstance(new PlatformNavigationFragment(), new State(
				operationSchedule));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = onCreateViewHelper(inflater, container,
				R.layout.platform_navigation_fragment,
				R.id.platform_navigation_close_button);

		for (Platform platform : getState().getOperationSchedule()
				.getPlatform().asSet()) {
			TextView titleTextView = (TextView) view
					.findViewById(R.id.platform_navigation_modal_title_text_view);
			titleTextView.setText(platform.getName());
		}
		return view;
	}
}
