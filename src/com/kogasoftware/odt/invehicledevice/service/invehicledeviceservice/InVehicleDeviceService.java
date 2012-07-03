package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Writer;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundthread.BackgroundTaskThread;
import com.kogasoftware.odt.invehicledevice.ui.activity.StartupActivity;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceService extends Service {
	public class LocalBinder extends Binder {
		public InVehicleDeviceService getService() {
			return InVehicleDeviceService.this;
		}
	}

	public static interface OnAlertUpdatedOperationScheduleListener {
		void onAlertUpdatedOperationSchedule();
	}

	public static interface OnAlertVehicleNotificationReceiveListener {
		void onAlertVehicleNotificationReceive();
	}

	public static interface OnChangeLocationListener {
		void onChangeLocation(Location location, Optional<GpsStatus> gpsStatus);
	}

	public static interface OnChangeOrientationListener {
		void onChangeOrientation(Double orientationDegree);
	}

	public static interface OnChangeSignalStrengthListener {
		void onChangeSignalStrength(Integer signalStrengthPercentage);
	}

	public static interface OnChangeTemperatureListener {
		void onChangeTemperature(Double celciusTemperature);
	}

	public static interface OnEnterPhaseListener {
		void onEnterDrivePhase();

		void onEnterFinishPhase();

		void onEnterPlatformPhase();
	}

	public static interface OnExitListener {
		void onExit();
	}

	public static interface OnInitializeListener {
		void onInitialize(InVehicleDeviceService localService);
	}

	public static interface OnMergeUpdatedOperationScheduleListener {
		void onMergeUpdatedOperationSchedule(
				List<VehicleNotification> triggerVehicleNotifications);
	}

	public interface OnPauseActivityListener {
		void onPauseActivity();
	}

	public static interface OnReceiveUpdatedOperationScheduleListener {
		void onReceiveUpdatedOperationSchedule(
				List<OperationSchedule> operationSchedules,
				List<VehicleNotification> triggerVehicleNotifications);
	}

	public static interface OnReceiveVehicleNotificationListener {
		void onReceiveVehicleNotification(
				List<VehicleNotification> vehicleNotifications);
	}

	public static interface OnReplyUpdatedOperationScheduleVehicleNotificationsListener {
		void onReplyUpdatedOperationScheduleVehicleNotifications(
				List<VehicleNotification> vehicleNotifications);
	}

	public static interface OnReplyVehicleNotificationListener {
		void onReplyVehicleNotification(VehicleNotification vehicleNotification);
	}

	public interface OnResumeActivityListener {
		void onResumeActivity();
	}

	public static interface OnStartNewOperationListener {
		void onStartNewOperation();
	}

	public static interface OnStartReceiveUpdatedOperationScheduleListener {
		void onStartReceiveUpdatedOperationSchedule();
	}

	public static enum PayTiming {
		GET_ON, GET_OFF,
	}

	public static final Integer NEW_SCHEDULE_DOWNLOAD_HOUR = 0;
	public static final Integer NEW_SCHEDULE_DOWNLOAD_MINUTE = 5;

	protected static final Object DEFAULT_DATE_LOCK = new Object();

	protected static Optional<Long> mockDateOffset = Optional.absent();

	protected static final String TAG = InVehicleDeviceService.class
			.getSimpleName();

	public static Handler getActivityHandler(Activity activity)
			throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Handler> handler = new AtomicReference<Handler>();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				handler.set(new Handler());
				latch.countDown();
			}
		});
		latch.await();
		Preconditions.checkNotNull(handler.get());
		return handler.get();

	}

	public static Date getDate() {
		if (!BuildConfig.DEBUG) {
			return new Date();
		}
		synchronized (DEFAULT_DATE_LOCK) {
			if (mockDateOffset.isPresent()) {
				return new Date(new Date().getTime() + mockDateOffset.get());
			}
			return new Date();
		}
	}

	public static void setDate(Date date) {
		if (!BuildConfig.DEBUG) {
			return;
		}
		synchronized (DEFAULT_DATE_LOCK) {
			Date now = new Date();
			mockDateOffset = Optional.of(date.getTime() - now.getTime());
		}
	}

	protected final OperationScheduleLogic operationScheduleLogic;
	protected final VehicleNotificationLogic vehicleNotificationLogic;
	protected final ServiceUnitStatusLogLogic serviceUnitStatusLogLogic;
	protected final IBinder binder = new LocalBinder();
	protected final Handler handler = new Handler(Looper.getMainLooper());
	protected final Set<OnInitializeListener> onInitializeListeners = new CopyOnWriteArraySet<OnInitializeListener>();
	protected final Set<OnEnterPhaseListener> onEnterPhaseListeners = new CopyOnWriteArraySet<OnEnterPhaseListener>();
	protected final Set<OnAlertUpdatedOperationScheduleListener> onAlertUpdatedOperationScheduleListeners = new CopyOnWriteArraySet<OnAlertUpdatedOperationScheduleListener>();
	protected final Set<OnAlertVehicleNotificationReceiveListener> onAlertVehicleNotificationReceiveListeners = new CopyOnWriteArraySet<OnAlertVehicleNotificationReceiveListener>();
	protected final Set<OnChangeLocationListener> onChangeLocationListeners = new CopyOnWriteArraySet<OnChangeLocationListener>();
	protected final Set<OnChangeOrientationListener> onChangeOrientationListeners = new CopyOnWriteArraySet<OnChangeOrientationListener>();
	protected final Set<OnChangeSignalStrengthListener> onChangeSignalStrengthListeners = new CopyOnWriteArraySet<OnChangeSignalStrengthListener>();
	protected final Set<OnChangeTemperatureListener> onChangeTemperatureListeners = new CopyOnWriteArraySet<OnChangeTemperatureListener>();
	protected final Set<OnExitListener> onExitListeners = new CopyOnWriteArraySet<OnExitListener>();
	protected final Set<OnMergeUpdatedOperationScheduleListener> onMergeUpdatedOperationScheduleListeners = new CopyOnWriteArraySet<OnMergeUpdatedOperationScheduleListener>();
	protected final Set<OnReceiveUpdatedOperationScheduleListener> onReceiveUpdatedOperationScheduleListeners = new CopyOnWriteArraySet<OnReceiveUpdatedOperationScheduleListener>();
	protected final Set<OnReceiveVehicleNotificationListener> onReceiveVehicleNotificationListeners = new CopyOnWriteArraySet<OnReceiveVehicleNotificationListener>();
	protected final Set<OnReplyUpdatedOperationScheduleVehicleNotificationsListener> onReplyUpdatedOperationScheduleVehicleNotificationsListeners = new CopyOnWriteArraySet<OnReplyUpdatedOperationScheduleVehicleNotificationsListener>();
	protected final Set<OnReplyVehicleNotificationListener> onReplyVehicleNotificationListeners = new CopyOnWriteArraySet<OnReplyVehicleNotificationListener>();
	protected final Set<OnStartNewOperationListener> onStartNewOperationListeners = new CopyOnWriteArraySet<OnStartNewOperationListener>();
	protected final Set<OnStartReceiveUpdatedOperationScheduleListener> onStartReceiveUpdatedOperationScheduleListeners = new CopyOnWriteArraySet<OnStartReceiveUpdatedOperationScheduleListener>();
	protected final Set<OnPauseActivityListener> onPauseActivityListeners = new CopyOnWriteArraySet<OnPauseActivityListener>();
	protected final Set<OnResumeActivityListener> onResumeActivityListeners = new CopyOnWriteArraySet<OnResumeActivityListener>();
	protected final VoiceServiceConnector voiceServiceConnector;
	protected volatile Thread backgroundThread = new EmptyThread();
	protected volatile DataSource dataSource = new EmptyDataSource();
	protected volatile LocalDataSource localDataSource = new LocalDataSource();

	public InVehicleDeviceService() {
		super();

		operationScheduleLogic = new OperationScheduleLogic(this);
		vehicleNotificationLogic = new VehicleNotificationLogic(this);
		serviceUnitStatusLogLogic = new ServiceUnitStatusLogLogic(this);
		voiceServiceConnector = new VoiceServiceConnector(this);
	}

	public void addOnAlertUpdatedOperationScheduleListener(
			OnAlertUpdatedOperationScheduleListener listener) {
		onAlertUpdatedOperationScheduleListeners.add(listener);
	}

	public void addOnAlertVehicleNotificationReceiveListener(
			OnAlertVehicleNotificationReceiveListener listener) {
		onAlertVehicleNotificationReceiveListeners.add(listener);
	}

	public void addOnChangeLocationListener(OnChangeLocationListener listener) {
		onChangeLocationListeners.add(listener);
	}

	public void addOnChangeOrientationListener(
			OnChangeOrientationListener listener) {
		onChangeOrientationListeners.add(listener);
	}

	public void addOnChangeSignalStrengthListener(
			OnChangeSignalStrengthListener listener) {
		onChangeSignalStrengthListeners.add(listener);
	}

	public void addOnChangeTemperatureListener(
			OnChangeTemperatureListener listener) {
		onChangeTemperatureListeners.add(listener);
	}

	public void addOnEnterPhaseListener(OnEnterPhaseListener listener) {
		onEnterPhaseListeners.add(listener);
	}

	public void addOnExitListener(OnExitListener listener) {
		onExitListeners.add(listener);
	}

	public void addOnInitializeListener(OnInitializeListener listener) {
		onInitializeListeners.add(listener);
	}

	public void addOnMergeUpdatedOperationScheduleListener(
			OnMergeUpdatedOperationScheduleListener listener) {
		onMergeUpdatedOperationScheduleListeners.add(listener);
	}

	public void addOnPauseActivityListener(OnPauseActivityListener listener) {
		onPauseActivityListeners.add(listener);
	}

	public void addOnReceiveUpdatedOperationScheduleListener(
			OnReceiveUpdatedOperationScheduleListener listener) {
		onReceiveUpdatedOperationScheduleListeners.add(listener);
	}

	public void addOnReceiveVehicleNotificationListener(
			OnReceiveVehicleNotificationListener listener) {
		onReceiveVehicleNotificationListeners.add(listener);
	}

	public void addOnReplyUpdatedOperationScheduleVehicleNotificationsListener(
			OnReplyUpdatedOperationScheduleVehicleNotificationsListener listener) {
		onReplyUpdatedOperationScheduleVehicleNotificationsListeners
				.add(listener);
	};

	public void addOnReplyVehicleNotificationListener(
			OnReplyVehicleNotificationListener listener) {
		onReplyVehicleNotificationListeners.add(listener);
	};

	public void addOnResumeActivityListener(OnResumeActivityListener listener) {
		onResumeActivityListeners.add(listener);
	}

	public void addOnStartNewOperationListener(
			OnStartNewOperationListener listener) {
		onStartNewOperationListeners.add(listener);
	}

	public void addOnStartReceiveUpdatedOperationScheduleListener(
			OnStartReceiveUpdatedOperationScheduleListener listener) {
		onStartReceiveUpdatedOperationScheduleListeners.add(listener);
	}

	public void alertUpdatedOperationSchedule() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnAlertUpdatedOperationScheduleListener listener : new ArrayList<OnAlertUpdatedOperationScheduleListener>(
						onAlertUpdatedOperationScheduleListeners)) {
					listener.onAlertUpdatedOperationSchedule();
				}
			}
		});
	}

	public void alertVehicleNotificationReceive() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnAlertVehicleNotificationReceiveListener listener : new ArrayList<OnAlertVehicleNotificationReceiveListener>(
						onAlertVehicleNotificationReceiveListeners)) {
					listener.onAlertVehicleNotificationReceive();
				}
			}
		});
	}

	public void changeLocation(final Location location, final Optional<GpsStatus> gpsStatus) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				serviceUnitStatusLogLogic.changeLocation(location);
				for (OnChangeLocationListener listener : new ArrayList<OnChangeLocationListener>(
						onChangeLocationListeners)) {
					listener.onChangeLocation(location, gpsStatus);
				}
			}
		});
	}

	public void changeOrientation(final Double degree) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				serviceUnitStatusLogLogic.changeOrientation(degree);
				for (OnChangeOrientationListener listener : new ArrayList<OnChangeOrientationListener>(
						onChangeOrientationListeners)) {
					listener.onChangeOrientation(degree);
				}
			}
		});
	}

	public void changeSignalStrength(final Integer signalStrengthPercentage) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnChangeSignalStrengthListener listener : new ArrayList<OnChangeSignalStrengthListener>(
						onChangeSignalStrengthListeners)) {
					listener.onChangeSignalStrength(signalStrengthPercentage);
				}
			}
		});
	}

	public void changeTemperature(final Double celciusTemperature) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				serviceUnitStatusLogLogic.changeTemperature(celciusTemperature);
				for (OnChangeTemperatureListener listener : new ArrayList<OnChangeTemperatureListener>(
						onChangeTemperatureListeners)) {
					listener.onChangeTemperature(celciusTemperature);
				}
			}
		});
	}

	public void enterDrivePhase() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				operationScheduleLogic.enterDrivePhase();
				for (OnEnterPhaseListener listener : new ArrayList<OnEnterPhaseListener>(
						onEnterPhaseListeners)) {
					listener.onEnterDrivePhase();
				}
			}
		});
	}

	public void enterFinishPhase() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				operationScheduleLogic.enterFinishPhase();
				for (OnEnterPhaseListener listener : new ArrayList<OnEnterPhaseListener>(
						onEnterPhaseListeners)) {
					listener.onEnterFinishPhase();
				}
			}
		});
	}

	public void enterPlatformPhase() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				operationScheduleLogic.enterPlatformPhase();
				for (OnEnterPhaseListener listener : new ArrayList<OnEnterPhaseListener>(
						onEnterPhaseListeners)) {
					listener.onEnterPlatformPhase();
				}
			}
		});
	}

	public void exit() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnExitListener listener : new ArrayList<OnExitListener>(
						onExitListeners)) {
					listener.onExit();
				}
			}
		});
	}

	public Optional<OperationSchedule> getCurrentOperationSchedule() {
		return localDataSource
				.withReadLock(new Reader<Optional<OperationSchedule>>() {
					@Override
					public Optional<OperationSchedule> read(LocalData status) {
						if (status.remainingOperationSchedules.isEmpty()) {
							return Optional.absent();
						} else {
							return Optional
									.of(status.remainingOperationSchedules
											.get(0));
						}
					}
				});
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public List<OperationSchedule> getFinishedOperationSchedules() {
		return localDataSource
				.withReadLock(new Reader<List<OperationSchedule>>() {
					@Override
					public List<OperationSchedule> read(LocalData status) {
						return new LinkedList<OperationSchedule>(
								status.finishedOperationSchedules);
					}
				});
	}

	public LocalDataSource getLocalDataSource() {
		return localDataSource;
	}

	public EnumSet<PayTiming> getPayTiming() {
		return EnumSet.of(PayTiming.GET_ON);
	}

	public Phase getPhase() {
		return localDataSource.withReadLock(new Reader<Phase>() {
			@Override
			public Phase read(LocalData status) {
				return status.phase;
			}
		});
	}

	public List<VehicleNotification> getReceivingOperationScheduleChangedVehicleNotifications() {
		return localDataSource
				.withReadLock(new Reader<List<VehicleNotification>>() {
					@Override
					public List<VehicleNotification> read(LocalData status) {
						return new LinkedList<VehicleNotification>(
								status.receivingOperationScheduleChangedVehicleNotifications);
					}
				});
	}

	public List<OperationSchedule> getRemainingOperationSchedules() {
		return localDataSource
				.withReadLock(new Reader<List<OperationSchedule>>() {
					@Override
					public List<OperationSchedule> read(LocalData status) {
						return new LinkedList<OperationSchedule>(
								status.remainingOperationSchedules);
					}
				});
	}

	public List<Reservation> getReservations() {
		return localDataSource.withReadLock(new Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(LocalData status) {
				return new LinkedList<Reservation>(status.reservations);
			}
		});
	}

	public ServiceUnitStatusLog getServiceUnitStatusLog() {
		return localDataSource.withReadLock(new Reader<ServiceUnitStatusLog>() {
			@Override
			public ServiceUnitStatusLog read(LocalData status) {
				return status.serviceUnitStatusLog;
			}
		});
	}

	public String getToken() {
		return localDataSource.withReadLock(new Reader<String>() {
			@Override
			public String read(LocalData status) {
				return status.token;
			}
		});
	}

	public List<VehicleNotification> getVehicleNotifications() {
		return localDataSource
				.withReadLock(new Reader<List<VehicleNotification>>() {
					@Override
					public List<VehicleNotification> read(LocalData status) {
						return new LinkedList<VehicleNotification>(
								status.vehicleNotifications);
					}
				});
	}

	public Boolean isOperationScheduleInitialized() {
		return localDataSource.withReadLock(new Reader<Boolean>() {
			@Override
			public Boolean read(LocalData status) {
				return (status.operationScheduleInitializedSign
						.availablePermits() > 0);
			}
		});
	}

	public void mergeUpdatedOperationSchedule(
			final List<VehicleNotification> triggerVehicleNotifications) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				refreshPhase();
				for (OnMergeUpdatedOperationScheduleListener listener : new ArrayList<OnMergeUpdatedOperationScheduleListener>(
						onMergeUpdatedOperationScheduleListeners)) {
					listener.onMergeUpdatedOperationSchedule(triggerVehicleNotifications);
				}
			}
		});
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");

		// @see http://stackoverflow.com/questions/3687200/implement-startforeground-method-in-android
		// The intent to launch when the user clicks the expanded notification
		Intent notificationIntent = new Intent(this, StartupActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendIntent = PendingIntent
				.getActivity(this, 0, notificationIntent, 0);

		// This constructor is deprecated. Use Notification.Builder instead
		Notification notification = new Notification(
				android.R.drawable.ic_menu_info_details, "車載器アプリケーションを起動しています",
				System.currentTimeMillis());

		// This method is deprecated. Use Notification.Builder instead.
		notification.setLatestEventInfo(this, "車載器アプリケーション", "起動しています",
				pendIntent);

		notification.flags |= Notification.FLAG_NO_CLEAR;
		startForeground(FOREGROUND_NOTIFICATION_ID, notification);
		backgroundThread = new BackgroundTaskThread(this);
		backgroundThread.start();

		return binder;
	}

	public static final Integer FOREGROUND_NOTIFICATION_ID = 10;

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "onUnbind()");
		stopForeground(false);
		backgroundThread.interrupt();
		onInitializeListeners.clear();
		onEnterPhaseListeners.clear();
		onAlertUpdatedOperationScheduleListeners.clear();
		onAlertVehicleNotificationReceiveListeners.clear();
		onChangeLocationListeners.clear();
		onChangeOrientationListeners.clear();
		onChangeSignalStrengthListeners.clear();
		onChangeTemperatureListeners.clear();
		onExitListeners.clear();
		onMergeUpdatedOperationScheduleListeners.clear();
		onReceiveUpdatedOperationScheduleListeners.clear();
		onReceiveVehicleNotificationListeners.clear();
		onReplyUpdatedOperationScheduleVehicleNotificationsListeners.clear();
		onReplyVehicleNotificationListeners.clear();
		onStartNewOperationListeners.clear();
		onStartReceiveUpdatedOperationScheduleListeners.clear();
		Closeables.closeQuietly(dataSource);
		Closeables.closeQuietly(localDataSource);

		stopSelf();
		return false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	public void receiveUpdatedOperationSchedule(
			final List<OperationSchedule> operationSchedules,
			final List<VehicleNotification> triggerVehicleNotifications) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				operationScheduleLogic.receiveUpdatedOperationSchedule(
						operationSchedules, triggerVehicleNotifications);
				for (OnReceiveUpdatedOperationScheduleListener listener : new ArrayList<OnReceiveUpdatedOperationScheduleListener>(
						onReceiveUpdatedOperationScheduleListeners)) {
					listener.onReceiveUpdatedOperationSchedule(
							operationSchedules, triggerVehicleNotifications);
				}
			}
		});
	}

	public void receiveVehicleNotification(
			final List<VehicleNotification> vehicleNotifications) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				vehicleNotificationLogic
						.receiveVehicleNotification(vehicleNotifications);
				for (OnReceiveVehicleNotificationListener listener : new ArrayList<OnReceiveVehicleNotificationListener>(
						onReceiveVehicleNotificationListeners)) {
					listener.onReceiveVehicleNotification(vehicleNotifications);
				}
			}
		});
	}

	public void refreshPhase() {
		switch (getPhase()) {
		case INITIAL:
			enterDrivePhase();
			break;
		case DRIVE:
			enterDrivePhase();
			break;
		case PLATFORM:
			enterPlatformPhase();
			break;
		case FINISH:
			enterFinishPhase();
			break;
		}
	}

	public void replyUpdatedOperationScheduleVehicleNotifications(
			final List<VehicleNotification> vehicleNotifications) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				vehicleNotificationLogic
						.replyUpdatedOperationScheduleVehicleNotifications(vehicleNotifications);
				for (OnReplyUpdatedOperationScheduleVehicleNotificationsListener listener : new ArrayList<OnReplyUpdatedOperationScheduleVehicleNotificationsListener>(
						onReplyUpdatedOperationScheduleVehicleNotificationsListeners)) {
					listener.onReplyUpdatedOperationScheduleVehicleNotifications(vehicleNotifications);
				}
			}
		});
	}

	public void replyVehicleNotification(
			final VehicleNotification vehicleNotification) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				vehicleNotificationLogic
						.replyVehicleNotification(vehicleNotification);
				for (OnReplyVehicleNotificationListener listener : new ArrayList<OnReplyVehicleNotificationListener>(
						onReplyVehicleNotificationListeners)) {
					listener.onReplyVehicleNotification(vehicleNotification);
				}
			}
		});
	}

	public void setActivityPaused() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnPauseActivityListener listener : new ArrayList<OnPauseActivityListener>(
						onPauseActivityListeners)) {
					listener.onPauseActivity();
				}
			}
		});
	}

	public void setActivityResumed() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnResumeActivityListener listener : new ArrayList<OnResumeActivityListener>(
						onResumeActivityListeners)) {
					listener.onResumeActivity();
				}
			}
		});
	}

	public void setInitialized() {
		localDataSource.withWriteLock(new Writer() {
			@Override
			public void write(LocalData status) {
				status.operationScheduleInitializedSign.release();
			}
		});
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnInitializeListener listener : new ArrayList<OnInitializeListener>(
						onInitializeListeners)) {
					listener.onInitialize(InVehicleDeviceService.this);
				}
				refreshPhase();
			}
		});
	}

	public void setLocalDataSource(LocalDataSource localDataSource) {
		this.localDataSource = localDataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void speak(String message) {
		voiceServiceConnector.speak(message);
	}

	public void startNewOperation() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				operationScheduleLogic.startNewOperation();
				for (OnStartNewOperationListener listener : new ArrayList<OnStartNewOperationListener>(
						onStartNewOperationListeners)) {
					listener.onStartNewOperation();
				}
			}
		});
	}

	public void startReceiveUpdatedOperationSchedule() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnStartReceiveUpdatedOperationScheduleListener listener : new ArrayList<OnStartReceiveUpdatedOperationScheduleListener>(
						onStartReceiveUpdatedOperationScheduleListeners)) {
					listener.onStartReceiveUpdatedOperationSchedule();
				}
			}
		});
	}

	public void waitForOperationScheduleInitialize()
			throws InterruptedException {
		Semaphore operationScheduleInitializedSign = localDataSource
				.withReadLock(new Reader<Semaphore>() {
					@Override
					public Semaphore read(LocalData status) {
						return status.operationScheduleInitializedSign;
					}
				});
		operationScheduleInitializedSign.acquire();
		operationScheduleInitializedSign.release();
	}
}
