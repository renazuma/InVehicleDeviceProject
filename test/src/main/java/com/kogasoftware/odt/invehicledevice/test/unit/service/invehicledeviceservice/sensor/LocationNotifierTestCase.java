package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice.sensor;

import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.mockito.Matchers;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKeys;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.logic.ServiceUnitStatusLogLogic;
import com.kogasoftware.odt.invehicledevice.service.trackingservice.LocationNotifier;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class LocationNotifierTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	Integer RESTART_TIMEOUT = 20000;
	InVehicleDeviceService s;
	PowerManager pm;
	LocationManager lm;
	WakeLock wl;
	LocationNotifier ln;
	SharedPreferences sp;
	HandlerThread ht;
	ServiceUnitStatusLogLogic susll;
	EventDispatcher ed;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		lm = mock(LocationManager.class);
		wl = mock(WakeLock.class);
		pm = mock(PowerManager.class);
		ed = mock(EventDispatcher.class);
		s = mock(InVehicleDeviceService.class);
		when(pm.newWakeLock(anyInt(), anyString())).thenReturn(wl);
		when(s.getSystemService(Context.LOCATION_SERVICE)).thenReturn(lm);
		when(s.getSystemService(Context.POWER_SERVICE)).thenReturn(pm);
		when(s.getEventDispatcher()).thenReturn(ed);
		susll = new ServiceUnitStatusLogLogic(s);
		sp = PreferenceManager.getDefaultSharedPreferences(getInstrumentation()
				.getContext());
		SharedPreferences.Editor e = sp.edit();
		e.putInt(SharedPreferencesKeys.LOCATION_RECEIVE_RESTART_TIMEOUT,
				RESTART_TIMEOUT);
		e.apply();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			if (ht != null) {
				ht.interrupt();
			}
		} finally {
			super.tearDown();
		}
	}

	public void test_タイムアウト内に正確な位置を取得出来なかった場合LocationManagerに再登録()
			throws Exception {
		Integer rt = (int) (RESTART_TIMEOUT * 1.05);
		Location l = new Location(LocationManager.GPS_PROVIDER);
		l.setAccuracy(1);

		ht = new HandlerThread("") {
			@Override
			protected void onLooperPrepared() {
				ln = new LocationNotifier(null, 500);
				ln.start();
			}
		};
		ht.start();
		Thread.sleep(rt / 2);
		//verify(ed, times(0)).dispatchChangeLocation(Matchers.<Location> any(),
		//		Matchers.<Optional<GpsStatus>> any());
		verify(wl, times(0)).release();
		verify(wl, times(1)).acquire();
		verify(lm, times(0)).removeUpdates(Matchers.<LocationListener> any());
		verify(lm, times(1)).requestLocationUpdates(
				Matchers.eq(LocationManager.GPS_PROVIDER), anyLong(),
				anyFloat(), Matchers.<LocationListener> any());

		Thread.sleep(rt / 2);
		//verify(ed, times(0)).dispatchChangeLocation(Matchers.<Location> any(),
		//		Matchers.<Optional<GpsStatus>> any());
		verify(wl, times(1)).release();
		verify(wl, times(2)).acquire();
		verify(lm, times(1)).removeUpdates(Matchers.<LocationListener> any());
		verify(lm, times(2)).requestLocationUpdates(
				Matchers.eq(LocationManager.GPS_PROVIDER), anyLong(),
				anyFloat(), Matchers.<LocationListener> any());

		Thread.sleep(rt / 2);
		l.setTime(new Date().getTime());
		ln.onLocationChanged(l);

		Thread.sleep(rt / 2);
		//verify(ed, times(1)).dispatchChangeLocation(Matchers.<Location> any(),
		//		Matchers.<Optional<GpsStatus>> any());
		verify(wl, times(1)).release();
		verify(wl, times(2)).acquire();
		verify(lm, times(1)).removeUpdates(Matchers.<LocationListener> any());
		verify(lm, times(2)).requestLocationUpdates(
				Matchers.eq(LocationManager.GPS_PROVIDER), anyLong(),
				anyFloat(), Matchers.<LocationListener> any());

		l.setTime(new Date().getTime());
		ln.onLocationChanged(l);
		Thread.sleep(rt / 2);
		//verify(ed, times(2)).dispatchChangeLocation(Matchers.<Location> any(),
		//		Matchers.<Optional<GpsStatus>> any());
		verify(wl, times(1)).release();
		verify(wl, times(2)).acquire();
		verify(lm, times(1)).removeUpdates(Matchers.<LocationListener> any());
		verify(lm, times(2)).requestLocationUpdates(
				Matchers.eq(LocationManager.GPS_PROVIDER), anyLong(),
				anyFloat(), Matchers.<LocationListener> any());

		Thread.sleep(rt);
		//verify(ed, times(2)).dispatchChangeLocation(Matchers.<Location> any(),
		//		Matchers.<Optional<GpsStatus>> any());
		verify(wl, times(2)).release();
		verify(wl, times(3)).acquire();
		verify(lm, times(2)).removeUpdates(Matchers.<LocationListener> any());
		verify(lm, times(3)).requestLocationUpdates(
				Matchers.eq(LocationManager.GPS_PROVIDER), anyLong(),
				anyFloat(), Matchers.<LocationListener> any());

		ht.quit();
	}
}
