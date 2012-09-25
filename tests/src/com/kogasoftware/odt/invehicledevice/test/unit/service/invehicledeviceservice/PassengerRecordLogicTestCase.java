package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import junitx.framework.ListAssert;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
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

	public void testSelectAndUnselect_GetOn() throws Exception {
		Integer m = 20;
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
		assertFalse(prl.isSelected(pr));
		Thread.sleep(m);
		prl.select(pr);
		assertTrue(prl.isSelected(pr));
		assertTrue(pr.isRiding());
		assertTrue(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		assertEquals(Optional.of(os.getId()),
				pr.getDepartureOperationScheduleId());
		Thread.sleep(m);
		prl.unselect(pr);
		assertFalse(prl.isSelected(pr));
		assertTrue(pr.isUnhandled());
		assertFalse(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		MoreAsserts.assertNotEqual(Optional.of(os.getId()),
				pr.getDepartureOperationScheduleId());
	}

	public void testSelectAndUnselect_GetOff() throws Exception {
		Integer m = 20;
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
		assertFalse(prl.isSelected(pr));
		Thread.sleep(m);
		prl.select(pr);
		assertTrue(prl.isSelected(pr));
		assertTrue(pr.isGotOff());
		assertTrue(pr.getGetOnTime().isPresent());
		assertTrue(pr.getGetOffTime().isPresent());
		assertEquals(Optional.of(os.getId()),
				pr.getArrivalOperationScheduleId());
		Thread.sleep(m);
		prl.unselect(pr);
		assertFalse(prl.isSelected(pr));
		assertTrue(pr.isRiding());
		assertTrue(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		MoreAsserts.assertNotEqual(Optional.of(os.getId()),
				pr.getArrivalOperationScheduleId());
	}

	public void testSelectAndUnselect_GetOnAndGetOff() throws Exception {
		Integer m = 20;
		User u = new User();
		Reservation r = new Reservation();
		final PassengerRecord pr = new PassengerRecord();
		pr.setReservation(r);
		pr.setUser(u);
		r.setDepartureScheduleId(135);
		r.setArrivalScheduleId(os.getId());
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.add(pr);
			}
		});
		assertTrue(pr.isUnhandled());
		assertFalse(prl.isSelected(pr));
		Thread.sleep(m);
		prl.select(pr);
		assertTrue(prl.isSelected(pr));
		assertTrue(pr.isGotOff());
		assertTrue(pr.getGetOnTime().isPresent());
		assertTrue(pr.getGetOffTime().isPresent());
		assertEquals(Optional.of(135), pr.getDepartureOperationScheduleId());
		assertEquals(Optional.of(os.getId()),
				pr.getArrivalOperationScheduleId());
		Thread.sleep(m);
		prl.unselect(pr);
		assertFalse(prl.isSelected(pr));
		assertTrue(pr.isUnhandled());
		assertFalse(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		MoreAsserts.assertNotEqual(Optional.of(os.getId()),
				pr.getDepartureOperationScheduleId());
	}

	public void testCanGetOff() {
		PassengerRecord pr = new PassengerRecord();
		pr.setGetOnTime(new Date());
		assertTrue(prl.canGetOff(pr));
		prl.select(pr);
		assertTrue(prl.canGetOff(pr));
		prl.unselect(pr);
		assertTrue(prl.canGetOff(pr));
		pr.clearGetOnTime();
		assertFalse(prl.canGetOff(pr));
		prl.select(pr);
		assertFalse(prl.canGetOff(pr));
		prl.unselect(pr);
		assertFalse(prl.canGetOff(pr));
	}

	public void testCanGetOn() {
		PassengerRecord pr = new PassengerRecord();
		assertTrue(prl.canGetOn(pr));
		prl.select(pr);
		assertTrue(prl.canGetOn(pr));
		prl.unselect(pr);
		assertTrue(prl.canGetOn(pr));
		pr.setGetOnTime(new Date());
		assertFalse(prl.canGetOn(pr));
		prl.select(pr);
		assertFalse(prl.canGetOn(pr));
		prl.unselect(pr);
		assertFalse(prl.canGetOn(pr));
	}

	public void testGetNoGettingOffPassengerRecords() {
		Reservation r1 = new Reservation();
		Reservation r2 = new Reservation();
		final PassengerRecord pr1 = new PassengerRecord();
		final PassengerRecord pr2 = new PassengerRecord();
		pr1.setReservation(r1);
		pr2.setReservation(r2);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.add(pr1);
			}
		});

		ListAssert.assertEquals(Lists.newArrayList(),
				prl.getNoGettingOnPassengerRecords());

		r1.setDepartureScheduleId(os.getId());
		ListAssert.assertEquals(Lists.newArrayList(pr1),
				prl.getNoGettingOnPassengerRecords());

		pr1.setGetOnTime(new Date());
		ListAssert.assertEquals(Lists.newArrayList(),
				prl.getNoGettingOnPassengerRecords());

		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.add(pr2);
			}
		});

		pr1.clearGetOnTime();
		r2.setDepartureScheduleId(os.getId());
		ListAssert.assertEquals(Lists.newArrayList(pr1, pr2),
				prl.getNoGettingOnPassengerRecords());

		pr2.setGetOnTime(new Date());
		ListAssert.assertEquals(Lists.newArrayList(pr1),
				prl.getNoGettingOnPassengerRecords());

		pr2.setGetOnTime(new Date());
		ListAssert.assertEquals(Lists.newArrayList(pr1),
				prl.getNoGettingOnPassengerRecords());
	}

	public void testGetNoGettingOnPassengerRecords() {
		Reservation r1 = new Reservation();
		Reservation r2 = new Reservation();
		final PassengerRecord pr1 = new PassengerRecord();
		final PassengerRecord pr2 = new PassengerRecord();
		pr1.setReservation(r1);
		pr2.setReservation(r2);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.add(pr1);
			}
		});

		ListAssert.assertEquals(Lists.newArrayList(),
				prl.getNoGettingOnPassengerRecords());

		r1.setDepartureScheduleId(os.getId());
		ListAssert.assertEquals(Lists.newArrayList(pr1),
				prl.getNoGettingOnPassengerRecords());

		pr1.setGetOnTime(new Date());
		ListAssert.assertEquals(Lists.newArrayList(),
				prl.getNoGettingOnPassengerRecords());

		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.passengerRecords.add(pr2);
			}
		});

		pr1.clearGetOnTime();
		r2.setDepartureScheduleId(os.getId());
		ListAssert.assertEquals(Lists.newArrayList(pr1, pr2),
				prl.getNoGettingOnPassengerRecords());

		pr2.setGetOnTime(new Date());
		ListAssert.assertEquals(Lists.newArrayList(pr1),
				prl.getNoGettingOnPassengerRecords());

		pr2.setGetOnTime(new Date());
		ListAssert.assertEquals(Lists.newArrayList(pr1),
				prl.getNoGettingOnPassengerRecords());
	}

	public void testGetNoPaymentPassengerRecords() {
		// not implemented
	}

	public void testIsGetOffScheduled() {
		Reservation r = new Reservation();
		PassengerRecord pr = new PassengerRecord();
		pr.setReservation(r);
		assertFalse(prl.isGetOffScheduled(pr));

		r.setDepartureScheduleId(os.getId());
		assertFalse(prl.isGetOffScheduled(pr));
		r.clearArrivalScheduleId();

		r.setArrivalScheduleId(os.getId());
		assertTrue(prl.isGetOffScheduled(pr));

		r.setArrivalScheduleId(os.getId() + 1);
		assertFalse(prl.isGetOffScheduled(pr));
	}

	public void testIsGetOnScheduled() {
		Reservation r = new Reservation();
		PassengerRecord pr = new PassengerRecord();
		pr.setReservation(r);
		assertFalse(prl.isGetOnScheduled(pr));

		r.setArrivalScheduleId(os.getId());
		assertFalse(prl.isGetOnScheduled(pr));
		r.clearArrivalScheduleId();

		r.setDepartureScheduleId(os.getId());
		assertTrue(prl.isGetOnScheduled(pr));

		r.setDepartureScheduleId(os.getId() + 1);
		assertFalse(prl.isGetOnScheduled(pr));
	}
}
