package com.kogasoftware.odt.invehicledevice.ui.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.EventDispatcher.OnChangeLocationListener;
import com.kogasoftware.odt.invehicledevice.ui.fragment.PlatformNavigationFragment.State;

public class PlatformNavigationFragment extends ApplicationFragment<State>
		implements OnChangeLocationListener {
	protected static final String TAG = PlatformNavigationFragment.class
			.getSimpleName();

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

	private TextView titleTextView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return onCreateViewHelper(inflater, container,
				R.layout.platform_navigation_fragment,
				R.id.platform_navigation_close_button);
	}

	public String getGoogleMapsHtml() throws IOException {
		Charset charset = Charsets.UTF_8;
		StringBuilder parameter = new StringBuilder();
		String language = getString(R.string.google_maps_language);
		if (!language.isEmpty()) {
			parameter.append("&amp;language=" + language);
		}
		String region = getString(R.string.google_maps_region);
		if (!region.isEmpty()) {
			parameter.append("&amp;region=" + region);
		}
		Closer closer = Closer.create();
		try {
			InputStream inputStream = closer.register(getActivity().getAssets()
					.open("google_maps.html"));
			String html = new String(ByteStreams.toByteArray(inputStream),
					charset);
			return html.replace("{GOOGLE_MAPS_URL_PARAMETERS}", parameter);
		} catch (Throwable e) {
			throw closer.rethrow(e);
		} finally {
			closer.close();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		titleTextView = (TextView) getView().findViewById(
				R.id.platform_navigation_modal_title_text_view);
		getService().getEventDispatcher().addOnChangeLocationListener(this);
		for (final Platform platform : getState().getOperationSchedule()
				.getPlatform().asSet()) {
			titleTextView.setText(platform.getName());
			final WebView webView = (WebView) getView().findViewById(
					R.id.platform_navigation_web_view);
			final AtomicInteger once = new AtomicInteger(1);
			final String baseUrl = "https://koko-bus.com/";
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String finishedUrl) {
					super.onPageFinished(view, finishedUrl);
					if (!finishedUrl.startsWith(baseUrl)
							|| !once.compareAndSet(1, 0)) {
						return;
					}
					String script = String.format(Locale.US,
							"javascript:initialize(%f, %f, %f, %f)", platform
									.getLatitude().doubleValue(), platform
									.getLongitude().doubleValue(), getState()
									.getLatitude(), getState().getLongitude());
					webView.loadUrl(script);
				}
			});
			webView.getSettings().setJavaScriptEnabled(true);

			new AsyncTask<Void, Void, String>() {
				@Override
				protected String doInBackground(Void... args) {
					try {
						return getGoogleMapsHtml();
					} catch (IOException e) {
						Log.w(TAG, e);
						cancel(false);
						return null;
					}
				}

				@Override
				protected void onPostExecute(String html) {
					webView.loadDataWithBaseURL(baseUrl, html, "text/html",
							"UTF-8", null);
				}
			}.execute();
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

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "platform: " + Objects.firstNonNull(titleTextView.getText(), "(None)"));
	}
}
