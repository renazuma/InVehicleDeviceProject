package com.kogasoftware.odt.invehicledevice.ui;

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
	private final Stopwatch stopwatch = new Stopwatch();
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
		// Are we charging / charged?
		Integer status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS,
				-1);
		Integer level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,
				-1);
		Boolean isCharging = status
				.equals(BatteryManager.BATTERY_STATUS_CHARGING)
				|| status.equals(BatteryManager.BATTERY_STATUS_FULL);
		if (isCharging) {
			int newResourceId = level > 95 ? R.drawable.battery_full
					: R.drawable.battery_charging;
			if (resourceId != newResourceId) {
				resourceId = newResourceId;
				blinkView.setImageResource(resourceId);
			}
			stopwatch.reset().start();
			if (!blinkView.isShown()) {
				blinkView.setVisibility(View.VISIBLE);
			}
			return;
		}
		if (resourceId != R.drawable.battery_alert) {
			resourceId = R.drawable.battery_alert;
			blinkView.setImageResource(resourceId);
		}
		if (stopwatch.elapsedMillis() > BATTERY_DISCONNECTED_LIMIT_MILLIS
				|| !stopwatch.isRunning()) {
			stopwatch.reset().start();
			if (fragmentManager
					.findFragmentByTag(BATTERY_ALERT_DIALOG_FRAGMENT_TAG) == null) {
				BatteryAlertDialogFragment batteryAlertDialogFragment = new BatteryAlertDialogFragment();
				batteryAlertDialogFragment.show(fragmentManager,
						BATTERY_ALERT_DIALOG_FRAGMENT_TAG);
			}
		}
		if (blinkView.isShown()) {
			blinkView.setVisibility(View.INVISIBLE);
		} else {
			blinkView.setVisibility(View.VISIBLE);
		}
	}
}
