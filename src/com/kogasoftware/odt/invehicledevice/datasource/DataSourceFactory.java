package com.kogasoftware.odt.invehicledevice.datasource;

import com.google.common.base.Optional;

public class DataSourceFactory {
	private static Optional<DataSource> dataSource = Optional
			.<DataSource> absent();
	private static Object dataSourceLock = new Object();

	/**
	 * TODO: DIなどで書き換え
	 */
	public static void setInstance(DataSource dataSource) {
		synchronized (dataSourceLock) {
			DataSourceFactory.dataSource = Optional.<DataSource> of(dataSource);
		}
	}

	public static DataSource newInstance() {
		synchronized (dataSourceLock) {
			if (dataSource.isPresent()) {
				return dataSource.get();
			}
		}
		return new DummyDataSource();
	}
}
