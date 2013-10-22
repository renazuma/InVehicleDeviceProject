package com.kogasoftware.odt.invehicledevice.apiclient;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;

public class InVehicleDeviceApiClientFactory {
	private static final Object API_CLIENT_LOCK = new Object();
	private static final String TAG = InVehicleDeviceApiClientFactory.class
			.getSimpleName();
	private static Optional<InVehicleDeviceApiClient> apiClient = Optional
			.absent();

	public static InVehicleDeviceApiClient newInstance() {
		if (!BuildConfig.DEBUG) {
			return new EmptyInVehicleDeviceApiClient();
		}
		synchronized (API_CLIENT_LOCK) {
			return apiClient.or(new EmptyInVehicleDeviceApiClient());
		}
	}

	public static InVehicleDeviceApiClient newInstance(final String url,
			final String token, final File file) {
		if (!BuildConfig.DEBUG) {
			return new DefaultInVehicleDeviceApiClient(url, token, file);
		}
		synchronized (API_CLIENT_LOCK) {
			return apiClient.or(new Supplier<InVehicleDeviceApiClient>() {
				@Override
				public InVehicleDeviceApiClient get() {
					// 厳密にclose()する必要があるため、インスタンス生成を遅延させる
					return new DefaultInVehicleDeviceApiClient(url, token, file);
					// return new DummyInVehicleDeviceApiClient();
					// return new ScheduleChangedTestApiClient();
					// return new InVehicleDeviceApiClient();
				}
			});
		}
	}

	/**
	 * TODO: DIなどに書き換え
	 */
	public static void setInstance(InVehicleDeviceApiClient newApiClient) {
		synchronized (API_CLIENT_LOCK) {
			try {
				for (Closeable apiClient : InVehicleDeviceApiClientFactory.apiClient
						.asSet()) {
					apiClient.close();
				}
			} catch (IOException e) {
				Log.w(TAG, e);
			}
			InVehicleDeviceApiClientFactory.apiClient = Optional
					.of(newApiClient);
		}
	}
}
