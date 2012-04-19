package com.kogasoftware.odt.invehicledevice.logic;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;

import com.google.common.base.Optional;

public class LooperThread extends Thread {
	private static final String TAG = LooperThread.class.getSimpleName();
	private static final long POLLING_PERIOD_MILLIS = 5000;
	private final Object myLooperLock = new Object();
	private Optional<Looper> myLooper = Optional.absent();
	private Optional<LocationManager> locationManager = Optional.absent();
	private Optional<SensorManager> sensorManager = Optional.absent();
	private final LocationSender locationSender = new LocationSender();
	private final TemperatureSensorEventListener temperatureSensorEventListener = new TemperatureSensorEventListener();
	private final OrientationSensorEventListener orientationSensorEventListener = new OrientationSensorEventListener();

	public LooperThread(Logic logic, Context context) {
		locationManager = Optional.of((LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE));
		sensorManager = Optional.of((SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE));
		try {
			logic.getExecutorService().scheduleWithFixedDelay(locationSender,
					0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
		}
		logic.getEventBus().register(locationSender);
		logic.getEventBus().register(temperatureSensorEventListener);
		logic.getEventBus().register(orientationSensorEventListener);
	}

	@Override
	public void interrupt() {
		synchronized (myLooperLock) {
			super.interrupt(); // synchronizedとinterruptとquitのタイミングを間違えるとスレッドが終了しなくなるので注意
			if (myLooper.isPresent()) {
				myLooper.get().quit();
			}
		}
	}

	private void onLooperStart() {
		if (locationManager.isPresent()) {
			locationManager.get().requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 2000, 0, locationSender);
		}

		if (sensorManager.isPresent()) {
			List<Sensor> temperatureSensors = sensorManager.get()
					.getSensorList(Sensor.TYPE_TEMPERATURE);
			if (temperatureSensors.size() > 0) {
				Sensor sensor = temperatureSensors.get(0);
				sensorManager.get().registerListener(
						temperatureSensorEventListener, sensor,
						SensorManager.SENSOR_DELAY_UI);
			}

			List<Sensor> orientationSensors = sensorManager.get()
					.getSensorList(Sensor.TYPE_TEMPERATURE);
			if (orientationSensors.size() > 0) {
				Sensor sensor = orientationSensors.get(0);
				sensorManager.get().registerListener(
						orientationSensorEventListener, sensor,
						SensorManager.SENSOR_DELAY_UI);
			}
		}
	}

	private void onLooperStop() {
		if (locationManager.isPresent()) {
			locationManager.get().removeUpdates(locationSender);
		}
		if (sensorManager.isPresent()) {
			sensorManager.get().unregisterListener(
					temperatureSensorEventListener);
			sensorManager.get().unregisterListener(
					orientationSensorEventListener);
		}
	}

	@Override
	public void run() {
		Looper.prepare();
		synchronized (myLooperLock) {
			// synchronizedとisInterruptedとmyLooper代入のタイミングを間違えるとスレッドが終了しなくなるので注意
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			myLooper = Optional.of(Looper.myLooper());
		}
		onLooperStart();
		Looper.loop();
		onLooperStop();
	}
}
