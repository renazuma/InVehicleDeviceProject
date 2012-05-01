package com.kogasoftware.odt.invehicledevice.navigation;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class OrientationSensor implements SensorEventListener {
	private final SensorManager sensorManager;

	public OrientationSensor(Context context) {
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
	}

	public abstract void create();

	public abstract void destroy();

	protected SensorManager getSensorManager() {
		return sensorManager;
	}

	public abstract void onOrientationChanged(Double orientation);
}
