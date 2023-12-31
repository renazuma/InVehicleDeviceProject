package com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.fragment.listflow.utils.FlickUnneededListView;
import com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.utils.OperationPhase;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationSchedulesSyncFragmentAbstract;
import com.kogasoftware.odt.invehicledevice.view.fragment.phaseflow.utils.arrayadapter.PassengerRecordArrayAdapter;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 停車中画面
 */
public class PlatformPhaseFragment extends OperationSchedulesSyncFragmentAbstract {
    public static PlatformPhaseFragment newInstance() {
        return new PlatformPhaseFragment();
    }

    private static final Integer BLINK_MILLIS = 500;
    private static final Integer UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS = 2000;
    private static final String TAG = PlatformPhaseFragment.class.getSimpleName();
    private static final int LOADER_ID = 1;
    private Handler handler;
    private TextView minutesRemainingTextView;
    private TextView currentPlatformNameTextView;
    private FlickUnneededListView passengerRecordListView;
    private PassengerRecordArrayAdapter adapter;

    private final Runnable blink = new Runnable() {
        @Override
        public void run() {
            if (adapter != null) {
                adapter.toggleBlink();
            }
            handler.postDelayed(this, BLINK_MILLIS);
        }
    };

    private final Runnable updateMinutesRemaining = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(updateMinutesRemaining, UPDATE_MINUTES_REMAINING_INTERVAL_MILLIS);

            if (operationPhase == null || !operationPhase.isExistNext()) {
                return;
            }

            DateTime now = DateTime.now();
            minutesRemainingTextView.setText("");

            OperationSchedule representativeOS = operationPhase.getCurrentRepresentativeOS();

            if (representativeOS == null || representativeOS.departureEstimate == null) {
                return;
            }

            Integer minutesRemaining = (int) (representativeOS.departureEstimate.getMillis() / 1000 / 60 - now.getMillis() / 1000 / 60);
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
            String dateString = dateFormat.format(representativeOS.departureEstimate.toDate());

            minutesRemainingTextView.setText(Html.fromHtml(
                    String.format(getResources().getString(R.string.minutes_remaining_to_depart_html),
                            dateString,
                            minutesRemaining)));

        }
    };

    private OperationPhase operationPhase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.platform_phase_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();
        View view = getView();
        minutesRemainingTextView = view.findViewById(R.id.minutes_remaining_text_view);
        currentPlatformNameTextView = view.findViewById(R.id.now_platform_text_view);
        passengerRecordListView = view.findViewById(R.id.reservation_list_view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLoaderManager().destroyLoader(LOADER_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "platform: " + Objects.firstNonNull(currentPlatformNameTextView.getText(), "(None)"));
        handler.post(updateMinutesRemaining);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateMinutesRemaining);
        handler.removeCallbacks(blink);
    }

    @Override
    protected void onSchedulesRefresh(
            LinkedList<OperationSchedule> newOperationSchedules,
            LinkedList<PassengerRecord> newPassengerRecords,
            Boolean phaseChanged) {


        operationPhase = new OperationPhase(newOperationSchedules, newPassengerRecords);

        setMinutesRemainingTextView();

        if (!operationPhase.isExistCurrent()) {
            currentPlatformNameTextView.setText("");
            return;
        }

        setPlatformNameTextView();
        setPassengerList(phaseChanged);

        handler.removeCallbacks(blink);
        handler.postDelayed(blink, 500);
    }

    private void setPassengerList(Boolean phaseChanged) {
        Phase phase = operationPhase.getPhase();

        if (phaseChanged) {
            adapter = new PassengerRecordArrayAdapter(this, phase, operationPhase.getCurrentOperationSchedules());
            ListView listView = new ListView(getActivity());
            listView.setAdapter(adapter);
            passengerRecordListView.replaceListView(listView);
        }

        if (phase.equals(Phase.PLATFORM_GET_OFF)) {
            List<PassengerRecord> get_off_passenger_records = Lists.newArrayList();
            for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
                get_off_passenger_records.addAll(operationSchedule.getGetOffScheduledPassengerRecords(operationPhase.passengerRecords));
            }
            adapter.update(get_off_passenger_records);
        } else {
            List<PassengerRecord> get_on_passenger_records = Lists.newArrayList();
            for (OperationSchedule operationSchedule : operationPhase.getCurrentOperationSchedules()) {
                get_on_passenger_records.addAll(operationSchedule.getGetOnScheduledPassengerRecords(operationPhase.passengerRecords));
            }
            adapter.update(get_on_passenger_records);
        }
    }

    private void setPlatformNameTextView() {
        if (isLastOperationSchedules()) {
            currentPlatformNameTextView.setText("現在最終乗降場です");
            Log.i(TAG, "last platform");
        } else {
            OperationSchedule representativeOS = operationPhase.getCurrentRepresentativeOS();
            Log.i(TAG, "platform id=" + representativeOS.platformId + " name=" + representativeOS.name);
            currentPlatformNameTextView.setText(Html.fromHtml(String.format(
                    getResources().getString(R.string.now_platform_is_html), representativeOS.name)));
        }
    }

    private void setMinutesRemainingTextView() {
        if (isLastOperationSchedules()) {
            minutesRemainingTextView.setVisibility(View.GONE);
        } else {
            minutesRemainingTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isLastOperationSchedules() {
        return !operationPhase.isExistNext();
    }
}
