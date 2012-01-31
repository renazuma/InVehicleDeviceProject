package com.kogasoftware.odt.invehicledevice.map;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

abstract public class OrientationSensor implements SensorEventListener {
	interface OrientationSensorListener {

	}

	protected final Context context;

	public OrientationSensor(Context context) {
		this.context = context;
	}

	abstract public void create();

	abstract public void destroy();

	protected SensorManager getSensorManager() {
		return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
	}

	abstract void onOrientationChanged(Double orientation);
}
