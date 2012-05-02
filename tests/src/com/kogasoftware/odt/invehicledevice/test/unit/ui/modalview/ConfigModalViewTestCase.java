package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;
import android.widget.FrameLayout;

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

	protected AttributeSet getDefaultAttributeSet() {
		AttributeSet as = null;
		Resources r = getInstrumentation().getContext().getResources();
		XmlResourceParser parser = r
				.getLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.testcameraoverlay);
		int state = 0;
		do {
			try {
				state = parser.next();
			} catch (XmlPullParserException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (state == XmlPullParser.START_TAG) {
				if (parser.getName().equals("TextView")) {
					as = Xml.asAttributeSet(parser);
					break;
				}
			}
		} while (state != XmlPullParser.END_DOCUMENT);
		return as;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = new CommonLogic(getActivity(), getActivityHandler());
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				FrameLayout fl = (FrameLayout) getActivity().findViewById(
						android.R.id.content);
				cmv = new ConfigModalView(getActivity(),
						getDefaultAttributeSet());
				fl.addView(cmv);
				cmv.setVisibility(View.VISIBLE);
			}
		});
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
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				cmv.setVisibility(View.GONE);
			}
		});
		Thread.sleep(500);
		cl.getEventBus().post(new ConfigModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();
		Thread.sleep(500);
		assertTrue(cmv.isShown());
		assertEquals(cmv.getVisibility(), View.VISIBLE);
	}

	public void test中止ボタンを押すとStopCheckModalView_ShowEvent通知() throws Exception {
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
		solo.clickOnView(solo.getView(R.id.config_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(cmv.isShown());
	}
}