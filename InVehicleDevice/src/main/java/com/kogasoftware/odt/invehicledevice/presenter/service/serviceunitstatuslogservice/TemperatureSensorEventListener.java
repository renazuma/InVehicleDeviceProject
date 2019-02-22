package com.kogasoftware.odt.invehicledevice.presenter.service.serviceunitstatuslogservice;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.ServiceUnitStatusLog;

/**
 * 温度センサー
 */
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
		final ContentValues contentValues = new ContentValues();
		contentValues.put(ServiceUnitStatusLog.Columns.TEMPERATURE, celsius);
		new Thread() {
			@Override
			public void run() {
				contentResolver.update(ServiceUnitStatusLog.CONTENT.URI,
						contentValues, null, null);
			}
		}.start();
	}
}
