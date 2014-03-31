package com.kogasoftware.odt.invehicledevice.ui.fragment;

import static org.mockito.Mockito.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.app.Fragment;
import android.app.FragmentManager;
import android.widget.FrameLayout;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.fragment.OperationScheduleChangedFragment;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification.NotificationKind;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification.Response;

public class OperationScheduleChangedFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	InVehicleDeviceApiClient ac;
	LocalData ld;
	EventDispatcher ed;
	ScheduledExecutorService ses;
	Fragment f;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ld = new LocalData();
		ses = Executors.newScheduledThreadPool(1);
		ed = mock(EventDispatcher.class);
		ac = mock(InVehicleDeviceApiClient.class);
		when(ac.withSaveOnClose()).thenReturn(ac);
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(new LocalStorage(ld));
		when(s.getEventDispatcher()).thenReturn(ed);
		when(s.getApiClient()).thenReturn(ac);
		when(s.getScheduledExecutorService()).thenReturn(ses);
		a.setService(s);
	}

	@Override
	protected void tearDown() throws Exception {
		ses.shutdownNow();
		super.tearDown();
	}

	public void assertShow(final VehicleNotification vn) throws Throwable {
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				f = OperationScheduleChangedFragment.newInstance(Lists
						.newArrayList(vn));
				FragmentManager fm = a.getFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		TestUtil.assertShow(f);
	}

	public void testShow() throws Throwable {
		for (String body : new String[] { "変更されました", "キャンセルされました" }) {
			VehicleNotification vn = new VehicleNotification();
			vn.setBody(body);
			vn.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
			assertShow(vn);
			assertTrue(solo.searchText(body, true));
		}
	}

	public void testBack() throws Throwable {
		String body = "キャンセルしました";
		VehicleNotification vn = new VehicleNotification();
		vn.setBody(body);
		assertShow(vn);
		solo.clickOnView(solo.getView(R.id.schedule_changed_close_button));
		TestUtil.assertHide(f);
		assertEquals(Optional.of(Response.YES), vn.getResponse());
	}
}
