package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.json.JSONException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.InVehicleDeviceContentProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevices;

public class SynchronizationTask implements Runnable {
	private static final String TAG = SynchronizationTask.class.getSimpleName();
	public static final ObjectMapper JSON = InVehicleDeviceContentProvider.JSON;
	protected static final String AUTHENTICATION_TOKEN_KEY = "authentication_token";
	protected final Context context;
	protected final ContentResolver contentResolver;
	protected final SQLiteDatabase database;
	protected final ScheduledExecutorService executorService;

	public SynchronizationTask(Context context, SQLiteDatabase database,
			ScheduledExecutorService executorService) {
		this.context = context;
		this.contentResolver = context.getContentResolver();
		this.database = database;
		this.executorService = executorService;
	}

	protected void runSession(URI baseUri, String authenticationToken)
			throws IOException, JSONException, URISyntaxException {
	}

	@Override
	public void run() {
		String[] columns = new String[] { InVehicleDevices.Columns.URL,
				InVehicleDevices.Columns.AUTHENTICATION_TOKEN };
		String url;
		String authenticationToken;
		Cursor cursor = database.query(InVehicleDevices.TABLE_NAME, columns,
				null, null, null, null, null);
		try {
			if (!cursor.moveToFirst()) {
				return;
			}
			url = cursor.getString(cursor
					.getColumnIndexOrThrow(InVehicleDevices.Columns.URL));
			authenticationToken = cursor
					.getString(cursor
							.getColumnIndexOrThrow(InVehicleDevices.Columns.AUTHENTICATION_TOKEN));
		} finally {
			cursor.close();
		}

		if (authenticationToken == null || url == null) {
			return;
		}

		try {
			runSession(new URI(url), authenticationToken);
		} catch (URISyntaxException e) {
			Log.e(TAG, "Syntax error: in_vehicle_devices.url", e);
		} catch (IOException e) {
			Log.w(TAG, e);
		} catch (JSONException e) {
			Log.w(TAG, e);
		}
	}

	protected List<ObjectNode> toObjectNodes(Cursor cursor) {
		List<ObjectNode> nodes = Lists.newLinkedList();
		if (!cursor.moveToFirst()) {
			return nodes;
		}
		do {
			nodes.add(toObjectNode(cursor));
		} while (cursor.moveToNext());
		return nodes;
	}

	protected ObjectNode toObjectNode(Cursor cursor) {
		ObjectNode node = JSON.createObjectNode();
		for (String columnName : cursor.getColumnNames()) {
			String key = columnName;
			if (columnName.equals(BaseColumns._ID)) {
				key = "id";
			}
			Integer index = cursor.getColumnIndexOrThrow(columnName);
			switch (cursor.getType(index)) {
			case Cursor.FIELD_TYPE_FLOAT:
				node.put(key, cursor.getDouble(index));
				break;
			case Cursor.FIELD_TYPE_INTEGER:
				node.put(key, cursor.getLong(index));
				break;
			case Cursor.FIELD_TYPE_NULL:
				node.putNull(key);
				break;
			case Cursor.FIELD_TYPE_STRING:
				node.put(key, cursor.getString(index));
				break;
			}
		}
		return node;
	}

	protected void submitRetry() {
		executorService.submit(this);
	}
}
