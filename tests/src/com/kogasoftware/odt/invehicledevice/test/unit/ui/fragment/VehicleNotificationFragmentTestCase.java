package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.*;

import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.fragment.VehicleNotificationFragment;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification.NotificationKind;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification.Response;

public class VehicleNotificationFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	InVehicleDeviceApiClient ac;
	LocalData ld;
	EventDispatcher ed;
	VehicleNotificationFragment f;

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
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void assertShow(final VehicleNotification vn) throws Throwable {
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				int id = 12345;
				FrameLayout fl = new FrameLayout(a);
				fl.setId(id);
				a.setContentView(fl);
				f = VehicleNotificationFragment.newInstance(vn);
				FragmentManager fm = a.getSupportFragmentManager();
				fm.beginTransaction().add(id, f).commit();
			}
		});
		TestUtil.assertShow(f);
	}

	public void testShow() throws Throwable {
		for (String body : new String[] { "こんにちわ", "こんばんわ" }) {
			VehicleNotification vn = new VehicleNotification();
			vn.setBody(body);
			vn.setNotificationKind(NotificationKind.FROM_OPERATOR);
			assertShow(vn);
			assertTrue(solo.searchText(body, true));
		}
	}

	public void testReplyYes() throws Throwable {
		String body = "Hello! testReplyYes";
		final VehicleNotification vn = new VehicleNotification();
		vn.setBody(body);
		assertShow(vn);
		solo.clickOnView(solo.getView(R.id.reply_yes_button));
		TestUtil.assertHide(f);
		assertEquals(Optional.of(Response.YES), vn.getResponse());
	}

	public void testReplyNo() throws Throwable {
		String body = "Hello! testReplyNo";
		final VehicleNotification vn = new VehicleNotification();
		vn.setBody(body);
		assertShow(vn);
		solo.clickOnView(solo.getView(R.id.reply_no_button));
		TestUtil.assertHide(f);
		assertEquals(Optional.of(Response.NO), vn.getResponse());
	}
}
