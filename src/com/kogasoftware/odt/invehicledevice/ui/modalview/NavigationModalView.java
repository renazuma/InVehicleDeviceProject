package com.kogasoftware.odt.invehicledevice.ui.modalview;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.CommonLogicLoadCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.MapZoomLevelChangedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.ui.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.NavigationRenderer;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.TilePipeline;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.invehicledevice.logic.Status;

public class NavigationModalView extends ModalView {
	public static class ShowEvent {
	}

	private static final String TAG = NavigationModalView.class.getSimpleName();
	private volatile Integer zoomLevel = 12;
	private final Button zoomInButton;
	private final Button zoomOutButton;
	private final ToggleButton autoZoomButton;
	private final TilePipeline tilePipeline;

	private WeakReference<GLSurfaceView> glSurfaceViewWeakReference = new WeakReference<GLSurfaceView>(
			null);
	private WeakReference<NavigationRenderer> navigationRendererWeakReference = new WeakReference<NavigationRenderer>(
			null);

	@Subscribe
	public void updateZoomButtons(MapZoomLevelChangedEvent e) {
		zoomLevel = e.zoomLevel;
		updateZoomButtons();
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

		//次の乗降場表示
		TextView titleTextView = (TextView) findViewById(R.id.next_platform_text_view);
		CommonLogic commonLogic = getCommonLogic();
		List<OperationSchedule> operationSchedules = commonLogic
				.getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			commonLogic.postEvent(new EnterFinishPhaseEvent());
			titleTextView.setText("");
		}

		OperationSchedule operationSchedule = operationSchedules.get(0);

		String titleTextFormat = "";
		String timeTextFormat = "";
		Date displayDate = new Date();
		switch (commonLogic.getPhase()) {
		case DRIVE:
			titleTextFormat = getResources().getString(R.string.next_platform_is_html);
			timeTextFormat = getResources().getString(R.string.platform_arrival_time);
			displayDate = operationSchedule.getArrivalEstimate();
			break;
		default:
			titleTextFormat = getResources().getString(R.string.now_platform_is_html);
			timeTextFormat = getResources().getString(R.string.platform_depature_time);
			displayDate = operationSchedule.getDepartureEstimate();
			break;
		}

		if (operationSchedules.size() > 0) {
			OperationSchedule nowOperationSchedule = operationSchedules.get(0);
			for (Platform platform : nowOperationSchedule.getPlatform()
					.asSet()) {
				titleTextView.setText(Html.fromHtml(String.format(
						titleTextFormat,
						platform.getName())));
			}
		} else {
			titleTextView.setText("");
		}

		//到着時刻表示
		TextView  platformArrivalTimeTextView = (TextView) findViewById(R.id.platform_arrival_time_view);
		DateFormat dateFormat = new SimpleDateFormat(timeTextFormat);

		platformArrivalTimeTextView.setText(dateFormat.format(displayDate));

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
