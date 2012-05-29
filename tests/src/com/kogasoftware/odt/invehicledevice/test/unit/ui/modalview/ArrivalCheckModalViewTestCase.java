package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import java.util.concurrent.CountDownLatch;

import android.view.View;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ArrivalCheckModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.StartCheckModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class ArrivalCheckModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	StatusAccess sa;
	ArrivalCheckModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
		mv = (ArrivalCheckModalView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_arrival_check_modal_view);
		cl.registerEventListener(mv);
		mv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));
		
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				OperationSchedule os = new OperationSchedule();
				Platform p = new Platform();
				p.setName("乗降場X");
				os.setPlatform(p);
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os);
			}
		});
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
			assertEquals(cl2.countRegisteredClass(StartCheckModalView.class)
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

		cl.postEvent(new ArrivalCheckModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
	}

	public void test到着予定の乗降場名が表示される1() throws InterruptedException {
		callTest到着予定の乗降場名が表示される("乗降場K");	
	}

	public void test到着予定の乗降場名が表示される2() throws InterruptedException {
		callTest到着予定の乗降場名が表示される("乗降場L");	
	}

	public void test到着予定の乗降場名が表示される3() throws InterruptedException {
		callTest到着予定の乗降場名が表示される("乗降場M");	
	}
	
	protected void callTest到着予定の乗降場名が表示される(final String name) throws InterruptedException {
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				OperationSchedule os = new OperationSchedule();
				Platform p = new Platform();
				p.setName(name);
				os.setPlatform(p);
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os);
			}
		});
		testShowEvent();
		assertTrue(solo.searchText(name, true));
	}	
	
	public void test到着するボタンを押すとEnterPlatformPhaseEvent通知() throws Exception {
		testShowEvent();
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.registerEventListener(new Function<EnterPlatformPhaseEvent, Void>() {
			@Subscribe
			@Override
			public Void apply(EnterPlatformPhaseEvent e) {
				cdl.countDown();
				return null;
			}
		});
		solo.clickOnView(solo.getView(R.id.arrival_button));
		cdl.await();
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.arrival_check_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}
}
