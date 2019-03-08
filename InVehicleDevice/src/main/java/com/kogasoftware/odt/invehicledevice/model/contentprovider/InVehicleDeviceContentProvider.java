package com.kogasoftware.odt.invehicledevice.model.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.Content;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.OperationRecord;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.Platform;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.Reservation;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.User;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.table.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.GetOperationSchedulesTask;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.GetServiceProviderTask;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.GetVehicleNotificationsTask;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.PatchOperationRecordTask;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.PatchPassengerRecordTask;
import com.kogasoftware.odt.invehicledevice.model.contentprovider.task.PatchVehicleNotificationTask;

import junit.framework.Assert;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 車載器内部データの管理とサーバーとの同期を行うContentProvider
 */
public class InVehicleDeviceContentProvider extends ContentProvider {
	public static final String AUTHORITY = "com.kogasoftware.odt.invehicledevice.model.contentprovider";
	private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	public static final ObjectMapper JSON = new ObjectMapper();
	static {
		JSON.registerModule(new JodaModule());
		JSON.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		JSON.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
		JSON.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		for (Content content : new Content[]{InVehicleDevice.CONTENT,
				OperationRecord.CONTENT, OperationSchedule.CONTENT,
				PassengerRecord.CONTENT, Platform.CONTENT, Reservation.CONTENT,
				ServiceProvider.CONTENT, ServiceUnitStatusLog.CONTENT,
				User.CONTENT, VehicleNotification.CONTENT}) {
			content.addTo(MATCHER);
		}
	}

	private DatabaseHelper databaseHelper;
	private SQLiteDatabase database;

	// 順番を保持したまま処理したい通信があるため、スレッドの数は1で固定
	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public ScheduledExecutorService getExecutorService() {
		return executorService;
	}

	public ContentResolver getContentResolver() {
		return getContext().getContentResolver();
	}

