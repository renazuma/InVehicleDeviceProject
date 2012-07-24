package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleChangedModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleModalView;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import static org.mockito.Mockito.*;

public class ScheduleChangedModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	LocalDataSource sa;
	ScheduleChangedModalView mv;
	ScheduleModalView smv;
	Activity a;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		s = mock(InVehicleDeviceService.class);
		smv = mock(ScheduleModalView.class);
		sa = new LocalDataSource(getActivity());
		mv = new ScheduleChangedModalView(a, s, smv);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestEventBusに自動で登録される() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(R.layout.in_vehicle_device);
			}
		});
	}

	/**
	 * ShowEventを受け取ると表示され、ScheduleModalView.HideEventが発行される
	 */
	public void testUpdatedOperationScheduleMergedEvent_1()
			throws InterruptedException {
//		Subscriber<ScheduleModalView.HideEvent> s = Subscriber.of(
//				ScheduleModalView.HideEvent.class, cl);

		final String body = "こんにちは";
		sa.withWriteLock(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(LocalData status) {
				VehicleNotification vn = new VehicleNotification();
				vn.setBody(body);
				vn.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
				status.repliedVehicleNotifications.clear();
				status.receivedOperationScheduleChangedVehicleNotifications
						.clear();
				status.receivedOperationScheduleChangedVehicleNotifications
						.add(vn);
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

//		cl.postEvent(new UpdatedOperationScheduleMergedEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(body, true));
//		assertTrue(s.cdl.await(3, TimeUnit.SECONDS));
	}

	/**
	 * ShowEventを二度受け取ると、内容が追記される
	 */
	public void testUpdatedOperationScheduleMergedEvent_2()
			throws InterruptedException {
//		Subscriber<ScheduleModalView.HideEvent> s = Subscriber.of(
//				ScheduleModalView.HideEvent.class, cl);

		final String body1 = "連絡1";
		final String body2 = "連絡2";
		final String body3 = "連絡3";
		sa.withWriteLock(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(LocalData status) {
				VehicleNotification vn1 = new VehicleNotification();
				vn1.setBody(body1);
				vn1.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);

				VehicleNotification vn2 = new VehicleNotification();
				vn2.setBody(body2);
				vn2.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);

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

//		cl.postEvent(new UpdatedOperationScheduleMergedEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(body1, true));
		assertTrue(solo.searchText(body2, true));
		assertFalse(solo.searchText(body3, true));
//		assertTrue(s.cdl.await(3, TimeUnit.SECONDS));

		sa.withWriteLock(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(LocalData status) {
				VehicleNotification vn = new VehicleNotification();
				vn.setBody(body3);
				vn.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
				status.receivedOperationScheduleChangedVehicleNotifications
						.add(vn);
			}
		});

//		cl.postEvent(new UpdatedOperationScheduleMergedEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(body1, true));
		assertTrue(solo.searchText(body2, true));
		assertTrue(solo.searchText(body3, true));
//		assertTrue(s.cdl.await(3, TimeUnit.SECONDS));
	}

	/**
	 * VehicleNotificationが無い場合ShowEventを受け取っても表示されず、ScheduleModalView.
	 * HideEventも発行されない
	 */
	public void testUpdatedOperationScheduleMergedEvent_3()
			throws InterruptedException {
//		Subscriber<ScheduleModalView.HideEvent> s = Subscriber.of(
//				ScheduleModalView.HideEvent.class, cl);

		sa.withWriteLock(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(LocalData status) {
				status.receivedOperationScheduleChangedVehicleNotifications
						.clear();
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

//		cl.postEvent(new ScheduleModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);
//		assertFalse(s.cdl.await(3, TimeUnit.SECONDS));
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
//		Subscriber<ScheduleModalView.ShowEvent> s = Subscriber.of(
//				ScheduleModalView.ShowEvent.class, cl);
		solo.clickOnView(solo.getView(R.id.schedule_confirm_button));
//		s.cdl.await();
	}
}
