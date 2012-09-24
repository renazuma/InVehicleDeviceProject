package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.PassengerRecordLogic;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

public class PassengerRecordLogicTestCase extends AndroidTestCase {
	DataSource rds;
	LocalDataSource lds;
	InVehicleDeviceService s;
	PassengerRecordLogic prl;
	OperationSchedule os;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		os = new OperationSchedule();
		os.setId(12345);
		rds = spy(new EmptyDataSource());
		lds = spy(new LocalDataSource(getContext()));
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalDataSource()).thenReturn(lds);
		when(s.getRemoteDataSource()).thenReturn(rds);
		when(s.isOperationInitialized()).thenReturn(true);
		when(s.getCurrentOperationSchedule()).thenReturn(Optional.of(os));
		when(s.getPassengerRecords()).then(new Answer<List<PassengerRecord>>() {
			@Override
			public List<PassengerRecord> answer(InvocationOnMock invocation)
					throws Throwable {
				return lds.withReadLock(new Reader<List<PassengerRecord>>() {
					@Override
					public List<PassengerRecord> read(LocalData localData) {
						return localData.passengerRecords;
					}
				});
			}
		});

		prl = new PassengerRecordLogic(s);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operationSchedules.add(os);
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

	public void testSelect_GetOn() {
		User u = new User();
		Reservation r = new Reservation();
		final PassengerRecord pr = new PassengerRecord();
		pr.setReservation(r);
		pr.setUser(u);
		r.setDepartureScheduleId(os.getId());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.add(pr);
			}
		});
		assertTrue(pr.isUnhandled());
		prl.select(pr);
		assertTrue(pr.isRiding());
		assertTrue(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		assertEquals(Optional.of(os.getId()),
				pr.getDepartureOperationScheduleId());
		prl.unselect(pr);
		assertTrue(pr.isUnhandled());
		assertFalse(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		MoreAsserts.assertNotEqual(Optional.of(os.getId()),
				pr.getDepartureOperationScheduleId());
	}

	public void testSelect_GetOff() {
		User u = new User();
		Reservation r = new Reservation();
		final PassengerRecord pr = new PassengerRecord();
		pr.setReservation(r);
		pr.setUser(u);
		pr.setDepartureOperationScheduleId(0);
		pr.setGetOnTime(new Date());
		r.setArrivalScheduleId(os.getId());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.add(pr);
			}
		});
		assertTrue(pr.isRiding());
		prl.select(pr);
		assertTrue(pr.isGotOff());
		assertTrue(pr.getGetOnTime().isPresent());
		assertTrue(pr.getGetOffTime().isPresent());
		assertEquals(Optional.of(os.getId()),
				pr.getArrivalOperationScheduleId());
		prl.unselect(pr);
		assertTrue(pr.isRiding());
		assertTrue(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		MoreAsserts.assertNotEqual(Optional.of(os.getId()),
				pr.getArrivalOperationScheduleId());
	}

	public void testSelect_GetOnAndGetOff() {
		User u = new User();
		Reservation r = new Reservation();
		final PassengerRecord pr = new PassengerRecord();
		pr.setReservation(r);
		pr.setUser(u);
		r.setDepartureScheduleId(0);
		r.setArrivalScheduleId(os.getId());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.add(pr);
			}
		});
		assertTrue(pr.isUnhandled());
		prl.select(pr);
		assertTrue(pr.isGotOff());
		assertTrue(pr.getGetOnTime().isPresent());
		assertTrue(pr.getGetOffTime().isPresent());
		assertEquals(Optional.of(os.getId()),
				pr.getDepartureOperationScheduleId());
		prl.unselect(pr);
		assertTrue(pr.isUnhandled());
		assertFalse(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		MoreAsserts.assertNotEqual(Optional.of(os.getId()),
				pr.getDepartureOperationScheduleId());
	}

	public void testUnselect() {
	}
}
