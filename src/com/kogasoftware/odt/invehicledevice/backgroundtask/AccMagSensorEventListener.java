package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;

// original: 
// http://kamoland.com/wiki/wiki.cgi?TYPE_ORIENTATION%A4%F2%BB%C8%A4%EF%A4%BA%A4%CB%CA%FD%B0%CC%B3%D1%A4%F2%BC%E8%C6%C0
public class AccMagSensorEventListener implements SensorEventListener {
	private static final Long SAVE_PERIOD_MILLIS = 200L;
	private Long lastSavedMillis = System.currentTimeMillis();

	private final WindowManager windowManager;
	private final CommonLogic commonLogic;
	private float[] accelerometerValues;
	private float[] geomagneticMatrix;
	private boolean sensorReady;

	public AccMagSensorEventListener(CommonLogic commonLogic, WindowManager windowManager) {
		this.commonLogic = commonLogic;
		this.windowManager = windowManager;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			accelerometerValues = event.values.clone();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			geomagneticMatrix = event.values.clone();
			sensorReady = true;
			break;
		default:
			return;
		}

		long now = System.currentTimeMillis();
		if (lastSavedMillis + SAVE_PERIOD_MILLIS > now) {
			return;
		}
		lastSavedMillis = now;
		Float degree = null;

		if (geomagneticMatrix != null && accelerometerValues != null
				&& sensorReady) {
			sensorReady = false;

			float[] R = new float[16];
			float[] I = new float[16];

			SensorManager.getRotationMatrix(R, I, accelerometerValues,
					geomagneticMatrix);

			float[] actual_orientation = new float[3];

			calcActualOrientation(R, actual_orientation);

			// 求まった方位角．ラジアンなので度に変換する
			degree = (float) Math.toDegrees(actual_orientation[0]);

			while (degree >= 360) {
				degree -= 360;
			}

			while (degree < 0) {
				degree += 360;
			}
		}

		if (degree != null) {
			// commonLogic.postEvent(new OrientationChangedEvent(360 - degree));
		}
	}

	private void calcActualOrientation(float[] R, float[] out) {
		// 画面の回転状態を取得する
		int dr = getDispRotation(windowManager);

		if (dr == Surface.ROTATION_0) {
			// 回転無し
			SensorManager.getOrientation(R, out);

		} else {
			// 回転あり
			float[] outR = new float[16];

			if (dr == Surface.ROTATION_90) {
				SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y,
						SensorManager.AXIS_MINUS_X, outR);

			} else if (dr == Surface.ROTATION_180) {
				float[] outR2 = new float[16];
				SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y,
						SensorManager.AXIS_MINUS_X, outR2);
				SensorManager.remapCoordinateSystem(outR2,
						SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);

			} else if (dr == Surface.ROTATION_270) {
				SensorManager.remapCoordinateSystem(R,
						SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_MINUS_X,
						outR);

			}
			SensorManager.getOrientation(outR, out);
		}
	}

	private static int getDispRotation(WindowManager windowManager) {
		Display d = windowManager.getDefaultDisplay();
		return d.getRotation();
	}
}
