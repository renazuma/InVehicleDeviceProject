package com.kogasoftware.odt.invehicledevice.navigation;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.google.android.maps.MapView;
import com.kogasoftware.odt.invehicledevice.LogTag;

class MyLocationListener implements LocationListener {
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}

class MyRenderer implements GLSurfaceView.Renderer {
	@Override
	public void onDrawFrame(GL10 gl) {
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}
}

public class NavigationView extends FrameLayout {
	private static final String T = LogTag.get(NavigationView.class);
	// private final MapViewRedirector mapViewRedirector;
	private final MapView mapView;
	private final LocationManager locationManager;
	private final MyLocationListener listener;

	// private final MapRenderer mapRenderer;
	// private final OrientationSensor orientationSensor;
	// private final MapOnTouchListener mapOnTouchListener;

	public MapView getMapView() {
		return mapView;
	}

	final private GLSurfaceView glSurfaceView;

	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);

		MapSynchronizer bitmapSynchronizer = new MapSynchronizer();

		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		mapView = new MapView(context,
				"0_ZIi_adDM8WHxCX0OJTfcXhHO8jOsYOjLF7xow");
		// mapOnTouchListener = new MapOnTouchListener(mapView);
		glSurfaceView = new GLSurfaceView(context);
		// mapViewRedirector = new MapViewRedirector(context, null);
		// mapView.setOnTouchListener(mapOnTouchListener);
		// MapRenderer mapRenderer = new MapRenderer(context,
		// bitmapSynchronizer,
		// mapView);
		// glSurfaceView.setRenderer(mapRenderer);
		glSurfaceView.setRenderer(new MyRenderer());

		// mapRenderer = new MapRenderer(context.getApplicationContext(),
		// bitmapSynchronizer, mapView);
		//
		// glSurfaceView.setOnTouchListener(mapOnTouchListener);
		//
		// mapViewRedirector.init(bitmapSynchronizer, mapView);
		// mapViewRedirector.addView(mapView, MapRenderer.MAP_TEXTURE_WIDTH,
		// MapRenderer.MAP_TEXTURE_HEIGHT);
		//
		// orientationSensor = new LegacyOrientationSensor(getContext()) {
		// Double lastOrientation = 0.0;
		//
		// @Override
		// void onOrientationChanged(Double orientation) {
		// Double fixedOrientation = Utility.getNearestRadian(
		// lastOrientation, orientation);
		// mapRenderer.setOrientation(fixedOrientation);
		// mapOnTouchListener.onOrientationChanged(fixedOrientation);
		// lastOrientation = fixedOrientation;
		// }
		// };
		// orientationSensor.create();
		//
		listener = new MyLocationListener();

		// addView(glSurfaceView, new FrameLayout.LayoutParams(
		// FrameLayout.LayoutParams.FILL_PARENT,
		// FrameLayout.LayoutParams.FILL_PARENT));
		// addView(mapViewRedirector, new FrameLayout.LayoutParams(512, 512));
		// addView(mapViewRedirector, new FrameLayout.LayoutParams(512, 512));
		addView(glSurfaceView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 0, listener);

	}

	@Override
	protected void onDetachedFromWindow() {
		locationManager.removeUpdates(listener);
		// removeView(mapViewRedirector);
		// removeView(glSurfaceView);
		// orientationSensor.destroy();
		// removeAllViews();
		// this.removeView(mapViewRedirector);
		super.onDetachedFromWindow();
	}

	public void onResumeActivity() {
		glSurfaceView.onResume();
	}

	public void onPauseActivity() {
		glSurfaceView.onPause();
	}
}
