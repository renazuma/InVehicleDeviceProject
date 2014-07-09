package com.kogasoftware.odt.invehicledevice.service.serviceunitstatuslogservice;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLogs;

public class TemperatureSensorEventListener implements SensorEventListener {
	private static final String TAG = TemperatureSensorEventListener.class
			.getSimpleName();
	private final ContentResolver contentResolver;

	public TemperatureSensorEventListener(ContentResolver contentResolver) {
		this.contentResolver = contentResolver;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_TEMPERATURE) {
			onSensorChanged(event.values);
		}
	}

	@VisibleForTesting
	public void onSensorChanged(float[] values) {
		float celsius = values[0];
		Log.v(TAG, "temperature changed=" + celsius);
		ContentValues contentValues = new ContentValues();
		contentValues.put(ServiceUnitStatusLogs.Columns.TEMPERATURE, celsius);
		contentResolver.update(ServiceUnitStatusLogs.CONTENT.URI,
				contentValues, null, null);
	}
}
