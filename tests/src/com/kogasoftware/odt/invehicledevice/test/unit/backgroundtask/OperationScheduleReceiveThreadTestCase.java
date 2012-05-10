package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Function;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.backgroundtask.OperationScheduleReceiveThread;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.VehicleNotifications;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceiveStartEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleReceivedEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class OperationScheduleReceiveThreadTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	OperationScheduleReceiveThread osrt;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		if (cl != null) {
			cl.dispose();
		}
		if (osrt != null) {
			osrt.interrupt();
			osrt.join();
		}
		super.tearDown();
	}

	public void xtest初回のOperationSchedule受信成功ではUpdatedOperationScheduleReceivedEvent送出しない()
			throws Exception {
		class TestDataSource extends DummyDataSource {
			public List<OperationSchedule> getOperationSchedules()
					throws WebAPIException {
				List<OperationSchedule> l = new LinkedList<OperationSchedule>();
				try {
					String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, user: {id: 1, last_name: 'ああああ', first_name: 'いちごう'}}";
					JSONObject j1 = new JSONObject(
							"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
									+ "platform: {name: '乗降場A', name_ruby: 'のりおりばえー'}, "
									+ "reservations_as_departure: ["
									+ r1
									+ "]}");
					l.add(new OperationSchedule(j1));
					JSONObject j2 = new JSONObject(
							"{id:2, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
									+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, reservations_as_arrival: [ "
									+ r1 + " ]}");
					l.add(new OperationSchedule(j2));
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return l;
			}
		}
		TestUtil.clearStatus();
		TestUtil.setDataSource(new TestDataSource());
		cl = newCommonLogic();
		final CountDownLatch cdl = new CountDownLatch(1);
		cl.registerEventListener(new Function<UpdatedOperationScheduleReceivedEvent, Void>() {
			@Subscribe
			@Override
			public Void apply(UpdatedOperationScheduleReceivedEvent e) {
				cdl.countDown();
				return null;
			}
		});
		osrt = new OperationScheduleReceiveThread(cl);
		assertFalse(cdl.await(2, TimeUnit.SECONDS));
	}

	List<OperationSchedule> getDummyOperationSchedules() {
		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, user: {id: 1, last_name: 'ああああ', first_name: 'いちごう'}}";
			JSONObject j1 = new JSONObject(
					"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場A', name_ruby: 'のりおりばえー'}, "
							+ "reservations_as_departure: [" + r1 + "]}");
			l.add(new OperationSchedule(j1));
			JSONObject j2 = new JSONObject(
					"{id:2, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, reservations_as_arrival: [ "
							+ r1 + " ]}");
			l.add(new OperationSchedule(j2));
		} catch (ParseException e) {
			fail();
		} catch (JSONException e) {
			fail();
		}
		return l;
	}

	class TestEventSubscriber {
		final Class<?> c;
		CountDownLatch cdl = new CountDownLatch(1);
		Semaphore s = new Semaphore(0);

		public TestEventSubscriber(Class<?> c) {
			this.c = c;
		}

		@Subscribe
		public void handle(Object object) {
			if (c.isInstance(object)) {
				cdl.countDown();
				s.release();
			}
		}
	}

	public void testUpdatedOperationScheduleReceiveStartEvent通知が無い場合二度めのOperationScheduleを受信はしない()
			throws Exception {
		final AtomicInteger seq = new AtomicInteger(0);
		class TestDataSource extends DummyDataSource {
			public List<OperationSchedule> getOperationSchedules()
					throws WebAPIException {
				seq.addAndGet(1);
				return getDummyOperationSchedules();
			}
		}
		TestUtil.clearStatus();
		TestUtil.setDataSource(new TestDataSource());
		cl = newCommonLogic();
		osrt = new OperationScheduleReceiveThread(cl);
		TestEventSubscriber tes = new TestEventSubscriber(
				UpdatedOperationScheduleReceivedEvent.class);
		cl.registerEventListener(tes);
		cl.registerEventListener(osrt);
		osrt.start();
		Thread.sleep(2 * 1000);
		assertEquals(1, seq.get());
		assertEquals(1, tes.s.availablePermits());
	}

	public void testUpdatedOperationScheduleReceiveStartEvent通知で二度めのOperationScheduleを受信()
			throws Exception {
		final AtomicInteger seq = new AtomicInteger(0);

		class TestDataSource extends DummyDataSource {
			public List<OperationSchedule> getOperationSchedules()
					throws WebAPIException {
				seq.addAndGet(1);
				return getDummyOperationSchedules();
			}
		}
		TestUtil.clearStatus();
		TestUtil.setDataSource(new TestDataSource());
		cl = newCommonLogic();
		osrt = new OperationScheduleReceiveThread(cl);
		TestEventSubscriber tes = new TestEventSubscriber(
				UpdatedOperationScheduleReceivedEvent.class);
		cl.registerEventListener(tes);
		cl.registerEventListener(osrt);
		osrt.start();
		cl.postEvent(new UpdatedOperationScheduleReceiveStartEvent());
		Thread.sleep(2 * 1000);
		assertEquals(2, seq.get());
		assertEquals(2, tes.s.availablePermits());
	}

	public void testUpdatedOperationScheduleReceiveStartEvent二回通知で三度めのOperationScheduleを受信()
			throws Exception {
		final AtomicInteger seq = new AtomicInteger(0);
		class TestDataSource extends DummyDataSource {
			public List<OperationSchedule> getOperationSchedules()
					throws WebAPIException {
				seq.addAndGet(1);
				return getDummyOperationSchedules();
			}
		}
		TestUtil.clearStatus();
		TestUtil.setDataSource(new TestDataSource());
		cl = newCommonLogic();
		osrt = new OperationScheduleReceiveThread(cl);
		osrt.start();
		TestEventSubscriber tes = new TestEventSubscriber(
				UpdatedOperationScheduleReceivedEvent.class);
		cl.registerEventListener(tes);
		cl.registerEventListener(osrt);
		cl.postEvent(new UpdatedOperationScheduleReceiveStartEvent());
		cl.postEvent(new UpdatedOperationScheduleReceiveStartEvent());
		Thread.sleep(2 * 1000);
		assertEquals(3, seq.get());
		assertEquals(3, tes.s.availablePermits());
	}
}
