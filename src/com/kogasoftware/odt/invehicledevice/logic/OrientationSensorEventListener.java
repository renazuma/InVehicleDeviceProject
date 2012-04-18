package com.kogasoftware.odt.invehicledevice.logic;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;

public class OrientationSensorEventListener extends LogicUser implements
		SensorEventListener {
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

		// 再描画
		final Float degree = values[SensorManager.DATA_X];
		logic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.orientation = Math.toRadians(360.0f - degree);
			}
		});
		Log.i(TAG, "orientation changed=" + degree);
	}
}
