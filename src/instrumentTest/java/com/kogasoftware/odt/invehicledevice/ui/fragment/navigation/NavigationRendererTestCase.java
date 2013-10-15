package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation;

import junit.framework.TestCase;
import android.graphics.PointF;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.tilepipeline.TileTestCase;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.NavigationRenderer;

public class NavigationRendererTestCase extends TestCase {
	static void assertDistance(float expected, float actual, float maxDistance) {
		TileTestCase.assertDistance(expected, actual, maxDistance);
	}

	public void testGetPoint() {
		PointF p;
		float d = 0.001f;

		p = NavigationRenderer.getPoint(new LatLng(0, 0));
		assertDistance(0f, p.x, d);
		assertDistance(0f, p.y, d);

		p = NavigationRenderer.getPoint(new LatLng(0, -180));
		assertDistance(-128f, p.x, d);
		assertDistance(0f, p.y, d);

		p = NavigationRenderer.getPoint(new LatLng(0, 180));
		assertDistance(128f, p.x, d);
		assertDistance(0f, p.y, d);

		p = NavigationRenderer.getPoint(new LatLng(85, 180));
		assertDistance(128f, p.x, d);
		assertDistance(128f, p.y, 1);

		p = NavigationRenderer.getPoint(new LatLng(0, -90));
		assertDistance(-64f, p.x, d);
		assertDistance(0f, p.y, d);

		p = NavigationRenderer.getPoint(new LatLng(-85, -90));
		assertDistance(-64f, p.x, d);
		assertDistance(-128f, p.y, 2);

	}
}
