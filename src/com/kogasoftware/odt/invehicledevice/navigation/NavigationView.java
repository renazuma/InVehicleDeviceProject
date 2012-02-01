package com.kogasoftware.odt.invehicledevice.navigation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.kogasoftware.odt.invehicledevice.LogTag;

public class NavigationView extends FrameLayout {
	private static final String T = LogTag.get(NavigationView.class);
	private final MapSynchronizer bitmapSynchronizer = MapSynchronizer
			.getInstance();
	private final GLSurfaceView glSurfaceView;
	private final MapViewRedirector mapViewRedirector;
	private final MapView mapView;
	private final MapRenderer mapRenderer;
	private final OrientationSensor orientationSensor;
	private final MapOnTouchListener mapOnTouchListener;
	private final LocationManager locationManager;

	public MapView getMapView() {
		return mapView;
	}

	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		mapView = new MapView(getContext(),
				"0_ZIi_adDM8WHxCX0OJTfcXhHO8jOsYOjLF7xow");
		mapOnTouchListener = new MapOnTouchListener(mapView);
		glSurfaceView = new GLSurfaceView(getContext());
		mapViewRedirector = new MapViewRedirector(getContext(), null);
		mapView.setOnTouchListener(mapOnTouchListener);
		mapRenderer = new MapRenderer(getContext(), bitmapSynchronizer, mapView);

		glSurfaceView.setOnTouchListener(mapOnTouchListener);
		glSurfaceView.setRenderer(mapRenderer);

		mapViewRedirector.init(bitmapSynchronizer, mapView);
		mapViewRedirector.addView(mapView, MapRenderer.MAP_TEXTURE_WIDTH,
				MapRenderer.MAP_TEXTURE_HEIGHT);

		orientationSensor = new LegacyOrientationSensor(getContext()) {
			Double lastOrientation = 0.0;

			@Override
			void onOrientationChanged(Double orientation) {
				Double fixedOrientation = Utility.getNearestRadian(
						lastOrientation, orientation);
				mapRenderer.setOrientation(fixedOrientation);
				mapOnTouchListener.onOrientationChanged(fixedOrientation);
				lastOrientation = fixedOrientation;
			}
		};
		orientationSensor.create();

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 0, new LocationListener() {
					@Override
					public void onLocationChanged(Location location) {
						Double latitude = location.getLatitude();
						Double longitude = location.getLongitude();
						GeoPoint newCenter = new GeoPoint(
								(int) (latitude * 1E6), (int) (longitude * 1E6));
						mapView.getController().animateTo(newCenter);
					}

					@Override
					public void onProviderDisabled(String provider) {
						Log.i(T, "onProviderDisabled(\"" + provider + "\")");
					}

					@Override
					public void onProviderEnabled(String provider) {
						Log.i(T, "onProviderEnabled(\"" + provider + "\")");
					}

					@Override
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
						Log.i(T, "onStatusChanged(\"" + provider + "\", "
								+ status + ", " + extras + ")");
					}
				});

		addView(glSurfaceView, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT));
		addView(mapViewRedirector, new FrameLayout.LayoutParams(512, 512));
	}

	@Override
	protected void finalize() {
		orientationSensor.destroy();
	}
}
