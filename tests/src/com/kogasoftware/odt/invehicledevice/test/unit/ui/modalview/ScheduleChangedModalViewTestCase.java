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
import com.kogasoftware.odt.invehicledevice.logic.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleMergedEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleChangedModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleModalView;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class ScheduleChangedModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	StatusAccess sa;
	CommonLogic cl;
	ScheduleChangedModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);

		mv = (ScheduleChangedModalView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_schedule_changed_modal_view);
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

	public void testEventBusに自動で登録される() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(R.layout.in_vehicle_device);
			}
		});
		CommonLogic cl2 = newCommonLogic();
		try {
			assertEquals(
					cl2.countRegisteredClass(ScheduleChangedModalView.class)
							.intValue(), 1);
		} finally {
			cl2.dispose();
		}
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void testUpdatedOperationScheduleMergedEvent_1()
			throws InterruptedException {
		final String body = "Hello";
		sa.write(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(Status status) {
				VehicleNotification vn = new VehicleNotification();
				vn.setBody(body);
				vn.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
				status.sendLists.repliedVehicleNotifications.clear();
				status.receivedOperationScheduleChangedVehicleNotifications
						.clear();
				status.receivedOperationScheduleChangedVehicleNotifications
						.add(vn);
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

		cl.postEvent(new UpdatedOperationScheduleMergedEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(body));
	}

	/**
	 * VehicleNotificationが無い場合ShowEventを受け取っても表示されない
	 */
	public void testUpdatedOperationScheduleMergedEvent_2()
			throws InterruptedException {
		sa.write(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(Status status) {
				status.receivedOperationScheduleChangedVehicleNotifications
						.clear();
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

		cl.postEvent(new NotificationModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);
	}

	public void test閉じるボタンを押すと消える() throws Exception {
		testUpdatedOperationScheduleMergedEvent_1();
		solo.clickOnView(solo.getView(R.id.schedule_changed_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	public void test予定を確認ボタンを押すと消えてScheduleModalView_ShowEvent発生()
			throws Exception {
		testUpdatedOperationScheduleMergedEvent_1();
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.registerEventListener(new Function<ScheduleModalView.ShowEvent, Void>() {
			@Subscribe
			@Override
			public Void apply(ScheduleModalView.ShowEvent e) {
				cdl.countDown();
				return null;
			}
		});
		solo.clickOnView(solo.getView(R.id.schedule_confirm_button));
		cdl.await();
	}
}
