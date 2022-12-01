package com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.modal;

import android.app.Fragment;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.utils.OperationPhase;

import org.joda.time.DateTime;

/**
 * 出発チェック画面
 */
public class DepartureCheckFragment extends Fragment {
    private static final String TAG = DepartureCheckFragment.class.getSimpleName();
    private static final String PHASE_KEY = "phase";
    private static final String OPERATION_PHASE_KEY = "operation_phase";

    public static Fragment newInstance(OperationPhase operationPhase) {
        DepartureCheckFragment fragment = new DepartureCheckFragment();
        Bundle args = new Bundle();
        args.putSerializable(OPERATION_PHASE_KEY, operationPhase);
        fragment.setArguments(args);
        return fragment;
    }

    private ContentResolver contentResolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.departure_check_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contentResolver = getActivity().getContentResolver();

        Bundle args = getArguments();
        final OperationPhase operationPhase = (OperationPhase) args.getSerializable(OPERATION_PHASE_KEY);
        Phase phase = operationPhase.getPhase();

        View view = getView();

        Button departureButton = view.findViewById(R.id.departure_button);
        if (operationPhase.isExistNext()) {
            departureButton.setText("出発する");
        } else {
            departureButton.setText("確定する");
        }

        Button closeButton = view.findViewById(R.id.departure_check_close_button);
        closeButton.setOnClickListener(v -> Fragments.hideModal(DepartureCheckFragment.this));
        if (phase == Phase.PLATFORM_GET_OFF) {
            closeButton.setText("降車一覧に戻る");
        } else {
            closeButton.setText("乗車一覧に戻る");
        }

        departureButton.setOnClickListener(v -> {
            Fragments.hideModal(DepartureCheckFragment.this);
            Thread tt = new Thread() {
                @Override
                public void run() {
                    for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
                        operationSchedule.departedAt = DateTime.now();
                        contentResolver.insert(OperationSchedule.CONTENT.URI, operationSchedule.toContentValues());
                    }
                }
            };
            tt.start();
            try {
                tt.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
