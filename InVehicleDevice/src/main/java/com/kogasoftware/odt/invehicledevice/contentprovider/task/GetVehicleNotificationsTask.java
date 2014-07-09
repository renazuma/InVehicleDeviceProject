package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.amazonaws.org.apache.http.client.utils.URIBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;

public class GetVehicleNotificationsTask extends SynchronizationTask {
	static final String TAG = GetVehicleNotificationsTask.class.getSimpleName();
	public static final Integer INTERVAL_MILLIS = 20 * 1000;

	public GetVehicleNotificationsTask(Context context,
			SQLiteDatabase database, ScheduledExecutorService executorService) {
		super(context, database, executorService);
	}

	@Override
	protected void runSession(URI baseUri, String authenticaitonToken)
			throws IOException, JSONException, URISyntaxException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		URIBuilder uriBuilder = new URIBuilder(baseUri);
		uriBuilder.setPath("/in_vehicle_devices/vehicle_notifications");
		uriBuilder.addParameter(AUTHENTICATION_TOKEN_KEY, authenticaitonToken);
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Accept", "application/json");
		request.setURI(uriBuilder.build());
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		byte[] responseEntity = new byte[] {};
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			responseEntity = EntityUtils.toByteArray(entity);
		}
		if (statusCode / 100 == 4 || statusCode / 100 == 5) {
			throw new IOException("code=" + statusCode);
		}
		List<Uri> committedUris;
		try {
			List<Uri> uris = Lists.newLinkedList();
			List<Long> ids = Lists.newLinkedList();
			database.beginTransaction();
			Cursor cursor = database.query(VehicleNotifications.TABLE_NAME,
					new String[] { VehicleNotifications.Columns._ID }, null,
					null, null, null, null);
			try {
				if (cursor.moveToFirst()) {
					do {
						ids.add(cursor.getLong(cursor
								.getColumnIndexOrThrow(VehicleNotifications.Columns._ID)));
					} while (cursor.moveToNext());
				}
			} finally {
				cursor.close();
			}
			for (JsonNode node : JSON.readValue(new String(responseEntity,
					Charsets.UTF_8), JsonNode[].class)) {
				ContentValues values = new ContentValues();
				Long id = node.path("id").asLong();
				if (ids.contains(id)) {
					continue;
				}
				values.put(VehicleNotifications.Columns._ID, id);
				values.put(VehicleNotifications.Columns.BODY, node.path("body")
						.asText());
				values.put(VehicleNotifications.Columns.BODY_RUBY,
						node.path("body_ruby").asText());
				values.put(VehicleNotifications.Columns.NOTIFICATION_KIND, node
						.path("notification_kind").asLong());
				database.insertOrThrow(VehicleNotifications.TABLE_NAME, null,
						values);
				uris.add(ContentUris.withAppendedId(
						VehicleNotifications.CONTENT.URI, id));
			}
			database.setTransactionSuccessful();
			committedUris = uris;
		} finally {
			database.endTransaction();
		}
		for (Uri changedUri : committedUris) {
			contentResolver.notifyChange(changedUri, null);
		}
		if (!committedUris.isEmpty()) {
			contentResolver
					.notifyChange(VehicleNotifications.CONTENT.URI, null);
		}
		executorService.execute(new GetOperationSchedulesTask(context,
				database, executorService, true));
	}
}
