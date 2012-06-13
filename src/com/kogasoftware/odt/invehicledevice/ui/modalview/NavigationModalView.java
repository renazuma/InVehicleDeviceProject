package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.NavigationRenderer;

public class NavigationModalView extends ModalView {
	public static class ShowEvent {
	}

	private Integer zoomLevel = 13;
	private final Button zoomInButton;
	private final Button zoomOutButton;

	private WeakReference<GLSurfaceView> glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
			null);
	private WeakReference<NavigationRenderer> navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
			null);

	public void setZoomLevel(Integer newZoomLevel) {
		if (newZoomLevel <= 9) {
			newZoomLevel = 9;
			zoomOutButton.setTextColor(Color.GRAY);
			zoomOutButton.setEnabled(false);
		} else {
			zoomOutButton.setTextColor(Color.BLACK);
			zoomOutButton.setEnabled(true);
		}

		if (newZoomLevel >= 17) {
			newZoomLevel = 17;
			zoomInButton.setTextColor(Color.GRAY);
			zoomInButton.setEnabled(false);
		} else {
			zoomInButton.setTextColor(Color.BLACK);
			zoomInButton.setEnabled(true);
		}

		zoomLevel = newZoomLevel;
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			navigationRenderer.setZoomLevel(zoomLevel);
		}

	}

	public void zoomIn() {
		setZoomLevel(zoomLevel + 2);
	}

	public void zoomOut() {
		setZoomLevel(zoomLevel - 2);
	}

	public NavigationModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.navigation_modal_view);
		setCloseOnClick(R.id.navigation_close_button);

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
				getContext());
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
