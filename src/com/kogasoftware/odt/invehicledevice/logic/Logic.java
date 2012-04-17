package com.kogasoftware.odt.invehicledevice.logic;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.event.AddUnexpectedReservationEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.UiEventBus;
import com.kogasoftware.odt.invehicledevice.logic.Status.Phase;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.ReaderAndWriter;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.modalview.ConfigModalView;
import com.kogasoftware.odt.invehicledevice.modalview.MemoModalView;
import com.kogasoftware.odt.invehicledevice.modalview.NotificationModalView;
import com.kogasoftware.odt.invehicledevice.modalview.PauseModalView;
import com.kogasoftware.odt.invehicledevice.modalview.ReturnPathModalView;
import com.kogasoftware.odt.invehicledevice.modalview.ScheduleChangedModalView;
import com.kogasoftware.odt.invehicledevice.modalview.ScheduleModalView;
import com.kogasoftware.odt.invehicledevice.modalview.StartCheckModalView;
import com.kogasoftware.odt.invehicledevice.modalview.StopCheckModalView;
import com.kogasoftware.odt.invehicledevice.modalview.StopModalView;
import com.kogasoftware.odt.invehicledevice.phaseview.PlatformPhaseView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 車載機の内部ロジック
 */
public class Logic {
	private static Optional<Date> defaultDate = Optional.<Date> absent();
	private static final Object DEFAULT_DATE_LOCK = new Object();
	private static final Integer NUM_THREADS = 3;
	private static final Integer POLLING_PERIOD_MILLIS = 30 * 1000;
	private static final String TAG = Logic.class.getSimpleName();

	private static final AtomicBoolean willClearStatusFile = new AtomicBoolean(
			true);

	public static void clearStatusFile() {
		willClearStatusFile.set(true);
	}

	public static Date getDate() {
		if (!BuildConfig.DEBUG) {
			return new Date();
		}
		synchronized (DEFAULT_DATE_LOCK) {
			if (defaultDate.isPresent()) {
				return defaultDate.get();
			}
		}
		return new Date();
	}

	public static void setDate(Date date) {
		if (BuildConfig.DEBUG) {
			synchronized (DEFAULT_DATE_LOCK) {
				defaultDate = Optional.of(date);
			}
		}
	}

	private final DataSource dataSource;
	private final UiEventBus eventBus = new UiEventBus();
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final StatusAccess statusAccess;
	private final LocationSender locationSender = new LocationSender();
	private final AtomicBoolean shutdowning = new AtomicBoolean(false);

	private Optional<LocationManager> locationManager = Optional
			.<LocationManager> absent();
	private Optional<SensorManager> sensorManager = Optional
			.<SensorManager> absent();
	private Thread voiceThread = new EmptyThread();
	private TemperatureSensorEventListener temperatureSensorEventListener = new TemperatureSensorEventListener();
	private VehicleNotificationReceiver vehicleNotificationReceiver = new VehicleNotificationReceiver();
	private VehicleNotificationSender vehicleNotificationSender = new VehicleNotificationSender();
	private ScheduleChangedReceiver scheduleChangedReceiver = new ScheduleChangedReceiver();
	private OperationScheduleSender operationScheduleSender = new OperationScheduleSender();

	private final Object myLooperLock = new Object();
	private Optional<Looper> myLooper = Optional.<Looper> absent();

	public Logic() {
		this.statusAccess = new StatusAccess();
		this.dataSource = DataSourceFactory.newInstance("http://127.0.0.1", "");
	}

