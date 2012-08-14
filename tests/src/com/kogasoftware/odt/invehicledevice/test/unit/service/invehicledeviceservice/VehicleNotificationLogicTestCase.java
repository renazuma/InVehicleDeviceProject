package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import java.util.LinkedList;
import java.util.List;

import android.test.ServiceTestCase;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.VoidReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class VehicleNotificationLogicTestCase extends
		ServiceTestCase<InVehicleDeviceService> {
	public VehicleNotificationLogicTestCase() {
		super(InVehicleDeviceService.class);
	}

	InVehicleDeviceService s;
	LocalDataSource lds;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setupService();
		s = getService();
		lds = s.getLocalDataSource();
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.vehicleNotifications.clear();
				status.repliedVehicleNotifications.clear();
				status.receivingOperationScheduleChangedVehicleNotifications
						.clear();
				status.receivedOperationScheduleChangedVehicleNotifications
						.clear();
			}
		});
	}

	@Override
	protected void tearDown() throws Exception {
		shutdownService();
		Closeables.closeQuietly(lds);
		super.tearDown();
	}

	public void testMergeVehicleNotification_スケジュールnotification追加()
			throws Exception {
		final VehicleNotification vn = new VehicleNotification();
		vn.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn);
		s.receiveVehicleNotification(vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(
						1,
						status.receivingOperationScheduleChangedVehicleNotifications
								.size());
				assertEquals(
						vn,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(0));
				assertTrue(status.vehicleNotifications.isEmpty());
			}
		});
	}

	public void testMergeVehicleNotification_スケジュールnotification同一IDがreceivedにある場合追加されない()
			throws Exception {
		final VehicleNotification vn0a = new VehicleNotification();
		final VehicleNotification vn0b = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		vn0a.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn0b.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn1.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn2.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn0a.setId(1000);
		vn0b.setId(1000);
		vn1.setId(1001);
		vn2.setId(1002);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0a);
		vns.add(vn1);
		vns.add(vn2);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.receivedOperationScheduleChangedVehicleNotifications
						.add(vn0b);
			}
		});
		s.receiveVehicleNotification(vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(
						2,
						status.receivingOperationScheduleChangedVehicleNotifications
								.size());
				assertEquals(
						vn1,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(0));
				assertEquals(
						vn2,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(1));

				assertEquals(
						1,
						status.receivedOperationScheduleChangedVehicleNotifications
								.size());
				assertEquals(
						vn0b,
						status.receivedOperationScheduleChangedVehicleNotifications
								.get(0));

				assertTrue(status.vehicleNotifications.isEmpty());
			}
		});
	}

	public void testMergeVehicleNotification_スケジュールnotification同一IDがsendListにある場合追加されない()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2a = new VehicleNotification();
		final VehicleNotification vn2b = new VehicleNotification();
		vn0.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn1.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn2a.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn2b.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn0.setId(200);
		vn1.setId(201);
		vn2a.setId(202);
		vn2b.setId(202);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1);
		vns.add(vn2a);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.repliedVehicleNotifications.add(vn2b);
			}
		});
		s.receiveVehicleNotification(vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(
						2,
						status.receivingOperationScheduleChangedVehicleNotifications
								.size());
				assertEquals(
						vn0,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(0));
				assertEquals(
						vn1,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(1));

				assertEquals(1, status.repliedVehicleNotifications.size());
				assertEquals(vn2b, status.repliedVehicleNotifications.get(0));

				assertTrue(status.vehicleNotifications.isEmpty());
			}
		});
	}

	public void testMergeVehicleNotification_スケジュールnotification同一IDは追加されない()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1a = new VehicleNotification();
		final VehicleNotification vn1b = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		vn0.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn1a.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn1b.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn2.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn0.setId(20);
		vn1a.setId(21);
		vn1b.setId(21);
		vn2.setId(22);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1a);
		vns.add(vn2);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.receivingOperationScheduleChangedVehicleNotifications
						.add(vn1b);
			}
		});
		s.receiveVehicleNotification(vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(
						3,
						status.receivingOperationScheduleChangedVehicleNotifications
								.size());
				assertEquals(
						vn1b,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(0));
				assertEquals(
						vn0,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(1));
				assertEquals(
						vn2,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(2));
				assertTrue(status.repliedVehicleNotifications.isEmpty());
				assertTrue(status.vehicleNotifications.isEmpty());
			}
		});
	}

	public void testMergeVehicleNotification_スケジュールと一般で振り分けが起きる()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		final VehicleNotification vn3 = new VehicleNotification();
		vn0.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn1.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn2.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		vn3.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn0.setId(200);
		vn1.setId(201);
		vn2.setId(202);
		vn3.setId(203);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1);
		vns.add(vn2);
		vns.add(vn3);
		s.receiveVehicleNotification(vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(2, status.vehicleNotifications.size());
				assertEquals(vn1, status.vehicleNotifications.get(0));
				assertEquals(vn3, status.vehicleNotifications.get(1));

				assertEquals(
						2,
						status.receivingOperationScheduleChangedVehicleNotifications
								.size());
				assertEquals(
						vn0,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(0));
				assertEquals(
						vn2,
						status.receivingOperationScheduleChangedVehicleNotifications
								.get(1));

				assertTrue(status.repliedVehicleNotifications.isEmpty());
				assertTrue(status.receivedOperationScheduleChangedVehicleNotifications
						.isEmpty());
			}
		});
	}

	public void testMergeVehicleNotification_一般notification追加()
			throws Exception {
		final VehicleNotification vn = new VehicleNotification();
		vn.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn);
		s.receiveVehicleNotification(vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(1, status.vehicleNotifications.size());
				assertEquals(vn, status.vehicleNotifications.get(0));
				assertTrue(status.repliedVehicleNotifications.isEmpty());
				assertTrue(status.receivingOperationScheduleChangedVehicleNotifications
						.isEmpty());
			}
		});
	}

	public void testMergeVehicleNotification_一般notification同一IDがsendListにある場合追加されない()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2a = new VehicleNotification();
		final VehicleNotification vn2b = new VehicleNotification();
		vn0.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn1.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn2a.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn2b.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn0.setId(10);
		vn1.setId(11);
		vn2a.setId(12);
		vn2b.setId(12);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1);
		vns.add(vn2a);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.repliedVehicleNotifications.add(vn2b);
			}
		});
		s.receiveVehicleNotification(vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(2, status.vehicleNotifications.size());
				assertEquals(vn0, status.vehicleNotifications.get(0));
				assertEquals(vn1, status.vehicleNotifications.get(1));
				assertEquals(1, status.repliedVehicleNotifications.size());
				assertEquals(vn2b, status.repliedVehicleNotifications.get(0));
				assertTrue(status.receivingOperationScheduleChangedVehicleNotifications
						.isEmpty());
			}
		});
	}

	public void testMergeVehicleNotification_一般notification同一IDは追加されない()
			throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1a = new VehicleNotification();
		final VehicleNotification vn1b = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		vn0.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn1a.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn1b.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn2.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn0.setId(100);
		vn1a.setId(101);
		vn1b.setId(101);
		vn2.setId(102);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1a);
		vns.add(vn2);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.vehicleNotifications.add(vn1b);
			}
		});
		s.receiveVehicleNotification(vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertEquals(3, status.vehicleNotifications.size());
				assertEquals(vn1b, status.vehicleNotifications.get(0));
				assertEquals(vn0, status.vehicleNotifications.get(1));
				assertEquals(vn2, status.vehicleNotifications.get(2));
				assertTrue(status.repliedVehicleNotifications.isEmpty());
				assertTrue(status.receivingOperationScheduleChangedVehicleNotifications
						.isEmpty());
			}
		});
	}

	/**
	 * 指定したVehicleNotificationがリプライ用のリストへ移動する
	 */
	public void testSetVehicleNotificationReplied_1() {
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.vehicleNotifications.add(vn1);
				status.repliedVehicleNotifications.add(vn2);
			}
		});
		s.replyVehicleNotification(vn1);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertTrue(status.vehicleNotifications.isEmpty());
				assertEquals(status.repliedVehicleNotifications.size(), 1);
				assertEquals(status.repliedVehicleNotifications.get(1), vn1);
			}
		});
		s.replyVehicleNotification(vn2);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData status) {
				assertTrue(status.vehicleNotifications.isEmpty());
				assertEquals(status.repliedVehicleNotifications.size(), 2);
				assertEquals(status.repliedVehicleNotifications.get(0), vn1);
				assertEquals(status.repliedVehicleNotifications.get(1), vn2);
			}
		});
	}

	/**
	 * 未リプライのVehicleNotificationがある場合NotificationModalViewが表示される
	 */
	public void testSetVehicleNotificationReplied_2() throws Exception {
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.vehicleNotifications.add(vn1);
				status.vehicleNotifications.add(vn2);
			}
		});
		// final CountDownLatch cdl = new CountDownLatch(1);
		// cl.registerEventListener(new
		// Function<NotificationModalView.ShowEvent, Void>() {
		// @Subscribe
		// @Override
		// public Void apply(NotificationModalView.ShowEvent e) {
		// cdl.countDown();
		// return null;
		// }
		// });
		// cl.postEvent(new VehicleNotificationRepliedEvent(vn1));
		// assertTrue(cdl.await(10, TimeUnit.SECONDS));
		// getInstrumentation().waitForIdleSync();
		// sa.withReadLock(new VoidReader() {
		// @Override
		// public void read(LocalData status) {
		// assertFalse(status.vehicleNotifications.isEmpty());
		// assertEquals(status.vehicleNotifications.get(0), vn2);
		// assertEquals(status.repliedVehicleNotifications.size(), 1);
		// assertEquals(status.repliedVehicleNotifications.get(0), vn1);
		// }
		// });
	}
}
