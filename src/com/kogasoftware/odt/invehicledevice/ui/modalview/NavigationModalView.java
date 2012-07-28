package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.NavigationRenderer;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.TilePipeline;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class NavigationModalView extends ModalView implements
		InVehicleDeviceService.OnResumeActivityListener,
		InVehicleDeviceService.OnPauseActivityListener,
		InVehicleDeviceService.OnChangeLocationListener,
		InVehicleDeviceService.OnChangeOrientationListener,
		InVehicleDeviceService.OnEnterPhaseListener,
		InVehicleDeviceService.OnMergeUpdatedOperationScheduleListener,
		NavigationRenderer.OnChangeMapZoomLevelListener {
	private static final String TAG = NavigationModalView.class.getSimpleName();
	private static final Integer GPS_ALERT_FLASH_MILLIS = 1000;
	private static final Integer GPS_EXPIRE_MILLIS = 20 * 1000;
	private static final Integer LOCATION_EXPIRE_MILLIS = 20 * 1000;
	private volatile Integer zoomLevel = 12;
	private final Button zoomInButton;
	private final Button zoomOutButton;
	private final LinearLayout gpsAlertLayout;
	private final TextView gpsAlertTextView;
	private final TextView gpsSatellitesTextView;
	private final ToggleButton autoZoomButton;
	private final TilePipeline tilePipeline;
	private final PlatformMemoModalView platformMemoModalView;
	private final Button platformMemoButton;
	private final Handler handler = new Handler();
	private final Runnable gpsAlert = new Runnable() {
		@Override
		public void run() {
			Date now = new Date();
			if (lastGpsUpdated.getTime() + GPS_EXPIRE_MILLIS > now
					.getTime()) {
				gpsAlertLayout.setVisibility(INVISIBLE);
				handler.postDelayed(this, GPS_EXPIRE_MILLIS);
				return;
			}
			gpsAlertLayout.setVisibility(VISIBLE);
			if (numSatellites < 4) {
				gpsSatellitesTextView.setText(String.format(
					getContext().getString(R.string.gps_satellites_format),
					numSatellites));
			} else {
				gpsSatellitesTextView.setText("(計算中)");
			}
			gpsAlertTextView
					.setVisibility(gpsAlertTextView.getVisibility() == VISIBLE ? INVISIBLE
							: VISIBLE);
			handler.postDelayed(this, GPS_ALERT_FLASH_MILLIS);
		}
	};
	private Date lastGpsUpdated = new Date(0);
	private Integer numSatellites = 0;
	private WeakReference<GLSurfaceView> glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
			null);
	private WeakReference<NavigationRenderer> navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
			null);

	public NavigationModalView(Context context, InVehicleDeviceService service, final PlatformMemoModalView platformMemoModalView) {
		super(context, service);
		this.platformMemoModalView = platformMemoModalView;
		setContentView(R.layout.navigation_modal_view);
		setCloseOnClick(R.id.navigation_close_button);

		tilePipeline = new TilePipeline(service);

		zoomInButton = (Button) findViewById(R.id.navigation_zoom_in_button);
		zoomInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				zoomIn();
			}
		});

		zoomOutButton = (Button) findViewById(R.id.navigation_zoom_out_button);
		zoomOutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				zoomOut();
			}
		});

		autoZoomButton = (ToggleButton) findViewById(R.id.navigation_auto_zoom_button);
		autoZoomButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						setAutoZoom(isChecked);
					}
				});
		platformMemoButton = (Button) findViewById(R.id.navigation_platform_memo_button);
		platformMemoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				platformMemoModalView.show();
			}
		});
		
		autoZoomButton.setChecked(true);
		gpsAlertLayout = (LinearLayout) findViewById(R.id.gps_alert_layout);
		gpsAlertTextView = (TextView) findViewById(R.id.gps_alert_text_view);
		gpsSatellitesTextView = (TextView) findViewById(R.id.gps_satellites_text_view);

		setAutoZoom(true);
		updateZoomButtons();
		onResumeActivity();
	}

	@Override
	public void hide() {
		setVisibility(GONE);
		GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
		if (glSurfaceView != null) {
			// glSurfaceView.setVisibility(INVISIBLE);
			glSurfaceView.setVisibility(GONE);
		}
		// TODO:アニメーション
		// super.hide();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		handler.post(gpsAlert);

		service.addOnResumeActivityListener(this);
		service.addOnPauseActivityListener(this);
		service.addOnChangeLocationListener(this);
		service.addOnChangeOrientationListener(this);
		service.addOnEnterPhaseListener(this);
		service.addOnMergeUpdatedOperationScheduleListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		service.removeOnResumeActivityListener(this);
		service.removeOnPauseActivityListener(this);
		service.removeOnChangeLocationListener(this);
		service.removeOnChangeOrientationListener(this);
		service.removeOnEnterPhaseListener(this);
		service.removeOnMergeUpdatedOperationScheduleListener(this);
		
		tilePipeline.onExit();

		handler.removeCallbacks(gpsAlert);
	}

	@Override
	public void onChangeLocation(Location location,
			Optional<GpsStatus> optionalGpsStatus) {
		if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			lastGpsUpdated = new Date(location.getTime());
		}
		for (GpsStatus gpsStatus : optionalGpsStatus.asSet()) {
			numSatellites = Iterables.size(gpsStatus.getSatellites());
		}
		Date now = new Date();
		if (location.getTime() + LOCATION_EXPIRE_MILLIS < now.getTime()) {
			return;
		}
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.updateLocation();
		}
	}

	@Override
	public void onChangeMapZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
		updateZoomButtons();
	}

	@Override
	public void onChangeOrientation(Double orientationDegree) {
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.changeOrientation(orientationDegree);
		}
	}

	protected void updatePlatform() {
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.updatePlatform();
		}
		
		Integer platformMemoVisibility = GONE;
		for (OperationSchedule operationSchedule : service
				.getCurrentOperationSchedule().asSet()) {
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				if (!platform.getMemo().isEmpty()) {
					platformMemoVisibility = VISIBLE;
				}
			}
		}
		platformMemoButton.setVisibility(platformMemoVisibility);
	}

	@Override
	public void onEnterDrivePhase() {
		updatePlatform();
	}

	@Override
	public void onEnterFinishPhase() {
		updatePlatform();
	}

	@Override
	public void onEnterPlatformPhase() {
		updatePlatform();
	}

	@Override
	public void onPauseActivity() {
		Log.i(TAG, "onPauseActivity()");
		GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
		if (glSurfaceView != null) {
			glSurfaceView.onPause();
		}

		FrameLayout navigationSurfaceParent = (FrameLayout) findViewById(R.id.navigation_surface_parent);
		navigationSurfaceParent.removeAllViews();
	}

	@Override
	public void onResumeActivity() {
		Log.i(TAG, "onResumeActivity()");
		// ICSのGLSurfaceView.GLThreadがその親ViewをメンバmParentに保存する。
		// そのため、Activity再構築などのタイミングで1/10程度の確率で循環参照でリークすることがある。
		// それを防ぐために参照を極力減らしたFrameLayoutを間にはさむ
		NavigationRenderer navigationRenderer = new NavigationRenderer(service,
				tilePipeline, new Handler());
		navigationRenderer.addOnChangeMapZoomLevelListener(this);
		navigationRenderer.setZoomLevel(zoomLevel);
		navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
				navigationRenderer);
		FrameLayout icsLeakAvoidanceFrameLayout = new FrameLayout(getContext()
				.getApplicationContext());
		GLSurfaceView glSurfaceView = new GLSurfaceView(getContext());
		glSurfaceView.setRenderer(navigationRenderer);
		FrameLayout navigationSurfaceParent = (FrameLayout) findViewById(R.id.navigation_surface_parent);
		navigationSurfaceParent.addView(icsLeakAvoidanceFrameLayout,
				new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.FILL_PARENT,
						FrameLayout.LayoutParams.FILL_PARENT));
		icsLeakAvoidanceFrameLayout.addView(glSurfaceView,
				new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.FILL_PARENT,
						FrameLayout.LayoutParams.FILL_PARENT));
		glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
				glSurfaceView);

		glSurfaceView.onResume();
	}

	protected void setAutoZoom(Boolean autoZoom) {
		autoZoomButton.setTextColor(autoZoom ? Color.BLACK : Color.GRAY);
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.setAutoZoomLevel(autoZoom);
		}
	}

	protected void setZoomLevel(Integer newZoomLevel) {
		zoomLevel = newZoomLevel;
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.setAutoZoomLevel(false);
			navigationRenderer.setZoomLevel(zoomLevel);
		}

		if (autoZoomButton.isChecked()) {
			autoZoomButton.setChecked(false);
		}

		updateZoomButtons();
	}

	@Override
	public void show() {
		setVisibility(VISIBLE);
		GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
		if (glSurfaceView != null) {
			glSurfaceView.setVisibility(VISIBLE);
		}
		// TODO:アニメーション
		// super.show();

		// 次の乗降場表示
		TextView titleTextView = (TextView) findViewById(R.id.next_platform_text_view);
		List<OperationSchedule> operationSchedules = service
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			service.enterFinishPhase();
			titleTextView.setText("");
			return;
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);

		String titleTextFormat = "";
		String timeTextFormat = "";
		Optional<Date> displayDate = Optional.absent();
		switch (service.getPhase()) {
		case DRIVE:
			titleTextFormat = getResources().getString(
					R.string.next_platform_is_html);
			timeTextFormat = getResources().getString(
					R.string.platform_arrival_time);
			displayDate = operationSchedule.getArrivalEstimate();
			break;
		default:
			titleTextFormat = getResources().getString(
					R.string.now_platform_is_html);
			timeTextFormat = getResources().getString(
					R.string.platform_depature_time);
			displayDate = operationSchedule.getDepartureEstimate();
			break;
		}

		if (operationSchedules.size() > 0) {
			OperationSchedule nowOperationSchedule = operationSchedules.get(0);
			for (Platform platform : nowOperationSchedule.getPlatform().asSet()) {
				titleTextView.setText(Html.fromHtml(String.format(
						titleTextFormat, platform.getName())));
			}
		} else {
			titleTextView.setText("");
		}

		// 到着時刻表示
		TextView platformArrivalTimeTextView = (TextView) findViewById(R.id.platform_arrival_time_view);
		DateFormat dateFormat = new SimpleDateFormat(timeTextFormat);

		if (displayDate.isPresent()) {
			platformArrivalTimeTextView.setText(dateFormat.format(displayDate.get()));
		}
		updatePlatform();
	}

	protected void updateZoomButtons() {
		if (zoomLevel <= NavigationRenderer.MIN_ZOOM_LEVEL) {
			zoomLevel = NavigationRenderer.MIN_ZOOM_LEVEL;
			zoomOutButton.setTextColor(Color.GRAY);
			zoomOutButton.setEnabled(false);
		} else {
			zoomOutButton.setTextColor(Color.BLACK);
			zoomOutButton.setEnabled(true);
		}

		if (zoomLevel >= NavigationRenderer.MAX_ZOOM_LEVEL) {
			zoomLevel = NavigationRenderer.MAX_ZOOM_LEVEL;
			zoomInButton.setTextColor(Color.GRAY);
			zoomInButton.setEnabled(false);
		} else {
			zoomInButton.setTextColor(Color.BLACK);
			zoomInButton.setEnabled(true);
		}
	}

	protected void zoomIn() {
		setZoomLevel(zoomLevel + 1);
	}

	protected void zoomOut() {
		setZoomLevel(zoomLevel - 1);
	}

	@Override
	public void onMergeUpdatedOperationSchedule(
			List<VehicleNotification> triggerVehicleNotifications) {
		updatePlatform();
	}
}
