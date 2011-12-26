package com.kogasoftware.odt.invehicledevice;

import org.apache.log4j.Logger;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.kogasoftware.odt.invehicledevice.Hazard.Severity;
import com.kogasoftware.odt.invehicledevice.Hazard.Type;

public class MainActivity extends MapActivity implements LocationListener {

	private static final Logger logger = Logger.getLogger(MainActivity.class);

	private final MapSynchronizer bitmapSynchronizer = MapSynchronizer
			.getInstance();
	private OrientationSensor orientationSensor = new NullOrientationSensor(
			this);

	private LocationManager locationManager = null;
	private MapView mapView = null;
	private GLSurfaceView glSurfaceView = null;
	private MapViewRedirector mapViewRedirector = null;
	private OnMeasureDetectableLinerLayout mainLayout = null;
	private MapOnTouchListener mapOnTouchListener = null;
	private MapRenderer mapRenderer = null;
	private ScreenForceUnlocker screenForceUnlocker = null;

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @Override アクティビティ生成時に呼び出される
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// タイトルバーを消す
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 通知領域を消す
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 初期状態ではIMEを隠す
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// スクリーンロックを解除
		screenForceUnlocker = new ScreenForceUnlocker(this);

		screenForceUnlocker.start();

		setContentView(R.layout.main);

		glSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
		mapViewRedirector = (MapViewRedirector) findViewById(R.id.mapViewRedirector);
		mainLayout = (OnMeasureDetectableLinerLayout) findViewById(R.id.mainLayout);
		mapView = new MapView(this, "0QOVGMzvn4BYfqySsdW0AnjCQ4-k-_Yk_iQFVpQ");
		mapOnTouchListener = new MapOnTouchListener(mapView, this);
		Button backButton = (Button) findViewById(R.id.backButton);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.this.finish();
			}
		});

		final MapController mapController = mapView.getController();
		final Button zoomInButton = (Button) findViewById(R.id.zoomInButton);
		final Button zoomOutButton = (Button) findViewById(R.id.zoomOutButton);

		zoomInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mapController.zoomIn()) {
					zoomInButton.setEnabled(false);
				}
				zoomOutButton.setEnabled(true);
			}
		});

		zoomOutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!mapController.zoomOut()) {
					zoomOutButton.setEnabled(false);
				}
				zoomInButton.setEnabled(true);
			}
		});

		mapView.setOnTouchListener(mapOnTouchListener);
		mapRenderer = new MapRenderer(this, bitmapSynchronizer, mapView);

		glSurfaceView.setOnTouchListener(mapOnTouchListener);
		glSurfaceView.setRenderer(mapRenderer);

		orientationSensor = new LegacyOrientationSensor(this) {
			Double lastOrientation = 0.0;

			@Override
			void onOrientationChanged(Double orientation) {
				Double fixedOrientation = Utility.getNearestRadian(
						lastOrientation, orientation);
				// logger.info(String.format("last=%.3f next=%.3f fixedNext=%.3f",
				// lastOrientation, orientation, fixedOrientation));
				lastOrientation = fixedOrientation;
				mapRenderer.setOrientation(fixedOrientation);
				mapOnTouchListener.onOrientationChanged(fixedOrientation);
			}
		};

		mapViewRedirector.init(bitmapSynchronizer, mapView);
		mapViewRedirector.addView(mapView, Property.MAP_TEXTURE_WIDTH,
				Property.MAP_TEXTURE_HEIGHT);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		mainLayout
				.setOnMeasureListener(new OnMeasureDetectableLinerLayout.OnMeasureListener() {
					@Override
					public void onMeasure(int widthMeasureSpec,
							int heightMeasureSpec) {
						onMeasureMainLayout(widthMeasureSpec, heightMeasureSpec);
					}
				});

		// 優先度
		ArrayAdapter<Severity> hazardSeverityAdapter = new ArrayAdapter<Severity>(
				this, android.R.layout.simple_spinner_item,
				Severity.values());
		hazardSeverityAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// 種類
		ArrayAdapter<Type> hazardTypeAdapter = new ArrayAdapter<Type>(
				this, android.R.layout.simple_spinner_item, Type.values());
		hazardTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		screenForceUnlocker.stop();
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

	private int currentWidthMeasureSpec = 0;
	private int currentHeightMeasureSpec = 0;

	public void onMeasureMainLayout(int widthMeasureSpec, int heightMeasureSpec) {
		if (currentWidthMeasureSpec == widthMeasureSpec
				&& currentHeightMeasureSpec == heightMeasureSpec) {
			return;
		}
		currentWidthMeasureSpec = widthMeasureSpec;
		currentHeightMeasureSpec = heightMeasureSpec;
		Integer width = MeasureSpec.getSize(widthMeasureSpec);
		Integer height = MeasureSpec.getSize(heightMeasureSpec);
		mapRenderer.setLayout(width, height, width, height);
	}

	public static enum MenuItemId {
		TOGGLE_HAZARD_RECEIVE, TOGGLE_SUB_VIEW,
	};

	public static enum SubViewStatus {
		HIDDEN, HAZARD_SEND,
	};

	@Override
	protected void onPause() {
		super.onPause();
		orientationSensor.destroy();
		locationManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		logger.debug("Provider disabled:" + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		logger.debug("Provider enabled:" + provider);
	}

	@Override
	protected void onResume() {
		super.onResume();

		GeoPoint newCenter = new GeoPoint(35901364, 139936004);
		MapController mapController = mapView.getController();
		mapController.setZoom(16);
		mapController.animateTo(newCenter);

		orientationSensor.create();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000,
				0, this);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}
}
