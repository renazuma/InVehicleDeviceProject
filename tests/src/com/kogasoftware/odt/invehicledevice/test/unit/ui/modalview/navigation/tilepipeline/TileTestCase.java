package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview.navigation.tilepipeline;

import junit.framework.TestCase;
import android.graphics.PointF;

import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.Tile;

public class TileTestCase extends TestCase {
	public void testF() {
		float f0 = 256;

		float d1 = f0 / (1 << 1);
		float d2 = f0 / (1 << 2);
		float d3 = f0 / (1 << 3);
		float d17 = f0 / (1 << 17);
		float d20 = f0 / (1 << 20);

		float m1 = d1 * (1 << 1);
		float m2 = d2 * (1 << 2);
		float m3 = d3 * (1 << 3);
		float m17 = d17 * (1 << 17);
		float m20 = d20 * (1 << 20);
		float m20a = (float) ((2.4414062E-4) * (1 << 20));

		assertEquals(256f, m1);
		assertEquals(256f, m2);
		assertEquals(256f, m3);
		assertEquals(256f, m17);
		assertEquals(256f, m20);
		assertEquals(256f, m20a);
	}

	public void testEquals() {
		assertTrue((new Tile(0, 0, 0)).equals(new Tile(0, 0, 0)));
		assertTrue((new Tile(1, 2, 3)).equals(new Tile(1, 2, 3)));

		assertFalse((new Tile(5, 6, 7).equals(new Tile(6, 6, 7))));
		assertFalse((new Tile(5, 6, 7).equals(new Tile(5, 7, 7))));
		assertFalse((new Tile(5, 6, 7).equals(new Tile(5, 6, 8))));

		assertFalse((new Tile(5, 6, 7).equals(new Tile(7, 6, 5))));

		assertFalse((new Tile(5, 6, 7).equals(1)));
	}

	static void assertDistance(float expected, float actual, float maxDistance) {
		if (Math.abs((double) expected - (double) actual) > Math
				.abs((double) maxDistance)) {
			fail("assertDistance failed: |(" + expected + ") - (" + actual
					+ ")| < |" + maxDistance + "|");
		}
	}

	void assertPointF(PointF expected, PointF actual) {
		if (expected.x != actual.x || expected.y != actual.y) {
			fail("assertPoint failed: (" + expected.x + ", " + expected.y
					+ ") != (" + actual.x + "," + actual.y + ")");
		}
	}

	public void testGetCenterPixel() {
		Tile tk;

		tk = new Tile(0, 0, 1);
		assertPointF(new PointF(-64, 64), tk.getCenterPixel());

		tk = new Tile(1, 0, 1);
		assertPointF(new PointF(64, 64), tk.getCenterPixel());

		tk = new Tile(1, 1, 1);
		assertPointF(new PointF(64, -64), tk.getCenterPixel());
	}

	// public void xtestGetOffsetPixels() {
	// PointF o;
	// Tile tk;
	// tk = new Tile(0, 0, 0);
	// o = tk.xgetOffsetPixels(new LatLng(0, 0));
	// float d = 0.001f;
	// assertDistance(0f, o.x, d);
	// assertDistance(0f, o.y, d);
	//
	// tk = new Tile(0, 0, 1);
	// o = tk.xgetOffsetPixels(new LatLng(0, 0));
	// assertDistance(-128f, o.x, d);
	// assertDistance(-128f, o.y, d);
	//
	// tk = new Tile(0, 0, 2);
	// o = tk.xgetOffsetPixels(new LatLng(0, 0));
	// assertDistance(-384f, o.x, d);
	// assertDistance(-384f, o.y, d);
	// }
}