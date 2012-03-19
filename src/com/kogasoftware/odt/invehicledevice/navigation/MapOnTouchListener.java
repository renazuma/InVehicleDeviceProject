package com.kogasoftware.odt.invehicledevice.navigation;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class MapOnTouchListener implements OnTouchListener {
	private final MapView mapView;
	private Double orientation = 0d;
	private Double lastX = 0d;
	private Double lastY = 0d;
	private Double downX = 0d;
	private Double downY = 0d;
	private final Double MOTION_SMOOTHER_LATENCY = 80.0;
	private final MotionSmoother latitudeMotionSmoother = new LazyMotionSmoother(
			MOTION_SMOOTHER_LATENCY);
	private final MotionSmoother longitudeMotionSmoother = new LazyMotionSmoother(
			MOTION_SMOOTHER_LATENCY);
	private final Handler motionHandler = new Handler();
	private Boolean motionRunnableRunning = false;
	private final Runnable motionRunnable = new Runnable() {
		@Override
		public void run() {
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

	public void updateGeoPoint(GeoPoint center) {
		latitudeMotionSmoother.addMotion((double) center.getLatitudeE6());
		longitudeMotionSmoother.addMotion((double) center.getLongitudeE6());
	}

	public MapOnTouchListener(MapView mapView) {
		this.mapView = mapView;
		updateGeoPoint(mapView.getMapCenter());
	}

	public void onOrientationChanged(Double orientation) {
		this.orientation = orientation;
	}

	private int lastLatitude = 0;
	private int lastLongitude = 0;

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		Double x = (double) motionEvent.getX();
		Double y = (double) motionEvent.getY();

		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
			lastX = x;
			lastY = y;
			downX = x;
			downY = y;
			GeoPoint center = mapView.getMapCenter();
			lastLatitude = center.getLatitudeE6();
			lastLongitude = center.getLongitudeE6();
			return true;
		}

		Double dx = 0.0;
		Double dy = 0.0;

		// if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
		// dx = downX - x;
		// dy = downY - y;
		// } else {
		dx = lastX - x;
		dy = lastY - y;
		// }

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
}
