package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview.navigation;

import junit.framework.TestCase;
import android.graphics.PointF;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.TileKey;

public class TileKeyTestCase extends TestCase {
	public void testEquals() {
		assertTrue((new TileKey(0, 0, 0)).equals(new TileKey(0, 0, 0)));
		assertTrue((new TileKey(1, 2, 3)).equals(new TileKey(1, 2, 3)));

		assertFalse((new TileKey(5, 6, 7).equals(new TileKey(6, 6, 7))));
		assertFalse((new TileKey(5, 6, 7).equals(new TileKey(5, 7, 7))));
		assertFalse((new TileKey(5, 6, 7).equals(new TileKey(5, 6, 8))));

		assertFalse((new TileKey(5, 6, 7).equals(new TileKey(7, 6, 5))));

		assertFalse((new TileKey(5, 6, 7).equals(1)));
	}

	void assertDistance(float expected, float actual, float maxDistance) {
		if (Math.abs((double) expected - (double) actual) > Math
				.abs((double) maxDistance)) {
			fail("assertDistance failed: |(" + expected + ") - (" + actual + ")| < |" + maxDistance + "|");
		}
	}

	public void testGetOffsetPixels() {
		PointF o;
		TileKey tk;
		tk = (new TileKey(0, 0, 0));
		o = tk.getOffsetPixels(new LatLng(0, 0));
		float d = 0.001f;
		assertDistance(0f, o.x, d);
		assertDistance(0f, o.y, d);

		tk = new TileKey(0, 0, 1);
		o = tk.getOffsetPixels(new LatLng(0, 0));
		assertDistance(-128f, o.x, d);
		assertDistance(-128f, o.y, d);

		tk = new TileKey(0, 0, 2);
		o = tk.getOffsetPixels(new LatLng(0, 0));
		assertDistance(-384f, o.x, d);
		assertDistance(-384f, o.y, d);
	}
}
