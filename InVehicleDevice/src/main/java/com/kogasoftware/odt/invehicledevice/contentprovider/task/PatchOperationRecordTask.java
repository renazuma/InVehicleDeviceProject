package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.joda.time.format.ISODateTimeFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationRecords;

public class PatchOperationRecordTask extends SynchronizationTask {
	static final String TAG = PatchOperationRecordTask.class.getSimpleName();
	public static final Long INTERVAL_MILLIS = 60 * 1000L;

	public PatchOperationRecordTask(Context context, SQLiteDatabase database,
			ScheduledExecutorService executorService) {
		super(context, database, executorService);
	}

	List<Pair<Long, ObjectNode>> getUpdatedOperationRecords()
			throws IllegalArgumentException {
		List<Pair<Long, ObjectNode>> nodes = Lists.newLinkedList();
		Cursor cursor = database.query(OperationRecords.TABLE_NAME,
				new String[]{OperationRecords.Columns._ID,
						OperationRecords.Columns.ARRIVED_AT,
						OperationRecords.Columns.DEPARTED_AT,
						OperationRecords.Columns.LOCAL_VERSION},
				OperationRecords.Columns.LOCAL_VERSION + " > "
						+ OperationRecords.Columns.SERVER_VERSION, null, null,
				null, null);
		try {
			if (!cursor.moveToFirst()) {
				return nodes;
			}
			do {
				ObjectNode node = JSON.createObjectNode();
				node.put("id", cursor.getLong(cursor
						.getColumnIndexOrThrow(OperationRecords.Columns._ID)));

				Integer arrivedAtIndex = cursor
						.getColumnIndexOrThrow(OperationRecords.Columns.ARRIVED_AT);
				if (cursor.isNull(arrivedAtIndex)) {
					node.putNull("arrived_at");
				} else {
					String arrivedAt = ISODateTimeFormat.dateTime().print(
							cursor.getLong(arrivedAtIndex));
					node.put("arrived_at", arrivedAt);
				}

				Integer departedAtIndex = cursor
						.getColumnIndexOrThrow(OperationRecords.Columns.DEPARTED_AT);
				if (cursor.isNull(departedAtIndex)) {
					node.putNull("departed_at");
				} else {
					String departedAt = ISODateTimeFormat.dateTime().print(
							cursor.getLong(departedAtIndex));
					node.put("departed_at", departedAt);
				}
				Long localVersion = cursor
						.getLong(cursor
								.getColumnIndexOrThrow(OperationRecords.Columns.LOCAL_VERSION));
				nodes.add(Pair.of(localVersion, node));
			} while (cursor.moveToNext());
		} finally {
			cursor.close();
		}
		return nodes;
	}

	@Override
	protected void runSession(URI baseUri, String authenticationToken) {
		for (Pair<Long, ObjectNode> versionAndNode : getUpdatedOperationRecords()) {
			final Long version = versionAndNode.getLeft();
			final ObjectNode node = versionAndNode.getRight();
			final Long id = node.get("id").asLong();
			ObjectNode rootNode = JSON.createObjectNode();
			rootNode.set("operation_record", node);
			doHttpPatch(baseUri, "operation_records/" + id,
					authenticationToken, rootNode, new LogCallback(TAG) {
						@Override
						public void onSuccess(HttpResponse response,
								byte[] entity) {
							save(node, id, version);
						}
					});
		}
	}

	private void save(ObjectNode node, Long id, Long version) {
		ContentValues values = new ContentValues();
		values.put(OperationRecords.Columns.SERVER_VERSION, version);
		database.update(OperationRecords.TABLE_NAME, values,
				OperationRecords.Columns._ID + " = ?",
				new String[]{id.toString()});
	}
}
