package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.DropBoxManager;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;

public class DropBoxThread extends Thread {
	private static final String LAST_CHECKED_DATE_KEY = "last_checked_date_key";
	private static final String TAG = DropBoxThread.class.getSimpleName();
	public static final Long DEFAULT_SPLIT_BYTES = 2 * 1024 * 1024L;
	public static final Long DEFAULT_TIMEOUT_MILLIS = 60 * 60 * 1000L;
	public static final Long DEFAULT_CHECK_INTERVAL_MILLIS = 30 * 1000L;
	private static final String SHARED_PREFERENCES_NAME = DropBoxThread.class
			.getSimpleName() + ".sharedpreferences";
	private static final Charset CHARSET = Charsets.UTF_8;
	private static final String[] DROPBOX_TAGS = { "APANIC_CONSOLE",
			"APANIC_THREADS", "BATTERY_DISCHARGE_INFO", "SYSTEM_BOOT",
			"SYSTEM_LAST_KMSG", "SYSTEM_RECOVERY_LOG", "SYSTEM_RESTART",
			"SYSTEM_TOMBSTONE", "data_app_anr", "data_app_strictmode",
			"data_app_wtf", "data_app_crash", "system_app_anr",
			"system_app_crash", "system_app_wtf", "system_server_anr",
			"system_server_crash", "system_server_wtf", };
	public static final Integer LAST_CHECK_DATE_LIMIT_DAYS = 5;
	private final DropBoxManager dropBoxManager;
	private final SplitFileOutputStream splitFileOutputStream;
	private final Context context;
	private final Long splitBytes;
	private final Long timeoutMillis;
	private final Long checkIntervalMillis;

	public DropBoxThread(Context context,
			SplitFileOutputStream splitFileOutputStream) {
		this(context, splitFileOutputStream, DEFAULT_SPLIT_BYTES,
				DEFAULT_TIMEOUT_MILLIS, DEFAULT_CHECK_INTERVAL_MILLIS);
	}

	@VisibleForTesting
	public DropBoxThread(Context context,
			SplitFileOutputStream splitFileOutputStream, Long splitBytes,
			Long timeoutMillis, Long checkIntervalMillis) {
		this.context = context;
		this.splitBytes = splitBytes;
		this.timeoutMillis = timeoutMillis;
		this.checkIntervalMillis = checkIntervalMillis;
		this.splitFileOutputStream = splitFileOutputStream;
		dropBoxManager = (DropBoxManager) context
				.getSystemService(Context.DROPBOX_SERVICE);
	}

	private void save(DropBoxManager.Entry entry) {
		JSONObject header = new JSONObject();
		try {
			header.put("timeMillis", entry.getTimeMillis());
			header.put("tag", entry.getTag());
			header.put("flags", entry.getFlags());
			header.put("describeContents", entry.describeContents());
		} catch (JSONException e) {
			Log.w(TAG, e);
		}
		Charset c = CHARSET;
		InputStream inputStream = null;
		try {
			if (splitFileOutputStream.getCount().equals(0L)) {
				splitFileOutputStream.write("[".getBytes(c));
			} else {
				splitFileOutputStream.write(",".getBytes(c));
			}
			splitFileOutputStream.write(("{\"header\":" + header).getBytes(c));
			inputStream = entry.getInputStream(); // 非常に大きなデータの可能性があるため、一度に全て読み出さないようにする
			if (inputStream != null) {
				splitFileOutputStream.write(",\"body\":\"".getBytes(c));
				OutputStream base64OutputStream = null;
				try {
					base64OutputStream = new Base64OutputStream(
							splitFileOutputStream, Base64.DEFAULT
									| Base64.NO_CLOSE | Base64.NO_WRAP);
					ByteStreams.copy(inputStream, base64OutputStream);
				} catch (IOException e) {
					Log.w(TAG, e);
				} finally {
					Closeables.closeQuietly(base64OutputStream);
					splitFileOutputStream.write("\"".getBytes(c));
				}
			}
			splitFileOutputStream.write("}\n".getBytes(c));
		} catch (IOException e) {
			Log.w(TAG, e);
		} finally {
			Closeables.closeQuietly(inputStream); // StrictModeの警告よけ
		}
	}

	/**
	 * DropBoxManagerを最後にチェックした日付を取得する。 ただし、指定日数以上前の場合は、指定日数前に丸める。
	 */
	@VisibleForTesting
	public static Date getLastCheckDate(SharedPreferences sharedPreferences) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(InVehicleDeviceService.getDate());
		calendar.add(Calendar.DAY_OF_MONTH, -LAST_CHECK_DATE_LIMIT_DAYS);
		Date minLastCheckDate = calendar.getTime();
		Date lastCheckDate = new Date(sharedPreferences.getLong(
				LAST_CHECKED_DATE_KEY, 0L));
		if (minLastCheckDate.after(lastCheckDate)) {
			lastCheckDate = minLastCheckDate;
			Log.i(TAG, "lastCheckDate changed");
		}
		Log.i(TAG, "lastCheckDate=" + lastCheckDate);
		return lastCheckDate;
	}

	private void splitLogFile() {
		if (splitFileOutputStream.getCount().equals(0L)) {
			return;
		}
		try {
			splitFileOutputStream.write("]".getBytes(CHARSET));
			splitFileOutputStream.split();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}

	@Override
	public void run() {
		Log.i(TAG, "start");
		try {
			while (true) {
				dumpToFile();
				Thread.sleep(checkIntervalMillis);
			}
		} catch (InterruptedException e) {
		} finally {
			splitLogFile();
		}
		Log.i(TAG, "exit");
	}

	public void dumpToFile() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Date lastCheckDate = getLastCheckDate(sharedPreferences);
		if (splitFileOutputStream.getElapsedMillisSinceFirstWrite() > timeoutMillis) {
			splitLogFile();
		}
		Date nextCheckDate = new Date();
		// for (String tag : DROPBOX_TAGS) {
		Long lastEntryTimeMillis = lastCheckDate.getTime();
		while (true) {
			DropBoxManager.Entry entry = null;
			try {
				entry = dropBoxManager.getNextEntry(
				/* tag */null, lastEntryTimeMillis);
				if (entry == null) {
					break;
				}
				save(entry);
				if (splitFileOutputStream.getCount() > splitBytes) {
					splitLogFile();
				}
				lastEntryTimeMillis = entry.getTimeMillis();
			} finally {
				Closeables.closeQuietly(entry);
			}
		}
		// }
		lastCheckDate = nextCheckDate;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(LAST_CHECKED_DATE_KEY, lastCheckDate.getTime());
		editor.commit();
	}
}
