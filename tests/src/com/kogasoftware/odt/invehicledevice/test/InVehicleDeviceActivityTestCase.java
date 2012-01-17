package com.kogasoftware.odt.invehicledevice.test;

import java.util.concurrent.atomic.AtomicBoolean;

import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;

public class InVehicleDeviceActivityTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {
	public InVehicleDeviceActivityTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	InVehicleDeviceActivity a = null;

	@Override
	protected void tearDown() {
	}

	@Override
	protected void setUp() {
		a = getActivity();
	}

	/**
	 * UIスレッドが生きているかのチェック
	 * 
	 * @throws InterruptedException
	 */
	public void testActivityRunning() throws InterruptedException {
		final AtomicBoolean running = new AtomicBoolean(false);
		assertEquals(running.get(), false);
		a.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				running.set(true);
			}
		});
		Thread.sleep(300);
		assertEquals(running.get(), true);
	}
}
