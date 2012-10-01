package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview;

import static org.mockito.Mockito.mock;
import android.app.Activity;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NavigationModalView;
import com.kogasoftware.odt.invehicledevice.ui.modalview.PlatformMemoModalView;

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
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				a.setContentView(mv);
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testShow() throws InterruptedException {
		TestUtil.assertHide(mv);
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});
		TestUtil.assertShow(mv);
	}

	public void test戻るボタンを押すと消える() throws Exception {
		testShow();
		solo.clickOnView(solo.getView(R.id.navigation_close_button));
		TestUtil.assertHide(mv);
	}

	public void xtest拡大ボタンを押すと拡大() throws Exception {
		testShow();
		solo.clickOnView(solo.getView(R.id.navigation_zoom_in_button));
		fail("stub!");
	}

	public void xtest縮小ボタンを押すと縮小() throws Exception {
		testShow();
		solo.clickOnView(solo.getView(R.id.navigation_zoom_out_button));
		fail("stub!");
	}
}
