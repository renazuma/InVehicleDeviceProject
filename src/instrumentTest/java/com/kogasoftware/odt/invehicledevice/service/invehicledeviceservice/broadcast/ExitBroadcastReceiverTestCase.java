package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.broadcast;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import android.content.Intent;
import android.test.AndroidTestCase;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.broadcast.ExitBroadcastReceiver;

public class ExitBroadcastReceiverTestCase extends
		AndroidTestCase {

	InVehicleDeviceService s;
	ExitBroadcastReceiver ebr;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		ebr = new ExitBroadcastReceiver(s);
	}

	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * exitメソッドを実行
	 */
	public void testOnReceive() throws Exception {
		verify(s, never()).exit();
		ebr.onReceive(getContext(), new Intent());
		verify(s, times(1)).exit();
	}
}
