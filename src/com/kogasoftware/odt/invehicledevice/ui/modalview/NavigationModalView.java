package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.lang.ref.WeakReference;

import android.content.Context;
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

	private final Button zoomInButton;
	private final Button zoomOutButton;

	private WeakReference<GLSurfaceView> glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
			null);
	private WeakReference<NavigationRenderer> navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
			null);

	public NavigationModalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContentView(R.layout.navigation_modal_view);
		setCloseOnClick(R.id.navigation_close_button);

		zoomInButton = (Button) findViewById(R.id.navigation_zoom_in_button);
		zoomInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				NavigationRenderer navigationRenderer = navigationRendererWeakReference
						.get();
				if (navigationRenderer != null) {
					navigationRenderer.zoomIn();
				}
			}
		});

		zoomOutButton = (Button) findViewById(R.id.navigation_zoom_out_button);
		zoomOutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				NavigationRenderer navigationRenderer = navigationRendererWeakReference
						.get();
				if (navigationRenderer != null) {
					navigationRenderer.zoomOut();
				}
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
			navigationRenderer.onResumeActivity();
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
