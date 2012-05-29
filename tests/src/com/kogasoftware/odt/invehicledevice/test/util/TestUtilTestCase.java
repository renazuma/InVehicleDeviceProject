package com.kogasoftware.odt.invehicledevice.test.util;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.logic.datasource.EmptyDataSource;
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
		TestUtil.clearStatus();
		TestUtil.setDataSource(new DummyDataSource());
		assertTrue(TestUtil.waitForStartUi(getActivity()));
	}

	public void testWaitForStartUiTimeout() throws Exception {
		TestUtil.clearStatus();
		TestUtil.setDataSource(new NoOperationScheduleDataSource());
		assertFalse(TestUtil.waitForStartUi(getActivity()));
	}
}

class NoOperationScheduleDataSource extends EmptyDataSource {
	@Override
	public List<OperationSchedule> getOperationSchedules()
			throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		throw new WebAPIException(false, "error");
	}
};
