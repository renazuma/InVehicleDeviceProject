package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import android.database.sqlite.SQLiteDatabase;

import com.amazonaws.org.apache.http.client.utils.URIBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Charsets;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProviders;

public class GetServiceProviderTask extends SynchronizationTask {
	static final String TAG = GetServiceProviderTask.class.getSimpleName();

	public GetServiceProviderTask(Context context, SQLiteDatabase database,
			ScheduledExecutorService executorService) {
		super(context, database, executorService);
	}

	@Override
	protected void runSession(URI baseUri, String authenticaitonToken)
			throws IOException, JSONException, URISyntaxException {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet();
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Accept", "application/json");
		URIBuilder uriBuilder = new URIBuilder(baseUri);
		uriBuilder.setPath("/in_vehicle_devices/service_provider");
		uriBuilder.addParameter(AUTHENTICATION_TOKEN_KEY, authenticaitonToken);
		request.setURI(uriBuilder.build());
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		JsonNode node;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			node = JSON.readTree(new String(EntityUtils.toByteArray(entity),
					Charsets.UTF_8));
		} else {
			node = JSON.createObjectNode();
		}
		if (statusCode / 100 == 4 || statusCode / 100 == 5) {
			throw new IOException("code=" + statusCode);
		}
		Long id;
		try {
			database.beginTransaction();
			database.delete(ServiceProviders.TABLE_NAME, null, null);
			ContentValues values = new ContentValues();
			values.put(ServiceProviders.Columns._ID, node.path("id").asInt());
			values.put(ServiceProviders.Columns.NAME, node.path("name")
					.asText());
			JsonNode accessKey = node.path("log_access_key_id_aws");
			if (accessKey.isTextual()) {
				values.put(ServiceProviders.Columns.LOG_ACCESS_KEY_ID_AWS,
						accessKey.asText());
			}
			JsonNode secretAccessKey = node.path("log_secret_access_key_aws");
			if (secretAccessKey.isTextual()) {
				values.put(ServiceProviders.Columns.LOG_SECRET_ACCESS_KEY_AWS,
						secretAccessKey.asText());
			}
			id = database.insertOrThrow(ServiceProviders.TABLE_NAME, null,
					values);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
		contentResolver.notifyChange(
				ContentUris.withAppendedId(ServiceProviders.CONTENT.URI, id),
				null);
	}
}
