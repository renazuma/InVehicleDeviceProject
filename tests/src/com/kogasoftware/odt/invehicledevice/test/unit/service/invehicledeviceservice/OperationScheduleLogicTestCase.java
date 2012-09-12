package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;

import android.test.AndroidTestCase;
import static org.mockito.Mockito.*;

public class OperationScheduleLogicTestCase extends AndroidTestCase {
	public void testConstructor_NoServiceInteractions() {
		InVehicleDeviceService s = mock(InVehicleDeviceService.class);
		new OperationScheduleLogic(s);
		verifyZeroInteractions(s);
	}
}
