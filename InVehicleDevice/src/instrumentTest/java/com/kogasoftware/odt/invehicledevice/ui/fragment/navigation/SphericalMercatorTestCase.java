package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation;

import junit.framework.TestCase;
import android.util.Log;

import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.SphericalMercator;

public class SphericalMercatorTestCase extends TestCase {
	public void test() {
		double lat = 35.658517; // 渋谷
		Log.w("SMTC", lat + " -> " + SphericalMercator.lat2y(lat));
		// for (double i = -90; i < 90; i += 0.5) {
		// Log.w("SMTC", i + " -> " + SphericalMercator.lat2y(i));
		// }
	}
}
