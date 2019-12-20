package com.kogasoftware.odt.invehicledevice.infra.contentprovider.task;

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
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationRecord;

/**
 * 運行実績の更新APIとの通信
 */
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
		Cursor cursor = database.query(OperationRecord.TABLE_NAME,
				new String[]{OperationRecord.Columns._ID,
						OperationRecord.Columns.ARRIVED_AT,
						OperationRecord.Columns.DEPARTED_AT,
						OperationRecord.Columns.LOCAL_VERSION},
				OperationRecord.Columns.LOCAL_VERSION + " > "
						+ OperationRecord.Columns.SERVER_VERSION, null, null,
				null, null);
		try {
			if (!cursor.moveToFirst()) {
				return nodes;
			}
			do {
				ObjectNode node = JSON.createObjectNode();
				node.put("id", cursor.getLong(cursor
						.getColumnIndexOrThrow(OperationRecord.Columns._ID)));

				Integer arrivedAtIndex = cursor
						.getColumnIndexOrThrow(OperationRecord.Columns.ARRIVED_AT);
				if (cursor.isNull(arrivedAtIndex)) {
					node.putNull("arrived_at");
				} else {
					String arrivedAt = ISODateTimeFormat.dateTime().print(
							cursor.getLong(arrivedAtIndex));
					node.put("arrived_at", arrivedAt);
				}

				Integer departedAtIndex = cursor
						.getColumnIndexOrThrow(OperationRecord.Columns.DEPARTED_AT);
				if (cursor.isNull(departedAtIndex)) {
					node.putNull("departed_at");
				} else {
					String departedAt = ISODateTimeFormat.dateTime().print(
							cursor.getLong(departedAtIndex));
					node.put("departed_at", departedAt);
				}
				Long localVersion = cursor
						.getLong(cursor
								.getColumnIndexOrThrow(OperationRecord.Columns.LOCAL_VERSION));
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
		values.put(OperationRecord.Columns.SERVER_VERSION, version);
		database.update(OperationRecord.TABLE_NAME, values,
				OperationRecord.Columns._ID + " = ?",
				new String[]{id.toString()});
	}
}
