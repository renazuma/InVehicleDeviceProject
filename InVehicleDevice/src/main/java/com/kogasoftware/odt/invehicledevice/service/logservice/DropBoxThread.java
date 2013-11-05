package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTimeUtils;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.DropBoxManager;
import android.os.DropBoxManager.Entry;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;

public class DropBoxThread extends Thread {
	private static final String LAST_CHECKED_DATE_KEY = "last_checked_date_key";
	private static final String TAG = DropBoxThread.class.getSimpleName();
	public static final Long DEFAULT_SPLIT_BYTES = 2 * 1024 * 1024L;
	public static final Long DEFAULT_TIMEOUT_MILLIS = 60 * 60 * 1000L;
	public static final Long DEFAULT_CHECK_INTERVAL_MILLIS = 30 * 1000L;
	private static final String SHARED_PREFERENCES_NAME = DropBoxThread.class
			.getSimpleName() + ".sharedpreferences";
	public static final Charset CHARSET = Charsets.UTF_8;
	public static final Integer LAST_CHECK_DATE_LIMIT_DAYS = 5;
	public static final String DELIMITER = "\u0001";
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

	/**
	 * DropBoxManager.EntryをJSON形式に変換してストリームへ保存する。
	 * 非常に大きくなる可能性のあるデータをストリームのまま扱わなければいけないため、直接JSONを組み立てる。
	 */
	private void write(DropBoxManager.Entry entry) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		json.append("\"time_millis\":" + entry.getTimeMillis() + ",");
		json.append("\"tag\":" + JSONObject.quote(entry.getTag()) + ",");
		json.append("\"flags\":" + entry.getFlags() + ",");
		json.append("\"describe_contents\":" + entry.describeContents());

		try {
			splitFileOutputStream.write(json.toString().getBytes(CHARSET));
			writeStream(entry);
			splitFileOutputStream.write(("}" + DELIMITER + "\n").getBytes(CHARSET));
			splitFileOutputStream.flush();
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}

	private void writeStream(Entry entry) throws IOException {
		Closer closer = Closer.create();
		try {
			// 非常に大きなデータの可能性があるため、一度に全て読み出さないようにする
			InputStream inputStream = closer.register(entry.getInputStream());
			if (inputStream == null) {
				return;
			}
			splitFileOutputStream.write(",\"contents\":\"".getBytes(CHARSET));
			Base64OutputStream base64OutputStream = closer
					.register(new Base64OutputStream(splitFileOutputStream,
							Base64.DEFAULT | Base64.NO_CLOSE | Base64.NO_WRAP));
			ByteStreams.copy(inputStream, base64OutputStream);
			splitFileOutputStream.write("\"".getBytes(CHARSET));
		} catch (Throwable e) {
			closer.rethrow(e);
		} finally {
			closer.close();
		}
	}

	/**
	 * DropBoxManagerを最後にチェックした日付を取得する。 ただし、指定日数以上前の場合は、指定日数前に丸める。
	 */
	@VisibleForTesting
	public static Date getLastCheckDate(SharedPreferences sharedPreferences) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(DateTimeUtils.currentTimeMillis());
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
		try {
			while (true) {
				try {
					dumpToFile();
				} catch (IOException e) {
					Log.w(TAG, e);
				}
				Thread.sleep(checkIntervalMillis);
			}
		} catch (InterruptedException e) {
		} finally {
			try {
				splitFileOutputStream.close();
			} catch (IOException e) {
				Log.w(TAG, e);
			}
		}
		Log.i(TAG, "exit");
	}

	public void dumpToFile() throws IOException {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		Date lastCheckDate = getLastCheckDate(sharedPreferences);
		if (splitFileOutputStream.getElapsedMillisSinceFirstWrite() > timeoutMillis) {
			splitFileOutputStream.split();
		}
		Date nextCheckDate = new Date();
		// for (String tag : DROPBOX_TAGS) {
		Long lastEntryTimeMillis = lastCheckDate.getTime();
		while (true) {
			if (Thread.currentThread().isInterrupted()) {
				return;
			}
			Closer closer = Closer.create();
			try {
				DropBoxManager.Entry entry = dropBoxManager.getNextEntry(
				/* tag */null, lastEntryTimeMillis);
				if (entry == null) {
					break;
				}
				closer.register(entry);
				write(entry);
				if (splitFileOutputStream.getCount() > splitBytes) {
					splitFileOutputStream.split();
				}
				lastEntryTimeMillis = entry.getTimeMillis();
			} catch (Throwable e) {
				throw closer.rethrow(e);
			} finally {
				closer.close();
			}
		}
		// }
		lastCheckDate = nextCheckDate;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(LAST_CHECKED_DATE_KEY, lastCheckDate.getTime());
		editor.commit();
	}
}
