package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.fragment.FinishPhaseFragment.State;

public class FinishPhaseFragment extends ApplicationFragment<State> {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
	}

	private static final String TAG = FinishPhaseFragment.class.getSimpleName();

	public static Fragment newInstance() {
		return newInstance(new FinishPhaseFragment(), new State());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView");
		return inflater.inflate(R.layout.finish_phase_fragment, container,
				false);
	}
}
