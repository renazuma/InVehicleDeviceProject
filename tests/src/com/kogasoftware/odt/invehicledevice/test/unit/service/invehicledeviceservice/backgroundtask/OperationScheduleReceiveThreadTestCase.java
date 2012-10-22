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

import com.kogasoftware.odt.invehicledevice.apiclient.DataSource;
import com.kogasoftware.odt.invehicledevice.apiclient.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
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
	EventDispatcher ed;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ed = mock(EventDispatcher.class);
		s = mock(InVehicleDeviceService.class);
		when(s.getEventDispatcher()).thenReturn(ed);
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
		verify(ed, t.never()).dispatchMergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(ed, times(1)).dispatchOperationScheduleReceiveFail();

		fail.set(false);
		verify(ed, t.times(1)).dispatchMergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(ed, times(1)).dispatchOperationScheduleReceiveFail();

		fail.set(true);
		verify(ed, t.times(1)).dispatchMergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(ed, times(2)).dispatchOperationScheduleReceiveFail();

		fail.set(false);
		verify(ed, t.times(1)).dispatchMergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(ed, times(3)).dispatchOperationScheduleReceiveFail();

		osrt.startNewOperationScheduleReceive();

		fail.set(true);
		verify(ed, t.times(1)).dispatchMergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(ed, times(3)).dispatchOperationScheduleReceiveFail();

		fail.set(false);
		verify(ed, t.times(2)).dispatchMergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());
		verify(ed, times(4)).dispatchOperationScheduleReceiveFail();

		osrt.interrupt();
		osrt.join(5000);
		assertFalse(osrt.isAlive());
	}

	public void testRun() throws Exception {
		when(s.getRemoteDataSource()).thenReturn(new EmptyDataSource());
		when(s.getLocalDataSource()).thenReturn(new LocalDataSource());

		VerificationWithTimeout t = timeout((int) (OperationScheduleReceiveThread.VOICE_DELAY_MILLIS * 1.2));
		osrt.start();
		verify(ed, t.times(1)).dispatchMergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());

		verify(ed, t.times(1)).dispatchMergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());

		osrt.startNewOperationScheduleReceive();
		verify(ed, t.times(2)).dispatchMergeOperationSchedules(
				Mockito.<List<OperationSchedule>> any(),
				Mockito.<List<VehicleNotification>> any());

		osrt.interrupt();
		osrt.join(5000);
		assertFalse(osrt.isAlive());
	}

}
