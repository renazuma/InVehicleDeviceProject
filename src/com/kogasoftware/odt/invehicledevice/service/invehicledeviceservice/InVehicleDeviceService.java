package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.apiclient.DataSource;
import com.kogasoftware.odt.invehicledevice.apiclient.EmptyDataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalDataSource.Reader;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.backgroundtask.BackgroundTaskThread;

public class InVehicleDeviceService extends Service {
	public class LocalBinder extends Binder {
		public InVehicleDeviceService getService() {
			return InVehicleDeviceService.this;
		}
	}

	private final EventDispatcher eventDispatcher = new EventDispatcher();
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public static enum PayTiming {
		GET_ON, GET_OFF,
	}

	public static final Integer NEW_SCHEDULE_DOWNLOAD_HOUR = 0;
	public static final Integer NEW_SCHEDULE_DOWNLOAD_MINUTE = 5;

	private static final Object MOCK_DATE_LOCK = new Object();
	public static final SortedMap<Date, List<CountDownLatch>> mockSleepStatus = new TreeMap<Date, List<CountDownLatch>>();
	private static Boolean useMockDate = false;
	private static Date mockDate = new Date();

	private static final WeakHashMap<Thread, Handler> HANDLERS = new WeakHashMap<Thread, Handler>();
	public static final Handler DEFAULT_HANDLER = new Handler(Looper.getMainLooper()); // TODO:メインスレッドではないHandlerを作る

	public static Handler getThreadHandler() { // TODO: 共有場所に移動
		synchronized (HANDLERS) {
			Thread currentThread = Thread.currentThread();
			if (HANDLERS.containsKey(currentThread)) {
				return HANDLERS.get(currentThread);
			}
			Looper looper = Looper.myLooper();
			if (looper == null) {
				return DEFAULT_HANDLER;
			} else {
				Handler handler = new Handler(looper);
				HANDLERS.put(currentThread, handler);
				return handler;
			}
		}
	}

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

	public static void sleep(long time) throws InterruptedException {
		if (!BuildConfig.DEBUG) {
			Thread.sleep(time);
			return;
		}
		CountDownLatch countDownLatch = new CountDownLatch(1);
		synchronized (MOCK_DATE_LOCK) {
			Date wakeUpDate = new Date(getDate().getTime() + time);
			if (!mockSleepStatus.containsKey(wakeUpDate)) {
				mockSleepStatus.put(wakeUpDate,
						new LinkedList<CountDownLatch>());
			}
			mockSleepStatus.get(wakeUpDate).add(countDownLatch);
		}
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
			for (Entry<Date, List<CountDownLatch>> entry : Maps.newTreeMap(
					mockSleepStatus).entrySet()) {
				if (mockDate.before(entry.getKey())) {
					break;
				}
				for (CountDownLatch countDownLatch : entry.getValue()) {
					countDownLatch.countDown();
					Thread.yield();
				}
				mockSleepStatus.remove(entry.getKey());
			}
		}
	}

	protected final IBinder binder = new LocalBinder();
	protected final Handler handler = new Handler(Looper.getMainLooper());
	protected final VoiceServiceConnector voiceServiceConnector;

	protected volatile Thread backgroundThread = new EmptyThread();
	protected volatile DataSource remoteDataSource = new EmptyDataSource();
	protected volatile LocalDataSource localDataSource = new LocalDataSource();

	public InVehicleDeviceService() {
		super();
		voiceServiceConnector = new VoiceServiceConnector(this);
	}



	public void exit() {
		eventDispatcher.dispatchExit();
	}

	public DataSource getRemoteDataSource() {
		return remoteDataSource;
	}

	public LocalDataSource getLocalDataSource() {
		return localDataSource;
	}

	public EnumSet<PayTiming> getPayTiming() {
		return EnumSet.of(PayTiming.GET_ON);
	}

	public String getToken() {
		return localDataSource.withReadLock(new Reader<String>() {
			@Override
			public String read(LocalData status) {
				return status.token;
			}
		});
	}

	public Boolean isOperationInitialized() {
		return localDataSource.withReadLock(new Reader<Boolean>() {
			@Override
			public Boolean read(LocalData localData) {
				return isOperationInitialized(localData);
			}
		});
	}

	public Boolean isOperationInitialized(LocalData localData) {
		return (localData.operationScheduleInitializedSign.availablePermits() > 0 && localData.serviceProviderInitializedSign
				.availablePermits() > 0);
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

		getEventDispatcher().addOnPauseActivityListener(voiceServiceConnector);
		getEventDispatcher().addOnResumeActivityListener(voiceServiceConnector);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		backgroundThread.interrupt();
		backgroundThread = new EmptyThread();

		Closeables.closeQuietly(eventDispatcher);
		Closeables.closeQuietly(remoteDataSource);
		Closeables.closeQuietly(localDataSource);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
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

	private Boolean mapAutoZoom = true;
	private Integer mapZoomLevel = 12;

	public Boolean getMapAutoZoom() {
		return mapAutoZoom;
	}

	public Integer getMapZoomLevel() {
		return mapZoomLevel;
	}

	public void setMapAutoZoom(Boolean mapAutoZoom) {
		this.mapAutoZoom = mapAutoZoom;
	}

	public void setMapZoomLevel(Integer mapZoomLevel) {
		this.mapZoomLevel = mapZoomLevel;
	}
}
