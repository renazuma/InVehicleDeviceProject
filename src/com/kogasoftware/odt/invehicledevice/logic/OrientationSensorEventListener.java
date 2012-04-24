package com.kogasoftware.odt.invehicledevice.logic;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;

public class OrientationSensorEventListener extends LogicUser implements
		SensorEventListener {
	private static final Long SAVE_PERIOD_MILLIS = 10 * 1000L;
	private Long lastSavedMillis = System.currentTimeMillis();

	private static final String TAG = OrientationSensorEventListener.class
			.getSimpleName();

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (!getLogic().isPresent()) {
			return;
		}
		final Logic logic = getLogic().get();

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

		logic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				// status.orientation = Optional.of(Math.toRadians(360 -
				// degree));
				status.orientation = Optional.of((int) (360 - degree));
			}
		});
		// Log.i(TAG, "orientation changed=" + degree);
	}
}
