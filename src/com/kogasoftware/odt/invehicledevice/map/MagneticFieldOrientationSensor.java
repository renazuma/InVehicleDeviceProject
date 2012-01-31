package com.kogasoftware.odt.invehicledevice.map;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.LogTag;

abstract public class MagneticFieldOrientationSensor extends OrientationSensor {
	private static final String T = LogTag
			.get(MagneticFieldOrientationSensor.class);

	public MagneticFieldOrientationSensor(Context context) {
		super(context);
	}

	@Override
	public void create() {
		// センサー管理クラスの取得

		// センサー管理クラスから地磁気センサーのリストを取得する。
		List<Sensor> sensors = getSensorManager().getSensorList(
				Sensor.TYPE_MAGNETIC_FIELD);
		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			// センサーにリスナ登録する
			getSensorManager().registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_UI);
			Log.d(T, "registered: Sensor.TYPE_MAGNETIC_FIELD");
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
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			// 方位から回転角を設定
			float[] values = event.values;

			// 再描画
			float r = values[SensorManager.DATA_X];

			onOrientationChanged(Math.toRadians(360.0 - r));
		}
	}
}
