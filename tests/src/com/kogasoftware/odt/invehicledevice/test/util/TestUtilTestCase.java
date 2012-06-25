package com.kogasoftware.odt.invehicledevice.test.util;

import java.util.List;

import android.test.ActivityInstrumentationTestCase2;

import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.ui.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;

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
		throw new WebAPIException("error");
	}
};
