package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.fragment.FinishPhaseFragment.State;

public class FinishPhaseFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
	}

	public static Fragment newInstance() {
		return newInstance(new FinishPhaseFragment(), new State());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.finish_phase_fragment, container,
				false);
	}
}
