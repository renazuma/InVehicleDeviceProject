package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import android.app.Activity;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NavigationModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.PlatformMemoModalView;

import static org.mockito.Mockito.*;

public class NavigationModalViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	NavigationModalView mv;
	PlatformMemoModalView pmmv;
	Activity a;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = getActivity();
		s = mock(InVehicleDeviceService.class);
		pmmv = mock(PlatformMemoModalView.class);
		mv = new NavigationModalView(a, s, pmmv);
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
	 * ShowEventを受け取ると表示される
	 */
	public void testShowEvent() throws InterruptedException {
		assertFalse(mv.isShown());
		assertNotSame(mv.getVisibility(), View.VISIBLE);

//		cl.postEvent(new NavigationModalView.ShowEvent());
		getInstrumentation().waitForIdleSync();

		assertTrue(mv.isShown());
		assertEquals(mv.getVisibility(), View.VISIBLE);
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.navigation_close_button));
		getInstrumentation().waitForIdleSync();
		assertFalse(mv.isShown());
	}

	public void xtest拡大ボタンを押すと拡大() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.navigation_zoom_in_button));
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}

	public void xtest縮小ボタンを押すと縮小() throws Exception {
		testShowEvent();
		solo.clickOnView(solo.getView(R.id.navigation_zoom_out_button));
		getInstrumentation().waitForIdleSync();
		fail("stub!");
	}
}
