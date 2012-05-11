package com.kogasoftware.odt.invehicledevice.logic.datasource;

import com.google.common.base.Optional;

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
			DataSourceFactory.dataSource = Optional.<DataSource> of(dataSource);
		}
	}
}