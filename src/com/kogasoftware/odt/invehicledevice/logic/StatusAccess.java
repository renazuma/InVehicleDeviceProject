package com.kogasoftware.odt.invehicledevice.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource;

/**
 * InVehicleDeviceStatusのアクセスに対し 書き込みがあったら自動で保存. 読み書き時にロックを実行を行う
 */
public class StatusAccess {
	private static final Object FILE_ACCESS_LOCK = new Object(); // ファイルアクセス中のスレッドを一つに制限するためのロック。将来的にはロックの粒度をファイル毎にする必要があるかもしれない。

	public interface Reader<T> {
		T read(Status status);
	}

	public static class ReadOnlyStatusAccess {
		private final StatusAccess statusAccess;

		private ReadOnlyStatusAccess(StatusAccess statusAccess) {
			this.statusAccess = statusAccess;
		}

		public <T> T read(Reader<T> reader) {
			return statusAccess.read(reader);
		}

		public void read(VoidReader reader) {
			statusAccess.read(reader);
		}
	}

	private static class SaveThread extends Thread {
		private final File file;
		private final byte[] serialized;

		public SaveThread(File file, byte[] serialized) {
			this.file = file;
			this.serialized = serialized;
		}

		@Override
		public void run() {
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
	}

	public interface VoidReader {
		void read(Status status);
	}

	public interface Writer {
		void write(Status status);
	}

	private static final AtomicBoolean willClearStatusFile = new AtomicBoolean(
			false);

	private static final String TAG = StatusAccess.class.getSimpleName();

	public static void clearSavedFile() {
		willClearStatusFile.set(true);
	}

	private static Status newStatusInstance() {
		return new Status();
	}

	/**
	 * isClearがtrueか、
	 * SharedPreferenceのCLEAR_REQUIRED_SHARED_PREFERENCE_KEYがtrueの場合
	 * 新しいStatusオブジェクトを作る。それ以外の場合はファイルからStatusオブジェクトを作る。
	 * 
	 * CLEAR_REQUIRED_SHARED_PREFERENCE_KEYにより新しいオブジェクトが作られた場合、
	 * CLEAR_REQUIRED_SHARED_PREFERENCE_KEYはfalseに設定する
	 */
	private static Status newStatusInstance(Context context) {
		Boolean isClear = willClearStatusFile.getAndSet(false);
		Status status = new Status();
		File file = new File(context.getFilesDir() + File.separator
				+ Status.class.getCanonicalName() + ".serialized");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (isClear) {
			synchronized (FILE_ACCESS_LOCK) {
				if (!file.delete()) {
					Log.e(TAG, "!\"" + file + "\".delete()");
				}
			}
		} else if (preferences.getBoolean(
				SharedPreferencesKey.CLEAR_STATUS_BACKUP, false)) {
			synchronized (FILE_ACCESS_LOCK) {
				if (!file.delete()) {
					Log.e(TAG, "!\"" + file + "\".delete()");
				}
			}
			preferences
					.edit()
					.putBoolean(SharedPreferencesKey.CLEAR_STATUS_BACKUP, false)
					.commit();
		} else if (!file.exists()) {
		} else {
			synchronized (FILE_ACCESS_LOCK) {
				try {
					Object object = SerializationUtils
							.deserialize(new FileInputStream(file));
					if (object instanceof Status) {
						status = (Status) object;
					}
				} catch (SerializationException e) {
					Log.e(TAG, e.toString(), e);
				} catch (IOException e) {
					Log.w(TAG, e);
				}
			}

			Date now = CommonLogic.getDate();
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.set(now.getYear(), now.getMonth(), now.getDay(), 3, 0); // TODO
			if (status.createdDate.before(calendar.getTime())) {
				status = new Status();
			}
		}
		status.file = file;
		status.token = preferences.getString(
				SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN, "");
		status.url = preferences.getString(SharedPreferencesKey.SERVER_URL,
				WebAPIDataSource.DEFAULT_URL);
		return status;
	}

	private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

	private final Lock readLock = reentrantReadWriteLock.readLock();
	private final Lock writeLock = reentrantReadWriteLock.writeLock();
	private final Status status;

	/**
	 * 空のStatusを生成する。
	 */
	public StatusAccess() {
		status = newStatusInstance();
	}

	public StatusAccess(Context context) {
		status = newStatusInstance(context);
	}

	public ReadOnlyStatusAccess getReadOnlyStatusAccess() {
		return new ReadOnlyStatusAccess(this);
	}

	public <T> T read(Reader<T> reader) {
		readLock.lock();
		try {
			return reader.read(status);
		} finally {
			readLock.unlock();
		}
	}

	public void read(final VoidReader reader) {
		read(new Reader<Void>() {
			@Override
			public Void read(Status status) {
				reader.read(status);
				return null;
			}
		});
	}

	private void save(final File file) {
		// ByteArrayへの変換を呼び出し元スレッドで行う
		long startTime = System.currentTimeMillis();
		byte[] serialized = new byte[0];
		try {
			readLock.lock();
			try {
				serialized = SerializationUtils.serialize(status);
			} finally {
				readLock.unlock();
			}
		} catch (SerializationException e) {
			Log.e(TAG, e.toString(), e);
			return;
		} finally {
			long stopTime = System.currentTimeMillis();
			long runTime = stopTime - startTime;
			Log.d(TAG, "StatusAccess.save() " + runTime + " ms");
		}

		// ByteArrayへの変換後は、呼び出し元スレッドでのファイルIOを避けるため新しいスレッドでデータを書き込む
		(new SaveThread(file, serialized)).start();
	}

	public void write(Writer writer) {
		writeLock.lock();
		try {
			writer.write(status);
		} finally {
			writeLock.unlock();
		}

		// findbugsの警告回避ができないため、Lockのダウングレードはしないでおく
		save(status.file);
	}
}
