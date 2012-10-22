package com.kogasoftware.odt.invehicledevice.apiclient;

import java.io.File;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.io.Closeables;

public class InVehicleDeviceApiClientFactory {
	private static final Object API_CLIENT_LOCK = new Object();
	private static Optional<InVehicleDeviceApiClient> apiClient = Optional.absent();

	public static InVehicleDeviceApiClient newInstance() {
		if (!BuildConfig.DEBUG) {
			return new EmptyInVehicleDeviceApiClient();
		}
		synchronized (API_CLIENT_LOCK) {
			return apiClient.or(new EmptyInVehicleDeviceApiClient());
		}
	}

	public static InVehicleDeviceApiClient newInstance(final String url, final String token,
			final File file) {
		if (!BuildConfig.DEBUG) {
			return new DefaultInVehicleDeviceApiClient(url, token, file);
		}
		synchronized (API_CLIENT_LOCK) {
			return apiClient.or(new Supplier<InVehicleDeviceApiClient>() {
				@Override
				public InVehicleDeviceApiClient get() {
					// 厳密にclose()する必要があるため、インスタンス生成を遅延させる
					return new DefaultInVehicleDeviceApiClient(url, token, file);
					// return new DummyApiClient();
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
			Closeables.closeQuietly(InVehicleDeviceApiClientFactory.apiClient.orNull());
			InVehicleDeviceApiClientFactory.apiClient = Optional.of(newApiClient);
		}
	}
}
