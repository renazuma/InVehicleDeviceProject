package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.Serializable;
import java.math.BigDecimal;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher.OnChangeLocationListener;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PlatformNavigationFragment.State;

public class PlatformNavigationFragment extends ApplicationFragment<State>
		implements OnChangeLocationListener {
	@SuppressWarnings("serial")
	protected static class State implements Serializable {
		private final OperationSchedule operationSchedule;
		private final BigDecimal latitude;
		private final BigDecimal longitude;

		public BigDecimal getLatitude() {
			return latitude;
		}

		public BigDecimal getLongitude() {
			return longitude;
		}

		public State(OperationSchedule operationSchedule, BigDecimal latitude,
				BigDecimal longitude) {
			this.operationSchedule = operationSchedule;
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public OperationSchedule getOperationSchedule() {
			return operationSchedule;
		}
	}

	public static Fragment newInstance(OperationSchedule operationSchedule,
			BigDecimal latitude, BigDecimal longitude) {
		return newInstance(new PlatformNavigationFragment(), new State(
				operationSchedule, latitude, longitude));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return onCreateViewHelper(inflater, container,
				R.layout.platform_navigation_fragment,
				R.id.platform_navigation_close_button);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getService().getEventDispatcher().addOnChangeLocationListener(this);
		for (final Platform platform : getState().getOperationSchedule()
				.getPlatform().asSet()) {
			TextView titleTextView = (TextView) getView().findViewById(
					R.id.platform_navigation_modal_title_text_view);
			titleTextView.setText(platform.getName());
			final WebView webView = (WebView) getView().findViewById(
					R.id.platform_navigation_web_view);
			final String url = "file:///android_asset/google_maps.html";
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String finishedUrl) {
					super.onPageFinished(view, finishedUrl);
					if (url.equals(finishedUrl)) { // これは本当にこれで良いのか?
						webView.loadUrl(String.format(
								"javascript:initialize(%f, %f, %f, %f)",
								platform.getLatitude().doubleValue(), platform
										.getLongitude().doubleValue(),
								getState().getLatitude(), getState()
										.getLongitude()));
					}
				}
			});
			webView.getSettings().setJavaScriptEnabled(true);
			webView.loadUrl(url);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getService().getEventDispatcher().removeOnChangeLocationListener(this);
	}

	@Override
	public void onChangeLocation(Optional<Location> optionalLocation,
			Optional<Integer> satelliteCount) {
		for (Location location : optionalLocation.asSet()) {
			setState(new State(getState().getOperationSchedule(),
					BigDecimal.valueOf(location.getLatitude()),
					BigDecimal.valueOf(location.getLongitude())));
		}
	}
}
