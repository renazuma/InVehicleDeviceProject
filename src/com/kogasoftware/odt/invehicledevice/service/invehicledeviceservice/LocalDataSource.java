package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.datasource.WebAPIDataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.ServiceProvider;

/**
 * LocalDataのアクセスに対し 書き込みがあったら自動で保存. 読み書き時にロックを実行を行う
 */
public class LocalDataSource implements Closeable {
	public interface Reader<T> {
		T read(LocalData localData);
	}

	private class SaveThread extends Thread {
		private final File file;

		public SaveThread(File file) {
			this.file = file;
		}
		
		private void save() {
			long startTime = System.currentTimeMillis();
			byte[] serialized = new byte[0];
			try {
				readLock.lock();
				try {
					serialized = SerializationUtils.serialize(localData);
				} finally {
					readLock.unlock();
				}
			} catch (SerializationException e) {
				Log.e(TAG, e.toString(), e);
				return;
			} finally {
				long stopTime = System.currentTimeMillis();
				long runTime = stopTime - startTime;
				Log.d(TAG, "LocalDataSource.save() " + runTime + " ms");
			}

			synchronized (FILE_ACCESS_LOCK) {
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(file);
					fileOutputStream.getChannel().lock();
					fileOutputStream.write(serialized);
				} catch (FileNotFoundException e) {
					Log.w(TAG, e);
				} catch (IOException e) {
					Log.w(TAG, e);
				} finally {
					Closeables.closeQuietly(fileOutputStream);
				}
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					Thread.sleep(SAVE_PERIO);
					saveSemaphore.acquire();
					saveSemaphore.drainPermits();
					save();
				}
			} catch (InterruptedException e) {
				save(); // アプリ終了時は必ずsaveを行う
			}
		}
	}

	public interface VoidReader {
		void read(LocalData localData);
	}

	public interface Writer {
		void write(LocalData localData);
	}

	private static final Object FILE_ACCESS_LOCK = new Object(); // ファイルアクセス中のスレッドを一つに制限するためのロック。将来的にはロックの粒度をファイル毎にする必要があるかもしれない。

	private static final Integer SAVE_PERIO = 5 * 60 * 1000;

	private static final AtomicBoolean WILL_CLEAR_SAVED_FILE = new AtomicBoolean(
			false);

	private static final String TAG = LocalDataSource.class.getSimpleName();

	public static void clearSavedFile() {
		WILL_CLEAR_SAVED_FILE.set(true);
	}

	/**
	 * isClearがtrueか、
	 * SharedPreferenceのCLEAR_REQUIRED_SHARED_PREFERENCE_KEYがtrueの場合
	 * 新しいLocalDataオブジェクトを作る。それ以外の場合はファイルからLocalDataオブジェクトを作る。
	 * 
	 * CLEAR_REQUIRED_SHARED_PREFERENCE_KEYにより新しいオブジェクトが作られた場合、
	 * CLEAR_REQUIRED_SHARED_PREFERENCE_KEYはfalseに設定する
	 */
	private static LocalData newStatusInstanceFromFile(Context context) {
		Boolean isClear = WILL_CLEAR_SAVED_FILE.getAndSet(false);
		LocalData localData = new LocalData();
		File file = new File(context.getFilesDir() + File.separator
				+ LocalData.class.getCanonicalName() + ".serialized");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (preferences.getBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP,
				false)) {
			isClear = true;
			preferences
					.edit()
					.putBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP, false)
					.commit();
		}

		if (!file.exists()) {
			Log.i(TAG, "\"" + file + "\" not found");
		} else if (isClear) {
			if (!file.delete()) {
				Log.e(TAG, "!\"" + file + "\".delete()");
			}
		} else {
			try {
				Object object = SerializationUtils
						.deserialize(new FileInputStream(file));
				if (object instanceof LocalData) {
					localData = (LocalData) object;
				}

			} catch (IndexOutOfBoundsException e) {
				Log.e(TAG, e.toString(), e);
			} catch (SerializationException e) {
				Log.e(TAG, e.toString(), e);
			} catch (IOException e) {
				Log.w(TAG, e);
			}

			Calendar now = Calendar.getInstance();
			now.setTime(InVehicleDeviceService.getDate());
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
					now.get(Calendar.DAY_OF_MONTH),
					InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR,
					InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_MINUTE);
			if (localData.updatedDate.before(calendar.getTime())) {
				localData.operationScheduleInitializedSign.drainPermits();
			}
		}

		localData.file = file;
		localData.token = preferences.getString(
				SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN, "");
		localData.url = preferences.getString(SharedPreferencesKeys.SERVER_URL,
				WebAPIDataSource.DEFAULT_URL);
		try {
			localData.inVehicleDevice = InVehicleDevice.parse(new JSONObject(
					preferences.getString(
							SharedPreferencesKeys.SERVICE_PROVIDER, "{}")));
		} catch (JSONException e) {
			Log.w(TAG, e);
		}
		try {
			localData.serviceProvider = ServiceProvider.parse(new JSONObject(
					preferences.getString(
							SharedPreferencesKeys.IN_VEHICLE_DEVICE, "{}")));
		} catch (JSONException e) {
			Log.e(TAG, "parse JSON failed", e);
		}

		return localData;
	}

	private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

	private final Lock readLock = reentrantReadWriteLock.readLock();
	private final Lock writeLock = reentrantReadWriteLock.writeLock();
	private final LocalData localData;
	private final Semaphore saveSemaphore = new Semaphore(0);
	private Thread saveThread = new EmptyThread();

	public LocalDataSource() {
		localData = new LocalData();
	}

	public LocalDataSource(Context context) {
		synchronized (FILE_ACCESS_LOCK) {
			localData = newStatusInstanceFromFile(context);
		}
		saveThread = new SaveThread(localData.file);
		saveThread.start();
	}

	@Override
	public void close() {
		saveThread.interrupt();
	}

	public <T> T withReadLock(Reader<T> reader) {
		readLock.lock();
		try {
			return reader.read(localData);
		} finally {
			readLock.unlock();
		}
	}

	public void withReadLock(final VoidReader reader) {
		withReadLock(new Reader<Void>() {
			@Override
			public Void read(LocalData status) {
				reader.read(status);
				return null;
			}
		});
	}

	private void save() {
		saveSemaphore.release();
	}

	public void withWriteLock(Writer writer) {
		writeLock.lock();
		try {
			writer.write(localData);
		} finally {
			writeLock.unlock();
		}

		// findbugsの警告回避ができないため、Lockのダウングレードはしないでおく
		save();
	}
}
