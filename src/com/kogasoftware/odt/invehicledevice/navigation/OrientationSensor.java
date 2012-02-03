package com.kogasoftware.odt.invehicledevice.navigation;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

abstract public class OrientationSensor implements SensorEventListener {
	private final SensorManager sensorManager;

	public OrientationSensor(Context context) {
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
	}

	abstract public void create();

	abstract public void destroy();

	protected SensorManager getSensorManager() {
		return sensorManager;
	}

	abstract public void onOrientationChanged(Double orientation);
}
