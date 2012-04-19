package com.kogasoftware.odt.invehicledevice.logic;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;

public class TemperatureSensorEventListener extends LogicUser implements
		SensorEventListener {
	private static final String TAG = TemperatureSensorEventListener.class
			.getSimpleName();

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (!getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();

		if (event.sensor.getType() != Sensor.TYPE_TEMPERATURE) {
			return;
		}

		final float celsius = event.values[0];
		logic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.temperature = Optional.of((int) celsius);
			}
		});

		Log.i(TAG, "temperature changed=" + celsius);
	}
}
