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
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.PassengerRecordArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.modalview.StartCheckModalView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;

public class StartCheckModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	StatusAccess sa;
	StartCheckModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
		mv = (StartCheckModalView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_start_check_modal_view);
		cl.registerEventListener(mv);
		mv.setCommonLogic(new CommonLogicLoadCompleteEvent(cl));

		sa.write(new Writer() { // TODO もっとスマートにする
			@Override
			public void write(Status status) {
				OperationSchedule os1 = new OperationSchedule();
				OperationSchedule os2 = new OperationSchedule();
				os1.setPlatform(new Platform());
				os2.setPlatform(new Platform());
				status.remainingOperationSchedules.clear();
				status.remainingOperationSchedules.add(os1);
				status.remainingOperationSchedules.add(os2);
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

		cl.postEvent(new StartCheckModalView.ShowEvent(
				new PassengerRecordArrayAdapter(getInstrumentation()
						.getContext(), cl)));
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
	}

	public void test出発するボタンを押すとEnterDrivePhaseEvent通知() throws Exception {
		testShowEvent();
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.registerEventListener(new Function<EnterDrivePhaseEvent, Void>() {
			@Subscribe
			@Override
			public Void apply(EnterDrivePhaseEvent e) {
				cdl.countDown();
				return null;
			}
		});
		solo.clickOnView(solo.getView(R.id.start_button));
		cdl.await();
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.start_check_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}
}
