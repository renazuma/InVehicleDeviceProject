package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.TemperatureChangedEvent;

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

		float celsius = event.values[0];
		commonLogic.postEvent(new TemperatureChangedEvent(celsius));
		Log.v(TAG, "temperature changed=" + celsius);
	}
}
