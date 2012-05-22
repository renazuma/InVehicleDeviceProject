package com.kogasoftware.odt.invehicledevice.test.util;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

import android.test.ActivityInstrumentationTestCase2;

public class TestUtilTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	public TestUtilTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	public void testWaitForStartUi() throws Exception {
		StatusAccess.clearSavedFile();
		DataSourceFactory.setInstance(new DummyDataSource());
		assertTrue(TestUtil.waitForStartUi(getActivity()));
	}

	public void testWaitForStartUiTimeout() throws Exception {
		StatusAccess.clearSavedFile();
		DataSourceFactory.setInstance(new NoOperationScheduleDataSource());
		assertFalse(TestUtil.waitForStartUi(getActivity()));
	}
}

class NoOperationScheduleDataSource implements DataSource {
	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public InVehicleDevice getInVehicleDevice() throws WebAPIException {
		return new InVehicleDevice();
	}

	@Override
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		return 0;
	}

	@Override
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		return 0;
	}

	@Override
	public List<OperationSchedule> getOperationSchedules()
			throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		throw new WebAPIException(false, "error");
	}

	@Override
	public List<VehicleNotification> getVehicleNotifications()
			throws WebAPIException {
		return new LinkedList<VehicleNotification>();
	}

	@Override
	public Reservation postReservation(Integer reservationCandidateId)
			throws WebAPIException {
		return new Reservation();
	}

	@Override
	public List<ReservationCandidate> postReservationCandidates(Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId)
			throws WebAPIException {
		return new LinkedList<ReservationCandidate>();
	}

	@Override
	public void putReservationTransferredAt(Integer id, Date transferredAt)
			throws WebAPIException {
	}

	@Override
	public void putVehicleNotificationReadAt(Integer id, Date readAt)
			throws WebAPIException {
	}

	@Override
	public int responseVehicleNotification(VehicleNotification vn,
			int response, WebAPICallback<VehicleNotification> callback) {
		return 0;
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback) {
		return 0;
	}

	@Override
	public void close() {
	}

	@Override
	public void cancelGetOnPassenger(Reservation reservation,
			EmptyWebAPICallback<PassengerRecord> callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancelGetOffPassenger(Reservation reservation,
			EmptyWebAPICallback<PassengerRecord> callback) {
		// TODO Auto-generated method stub
		
	}
};