	public Logic(Activity activity) throws InterruptedException {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(activity);

		locationManager = Optional.of((LocationManager) activity
				.getSystemService(Context.LOCATION_SERVICE));

		sensorManager = Optional.of((SensorManager) activity
				.getSystemService(Context.SENSOR_SERVICE));

		dataSource = DataSourceFactory.newInstance(
				preferences.getString("url", "http://127.0.0.1"),
				preferences.getString("token", ""));
		try {
			Thread.sleep(0); // interruption point
			this.statusAccess = new StatusAccess(activity,
					willClearStatusFile.getAndSet(false));
			Future<?> receiveOperationSchedule = null;
			try {
				receiveOperationSchedule = executorService
						.submit(new OperationScheduleReceiver(this));
				executorService.scheduleWithFixedDelay(
						vehicleNotificationReceiver, 0, POLLING_PERIOD_MILLIS,
						TimeUnit.MILLISECONDS);
				executorService.scheduleWithFixedDelay(
						vehicleNotificationSender, 0, POLLING_PERIOD_MILLIS,
						TimeUnit.MILLISECONDS);
				executorService.scheduleWithFixedDelay(scheduleChangedReceiver,
						0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
				executorService.scheduleWithFixedDelay(locationSender, 0,
						POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
				executorService.scheduleWithFixedDelay(operationScheduleSender,
						0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
			} catch (RejectedExecutionException e) {
				Log.e(TAG, "Task Rejected", e);
			}

			eventBus.register(activity);
			Thread.sleep(0); // interruption point
			voiceThread = new VoiceThread(activity);
			voiceThread.start();
			eventBus.register(voiceThread);
			Thread.sleep(0); // interruption point
			for (Integer resourceId : new Integer[] { R.id.config_modal,
					R.id.start_check_modal, R.id.schedule_modal,
					R.id.memo_modal, R.id.pause_modal, R.id.return_path_modal,
					R.id.stop_check_modal, R.id.stop_modal,
					R.id.notification_modal, R.id.schedule_changed_modal,
					R.id.navigation_modal, R.id.login_modal,
					R.id.phase_text_view, R.id.driving_layout,
					R.id.waiting_layout, R.id.finish_layout }) {
				View view = activity.findViewById(resourceId);
				eventBus.register(view);
				Thread.sleep(0); // interruption point
			}

			for (LogicUser logicUser : new LogicUser[] { locationSender,
					temperatureSensorEventListener,
					vehicleNotificationReceiver, vehicleNotificationSender,
					scheduleChangedReceiver, operationScheduleSender }) {
				eventBus.register(logicUser);
			}

			if (receiveOperationSchedule != null && getPhase() != Phase.FINISH
					&& getOperationSchedules().isEmpty()) {
				try {
					receiveOperationSchedule.get(); // wait for initialize
				} catch (ExecutionException e) {
				}
			}
			new Thread() {
				@Override
				public void run() {
					runLooperThread();
				}
			}.start();
		} catch (InterruptedException e) {
			shutdown();
			throw e;
		}
	}

	public void addMissedReservations(List<Reservation> reservations) {
		final List<Reservation> finalReservations = reservations;
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.missedReservations.addAll(finalReservations);
			}
		});
	}

	public void addUnexpectedReservation(Integer arrivalOperationScheduleId) {
		List<OperationSchedule> operationSchedules = getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			Log.w(TAG, "operationSchedules.isEmpty()", new Exception());
			return;
		}
		OperationSchedule operationSchedule = operationSchedules.get(0);
		final Reservation reservation = new Reservation();

