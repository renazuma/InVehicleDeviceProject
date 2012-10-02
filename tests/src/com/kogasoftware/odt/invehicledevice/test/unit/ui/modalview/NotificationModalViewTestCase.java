package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import static org.mockito.Mockito.mock;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class NotificationModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	LocalDataSource sa;
	NotificationModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		sa = new LocalDataSource(a);
		mv = new NotificationModalView(a, s);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestEventBusに自動で登録される() throws Exception {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				a.setContentView(R.layout.in_vehicle_device);
			}
		});
	}

	/**
	 * ShowEventを受け取ると表示される
	 */
	public void testShowEvent_1() throws InterruptedException {
		final String message = "Hello";
		sa.withWriteLock(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(LocalData status) {
				VehicleNotification vn = new VehicleNotification();
				vn.setBody(message);
				status.vehicleNotifications.clear();
				status.vehicleNotifications.add(vn);
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

//		cl.postEvent(new NotificationModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
		assertTrue(solo.searchText(message));
	}

	/**
	 * VehicleNotificationが無い場合ShowEventを受け取っても表示されない
	 */
	public void testShowEvent_2() throws InterruptedException {
		sa.withWriteLock(new Writer() { // TODO: イベントで書き直し
			@Override
			public void write(LocalData status) {
				status.vehicleNotifications.clear();
			}
		});

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

//		cl.postEvent(new NotificationModalView.ShowEvent());
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
