package com.kogasoftware.odt.invehicledevice.logic;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.common.base.Optional;

public class LooperThread extends Thread {
	private static final String TAG = LooperThread.class.getSimpleName();
	private static final long POLLING_PERIOD_MILLIS = 5000;
	private final Logic logic;
	private final LocationManager locationManager;
	private final SensorManager sensorManager;
	private final TelephonyManager telephonyManager;
	private final ConnectivityManager connectivityManager;
	private final SharedPreferences sharedPreferences;
	private final LocationSender locationSender = new LocationSender();
	private final ExitRequiredPreferenceChangeListener exitRequiredPreferenceChangeListener;
	private final TemperatureSensorEventListener temperatureSensorEventListener = new TemperatureSensorEventListener();
	private final OrientationSensorEventListener orientationSensorEventListener = new OrientationSensorEventListener();
	private final Object myLooperLock = new Object();
	private Optional<SignalStrengthListener> signalStrengthListener = Optional
			.absent(); // newはLooperが有効なスレッドで行う
	private Optional<Looper> myLooper = Optional.absent();

	public LooperThread(Logic logic, Context context) {
		this.logic = logic;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		sensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		exitRequiredPreferenceChangeListener = new ExitRequiredPreferenceChangeListener(
				logic);
		try {
			logic.getExecutorService().scheduleWithFixedDelay(locationSender,
					0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
		}
		for (Object object : new Object[] { locationSender,
				temperatureSensorEventListener, orientationSensorEventListener,
				exitRequiredPreferenceChangeListener }) {
			logic.getEventBus().register(object);
		}
	}

	@Override
	public void interrupt() {
		synchronized (myLooperLock) {
			try {
				if (myLooper.isPresent()) {
					myLooper.get().quit();
				}
			} finally {
				super.interrupt(); // synchronizedとinterruptとquitのタイミングを間違えるとスレッドが終了しなくなるので注意
			}
		}
	}

	private void onLooperStart() {
		sharedPreferences
				.registerOnSharedPreferenceChangeListener(exitRequiredPreferenceChangeListener);
		signalStrengthListener = Optional.of(new SignalStrengthListener(logic,
				connectivityManager));
		logic.getEventBus().register(signalStrengthListener.get());

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 0, locationSender);

		List<Sensor> temperatureSensors = sensorManager
				.getSensorList(Sensor.TYPE_TEMPERATURE);
		if (temperatureSensors.size() > 0) {
			Sensor sensor = temperatureSensors.get(0);
			sensorManager.registerListener(temperatureSensorEventListener,
					sensor, SensorManager.SENSOR_DELAY_UI);
		}

		List<Sensor> orientationSensors = sensorManager
				.getSensorList(Sensor.TYPE_TEMPERATURE);
		if (orientationSensors.size() > 0) {
			Sensor sensor = orientationSensors.get(0);
			sensorManager.registerListener(orientationSensorEventListener,
					sensor, SensorManager.SENSOR_DELAY_UI);
		}

		telephonyManager.listen(signalStrengthListener.get(),
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}

	private void onLooperStop() {
		sharedPreferences
				.unregisterOnSharedPreferenceChangeListener(exitRequiredPreferenceChangeListener);
		locationManager.removeUpdates(locationSender);
		sensorManager.unregisterListener(temperatureSensorEventListener);
		sensorManager.unregisterListener(orientationSensorEventListener);
		if (signalStrengthListener.isPresent()) {
			telephonyManager.listen(signalStrengthListener.get(),
					PhoneStateListener.LISTEN_NONE);
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
		try {
			onLooperStart();
			Looper.loop();
		} finally {
			onLooperStop();
		}
	}
}
