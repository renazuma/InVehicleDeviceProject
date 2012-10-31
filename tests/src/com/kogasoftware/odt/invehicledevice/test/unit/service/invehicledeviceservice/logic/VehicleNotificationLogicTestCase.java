package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.logic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import junitx.framework.ListAssert;
import android.test.AndroidTestCase;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.VehicleNotificationLogic;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification.NotificationKind;

public class VehicleNotificationLogicTestCase extends AndroidTestCase {
	InVehicleDeviceService s;
	VehicleNotificationLogic vnl;
	LocalStorage lds;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		lds = new LocalStorage(getContext());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.vehicleNotifications.clear();
			}
		});
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(lds);
		when(s.getEventDispatcher()).thenReturn(new EventDispatcher());
		vnl = new VehicleNotificationLogic(s);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			Closeables.closeQuietly(lds);
		} finally {
			super.tearDown();
		}
	}

	public void testConstructor_NoServiceInteractions() {
		verifyZeroInteractions(s);
	}

	public void testGetVehicleNotifications() {
		final VehicleNotification vn1a = new VehicleNotification();
		final VehicleNotification vn1b = new VehicleNotification();
		final VehicleNotification vn2a = new VehicleNotification();
		final VehicleNotification vn2b = new VehicleNotification();
		final VehicleNotification vn2c = new VehicleNotification();
		vn1a.setId(11);
		vn1b.setId(12);
		vn2a.setId(21);
		vn2b.setId(22);
		vn2c.setId(23);
		vn1a.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn1b.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn2a.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn2b.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn2c.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vnl.setStatusWithWriteLock(Lists.newArrayList(vn1a, vn2a),
				VehicleNotificationStatus.UNHANDLED);
		vnl.setStatusWithWriteLock(Lists.newArrayList(vn1b, vn2b),
				VehicleNotificationStatus.REPLIED);
		vnl.setStatusWithWriteLock(Lists.newArrayList(vn2c),
				VehicleNotificationStatus.OPERATION_SCHEDULE_RECEIVED);
		ListAssert.assertEquals(Lists.newArrayList(vn1a), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.UNHANDLED));
		ListAssert.assertEquals(Lists.newArrayList(vn1b), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.REPLIED));
		ListAssert.assertEquals(Lists.newArrayList(vn2a), vnl
				.getWithReadLock(NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.UNHANDLED));
		ListAssert.assertEquals(Lists.newArrayList(vn2b), vnl
				.getWithReadLock(NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.REPLIED));
		ListAssert.assertEquals(Lists.newArrayList(vn2c), vnl
				.getWithReadLock(NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.OPERATION_SCHEDULE_RECEIVED));
	}

	public void testMergeVehicleNotification_RESERVATION_CHANGED追加()
			throws Exception {
		final VehicleNotification vn = new VehicleNotification();
		vn.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn);
		vnl.receive(vns);
		ListAssert.assertEquals(Lists.newArrayList(vn), vnl
				.getWithReadLock(NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.UNHANDLED));
		assertEquals(1, vnl.getWithReadLock().size());
	}

	public void testMergeVehicleNotification_RESERVATION_CHANGED同一IDがUNHANDLEDにある場合追加されない()
			throws Exception {
		final VehicleNotification vn0a = new VehicleNotification();
		final VehicleNotification vn0b = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		vn0a.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn0b.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn1.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn2.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn0a.setId(1000);
		vn0b.setId(1000);
		vn1.setId(1001);
		vn2.setId(1002);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0a);
		vns.add(vn1);
		vns.add(vn2);
		vnl.setStatusWithWriteLock(vn0b,
				VehicleNotificationStatus.OPERATION_SCHEDULE_RECEIVED);
		vnl.receive(vns);
		ListAssert.assertEquals(Lists.newArrayList(vn1, vn2), vnl
				.getWithReadLock(NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.UNHANDLED));
		ListAssert.assertEquals(Lists.newArrayList(vn0b), vnl
				.getWithReadLock(NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.OPERATION_SCHEDULE_RECEIVED));
		assertEquals(3, vnl.getWithReadLock().size());
	}

	public void testMergeVehicleNotification_RESERVATION_CHANGED同一IDがREPLIEDにある場合追加されない()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2a = new VehicleNotification();
		final VehicleNotification vn2b = new VehicleNotification();
		vn0.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn1.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn2a.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn2b.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn0.setId(200);
		vn1.setId(201);
		vn2a.setId(202);
		vn2b.setId(202);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1);
		vns.add(vn2a);
		vnl.setStatusWithWriteLock(vn2b,
				VehicleNotificationStatus.REPLIED);
		vnl.receive(vns);

		ListAssert.assertEquals(Lists.newArrayList(vn0, vn1), vnl
				.getWithReadLock(NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.UNHANDLED));
		ListAssert.assertEquals(Lists.newArrayList(vn2b), vnl
				.getWithReadLock(NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.REPLIED));
		assertEquals(3, vnl.getWithReadLock().size());
	}

	public void testMergeVehicleNotification_RESERVATION_CHANGED同一IDがOPERATION_SCHEDULE_RECEIVEDは追加されない()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1a = new VehicleNotification();
		final VehicleNotification vn1b = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		vn0.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn1a.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn1b.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn2.setNotificationKind(NotificationKind.RESERVATION_CHANGED);
		vn0.setId(20);
		vn1a.setId(21);
		vn1b.setId(21);
		vn2.setId(22);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1a);
		vns.add(vn2);
		vnl.setStatusWithWriteLock(vn1b,
				VehicleNotificationStatus.UNHANDLED);
		vnl.receive(vns);
		ListAssert.assertEquals(Lists.newArrayList(vn1b, vn0, vn2), vnl
				.getWithReadLock(NotificationKind.RESERVATION_CHANGED,
						VehicleNotificationStatus.UNHANDLED));
		assertEquals(3, vnl.getWithReadLock().size());
	}

	public void testMergeVehicleNotification_FROM_OPERATOR追加() throws Exception {
		final VehicleNotification vn = new VehicleNotification();
		vn.setNotificationKind(NotificationKind.FROM_OPERATOR);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn);
		vnl.receive(vns);
		ListAssert.assertEquals(Lists.newArrayList(vn), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.UNHANDLED));
		assertEquals(1, vnl.getWithReadLock().size());
	}

	public void testMergeVehicleNotification_FROM_OPERATOR同一IDがREPLIEDにあると追加されない()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2a = new VehicleNotification();
		final VehicleNotification vn2b = new VehicleNotification();
		vn0.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn1.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn2a.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn2b.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn0.setId(10);
		vn1.setId(11);
		vn2a.setId(12);
		vn2b.setId(12);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1);
		vns.add(vn2a);

		vnl.setStatusWithWriteLock(vn2b,
				VehicleNotificationStatus.REPLIED);
		vnl.receive(vns);

		ListAssert.assertEquals(Lists.newArrayList(vn0, vn1), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.UNHANDLED));
		ListAssert.assertEquals(Lists.newArrayList(vn2b), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.REPLIED));
		assertEquals(3, vnl.getWithReadLock().size());
	}

	public void testMergeVehicleNotification_FROM_OPERATOR同一IDがUNHANDLEDにあると追加されない()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1a = new VehicleNotification();
		final VehicleNotification vn1b = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		vn0.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn1a.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn1b.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn2.setNotificationKind(NotificationKind.FROM_OPERATOR);
		vn0.setId(100);
		vn1a.setId(101);
		vn1b.setId(101);
		vn2.setId(102);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1a);
		vns.add(vn2);
		vnl.setStatusWithWriteLock(vn1b,
				VehicleNotificationStatus.UNHANDLED);
		vnl.receive(vns);
		ListAssert.assertEquals(Lists.newArrayList(vn1b, vn0, vn2), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.UNHANDLED));
		assertEquals(3, vnl.getWithReadLock().size());
	}

	/**
	 * 指定したVehicleNotificationがリプライ用のリストへ移動する
	 */
	public void testSetVehicleNotificationReplied() throws Exception {
		final VehicleNotification vn1 = new VehicleNotification();
		vn1.setId(1);
		final VehicleNotification vn2 = new VehicleNotification();
		vn2.setId(2);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.vehicleNotifications.put(
						VehicleNotificationStatus.UNHANDLED, vn1);
				status.vehicleNotifications.put(
						VehicleNotificationStatus.UNHANDLED, vn2);
			}
		});

		ListAssert.assertEquals(Lists.newArrayList(vn1, vn2), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.UNHANDLED));
		assertEquals(2, vnl.getWithReadLock().size());

		vnl.reply(vn1);
		Thread.sleep(2000);
		ListAssert.assertEquals(Lists.newArrayList(vn2), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.UNHANDLED));
		ListAssert.assertEquals(Lists.newArrayList(vn1), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.REPLIED));
		assertEquals(2, vnl.getWithReadLock().size());

		vnl.reply(vn2);
		Thread.sleep(2000);
		
		ListAssert.assertEquals(Lists.newArrayList(vn1, vn2), vnl
				.getWithReadLock(NotificationKind.FROM_OPERATOR,
						VehicleNotificationStatus.REPLIED));
		assertEquals(2, vnl.getWithReadLock().size());
	}
}

