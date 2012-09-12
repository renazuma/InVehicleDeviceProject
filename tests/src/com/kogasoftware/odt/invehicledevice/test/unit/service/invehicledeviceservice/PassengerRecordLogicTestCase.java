package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.PassengerRecordLogic;

import android.test.AndroidTestCase;
import static org.mockito.Mockito.*;

public class PassengerRecordLogicTestCase extends AndroidTestCase {
	public void testConstructor_NoServiceInteractions() {
		InVehicleDeviceService s = mock(InVehicleDeviceService.class);
		new PassengerRecordLogic(s);
		verifyZeroInteractions(s);
	}
}
