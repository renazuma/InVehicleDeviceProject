package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Instrumentation;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;

public class EmptyActivityInstrumentationTestCase2 extends
		ActivityInstrumentationTestCase2<EmptyActivity> {

	public Solo solo;
	public EmptyActivity a;

	public EmptyActivityInstrumentationTestCase2() {
		super(EmptyActivity.class);
	}

	public Handler getActivityHandler() throws InterruptedException {
		// 一定時間取れなかったらInterruptする
		final Thread t = Thread.currentThread();
		final CountDownLatch cdl = new CountDownLatch(1);
		(new Thread() {
			@Override
			public void run() {
				try {
					if (cdl.await(10, TimeUnit.SECONDS)) {
						return;
					}
				} catch (InterruptedException e) {
				}
				t.interrupt();
			}
		}).start();

		Handler h = InVehicleDeviceService.getActivityHandler(a);
		cdl.countDown();
		return h;
	}

	public void runOnUiThreadSync(Runnable runnable, Integer timeoutSeconds)
			throws InterruptedException {
		TestUtil.runOnUiThreadSync(a, runnable, timeoutSeconds);
	}

	public void runOnUiThreadSync(Runnable runnable)
			throws InterruptedException {
		runOnUiThreadSync(runnable, 20);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Instrumentation i = getInstrumentation();
		TestUtil.disableAutoStart(i.getContext());

		// すでに車載器Activityが起動していることがあるので、BACK,HOMEキーを送信して終了
		try {
			i.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
		} catch (SecurityException e) {
		}
		try {
			i.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
		} catch (SecurityException e) {
		}

		a = getActivity();
		solo = new Solo(i, a);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			if (solo != null) {
				solo.finishOpenedActivities();
			}
			if (a != null) {
				a.finish();
			}
		} finally {
			super.tearDown();
		}
	}
}
