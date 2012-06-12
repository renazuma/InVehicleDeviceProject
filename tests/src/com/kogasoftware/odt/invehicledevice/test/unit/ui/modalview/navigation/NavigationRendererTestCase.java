package com.kogasoftware.odt.invehicledevice.test.unit.ui.modalview.navigation;

import junit.framework.TestCase;
import android.graphics.PointF;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.NavigationRenderer;

public class NavigationRendererTestCase extends TestCase {
	static void assertDistance(float expected, float actual, float maxDistance) {
		TileKeyTestCase.assertDistance(expected, actual, maxDistance);
	}

	public void testGetPont() {
		PointF p;
		float d = 0.001f;

		// zoom0
		p = NavigationRenderer.getPoint(new LatLng(0, 0), 0);
		assertDistance(0f, p.x, d);
		assertDistance(0f, p.y, d);

		p = NavigationRenderer.getPoint(new LatLng(0, -180), 0);
		assertDistance(-128f, p.x, d);
		assertDistance(0f, p.y, d);

		p = NavigationRenderer.getPoint(new LatLng(0, 180), 0);
		assertDistance(128f, p.x, d);
		assertDistance(0f, p.y, d);

		p = NavigationRenderer.getPoint(new LatLng(85, 180), 0);
		assertDistance(128f, p.x, d);
		assertDistance(128f, p.y, 1);

		// zoom1
		p = NavigationRenderer.getPoint(new LatLng(0, -90), 1);
		assertDistance(-128f, p.x, d);
		assertDistance(0f, p.y, d);

		// zoom2
		p = NavigationRenderer.getPoint(new LatLng(0, 0), 2);
		assertDistance(0f, p.x, d);
		assertDistance(0f, p.y, d);

		p = NavigationRenderer.getPoint(new LatLng(0, -90), 2);
		assertDistance(-256f, p.x, d);
		assertDistance(0f, p.y, d);

		p = NavigationRenderer.getPoint(new LatLng(-85, -90), 2);
		assertDistance(-256f, p.x, d);
		assertDistance(-512f, p.y, 2);

	}
}
