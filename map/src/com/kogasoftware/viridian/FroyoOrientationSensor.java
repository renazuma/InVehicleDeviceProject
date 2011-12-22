package com.kogasoftware.viridian;

import java.util.List;

import org.apache.log4j.Logger;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public abstract class FroyoOrientationSensor extends OrientationSensor {
	private static final Logger logger = Logger
			.getLogger(FroyoOrientationSensor.class);
	private static final Integer SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI;
	private static final Integer MATRIX_SIZE = 16;

	private Boolean isMagSensor = false;
	private Boolean isAccSensor = false;

	// 回転行列
	float[] inR = new float[MATRIX_SIZE];

	float[] outR = new float[MATRIX_SIZE];
	float[] I = new float[MATRIX_SIZE];
	// センサーの値
	float[] orientationValues = new float[3];
	float[] magneticValues = new float[3];

	float[] accelerometerValues = new float[3];

	public FroyoOrientationSensor(Context context) {
		super(context);
	}

	@Override
	public void create() {
		// センサの取得
		List<Sensor> sensors = getSensorManager()
				.getSensorList(Sensor.TYPE_ALL);

		// センサマネージャへリスナーを登録
		for (Sensor sensor : sensors) {

			if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				getSensorManager().registerListener(this, sensor, SENSOR_DELAY);
				isMagSensor = true;
			}

			if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				getSensorManager().registerListener(this, sensor, SENSOR_DELAY);
				isAccSensor = true;
			}
		}
	}

	@Override
	public void destroy() {
		// センサーマネージャのリスナ登録破棄
		if (isMagSensor || isAccSensor) {
			getSensorManager().unregisterListener(this);
			isMagSensor = false;
			isAccSensor = false;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
			logger.error("SensorManager.SENSOR_STATUS_UNRELIABLE");
			return;
		}
		switch (event.sensor.getType()) {
		case Sensor.TYPE_MAGNETIC_FIELD:
			magneticValues = event.values.clone();
			break;
		case Sensor.TYPE_ACCELEROMETER:
			accelerometerValues = event.values.clone();
			break;
		}

		if (magneticValues != null && accelerometerValues != null) {

			SensorManager.getRotationMatrix(inR, I, accelerometerValues,
					magneticValues);

			// Activityの表示が縦固定の場合。横向きになる場合、修正が必要です
			SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X,
					SensorManager.AXIS_Z, outR);
			SensorManager.getOrientation(outR, orientationValues);

			logger.trace(String.valueOf(Math.toDegrees(orientationValues[0]))
					+ ", "
					+ // Z軸方向,azmuth
					String.valueOf(Math.toDegrees(orientationValues[1])) + ", "
					+ // X軸方向,pitch
					String.valueOf(Math.toDegrees(orientationValues[2]))); // Y軸方向,roll
			onOrientationChanged((double) orientationValues[0]);
		}
	}
}
