package com.kogasoftware.odt.invehicledevice.test.unit.service.trackingservice;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeUtils.MillisProvider;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.trackingservice.TrackingIntent;
import com.kogasoftware.odt.invehicledevice.service.trackingservice.TrackingNotifier;

public class TrackingNotifierTestCase extends AndroidTestCase {
	TrackingNotifier trackingNotifier;
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
			TrackingNotifierTestCase.class.getSimpleName()) {
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
			getContext().registerReceiver(trackingBroadcastReceiver,
					new IntentFilter(TrackingIntent.ACTION_TRACKING));
			handlerThreadIsReady.countDown();
		}
	};

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		DateTimeUtils.setCurrentMillisProvider(new MillisProvider() {
			@Override
			public long getMillis() {
				return currentMillis;
			}
		});

		handlerThread.start();
		assertTrue(handlerThreadIsReady.await(5, TimeUnit.SECONDS));
		mockLocationManager = Mockito.mock(LocationManager.class);
		Mockito.when(
				mockLocationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER))
				.thenAnswer(new Answer<Boolean>() {
					@Override
					public Boolean answer(InvocationOnMock invocation)
							throws Throwable {
						return isGpsProviderEnabled;
					}
				});
		trackingNotifier = new TrackingNotifier(mockContext);
	}

	private void assertRequestLocationUpdates(Integer times) {
		Mockito.verify(mockLocationManager, Mockito.times(times))
				.requestLocationUpdates(Mockito.anyString(), Mockito.anyLong(),
						Mockito.anyFloat(), Mockito.<LocationListener> any());
	}

	private void assertRequestLocationUpdates() {
		startCount++;
		assertRequestLocationUpdates(startCount);
	}

	private void assertRemoveUpdates() {
		stopCount++;
		assertRemoveUpdates(stopCount);
	}

	private void assertRemoveUpdates(Integer times) {
		Mockito.verify(mockLocationManager, Mockito.times(times))
				.removeUpdates(Mockito.<LocationListener> any());
	}

	public void test_定数時間内に位置を受信しなかった場合再起動する() {
		assertRequestLocationUpdates();
		currentMillis += trackingNotifier.getRestartTimeout();
		trackingNotifier.run();
		assertRemoveUpdates();
		currentMillis += trackingNotifier.getSleepTimeout();
		trackingNotifier.run();
		assertRequestLocationUpdates();
	}

	public void test_定数時間内に位置を受信したら再起動しない() {
		Location l = new Location("");
		l.setLatitude(50);
		l.setLongitude(100);

		assertRequestLocationUpdates();

		currentMillis += trackingNotifier.getRestartTimeout() / 2;
		trackingNotifier.run();
		trackingNotifier.onLocationChanged(l);
		trackingNotifier.run();
		currentMillis += trackingNotifier.getRestartTimeout() / 10 * 9;
		trackingNotifier.run();

		assertRemoveUpdates(0);

		trackingNotifier.onLocationChanged(l);
		trackingNotifier.run();
		currentMillis += trackingNotifier.getRestartTimeout() / 10 * 9;
		trackingNotifier.run();
		assertRemoveUpdates(0);

		// 受信がなければ再起動
		currentMillis += trackingNotifier.getRestartTimeout() / 10 * 9;
		trackingNotifier.run();
		assertRemoveUpdates();
	}

	public void test_closeしたらRemoveUpdatesをする() {
		assertRequestLocationUpdates();
		trackingNotifier.close();
		assertRemoveUpdates();
	}

	public void test_Locationを受信したらブロードキャストする() throws InterruptedException {
		Location l1 = new Location("");
		l1.setLatitude(50);
		l1.setLongitude(100);

		trackingNotifier.onLocationChanged(l1);
		TrackingIntent ti1 = getBroadcast();
		assertEquals(l1.getLatitude(), ti1.getLocation().get().getLatitude());
		assertEquals(l1.getLongitude(), ti1.getLocation().get().getLongitude());

		Location l2 = new Location("");
		l2.setLatitude(-50);
		l2.setLongitude(-90);

		trackingNotifier.onLocationChanged(l2);
		TrackingIntent ti2 = getBroadcast();
		assertEquals(l2.getLatitude(), ti2.getLocation().get().getLatitude());
		assertEquals(l2.getLongitude(), ti2.getLocation().get().getLongitude());
	}

	public void test_人工衛星の数を受信したらブロードキャストする() throws InterruptedException {
		Integer s1 = 10;
		trackingNotifier.onSatellitesCountChanged(s1, 0);
		TrackingIntent ti1 = getBroadcast();
		assertEquals(s1, ti1.getSatellitesCount());

		Integer s2 = 0;
		trackingNotifier.onSatellitesCountChanged(s2, 0);
		TrackingIntent ti2 = getBroadcast();
		assertEquals(s2, ti2.getSatellitesCount());
	}

	public void test_定数秒に一度現在位置をブロードキャストする() throws InterruptedException {
		trackingNotifier.run();
		currentMillis += TrackingNotifier.BROADCAST_PERIOD_MILLIS;
		trackingNotifier.run();

		// 定数秒に達したのでここでブロードキャストが飛ぶ
		TrackingIntent ti1 = getBroadcast();
		assertFalse(ti1.getLocation().isPresent());

		// 定数秒に達していないのでブロードキャストは飛ばない
		trackingNotifier.run();
		trackingNotifier.run();
		currentMillis += TrackingNotifier.BROADCAST_PERIOD_MILLIS / 2;
		trackingNotifier.run();
		currentMillis += TrackingNotifier.BROADCAST_PERIOD_MILLIS;
		trackingNotifier.run();

		// 定数秒に達したのでここでブロードキャストが飛ぶ
		TrackingIntent ti2 = getBroadcast();
		assertFalse(ti2.getLocation().isPresent());
	}

	public void test_位置FIXに利用された人工衛星の数が一定以上の場合定期ブロードキャスト時のLocationの時刻を更新する()
			throws InterruptedException {
		trackingNotifier.run();
		currentMillis += TrackingNotifier.BROADCAST_PERIOD_MILLIS;
		trackingNotifier.run();

		// Locationが存在しない場合
		assertFalse(getBroadcast().getLocation().isPresent());

		// Locationが存在する場合
		Location l = new Location("");
		l.setTime(12345);
		trackingNotifier.onLocationChanged(l);
		assertEquals(12345, getBroadcast().getLocation().get().getTime());
		currentMillis += TrackingNotifier.BROADCAST_PERIOD_MILLIS;
		trackingNotifier.run();
		assertEquals(12345, getBroadcast().getLocation().get().getTime()); // 時刻は変わらない

		final Integer USCFULT = TrackingNotifier.USED_SATELLITES_COUNT_FOR_UPDATE_LOCATION_TIME;
		trackingNotifier.onSatellitesCountChanged(30, USCFULT / 2);
		assertEquals(12345, getBroadcast().getLocation().get().getTime()); // 時刻は変わらない

		trackingNotifier.onSatellitesCountChanged(30, USCFULT);
		assertEquals(currentMillis.longValue(), getBroadcast().getLocation()
				.get().getTime()); // 時刻が変更される

		long t = currentMillis;
		currentMillis += 100;
		trackingNotifier.onSatellitesCountChanged(30, USCFULT - 1);
		assertEquals(t, getBroadcast().getLocation().get().getTime()); // 時刻は変わらない

		currentMillis += TrackingNotifier.BROADCAST_PERIOD_MILLIS;
		trackingNotifier.run();
		assertEquals(t, getBroadcast().getLocation().get().getTime()); // 時刻は変わらない

		trackingNotifier.onSatellitesCountChanged(0, USCFULT);
		assertEquals(currentMillis.longValue(), getBroadcast().getLocation()
				.get().getTime()); // 時刻が変更される
	}

	TrackingIntent getBroadcast() throws InterruptedException {
		checkBroadcastsAreAllChecked = true;
		Intent i = trackingBroadcasts.poll(1, TimeUnit.SECONDS);
		assertNotNull(i);
		return new TrackingIntent(i);
	}

	void assertBroadcast() {
		try {
			getBroadcast();
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		handlerThread.quit();
		handlerThread.interrupt();
		if (checkBroadcastsAreAllChecked) {
			assertEquals(0, trackingBroadcasts.size());
		}
		Closeables.closeQuietly(trackingNotifier);
	}
}
