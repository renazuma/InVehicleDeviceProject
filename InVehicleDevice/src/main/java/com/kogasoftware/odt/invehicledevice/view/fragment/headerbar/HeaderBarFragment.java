package com.kogasoftware.odt.invehicledevice.view.fragment.headerbar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.PlatformMemoFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.SignInFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationPhase;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationSchedulesSyncFragmentAbstract;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ViewDisabler;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

/**
 * 時刻やバッテリー状況などを表示する領域
 */
public class HeaderBarFragment extends OperationSchedulesSyncFragmentAbstract {
    private static final int UPDATE_TIME_INTERVAL_MILLIS = 3000;
    private static final int OPERATION_SCHEDULES_LOADER_ID = 1;
    private static final int PASSENGER_RECORDS_LOADER_ID = 2;

    private Handler handler;

    public static HeaderBarFragment newInstance() {
        return new HeaderBarFragment();
    }

    private final Runnable updateTime = new Runnable() {
        @Override
        public void run() {
            Date now = new Date(DateTimeUtils.currentTimeMillis());
            DateFormat f = new SimpleDateFormat(getString(R.string.present_time_format), Locale.US);
            ((TextView) getView().findViewById(R.id.present_time_text_view)).setText(f.format(now));
            handler.postDelayed(this, UPDATE_TIME_INTERVAL_MILLIS);
        }
    };

    /**
     * バッテリー状態を監視
     */
    private Runnable blinkBatteryAlert;

    /**
     * ネットワーク状態を監視
     */
    private Runnable networkAlert;

    private OperationPhase operationPhase;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler();

        getView().findViewById(R.id.open_login_image_view).setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity instanceof InVehicleDeviceActivity) {
                SignInFragment.showModal((InVehicleDeviceActivity) activity);
            }
        });

        // 各ライフサイクルで適宜使用されるRunnableの定義だが、getViewが必要なのでここで初期化している。
        blinkBatteryAlert = new BatteryAlerter(
                getActivity().getApplicationContext(),
                handler,
                getView().findViewById(R.id.battery_alert_image_view),
                getFragmentManager());
        networkAlert = new NetworkAlerter(
                getActivity().getApplicationContext(),
                handler,
                getView().findViewById(R.id.network_strength_image_view),
                getFragmentManager()
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.header_bar_fragment, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTime);
        handler.removeCallbacks(blinkBatteryAlert);
        handler.removeCallbacks(networkAlert);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(updateTime);
        handler.post(blinkBatteryAlert);
        handler.post(networkAlert);
    }

    private void updateView() {
        View view = getView();

        view.setBackgroundColor(getPhaseColor());

        ((TextView) view.findViewById(R.id.phase_text_view)).setText(getPhaseText());

        // フェーズに合わせてメモボタンを設定
        final Button platformMemoButton = view.findViewById(R.id.platform_memo_button);
        platformMemoButton.setVisibility(View.INVISIBLE);
        if (isShowMemoButtonPattern()) {
            platformMemoButton.setVisibility(View.VISIBLE);
            platformMemoButton.setOnClickListener(v -> {
                ViewDisabler.disable(v);
                showPlatformMemoFragment();
            });
        }
    }

    private Boolean isShowMemoButtonPattern() {
        return (operationPhase.getPhase() != Phase.FINISH
                && operationPhase.isExistCurrent()
                && StringUtils.isNotBlank(operationPhase.getCurrentRepresentativeOS().memo));
    }

    private void showPlatformMemoFragment() {
        if (!isAdded()) {
            return;
        }

        Fragments.showModalFragment(
                getFragmentManager(),
                PlatformMemoFragment.newInstance(operationPhase.getCurrentRepresentativeOS()));
    }

    private int getPhaseColor() {
        switch (operationPhase.getPhase()) {
            case DRIVE:
                return ContextCompat.getColor(getContext(), R.color.drive_phase_header);
            case FINISH:
                return ContextCompat.getColor(getContext(), R.color.finish_phase_header);
            case PLATFORM_GET_ON:
                return ContextCompat.getColor(getContext(), R.color.get_on_phase_header);
            case PLATFORM_GET_OFF:
                return ContextCompat.getColor(getContext(), R.color.get_off_phase_header);
            default:
                break;
        }
        return Color.WHITE;
    }

    private String getPhaseText() {
        switch (operationPhase.getPhase()) {
            case DRIVE:
                return "運行中";
            case FINISH:
                return "運行終了";
            case PLATFORM_GET_OFF:
                return "降車中";
            case PLATFORM_GET_ON:
                return "乗車中";
        }
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLoaderManager().destroyLoader(PASSENGER_RECORDS_LOADER_ID);
        getLoaderManager().destroyLoader(OPERATION_SCHEDULES_LOADER_ID);
    }

    @Override
    protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
            LinkedList<OperationSchedule> operationSchedules,
            LinkedList<PassengerRecord> passengerRecords) {

        this.operationPhase = new OperationPhase(operationSchedules, passengerRecords);

        updateView();
    }
}
