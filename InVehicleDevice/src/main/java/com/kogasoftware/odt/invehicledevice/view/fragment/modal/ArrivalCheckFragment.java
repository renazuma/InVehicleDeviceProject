package com.kogasoftware.odt.invehicledevice.view.fragment.modal;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationPhase;

import org.joda.time.DateTime;

/**
 * 到着チェック画面
 */
public class ArrivalCheckFragment extends Fragment {
    private static final String OPERATION_PHASE_KEY = "operation_phase";

    public static Fragment newInstance(OperationPhase operationPhase) {
        ArrivalCheckFragment fragment = new ArrivalCheckFragment();
        Bundle args = new Bundle();
        args.putSerializable(OPERATION_PHASE_KEY, operationPhase);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        final ContentResolver contentResolver = getActivity().getContentResolver();
        final OperationPhase operationPhase = (OperationPhase) getArguments().getSerializable(OPERATION_PHASE_KEY);

        TextView commentTextView = view.findViewById(R.id.arrival_check_comment_text_view);

        Button closeButton = view.findViewById(R.id.arrival_check_close_button);
        closeButton.setOnClickListener(v -> Fragments.hide(ArrivalCheckFragment.this));

        Button arrivalButton = view.findViewById(R.id.arrival_button);
        arrivalButton.setOnClickListener(view1 -> {
            Fragments.hide(ArrivalCheckFragment.this);
            Thread tt = new Thread() {
                @Override
                public void run() {
                    for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
                        operationSchedule.arrivedAt = DateTime.now();
                        ContentValues values = operationSchedule.toContentValues();
                        contentResolver.insert(OperationSchedule.CONTENT.URI, values);
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

        commentTextView.setText(operationPhase.getCurrentRepresentativeOS().name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.arrival_check_fragment, container, false);
    }
}