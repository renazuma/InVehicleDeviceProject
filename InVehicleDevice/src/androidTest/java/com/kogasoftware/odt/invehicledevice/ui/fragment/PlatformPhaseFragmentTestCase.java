package com.kogasoftware.odt.invehicledevice.ui.fragment;

import static org.mockito.Mockito.*;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.widget.FrameLayout;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PlatformPhaseFragment;

public class PlatformPhaseFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	InVehicleDeviceApiClient ac;
	LocalData ld;
	EventDispatcher ed;
	Fragment f;

	List<OperationSchedule> oss;
	OperationSchedule os0;
	OperationSchedule os1;
	List<PassengerRecord> prs;
	PassengerRecord pr0;

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

		os0 = new OperationSchedule();
		os1 = new OperationSchedule();
		oss = Lists.newArrayList(os0, os1);
		pr0 = new PassengerRecord();
		prs = Lists.newArrayList(pr0);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testShow() throws Throwable {
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				Operation o = new Operation();
				o.operationSchedules.addAll(oss);
				o.passengerRecords.addAll(prs);
				f = PlatformPhaseFragment.newInstance(o);
				FragmentManager fm = a.getFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		TestUtil.assertShow(f);
	}
}
