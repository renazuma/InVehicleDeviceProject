package com.kogasoftware.odt.invehicledevice.logic;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class TemperatureSensorEventListener extends LogicUser implements
		SensorEventListener {

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	}

}
