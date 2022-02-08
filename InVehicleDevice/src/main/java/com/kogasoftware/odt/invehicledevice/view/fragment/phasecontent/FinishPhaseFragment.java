package com.kogasoftware.odt.invehicledevice.view.fragment.phasecontent;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.R;

/**
 * 運行終了画面
 */
public class FinishPhaseFragment extends Fragment {
    private static final String TAG = FinishPhaseFragment.class.getSimpleName();

    public static FinishPhaseFragment newInstance() {
        return new FinishPhaseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        return inflater.inflate(R.layout.finish_phase_fragment, container, false);
    }
}
