package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTimeUtils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.empty.EmptyRunnable;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation.Phase;
import com.kogasoftware.odt.invehicledevice.ui.BatteryAlerter;
import com.kogasoftware.odt.invehicledevice.ui.BgColorTransitionDrawable;
import com.kogasoftware.odt.invehicledevice.ui.ViewDisabler;
import com.kogasoftware.odt.invehicledevice.ui.fragment.InformationBarFragment.State;

public class InformationBarFragment extends AutoUpdateOperationFragment<State>
		implements EventDispatcher.OnChangeSignalStrengthListener {

	@SuppressWarnings("serial")
	static class State implements Serializable {
		private final Optional<OperationSchedule> operationSchedule;
		private final Phase phase;
		private final Integer operationSchedulesReceivedSequence;

		public State(Operation operation) {
			this.phase = operation.getPhase();
			this.operationSchedule = OperationSchedule.getCurrent(operation.operationSchedules);
			this.operationSchedulesReceivedSequence = operation.operationScheduleReceiveSequence;
		}

		public Integer getOperationSchedulesReceivedSequence() {
			return operationSchedulesReceivedSequence;
		}

		public Phase getPhase() {
			return phase;
		}

		public Optional<OperationSchedule> getOperationSchedule() {
			return operationSchedule;
		}
	}

	private static final int UPDATE_TIME_INTERVAL_MILLIS = 3000;

	private BgColorTransitionDrawable bgColorTransitionDrawable;
	private ImageView networkStrengthImageView;
	private TextView presentTimeTextView;
	private Handler handler;

	public static InformationBarFragment newInstance(Operation operation) {
		return newInstance(new InformationBarFragment(), new State(operation));
	}

	private final Runnable updateTime = new Runnable() {
		@Override
		public void run() {
			Date now = new Date(DateTimeUtils.currentTimeMillis());
			DateFormat f = new SimpleDateFormat(
					getString(R.string.present_time_format), Locale.US);
			presentTimeTextView.setText(f.format(now));
			handler.postDelayed(this, UPDATE_TIME_INTERVAL_MILLIS);
		}
	};

	/**
	 * バッテリー警告ダイアログ
	 */
	public static class BatteryAlertDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setMessage(Html
					.fromHtml("<big><big>充電ケーブルを接続してください</big></big>"));
			builder.setPositiveButton(
					Html.fromHtml("<big><big>確認</big></big>"), null);
			return builder.create();
		}
	}

	/**
	 * バッテリー状態を監視
	 */
	private Runnable blinkBatteryAlert;

	/**
	 * 背景色を変更
	 */
	private Runnable changeBgColor;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		handler = new Handler();
		presentTimeTextView = (TextView) view
				.findViewById(R.id.present_time_text_view);
		networkStrengthImageView = (ImageView) view
				.findViewById(R.id.network_strength_image_view);
		bgColorTransitionDrawable = new BgColorTransitionDrawable(
				getPhaseColor(getState().getPhase()));
		changeBgColor = new Runnable() {
			@Override
			public void run() {
				bgColorTransitionDrawable.changeColor(getPhaseColor(getState().getPhase()));
			}
		};
		View bcwl = view.findViewById(R.id.background_color_workaround_layout);
		bcwl.setBackgroundDrawable(bgColorTransitionDrawable);
		updateView(view);
		blinkBatteryAlert = new EmptyRunnable();
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			blinkBatteryAlert = new BatteryAlerter(getActivity()
					.getApplicationContext(), handler, (ImageView) getView()
					.findViewById(R.id.battery_alert_image_view),
					fragmentManager);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.information_bar_fragment, container,
				false);
	}

	@Override
	public void onPause() {
		super.onPause();
		handler.removeCallbacks(updateTime);
		handler.removeCallbacks(blinkBatteryAlert);
		handler.removeCallbacks(changeBgColor);
	}

	@Override
	public void onResume() {
		super.onResume();
		handler.post(updateTime);
		handler.post(blinkBatteryAlert);
	}

	public void updateView(View view) {
		handler.removeCallbacks(changeBgColor);
		handler.postDelayed(changeBgColor, 400);
		TextView phaseTextView = (TextView) view
				.findViewById(R.id.phase_text_view);
		Boolean showPlatformMemo = true;
		switch (getState().getPhase()) {
		case DRIVE:
			phaseTextView.setText("運行中");
			break;
		case FINISH:
			showPlatformMemo = false;
			phaseTextView.setText("運行終了");
			break;
		case PLATFORM_GET_OFF:
			phaseTextView.setText("降車中");
			break;
		case PLATFORM_GET_ON:
			phaseTextView.setText("乗車中");
			break;
		}

		final Button platformMemoButton = (Button) view
				.findViewById(R.id.platform_memo_button);
		platformMemoButton.setVisibility(View.INVISIBLE);
		if (showPlatformMemo) {
			for (final OperationSchedule operationSchedule : getState()
					.getOperationSchedule().asSet()) {
				for (Platform platform : operationSchedule.getPlatform()
						.asSet()) {
					if (!platform.getMemo().isEmpty()) {
						platformMemoButton.setVisibility(View.VISIBLE);
						platformMemoButton
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if (isRemoving()) {
											return;
										}
										ViewDisabler.disable(v);
										showPlatformMemoFragment(operationSchedule);
									}
								});
					}
				}
			}
		}
	}

	public void showPlatformMemoFragment(OperationSchedule operationSchedule) {
		String tag = "tag:" + PlatformMemoFragment.class.getSimpleName();
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			Fragment old = fragmentManager.findFragmentByTag(tag);
			if (old != null) {
				setCustomAnimation(fragmentManager.beginTransaction()).remove(
						old).commit();
			}
			setCustomAnimation(fragmentManager.beginTransaction()).add(
					R.id.modal_fragment_container,
					PlatformMemoFragment.newInstance(operationSchedule)).commit();
		}
	}

	@Override
	public void onUpdateOperation(Operation operation) {
		setState(new State(operation));
		updateView(getView());
	}

	@Override
	public void onChangeSignalStrength(Integer signalStrengthPercentage) {
		int imageResourceId = R.drawable.network_strength_4;
		if (signalStrengthPercentage.intValue() == 0) {
			imageResourceId = R.drawable.network_strength_0;
		} else if (signalStrengthPercentage.intValue() <= 25) {
			imageResourceId = R.drawable.network_strength_1;
		} else if (signalStrengthPercentage.intValue() <= 50) {
			imageResourceId = R.drawable.network_strength_2;
		} else if (signalStrengthPercentage.intValue() <= 75) {
			imageResourceId = R.drawable.network_strength_3;
		}
		networkStrengthImageView.setImageResource(imageResourceId);
	}

	@Override
	protected Integer getOperationSchedulesReceiveSequence() {
		return getState().getOperationSchedulesReceivedSequence();
	}
}
