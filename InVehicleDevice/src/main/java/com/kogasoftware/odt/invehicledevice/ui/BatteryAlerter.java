package com.kogasoftware.odt.invehicledevice.ui;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Stopwatch;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.fragment.InformationBarFragment.BatteryAlertDialogFragment;

public class BatteryAlerter implements Runnable {
	public static final int BATTERY_ALERT_BLINK_MILLIS = 500;
	public static final int CHECK_BATTERY_MILLIS = 10 * 1000;
	public static final int BATTERY_DISCONNECTED_LIMIT_MILLIS = 10 * 60 * 1000;
	public static final String BATTERY_ALERT_DIALOG_FRAGMENT_TAG = "BatteryAlertDialogFragmentTag";
	private final Handler handler;
	private final Context context;
	private final ImageView blinkView;
	private final Stopwatch dialogStopwatch = new Stopwatch();
	private final Stopwatch blinkStopwatch = new Stopwatch();
	private final FragmentManager fragmentManager;
	private int resourceId = -1;

	public BatteryAlerter(Context context, Handler handler,
			ImageView blinkView, FragmentManager fragmentManager) {
		this.context = context;
		this.handler = handler;
		this.blinkView = blinkView;
		this.fragmentManager = fragmentManager;
	}

	@Override
	public void run() {
		handler.postDelayed(this, BATTERY_ALERT_BLINK_MILLIS);
		// "http://developer.android.com/training/monitoring-device-state/battery-monitoring.html"
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, intentFilter);
		Integer status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,
				-1);
		Integer plugged = batteryStatus.getIntExtra(
				BatteryManager.EXTRA_PLUGGED, -1);
		Boolean isCharging = status
				.equals(BatteryManager.BATTERY_STATUS_CHARGING)
				|| status.equals(BatteryManager.BATTERY_STATUS_FULL)
				|| plugged.equals(BatteryManager.BATTERY_PLUGGED_AC)
				|| plugged.equals(BatteryManager.BATTERY_PLUGGED_USB);

		Integer level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,
				-1);

		// チャージ中やfullの場合、何もしない
		if (isCharging) {
			if (blinkStopwatch.isRunning()) {
				blinkStopwatch.stop().reset();
			}
			if (dialogStopwatch.isRunning()) {
				dialogStopwatch.stop().reset();
			}
			int newResourceId = level > 95 ? R.drawable.battery_full
					: R.drawable.battery_charging;
			if (resourceId != newResourceId) {
				resourceId = newResourceId;
				blinkView.setImageResource(resourceId);
			}
			if (!blinkView.isShown()) {
				blinkView.setVisibility(View.VISIBLE);
			}
			return;
		}

		// チャージ中やfullでない場合、ストップウォッチを開始し、しきい値以内の場合は何もしない
		if (!blinkStopwatch.isRunning()) {
			blinkStopwatch.start();
			return;
		}
		if (blinkStopwatch.elapsed(TimeUnit.MILLISECONDS) < 5000) {
			return;
		}

		// しきい値を超えた場合、画像を点滅
		if (resourceId != R.drawable.battery_alert) {
			resourceId = R.drawable.battery_alert;
			blinkView.setImageResource(resourceId);
		}
		if (blinkView.isShown()) {
			blinkView.setVisibility(View.INVISIBLE);
		} else {
			blinkView.setVisibility(View.VISIBLE);
		}

		// ダイアログの表示
		if (dialogStopwatch.elapsed(TimeUnit.MILLISECONDS) > BATTERY_DISCONNECTED_LIMIT_MILLIS
				|| !dialogStopwatch.isRunning()) {
			dialogStopwatch.reset().start();
			if (fragmentManager
					.findFragmentByTag(BATTERY_ALERT_DIALOG_FRAGMENT_TAG) == null) {
				BatteryAlertDialogFragment batteryAlertDialogFragment = new BatteryAlertDialogFragment();
				batteryAlertDialogFragment.show(fragmentManager,
						BATTERY_ALERT_DIALOG_FRAGMENT_TAG);
			}
		}
	}
}