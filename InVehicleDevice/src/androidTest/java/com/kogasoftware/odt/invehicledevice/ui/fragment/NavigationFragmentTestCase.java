package com.kogasoftware.odt.invehicledevice.ui.fragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.widget.FrameLayout;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.fragment.NavigationFragment;

public class NavigationFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	LocalData ld;
	Fragment f;
	EventDispatcher ed;

	Platform p;;
	List<OperationSchedule> oss;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ld = new LocalData();
		ed = mock(EventDispatcher.class);
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(new LocalStorage(ld));
		when(s.getEventDispatcher()).thenReturn(ed);
		when(s.getLastMapBitmap()).thenReturn(Optional.<Bitmap> absent());
		when(s.getResources()).thenReturn(
				getInstrumentation().getTargetContext().getResources());
		a.setService(s);

		p = new Platform();
		oss = Lists.newLinkedList();
		oss.add(new OperationSchedule());
		oss.get(0).setPlatform(p);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	protected void assertShow(final String platformName) throws Throwable {
		p.setName(platformName);
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				Operation o = new Operation();
				o.operationSchedules.addAll(oss);
				f = NavigationFragment.newInstance(o, new ServiceUnitStatusLog(), true, 0);
				FragmentManager fm = a.getFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		TestUtil.assertShow(f);
		assertTrue(solo.searchText(platformName));
	}

	public void testBack() throws Throwable {
		assertShow("テスト1");
		solo.clickOnView(solo.getView(R.id.navigation_close_button));
	}

	public void testZoomIn() throws Throwable {
		// TODO: Stub
		assertShow("テスト2");
		solo.clickOnView(solo.getView(R.id.navigation_zoom_in_button));
	}

	public void testZoomOut() throws Throwable {
		// TODO: Stub
		assertShow("テスト3");
		solo.clickOnView(solo.getView(R.id.navigation_zoom_out_button));
	}
}
