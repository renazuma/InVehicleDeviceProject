package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.concurrent.TimeUnit;

import android.view.View;

import com.google.common.util.concurrent.Uninterruptibles;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class TestUtil {
	public static Boolean waitForStartUi(final InVehicleDeviceActivity activity) throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
					if (activity.findViewById(android.R.id.content).getVisibility() == View.VISIBLE) {
						return;
					}
				}
			}
		};
		t.start();
		t.join(60 * 1000);
		if (t.isAlive()) {
			t.interrupt();
			return false;
		}
		return true;
	}
}
