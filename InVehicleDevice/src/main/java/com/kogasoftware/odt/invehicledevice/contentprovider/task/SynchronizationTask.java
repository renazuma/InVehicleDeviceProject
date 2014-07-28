package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.amazonaws.org.apache.http.client.utils.URIBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.kogasoftware.android.org.apache.http.client.methods.HttpPatch;
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

	public static String dumpEntity(byte[] entity) {
		try {
			return Charsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(entity))
					.toString();
		} catch (CharacterCodingException e) {
		}
		if (entity.length > 5000) {
			entity = Arrays.copyOf(entity, 5000);
		}
		return "[" + Joiner.on(",").join(Arrays.asList(entity)) + "]";
	}

	public static interface Callback {
		void onSuccess(HttpResponse response, byte[] entity);
		void onFailure(HttpResponse response, byte[] entity);
		void onException(IOException e);
	}

	public static class LogCallback implements Callback {
		private final String tag;

		public LogCallback(String tag) {
			this.tag = tag;
		}

		@Override
		public void onSuccess(HttpResponse response, byte[] entity) {
			Log.i(tag, "onSuccess: " + response.getStatusLine() + " entity="
					+ dumpEntity(entity));
		}

		@Override
		public void onException(IOException e) {
			Log.i(tag, "onException", e);
		}

		@Override
		public void onFailure(HttpResponse response, byte[] entity) {
			Log.e(tag, "onFailure: " + response.getStatusLine() + " entity="
					+ dumpEntity(entity));
		}
	}

	public SynchronizationTask(Context context, SQLiteDatabase database,
			ScheduledExecutorService executorService) {
		this.context = context;
		this.contentResolver = context.getContentResolver();
		this.database = database;
		this.executorService = executorService;
	}

	protected void runSession(URI baseUri, String authenticationToken) {
	}

	@Override
	public void run() {
		try {
			String[] columns = new String[]{InVehicleDevices.Columns.URL,
					InVehicleDevices.Columns.AUTHENTICATION_TOKEN};
			String url;
			String authenticationToken;
			Cursor cursor = database.query(InVehicleDevices.TABLE_NAME,
					columns, null, null, null, null, null);
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

			URI uri;
			try {
				uri = new URI(url);
			} catch (URISyntaxException e) {
				Log.e(TAG,
						"Syntax error: in_vehicle_devices.url (" + url + ")", e);
				return;
			}
			runSession(uri, authenticationToken);
		} catch (RuntimeException e) {
			// ExecutorService上で例外が発生すると、どこにも表示されないためここでログに出しておく
			Log.e(TAG, "Unexpected RuntimeException", e);
			throw e;
		}
	}

	protected void submitRetry() {
		executorService.schedule(this, 5, TimeUnit.SECONDS);
	}

	protected void doHttpGet(URI baseUri, String resource,
			String authenticationToken, Callback callback) {
		doHttpRequest(baseUri, resource, authenticationToken, new HttpGet(),
				callback);
	}

	protected void doHttpPatch(URI baseUri, String resource,
			String authenticationToken, ObjectNode rootNode, Callback callback) {
		doHttpEntityEnclosingRequest(baseUri, resource, authenticationToken,
				new HttpPatch(), rootNode, callback);
	}

	protected void doHttpPost(URI baseUri, String resource,
			String authenticationToken, ObjectNode rootNode, Callback callback) {
		doHttpEntityEnclosingRequest(baseUri, resource, authenticationToken,
				new HttpPost(), rootNode, callback);
	}

	private void doHttpEntityEnclosingRequest(URI baseUri, String resource,
			String authenticationToken, HttpEntityEnclosingRequestBase request,
			ObjectNode rootNode, Callback callback) {
		rootNode.put(AUTHENTICATION_TOKEN_KEY, authenticationToken);
		try {
			request.setEntity(new StringEntity(rootNode.toString(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e); // fatal
		}
		URIBuilder uriBuilder = new URIBuilder(baseUri);
		Log.i(getClass().getSimpleName(), "HTTP " + request.getMethod() + " "
				+ uriBuilder + " " + rootNode);
		doSessionAndCallback(uriBuilder, resource, request, callback);
	}
	private void doHttpRequest(URI baseUri, String resource,
			String authenticationToken, HttpRequestBase request,
			Callback callback) {
		URIBuilder uriBuilder = new URIBuilder(baseUri);
		uriBuilder.addParameter(AUTHENTICATION_TOKEN_KEY, authenticationToken);
		Log.i(getClass().getSimpleName(), "HTTP " + request.getMethod() + " "
				+ uriBuilder);
		doSessionAndCallback(uriBuilder, resource, request, callback);
	}

	private void doSessionAndCallback(URIBuilder uriBuilder, String resource,
			HttpRequestBase request, Callback callback) {
		uriBuilder.setPath("/in_vehicle_devices/" + resource);
		URI uri;
		try {
			uri = uriBuilder.build();
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e); // fatal exception
		}
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Accept", "application/json");
		request.setURI(uri);
		byte[] entity = new byte[0];
		HttpResponse response;
		HttpClient client = new DefaultHttpClient();
		try {
			response = client.execute(request);
			HttpEntity entityStream = response.getEntity();
			if (entityStream != null) {
				entity = EntityUtils.toByteArray(entityStream);
			}
		} catch (ClientProtocolException e) {
			callback.onException(e);
			return;
		} catch (IOException e) {
			callback.onException(e);
			return;
		} finally {
			client.getConnectionManager().shutdown();
		}
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode / 100 == 4 || statusCode / 100 == 5) {
			callback.onFailure(response, entity);
		} else {
			callback.onSuccess(response, entity);
		}
	}
}
