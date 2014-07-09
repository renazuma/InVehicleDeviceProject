package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevices;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProviders;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotifications;

public class SignInTask extends SynchronizationTask {
	static final String TAG = SignInTask.class.getSimpleName();
	private List<Runnable> onCompleteListeners = Lists.newLinkedList();

	public void addOnCompleteListener(Runnable onCompleteListener) {
		onCompleteListeners.add(onCompleteListener);
	}

	public SignInTask(Context context, SQLiteDatabase database,
			ScheduledExecutorService executor) {
		super(context, database, executor);
	}

	void writeAuthenticationToken(Long id, String authenticationToken) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(InVehicleDevices.Columns.AUTHENTICATION_TOKEN,
				authenticationToken);
		String whereClause = InVehicleDevices.Columns._ID + " = ?";
		String[] whereArgs = new String[] { id.toString() };
		Integer affected = database.update(InVehicleDevices.TABLE_NAME,
				contentValues, whereClause, whereArgs);
		if (affected > 0) {
			contentResolver.notifyChange(ContentUris.withAppendedId(
					InVehicleDevices.CONTENT.URI, id), null);
		}
	}

	void runSession(Long id, URI baseUri, String login, String password)
			throws IOException, JSONException {
		HttpClient httpClient = new DefaultHttpClient();
		JSONObject inVehicleDevice = new JSONObject();
		inVehicleDevice.put(InVehicleDevices.Columns.LOGIN, login);
		inVehicleDevice.put(InVehicleDevices.Columns.PASSWORD, password);
		JSONObject root = new JSONObject();
		root.put("in_vehicle_device", inVehicleDevice);
		HttpPost request = new HttpPost();
		request.addHeader("Content-Type", "application/json");
		request.addHeader("Accept", "application/json");
		URI uri = baseUri.resolve("in_vehicle_devices/sign_in");
		request.setURI(uri);
		request.setEntity(new StringEntity(root.toString(), "UTF-8"));
		HttpResponse httpResponse = httpClient.execute(request);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		byte[] response = new byte[] {};
		HttpEntity entity = httpResponse.getEntity();
		if (entity != null) {
			response = EntityUtils.toByteArray(entity);
		}
		if (statusCode / 100 == 4) {
			throw new IOException("車載器の認証に失敗しました。\ncode=" + statusCode);
		} else if (statusCode / 100 == 5) {
			throw new IOException("車載器の認証処理中にエラーが発生しました。\ncode=" + statusCode);
		}
		JSONObject responseJSON = new JSONObject(new String(response,
				Charsets.UTF_8));
		String authenticationToken = responseJSON
				.getString("authentication_token");
		if (authenticationToken != null) {
			writeAuthenticationToken(id, authenticationToken);
		}
	}

	@Override
	public void run() {
		String[] columns = new String[] { InVehicleDevices.Columns._ID,
				InVehicleDevices.Columns.URL, InVehicleDevices.Columns.LOGIN,
				InVehicleDevices.Columns.PASSWORD,
				InVehicleDevices.Columns.AUTHENTICATION_TOKEN, };
		Long id;
		String url;
		String login;
		String password;
		String authenticationToken;
		Cursor cursor = database.query(InVehicleDevices.TABLE_NAME, columns,
				null, null, null, null, null);
		try {
			if (!cursor.moveToFirst()) {
				return;
			}
			id = cursor.getLong(cursor
					.getColumnIndexOrThrow(InVehicleDevices.Columns._ID));
			url = cursor.getString(cursor
					.getColumnIndexOrThrow(InVehicleDevices.Columns.URL));
			login = cursor.getString(cursor
					.getColumnIndexOrThrow(InVehicleDevices.Columns.LOGIN));
			password = cursor.getString(cursor
					.getColumnIndexOrThrow(InVehicleDevices.Columns.PASSWORD));
			authenticationToken = cursor
					.getString(cursor
							.getColumnIndexOrThrow(InVehicleDevices.Columns.AUTHENTICATION_TOKEN));
		} finally {
			cursor.close();
		}

		if (authenticationToken != null) {
			return;
		}

		// TODO: Remove all
		database.delete(ServiceProviders.TABLE_NAME, null, null);
		database.delete(OperationSchedules.TABLE_NAME, null, null);
		database.delete(PassengerRecords.TABLE_NAME, null, null);
		database.delete(VehicleNotifications.TABLE_NAME, null, null);
		contentResolver.notifyChange(ServiceProviders.CONTENT.URI, null);

		try {
			runSession(id, new URI(url), login, password);
		} catch (HttpHostConnectException e) {
			sendSignInFailureBroadcast("サーバーとの接続に失敗しました");
		} catch (IOException e) {
			sendSignInFailureBroadcast(e);
		} catch (URISyntaxException e) {
			sendSignInFailureBroadcast(e);
		} catch (JSONException e) {
			sendSignInFailureBroadcast(e);
		}

		for (Runnable onCompleteListener : onCompleteListeners) {
			onCompleteListener.run();
		}
	}

	void sendSignInFailureBroadcast(String message) {
		context.sendBroadcast(new SignInErrorBroadcastIntent(message));
	}

	void sendSignInFailureBroadcast(Exception cause) {
		sendSignInFailureBroadcast(cause.getClass().getSimpleName() + ":"
				+ cause.getMessage());
	}
}
