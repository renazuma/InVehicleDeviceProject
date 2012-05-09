package com.kogasoftware.odt.invehicledevice.test.util;

import android.view.View;

import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;

public class TestUtil {
	public static Boolean waitForStartUi(final InVehicleDeviceActivity activity) throws InterruptedException {
		Thread t = new Thread() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					if (activity.findViewById(android.R.id.content).getVisibility() == View.VISIBLE) {
						return;
					}
				}
			}
		};
		t.start();
		t.join(20 * 1000);
		if (t.isAlive()) {
			t.interrupt();
			return false;
		}
		return true;
	}
}
