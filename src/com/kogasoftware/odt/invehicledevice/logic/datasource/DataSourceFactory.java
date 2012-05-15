package com.kogasoftware.odt.invehicledevice.logic.datasource;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;

public class DataSourceFactory {
	private static Optional<DataSource> dataSource = Optional.absent();
	private static Object dataSourceLock = new Object();

	public static DataSource newInstance(String url, String token) {
		synchronized (dataSourceLock) {
			if (dataSource.isPresent()) {
				return dataSource.get();
			}
		}
		// return new DummyDataSource();
		return new WebAPIDataSource(url, token);
	}

	/**
	 * TODO: DIなどで書き換え
	 */
	public static void setInstance(DataSource dataSource) {
		synchronized (dataSourceLock) {
			Closeables.closeQuietly(DataSourceFactory.dataSource.orNull());
			DataSourceFactory.dataSource = Optional.<DataSource> of(dataSource);
		}
	}
}
