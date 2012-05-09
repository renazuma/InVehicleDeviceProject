package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import java.util.concurrent.CountDownLatch;

import android.view.View;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.StopCheckModalView;

public class StopCheckModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	StopCheckModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		cl = newCommonLogic();
		mv = (StopCheckModalView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_stop_check_modal_view);
		cl.registerEventListener(mv);
		mv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	public void xtestEventBusに自動で登録される() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(R.layout.in_vehicle_device);
			}
		});
		CommonLogic cl2 = newCommonLogic();
		try {
			assertEquals(cl2.countRegisteredClass(StopCheckModalView.class)
					.intValue(), 1);
		} finally {
			cl2.dispose();
		}
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void testShowEvent() throws InterruptedException {
		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

		cl.postEvent(new StopCheckModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
	}

	public void test中止するボタンを押すとStopEvent通知() throws Exception {
		testShowEvent();
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.registerEventListener(new Function<StopEvent, Void>() {
			@Subscribe
			@Override
			public Void apply(StopEvent e) {
				cdl.countDown();
				return null;
			}
		});
		solo.clickOnView(solo.getView(R.id.stop_button));
		cdl.await();
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.stop_check_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}
}
