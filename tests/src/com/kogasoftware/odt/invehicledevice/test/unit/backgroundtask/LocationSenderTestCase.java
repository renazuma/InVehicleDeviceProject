package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import com.kogasoftware.odt.invehicledevice.backgroundtask.BackgroundTask;
import com.kogasoftware.odt.invehicledevice.backgroundtask.LocationSender;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.Status;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.Subscriber;
import com.kogasoftware.odt.invehicledevice.test.util.datasource.DummyDataSource;

public class LocationSenderTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	StatusAccess sa;
	CommonLogic cl;
	DummyDataSource dds;
	LocationSender ls;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dds = new DummyDataSource();
		DataSourceFactory.setInstance(dds);
		sa = new StatusAccess(getActivity());
		cl = new CommonLogic(getActivity(), getActivityHandler(), sa);
		ls = new LocationSender(cl);
	}

	@Override
	protected void tearDown() throws Exception {
		if (cl != null) {
			cl.dispose();
		}
		super.tearDown();
	}

	public void testBackgroundTaskによってUiEventBusに登録される() throws Exception {
		final AtomicReference<BackgroundTask> bt = new AtomicReference<BackgroundTask>();
		final CountDownLatch cdl = new CountDownLatch(1);
		Thread t = new Thread() {
			@Override
			public void run() {
				bt.set(new BackgroundTask(cl,
						getInstrumentation().getTargetContext(), sa));
				cdl.countDown();
				bt.get().loop();
			}
		};
		t.start();
		assertTrue(cdl.await(5, TimeUnit.SECONDS));
		Thread.sleep(5000);
		assertEquals(cl.countRegisteredClass(LocationSender.class).intValue(),
				1);
		bt.get().quit();
	}

	/**
	 * 現在のServiceUnitStatusLogをサーバーへ送信
	 */
	public void testRun_1() throws Exception {
		sa.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog.setTemperature(500);
			}
		});
		assertTrue(dds.sendServiceUnitStatusLogArgs.isEmpty());
		ls.run();
		assertEquals(dds.sendServiceUnitStatusLogArgs.size(), 1);
		assertEquals(dds.sendServiceUnitStatusLogArgs.get(0).getTemperature()
				.get().intValue(), 500);
	}

	/**
	 * 位置情報を受信したらLocationReceivedEvent送出
	 */
	public void testOnLocationChanged() throws Exception {
		for (Integer i = 0; i < 3; ++i) {
			final Integer lat = 10 + i * 20; // TODO:値が丸まっていないかのテスト
			final Integer lon = 45 + i * 5;
			Subscriber<LocationReceivedEvent> s = Subscriber.of(
					LocationReceivedEvent.class, cl);
			final AtomicReference<Location> location = new AtomicReference<Location>();
			final CountDownLatch cdl = new CountDownLatch(1);
			Thread t = new Thread() {
				Looper looper;

				@Override
				public void run() {
					String provider = "Test";
					Looper.prepare();
					looper = Looper.myLooper();
					LocationManager locationManager = (LocationManager) getInstrumentation()
							.getContext().getSystemService(
									Context.LOCATION_SERVICE);
					locationManager.addTestProvider("Test", false, false,
							false, false, false, false, false,
							Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
					locationManager.setTestProviderEnabled(provider, true);
					try {
						locationManager.requestLocationUpdates(
								provider, 0, 0, ls);
						Location l = new Location(provider);
						l.setLatitude(lat);
						l.setLongitude(lon);
						locationManager.setTestProviderLocation(provider, l);
						location.set(l);
						cdl.countDown();
						Looper.loop();
					} finally {
						locationManager.removeUpdates(ls);
						locationManager.removeTestProvider(provider);
					}
				}

				@Override
				public void interrupt() {
					looper.quit();
					super.interrupt();
				}
			};
			t.start();
			cdl.await();
			Thread.sleep(5000);
			t.interrupt();
			assertEquals(1, s.l.size());
			assertEquals((int) location.get().getLatitude(), lat.intValue());
			assertEquals((int) location.get().getLongitude(), lon.intValue());
			t.join();
		}
	}
}
