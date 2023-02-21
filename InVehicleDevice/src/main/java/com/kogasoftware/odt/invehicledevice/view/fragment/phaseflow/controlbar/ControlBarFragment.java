package com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.controlbar;

import static android.view.View.VISIBLE;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ZenrinMapsAccount;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.MapFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.modal.ArrivalCheckFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.modal.DepartureCheckFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.listflow.modal.OperationListFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.modal.PassengerRecordErrorFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.FragmentUtils;
import com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.utils.OperationPhase;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationSchedulesSyncFragmentAbstract;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ViewDisabler;

import java.util.LinkedList;
import java.util.List;

/**
 * 到着ボタン、ナビボタン、地図ボタン、運行予定ボタンを表示する領域
 */
public class ControlBarFragment extends OperationSchedulesSyncFragmentAbstract {
    private static final String TAG = ControlBarFragment.class.getSimpleName();
    public static final String OPERATION_LIST_FRAGMENT_TAG = ControlBarFragment.class
            + "/" + OperationSchedulesSyncFragmentAbstract.class;
    private ContentResolver contentResolver;
    private OperationPhase operationPhase;

    public static ControlBarFragment newInstance() {
        return new ControlBarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.control_bar_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contentResolver = getActivity().getContentResolver();
    }

    // 表示メソッド
    private void showNavigation(Phase phase) {
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

    private void showMapFragment() {
        if (!isAdded()) {
            return;
        }

        FragmentUtils.showModal(getFragmentManager(), new MapFragment());
    }

    private void showOperationScheduleListFragment() {
        if (!isAdded()) {
            return;
        }

        FragmentUtils.showModal(getFragmentManager(), OperationListFragment.newInstance(true), OPERATION_LIST_FRAGMENT_TAG);
    }

    private void showArrivalCheckFragment() {

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

    private void showDepartureCheckFragment(Phase phase) {
        if (!isAdded()) {
            return;
        }

        if (!operationPhase.isExistCurrent()) {
            return;
        }

        if (existPassengerRecordError(phase)) {
            FragmentUtils.showModal(getFragmentManager(), PassengerRecordErrorFragment.newInstance(operationPhase));
        } else if (phase.equals(Phase.PLATFORM_GET_OFF)) {
            List<PassengerRecord> getOnPassengerRecords = Lists.newArrayList();
            for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
                getOnPassengerRecords.addAll(operationSchedule.getGetOnScheduledPassengerRecords(operationPhase.passengerRecords));
            }

            if (getOnPassengerRecords.isEmpty()) {
                FragmentUtils.showModal(getFragmentManager(), DepartureCheckFragment.newInstance(operationPhase));
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
            FragmentUtils.showModal(getFragmentManager(), DepartureCheckFragment.newInstance(operationPhase));
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

    // 運行が更新された際に画面右部のボタンの定義をする
    @Override
    protected void onSchedulesRefresh(
            final LinkedList<OperationSchedule> operationSchedules,
            final LinkedList<PassengerRecord> passengerRecords) {
        this.operationPhase = new OperationPhase(operationSchedules, passengerRecords);
        final Phase phase = operationPhase.getPhase();

        getView().findViewById(R.id.navi_button).setOnClickListener(v -> {
            ViewDisabler.disable(v);
            showNavigation(phase);
        });

        Button mapButton = getView().findViewById(R.id.map_button);

        Cursor serviceProviderCursor = getContext()
            .getContentResolver()
            .query(ServiceProvider.CONTENT.URI, null, null, null, null);

        Cursor zenrinMapsAccountCursor = getContext()
            .getContentResolver()
            .query(ZenrinMapsAccount.CONTENT.URI, null, null, null, null);

        if (serviceProviderCursor.moveToFirst() && (new ServiceProvider(serviceProviderCursor)).zenrinMaps && zenrinMapsAccountCursor.moveToFirst()) {
            mapButton.setVisibility(VISIBLE);
        }
        mapButton.setOnClickListener(v -> {
          ViewDisabler.disable(v);
          showMapFragment();
        });

        getView().findViewById(R.id.operation_schedule_list_button).setOnClickListener(v -> {
            ViewDisabler.disable(v);
            showOperationScheduleListFragment();
        });

        Button changePhaseButton = getView().findViewById(R.id.change_phase_button);
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
