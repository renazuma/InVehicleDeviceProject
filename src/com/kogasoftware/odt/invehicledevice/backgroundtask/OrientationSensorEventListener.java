package com.kogasoftware.odt.invehicledevice.backgroundtask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;

public class OrientationSensorEventListener implements SensorEventListener {
	private static final Long SAVE_PERIOD_MILLIS = 10 * 1000L;
	private Long lastSavedMillis = System.currentTimeMillis();

	private final CommonLogic commonLogic;

	public OrientationSensorEventListener(CommonLogic commonLogic) {
		this.commonLogic = commonLogic;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
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

		commonLogic.getStatusAccess().write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog
						.setOrientation((int) (360 - degree));
			}
		});
		// Log.i(TAG, "orientation changed=" + degree);
	}
}
