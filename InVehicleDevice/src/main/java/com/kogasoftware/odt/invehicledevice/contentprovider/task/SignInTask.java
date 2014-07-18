package com.kogasoftware.odt.invehicledevice.contentprovider.task;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpResponse;
import org.apache.http.conn.HttpHostConnectException;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevices;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedules;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecords;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Platforms;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Reservations;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProviders;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLogs;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Users;
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
		try {
			database.beginTransaction();
			ContentValues contentValues = new ContentValues();
			contentValues.put(InVehicleDevices.Columns.AUTHENTICATION_TOKEN,
					authenticationToken);
			String whereClause = InVehicleDevices.Columns._ID + " = ?";
			String[] whereArgs = new String[]{id.toString()};
			Integer affected = database.update(InVehicleDevices.TABLE_NAME,
					contentValues, whereClause, whereArgs);
			if (affected == 0) {
				return;
			}
			// TODO: More elegant way
			for (String tableName : new String[]{
					ServiceUnitStatusLogs.TABLE_NAME,
					VehicleNotifications.TABLE_NAME, Reservations.TABLE_NAME,
					Users.TABLE_NAME, PassengerRecords.TABLE_NAME,
					OperationSchedules.TABLE_NAME, OperationRecords.TABLE_NAME,
					ServiceProviders.TABLE_NAME, Platforms.TABLE_NAME}) {
				database.delete(tableName, null, null);
			}
			contentResolver.notifyChange(ContentUris.withAppendedId(
					InVehicleDevices.CONTENT.URI, id), null);
			contentResolver.notifyChange(ServiceProviders.CONTENT.URI, null);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
		}
	}

	void runSession(final Long id, String url, String login, String password) {
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			sendSignInFailureBroadcast(e);
			return;
		}

		ObjectNode node = JSON.createObjectNode();
		node.put(InVehicleDevices.Columns.LOGIN, login);
		node.put(InVehicleDevices.Columns.PASSWORD, password);
		ObjectNode rootNode = JSON.createObjectNode();
		rootNode.set("in_vehicle_device", node);

		doHttpPost(uri, "sign_in", null, rootNode, new Callback() {
			@Override
			public void onSuccess(HttpResponse response, byte[] entity) {
				complete(id, new String(entity, Charsets.UTF_8));
			}

			@Override
			public void onFailure(HttpResponse response, byte[] entity) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode / 100 == 4) {
					sendSignInFailureBroadcast(new IOException(
							"車載器の認証に失敗しました。\ncode=" + statusCode));
				} else {
					sendSignInFailureBroadcast(new IOException(
							"車載器の認証処理中にエラーが発生しました。\ncode=" + statusCode));
				}
			}

			@Override
			public void onException(IOException e) {
				if (e instanceof HttpHostConnectException) {
					sendSignInFailureBroadcast(context
							.getString(R.string.error_connection));
				} else {
					sendSignInFailureBroadcast(e);
				}
			}
		});
	}

	@Override
	public void run() {
		String[] columns = new String[]{InVehicleDevices.Columns._ID,
				InVehicleDevices.Columns.URL, InVehicleDevices.Columns.LOGIN,
				InVehicleDevices.Columns.PASSWORD,
				InVehicleDevices.Columns.AUTHENTICATION_TOKEN,};
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
		runSession(id, url, login, password);
	}

	void sendSignInFailureBroadcast(String message) {
		context.sendBroadcast(new SignInErrorBroadcastIntent(message));
	}

	void sendSignInFailureBroadcast(Exception cause) {
		sendSignInFailureBroadcast(cause.getClass().getSimpleName() + ":"
				+ cause.getMessage());
	}

	private void complete(Long id, String entity) {
		JsonNode node;
		try {
			node = JSON.readTree(entity);
		} catch (IOException e) {
			Log.e(TAG, "IOException while parsing entity: " + entity, e);
			submitRetry();
			return;
		}
		writeAuthenticationToken(id, node.path("authentication_token").asText());
		for (Runnable onCompleteListener : onCompleteListeners) {
			onCompleteListener.run();
		}
	}
}
