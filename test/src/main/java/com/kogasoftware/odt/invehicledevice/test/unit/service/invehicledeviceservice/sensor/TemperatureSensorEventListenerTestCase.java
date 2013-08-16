package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.sensor;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor.TemperatureSensorEventListener;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TemperatureSensorEventListenerTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	EventDispatcher ed;
	ServiceUnitStatusLogLogic susll;
	TemperatureSensorEventListener tsel;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		ed = mock(EventDispatcher.class);
		when(s.getEventDispatcher()).thenReturn(ed);
		tsel = new TemperatureSensorEventListener(susll);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnSensorChanged() throws Exception {
		float[] values = new float[] { 0, 0, 0, 0 };
		tsel.onSensorChanged(values);
	}
}
