package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.DropBoxManager;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class DropBoxThread extends LogCollectorThread {
	private static final String LAST_CHECKED_DATE_KEY = "last_checked_date_key";
	private static final String TAG = DropBoxThread.class.getSimpleName();
	private static final String[] DROPBOX_TAGS = { "APANIC_CONSOLE",
			"APANIC_THREADS", "BATTERY_DISCHARGE_INFO", "SYSTEM_BOOT",
			"SYSTEM_LAST_KMSG", "SYSTEM_RECOVERY_LOG", "SYSTEM_RESTART",
			"SYSTEM_TOMBSTONE", "data_app_anr", "data_app_strictmode",
			"data_app_wtf", "data_app_crash", "system_app_anr",
			"system_app_crash", "system_app_wtf", "system_server_anr",
			"system_server_crash", "system_server_wtf", };
	private final DropBoxManager dropBoxManager;

	public DropBoxThread(Context context, File dataDirectory,
			BlockingQueue<File> rawLogFiles) {
		super(context, dataDirectory, rawLogFiles, "dropbox");
		dropBoxManager = (DropBoxManager) context
				.getSystemService(Context.DROPBOX_SERVICE);
	}

	void save(String tag, Date lastCheckDate, Date nextCheckDate) {
		Long lastEntryTimeMillis = lastCheckDate.getTime();
		while (true) {
			DropBoxManager.Entry entry = dropBoxManager.getNextEntry(/* tag */ null,
					lastEntryTimeMillis);
			InputStream inputStream = null;
			try {
				if (entry == null) {
					break;
				}
				if (entry.getTimeMillis() > nextCheckDate.getTime()) {
					break;
				}

				JSONObject metadata = new JSONObject();
				try {
					metadata.put("timeMillis", entry.getTimeMillis());
					metadata.put("tag", entry.getTag());
					metadata.put("flags", entry.getFlags());
					metadata.put("describeContents", entry.describeContents());
				} catch (JSONException e) {
					Log.w(TAG, e);
				}
				getOutputStream().write(metadata.toString().getBytes(
						Charsets.UTF_8));
				inputStream = entry.getInputStream(); // 非常に大きなデータの可能性があるため、一度に全て読み出さないようにする
				if (inputStream != null) {
					OutputStream base64OutputStream = null;
					try {
						base64OutputStream = new Base64OutputStream(
								getOutputStream(), Base64.DEFAULT | Base64.NO_CLOSE);
						ByteStreams.copy(inputStream, base64OutputStream);
					} finally {
						Closeables.closeQuietly(base64OutputStream);
					}
				}
			} catch (IOException e) {
				Log.w(TAG, e);
			} finally {
				Closeables.closeQuietly(inputStream); // StrictModeの警告よけ
				Closeables.closeQuietly(entry);
			}
			lastEntryTimeMillis = entry.getTimeMillis();
		}
	}

	@Override
	public void run() {
		Log.i(TAG, "start");
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -5);
		Date minLastCheckDate = calendar.getTime();
		Date lastCheckDate = new Date(sharedPreferences.getLong(
				LAST_CHECKED_DATE_KEY, 0L));
		if (minLastCheckDate.after(lastCheckDate)) {
			lastCheckDate = minLastCheckDate;
			Log.i(TAG, "lastCheckDate changed");
		}
		Log.i(TAG, "lastCheckDate=" + lastCheckDate);
		try {
			while (true) {
				Date nextCheckDate = new Date();
				// for (String tag : DROPBOX_TAGS) {
				// save(tag, lastCheckDate, nextCheckDate);
				// Thread.sleep(1000);
				// }
				save("", lastCheckDate, nextCheckDate);

				lastCheckDate = nextCheckDate;
				flush();
				sharedPreferences
						.edit()
						.putLong(LAST_CHECKED_DATE_KEY, lastCheckDate.getTime())
						.commit();
				Thread.sleep(20 * 1000);

				try { // 何か書き込むことで読み込み待ちスレッドを起こし、時間によるログローテートができるようにする
					getOutputStream().write('\n');
				} catch (IOException e) {
					Log.w(TAG, e);
				}
			}
		} catch (InterruptedException e) {
		}
		Log.i(TAG, "exit");
	}
}
