package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.backgroundtask;

import android.hardware.SensorEvent;
import android.view.WindowManager;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.ServiceUnitStatusLogLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.OrientationSensorEventListener;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TemperatureSensorEventListenerTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	InVehicleDeviceService s;
	EventDispatcher ed;
	ServiceUnitStatusLogLogic susll;
	WindowManager wm;
	OrientationSensorEventListener osel;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		s = mock(InVehicleDeviceService.class);
		wm = mock(WindowManager.class);
		ed = mock(EventDispatcher.class);
		when(s.getEventDispatcher()).thenReturn(ed);
		osel = new OrientationSensorEventListener(susll, wm);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testOnSensorChanged() throws Exception {
		SensorEvent se = mock(SensorEvent.class);
		osel.onSensorChanged(se);
	}
}
