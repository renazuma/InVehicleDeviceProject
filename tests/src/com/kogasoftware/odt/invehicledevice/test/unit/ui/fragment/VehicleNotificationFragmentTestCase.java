package com.kogasoftware.odt.invehicledevice.test.unit.ui.fragment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.VehicleNotificationLogic;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification.NotificationKind;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification.Response;

public class VehicleNotificationFragmentTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	NotificationModalView mv;
	VehicleNotificationLogic vnl;
	LocalStorage lds;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		lds = new LocalStorage(a);
		lds.withWriteLock(new Writer(){
			@Override
			public void write(LocalData localData) {
				localData.vehicleNotifications.clear();
			}
		});
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(lds);
		vnl = new VehicleNotificationLogic(s);
		Answer<List<VehicleNotification>> answer = new Answer<List<VehicleNotification>>() {
			@Override
			public List<VehicleNotification> answer(InvocationOnMock invocation)
					throws Throwable {
				Object[] arguments = invocation.getArguments();
				return vnl.getVehicleNotifications((Integer) arguments[0],
						(VehicleNotificationStatus) arguments[1]);
			}
		};
		when(
				s.getVehicleNotifications(Mockito.anyInt(),
						Mockito.<VehicleNotificationStatus> any())).thenAnswer(
				answer);
		mv = new NotificationModalView(a, s);
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

	public void assertShow() throws Exception {
		assertFalse(mv.isShown());
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				mv.show();
			}
		});
		TestUtil.assertShow(mv);
	}

	public void testShow1() throws Exception {
		String body = "Hello!";
		final VehicleNotification vn = new VehicleNotification();
		vn.setBody(body);
		vn.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vnl.setVehicleNotificationStatus(vn,
				VehicleNotificationStatus.UNHANDLED);

		assertShow();

		assertTrue(solo.searchText(body, true));
	}

	public void testReplyYes() throws Exception {
		String body = "Hello! testReplyYes";
		final VehicleNotification vn = new VehicleNotification();
		vn.setBody(body);
		vn.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vnl.setVehicleNotificationStatus(vn,
				VehicleNotificationStatus.UNHANDLED);
		assertShow();
		solo.clickOnView(solo.getView(R.id.reply_yes_button));
		TestUtil.assertHide(mv);
		assertEquals(Optional.of(Response.YES), vn.getResponse());
	}

	public void testReplyNo() throws Exception {
		String body = "Hello! testReplyNo";
		final VehicleNotification vn = new VehicleNotification();
		vn.setBody(body);
		vn.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vnl.setVehicleNotificationStatus(vn,
				VehicleNotificationStatus.UNHANDLED);
		assertShow();
		solo.clickOnView(solo.getView(R.id.reply_no_button));
		TestUtil.assertHide(mv);
		assertEquals(Optional.of(Response.NO), vn.getResponse());
	}
}

