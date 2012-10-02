package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import static org.mockito.Mockito.mock;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.ScheduleModalView;

public class ScheduleModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	ScheduleModalView mv;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		mv = new ScheduleModalView(a, s);
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
	public void testShowEvent() throws InterruptedException {
		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
	}

	/**
	 * HideEventを受け取ると消える
	 */
	public void testHideEvent() throws InterruptedException {
		testShowEvent();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);

		getInstrumentation().waitForIdleSync();

		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.schedule_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}
}