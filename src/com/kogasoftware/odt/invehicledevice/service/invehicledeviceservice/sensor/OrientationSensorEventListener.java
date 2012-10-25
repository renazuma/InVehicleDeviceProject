package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.sensor;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;


public class OrientationSensorEventListener implements SensorEventListener {
	private static final Long SAVE_PERIOD_MILLIS = 500L;
	private static final String TAG = OrientationSensorEventListener.class
			.getSimpleName();
	protected final WindowManager windowManager;
	protected final ServiceUnitStatusLogLogic serviceUnitStatusLogLogic;
	protected Long lastSavedMillis = System.currentTimeMillis();

	public OrientationSensorEventListener(ServiceUnitStatusLogLogic serviceUnitStatusLogLogic, WindowManager windowManager) {
		this.serviceUnitStatusLogLogic = serviceUnitStatusLogLogic;
		this.windowManager = windowManager;
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
		Float degree = values[SensorManager.DATA_X];
		long now = System.currentTimeMillis();
		if (lastSavedMillis + SAVE_PERIOD_MILLIS > now) {
			return;
		}
		lastSavedMillis = now;

		Display display = windowManager.getDefaultDisplay();
		int displayRotation = display.getRotation();
		switch (displayRotation) {
		case Surface.ROTATION_0:
			break;
		case Surface.ROTATION_90:
			degree += 90f;
			break;
		case Surface.ROTATION_180:
			degree += 180f;
			break;
		case Surface.ROTATION_270:
			degree += 270f;
			break;
		default:
			Log.w(TAG, "unexpected display.getRotation() " + displayRotation);
			break;
		}

		serviceUnitStatusLogLogic.changeOrientation(360.0 - degree);
	}
}
