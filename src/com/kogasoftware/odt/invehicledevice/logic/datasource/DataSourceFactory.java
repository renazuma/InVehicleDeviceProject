package com.kogasoftware.odt.invehicledevice.logic.datasource;

import java.io.File;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;

public class DataSourceFactory {
	private static Optional<DataSource> dataSource = Optional.absent();
	private static Object dataSourceLock = new Object();

	public static DataSource newInstance() {
		synchronized (dataSourceLock) {
			// return dataSource.or(new ScheduleChangedTestDataSource());
			return dataSource.or(new EmptyDataSource());
		}
	}

	public static DataSource newInstance(String url, String token, File file) {
		synchronized (dataSourceLock) {
			// return dataSource.or(new ScheduleChangedTestDataSource());
			if (dataSource.isPresent()) {
				return dataSource.get();
			}
			// 厳密にclose()する必要があるため、Optional.or()は使わない
			return new WebAPIDataSource(url, token, file);
		}
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
