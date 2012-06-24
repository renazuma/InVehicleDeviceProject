package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class TemperatureSensorEventListener implements SensorEventListener {
	private static final String TAG = TemperatureSensorEventListener.class
			.getSimpleName();

	protected final InVehicleDeviceService service;

	public TemperatureSensorEventListener(InVehicleDeviceService service) {
		this.service = service;
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
		service.changeTemperature((double) celsius);
		Log.v(TAG, "temperature changed=" + celsius);
	}
}
