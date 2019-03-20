package com.kogasoftware.odt.invehicledevice.view.fragment.informationbar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationSchedulesSyncFragmentAbstract;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.PlatformMemoFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.modal.SignInFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.Fragments;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.ViewDisabler;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 時刻やバッテリー状況などを表示する領域
 */
public class InformationBarFragment	extends OperationSchedulesSyncFragmentAbstract {
	private static final int UPDATE_TIME_INTERVAL_MILLIS = 3000;
	private static final int OPERATION_SCHEDULES_LOADER_ID = 1;
	private static final int PASSENGER_RECORDS_LOADER_ID = 2;

	private Handler handler;

	public static InformationBarFragment newInstance() {
		InformationBarFragment fragment = new InformationBarFragment();
		return fragment;
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

	private final List<PassengerRecord> passengerRecords = Lists.newLinkedList();
	private final List<OperationSchedule> operationSchedules = Lists.newLinkedList();

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		handler = new Handler();

		((ImageView) getView().findViewById(R.id.open_login_image_view)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
				if (activity instanceof InVehicleDeviceActivity) {
		             SignInFragment.showModal((InVehicleDeviceActivity)activity);
				}
			}
		});

		// 各ライフサイクルで適宜使用されるRunnableの定義だが、getViewが必要なのでここで初期化している。
		blinkBatteryAlert = new BatteryAlerter(
				getActivity().getApplicationContext(),
				handler,
				(ImageView) getView().findViewById(R.id.battery_alert_image_view),
				getFragmentManager());
		networkAlert = new NetworkAlerter(
				getActivity().getApplicationContext(),
				handler,
				(ImageView) getView().findViewById(R.id.network_strength_image_view),
				getFragmentManager()
		);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.information_bar_fragment, container,false);
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

	public void updateView() {
		View view = getView();

		view.setBackgroundColor(getPhaseColor());

		((TextView)view.findViewById(R.id.phase_text_view)).setText(getPhaseText());

		// フェーズに合わせてメモボタンを設定
		final Button platformMemoButton = (Button) view.findViewById(R.id.platform_memo_button);
		platformMemoButton.setVisibility(View.INVISIBLE);
		if (isShowMemoButtonPattern()) {
			platformMemoButton.setVisibility(View.VISIBLE);
			platformMemoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewDisabler.disable(v);
					showPlatformMemoFragment(OperationSchedule.getCurrent(operationSchedules));
				}
			});
		}
	}

	private Boolean isShowMemoButtonPattern() {
		final OperationSchedule operationSchedule = OperationSchedule.getCurrent(operationSchedules);
		return (OperationSchedule.getPhase(operationSchedules, passengerRecords) != Phase.FINISH
						&& operationSchedule != null
						&& StringUtils.isNotBlank(operationSchedule.memo));
	}

	public void showPlatformMemoFragment(OperationSchedule operationSchedule) {
		if (!isAdded()) { return; }

		Fragments.showModalFragment(getFragmentManager(), PlatformMemoFragment.newInstance(operationSchedule));
	}

	private int getPhaseColor() {
		switch (OperationSchedule.getPhase(operationSchedules, passengerRecords)) {
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
		switch (OperationSchedule.getPhase(operationSchedules, passengerRecords)) {
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
			Phase phase, LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords) {
		this.operationSchedules.clear();
		this.passengerRecords.clear();
		this.operationSchedules.addAll(operationSchedules);
		this.passengerRecords.addAll(passengerRecords);
		updateView();
	}
}
