package com.kogasoftware.odt.invehicledevice.service.logservice;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.DropBoxManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class DropBoxThread extends LogCollectorThread {
	private static final String LAST_CHECKED_DATE_KEY = "last_checked_date_key";
	private static final String TAG = DropBoxThread.class.getSimpleName();
	private final DropBoxManager dropBoxManager;
	private final SharedPreferences sharedPreferences;

	public DropBoxThread(Context context, File dataDirectory,
			BlockingQueue<File> rawLogFiles) {
		super(context, dataDirectory, rawLogFiles, "dropbox");
		dropBoxManager = (DropBoxManager) context
				.getSystemService(Context.DROPBOX_SERVICE);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
	}

	void save(String tag, Date lastCheckDate, Date nextCheckDate) {
		Long lastEntryTimeMillis = lastCheckDate.getTime();
		while (true) {
			DropBoxManager.Entry entry = dropBoxManager.getNextEntry(tag,
					lastEntryTimeMillis);
			if (entry == null) {
				break;
			}
			if (entry.getTimeMillis() > nextCheckDate.getTime()) {
				break;
			}
			try {
				pipedOutputStream.write(("===== TAG: " + entry.getTag() + " =====\n")
						.getBytes(Charsets.UTF_8));
				InputStream inputStream = entry.getInputStream();
				if (inputStream != null) {
					ByteStreams.copy(inputStream, pipedOutputStream);
				}
			} catch (IOException e) {
				Log.w(TAG, e);
			}
			lastEntryTimeMillis = entry.getTimeMillis();
		}
	}

	@Override
	public void run() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -5);
		Date minLastCheckDate = calendar.getTime();
		Date lastCheckDate = new Date(sharedPreferences.getLong(
				LAST_CHECKED_DATE_KEY, 0L));
		if (minLastCheckDate.after(lastCheckDate)) {
			lastCheckDate = minLastCheckDate;
		}
		try {
			while (true) {
				Date nextCheckDate = new Date();
				for (String tag : new String[] { "system_app_anr",
						"SYSTEM_RESTART" }) {
					save(tag, lastCheckDate, nextCheckDate);
					Thread.sleep(1000);
				}
				pipedOutputStream.flush();

				lastCheckDate = nextCheckDate;
				flush();
				sharedPreferences
						.edit()
						.putLong(LAST_CHECKED_DATE_KEY, lastCheckDate.getTime())
						.commit();
				Thread.sleep(10 * 1000);
			}
		} catch (InterruptedException e) {
		} catch (IOException e) {
			Log.w(TAG, e);
		}
	}
}
