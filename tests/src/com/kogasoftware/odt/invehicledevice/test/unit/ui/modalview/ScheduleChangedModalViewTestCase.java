package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import java.util.concurrent.TimeUnit;

import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleMergedEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.Subscriber;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleChangedModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleModalView;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.model.VehicleNotifications;

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

	public void xtestEventBusに自動で登録される() throws Exception {
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
	 * ShowEventを受け取ると表示され、ScheduleModalView.HideEventが発行される
	 */
	public void testUpdatedOperationScheduleMergedEvent_1()
			throws InterruptedException {
		Subscriber<ScheduleModalView.HideEvent> s = Subscriber.of(
				ScheduleModalView.HideEvent.class, cl);

		final String body = "こんにちは";
		sa.write(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(Status status) {
				VehicleNotification vn = new VehicleNotification();
				vn.setBody(body);
				vn.setNotificationKind(VehicleNotifications.NotificationKind.SCHEDULE_CHANGED);
				status.repliedVehicleNotifications.clear();
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
		assertTrue(solo.searchText(body, true));
		assertTrue(s.cdl.await(3, TimeUnit.SECONDS));
	}

	/**
	 * ShowEventを二度受け取ると、内容が追記される
	 */
	public void testUpdatedOperationScheduleMergedEvent_2()
			throws InterruptedException {
		Subscriber<ScheduleModalView.HideEvent> s = Subscriber.of(
				ScheduleModalView.HideEvent.class, cl);

		final String body1 = "連絡1";
		final String body2 = "連絡2";
		final String body3 = "連絡3";
		sa.write(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(Status status) {
				VehicleNotification vn1 = new VehicleNotification();
				vn1.setBody(body1);
				vn1.setNotificationKind(VehicleNotifications.NotificationKind.SCHEDULE_CHANGED);

				VehicleNotification vn2 = new VehicleNotification();
				vn2.setBody(body2);
				vn2.setNotificationKind(VehicleNotifications.NotificationKind.SCHEDULE_CHANGED);

				status.repliedVehicleNotifications.clear();
				status.receivedOperationScheduleChangedVehicleNotifications
						.clear();
				status.receivedOperationScheduleChangedVehicleNotifications
						.add(vn1);
				status.receivedOperationScheduleChangedVehicleNotifications
						.add(vn2);
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

		cl.postEvent(new UpdatedOperationScheduleMergedEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(body1, true));
		assertTrue(solo.searchText(body2, true));
		assertFalse(solo.searchText(body3, true));
		assertTrue(s.cdl.await(3, TimeUnit.SECONDS));

		sa.write(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(Status status) {
				VehicleNotification vn = new VehicleNotification();
				vn.setBody(body3);
				vn.setNotificationKind(VehicleNotifications.NotificationKind.SCHEDULE_CHANGED);
				status.receivedOperationScheduleChangedVehicleNotifications
						.add(vn);
			}
		});

		cl.postEvent(new UpdatedOperationScheduleMergedEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(body1, true));
		assertTrue(solo.searchText(body2, true));
		assertTrue(solo.searchText(body3, true));
		assertTrue(s.cdl.await(3, TimeUnit.SECONDS));
	}

	/**
	 * VehicleNotificationが無い場合ShowEventを受け取っても表示されず、ScheduleModalView.
	 * HideEventも発行されない
	 */
	public void testUpdatedOperationScheduleMergedEvent_3()
			throws InterruptedException {
		Subscriber<ScheduleModalView.HideEvent> s = Subscriber.of(
				ScheduleModalView.HideEvent.class, cl);

		sa.write(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(Status status) {
				status.receivedOperationScheduleChangedVehicleNotifications
						.clear();
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

		cl.postEvent(new ScheduleModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);
		assertFalse(s.cdl.await(3, TimeUnit.SECONDS));
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testUpdatedOperationScheduleMergedEvent_1();
		solo.clickOnView(solo.getView(R.id.schedule_changed_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	public void test予定を確認ボタンを押すと消えてScheduleModalView_ShowEvent発生()
			throws Exception {
		testUpdatedOperationScheduleMergedEvent_1();
		Subscriber<ScheduleModalView.ShowEvent> s = Subscriber.of(
				ScheduleModalView.ShowEvent.class, cl);
		solo.clickOnView(solo.getView(R.id.schedule_confirm_button));
		s.cdl.await();
	}
}
