package com.kogasoftware.odt.invehicledevice.ui.arrayadapter;

import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.app.Activity;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.OperationScheduleArrayAdapter;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;

import static org.mockito.Mockito.*;

public class OperationScheduleArrayAdapterTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	Activity a;
	InVehicleDeviceService s;
	OperationScheduleArrayAdapter aa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		a = mock(Activity.class);
		s = mock(InVehicleDeviceService.class);
		when(a.getSystemService(Mockito.anyString())).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return getInstrumentation()
						.getTargetContext()
						.getSystemService((String) invocation.getArguments()[0]);
			}
		});
		aa = new OperationScheduleArrayAdapter(a, s,
				new ArrayList<OperationSchedule>(), null);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestShowOperationSchedules() throws Throwable {
		String platformName0 = "上野駅前";
		String platformName1 = "御徒町駅前";
		List<UnmergedOperationSchedule> oss = new ArrayList<UnmergedOperationSchedule>();
		{
			UnmergedOperationSchedule os = new UnmergedOperationSchedule();
			Platform p = new Platform();
			p.setName(platformName0);
			os.setPlatform(p);
			oss.add(os);
		}
		{
			UnmergedOperationSchedule os = new UnmergedOperationSchedule();
			Platform p = new Platform();
			p.setName(platformName1);
			os.setPlatform(p);
			oss.add(os);
		}

		aa = new OperationScheduleArrayAdapter(a, s, OperationSchedule.create(oss), null);

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(aa.getView(0, null, null));
			}
		});
		assertTrue(solo.searchText(platformName0));

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				getActivity().setContentView(aa.getView(1, null, null));
			}
		});
		assertTrue(solo.searchText(platformName1));
	}
}
