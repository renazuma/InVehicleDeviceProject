package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.location.Location;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.backgroundtask.CommonEventSubscriber;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.ServiceUnitStatusLogs;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.VoidReader;
import com.kogasoftware.odt.invehicledevice.logic.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.TemperatureChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.VehicleNotificationRepliedEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.modalview.NotificationModalView;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class CommonEventSubscriberTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	CommonLogic cl;
	StatusAccess sa;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sa = new StatusAccess(getActivity());
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.vehicleNotifications.clear();
				status.sendLists.repliedVehicleNotifications.clear();
				status.receivingOperationScheduleChangedVehicleNotifications.clear();
				status.receivedOperationScheduleChangedVehicleNotifications.clear();
			}
		});

		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);

		assertEquals(cl.countRegisteredClass(CommonEventSubscriber.class)
				.intValue(), 1);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (cl != null) {
			cl.dispose();
		}
	}

	/**
	 * PauseEventを受信すると状態がStatus.PAUSEとなる
	 */
	public void testPauseEvent() throws Exception {
		ServiceUnitStatusLog sul = sa.read(new Reader<ServiceUnitStatusLog>() {
			@Override
			public ServiceUnitStatusLog read(Status status) {
				return status.serviceUnitStatusLog;
			}
		});
		assertNotSame(sul.getStatus().get(), ServiceUnitStatusLogs.Status.PAUSE);
		cl.postEvent(new PauseEvent());
		getInstrumentation().waitForIdleSync();
		assertEquals(sul.getStatus().get(), ServiceUnitStatusLogs.Status.PAUSE);
	}

	/**
	 * StopEventを受信すると状態がStatus.STOPとなる
	 */
	public void testStopEvent() throws Exception {
		ServiceUnitStatusLog sul = sa.read(new Reader<ServiceUnitStatusLog>() {
			@Override
			public ServiceUnitStatusLog read(Status status) {
				return status.serviceUnitStatusLog;
			}
		});
		assertNotSame(sul.getStatus().get(), ServiceUnitStatusLogs.Status.STOP);
		cl.postEvent(new StopEvent());
		getInstrumentation().waitForIdleSync();
		assertEquals(sul.getStatus().get(), ServiceUnitStatusLogs.Status.STOP);
	}

	public void testSetOrientation() {
		final Float f1 = 10f;
		final Float f2 = 20f;

		cl.postEvent(new OrientationChangedEvent(f1));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.serviceUnitStatusLog.getOrientation().get()
						.intValue(), f1.intValue());
			}
		});

		cl.postEvent(new OrientationChangedEvent(f2));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.serviceUnitStatusLog.getOrientation().get()
						.intValue(), f2.intValue());
			}
		});
	}

	public void testSetTemperature() {
		final Float f1 = 30f;
		final Float f2 = 40f;

		cl.postEvent(new TemperatureChangedEvent(f1));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.serviceUnitStatusLog.getTemperature().get()
						.intValue(), f1.intValue());
			}
		});

		cl.postEvent(new TemperatureChangedEvent(f2));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(status.serviceUnitStatusLog.getTemperature().get()
						.intValue(), f2.intValue());
			}
		});
	}

	/**
	 * 指定したVehicleNotificationがリプライ用のリストへ移動する
	 */
	public void testSetVehicleNotificationReplied_1() {
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.vehicleNotifications.add(vn1);
				status.sendLists.repliedVehicleNotifications.add(vn2);
			}
		});
		cl.postEvent(new VehicleNotificationRepliedEvent(vn1));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertTrue(status.vehicleNotifications.isEmpty());
				assertEquals(
						status.sendLists.repliedVehicleNotifications.size(), 2);
				assertEquals(
						status.sendLists.repliedVehicleNotifications.get(0),
						vn2);
				assertEquals(
						status.sendLists.repliedVehicleNotifications.get(1),
						vn1);
			}
		});
	}

	/**
	 * 未リプライのVehicleNotificationがある場合NotificationModalView_ShowEventが発生
	 */
	public void testSetVehicleNotificationReplied_2() throws Exception {
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.vehicleNotifications.add(vn1);
				status.vehicleNotifications.add(vn2);
			}
		});
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.registerEventListener(new Function<NotificationModalView.ShowEvent, Void>() {
			@Subscribe
			@Override
			public Void apply(NotificationModalView.ShowEvent e) {
				cdl.countDown();
				return null;
			}
		});
		cl.postEvent(new VehicleNotificationRepliedEvent(vn1));
		assertTrue(cdl.await(10, TimeUnit.SECONDS));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertFalse(status.vehicleNotifications.isEmpty());
				assertEquals(status.vehicleNotifications.get(0), vn2);
				assertEquals(
						status.sendLists.repliedVehicleNotifications.size(), 1);
				assertEquals(
						status.sendLists.repliedVehicleNotifications.get(0),
						vn1);
			}
		});
	}
	
	public void testMergeVehicleNotification_一般notification追加() throws Exception {
		final VehicleNotification vn = new VehicleNotification();
		vn.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn);
		cl.postEvent(new VehicleNotificationReceivedEvent(vns));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(1, status.vehicleNotifications.size());
				assertEquals(vn, status.vehicleNotifications.get(0));
				assertTrue(status.sendLists.repliedVehicleNotifications.isEmpty());
				assertTrue(status.receivingOperationScheduleChangedVehicleNotifications.isEmpty());
			}
		});
	}
	
	public void testMergeVehicleNotification_一般notification同一IDは追加されない() throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1a = new VehicleNotification();
		final VehicleNotification vn1b = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		vn0.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn1a.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn1b.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn2.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn0.setId(100);
		vn1a.setId(101);
		vn1b.setId(101);
		vn2.setId(102);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1a);
		vns.add(vn2);
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.vehicleNotifications.add(vn1b);
			}
		});
		cl.postEvent(new VehicleNotificationReceivedEvent(vns));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(3, status.vehicleNotifications.size());
				assertEquals(vn1b, status.vehicleNotifications.get(0));
				assertEquals(vn0, status.vehicleNotifications.get(1));
				assertEquals(vn2, status.vehicleNotifications.get(2));
				assertTrue(status.sendLists.repliedVehicleNotifications.isEmpty());
				assertTrue(status.receivingOperationScheduleChangedVehicleNotifications.isEmpty());
			}
		});
	}

	public void testMergeVehicleNotification_一般notification同一IDがsendListにある場合追加されない() throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2a = new VehicleNotification();
		final VehicleNotification vn2b = new VehicleNotification();
		vn0.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn1.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn2a.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn2b.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn0.setId(10);
		vn1.setId(11);
		vn2a.setId(12);
		vn2b.setId(12);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1);
		vns.add(vn2a);
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.sendLists.repliedVehicleNotifications.add(vn2b);
			}
		});
		cl.postEvent(new VehicleNotificationReceivedEvent(vns));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(2, status.vehicleNotifications.size());
				assertEquals(vn0, status.vehicleNotifications.get(0));
				assertEquals(vn1, status.vehicleNotifications.get(1));
				assertEquals(1, status.sendLists.repliedVehicleNotifications.size());
				assertEquals(vn2b, status.sendLists.repliedVehicleNotifications.get(0));
				assertTrue(status.receivingOperationScheduleChangedVehicleNotifications.isEmpty());
			}
		});
	}
	
	public void testMergeVehicleNotification_スケジュールnotification追加() throws Exception {
		final VehicleNotification vn = new VehicleNotification();
		vn.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn);
		cl.postEvent(new VehicleNotificationReceivedEvent(vns));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(1, status.receivingOperationScheduleChangedVehicleNotifications.size());
				assertEquals(vn, status.receivingOperationScheduleChangedVehicleNotifications.get(0));
				assertTrue(status.vehicleNotifications.isEmpty());
			}
		});
	}
	
	public void testMergeVehicleNotification_スケジュールnotification同一IDは追加されない() throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1a = new VehicleNotification();
		final VehicleNotification vn1b = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		vn0.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn1a.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn1b.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn2.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn0.setId(20);
		vn1a.setId(21);
		vn1b.setId(21);
		vn2.setId(22);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1a);
		vns.add(vn2);
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.receivingOperationScheduleChangedVehicleNotifications.add(vn1b);
			}
		});
		cl.postEvent(new VehicleNotificationReceivedEvent(vns));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(3, status.receivingOperationScheduleChangedVehicleNotifications.size());
				assertEquals(vn1b, status.receivingOperationScheduleChangedVehicleNotifications.get(0));
				assertEquals(vn0, status.receivingOperationScheduleChangedVehicleNotifications.get(1));
				assertEquals(vn2, status.receivingOperationScheduleChangedVehicleNotifications.get(2));
				assertTrue(status.sendLists.repliedVehicleNotifications.isEmpty());
				assertTrue(status.vehicleNotifications.isEmpty());
			}
		});
	}

	public void testMergeVehicleNotification_スケジュールnotification同一IDがsendListにある場合追加されない() throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2a = new VehicleNotification();
		final VehicleNotification vn2b = new VehicleNotification();
		vn0.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn1.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn2a.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn2b.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn0.setId(200);
		vn1.setId(201);
		vn2a.setId(202);
		vn2b.setId(202);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1);
		vns.add(vn2a);
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.sendLists.repliedVehicleNotifications.add(vn2b);
			}
		});
		cl.postEvent(new VehicleNotificationReceivedEvent(vns));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(2, status.receivingOperationScheduleChangedVehicleNotifications.size());
				assertEquals(vn0, status.receivingOperationScheduleChangedVehicleNotifications.get(0));
				assertEquals(vn1, status.receivingOperationScheduleChangedVehicleNotifications.get(1));

				assertEquals(1, status.sendLists.repliedVehicleNotifications.size());
				assertEquals(vn2b, status.sendLists.repliedVehicleNotifications.get(0));

				assertTrue(status.vehicleNotifications.isEmpty());
			}
		});
	}
	
	public void testMergeVehicleNotification_スケジュールnotification同一IDがreceivedにある場合追加されない() throws Exception {
		final VehicleNotification vn0a = new VehicleNotification();
		final VehicleNotification vn0b = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		vn0a.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn0b.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn1.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn2.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn0a.setId(1000);
		vn0b.setId(1000);
		vn1.setId(1001);
		vn2.setId(1002);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0a);
		vns.add(vn1);
		vns.add(vn2);
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.receivedOperationScheduleChangedVehicleNotifications.add(vn0b);
			}
		});
		cl.postEvent(new VehicleNotificationReceivedEvent(vns));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(2, status.receivingOperationScheduleChangedVehicleNotifications.size());
				assertEquals(vn1, status.receivingOperationScheduleChangedVehicleNotifications.get(0));
				assertEquals(vn2, status.receivingOperationScheduleChangedVehicleNotifications.get(1));

				assertEquals(1, status.receivedOperationScheduleChangedVehicleNotifications.size());
				assertEquals(vn0b, status.receivedOperationScheduleChangedVehicleNotifications.get(0));
				
				assertTrue(status.vehicleNotifications.isEmpty());
			}
		});
	}
	
	public void testMergeVehicleNotification_スケジュールと一般で振り分けが起きる() throws Exception {
		final VehicleNotification vn0 = new VehicleNotification();
		final VehicleNotification vn1 = new VehicleNotification();
		final VehicleNotification vn2 = new VehicleNotification();
		final VehicleNotification vn3 = new VehicleNotification();
		vn0.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn1.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn2.setNotificationType(VehicleNotifications.NotificationType.SCHEDULE_CHANGED);
		vn3.setNotificationType(VehicleNotifications.NotificationType.NORMAL);
		vn0.setId(200);
		vn1.setId(201);
		vn2.setId(202);
		vn3.setId(203);
		List<VehicleNotification> vns = new LinkedList<VehicleNotification>();
		vns.add(vn0);
		vns.add(vn1);
		vns.add(vn2);
		vns.add(vn3);
		cl.postEvent(new VehicleNotificationReceivedEvent(vns));
		getInstrumentation().waitForIdleSync();
		sa.read(new VoidReader() {
			@Override
			public void read(Status status) {
				assertEquals(2, status.vehicleNotifications.size());
				assertEquals(vn1, status.vehicleNotifications.get(0));
				assertEquals(vn3, status.vehicleNotifications.get(1));

				assertEquals(2, status.receivingOperationScheduleChangedVehicleNotifications.size());
				assertEquals(vn0, status.receivingOperationScheduleChangedVehicleNotifications.get(0));
				assertEquals(vn2, status.receivingOperationScheduleChangedVehicleNotifications.get(1));
				
				assertTrue(status.sendLists.repliedVehicleNotifications.isEmpty());
				assertTrue(status.receivedOperationScheduleChangedVehicleNotifications.isEmpty());
			}
		});
	}
	
	public void testSetLocation() {
		String provider = "test";
		for (Integer i = 0; i < 20; ++i) {
			final Integer lat = 10 + i * 2; // TODO:値が丸まっていないかのテスト
			final Integer lon = 45 + i;
			Location l = new Location(provider);
			l.setLatitude(lat);
			l.setLongitude(lon);
			cl.postEvent(new LocationReceivedEvent(l));
			getInstrumentation().waitForIdleSync();
			sa.read(new VoidReader() {
				@Override
				public void read(Status status) {
					assertEquals(status.serviceUnitStatusLog.getLatitude().intValue(), lat.intValue());
					assertEquals(status.serviceUnitStatusLog.getLongitude().intValue(), lon.intValue());
				}
			});
		}
	}
}
