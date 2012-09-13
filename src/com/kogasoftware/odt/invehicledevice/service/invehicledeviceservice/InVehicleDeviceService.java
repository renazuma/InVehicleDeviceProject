package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.tuple.Pair;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.BackgroundTaskThread;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceService extends Service {
	public class LocalBinder extends Binder {
		public InVehicleDeviceService getService() {
			return InVehicleDeviceService.this;
		}
	}

	public interface OnAlertUpdatedOperationScheduleListener {
		void onAlertUpdatedOperationSchedule();
	}

	public interface OnAlertVehicleNotificationReceiveListener {
		void onAlertVehicleNotificationReceive();
	}

	public interface OnChangeLocationListener {
		void onChangeLocation(Location location, Optional<GpsStatus> gpsStatus);
	}

	public interface OnChangeOrientationListener {
		void onChangeOrientation(Double orientationDegree);
	}

	public interface OnChangeSignalStrengthListener {
		void onChangeSignalStrength(Integer signalStrengthPercentage);
	}

	public interface OnChangeTemperatureListener {
		void onChangeTemperature(Double celciusTemperature);
	}

	public interface OnEnterPhaseListener {
		void onEnterDrivePhase();

		void onEnterFinishPhase();

		void onEnterPlatformPhase();
	}

	public interface OnExitListener {
		void onExit();
	}

	public interface OnMergeUpdatedOperationScheduleListener {
		void onMergeUpdatedOperationSchedule(
				List<VehicleNotification> triggerVehicleNotifications);
	}

	public interface OnPauseActivityListener {
		void onPauseActivity();
	}

	public interface OnReceiveUpdatedOperationScheduleListener {
		void onReceiveUpdatedOperationSchedule(
				List<OperationSchedule> operationSchedules,
				List<VehicleNotification> triggerVehicleNotifications);
	}

	public interface OnReceiveVehicleNotificationListener {
		void onReceiveVehicleNotification(
				List<VehicleNotification> vehicleNotifications);
	}

	public interface OnReplyUpdatedOperationScheduleVehicleNotificationsListener {
		void onReplyUpdatedOperationScheduleVehicleNotifications(
				List<VehicleNotification> vehicleNotifications);
	}

	public interface OnReplyVehicleNotificationListener {
		void onReplyVehicleNotification(VehicleNotification vehicleNotification);
	}

	public interface OnResumeActivityListener {
		void onResumeActivity();
	}

	public interface OnStartNewOperationListener {
		void onStartNewOperation();
	}

	public interface OnStartReceiveUpdatedOperationScheduleListener {
		void onStartReceiveUpdatedOperationSchedule();
	}

	public static enum PayTiming {
		GET_ON, GET_OFF,
	}

	public static final Integer NEW_SCHEDULE_DOWNLOAD_HOUR = 0;
	public static final Integer NEW_SCHEDULE_DOWNLOAD_MINUTE = 5;

	private static final Object MOCK_DATE_LOCK = new Object();
	private static Boolean useMockDate = false;
	private static Date mockDate = new Date();

	private static final String TAG = InVehicleDeviceService.class
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
		Date now = new Date();
		synchronized (MOCK_DATE_LOCK) {
			if (useMockDate) {
				return mockDate;
			} else {
				return now;
			}
		}
	}

	private static final List<Pair<Date, CountDownLatch>> mockSleepStatus = new LinkedList<Pair<Date, CountDownLatch>>();

	public static void sleep(long time) throws InterruptedException {
		if (!BuildConfig.DEBUG) {
			Thread.sleep(time);
			return;
		}
		CountDownLatch countDownLatch = new CountDownLatch(1);
		mockSleepStatus.add(Pair.of(new Date(getDate().getTime() + time),
				countDownLatch));
		countDownLatch.await();
	}

	@VisibleForTesting
	public static void setMockDate(Date mockDate) {
		if (!BuildConfig.DEBUG) {
			return;
		}
		synchronized (MOCK_DATE_LOCK) {
			useMockDate = true;
			InVehicleDeviceService.mockDate = mockDate;
			for (Pair<Date, CountDownLatch> sleepState : Lists.newLinkedList(mockSleepStatus)) {
				if (mockDate.after(sleepState.getKey())) {
					sleepState.getValue().countDown();
					mockSleepStatus.remove(sleepState);
				}
			}
		}
	}

	protected static <T> Set<T> newListenerSet() {
		// return Collections.newSetFromMap(new WeakHashMap<T, Boolean>());
		return new CopyOnWriteArraySet<T>();
	}

	protected final OperationScheduleLogic operationScheduleLogic;
	protected final PassengerRecordLogic passengerRecordLogic;
	protected final VehicleNotificationLogic vehicleNotificationLogic;
	protected final ServiceUnitStatusLogLogic serviceUnitStatusLogLogic;
	protected final IBinder binder = new LocalBinder();
	protected final Handler handler = new Handler(Looper.getMainLooper());
	protected final VoiceServiceConnector voiceServiceConnector;

	protected final Set<OnEnterPhaseListener> onEnterPhaseListeners = newListenerSet();
	protected final Set<OnAlertUpdatedOperationScheduleListener> onAlertUpdatedOperationScheduleListeners = newListenerSet();
	protected final Set<OnAlertVehicleNotificationReceiveListener> onAlertVehicleNotificationReceiveListeners = newListenerSet();
	protected final Set<OnChangeLocationListener> onChangeLocationListeners = newListenerSet();
	protected final Set<OnChangeOrientationListener> onChangeOrientationListeners = newListenerSet();
	protected final Set<OnChangeSignalStrengthListener> onChangeSignalStrengthListeners = newListenerSet();
	protected final Set<OnChangeTemperatureListener> onChangeTemperatureListeners = newListenerSet();
	protected final Set<OnExitListener> onExitListeners = newListenerSet();
	protected final Set<OnMergeUpdatedOperationScheduleListener> onMergeUpdatedOperationScheduleListeners = newListenerSet();
	protected final Set<OnReceiveUpdatedOperationScheduleListener> onReceiveUpdatedOperationScheduleListeners = newListenerSet();
	protected final Set<OnReceiveVehicleNotificationListener> onReceiveVehicleNotificationListeners = newListenerSet();
	protected final Set<OnReplyUpdatedOperationScheduleVehicleNotificationsListener> onReplyUpdatedOperationScheduleVehicleNotificationsListeners = newListenerSet();
	protected final Set<OnReplyVehicleNotificationListener> onReplyVehicleNotificationListeners = newListenerSet();
	protected final Set<OnStartNewOperationListener> onStartNewOperationListeners = newListenerSet();
	protected final Set<OnStartReceiveUpdatedOperationScheduleListener> onStartReceiveUpdatedOperationScheduleListeners = newListenerSet();
	protected final Set<OnPauseActivityListener> onPauseActivityListeners = newListenerSet();
	protected final Set<OnResumeActivityListener> onResumeActivityListeners = newListenerSet();

	protected volatile Thread backgroundThread = new EmptyThread();
	protected volatile DataSource remoteDataSource = new EmptyDataSource();
	protected volatile LocalDataSource localDataSource = new LocalDataSource();

	public InVehicleDeviceService() {
		super();

		operationScheduleLogic = new OperationScheduleLogic(this);
		passengerRecordLogic = new PassengerRecordLogic(this);
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

	public void removeOnAlertUpdatedOperationScheduleListener(
			OnAlertUpdatedOperationScheduleListener listener) {
		onAlertUpdatedOperationScheduleListeners.remove(listener);
	}

	public void removeOnAlertVehicleNotificationReceiveListener(
			OnAlertVehicleNotificationReceiveListener listener) {
		onAlertVehicleNotificationReceiveListeners.remove(listener);
	}

	public void removeOnChangeLocationListener(OnChangeLocationListener listener) {
		onChangeLocationListeners.remove(listener);
	}

	public void removeOnChangeOrientationListener(
			OnChangeOrientationListener listener) {
		onChangeOrientationListeners.remove(listener);
	}

	public void removeOnChangeSignalStrengthListener(
			OnChangeSignalStrengthListener listener) {
		onChangeSignalStrengthListeners.remove(listener);
	}

	public void removeOnChangeTemperatureListener(
			OnChangeTemperatureListener listener) {
		onChangeTemperatureListeners.remove(listener);
	}

	public void removeOnEnterPhaseListener(OnEnterPhaseListener listener) {
		onEnterPhaseListeners.remove(listener);
	}

	public void removeOnExitListener(OnExitListener listener) {
		onExitListeners.remove(listener);
	}

	public void removeOnMergeUpdatedOperationScheduleListener(
			OnMergeUpdatedOperationScheduleListener listener) {
		onMergeUpdatedOperationScheduleListeners.remove(listener);
	}

	public void removeOnPauseActivityListener(OnPauseActivityListener listener) {
		onPauseActivityListeners.remove(listener);
	}

	public void removeOnReceiveUpdatedOperationScheduleListener(
			OnReceiveUpdatedOperationScheduleListener listener) {
		onReceiveUpdatedOperationScheduleListeners.remove(listener);
	}

	public void removeOnReceiveVehicleNotificationListener(
			OnReceiveVehicleNotificationListener listener) {
		onReceiveVehicleNotificationListeners.remove(listener);
	}

	public void removeOnReplyUpdatedOperationScheduleVehicleNotificationsListener(
			OnReplyUpdatedOperationScheduleVehicleNotificationsListener listener) {
		onReplyUpdatedOperationScheduleVehicleNotificationsListeners
				.remove(listener);
	};

	public void removeOnReplyVehicleNotificationListener(
			OnReplyVehicleNotificationListener listener) {
		onReplyVehicleNotificationListeners.remove(listener);
	};

	public void removeOnResumeActivityListener(OnResumeActivityListener listener) {
		onResumeActivityListeners.remove(listener);
	}

	public void removeOnStartNewOperationListener(
			OnStartNewOperationListener listener) {
		onStartNewOperationListeners.remove(listener);
	}

	public void removeOnStartReceiveUpdatedOperationScheduleListener(
			OnStartReceiveUpdatedOperationScheduleListener listener) {
		onStartReceiveUpdatedOperationScheduleListeners.remove(listener);
	}

	public void alertUpdatedOperationSchedule() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnAlertUpdatedOperationScheduleListener listener : Lists
						.newArrayList(onAlertUpdatedOperationScheduleListeners)) {
					listener.onAlertUpdatedOperationSchedule();
				}
			}
		});
	}

	public void alertVehicleNotificationReceive() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnAlertVehicleNotificationReceiveListener listener : Lists
						.newArrayList(onAlertVehicleNotificationReceiveListeners)) {
					listener.onAlertVehicleNotificationReceive();
				}
			}
		});
	}

	public void changeLocation(final Location location,
			final Optional<GpsStatus> gpsStatus) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				serviceUnitStatusLogLogic.changeLocation(location);
				for (OnChangeLocationListener listener : Lists
						.newArrayList(onChangeLocationListeners)) {
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
				for (OnChangeOrientationListener listener : Lists
						.newArrayList(onChangeOrientationListeners)) {
					listener.onChangeOrientation(degree);
				}
			}
		});
	}

	public void changeSignalStrength(final Integer signalStrengthPercentage) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnChangeSignalStrengthListener listener : Lists
						.newArrayList(onChangeSignalStrengthListeners)) {
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
				for (OnChangeTemperatureListener listener : Lists
						.newArrayList(onChangeTemperatureListeners)) {
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
				for (OnEnterPhaseListener listener : Lists
						.newArrayList(onEnterPhaseListeners)) {
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
				for (OnEnterPhaseListener listener : Lists
						.newArrayList(onEnterPhaseListeners)) {
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
				for (OnEnterPhaseListener listener : Lists
						.newArrayList(onEnterPhaseListeners)) {
					listener.onEnterPlatformPhase();
				}
			}
		});
	}

	public void exit() {
		// このメソッドが呼ばれた後に追加されたOnExitListenerを無視するため、
		// handlerと同じスレッドで呼ばれた場合はpostせずそのままコールバックを行う
		if (handler.getLooper().getThread().getId() != Thread.currentThread()
				.getId()) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					exit();
				}
			});
			return;
		}
		for (OnExitListener listener : new ArrayList<OnExitListener>(
				onExitListeners)) {
			listener.onExit();
		}
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

	public DataSource getRemoteDataSource() {
		return remoteDataSource;
	}

	public LocalDataSource getLocalDataSource() {
		return localDataSource;
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

	public List<PassengerRecord> getPassengerRecords() {
		return localDataSource
				.withReadLock(new Reader<List<PassengerRecord>>() {
					@Override
					public List<PassengerRecord> read(LocalData status) {
						return new LinkedList<PassengerRecord>(
								status.passengerRecords);
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

	public Boolean isOperationInitialized() {
		return localDataSource.withReadLock(new Reader<Boolean>() {
			@Override
			public Boolean read(LocalData status) {
				return (status.operationScheduleInitializedSign
						.availablePermits() > 0 && status.serviceProviderInitializedSign
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
				for (OnMergeUpdatedOperationScheduleListener listener : Lists
						.newArrayList(onMergeUpdatedOperationScheduleListeners)) {
					listener.onMergeUpdatedOperationSchedule(triggerVehicleNotifications);
				}
			}
		});
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind()");
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "onUnbind()");
		return false;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate()");

		backgroundThread = new BackgroundTaskThread(this);
		backgroundThread.start();

		addOnPauseActivityListener(voiceServiceConnector);
		addOnResumeActivityListener(voiceServiceConnector);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		backgroundThread.interrupt();
		backgroundThread = new EmptyThread();

		removeOnPauseActivityListener(voiceServiceConnector);
		removeOnResumeActivityListener(voiceServiceConnector);

		onEnterPhaseListeners.clear();
		onAlertUpdatedOperationScheduleListeners.clear();
		onAlertVehicleNotificationReceiveListeners.clear();
		onChangeLocationListeners.clear();
		onChangeOrientationListeners.clear();
		onChangeSignalStrengthListeners.clear();
		onChangeTemperatureListeners.clear();
		onExitListeners.clear();
		onMergeUpdatedOperationScheduleListeners.clear();
		onPauseActivityListeners.clear();
		onReceiveUpdatedOperationScheduleListeners.clear();
		onReceiveVehicleNotificationListeners.clear();
		onReplyUpdatedOperationScheduleVehicleNotificationsListeners.clear();
		onReplyVehicleNotificationListeners.clear();
		onResumeActivityListeners.clear();
		onStartNewOperationListeners.clear();
		onStartReceiveUpdatedOperationScheduleListeners.clear();

		Closeables.closeQuietly(remoteDataSource);
		Closeables.closeQuietly(localDataSource);
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
				for (OnReceiveUpdatedOperationScheduleListener listener : Lists
						.newArrayList(onReceiveUpdatedOperationScheduleListeners)) {
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
				for (OnReceiveVehicleNotificationListener listener : Lists
						.newArrayList(onReceiveVehicleNotificationListeners)) {
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
				for (OnReplyUpdatedOperationScheduleVehicleNotificationsListener listener : Lists
						.newArrayList(onReplyUpdatedOperationScheduleVehicleNotificationsListeners)) {
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
				for (OnReplyVehicleNotificationListener listener : Lists
						.newArrayList(onReplyVehicleNotificationListeners)) {
					listener.onReplyVehicleNotification(vehicleNotification);
				}
			}
		});
	}

	public void setActivityPaused() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnPauseActivityListener listener : Lists
						.newArrayList(onPauseActivityListeners)) {
					listener.onPauseActivity();
				}
			}
		});
	}

	public void setActivityResumed() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnResumeActivityListener listener : Lists
						.newArrayList(onResumeActivityListeners)) {
					listener.onResumeActivity();
				}
			}
		});
	}

	public void setLocalDataSource(LocalDataSource localDataSource) {
		this.localDataSource = localDataSource;
	}

	public void setRemoteDataSource(DataSource remoteDataSource) {
		this.remoteDataSource = remoteDataSource;
	}

	public void speak(String message) {
		voiceServiceConnector.speak(message);
	}

	public void startNewOperation() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				operationScheduleLogic.startNewOperation();
				for (OnStartNewOperationListener listener : Lists
						.newArrayList(onStartNewOperationListeners)) {
					listener.onStartNewOperation();
				}
			}
		});
	}

	public void startReceiveUpdatedOperationSchedule() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				for (OnStartReceiveUpdatedOperationScheduleListener listener : Lists
						.newArrayList(onStartReceiveUpdatedOperationScheduleListeners)) {
					listener.onStartReceiveUpdatedOperationSchedule();
				}
			}
		});
	}

	public void waitForOperationInitialize() throws InterruptedException {
		Semaphore operationScheduleInitializedSign = localDataSource
				.withReadLock(new Reader<Semaphore>() {
					@Override
					public Semaphore read(LocalData status) {
						return status.operationScheduleInitializedSign;
					}
				});
		Semaphore serviceProviderInitializedSign = localDataSource
				.withReadLock(new Reader<Semaphore>() {
					@Override
					public Semaphore read(LocalData status) {
						return status.serviceProviderInitializedSign;
					}
				});

		operationScheduleInitializedSign.acquire();
		operationScheduleInitializedSign.release();
		serviceProviderInitializedSign.acquire();
		serviceProviderInitializedSign.release();
	}

	public Boolean isSelected(PassengerRecord passengerRecord) {
		return passengerRecordLogic.isSelected(passengerRecord);
	}

	public void unselect(PassengerRecord passengerRecord) {
		passengerRecordLogic.unselect(passengerRecord);
	}

	public void select(PassengerRecord passengerRecord) {
		passengerRecordLogic.select(passengerRecord);
	}

	public Boolean isGetOffScheduled(PassengerRecord passengerRecord) {
		return passengerRecordLogic.isGetOffScheduled(passengerRecord);
	}

	public Boolean canGetOn(PassengerRecord passengerRecord) {
		return passengerRecordLogic.canGetOn(passengerRecord);
	}

	public Boolean isGetOnScheduled(PassengerRecord passengerRecord) {
		return passengerRecordLogic.isGetOnScheduled(passengerRecord);
	}

	public Boolean canGetOff(PassengerRecord passengerRecord) {
		return passengerRecordLogic.canGetOff(passengerRecord);
	}

	public List<PassengerRecord> getGetOffScheduledAndUnhandledPassengerRecords() {
		return passengerRecordLogic
				.getGetOffScheduledAndUnhandledPassengerRecords();
	}

	public List<PassengerRecord> getNoGettingOnPassengerRecords() {
		return passengerRecordLogic.getNoGettingOnPassengerRecords();
	}

	public List<PassengerRecord> getNoGettingOffPassengerRecords() {
		return passengerRecordLogic.getNoGettingOffPassengerRecords();
	}

	public List<PassengerRecord> getNoPaymentPassengerRecords() {
		return passengerRecordLogic.getNoPaymentPassengerRecords();
	}
}
