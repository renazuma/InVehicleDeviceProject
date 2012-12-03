package com.kogasoftware.odt.invehicledevice.test.unit.ui.arrayadapter;

import java.util.ArrayList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import static org.mockito.Mockito.*;

public class OperationScheduleArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	InVehicleDeviceService s;
	OperationScheduleArrayAdapter aa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		aa = new OperationScheduleArrayAdapter(s,
				new ArrayList<OperationSchedule>());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testShowOperationSchedules() throws Exception {
		String platformName0 = "上野駅前";
		String platformName1 = "御徒町駅前";
		List<OperationSchedule> oss = new ArrayList<OperationSchedule>();
		{
			OperationSchedule os = new OperationSchedule();
			Platform p = new Platform();
			p.setName(platformName0);
			os.setPlatform(p);
			oss.add(os);
		}
		{
			OperationSchedule os = new OperationSchedule();
			Platform p = new Platform();
			p.setName(platformName1);
			os.setPlatform(p);
			oss.add(os);
		}

		aa = new OperationScheduleArrayAdapter(s, oss);

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(aa.getView(0, null, null));
			}
		});
		assertTrue(solo.searchText(platformName0));

		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(aa.getView(1, null, null));
			}
		});
		assertTrue(solo.searchText(platformName1));
	}
}
