package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.ui.activity.EmptyActivity;

public class EmptyActivityInstrumentationTestCase2 extends
		ActivityInstrumentationTestCase2<EmptyActivity> {

	public Solo solo;

	public EmptyActivityInstrumentationTestCase2() {
		super("com.kogasoftware.odt.invehicledevice", EmptyActivity.class);
	}

	public Handler getActivityHandler() throws InterruptedException {
		return CommonLogic.getActivityHandler(getActivity());
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
		MockActivityUnitTestCase.runOnUiThreadSync(getActivity(), runnable);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		solo.finishInactiveActivities();
		super.tearDown();
	}
}
