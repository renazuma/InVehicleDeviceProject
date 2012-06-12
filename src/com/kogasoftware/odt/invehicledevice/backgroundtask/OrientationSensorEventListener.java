package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.OrientationChangedEvent;

public class OrientationSensorEventListener implements SensorEventListener {
	private static final Long SAVE_PERIOD_MILLIS = 200L;
	private Long lastSavedMillis = System.currentTimeMillis();

	private final CommonLogic commonLogic;

	public OrientationSensorEventListener(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ORIENTATION) {
			return;
		}

		// 方位から回転角を設定
		float[] values = event.values;
		final Float degree = values[SensorManager.DATA_X];
		long now = System.currentTimeMillis();
		if (lastSavedMillis + SAVE_PERIOD_MILLIS > now) {
			return;
		}
		lastSavedMillis = now;

		commonLogic.postEvent(new OrientationChangedEvent(360 - degree));
	}
}
