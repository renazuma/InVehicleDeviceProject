package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.format.ISODateTimeFormat;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.kogasoftware.android.org.apache.http.client.methods.HttpPatch;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;

public class PatchVehicleNotificationTask extends SynchronizationTask {
	static final String TAG = PatchVehicleNotificationTask.class
			.getSimpleName();
	public static final Long INTERVAL_MILLIS = 60 * 1000L;

	public PatchVehicleNotificationTask(Context context,
			SQLiteDatabase database, ScheduledExecutorService executorService) {
		super(context, database, executorService);
	}

	List<ObjectNode> getNotRepliedVehicleNotifications()
			throws IllegalArgumentException {
		List<ObjectNode> nodes = Lists.newLinkedList();
		Cursor cursor = database.query(VehicleNotifications.TABLE_NAME,
				new String[]{VehicleNotifications.Columns._ID,
						VehicleNotifications.Columns.RESPONSE,
						VehicleNotifications.Columns.READ_AT},
				"response IS NOT NULL AND read_at IS NOT NULL", null, null,
				null, null);
		try {
			DatabaseUtils.dumpCursor(cursor);
			if (!cursor.moveToFirst()) {
				return nodes;
			}
			do {
				ObjectNode node = JSON.createObjectNode();
				node.put(
						"id",
						cursor.getLong(cursor
								.getColumnIndexOrThrow(VehicleNotifications.Columns._ID)));
				node.put(
						"response",
						cursor.getLong(cursor
								.getColumnIndexOrThrow(VehicleNotifications.Columns.RESPONSE)));
				Long readAt = cursor
						.getLong(cursor
								.getColumnIndexOrThrow(VehicleNotifications.Columns.READ_AT));
				node.put("read_at", ISODateTimeFormat.dateTime().print(readAt));
				nodes.add(node);
			} while (cursor.moveToNext());
		} finally {
			cursor.close();
		}
		return nodes;
	}

	@Override
	protected void runSession(URI baseUri, String authenticationToken)
			throws IOException, URISyntaxException {
		List<ObjectNode> nodes = getNotRepliedVehicleNotifications();
		for (ObjectNode node : nodes) {
			ObjectNode rootNode = JSON.createObjectNode();
			Long id = node.get("id").asLong();
			rootNode.put(AUTHENTICATION_TOKEN_KEY, authenticationToken);
			rootNode.set("vehicle_notification", node);
			HttpClient client = new DefaultHttpClient();
			HttpPatch request = new HttpPatch();
			request.addHeader("Content-Type", "application/json");
			request.addHeader("Accept", "application/json");
			URI uri = baseUri
					.resolve("in_vehicle_devices/vehicle_notifications/" + id);
			request.setURI(uri);
			request.setEntity(new StringEntity(rootNode.toString()));
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode / 100 == 4 || statusCode / 100 == 5) {
				Log.e(TAG, "PATCH " + uri + " " + node + " failed code="
						+ statusCode);
				continue;
			}
			database.delete(VehicleNotifications.TABLE_NAME,
					VehicleNotifications.Columns._ID + " = ?",
					new String[]{id.toString()});
		}
	}
}