	@Override
	public boolean onCreate() {
		Context context = getContext();

		databaseHelper = new DatabaseHelper(context);
		database = databaseHelper.getWritableDatabase();

		// TODO: 定期処理はここで良い？ContentProviderはあくまでDBとのやり取りを担う機能としておいて、定期処理はMainActivityで開始するべきでは？
        // TODO: おそらく、Activityがバックグラウンドに入っても連携はし続ける必要があるので、ContentProviderに入っているのだと考えられる。
		// TODO: それであれば、別途serviceに切り出したりした方が良いかも。

		// 一定間隔で、未読通知をサーバから取得
		executorService.scheduleWithFixedDelay(
						new GetVehicleNotificationsTask(context, database, executorService),
						5,
						GetVehicleNotificationsTask.INTERVAL_MILLIS,
						TimeUnit.MILLISECONDS);

		// 一定間隔で、既読かつサーバ側と同期前の通知データを、サーバ側に送信
		executorService.scheduleWithFixedDelay(
				        new PatchVehicleNotificationTask(context, database, executorService),
						10,
						PatchVehicleNotificationTask.INTERVAL_MILLIS,
						TimeUnit.MILLISECONDS);

		// 一定間隔で、更新のあった運行履歴を、サーバ側に送信
		executorService.scheduleWithFixedDelay(
						new PatchOperationRecordTask(context, database, executorService),
						10,
						PatchOperationRecordTask.INTERVAL_MILLIS,
						TimeUnit.MILLISECONDS);

		// 一定間隔で、更新のあった乗降情報を、サーバ側に送信
		executorService.scheduleWithFixedDelay(
						new PatchPassengerRecordTask(context, database, executorService),
						10,
						PatchPassengerRecordTask.INTERVAL_MILLIS,
						TimeUnit.MILLISECONDS);

		// 起動時に一度だけ、運行情報をサーバ側から取得
		executorService.execute(new GetOperationSchedulesTask(context, database, executorService));

		// 起動時に一度だけ、サービスプロバイダーをサーバ側から取得
		// 旧バージョンからのアップグレード用。車載器情報が存在してもサービスプロバイダー情報が存在しない場合がある。
		new Thread() {
			@Override
			public void run() {
				Cursor cursor = database.query(ServiceProvider.TABLE_NAME,
						null, null, null, null, null, null);
				try {
					if (cursor.moveToFirst()) { return; }
				} finally {
					cursor.close();
				}
				executorService.execute(new GetServiceProviderTask(getContext(), database, executorService));
			}
		}.start();
		return true;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int code = MATCHER.match(uri);
		switch (code) {
			case InVehicleDevice.TABLE_CODE :
				return InVehicleDevice.replaceLoginAndPassword(values, this,
						new Runnable() {
							@Override
							public void run() {
								onUpdateAuthenticationToken();
							}
						});
			case VehicleNotification.TABLE_CODE :
				return VehicleNotification.replace(values, this);
			case OperationSchedule.TABLE_CODE :
				return OperationSchedule.replace(values, this);
			default :
				throw new IllegalArgumentException("Unknown uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Integer match = MATCHER.match(uri);
		switch (match) {
			case ServiceProvider.TABLE_CODE :
				return ServiceProvider.query(this, projection, selection,
						selectionArgs, sortOrder);
			case InVehicleDevice.TABLE_CODE :
				return InVehicleDevice.query(this, projection, selection,
						selectionArgs, sortOrder);
			case VehicleNotification.TABLE_CODE :
				return VehicleNotification.query(this, projection, selection,
						selectionArgs, sortOrder);
			case OperationSchedule.TABLE_CODE :
				return OperationSchedule.query(this, projection, selection,
						selectionArgs, sortOrder);
			case PassengerRecord.TABLE_CODE :
				return PassengerRecord.query(this, projection, selection,
						selectionArgs, sortOrder);
			default :
				throw new IllegalArgumentException("Unknown uri: " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Integer match = MATCHER.match(uri);
		switch (match) {
			case InVehicleDevice.TABLE_CODE :
				return InVehicleDevice.delete(this, selection, selectionArgs);
			default :
				throw new IllegalArgumentException("Unknown uri: " + uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		Integer match = MATCHER.match(uri);
		switch (match) {
			case PassengerRecord.TABLE_CODE :
				return PassengerRecord.update(this, values, selection,	selectionArgs);
			case ServiceUnitStatusLog.TABLE_CODE :
				return ServiceUnitStatusLog.update(this, values, selection, selectionArgs);
			default :
				throw new IllegalArgumentException("Unknown uri: " + uri);
		}
	}

	private void onUpdateAuthenticationToken() {
		database.delete(ServiceProvider.TABLE_NAME, null, null);
		Cursor cursor = database.query(InVehicleDevice.TABLE_NAME, null, null,
				null, null, null, null);
		try {
			if (!cursor.moveToFirst()) { return; }

			Integer authenticationTokenIndex = cursor.getColumnIndexOrThrow(InVehicleDevice.Columns.AUTHENTICATION_TOKEN);

			if (cursor.isNull(authenticationTokenIndex)) { return; }

		} finally {
			cursor.close();
		}
		executorService.execute(new GetServiceProviderTask(getContext(), database, executorService));
		executorService.execute(new GetOperationSchedulesTask(getContext(),	database, executorService));
	}

	/**
	 * Implement this to shut down the ContentProvider instance. You can then
	 * invoke this method in unit tests.
	 *
	 * @see "http://developer.android.com/reference/android/content/ContentProvider.html#shutdown%28%29"
	 */
	@Override
	public void shutdown() {
		try {
			executorService.shutdownNow();
			try {
				Assert.assertTrue(executorService.awaitTermination(30, TimeUnit.SECONDS));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		} finally {
			try {
				database.close();
				databaseHelper.close();
			} finally {
				super.shutdown();
			}
		}
	}
}
