package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.WeakHashMap;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.empty.EmptyRunnable;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Phase;
import com.kogasoftware.odt.invehicledevice.ui.BatteryAlerter;
import com.kogasoftware.odt.invehicledevice.ui.ViewDisabler;
import com.kogasoftware.odt.invehicledevice.ui.fragment.NavigationFragment.State;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.NavigationRenderer;
import com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline.TilePipeline;

public class NavigationFragment extends ApplicationFragment<State> implements
		EventDispatcher.OnChangeLocationListener,
		EventDispatcher.OnChangeOrientationListener,
		EventDispatcher.OnUpdatePhaseListener,
		NavigationRenderer.OnChangeMapZoomLevelListener {
	private static final String TAG = NavigationFragment.class.getSimpleName();
	private static final Integer GPS_ALERT_FLASH_MILLIS = 1000;
	private static final Integer GPS_EXPIRE_MILLIS = 20 * 1000;
	private static final Integer LOCATION_EXPIRE_MILLIS = 20 * 1000;
	private static final WeakHashMap<Activity, Boolean> ACTIVITY_FIRST_RESUME = new WeakHashMap<Activity, Boolean>();

	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final List<OperationSchedule> operationSchedules;
		private final Phase phase;
		private final Double orientationDegree;
		private final BigDecimal initialLatitude;
		private final BigDecimal initialLongitude;

		public State(Phase phase, List<OperationSchedule> operationSchedules,
				Double orientationDegree, BigDecimal initialLatitude, BigDecimal initialLongitude) {
			this.phase = phase;
			this.operationSchedules = Lists.newArrayList(operationSchedules);
			this.orientationDegree = orientationDegree;
			this.initialLatitude = initialLatitude;
			this.initialLongitude = initialLongitude;
		}

		public List<OperationSchedule> getOperationSchedules() {
			return Lists.newArrayList(operationSchedules);
		}

		public Phase getPhase() {
			return phase;
		}

		public Double getOrientationDegree() {
			return orientationDegree;
		}

		public BigDecimal getInitialLatitude() {
			return initialLatitude;
		}

		public BigDecimal getInitialLongitude() {
			return initialLongitude;
		}
	}

	public static NavigationFragment newInstance(Phase phase,
			List<OperationSchedule> operationSchedules, ServiceUnitStatusLog serviceUnitStatusLog) {
		return newInstance(new NavigationFragment(), new State(phase,
				operationSchedules, serviceUnitStatusLog.getOrientation().or(0).doubleValue(), serviceUnitStatusLog.getLatitude(), serviceUnitStatusLog.getLongitude()));
	}

	private final Handler handler = new Handler();
	private final Runnable gpsAlert = new Runnable() {
		@Override
		public void run() {
			Date now = new Date();
			if (lastGpsUpdated.getTime() + GPS_EXPIRE_MILLIS > now.getTime()) {
				gpsAlertLayout.setVisibility(View.INVISIBLE);
				handler.postDelayed(this, GPS_EXPIRE_MILLIS);
				return;
			}
			gpsAlertLayout.setVisibility(View.VISIBLE);
			if (numSatellites < 4) {
				gpsSatellitesTextView.setText(String.format(
						getString(R.string.gps_satellites_format),
						numSatellites));
			} else {
				gpsSatellitesTextView.setText("(計算中)");
			}
			gpsAlertTextView
					.setVisibility(gpsAlertTextView.getVisibility() == View.VISIBLE ? View.INVISIBLE
							: View.VISIBLE);
			handler.postDelayed(this, GPS_ALERT_FLASH_MILLIS);
		}
	};

	public static class SurficeFlashMaskDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.navigation_fragment_loading_dialog);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			return dialog;
		}
	}

	public static class ExitDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Dialog dialog = new Dialog(getActivity());
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			View view = new View(getActivity());
			view.setVisibility(View.INVISIBLE);
			dialog.setContentView(view);
			dialog.getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			return dialog;
		}
	}

	private WeakReference<GLSurfaceView> glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
			null);
	private WeakReference<NavigationRenderer> navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
			null);
	private Date lastGpsUpdated = new Date(0);
	private Integer numSatellites = 0;
	private TilePipeline tilePipeline;
	private Button zoomInButton;
	private Button zoomOutButton;
	private LinearLayout gpsAlertLayout;
	private TextView gpsAlertTextView;
	private TextView gpsSatellitesTextView;
	private ToggleButton autoZoomButton;
	private Button platformMemoButton;
	private Runnable blinkBatteryAlert;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		tilePipeline = new TilePipeline(getService());

		View view = getView();
		view.setBackgroundColor(Color.WHITE);

		zoomInButton = (Button) view
				.findViewById(R.id.navigation_zoom_in_button);
		zoomInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				zoomIn();
			}
		});

		zoomOutButton = (Button) view
				.findViewById(R.id.navigation_zoom_out_button);
		zoomOutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				zoomOut();
			}
		});

		autoZoomButton = (ToggleButton) view
				.findViewById(R.id.navigation_auto_zoom_button);
		autoZoomButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						setAutoZoom(isChecked);
					}
				});

		platformMemoButton = (Button) view
				.findViewById(R.id.navigation_platform_memo_button);

		gpsAlertLayout = (LinearLayout) view
				.findViewById(R.id.gps_alert_layout);
		gpsAlertTextView = (TextView) view
				.findViewById(R.id.gps_alert_text_view);
		gpsSatellitesTextView = (TextView) view
				.findViewById(R.id.gps_satellites_text_view);

		blinkBatteryAlert = new EmptyRunnable();
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			blinkBatteryAlert = new BatteryAlerter(getActivity()
					.getApplicationContext(), handler,
					(ImageView) view.findViewById(R.id.battery_alert_image_view),
					fragmentManager);
		}

		getService().getEventDispatcher().addOnChangeLocationListener(this);
		getService().getEventDispatcher().addOnChangeOrientationListener(this);
		getService().getEventDispatcher().addOnUpdatePhaseListener(this);

		updateZoomButtons();
		updatePlatform();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return onCreateViewHelper(inflater, container,
				R.layout.navigation_fragment, R.id.navigation_close_button);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		getService().getEventDispatcher().removeOnChangeLocationListener(this);
		getService().getEventDispatcher().removeOnChangeOrientationListener(
				this);
		getService().getEventDispatcher().removeOnUpdatePhaseListener(this);

		tilePipeline.onExit();
	}

	@Override
	public void onChangeLocation(Location location,
			Optional<Integer> satelliteCount) {
		if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			lastGpsUpdated = new Date(location.getTime());
		}
		numSatellites = satelliteCount.or(0);
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
		getService().setMapZoomLevel(zoomLevel);
		updateZoomButtons();
	}

	@Override
	public void onChangeOrientation(Double orientationDegree) {
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.changeOrientation(orientationDegree);
		}
		setState(new State(getState().getPhase(), getState().getOperationSchedules(),
				orientationDegree, getState().getInitialLatitude(), getState().getInitialLongitude()));
	}

	protected void updatePlatform() {
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.updatePlatform(OperationSchedule
					.getCurrent(getState().getOperationSchedules()));
		}

		// 現在の乗降場メモ表示
		platformMemoButton.setVisibility(View.INVISIBLE);
		for (final OperationSchedule operationSchedule : OperationSchedule
				.getCurrent(getState().getOperationSchedules()).asSet()) {
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				if (!platform.getMemo().isEmpty()) {
					platformMemoButton.setVisibility(View.VISIBLE);
					platformMemoButton
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View view) {
									ViewDisabler.disable(view);
									for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
										setCustomAnimation(
												fragmentManager
														.beginTransaction())
												.add(R.id.modal_fragment_container,
														PlatformMemoFragment
																.newInstance(operationSchedule))
												.commitAllowingStateLoss();
									}
								}
							});
				}
			}
		}

		// 次の乗降場表示
		TextView titleTextView = (TextView) getView().findViewById(
				R.id.next_platform_text_view);
		titleTextView.setText("");
		for (OperationSchedule operationSchedule : ((getState().getPhase() == Phase.DRIVE) ? OperationSchedule
				.getCurrent(getState().getOperationSchedules())
				: OperationSchedule.getRelative(getState()
						.getOperationSchedules(), 1)).asSet()) {
			String titleTextFormat = "";
			String timeTextFormat = "";
			titleTextFormat = getResources().getString(
					R.string.next_platform_is_html);
			timeTextFormat = getResources().getString(
					R.string.platform_arrival_time);
			Optional<Date> displayDate = operationSchedule.getArrivalEstimate();
			for (Platform platform : operationSchedule.getPlatform().asSet()) {
				titleTextView.setText(Html.fromHtml(String.format(
						titleTextFormat, platform.getName())));
			}

			// 到着時刻表示
			TextView platformArrivalTimeTextView = (TextView) getView()
					.findViewById(R.id.platform_arrival_time_view);
			DateFormat dateFormat = new SimpleDateFormat(timeTextFormat);
			if (displayDate.isPresent()) {
				platformArrivalTimeTextView.setText(dateFormat
						.format(displayDate.get()));
			}
		}
	}

	@Override
	public void hide() {
		if (isRemoving()) {
			return;
		}
		ImageView mask = (ImageView) getView().findViewById(
				R.id.navigation_surface_black_flash_mask);
		mask.setVisibility(View.VISIBLE);
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			for (Bitmap bitmap : navigationRenderer.createBitmapAndPause()
					.asSet()) {
				mask.setImageBitmap(bitmap);
				getService().setLastMapBitmap(bitmap);
			}
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				removeGL();
				NavigationFragment.super.hide();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "onPause()");
		handler.removeCallbacks(gpsAlert);
		handler.removeCallbacks(blinkBatteryAlert);
		pauseGL();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume()");
		final DialogFragment dialogFragment = new SurficeFlashMaskDialogFragment();
		for (FragmentManager fragmentManager : getOptionalFragmentManager().asSet()) {
			dialogFragment.show(fragmentManager,
					SurficeFlashMaskDialogFragment.class.getSimpleName());
		}
		final ImageView mask = (ImageView) getView().findViewById(
				R.id.navigation_surface_black_flash_mask);
		for (Bitmap bitmap : getService().getLastMapBitmap().asSet()) {
			mask.setImageBitmap(bitmap);
		}
		mask.setVisibility(View.VISIBLE);
		handler.post(gpsAlert);
		handler.post(blinkBatteryAlert);
		Boolean first = false;
		synchronized (ACTIVITY_FIRST_RESUME) {
			Activity activity = getActivity();
			if (!ACTIVITY_FIRST_RESUME.containsKey(activity)) {
				ACTIVITY_FIRST_RESUME.put(activity, true);
				first = true;
			}
		}
		final Integer resumeGLDelay = first ? 800 : 300;
		final Integer dismissDialogDelay = first ? 1000 : 750;
		final Integer hideMaskDelay = first ? 750 : 500;
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				resumeGL();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (!isResumed()) {
							return;
						}
						mask.setVisibility(View.GONE);
					}
				}, hideMaskDelay);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						dialogFragment.dismissAllowingStateLoss();
					}
				}, dismissDialogDelay);
			}
		}, resumeGLDelay);
	}

	public void resumeGL() {
		if (!isResumed()) {
			return;
		}
		// ICSのGLSurfaceView.GLThreadがその親ViewをメンバmParentに保存する。
		// GLThreadが、Activity再構築などのタイミングで1/10程度の確率で循環参照でリークすることがある。
		// それを防ぐために参照を極力減らしたFrameLayoutを間にはさむ
		NavigationRenderer navigationRenderer = new NavigationRenderer(
				getService(), tilePipeline, new Handler(),
				OperationSchedule
						.getCurrent(getState().getOperationSchedules()),
				getState().getOrientationDegree(), getState().getInitialLatitude(), getState().getInitialLongitude());
		navigationRenderer.addOnChangeMapZoomLevelListener(this);
		navigationRenderer.setZoomLevel(getService().getMapZoomLevel());
		navigationRenderer.setAutoZoomLevel(getService().getMapAutoZoom());
		navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
				navigationRenderer);
		FrameLayout icsLeakAvoidanceFrameLayout = new FrameLayout(getActivity());
		GLSurfaceView glSurfaceView = new GLSurfaceView(getActivity()
				.getApplication());
		glSurfaceView.setRenderer(navigationRenderer);
		glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		FrameLayout navigationSurfaceParent = (FrameLayout) getView()
				.findViewById(R.id.navigation_surface_parent);
		navigationSurfaceParent.removeAllViews();
		navigationSurfaceParent.addView(icsLeakAvoidanceFrameLayout,
				new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						FrameLayout.LayoutParams.MATCH_PARENT));
		icsLeakAvoidanceFrameLayout.addView(glSurfaceView,
				new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						FrameLayout.LayoutParams.MATCH_PARENT));
		glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
				glSurfaceView);
		navigationRenderer.setGLSurfaceView(glSurfaceViewWeakReference);
		glSurfaceView.onResume();
		navigationRenderer.onResume();
		if (getService().getMapAutoZoom()) {
			autoZoomButton.setChecked(getService().getMapAutoZoom());
			setAutoZoom(getService().getMapAutoZoom());
		} else {
			setZoomLevel(getService().getMapZoomLevel());
		}
	}

	public void pauseGL() {
		if (!isResumed()) {
			return;
		}
		{
			GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
			if (glSurfaceView != null) {
				glSurfaceView.onPause();
			}
		}
		{
			NavigationRenderer navigationRenderer = navigationRendererWeakReference
					.get();
			if (navigationRenderer != null) {
				navigationRenderer.onPause();
			}
		}

		removeGL();

		glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(null);
		navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
				null);
	}

	private void removeGL() {
		View view = getView();
		if (view != null) {
			FrameLayout navigationSurfaceParent = (FrameLayout) view
					.findViewById(R.id.navigation_surface_parent);
			if (navigationSurfaceParent != null) {
				navigationSurfaceParent.removeAllViews();
			}
		}
	}

	protected void setAutoZoom(Boolean autoZoom) {
		getService().setMapAutoZoom(autoZoom);
		autoZoomButton.setTextColor(autoZoom ? Color.BLACK : Color.GRAY);
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.setAutoZoomLevel(autoZoom);
		}
	}

	protected void setZoomLevel(Integer newZoomLevel) {
		getService().setMapZoomLevel(newZoomLevel);
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.setAutoZoomLevel(false);
			navigationRenderer.setZoomLevel(newZoomLevel);
		}

		if (autoZoomButton.isChecked()) {
			autoZoomButton.setChecked(false);
		}

		updateZoomButtons();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	protected void updateZoomButtons() {
		Integer zoomLevel = getService().getMapZoomLevel();

		if (zoomLevel <= NavigationRenderer.MIN_ZOOM_LEVEL) {
			zoomOutButton.setTextColor(Color.GRAY);
			zoomOutButton.setEnabled(false);
		} else {
			zoomOutButton.setTextColor(Color.BLACK);
			zoomOutButton.setEnabled(true);
		}

		if (zoomLevel >= NavigationRenderer.MAX_ZOOM_LEVEL) {
			zoomInButton.setTextColor(Color.GRAY);
			zoomInButton.setEnabled(false);
		} else {
			zoomInButton.setTextColor(Color.BLACK);
			zoomInButton.setEnabled(true);
		}
	}

	protected void zoomIn() {
		setZoomLevel(getService().getMapZoomLevel() + 1);
	}

	protected void zoomOut() {
		setZoomLevel(getService().getMapZoomLevel() - 1);
	}

	@Override
	public void onUpdatePhase(Phase phase,
			List<OperationSchedule> operationSchedules,
			List<PassengerRecord> passengerRecords) {
		setState(new State(phase, operationSchedules, getState().getOrientationDegree(),
				getState().getInitialLatitude(), getState().getInitialLongitude()));
		updatePlatform();
	}
}
