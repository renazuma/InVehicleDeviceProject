package com.kogasoftware.odt.invehicledevice.test.unit.logic;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.TemperatureChangedEvent;
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
				status.vehicleNotifications.clear();
				status.vehicleNotifications.add(vn1);
				status.sendLists.repliedVehicleNotifications.clear();
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
				status.vehicleNotifications.clear();
				status.vehicleNotifications.add(vn1);
				status.vehicleNotifications.add(vn2);
				status.sendLists.repliedVehicleNotifications.clear();
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
}
