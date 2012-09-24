package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.backgroundtask;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.Mockito;

import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.OperationScheduleReceiveThread;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class OperationScheduleReceiveThreadTestCase extends AndroidTestCase {
	OperationScheduleReceiveThread osrt;
	InVehicleDeviceService s;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		osrt = new OperationScheduleReceiveThread(s);
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			if (osrt != null) {
				osrt.interrupt();
			}
		} finally {
			super.tearDown();
		}
	}

	public void testRun() throws Exception {
		DataSource ds = new EmptyDataSource() {
			@Override
			public int getServiceProvider(
					WebAPICallback<ServiceProvider> callback) {
				ServiceProvider sp = new ServiceProvider();
				callback.onSucceed(0, 200, sp);
				return 0;
			}
		};
		when(s.getRemoteDataSource()).thenReturn(ds);
		when(s.getLocalDataSource()).thenReturn(new LocalDataSource());

		Integer m = (int) (OperationScheduleReceiveThread.VOICE_DELAY_MILLIS * 1.2);
		osrt.start();
		Thread.sleep(m);
		verify(s, Mockito.times(1)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());

		Thread.sleep(m);
		verify(s, Mockito.times(1)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());

		osrt.startNewOperationScheduleReceive();
		Thread.sleep(m);
		verify(s, Mockito.times(2)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());

		osrt.interrupt();
		osrt.join(m);
		assertFalse(osrt.isAlive());
	}
}
