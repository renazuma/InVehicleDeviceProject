package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.fragment.DepartureCheckFragment;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;

public class DepartureCheckFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	InVehicleDeviceApiClient ac;
	LocalData ld;
	EventDispatcher ed;
	Fragment f;
	ScheduledExecutorService ses;

	OperationSchedule os;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ses = Executors.newScheduledThreadPool(1);
		ld = new LocalData();
		ed = mock(EventDispatcher.class);
		ac = mock(InVehicleDeviceApiClient.class);
		when(ac.withSaveOnClose()).thenReturn(ac);
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(new LocalStorage(ld));
		when(s.getEventDispatcher()).thenReturn(ed);
		when(s.getApiClient()).thenReturn(ac);
		when(s.getScheduledExecutorService()).thenReturn(ses);
		a.setService(s);

		os = new OperationSchedule();
		Platform p = new Platform();
		p.setName("乗降場X");
		os.setPlatform(p);
		os.setOperationRecord(new OperationRecord());
		ld.operationSchedules.add(os);
	}

	@Override
	protected void tearDown() throws Exception {
		ses.shutdownNow();
		super.tearDown();
	}

	public void testShow() throws Throwable {
		for (String name : new String[] { "乗降場A", "乗降場B", "乗降場C" }) {
			assertShow(name);
		}
	}

	protected void assertShow(final String platformName) throws Throwable {
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				f = DepartureCheckFragment.newInstance(Phase.PLATFORM_GET_ON,
						Lists.newArrayList(os));
				FragmentManager fm = a.getSupportFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		TestUtil.assertShow(f);
	}

	public void testDepart() throws Throwable {
		os.setId(101);
		assertShow("のりおりば");
		solo.clickOnView(solo.getView(R.id.departure_button));
		TestUtil.assertHide(f);
		ArgumentCaptor<OperationSchedule> a = ArgumentCaptor
				.forClass(OperationSchedule.class);
		verify(ac, timeout(1000).times(1)).departureOperationSchedule(
				a.capture(),
				Mockito.<ApiClientCallback<OperationSchedule>> any());
		assertEquals(101, a.getValue().getId().intValue());
	}

	public void testBack() throws Throwable {
		os.setId(100);
		assertShow("のりおりば");
		solo.clickOnView(solo.getView(R.id.departure_check_close_button));
		TestUtil.assertHide(f);
		verify(ac, timeout(1000).never()).departureOperationSchedule(
				Mockito.<OperationSchedule> any(),
				Mockito.<ApiClientCallback<OperationSchedule>> any());
	}
}
