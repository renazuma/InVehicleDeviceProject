package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.ServiceUnitStatusLogLogic;

public class TemperatureSensorEventListener implements SensorEventListener {
	private static final String TAG = TemperatureSensorEventListener.class
			.getSimpleName();

	protected final ServiceUnitStatusLogLogic serviceUnitStatusLogLogic;

	public TemperatureSensorEventListener(ServiceUnitStatusLogLogic serviceUnitStatusLogLogic) {
		this.serviceUnitStatusLogLogic = serviceUnitStatusLogLogic;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_TEMPERATURE) {
			return;
		}

		float celsius = event.values[0];
		serviceUnitStatusLogLogic.changeTemperature((double) celsius);
		Log.v(TAG, "temperature changed=" + celsius);
	}
}
