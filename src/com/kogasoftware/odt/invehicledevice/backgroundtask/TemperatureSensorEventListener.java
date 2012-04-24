package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.CommonLogic;
import com.kogasoftware.odt.invehicledevice.Status;
import com.kogasoftware.odt.invehicledevice.StatusAccess.Writer;

public class TemperatureSensorEventListener implements SensorEventListener {
	private static final String TAG = TemperatureSensorEventListener.class
			.getSimpleName();

	private final CommonLogic commonLogic;

	public TemperatureSensorEventListener(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		if (event.sensor.getType() != Sensor.TYPE_TEMPERATURE) {
			return;
		}

		final float celsius = event.values[0];
		commonLogic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.temperature = Optional.of((int) celsius);
			}
		});

		Log.i(TAG, "temperature changed=" + celsius);
	}
}
