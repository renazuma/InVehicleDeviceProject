package com.kogasoftware.odt.invehicledevice.service.serviceunitstatuslogservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import org.mockito.Mockito;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class GpsLoggerTestCase extends AndroidTestCase {
	GpsLogger gpsLogger;
	LocationManager mockLocationManager;
	Long currentMillis = System.currentTimeMillis();
	Boolean isGpsProviderEnabled = true;
	Integer startCount = 0;
	Integer stopCount = 0;
	Boolean checkBroadcastsAreAllChecked = false;

	Context mockContext = new MockContext() {
		@Override
		public Object getSystemService(String name) {
			if (name.equals(Context.LOCATION_SERVICE)) {
				return mockLocationManager;
			} else {
				return getContext().getSystemService(name);
			}
		}

		@Override
		public void sendBroadcast(Intent intent) {
			getContext().sendBroadcast(intent);
		}
	};

	CountDownLatch handlerThreadIsReady = new CountDownLatch(1);
	BlockingQueue<Intent> trackingBroadcasts = new LinkedBlockingQueue<Intent>();
	BroadcastReceiver trackingBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			trackingBroadcasts.add(intent);
		}
	};

	HandlerThread handlerThread = new HandlerThread(
			GpsLoggerTestCase.class.getSimpleName()) {
		@Override
		public void run() {
			try {
				super.run();
			} finally {
				getContext().unregisterReceiver(trackingBroadcastReceiver);
			}
		}

		@Override
		protected void onLooperPrepared() {
			// getContext().registerReceiver(trackingBroadcastReceiver,
			// new IntentFilter(TrackingIntent.ACTION_TRACKING));
			handlerThreadIsReady.countDown();
		}
	};

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// DateTimeUtils.setCurrentMillisProvider(new MillisProvider() {
		// @Override
		// public long getMillis() {
		// return currentMillis;
		// }
		// });
		//
		// handlerThread.start();
		// assertTrue(handlerThreadIsReady.await(5, TimeUnit.SECONDS));
		// mockLocationManager = Mockito.mock(LocationManager.class);
		// Mockito.when(
		// mockLocationManager
		// .isProviderEnabled(LocationManager.GPS_PROVIDER))
		// .thenAnswer(new Answer<Boolean>() {
		// @Override
		// public Boolean answer(InvocationOnMock invocation)
		// throws Throwable {
		// return isGpsProviderEnabled;
		// }
		// });
		// trackingNotifier = new GpsLogger(mockContext);
	}

	private void assertRequestLocationUpdates(Integer extra) {
		startCount += extra;
		Mockito.verify(mockLocationManager, Mockito.times(startCount))
				.requestLocationUpdates(Mockito.anyString(), Mockito.anyLong(),
						Mockito.anyFloat(), Mockito.<LocationListener> any());
	}

	private void assertRequestLocationUpdates() {
		startCount++;
		assertRequestLocationUpdates(0);
	}

	private void assertRemoveUpdates() {
		stopCount++;
		assertRemoveUpdates(0);
	}

	private void assertRemoveUpdates(Integer extra) {
		stopCount += extra;
		Mockito.verify(mockLocationManager, Mockito.times(stopCount))
				.removeUpdates(Mockito.<LocationListener> any());
	}

	public void xtest_定数時間内に位置を受信しなかった場合再起動する() {
		assertRequestLocationUpdates();
		currentMillis += gpsLogger.getRestartTimeout();
		gpsLogger.run();
		assertRemoveUpdates();
		currentMillis += gpsLogger.getSleepTimeout();
		gpsLogger.run();
		assertRequestLocationUpdates();
	}

	public void xtest_closeしたらRemoveUpdatesをする() {
		assertRequestLocationUpdates();
		gpsLogger.close();
		assertRemoveUpdates();
	}

	public void xtest_定数秒に一度現在位置をブロードキャストする() throws InterruptedException {
		gpsLogger.run();
		currentMillis += GpsLogger.BROADCAST_PERIOD_MILLIS;
		gpsLogger.run();

		// 定数秒に達したのでここでブロードキャストが飛ぶ
		// TrackingIntent ti1 = getBroadcast();
		// assertFalse(ti1.getLocation().isPresent());

		// 定数秒に達していないのでブロードキャストは飛ばない
		gpsLogger.run();
		gpsLogger.run();
		currentMillis += GpsLogger.BROADCAST_PERIOD_MILLIS / 2;
		gpsLogger.run();
		currentMillis += GpsLogger.BROADCAST_PERIOD_MILLIS;
		gpsLogger.run();

		// 定数秒に達したのでここでブロードキャストが飛ぶ
		// TrackingIntent ti2 = getBroadcast();
		// assertFalse(ti2.getLocation().isPresent());
	}

	@Override
	protected void tearDown() throws Exception {
		// handlerThread.quit();
		// handlerThread.interrupt();
		// if (checkBroadcastsAreAllChecked) {
		// assertEquals(0, trackingBroadcasts.size());
		// }
		// Closeables.close(trackingNotifier, false);
	}
}
