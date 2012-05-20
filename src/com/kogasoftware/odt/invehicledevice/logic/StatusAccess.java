package com.kogasoftware.odt.invehicledevice.logic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
		private final ByteArrayOutputStream byteArrayOutputStream;

		public SaveThread(File file, ByteArrayOutputStream byteArrayOutputStream) {
			this.file = file;
			this.byteArrayOutputStream = byteArrayOutputStream;
		}

		@Override
		public void run() {
			synchronized (FILE_ACCESS_LOCK) {
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(file);
					fileOutputStream.getChannel().lock();
					fileOutputStream.write(byteArrayOutputStream.toByteArray());
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
				FileInputStream fileInputStream = null;
				ObjectInputStream objectInputStream = null;
				try {
					fileInputStream = new FileInputStream(file);
					objectInputStream = new ObjectInputStream(fileInputStream);
					Object object = objectInputStream.readObject();
					if (object instanceof Status) {
						status = (Status) object;
					}
				} catch (IOException e) {
					Log.w(TAG, e);
				} catch (RuntimeException e) {
					Log.w(TAG, e);
				} catch (ClassNotFoundException e) {
					Log.w(TAG, e);
				} catch (Exception e) {
					Log.w(TAG, e);
				} finally {
					Closeables.closeQuietly(objectInputStream);
					Closeables.closeQuietly(fileInputStream);
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
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			readLock.lock();
			try {
				objectOutputStream.writeObject(status);
			} finally {
				readLock.unlock();
			}
		} catch (NotSerializableException e) {
			Log.e(TAG, e.toString(), e);
			return;
		} catch (IOException e) {
			Log.w(TAG, e);
			return;
		} finally {
			Closeables.closeQuietly(objectOutputStream);
			Closeables.closeQuietly(byteArrayOutputStream);
			long stopTime = System.currentTimeMillis();
			long runTime = stopTime - startTime;
			Log.d(TAG, "StatusAccess.save() " + runTime + " ms");
		}

		// ByteArrayへの変換後は、呼び出し元スレッドでのファイルIOを避けるため新しいスレッドでデータを書き込む
		(new SaveThread(file, byteArrayOutputStream)).start();
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
