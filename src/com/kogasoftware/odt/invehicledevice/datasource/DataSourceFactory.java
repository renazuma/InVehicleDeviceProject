package com.kogasoftware.odt.invehicledevice.datasource;

import java.io.File;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.BuildConfig;

public class DataSourceFactory {
	private static Optional<DataSource> dataSource = Optional.absent();
	private static final Object DATA_SOURCE_LOCK = new Object();

	public static DataSource newInstance() {
		if (!BuildConfig.DEBUG) {
			return new EmptyDataSource();
		}
		synchronized (DATA_SOURCE_LOCK) {
			return dataSource.or(new EmptyDataSource());
		}
	}

	public static DataSource newInstance(final String url, final String token,
			final File file) {
		if (!BuildConfig.DEBUG) {
			return new WebAPIDataSource(url, token, file);
		}
		synchronized (DATA_SOURCE_LOCK) {
			return dataSource.or(new Supplier<DataSource>() {
				@Override
				public DataSource get() {
					// 厳密にclose()する必要があるため、インスタンス生成を遅延させる
					return new WebAPIDataSource(url, token, file);
					// return new _GIT_IGNORE_DummyDataSource();
					// return new ScheduleChangedTestDataSource();
					// return new EmptyDataSource();
				}
			});
		}
	}

	/**
	 * TODO: DIなどで書き換え
	 */
	public static void setInstance(DataSource dataSource) {
		synchronized (DATA_SOURCE_LOCK) {
			Closeables.closeQuietly(DataSourceFactory.dataSource.orNull());
			DataSourceFactory.dataSource = Optional.<DataSource> of(dataSource);
		}
	}
}
