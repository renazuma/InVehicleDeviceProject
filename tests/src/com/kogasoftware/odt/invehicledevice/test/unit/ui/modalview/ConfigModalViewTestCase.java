package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.res.XmlResourceParser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ConfigModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.PauseModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.StopCheckModalView;

public class ConfigModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	ConfigModalView cmv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = new CommonLogic(getActivity(), getActivityHandler());
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				Activity a = getActivity();
				LayoutInflater li = a.getLayoutInflater(); // Activity用のLayoutInflaterを使う
				XmlResourceParser p = getInstrumentation()
						.getContext()
						.getResources()
						.getXml(com.kogasoftware.odt.invehicledevice.test.R.layout.test_config_modal_view);
				cmv = (ConfigModalView) li.inflate(p, null);
				ViewGroup vg = (ViewGroup) a.findViewById(android.R.id.content);
				vg.addView(cmv);
			}
		});
		cl.getEventBus().register(cmv);
		cmv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void testShowEvent() throws InterruptedException {
		cl.getEventBus().post(new ConfigModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();
		assertTrue(cmv.isShown());
		assertEquals(cmv.getVisibility(), View.VISIBLE);
	}

	public void test中止ボタンを押すとStopCheckModalView_ShowEvent通知() throws Exception {
		testShowEvent();
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.getEventBus().register(
				new Function<StopCheckModalView.ShowEvent, Void>() {
					@Subscribe
					@Override
					public Void apply(StopCheckModalView.ShowEvent e) {
						cdl.countDown();
						return null;
					}
				});
		solo.clickOnView(solo.getView(R.id.stop_check_button));
		cdl.await();
	}

	public void test停止ボタンを押すとPauseModalView_ShowEvent通知() throws Exception {
		testShowEvent();
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.getEventBus().register(
				new Function<PauseModalView.ShowEvent, Void>() {
					@Subscribe
					@Override
					public Void apply(PauseModalView.ShowEvent e) {
						cdl.countDown();
						return null;
					}
				});
		solo.clickOnView(solo.getView(R.id.pause_button));
		cdl.await();
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.config_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(cmv.isShown());
	}
}