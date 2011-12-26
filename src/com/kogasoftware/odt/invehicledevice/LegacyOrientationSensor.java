package com.kogasoftware.odt.invehicledevice;

import java.util.List;

import org.apache.log4j.Logger;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

abstract public class LegacyOrientationSensor extends OrientationSensor {
	private static final Logger logger = Logger
			.getLogger(LegacyOrientationSensor.class);

	public LegacyOrientationSensor(Context context) {
		super(context);
	}

	@Override
	public void create() {
		// センサー管理クラスの取得

		// センサー管理クラスから方位センサーのリストを取得する。
		List<Sensor> sensors = getSensorManager().getSensorList(
				Sensor.TYPE_ORIENTATION);
		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			// センサーにリスナ登録する
			getSensorManager().registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_UI);
			logger.debug("registered: Sensor.TYPE_ORIENTATION");
		}
	}

	@Override
	public void destroy() {
		getSensorManager().unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			// 方位から回転角を設定
			float[] values = event.values;

			// 再描画
			float r = values[SensorManager.DATA_X];
			onOrientationChanged(Math.toRadians(360.0f - r - 90.0f));
		}
	}
}
