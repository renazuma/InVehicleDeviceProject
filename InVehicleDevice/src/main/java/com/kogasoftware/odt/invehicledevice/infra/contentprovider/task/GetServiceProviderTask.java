package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpResponse;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.ServiceProvider;

/**
 * サービスプロバイダーの取得APIとの通信
 */
public class GetServiceProviderTask extends SynchronizationTask {
	private static final String TAG = GetServiceProviderTask.class.getSimpleName();

	public GetServiceProviderTask(Context context, SQLiteDatabase database,
			ScheduledExecutorService executorService) {
		super(context, database, executorService);
	}

	@Override
	protected void runSession(URI baseUri, String authenticationToken) {
		doHttpGet(baseUri, "service_provider", authenticationToken,
				new LogCallback(TAG) {
					@Override
					public void onSuccess(HttpResponse response, byte[] entity) {
						save(new String(entity, Charsets.UTF_8));
					}

					@Override
					public void onException(IOException e) {
						super.onException(e);
						submitRetry();
					}

					@Override
					public void onFailure(HttpResponse response, byte[] entity) {
						super.onFailure(response, entity);
						submitRetry();
					}
				});
	}

	private void save(String entity) {
		JsonNode node;
		try {
			node = JSON.readTree(entity);
		} catch (IOException e) {
			Log.e(TAG, "IOException while parsing entity: " + entity, e);
			submitRetry();
			return;
		}
		Long id;
		try {
			database.beginTransaction();
			database.delete(ServiceProvider.TABLE_NAME, null, null);
			ContentValues values = new ContentValues();
			values.put(ServiceProvider.Columns._ID, node.path("id").asInt());
			values.put(ServiceProvider.Columns.NAME, node.path("name").asText());
			JsonNode accessKey = node.path("log_access_key_id_aws");
			if (accessKey.isTextual()) {
				values.put(ServiceProvider.Columns.LOG_ACCESS_KEY_ID_AWS, accessKey.asText());
			}
			JsonNode secretAccessKey = node.path("log_secret_access_key_aws");
			if (secretAccessKey.isTextual()) {
				values.put(ServiceProvider.Columns.LOG_SECRET_ACCESS_KEY_AWS, secretAccessKey.asText());
			}
			id = database.insertOrThrow(ServiceProvider.TABLE_NAME, null, values);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		contentResolver.notifyChange(
				ContentUris.withAppendedId(ServiceProvider.CONTENT.URI, id), null);
	}
}
