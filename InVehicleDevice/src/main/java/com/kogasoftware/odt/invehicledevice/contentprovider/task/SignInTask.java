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
import com.kogasoftware.odt.invehicledevice.contentprovider.table.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Platform;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.Reservation;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.User;
import com.kogasoftware.odt.invehicledevice.contentprovider.table.VehicleNotification;

/**
 * サインインAPIとの通信
 */
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
			contentValues.put(InVehicleDevice.Columns.AUTHENTICATION_TOKEN,
					authenticationToken);
			String whereClause = InVehicleDevice.Columns._ID + " = ?";
			String[] whereArgs = new String[]{id.toString()};
			Integer affected = database.update(InVehicleDevice.TABLE_NAME,
					contentValues, whereClause, whereArgs);
			if (affected == 0) {
				return;
			}
			// TODO: More elegant way
			for (String tableName : new String[]{
					ServiceUnitStatusLog.TABLE_NAME,
					VehicleNotification.TABLE_NAME, Reservation.TABLE_NAME,
					User.TABLE_NAME, PassengerRecord.TABLE_NAME,
					OperationSchedule.TABLE_NAME, OperationRecord.TABLE_NAME,
					ServiceProvider.TABLE_NAME, Platform.TABLE_NAME}) {
				database.delete(tableName, null, null);
			}
			contentResolver.notifyChange(ContentUris.withAppendedId(
					InVehicleDevice.CONTENT.URI, id), null);
			contentResolver.notifyChange(ServiceProvider.CONTENT.URI, null);
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
		node.put(InVehicleDevice.Columns.LOGIN, login);
		node.put(InVehicleDevice.Columns.PASSWORD, password);
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
		String[] columns = new String[]{InVehicleDevice.Columns._ID,
				InVehicleDevice.Columns.URL, InVehicleDevice.Columns.LOGIN,
				InVehicleDevice.Columns.PASSWORD,
				InVehicleDevice.Columns.AUTHENTICATION_TOKEN,};
		Long id;
		String url;
		String login;
		String password;
		String authenticationToken;
		Cursor cursor = database.query(InVehicleDevice.TABLE_NAME, columns,
				null, null, null, null, null);
		try {
			if (!cursor.moveToFirst()) {
				return;
			}
			id = cursor.getLong(cursor
					.getColumnIndexOrThrow(InVehicleDevice.Columns._ID));
			url = cursor.getString(cursor
					.getColumnIndexOrThrow(InVehicleDevice.Columns.URL));
			login = cursor.getString(cursor
					.getColumnIndexOrThrow(InVehicleDevice.Columns.LOGIN));
			password = cursor.getString(cursor
					.getColumnIndexOrThrow(InVehicleDevice.Columns.PASSWORD));
			authenticationToken = cursor
					.getString(cursor
							.getColumnIndexOrThrow(InVehicleDevice.Columns.AUTHENTICATION_TOKEN));
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
