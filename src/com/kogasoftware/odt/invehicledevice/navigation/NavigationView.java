package com.kogasoftware.odt.invehicledevice.navigation;

import java.lang.ref.WeakReference;

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

class MyOrientationSensor extends OrientationSensor {
	public MyOrientationSensor(Context context, MapRenderer mapRenderer,
			MapOnTouchListener mapOnTouchListener) {
		super(context);
		this.mapRenderer = mapRenderer;
		this.mapOnTouchListener = mapOnTouchListener;
	}

	private Double lastOrientation = 0.0;
	private final MapRenderer mapRenderer;
	private final MapOnTouchListener mapOnTouchListener;

	@Override
	public void onOrientationChanged(Double orientation) {
		Double fixedOrientation = Utility.getNearestRadian(lastOrientation,
				orientation);
		mapRenderer.setOrientation(fixedOrientation);
		mapOnTouchListener.onOrientationChanged(fixedOrientation);
		lastOrientation = fixedOrientation;
	}
}

public class NavigationView extends FrameLayout {
	private static final String T = LogTag.get(NavigationView.class);
	private final MapView mapView;

	private final MapViewRedirector mapViewRedirector;
	private final LocationManager locationManager;
	private final MyLocationListener listener;

	private final MapSynchronizer mapSynchronizer;
	private final OrientationSensor orientationSensor;
	private final MapOnTouchListener mapOnTouchListener;
	private final MapRenderer mapRenderer;

	private WeakReference<GLSurfaceView> glSurfaceViewWeakReference;

	public MapView getMapView() {
		return mapView;
	}

	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mapView = new MapView(context,
				"0_ZIi_adDM8WHxCX0OJTfcXhHO8jOsYOjLF7xow");
		mapSynchronizer = new MapSynchronizer();
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		mapOnTouchListener = new MapOnTouchListener(mapView);
		mapViewRedirector = new MapViewRedirector(context, null);
		mapView.setOnTouchListener(mapOnTouchListener);

		mapViewRedirector.init(mapSynchronizer, mapView);

		mapRenderer = new MapRenderer(context.getResources(), mapSynchronizer,
				mapView);
		orientationSensor = new MyOrientationSensor(getContext(), mapRenderer,
				mapOnTouchListener);
		listener = new MyLocationListener();
		glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(null);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 0, listener);
		mapSynchronizer.create();
		orientationSensor.create();
		addView(mapViewRedirector, new NavigationView.LayoutParams(512, 512));

		GLSurfaceView glSurfaceView = new GLSurfaceView(getContext());
		// glSurfaceView.setRenderer(new MyRenderer());
		glSurfaceView.setRenderer(mapRenderer);

		// ICSのGLSurfaceView.GLThreadがその親Viewをメンバに保存するため、循環参照でリークすることある。
		// それを防ぐために参照を極力減らしたFrameLayoutを間にはさむ
		FrameLayout icsLeakAvoidanceFrameLayout = new FrameLayout(getContext());
		addView(icsLeakAvoidanceFrameLayout, new NavigationView.LayoutParams(
				NavigationView.LayoutParams.FILL_PARENT,
				NavigationView.LayoutParams.FILL_PARENT));
		icsLeakAvoidanceFrameLayout.addView(glSurfaceView,
				new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.FILL_PARENT,
						FrameLayout.LayoutParams.FILL_PARENT));
		addView(mapView, new NavigationView.LayoutParams(512, 512));

		glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
				glSurfaceView);
	}

	@Override
	protected void onDetachedFromWindow() {
		mapSynchronizer.destroy();
		orientationSensor.destroy();
		locationManager.removeUpdates(listener);
		glSurfaceViewWeakReference.clear();
		super.onDetachedFromWindow();
		removeAllViews();
	}

	public void onResumeActivity() {
		GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
		if (glSurfaceView != null) {
			glSurfaceView.onResume();
		}
	}

	public void onPauseActivity() {
		GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
		if (glSurfaceView != null) {
			glSurfaceView.onPause();
		}
	}
}
