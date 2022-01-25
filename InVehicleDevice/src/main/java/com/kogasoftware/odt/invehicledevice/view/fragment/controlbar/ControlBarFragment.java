package com.kogasoftware.odt.invehicledevice.view.fragment.controlbar;

import android.content.ContentResolver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.ArrivalCheckFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.DepartureCheckFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.OperationListFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.PassengerRecordErrorFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationPhase;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationSchedulesSyncFragmentAbstract;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ViewDisabler;

import java.util.LinkedList;
import java.util.List;

/**
 * 到着ボタン、地図ボタン、運行予定ボタンを表示する領域
 */
public class ControlBarFragment extends OperationSchedulesSyncFragmentAbstract {
    private static final String TAG = ControlBarFragment.class.getSimpleName();
    public static final String OPERATION_LIST_FRAGMENT_TAG = ControlBarFragment.class
            + "/" + OperationSchedulesSyncFragmentAbstract.class;
    private ContentResolver contentResolver;
    private Button mapButton;
    private OperationPhase operationPhase;

    public static ControlBarFragment newInstance() {
        ControlBarFragment fragment = new ControlBarFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.control_bar_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contentResolver = getActivity().getContentResolver();
        mapButton = (Button) getView().findViewById(R.id.map_button);
        Button operationScheduleListButton = (Button) getView().findViewById(R.id.operation_schedule_list_button);

        // TODO: 運行予定ボタンの定義。地図やphaseボタンと異なり、特別な引数や文字の変更が無いから、ここで定義されているが、わかりにくい。
        operationScheduleListButton.setOnClickListener(v -> {
            ViewDisabler.disable(v);
            showOperationScheduleListFragment();
        });
    }

    // 表示メソッド
    public void showNavigation(Phase phase) {
        if (!isAdded()) {
            return;
        }

        if (phase.equals(Phase.DRIVE)) {
            if (operationPhase.isExistCurrent()) {
                operationPhase.getCurrentRepresentativeOS().startNavigation(getActivity());
            }
        } else {
            if (operationPhase.isExistNext()) {
                operationPhase.getNextRepresentativeOS().startNavigation(getActivity());
            }
        }
    }

    public void showOperationScheduleListFragment() {
        if (!isAdded()) {
            return;
        }

        Fragments.showModalFragment(getFragmentManager(), OperationListFragment.newInstance(true), OPERATION_LIST_FRAGMENT_TAG);
    }

    public void showArrivalCheckFragment() {

        if (!isAdded()) {
            return;
        }

        if (!operationPhase.isExistCurrent()) {
            return;
        }

        getFragmentManager()
                .beginTransaction()
                .add(R.id.modal_fragment_container, ArrivalCheckFragment.newInstance(operationPhase))
                .commitAllowingStateLoss();
    }

    public void showDepartureCheckFragment(Phase phase) {
        if (!isAdded()) {
            return;
        }

        if (!operationPhase.isExistCurrent()) {
            return;
        }

        if (existPassengerRecordError(phase)) {
            Fragments.showModalFragment(getFragmentManager(), PassengerRecordErrorFragment.newInstance(operationPhase));
        } else if (phase.equals(Phase.PLATFORM_GET_OFF)) {
            List<PassengerRecord> getOnPassengerRecords = Lists.newArrayList();
            for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
                getOnPassengerRecords.addAll(operationSchedule.getGetOnScheduledPassengerRecords(operationPhase.passengerRecords));
            }

            if (getOnPassengerRecords.isEmpty()) {
                Fragments.showModalFragment(getFragmentManager(), DepartureCheckFragment.newInstance(operationPhase));
            } else {
                for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
                    operationSchedule.completeGetOff = true;
                }
                new Thread() {
                    @Override
                    public void run() {
                        for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
                            contentResolver.insert(OperationSchedule.CONTENT.URI, operationSchedule.toContentValues());
                        }
                    }
                }.start();
            }
        } else {
            Fragments.showModalFragment(getFragmentManager(), DepartureCheckFragment.newInstance(operationPhase));
        }
    }

    private boolean existPassengerRecordError(Phase phase) {
        return (existGetOffPassengerError(phase) || existGetOnPassengerError(phase));
    }

    private boolean existGetOffPassengerError(Phase phase) {
        if (!phase.equals(Phase.PLATFORM_GET_OFF)) {
            return false;
        }

        for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
            if (!operationSchedule.getNoGetOffErrorPassengerRecords(operationPhase.passengerRecords).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean existGetOnPassengerError(Phase phase) {
        if (!phase.equals(Phase.PLATFORM_GET_ON)) {
            return false;
        }

        for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
            if (!operationSchedule.getNoGetOnErrorPassengerRecords(operationPhase.passengerRecords).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // 画面右部のボタンの、地図ボタン、phase変更ボタン（到着しました等）を定義する
    @Override
    protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
            final LinkedList<OperationSchedule> operationSchedules,
            final LinkedList<PassengerRecord> passengerRecords) {
        this.operationPhase = new OperationPhase(operationSchedules, passengerRecords);
        final Phase phase = operationPhase.getPhase();

        mapButton.setOnClickListener(v -> {
            ViewDisabler.disable(v);
            showNavigation(phase);
        });

        Button changePhaseButton = (Button) getView().findViewById(R.id.change_phase_button);
        getView().setBackgroundColor(Color.WHITE);
        switch (operationPhase.getPhase()) {
            case DRIVE:
                changePhaseButton.setEnabled(true);
                changePhaseButton.setText(getString(R.string.it_arrives_button_text));
                changePhaseButton.setOnClickListener(v -> {
                    ViewDisabler.disable(v);
                    showArrivalCheckFragment();
                });
                break;
            case FINISH:
                changePhaseButton.setEnabled(false);
                changePhaseButton.setText("");
                break;
            case PLATFORM_GET_OFF:
                changePhaseButton.setEnabled(true);
                changePhaseButton.setText("確認\nする");
                changePhaseButton.setOnClickListener(v -> {
                    ViewDisabler.disable(v);
                    showDepartureCheckFragment(phase);
                });
                break;
            case PLATFORM_GET_ON:
                changePhaseButton.setEnabled(true);
                changePhaseButton.setText("確認\nする");
                changePhaseButton.setOnClickListener(v -> {
                    ViewDisabler.disable(v);
                    showDepartureCheckFragment(phase);
                });
                break;
        }
    }
}
