package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpResponse;
import org.joda.time.format.ISODateTimeFormat;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;

public class PatchVehicleNotificationTask extends SynchronizationTask {
	private static final String TAG = PatchVehicleNotificationTask.class
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
	protected void runSession(URI baseUri, String authenticationToken) {
		List<ObjectNode> nodes = getNotRepliedVehicleNotifications();
		for (ObjectNode node : nodes) {
			ObjectNode rootNode = JSON.createObjectNode();
			final Long id = node.get("id").asLong();
			rootNode.set("vehicle_notification", node);
			doHttpPatch(baseUri, "vehicle_notifications/" + id,
					authenticationToken, rootNode, new LogCallback(TAG) {
						@Override
						public void onSuccess(HttpResponse response,
								byte[] entity) {
							database.delete(VehicleNotifications.TABLE_NAME,
									VehicleNotifications.Columns._ID + " = ?",
									new String[]{id.toString()});
						}
					});
		}
	}
}
