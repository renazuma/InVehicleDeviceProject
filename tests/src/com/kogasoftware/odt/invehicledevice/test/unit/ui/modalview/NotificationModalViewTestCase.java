package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class NotificationModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	StatusAccess sa;
	CommonLogic cl;
	NotificationModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
		mv = (NotificationModalView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_notification_modal_view);
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
			assertEquals(cl2.countRegisteredClass(NotificationModalView.class)
					.intValue(), 1);
		} finally {
			cl2.dispose();
		}
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void testShowEvent_1() throws InterruptedException {
		final String message = "Hello";
		sa.write(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(Status status) {
				VehicleNotification vn = new VehicleNotification();
				vn.setBody(message);
				status.vehicleNotifications.clear();
				status.vehicleNotifications.add(vn);
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

		cl.postEvent(new NotificationModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(message));
	}

	/**
	 * VehicleNotificationが無い場合ShowEventを受け取っても表示されない
	 */
	public void testShowEvent_2() throws InterruptedException {
		sa.write(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(Status status) {
				status.vehicleNotifications.clear();
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

		cl.postEvent(new NotificationModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);
	}

	public void testいいえボタンを押すと消える() throws Exception {
		testShowEvent_1();
		solo.clickOnView(solo.getView(R.id.reply_no_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	public void testはいボタンを押すと消える() throws Exception {
		testShowEvent_1();
		solo.clickOnView(solo.getView(R.id.reply_yes_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}
}
