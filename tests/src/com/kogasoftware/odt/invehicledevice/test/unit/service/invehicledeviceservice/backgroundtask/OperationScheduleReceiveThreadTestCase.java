package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.backgroundtask;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.OperationScheduleReceiveThread;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

public class OperationScheduleReceiveThreadTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	OperationScheduleReceiveThread osrt;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		if (osrt != null) {
			osrt.interrupt();
			osrt.join();
		}
		super.tearDown();
	}

	public void xtest初回のOperationSchedule受信成功ではUpdatedOperationScheduleReceivedEvent送出しない()
			throws Exception {
		class TestDataSource extends DummyDataSource {
			@Override
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
					l.add(OperationSchedule.parse(j1));
					JSONObject j2 = new JSONObject(
							"{id:2, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
									+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, reservations_as_arrival: [ "
									+ r1 + " ]}");
					l.add(OperationSchedule.parse(j2));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return l;
			}
		}
		TestUtil.clearStatus();
		TestUtil.setDataSource(new TestDataSource());
		osrt = new OperationScheduleReceiveThread(null);
		assertFalse(true);
	}

	List<OperationSchedule> getDummyOperationSchedules() {
		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, user: {id: 1, last_name: 'ああああ', first_name: 'いちごう'}}";
			JSONObject j1 = new JSONObject(
					"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場A', name_ruby: 'のりおりばえー'}, "
							+ "reservations_as_departure: [" + r1 + "]}");
			l.add(OperationSchedule.parse(j1));
			JSONObject j2 = new JSONObject(
					"{id:2, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, reservations_as_arrival: [ "
							+ r1 + " ]}");
			l.add(OperationSchedule.parse(j2));
		} catch (JSONException e) {
			fail();
		}
		return l;
	}

	public void testUpdatedOperationScheduleReceiveStartEvent通知が無い場合OperationScheduleを受信はしない()
			throws Exception {
		final AtomicInteger seq = new AtomicInteger(0);
		class TestDataSource extends DummyDataSource {
			@Override
			public List<OperationSchedule> getOperationSchedules()
					throws WebAPIException {
				seq.addAndGet(1);
				return getDummyOperationSchedules();
			}
		}
		TestUtil.clearStatus();
		TestUtil.setDataSource(new TestDataSource());
		osrt = new OperationScheduleReceiveThread(null);
		osrt.start();
		Thread.sleep(10 * 1000);
		assertEquals(0, seq.get());
		assertEquals(0, 1);
	}

	public void testUpdatedOperationScheduleReceiveStartEvent通知でのOperationScheduleを受信()
			throws Exception {
		final AtomicInteger seq = new AtomicInteger(0);

		class TestDataSource extends DummyDataSource {
			@Override
			public List<OperationSchedule> getOperationSchedules()
					throws WebAPIException {
				seq.addAndGet(1);
				return getDummyOperationSchedules();
			}
		}
		TestUtil.clearStatus();
		TestUtil.setDataSource(new TestDataSource());
		osrt = new OperationScheduleReceiveThread(null);
		osrt.start();
		Thread.sleep(10 * 1000);
		assertEquals(1, seq.get());
		assertEquals(1, 0);
	}

	public void testUpdatedOperationScheduleReceiveStartEvent二回通知で二回OperationScheduleを受信()
			throws Exception {
		final AtomicInteger seq = new AtomicInteger(0);
		class TestDataSource extends DummyDataSource {
			@Override
			public List<OperationSchedule> getOperationSchedules()
					throws WebAPIException {
				seq.addAndGet(1);
				return getDummyOperationSchedules();
			}
		}
		TestUtil.clearStatus();
		TestUtil.setDataSource(new TestDataSource());
		osrt = new OperationScheduleReceiveThread(null);
		osrt.start();
		Thread.sleep(10 * 1000);
		assertEquals(2, seq.get());
		assertEquals(2, 0);
	}
}
