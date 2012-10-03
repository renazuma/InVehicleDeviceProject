package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import junitx.framework.ListAssert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.VoidReader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class OperationScheduleLogicTestCase extends AndroidTestCase {
	InVehicleDeviceService s;
	OperationScheduleLogic osl;
	LocalDataSource lds;
	List<OperationSchedule> remotes = Lists.newLinkedList();
	List<VehicleNotification> vns = Lists.newLinkedList();

	@Override
	public void setUp() throws Exception {
		super.setUp();
		lds = new LocalDataSource(getContext());
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalDataSource()).thenReturn(lds);
		when(s.isOperationInitialized()).thenReturn(true);
		osl = new OperationScheduleLogic(s);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.clear();
				localData.operationSchedules.clear();
			}
		});
	}

	@Override
	public void tearDown() throws Exception {
		try {
			Closeables.closeQuietly(lds);
		} finally {
			super.tearDown();
		}
	}

	public void testConstructor_NoServiceInteractions() {
		verifyZeroInteractions(s);
	}

	public void testMergeOperationSchedules() throws JSONException {
		final OperationSchedule local = OperationSchedule.parse(new JSONObject(
				"{id: 12345, updated_at: '2013-08-12'}"));
		final OperationSchedule remote = OperationSchedule
				.parse(new JSONObject("{id: 12345, updated_at: '2000-08-10'}"));
		remotes.add(remote);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationSchedules.add(local);
			}
		});
		osl.mergeOperationSchedules(remotes, vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData localData) {
				assertEquals(1, localData.operationSchedules.size());
				assertEquals(remote.getId(), localData.operationSchedules
						.get(0).getId());
			}
		});
	}

	void callTestMergePassengerRecords(final Boolean preferLocal)
			throws Exception {
		final OperationSchedule remote = OperationSchedule
				.parse(new JSONObject(
						"{id: 1, reservations_as_departure: [{fellow_users: [{id: 2}], passenger_records: [{id: 3, user_id: 2, updated_at: '2000-01-01'}]}]}"));
		String ua = preferLocal ? "2030-12-31" : "1990-01-01";
		final OperationSchedule local = OperationSchedule
				.parse(new JSONObject(
						"{id: 1, reservations_as_departure: [{fellow_users: [{id: 2}], passenger_records: [{id: 3, user_id: 2, updated_at: '"
								+ ua + "', payment: 200}]}]}"));
		final PassengerRecord localPR = local.getReservationsAsDeparture()
				.get(0).getPassengerRecords().get(0);
		final PassengerRecord remotePR = remote.getReservationsAsDeparture()
				.get(0).getPassengerRecords().get(0);
		assertEquals(3, remotePR.getId().intValue());
		assertEquals(3, localPR.getId().intValue());
		assertEquals(Optional.absent(), remotePR.getPayment());
		assertEquals(Optional.of(200), localPR.getPayment());
		remotes.add(remote);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.add(localPR);
				localData.operationSchedules.add(local);
			}
		});
		osl.mergeOperationSchedules(remotes, vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData localData) {
				assertEquals(1, localData.operationSchedules.size());
				assertEquals(remote.getId(), localData.operationSchedules
						.get(0).getId());
				assertEquals(1, localData.passengerRecords.size());
				assertEquals(3, localData.passengerRecords.getFirst().getId()
						.intValue());
				assertEquals(
						preferLocal ? Optional.of(200) : Optional.absent(),
						localData.passengerRecords.getFirst().getPayment());
			}
		});
	}

	public void testMergePassengerRecords_ローカル優先() throws Exception {
		callTestMergePassengerRecords(true);
	}

	public void testMergePassengerRecords_リモート優先() throws Exception {
		callTestMergePassengerRecords(false);
	}

	public void testMergeOperationRecords_ローカル優先() throws Exception {
		callTestMergeOperationRecords(true);
	}

	public void testMergeOperationRecords_リモート優先() throws Exception {
		callTestMergeOperationRecords(false);
	}

	void callTestMergeOperationRecords(final Boolean preferLocal)
			throws Exception {
		String ua = preferLocal ? "2030-12-31" : "1990-01-01";
		String localsString = ""
				+ "[{id: 1, operation_record: {id: 11, updated_at: '%s'}}"
				+ ",{id: 2, operation_record: {id: 12, updated_at: '%s'}}"
				+ ",{id: 3, operation_record: {id: 13, updated_at: '%s'}}"
				+ "]";
		localsString = localsString.replaceAll("%s", ua);
		String remotesString = ""
				+ "[{id: 1, operation_record: {id: 21, updated_at: '2001-08-31'}}"
				+ ",{id: 3, operation_record: {id: 22, updated_at: '2001-08-31'}}"
				+ ",{id: 4, operation_record: {id: 23, updated_at: '2001-08-31'}}"
				+ "]";

		final List<OperationSchedule> locals = OperationSchedule
				.parseList(new JSONArray(localsString));
		remotes.addAll(OperationSchedule
				.parseList(new JSONArray(remotesString)));
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationSchedules.addAll(locals);
			}
		});
		osl.mergeOperationSchedules(remotes, vns);
		lds.withReadLock(new VoidReader() {
			@Override
			public void read(LocalData localData) {
				assertEquals(3, localData.operationSchedules.size());
				for (Integer i = 0; i < localData.operationSchedules.size(); ++i) {
					assertEquals(remotes.get(i).getId(),
							localData.operationSchedules.get(i).getId());
				}
				assertEquals(preferLocal ? locals.get(0).getOperationRecord()
						.get().getId() : remotes.get(0).getOperationRecord()
						.get().getId(), localData.operationSchedules.get(0)
						.getOperationRecord().get().getId());
				assertEquals(remotes.get(1).getOperationRecord().get().getId(),
						localData.operationSchedules.get(1)
								.getOperationRecord().get().getId());
				assertEquals(remotes.get(2).getOperationRecord().get().getId(),
						localData.operationSchedules.get(2)
								.getOperationRecord().get().getId());
			}
		});
	}

	public void callTestGetCurrentOperationSchedules(String jsonString,
			Optional<Integer> id) throws Exception {
		final List<OperationSchedule> locals = OperationSchedule
				.parseList(new JSONArray(jsonString));
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationSchedules.clear();
				localData.operationSchedules.addAll(locals);
			}
		});
		if (id.isPresent()) {
			assertEquals(id.get(), osl.getCurrentOperationSchedule().get()
					.getId());
		} else {
			assertFalse(osl.getCurrentOperationSchedule().isPresent());
		}
	}

	public void testGetCurrentOperationSchedules() throws Exception {
		String s1 = "[]";
		callTestGetCurrentOperationSchedules(s1, Optional.<Integer> absent());

		String s2 = "[{id: 10}]";
		callTestGetCurrentOperationSchedules(s2, Optional.of(10));

		String s3 = "[{id: 11, operation_record: {}}]";
		callTestGetCurrentOperationSchedules(s3, Optional.of(11));

		String s4 = "[{id: 12, operation_record: {arrived_at: '2012-01-01'}}]";
		callTestGetCurrentOperationSchedules(s4, Optional.of(12));

		String s5 = "[{id: 13, operation_record: {departed_at: '2012-01-01'}}]";
		callTestGetCurrentOperationSchedules(s5, Optional.<Integer> absent());

		String s6 = "[{id: 14, operation_record: {departed_at: '2012-01-01'}}, {id: 15}]";
		callTestGetCurrentOperationSchedules(s6, Optional.of(15));
	}

	public void callTestGetOperationSchedules(String jsonString) throws Exception {
		final List<OperationSchedule> locals = OperationSchedule
				.parseList(new JSONArray(jsonString));
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationSchedules.clear();
				localData.operationSchedules.addAll(locals);
			}
		});
		ListAssert.assertEquals(locals, osl.getOperationSchedules());
	}

	public void testGetOperationSchedules() throws Exception {
		String s1 = "[]";
		callTestGetOperationSchedules(s1);

		String s2 = "[{id: 10}]";
		callTestGetOperationSchedules(s2);

		String s3 = "[{id: 11, operation_record: {}}]";
		callTestGetOperationSchedules(s3);

		String s4 = "[{id: 12, operation_record: {arrived_at: '2012-01-01'}}]";
		callTestGetOperationSchedules(s4);

		String s5 = "[{id: 13, operation_record: {departed_at: '2012-01-01'}}]";
		callTestGetOperationSchedules(s5);

		String s6 = "[{id: 14, operation_record: {departed_at: '2012-01-01'}}, {id: 15}]";
		callTestGetOperationSchedules(s6);
	}

	public void callTestGetRemainingOperationSchedules(String expected, String param) throws Exception {
		final List<OperationSchedule> paramList = OperationSchedule
				.parseList(new JSONArray(param));
		final List<OperationSchedule> expectedList = OperationSchedule
				.parseList(new JSONArray(expected));
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationSchedules.clear();
				localData.operationSchedules.addAll(paramList);
			}
		});

		List<OperationSchedule> gotList = osl.getRemainingOperationSchedules();
		assertEquals(expectedList.size(), gotList.size());
		for (Integer i = 0; i < expectedList.size(); ++i) {
			assertEquals(expectedList.get(i).getId(), gotList.get(i).getId());
		}
	}

	public void testGetRemainingOperationSchedules() throws Exception {
		String s1e = "[]";
		String s1p = "[]";
		callTestGetRemainingOperationSchedules(s1e, s1p);

		String s2e = "[{id: 10, operation_record: {}}]";
		String s2p = "[{id: 10, operation_record: {}}]";
		callTestGetRemainingOperationSchedules(s2e, s2p);

		String s3e = "[{id: 11, operation_record: {}}, {id: 12, operation_record: {}}, {id: 13, operation_record: {}}]";
		String s3p = "[{id: 11, operation_record: {}}, {id: 12, operation_record: {}}, {id: 13, operation_record: {}}]";
		callTestGetRemainingOperationSchedules(s3e, s3p);

		String s4e = "[]";
		String s4p = "[{id: 14, operation_record: {departed_at: '2012-01-01'}}]";
		callTestGetRemainingOperationSchedules(s4e, s4p);

		String s5e = "[{id: 16, operation_record: {}}]";
		String s5p = "[{id: 15, operation_record: {departed_at: '2012-01-01'}}, {id: 16, operation_record: {}}]";
		callTestGetRemainingOperationSchedules(s5e, s5p);
	}
}

