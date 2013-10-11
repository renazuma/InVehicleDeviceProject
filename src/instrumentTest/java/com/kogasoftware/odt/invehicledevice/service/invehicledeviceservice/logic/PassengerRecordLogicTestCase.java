package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.logic;

import static org.mockito.Mockito.*;

import java.util.Date;

import android.test.AndroidTestCase;
import android.test.MoreAsserts;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.EmptyInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalStorage.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.PassengerRecordLogic;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;

public class PassengerRecordLogicTestCase extends AndroidTestCase {
	InVehicleDeviceApiClient rds;
	LocalStorage lds;
	InVehicleDeviceService s;
	PassengerRecordLogic prl;
	OperationSchedule os;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		os = new OperationSchedule();
		os.setOperationRecord(new OperationRecord());
		os.setId(12345);
		rds = spy(new EmptyInVehicleDeviceApiClient());
		lds = new LocalStorage(getContext());
		s = mock(InVehicleDeviceService.class);
		when(s.getLocalStorage()).thenReturn(lds);
		when(s.getApiClient()).thenReturn(rds);
		when(s.isOperationInitialized()).thenReturn(true);
		prl = new PassengerRecordLogic(s);
		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operation.passengerRecords.clear();
				localData.operation.operationSchedules.clear();
				localData.operation.operationSchedules.add(os);
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
				localData.operation.passengerRecords.add(pr);
			}
		});
		assertTrue(pr.isUnhandled());
		// assertFalse(prl.isSelected(pr));
		Thread.sleep(m);
		// prl.select(pr);
		// assertTrue(prl.isSelected(pr));
		assertTrue(pr.isRiding());
		assertTrue(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		assertEquals(Optional.of(os.getId()),
				pr.getDepartureOperationScheduleId());
		Thread.sleep(m);
		// prl.unselect(pr);
		// assertFalse(prl.isSelected(pr));
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
				localData.operation.passengerRecords.add(pr);
			}
		});
		assertTrue(pr.isRiding());
		// assertFalse(prl.isSelected(pr));
		Thread.sleep(m);
		// prl.select(pr);
		// assertTrue(prl.isSelected(pr));
		assertTrue(pr.isGotOff());
		assertTrue(pr.getGetOnTime().isPresent());
		assertTrue(pr.getGetOffTime().isPresent());
		assertEquals(Optional.of(os.getId()),
				pr.getArrivalOperationScheduleId());
		Thread.sleep(m);
		// prl.unselect(pr);
		// assertFalse(prl.isSelected(pr));
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
				localData.operation.passengerRecords.add(pr);
			}
		});
		assertTrue(pr.isUnhandled());
		// assertFalse(prl.isSelected(pr));
		Thread.sleep(m);
		// prl.select(pr);
		// assertTrue(prl.isSelected(pr));
		assertTrue(pr.isGotOff());
		assertTrue(pr.getGetOnTime().isPresent());
		assertTrue(pr.getGetOffTime().isPresent());
		assertEquals(Optional.of(135), pr.getDepartureOperationScheduleId());
		assertEquals(Optional.of(os.getId()),
				pr.getArrivalOperationScheduleId());
		Thread.sleep(m);
		// prl.unselect(pr);
		// assertFalse(prl.isSelected(pr));
		assertTrue(pr.isUnhandled());
		assertFalse(pr.getGetOnTime().isPresent());
		assertFalse(pr.getGetOffTime().isPresent());
		MoreAsserts.assertNotEqual(Optional.of(os.getId()),
				pr.getDepartureOperationScheduleId());
	}

	public void testCanGetOff() {
		PassengerRecord pr = new PassengerRecord();
		pr.setGetOnTime(new Date());
		// assertTrue(prl.canGetOff(pr));
		// prl.select(pr);
		// assertTrue(prl.canGetOff(pr));
		// prl.unselect(pr);
		// assertTrue(prl.canGetOff(pr));
		pr.clearGetOnTime();
		// assertFalse(prl.canGetOff(pr));
		// prl.select(pr);
		// assertFalse(prl.canGetOff(pr));
		// prl.unselect(pr);
		// assertFalse(prl.canGetOff(pr));
	}

	public void testCanGetOn() {
		PassengerRecord pr = new PassengerRecord();
		// assertTrue(prl.canGetOn(pr));
		// prl.select(pr);
		// assertTrue(prl.canGetOn(pr));
		// prl.unselect(pr);
		// assertTrue(prl.canGetOn(pr));
		pr.setGetOnTime(new Date());
		// assertFalse(prl.canGetOn(pr));
		// prl.select(pr);
		// assertFalse(prl.canGetOn(pr));
		// prl.unselect(pr);
		// assertFalse(prl.canGetOn(pr));
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
				localData.operation.passengerRecords.add(pr1);
			}
		});

		// ListAssert.assertEquals(Lists.newArrayList(),
		// prl.getNoGettingOnPassengerRecords());

		r1.setDepartureScheduleId(os.getId());
		// ListAssert.assertEquals(Lists.newArrayList(pr1),
		// prl.getNoGettingOnPassengerRecords());

		pr1.setGetOnTime(new Date());
		// ListAssert.assertEquals(Lists.newArrayList(),
		// prl.getNoGettingOnPassengerRecords());

		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operation.passengerRecords.add(pr2);
			}
		});

		pr1.clearGetOnTime();
		r2.setDepartureScheduleId(os.getId());
		// ListAssert.assertEquals(Lists.newArrayList(pr1, pr2),
		// prl.getNoGettingOnPassengerRecords());

		pr2.setGetOnTime(new Date());
		// ListAssert.assertEquals(Lists.newArrayList(pr1),
		// prl.getNoGettingOnPassengerRecords());

		pr2.setGetOnTime(new Date());
		// ListAssert.assertEquals(Lists.newArrayList(pr1),
		// prl.getNoGettingOnPassengerRecords());
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
				localData.operation.passengerRecords.add(pr1);
			}
		});

		// ListAssert.assertEquals(Lists.newArrayList(),
		// prl.getNoGettingOnPassengerRecords());

		r1.setDepartureScheduleId(os.getId());
		// ListAssert.assertEquals(Lists.newArrayList(pr1),
		// prl.getNoGettingOnPassengerRecords());

		pr1.setGetOnTime(new Date());
		// ListAssert.assertEquals(Lists.newArrayList(),
		// prl.getNoGettingOnPassengerRecords());

		lds.withWriteLock(new Writer() {
			@Override
			public void write(LocalData localData) {
				localData.operation.passengerRecords.add(pr2);
			}
		});

		pr1.clearGetOnTime();
		r2.setDepartureScheduleId(os.getId());
		// ListAssert.assertEquals(Lists.newArrayList(pr1, pr2),
		// prl.getNoGettingOnPassengerRecords());

		pr2.setGetOnTime(new Date());
		// ListAssert.assertEquals(Lists.newArrayList(pr1),
		// prl.getNoGettingOnPassengerRecords());

		pr2.setGetOnTime(new Date());
		// ListAssert.assertEquals(Lists.newArrayList(pr1),
		// prl.getNoGettingOnPassengerRecords());
	}

	public void testGetNoPaymentPassengerRecords() {
		// not implemented
	}

	public void testIsGetOffScheduled() {
		Reservation r = new Reservation();
		PassengerRecord pr = new PassengerRecord();
		pr.setReservation(r);
		// assertFalse(prl.isGetOffScheduled(pr));

		r.setDepartureScheduleId(os.getId());
		// assertFalse(prl.isGetOffScheduled(pr));
		r.clearArrivalScheduleId();

		r.setArrivalScheduleId(os.getId());
		// assertTrue(prl.isGetOffScheduled(pr));

		r.setArrivalScheduleId(os.getId() + 1);
		// assertFalse(prl.isGetOffScheduled(pr));
	}

	public void testIsGetOnScheduled() {
		Reservation r = new Reservation();
		PassengerRecord pr = new PassengerRecord();
		pr.setReservation(r);
		// assertFalse(prl.isGetOnScheduled(pr));

		r.setArrivalScheduleId(os.getId());
		// assertFalse(prl.isGetOnScheduled(pr));
		r.clearArrivalScheduleId();

		r.setDepartureScheduleId(os.getId());
		// assertTrue(prl.isGetOnScheduled(pr));

		r.setDepartureScheduleId(os.getId() + 1);
		// assertFalse(prl.isGetOnScheduled(pr));
	}
}
