package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpResponse;
import org.joda.time.DateTime;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLogs;

public class PostServiceUnitStatusLogTask extends SynchronizationTask {
	static final String TAG = PostServiceUnitStatusLogTask.class
			.getSimpleName();
	public static final Integer INTERVAL_MILLIS = 30 * 1000;

	public PostServiceUnitStatusLogTask(Context context,
			SQLiteDatabase database, ScheduledExecutorService executorService) {
		super(context, database, executorService);
	}

	List<ObjectNode> getServiceUnitStatusLogs() throws IllegalArgumentException {
		Long millis = DateTime.now()
				.minusMillis(InsertServiceUnitStatusLogTask.INTERVAL_MILLIS)
				.getMillis();
		String where = ServiceUnitStatusLogs.Columns.CREATED_AT + " < "
				+ millis;
		Cursor cursor = database.query(ServiceUnitStatusLogs.TABLE_NAME, null,
				where, null, null, null, null);
		try {
			return toObjectNodes(cursor);
		} finally {
			cursor.close();
		}
	}

	@Override
	protected void runSession(URI baseUri, String authenticationToken) {
		for (ObjectNode node : getServiceUnitStatusLogs()) {
			ObjectNode rootNode = JSON.createObjectNode();

			// TODO: サーバー側を修正
			if (node.path("latitude").isNull()) {
				node.put("latitude", 0);
			}
			if (node.path("longitude").isNull()) {
				node.put("longitude", 0);
			}

			final Long id = node.get("id").asLong();
			rootNode.set("service_unit_status_log", node);
			doHttpPost(baseUri, "service_unit_status_logs",
					authenticationToken, rootNode, new LogCallback(TAG) {
						@Override
						public void onSuccess(HttpResponse response,
								byte[] entity) {
							database.delete(ServiceUnitStatusLogs.TABLE_NAME,
									ServiceUnitStatusLogs.Columns._ID + " = ?",
									new String[]{id.toString()});
						}
					});
		}
	}
}
