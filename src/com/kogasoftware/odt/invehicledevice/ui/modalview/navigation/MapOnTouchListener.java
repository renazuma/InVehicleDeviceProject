package com.kogasoftware.odt.invehicledevice.navigation;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MapOnTouchListener implements OnTouchListener {
	private static final Double MOTION_SMOOTHER_LATENCY = 80.0;
	private final WeakReference<MapView> mapViewWeakReference; // TODO:
	// GLSurfaceView.GLThreadリーク対策のリファクタリング
	private Double orientation = 0d;
	private Double lastX = 0d;
	private Double lastY = 0d;
	private final MotionSmoother latitudeMotionSmoother = new LazyMotionSmoother(
			MOTION_SMOOTHER_LATENCY);
	private final MotionSmoother longitudeMotionSmoother = new LazyMotionSmoother(
			MOTION_SMOOTHER_LATENCY);
	private final Handler motionHandler = new Handler();
	private Boolean motionRunnableRunning = false;
	private final Runnable motionRunnable = new Runnable() {
		@Override
		public void run() {
			MapView mapView = mapViewWeakReference.get();
			if (mapView == null) {
				return;
			}
			Integer lat = latitudeMotionSmoother.getSmoothMotion().intValue();
			Integer lon = longitudeMotionSmoother.getSmoothMotion().intValue();
			// logger.error("lat=" + lat + ", lon=" + lon);
			GeoPoint center = mapView.getMapCenter();
			GeoPoint target = new GeoPoint(lat, lon);
			if (center.equals(target)) {
				motionRunnableRunning = false;
				return;
			}
			mapView.getController().setCenter(target);
			motionHandler.post(this);
		}
	};

	private int lastLatitude = 0;

	private int lastLongitude = 0;

	public MapOnTouchListener(MapView mapView) {
		this.mapViewWeakReference = new WeakReference<MapView>(mapView);
		updateGeoPoint(mapView.getMapCenter());
	}

	public void onOrientationChanged(Double orientation) {
		this.orientation = orientation;
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		MapView mapView = mapViewWeakReference.get();
		if (mapView == null) {
			return true;
		}

		Double x = (double) motionEvent.getX();
		Double y = (double) motionEvent.getY();

		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			lastX = x;
			lastY = y;
			GeoPoint center = mapView.getMapCenter();
			lastLatitude = center.getLatitudeE6();
			lastLongitude = center.getLongitudeE6();
			return true;
		}

		Double dx = lastX - x;
		Double dy = lastY - y;

		lastX = x;
		lastY = y;
		Double distance = Math.sqrt(dx * dx + dy * dy);
		Double extraOrientation = Math.atan2(dy, dx);
		dx = distance * Math.cos(extraOrientation - orientation + Math.PI / 2);
		dy = distance * Math.sin(extraOrientation - orientation + Math.PI / 2);
		lastLatitude += dx * 20; // TODO
		lastLongitude += dy * 20; // TODO
		latitudeMotionSmoother.addMotion((double) lastLatitude);
		longitudeMotionSmoother.addMotion((double) lastLongitude);
		if (!motionRunnableRunning) {
			motionRunnableRunning = true;
			motionHandler.post(motionRunnable);
		}
		return true;
	}

	public void updateGeoPoint(GeoPoint center) {
		latitudeMotionSmoother.addMotion((double) center.getLatitudeE6());
		longitudeMotionSmoother.addMotion((double) center.getLongitudeE6());
	}
}
