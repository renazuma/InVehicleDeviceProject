package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.JSONException;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

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
	protected void runSession(URI baseUri, String authenticationToken)
			throws IOException, JSONException, URISyntaxException {
		for (ObjectNode node : getServiceUnitStatusLogs()) {
			ObjectNode rootNode = JSON.createObjectNode();

			// TODO: サーバー側を修正
			if (node.path("latitude").isNull()) {
				node.put("latitude", 0);
			}
			if (node.path("longitude").isNull()) {
				node.put("longitude", 0);
			}

			Long id = node.get("id").asLong();
			rootNode.put(AUTHENTICATION_TOKEN_KEY, authenticationToken);
			rootNode.set("service_unit_status_log", node);
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost();
			URI uri = baseUri
					.resolve("in_vehicle_devices/service_unit_status_logs");
			request.setURI(uri);
			request.setEntity(new StringEntity(rootNode.toString()));
			request.addHeader("Content-Type", "application/json");
			request.addHeader("Accept", "application/json");
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode / 100 == 4 || statusCode / 100 == 5) {
				String message = "POST " + uri + " " + node + " failed code="
						+ statusCode + " entity=["
						+ EntityUtils.toString(response.getEntity(), "UTF-8")
						+ "]";
				Log.e(TAG, message);
				continue;
			}
			database.delete(ServiceUnitStatusLogs.TABLE_NAME,
					ServiceUnitStatusLogs.Columns._ID + " = ?",
					new String[]{id.toString()});
		}
	}
}
