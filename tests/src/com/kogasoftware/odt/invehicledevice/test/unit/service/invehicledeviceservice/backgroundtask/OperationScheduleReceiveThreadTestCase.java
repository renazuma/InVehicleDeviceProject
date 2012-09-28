package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.backgroundtask;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.mockito.Mockito;
import org.mockito.verification.VerificationWithTimeout;

import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.OperationScheduleReceiveThread;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
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

	public void testRun_NotifyOperationScheduleReceiveFailed() throws Exception {
		VerificationWithTimeout t = timeout((int) (OperationScheduleReceiveThread.VOICE_DELAY_MILLIS * 1.2));
		final AtomicBoolean fail = new AtomicBoolean(true);
		DataSource ds = new EmptyDataSource() {
			@Override
			public int getOperationSchedules(
					WebAPICallback<List<OperationSchedule>> callback) {
				if (fail.get()) {
					callback.onException(0, new WebAPIException(
							new RuntimeException()));
				}
				return 0;
			}
		};
		when(s.getRemoteDataSource()).thenReturn(ds);
		when(s.getLocalDataSource()).thenReturn(new LocalDataSource());

		osrt.start();

		fail.set(true);
		verify(s, t.never()).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(s, times(1)).notifyOperationScheduleReceiveFailed();

		fail.set(false);
		verify(s, t.times(1)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(s, times(1)).notifyOperationScheduleReceiveFailed();

		fail.set(true);
		verify(s, t.times(1)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(s, times(2)).notifyOperationScheduleReceiveFailed();

		fail.set(false);
		verify(s, t.times(1)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(s, times(3)).notifyOperationScheduleReceiveFailed();

		osrt.startNewOperationScheduleReceive();

		fail.set(true);
		verify(s, t.times(1)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(s, times(3)).notifyOperationScheduleReceiveFailed();

		fail.set(false);
		verify(s, t.times(2)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(s, times(4)).notifyOperationScheduleReceiveFailed();

		osrt.interrupt();
		osrt.join(5000);
		assertFalse(osrt.isAlive());
	}

	public void testRun() throws Exception {
		when(s.getRemoteDataSource()).thenReturn(new EmptyDataSource());
		when(s.getLocalDataSource()).thenReturn(new LocalDataSource());

		VerificationWithTimeout t = timeout((int) (OperationScheduleReceiveThread.VOICE_DELAY_MILLIS * 1.2));
		osrt.start();
		verify(s, t.times(1)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());

		verify(s, t.times(1)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());

		osrt.startNewOperationScheduleReceive();
		verify(s, t.times(2)).mergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());

		osrt.interrupt();
		osrt.join(5000);
		assertFalse(osrt.isAlive());
	}

}
