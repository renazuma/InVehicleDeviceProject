package com.kogasoftware.odt.invehicledevice.ui.fragment;

import static org.mockito.Mockito.*;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.widget.FrameLayout;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.fragment.DrivePhaseFragment;

public class DrivePhaseFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	InVehicleDeviceApiClient ac;
	LocalData ld;
	EventDispatcher ed;
	Fragment f;

	List<UnmergedOperationSchedule> oss;
	UnmergedOperationSchedule os0;
	UnmergedOperationSchedule os1;
	UnmergedOperationSchedule os2;
	Platform p0;
	Platform p1;
	Platform p2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ld = new LocalData();
		ed = mock(EventDispatcher.class);
		ac = mock(InVehicleDeviceApiClient.class);
		when(ac.withSaveOnClose()).thenReturn(ac);
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(new LocalStorage(ld));
		when(s.getEventDispatcher()).thenReturn(ed);
		when(s.getApiClient()).thenReturn(ac);
		a.setService(s);

		p0 = new Platform();
		p1 = new Platform();
		p2 = new Platform();

		os0 = new UnmergedOperationSchedule();
		os1 = new UnmergedOperationSchedule();
		os2 = new UnmergedOperationSchedule();

		os0.setPlatform(p0);
		os1.setPlatform(p1);
		os2.setPlatform(p2);

		oss = Lists.newArrayList(os0, os1, os2);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testShow() throws Throwable {
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				f = DrivePhaseFragment.newInstance(OperationSchedule.create(oss));
				FragmentManager fm = a.getFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		TestUtil.assertShow(f);
	}
}
