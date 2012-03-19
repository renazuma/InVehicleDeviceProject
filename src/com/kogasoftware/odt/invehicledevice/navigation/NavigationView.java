package com.kogasoftware.odt.invehicledevice.navigation;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.kogasoftware.odt.invehicledevice.modal.ModalCloseButton;

public class NavigationView extends FrameLayout {
	private static final String TAG = NavigationView.class.getSimpleName();
	private final MapView mapView;
	private final MapViewRedirector mapViewRedirector;
	private final LocationManager locationManager;
	private final NavigationViewLocationListener locationListener;
	private final MapSynchronizer mapSynchronizer;
	private final OrientationSensor orientationSensor;
	private final MapRenderer mapRenderer;
	private final Button closeButton;
	private final Button zoomInButton;
	private final Button zoomOutButton;

	private WeakReference<GLSurfaceView> glSurfaceViewWeakReference;
	private WeakReference<MapOnTouchListener> mapOnTouchListenerWeakReference;

	static class NavigationViewLocationListener implements LocationListener {
		private static final String TAG = NavigationViewLocationListener.class
				.getSimpleName();
		private final MapOnTouchListener mapOnTouchListener;
		private final MapView mapView;

		public NavigationViewLocationListener(
				MapOnTouchListener mapOnTouchListener, MapView mapView) {
			this.mapOnTouchListener = mapOnTouchListener;
			this.mapView = mapView;
		}

		@Override
		public void onLocationChanged(Location location) {
			Double latitude = location.getLatitude();
			Double longitude = location.getLongitude();
			GeoPoint newCenter = new GeoPoint((int) (latitude * 1E6),
					(int) (longitude * 1E6));
			mapOnTouchListener.updateGeoPoint(newCenter);
			mapView.getController().animateTo(newCenter);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d(TAG, "onProviderDisabled:" + provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d(TAG, "onProviderEnabled:" + provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.d(TAG, "onStatusChanged: " + provider + " " + status + " "
					+ extras);
		}
	}

	public MapView getMapView() {
		return mapView;
	}

	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mapView = new MapView(context,
				"0_ZIi_adDM8WHxCX0OJTfcXhHO8jOsYOjLF7xow");
		mapView.setClickable(true);
		mapSynchronizer = new MapSynchronizer();
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		MapOnTouchListener mapOnTouchListener = new MapOnTouchListener(mapView);
		mapView.setOnTouchListener(mapOnTouchListener);

		mapViewRedirector = new MapViewRedirector(context, null);
		mapViewRedirector.init(mapSynchronizer, mapView);

		mapRenderer = new MapRenderer(context.getResources(), mapSynchronizer,
				mapView);

		orientationSensor = new LegacyOrientationSensor(context) {
			private Double lastOrientation = 0.0;

			@Override
			public void onOrientationChanged(Double orientation) {
				Double fixedOrientation = Utility.getNearestRadian(
						lastOrientation, orientation);
				mapRenderer.setOrientation(fixedOrientation);
				MapOnTouchListener mapOnTouchListener = mapOnTouchListenerWeakReference
						.get();
				if (mapOnTouchListener != null) {
					mapOnTouchListener.onOrientationChanged(fixedOrientation);
				}
				lastOrientation = fixedOrientation;
				// Log.v(TAG, TAG + "," + orientation + "," + fixedOrientation);
			}
		};
		locationListener = new NavigationViewLocationListener(
				mapOnTouchListener, mapView);
		glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(null);
		mapOnTouchListenerWeakReference = new WeakReference<MapOnTouchListener>(
				mapOnTouchListener);

		closeButton = new ModalCloseButton(getContext(), null);
		closeButton.setText("閉じる");

		zoomInButton = new Button(getContext());
		zoomInButton.setText("拡大");
		zoomInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mapView.getController().zoomIn();
			}
		});

		zoomOutButton = new Button(getContext());
		zoomOutButton.setText("縮小");
		zoomOutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mapView.getController().zoomOut();
			}
		});
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				2000, 0, locationListener);
		mapSynchronizer.create();
		orientationSensor.create();

		GLSurfaceView glSurfaceView = new GLSurfaceView(getContext());
		glSurfaceView.setRenderer(mapRenderer);

		MapOnTouchListener mapOnTouchListener = mapOnTouchListenerWeakReference
				.get();
		if (mapOnTouchListener != null) {
			glSurfaceView.setOnTouchListener(mapOnTouchListener);
		}

		// ICSのGLSurfaceView.GLThreadがその親ViewをメンバmParentに保存する。
		// そのため、Activity再構築などのタイミングで1/10程度の確率で循環参照でリークすることがある。
		// それを防ぐために参照を極力減らしたFrameLayoutを間にはさむ
		{
			FrameLayout icsLeakAvoidanceFrameLayout = new FrameLayout(
					getContext());
			addView(icsLeakAvoidanceFrameLayout,
					new NavigationView.LayoutParams(
							NavigationView.LayoutParams.FILL_PARENT,
							NavigationView.LayoutParams.FILL_PARENT));
			icsLeakAvoidanceFrameLayout.addView(glSurfaceView,
					new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.FILL_PARENT,
							FrameLayout.LayoutParams.FILL_PARENT));
		}

		mapViewRedirector.addView(mapView, new MapViewRedirector.LayoutParams(
				MapRenderer.MAP_TEXTURE_WIDTH, MapRenderer.MAP_TEXTURE_HEIGHT));
		addView(mapViewRedirector, new NavigationView.LayoutParams(
				MapRenderer.MAP_TEXTURE_WIDTH, MapRenderer.MAP_TEXTURE_HEIGHT));

		LinearLayout buttons = new LinearLayout(getContext());

		buttons.addView(closeButton, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		buttons.addView(zoomInButton, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		buttons.addView(zoomOutButton, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1));

		addView(buttons, new NavigationView.LayoutParams(
				NavigationView.LayoutParams.FILL_PARENT,
				NavigationView.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM));

		glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
				glSurfaceView);
		mapView.getController().animateTo(new GeoPoint(35899045, 139928656));
		mapView.getController().setZoom(15);
	}

	@Override
	protected void onDetachedFromWindow() {
		mapSynchronizer.destroy();
		orientationSensor.destroy();
		locationManager.removeUpdates(locationListener);
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
