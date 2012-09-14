package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
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
		super("com.kogasoftware.odt.invehicledevice", EmptyActivity.class);
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

		Handler h = InVehicleDeviceService.getActivityHandler(getActivity());
		cdl.countDown();
		return h;
	}

	/**
	 * テストプロジェクト内のリソースIDからレイアウトを読み込みActivityへ配置する。読み込まれたレイアウトを返す。
	 */
	protected View inflateAndAddTestLayout(final int testLayoutResourceId)
			throws InterruptedException {
		final AtomicReference<View> v = new AtomicReference<View>();
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				Activity a = getActivity();
				LayoutInflater li = a.getLayoutInflater(); // Activity用のLayoutInflaterを使う
				XmlResourceParser p = getInstrumentation().getContext()
						.getResources().getXml(testLayoutResourceId);
				v.set(li.inflate(p, null));
				ViewGroup vg = (ViewGroup) getActivity().findViewById(
						android.R.id.content);
				vg.addView(v.get());
			}
		});
		return v.get();
	}

	public void runOnUiThreadSync(Runnable runnable)
			throws InterruptedException {
		TestUtil.runOnUiThreadSync(getActivity(), runnable);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Instrumentation i = getInstrumentation();
		TestUtil.disableAutoStart(i.getContext());
		Thread.sleep(2000); // 別タスクがStatusを保存するかもしれないため、一定時間待つ
		a = getActivity();
		solo = new Solo(i, a);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			if (solo != null) {
				solo.finishOpenedActivities();
			} else if (a != null) {
				a.finish();
			}
		} finally {
			super.tearDown();
		}
	}
}
