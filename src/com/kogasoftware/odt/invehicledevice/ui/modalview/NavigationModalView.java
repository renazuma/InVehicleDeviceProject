package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.google.common.eventbus.Subscribe;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.LocationReceivedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.MapZoomLevelChangedEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.NavigationRenderer;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.TilePipeline;

public class NavigationModalView extends ModalView {
	public static class ShowEvent {
	}

	private static final String TAG = NavigationModalView.class.getSimpleName();

	private volatile Integer zoomLevel = 12;
	private final Button zoomInButton;
	private final Button zoomOutButton;
	private final ToggleButton autoZoomButton;
	private final TilePipeline tilePipeline;
	private double orientation = 0.0;
	private LatLng lastLatLng = new LatLng(0, 0);

	private WeakReference<GLSurfaceView> glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
			null);
	private WeakReference<NavigationRenderer> navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
			null);

	@Subscribe
	public void updateZoomButtons(MapZoomLevelChangedEvent e) {
		zoomLevel = e.zoomLevel;
		updateZoomButtons();
	}

	/**
	 * GPSを利用して方向を取得する
	 */
	@Subscribe
	public void changeOrientation(LocationReceivedEvent event) {
//		NavigationRenderer navigationRenderer = navigationRendererWeakReference
//				.get();
//		if (navigationRenderer == null) {
//			return;
//		}
//
//		ServiceUnitStatusLog serviceUnitStatusLog = getCommonLogic()
//				.getServiceUnitStatusLog();
//		double latitude = serviceUnitStatusLog.getLatitude().doubleValue();
//		double longitude = serviceUnitStatusLog.getLongitude().doubleValue();
//		if (latitude == 0 && longitude == 0) {
//			return;
//		}
//		if (lastLatLng.equals(new LatLng(0, 0))) {
//			// TODO:
//			Log.i(TAG, "changeLocation lastLatLng is uninitialized");
//			lastLatLng = new LatLng(latitude, longitude);
//			return;
//		}
//
//		LatLng latLng = new LatLng(latitude, longitude);
//		double distance = LatLngTool.distance(latLng, lastLatLng,
//				LengthUnit.METER);
//		Log.i(TAG, "changeLocation distance=" + distance);
//		if (distance <= 2) {
//			return;
//		}
//
//		PointF last = NavigationRenderer.getPoint(lastLatLng);
//		PointF current = NavigationRenderer.getPoint(latLng);
//		orientation = Math.atan2(current.y - last.y, current.x - last.x)
//				- Math.PI / 2;
//		Log.i(TAG, "changeOrientation last=(" + last.x + "," + last.y
//				+ ") current=(" + current.x + "," + current.y + ")");
//		lastLatLng = latLng;
//		navigationRenderer.changeOrientation(orientation);
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

	protected void setAutoZoom(Boolean autoZoom) {
		autoZoomButton.setTextColor(autoZoom ? Color.BLACK : Color.GRAY);
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.setAutoZoomLevel(autoZoom);
		}
	}

	protected void zoomIn() {
		setZoomLevel(zoomLevel + 1);
	}

	protected void zoomOut() {
		setZoomLevel(zoomLevel - 1);
	}

	public NavigationModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.navigation_modal_view);
		setCloseOnClick(R.id.navigation_close_button);

		tilePipeline = new TilePipeline(context);

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
		autoZoomButton.setChecked(true);
		setAutoZoom(true);
		updateZoomButtons();
	}

	public void onPauseActivity() {
		GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
		if (glSurfaceView != null) {
			glSurfaceView.onPause();
		}
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.onPauseActivity();
			getCommonLogic().unregisterEventListener(navigationRenderer);
		}

		FrameLayout navigationSurfaceParent = (FrameLayout) findViewById(R.id.navigation_surface_parent);
		navigationSurfaceParent.removeAllViews();
	}

	public void onResumeActivity() {
		// ICSのGLSurfaceView.GLThreadがその親ViewをメンバmParentに保存する。
		// そのため、Activity再構築などのタイミングで1/10程度の確率で循環参照でリークすることがある。
		// それを防ぐために参照を極力減らしたFrameLayoutを間にはさむ
		NavigationRenderer navigationRenderer = new NavigationRenderer(
				getContext(), tilePipeline);
		navigationRenderer.setZoomLevel(zoomLevel);
		navigationRenderer.changeOrientation(orientation);
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

		getCommonLogic().registerEventListener(navigationRenderer);
		navigationRenderer.setCommonLogic(getCommonLogic());
		navigationRenderer.onResumeActivity();
	}

	@Override
	public void setCommonLogic(CommonLogicLoadCompleteEvent event) {
		super.setCommonLogic(event);
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			getCommonLogic().registerEventListener(navigationRenderer);
			navigationRenderer.setCommonLogic(getCommonLogic());
		}
	}

	@Subscribe
	public void show(ShowEvent event) {
		show();
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
}
