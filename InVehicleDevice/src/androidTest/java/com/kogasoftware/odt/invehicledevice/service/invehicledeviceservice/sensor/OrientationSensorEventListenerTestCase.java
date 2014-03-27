package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor;

import static org.mockito.Mockito.mock;
import android.view.WindowManager;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor.OrientationSensorEventListener;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;

public class OrientationSensorEventListenerTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	InVehicleDeviceService s;
	ServiceUnitStatusLogLogic susll;
	WindowManager wm;
	OrientationSensorEventListener osel;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		susll = new ServiceUnitStatusLogLogic(s);
		wm = mock(WindowManager.class);
		osel = new OrientationSensorEventListener(susll, wm);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void xtestOnSensorChanged() throws Exception {
		fail("stub!");
	}
}
