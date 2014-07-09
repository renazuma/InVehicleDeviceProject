package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.kogasoftware.android.org.apache.http.client.methods.HttpPatch;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;

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
		Cursor cursor = database.query(PassengerRecords.TABLE_NAME,
				new String[]{PassengerRecords.Columns._ID,
						PassengerRecords.Columns.GET_ON_TIME,
						PassengerRecords.Columns.GET_OFF_TIME,
						PassengerRecords.Columns.LOCAL_VERSION},
				PassengerRecords.Columns.LOCAL_VERSION + " > "
						+ PassengerRecords.Columns.SERVER_VERSION, null, null,
				null, null);
		try {
			if (!cursor.moveToFirst()) {
				return nodes;
			}
			do {
				ObjectNode node = JSON.createObjectNode();
				node.put("id", cursor.getLong(cursor
						.getColumnIndexOrThrow(PassengerRecords.Columns._ID)));

				Integer getOnTimeIndex = cursor
						.getColumnIndexOrThrow(PassengerRecords.Columns.GET_ON_TIME);
				if (cursor.isNull(getOnTimeIndex)) {
					node.putNull("get_on_time");
				} else {
					String getOnTime = ISODateTimeFormat.dateTime().print(
							cursor.getLong(getOnTimeIndex));
					node.put("get_on_time", getOnTime);
				}

				Integer getOffTimeIndex = cursor
						.getColumnIndexOrThrow(PassengerRecords.Columns.GET_OFF_TIME);
				if (cursor.isNull(getOffTimeIndex)) {
					node.putNull("get_off_time");
				} else {
					String getOffTime = ISODateTimeFormat.dateTime().print(
							cursor.getLong(getOffTimeIndex));
					node.put("get_off_time", getOffTime);
				}

				Long localVersion = cursor
						.getLong(cursor
								.getColumnIndexOrThrow(PassengerRecords.Columns.LOCAL_VERSION));
				nodes.add(Pair.of(localVersion, node));
			} while (cursor.moveToNext());
		} finally {
			cursor.close();
		}
		return nodes;
	}

	@Override
	protected void runSession(URI baseUri, String authenticationToken)
			throws IOException, JSONException, URISyntaxException {
		for (Pair<Long, ObjectNode> versionAndNode : getUpdatedPassengerRecords()) {
			Long version = versionAndNode.getLeft();
			ObjectNode node = versionAndNode.getRight();
			Long id = node.get("id").asLong();
			ObjectNode rootNode = JSON.createObjectNode();
			rootNode.put(AUTHENTICATION_TOKEN_KEY, authenticationToken);
			rootNode.set("passenger_record", node);
			HttpClient client = new DefaultHttpClient();
			HttpPatch request = new HttpPatch();
			URI uri = baseUri.resolve("in_vehicle_devices/passenger_records/"
					+ id);
			request.setURI(uri);
			request.addHeader("Content-Type", "application/json");
			request.addHeader("Accept", "application/json");
			request.setEntity(new StringEntity(rootNode.toString()));
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode / 100 == 4 || statusCode / 100 == 5) {
				Log.e(TAG, "PATCH " + uri + " " + node + " failed code="
						+ statusCode);
				continue;
			}
			ContentValues values = new ContentValues();
			values.put(PassengerRecords.Columns.SERVER_VERSION, version);
			database.update(PassengerRecords.TABLE_NAME, values,
					PassengerRecords.Columns._ID + " = ?",
					new String[]{id.toString()});
		}
	}
}
