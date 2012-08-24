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
import android.preference.PreferenceManager;
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
	private static final String[] DROPBOX_TAGS = { "APANIC_CONSOLE",
			"APANIC_THREADS", "BATTERY_DISCHARGE_INFO", "SYSTEM_BOOT",
			"SYSTEM_LAST_KMSG", "SYSTEM_RECOVERY_LOG", "SYSTEM_RESTART",
			"SYSTEM_TOMBSTONE", "data_app_anr", "data_app_strictmode",
			"data_app_wtf", "data_app_crash", "system_app_anr",
			"system_app_crash", "system_app_wtf", "system_server_anr",
			"system_server_crash", "system_server_wtf", };
	public static final Integer LAST_CHECK_DATE_LIMIT_DAYS = 5;
	private final DropBoxManager dropBoxManager;
	private final OutputStream outputStream;
	private final Context context;

	public DropBoxThread(OutputStream outputStream, Context context) {
		this.outputStream = outputStream;
		this.context = context;
		dropBoxManager = (DropBoxManager) context
				.getSystemService(Context.DROPBOX_SERVICE);
	}

	void save(String tag, Date lastCheckDate, Date nextCheckDate) {
		Long lastEntryTimeMillis = lastCheckDate.getTime();
		while (true) {
			DropBoxManager.Entry entry = dropBoxManager.getNextEntry(
			/* tag */null, lastEntryTimeMillis);
			InputStream inputStream = null;
			try {
				if (entry == null) {
					break;
				}
				if (entry.getTimeMillis() > nextCheckDate.getTime()) {
					break;
				}

				JSONObject header = new JSONObject();
				try {
					header.put("timeMillis", entry.getTimeMillis());
					header.put("tag", entry.getTag());
					header.put("flags", entry.getFlags());
					header.put("describeContents", entry.describeContents());
				} catch (JSONException e) {
					Log.w(TAG, e);
				}
				Charset c = Charsets.UTF_8;
				outputStream.write(("{\"header\":" + header).getBytes(c));
				inputStream = entry.getInputStream(); // 非常に大きなデータの可能性があるため、一度に全て読み出さないようにする
				if (inputStream != null) {
					outputStream.write(", \"body\":\"".getBytes(c));
					OutputStream base64OutputStream = null;
					try {
						base64OutputStream = new Base64OutputStream(
								outputStream, Base64.DEFAULT | Base64.NO_CLOSE
										| Base64.NO_WRAP);
						ByteStreams.copy(inputStream, base64OutputStream);
					} finally {
						Closeables.closeQuietly(base64OutputStream);
						outputStream.write("\"".getBytes(c));
					}
				}
				outputStream.write("}\n".getBytes(c));
			} catch (IOException e) {
				Log.w(TAG, e);
			} finally {
				Closeables.closeQuietly(inputStream); // StrictModeの警告よけ
				Closeables.closeQuietly(entry);
			}
			lastEntryTimeMillis = entry.getTimeMillis();
		}
	}

	/**
	 * DropBoxManagerを最後にチェックした日付を取得する。 ただし、指定日数以上前の場合は、指定日数前に丸める。
	 */
	@VisibleForTesting
	public static Date getLastCheckDate(Context context) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
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

	@Override
	public void run() {
		Log.i(TAG, "start");
		Date lastCheckDate = getLastCheckDate(context);
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		try {
			while (true) {
				Date nextCheckDate = new Date();
				// for (String tag : DROPBOX_TAGS) {
				// save(tag, lastCheckDate, nextCheckDate);
				save("", lastCheckDate, nextCheckDate);
				// Thread.sleep(1000);
				// }

				lastCheckDate = nextCheckDate;
				sharedPreferences
						.edit()
						.putLong(LAST_CHECKED_DATE_KEY, lastCheckDate.getTime())
						.commit();
				Thread.sleep(20 * 1000);
			}
		} catch (InterruptedException e) {
		}
		Log.i(TAG, "exit");
	}
}
