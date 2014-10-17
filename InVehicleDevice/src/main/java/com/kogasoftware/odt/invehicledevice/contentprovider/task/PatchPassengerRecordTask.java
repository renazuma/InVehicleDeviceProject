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
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecord;

/**
 * 乗車実績の更新APIとの通信
 */
public class PatchPassengerRecordTask extends SynchronizationTask {
	static final String TAG = PatchPassengerRecordTask.class.getSimpleName();
	public static final Long INTERVAL_MILLIS = 60 * 1000L;

	public PatchPassengerRecordTask(Context context, SQLiteDatabase database,
			ScheduledExecutorService executorService) {
		super(context, database, executorService);
	}

	List<Pair<Long, ObjectNode>> getUpdatedPassengerRecords()
			throws IllegalArgumentException {
		List<Pair<Long, ObjectNode>> nodes = Lists.newLinkedList();
		Cursor cursor = database.query(PassengerRecord.TABLE_NAME,
				new String[]{PassengerRecord.Columns._ID,
						PassengerRecord.Columns.GET_ON_TIME,
						PassengerRecord.Columns.GET_OFF_TIME,
						PassengerRecord.Columns.LOCAL_VERSION},
				PassengerRecord.Columns.LOCAL_VERSION + " > "
						+ PassengerRecord.Columns.SERVER_VERSION, null, null,
				null, null);
		try {
			if (!cursor.moveToFirst()) {
				return nodes;
			}
			do {
				ObjectNode node = JSON.createObjectNode();
				node.put("id", cursor.getLong(cursor
						.getColumnIndexOrThrow(PassengerRecord.Columns._ID)));

				Integer getOnTimeIndex = cursor
						.getColumnIndexOrThrow(PassengerRecord.Columns.GET_ON_TIME);
				if (cursor.isNull(getOnTimeIndex)) {
					node.putNull("get_on_time");
				} else {
					String getOnTime = ISODateTimeFormat.dateTime().print(
							cursor.getLong(getOnTimeIndex));
					node.put("get_on_time", getOnTime);
				}

				Integer getOffTimeIndex = cursor
						.getColumnIndexOrThrow(PassengerRecord.Columns.GET_OFF_TIME);
				if (cursor.isNull(getOffTimeIndex)) {
					node.putNull("get_off_time");
				} else {
					String getOffTime = ISODateTimeFormat.dateTime().print(
							cursor.getLong(getOffTimeIndex));
					node.put("get_off_time", getOffTime);
				}

				Long localVersion = cursor
						.getLong(cursor
								.getColumnIndexOrThrow(PassengerRecord.Columns.LOCAL_VERSION));
				nodes.add(Pair.of(localVersion, node));
			} while (cursor.moveToNext());
		} finally {
			cursor.close();
		}
		return nodes;
	}

	@Override
	protected void runSession(URI baseUri, String authenticationToken) {
		for (Pair<Long, ObjectNode> versionAndNode : getUpdatedPassengerRecords()) {
			final Long version = versionAndNode.getLeft();
			final ObjectNode node = versionAndNode.getRight();
			final Long id = node.get("id").asLong();
			ObjectNode rootNode = JSON.createObjectNode();
			rootNode.set("passenger_record", node);
			doHttpPatch(baseUri, "passenger_records/" + id,
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
		values.put(PassengerRecord.Columns.SERVER_VERSION, version);
		database.update(PassengerRecord.TABLE_NAME, values,
				PassengerRecord.Columns._ID + " = ?",
				new String[]{id.toString()});
	}
}
