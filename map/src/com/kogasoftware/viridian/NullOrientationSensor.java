package com.kogasoftware.viridian;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class NullOrientationSensor extends OrientationSensor {

	public NullOrientationSensor(Context context) {
		super(context);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	}

	@Override
	public void create() {
	}

	@Override
	public void destroy() {
	}

	@Override
	void onOrientationChanged(Double orientation) {
	}
}
