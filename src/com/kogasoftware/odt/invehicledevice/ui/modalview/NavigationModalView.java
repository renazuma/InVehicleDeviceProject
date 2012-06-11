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
				// mapView.getController().zoomIn();
			}
		});

		zoomOutButton = (Button) findViewById(R.id.navigation_zoom_out_button);
		zoomOutButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// mapView.getController().zoomOut();
			}
		});

		// ICSのGLSurfaceView.GLThreadがその親ViewをメンバmParentに保存する。
		// そのため、Activity再構築などのタイミングで1/10程度の確率で循環参照でリークすることがある。
		// それを防ぐために参照を極力減らしたFrameLayoutを間にはさむ
		{
			NavigationRenderer navigationRenderer = new NavigationRenderer(
					getContext().getResources());
			navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
					navigationRenderer);
			FrameLayout icsLeakAvoidanceFrameLayout = new FrameLayout(
					getContext().getApplicationContext());
			GLSurfaceView glSurfaceView = new GLSurfaceView(getContext());
			glSurfaceView.setRenderer(navigationRenderer);
			addView(icsLeakAvoidanceFrameLayout, 0,
					new NavigationModalView.LayoutParams(
							NavigationModalView.LayoutParams.FILL_PARENT,
							NavigationModalView.LayoutParams.FILL_PARENT));
			icsLeakAvoidanceFrameLayout.addView(glSurfaceView,
					new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.FILL_PARENT,
							FrameLayout.LayoutParams.FILL_PARENT));
			glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
					glSurfaceView);
		}
	}

	public void onPauseActivity() {
		GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
		if (glSurfaceView != null) {
			glSurfaceView.onPause();
		}
	}

	public void onResumeActivity() {
		GLSurfaceView glSurfaceView = glSurfaceViewWeakReference.get();
		if (glSurfaceView != null) {
			glSurfaceView.onResume();
		}
	}

	@Override
	public void setCommonLogic(CommonLogicLoadCompleteEvent event) {
		super.setCommonLogic(event);
		NavigationRenderer navigationRenderer = navigationRendererWeakReference
				.get();
		if (navigationRenderer != null) {
			getCommonLogic().registerEventListener(navigationRenderer);
		}
	}

	@Subscribe
	public void show(ShowEvent event) {
		super.show();
	}
}
