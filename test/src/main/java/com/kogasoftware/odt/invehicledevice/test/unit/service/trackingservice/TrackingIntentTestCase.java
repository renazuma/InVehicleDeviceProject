package com.kogasoftware.odt.invehicledevice.test.unit.service.trackingservice;

import junit.framework.TestCase;
import android.location.Location;

import com.kogasoftware.odt.invehicledevice.service.trackingservice.TrackingIntent;

public class TrackingIntentTestCase extends TestCase {
	public void test() {
		Location l = new Location("");
		l.setLatitude(100.0);
		l.setLongitude(90.0);
		TrackingIntent ti1 = new TrackingIntent(2, l);
		TrackingIntent ti2 = new TrackingIntent(ti1);

		assertEquals(TrackingIntent.ACTION_TRACKING, ti1.getAction());
		assertEquals(TrackingIntent.ACTION_TRACKING, ti2.getAction());

		assertEquals(l, ti1.getLocation().get());
		assertEquals(l, ti2.getLocation().get());
		assertEquals(2, ti1.getSatellitesCount().intValue());
		assertEquals(2, ti2.getSatellitesCount().intValue());

		ti1.setSatellitesCount(3);
		assertEquals(3, ti1.getSatellitesCount().intValue());
		assertEquals(2, ti2.getSatellitesCount().intValue());
		
		TrackingIntent ti3 = new TrackingIntent(1);
		assertFalse(ti3.getLocation().isPresent());
	}
}