		Integer id = statusAccess.readAndWrite(new ReaderAndWriter<Integer>() {
			@Override
			public Integer readAndWrite(Status status) {
				return status.unexpectedReservationSequence++;
			}
		});
		reservation.setId(id); // TODO
		// 未予約乗車の予約情報はどうするか
		reservation.setDepartureScheduleId(operationSchedule.getId());
		reservation.setArrivalScheduleId(arrivalOperationScheduleId);

		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.unexpectedReservations.add(reservation);
			}
		});
		eventBus.post(new AddUnexpectedReservationEvent(reservation));
	}

	public void cancelPause() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.paused = false;
			}
		});
	}

	public void enterDrivePhase() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				if (status.phase == Status.Phase.PLATFORM) {
					status.currentOperationScheduleIndex++;
				}
				status.phase = Status.Phase.DRIVE;
				if (status.operationSchedules.size() <= status.currentOperationScheduleIndex) {
					// TODO warning
				} else {
					OperationSchedule operationSchedule = status.operationSchedules
							.get(status.currentOperationScheduleIndex);
					status.departureOperationSchedule.add(operationSchedule); // TODO
																				// コピーしなくて良い？
				}
			}
		});
		eventBus.post(new EnterDrivePhaseEvent());
	}

	public void enterFinishPhase() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.phase = Status.Phase.FINISH;
			}
		});
		eventBus.post(new EnterFinishPhaseEvent());
	}

	public void enterPlatformPhase() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.phase = Status.Phase.PLATFORM;
				if (status.operationSchedules.size() <= status.currentOperationScheduleIndex) {
					// TODO warning
				} else {
					OperationSchedule operationSchedule = status.operationSchedules
							.get(status.currentOperationScheduleIndex);
					status.arrivalOperationSchedule.add(operationSchedule); // TODO
																			// コピーしなくて良い？
				}
			}
		});
		eventBus.post(new EnterPlatformPhaseEvent());
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public UiEventBus getEventBus() {
		return eventBus;
	}

	public List<Reservation> getMissedReservations() {
		return statusAccess.read(new Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(Status status) {
				return new LinkedList<Reservation>(status.missedReservations);
			}
		});
	}

	public void getOnReservation(final List<Reservation> reservations) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.ridingReservations.addAll(reservations);
			}
		});
	}

	public List<OperationSchedule> getOperationSchedules() {
		return statusAccess.read(new Reader<List<OperationSchedule>>() {
			@Override
			public List<OperationSchedule> read(Status status) {
				return new LinkedList<OperationSchedule>(
						status.operationSchedules);
			}
		});
	}

	public void getOutReservation(final List<Reservation> reservations) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.ridingReservations.removeAll(reservations);
			}
		});
	}

	public Phase getPhase() {
		return statusAccess.read(new Reader<Phase>() {
			@Override
			public Phase read(Status status) {
				return status.phase;
			}
		});
	}

	public List<OperationSchedule> getRemainingOperationSchedules() {
		return statusAccess.read(new Reader<List<OperationSchedule>>() {
			@Override
			public List<OperationSchedule> read(Status status) {
				try {
					List<OperationSchedule> remainings = new LinkedList<OperationSchedule>(
							status.operationSchedules);
					return remainings.subList(
							status.currentOperationScheduleIndex,
							remainings.size());
				} catch (ArrayIndexOutOfBoundsException e) {
				} catch (IllegalArgumentException e) {
				}
				return new LinkedList<OperationSchedule>();
			}
		});
	}

	public List<Reservation> getRidingReservations() {
		return statusAccess.read(new Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(Status status) {
				return new LinkedList<Reservation>(status.ridingReservations);
			}
		});
	}

	public StatusAccess getStatusAccess() {
		return statusAccess;
	}

	public String getToken() {
		return statusAccess.read(new Reader<String>() {
			@Override
			public String read(Status status) {
				return status.token;
			}
		});
	}

	public List<Reservation> getUnexpectedReservations() {
		return statusAccess.read(new Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(Status status) {
				return new LinkedList<Reservation>(
						status.unexpectedReservations);
			}
		});
	}

	public Boolean isInitialized() {
		return statusAccess.read(new Reader<Boolean>() {
			@Override
			public Boolean read(Status status) {
				return status.initialized.get();
			}
		});
	}

	private void onLooperStart() {
		try {
			locationManager.get().requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 2000, 0, locationSender);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		List<Sensor> sensors = sensorManager.get().getSensorList(
				Sensor.TYPE_TEMPERATURE);
		if (sensors.size() > 0) {
			Sensor sensor = sensors.get(0);
			try {
				sensorManager.get().registerListener(
						temperatureSensorEventListener, sensor,
						SensorManager.SENSOR_DELAY_UI);
			} catch (RuntimeException e) {
				e.printStackTrace();
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
		}
	}

	public void pause() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.paused = true;
			}
		});
		eventBus.post(new PauseModalView.ShowEvent());
	}

	public void restoreStatus() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				if (status.phase == Status.Phase.PLATFORM) {
					enterPlatformPhase();
				} else {
					enterDrivePhase();
				}
				if (status.paused) {
					pause();
				}
				if (status.stopped) {
					stop();
				}
			}
		});
	}

	private void runLooperThread() {
		Looper.prepare();
		synchronized (myLooperLock) { // TODO: shutdowningとの同期の順番
			myLooper = Optional.of(Looper.myLooper());
		}
		onLooperStart();
		if (!shutdowning.get()) {
			Looper.loop();
		}
		onLooperStop();
	}

	public void setLocation(final Location location) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.location = Optional.of(location);
			}
		});
	}

	public void showConfigModalView() {
		eventBus.post(new ConfigModalView.ShowEvent());
	}

	public void showMemoModalView(Reservation reservation) {
		eventBus.post(new MemoModalView.ShowEvent(reservation));
	}

	public void showNotificationModalView(
			List<VehicleNotification> vehicleNotifications) {
		eventBus.post(new NotificationModalView.ShowEvent(vehicleNotifications));
	}

	public void showReturnPathModalView(Reservation reservation) {
		eventBus.post(new ReturnPathModalView.ShowEvent(reservation));
	}

	public void showScheduleChangedModalView(
			List<VehicleNotification> vehicleNotification) {
		eventBus.post(new ScheduleChangedModalView.ShowEvent(
				vehicleNotification));
	}

	public void showScheduleModalView() {
		eventBus.post(new ScheduleModalView.ShowEvent());
	}

	public void showStartCheckModalView() {
		eventBus.post(new PlatformPhaseView.StartCheckEvent());
	}

	public void showStartCheckModalView(ReservationArrayAdapter adapter) {
		eventBus.post(new StartCheckModalView.ShowEvent(adapter));
	}

	public void showStopCheckModalView() {
		eventBus.post(new StopCheckModalView.ShowEvent());
	}

	public void shutdown() {
		eventBus.dispose();
		executorService.shutdownNow();
		voiceThread.interrupt();

		shutdowning.set(true); // TODO 順番
		synchronized (myLooperLock) { // TODO 順番
			if (myLooper.isPresent()) {
				myLooper.get().quit();
			}
		}
	}

	public void speak(String message) {
		eventBus.post(new SpeakEvent(message));
	}

	public void stop() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.stopped = true;
			}
		});
		eventBus.post(new StopModalView.ShowEvent());
	}
}
