package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.app.Fragment;

import com.kogasoftware.odt.invehicledevice.ui.fragment.BatteryAlertFragment.State;

public class BatteryAlertFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	static class State implements Serializable {
	};

	public static Fragment newInstance() {
		return newInstance(new BatteryAlertFragment(), new State());
	}
}
