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
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.datasource.WebAPIDataSource;

/**
 * InVehicleDeviceStatusのアクセスに対し 書き込みがあったら自動で保存. 読み書き時にロックを実行を行う
 */
public class StatusAccess {
	public interface Reader<T> {
		T read(Status status);
	}

	public interface ReaderAndWriter<T> {
		T readAndWrite(Status status);
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
			FileOutputStream fileOutputStream = null;
			FileLock lock = null;
			try {
				fileOutputStream = new FileOutputStream(file);
				lock = fileOutputStream.getChannel().lock();
				fileOutputStream.write(byteArrayOutputStream.toByteArray());
			} catch (FileNotFoundException e) {
				Log.w(TAG, e);
			} catch (IOException e) {
				Log.w(TAG, e);
			} finally {
				Closeables.closeQuietly(fileOutputStream);
				if (lock != null) {
					try {
						lock.release();
					} catch (ClosedChannelException e) {
						// do nothing
					} catch (IOException e) {
						Log.w(TAG, e);
					}
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

	private static final String TAG = StatusAccess.class.getSimpleName();

	private static Status newStatusInstance() {
		return new Status();
	}

	private static Status newStatusInstance(Context context, Boolean isClear) {
		Status status = new Status();
		File file = new File(context.getFilesDir() + File.separator
				+ Status.class.getCanonicalName() + ".serialized");
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (isClear) {
		} else if (preferences.getBoolean("update", false)) {
			preferences.edit().putBoolean("update", false).commit();
		} else {
			FileInputStream fileInputStream = null;
			FileLock lock = null;
			ObjectInputStream objectInputStream = null;
			try {
				fileInputStream = new FileInputStream(file);
				lock = fileInputStream.getChannel().lock();
				objectInputStream = new ObjectInputStream(fileInputStream);
				Object object = objectInputStream.readObject();
				if (object instanceof Status) {
					status = (Status) object;
				}
			} catch (FileNotFoundException e) {
				// Log.w(TAG, e);
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
				if (lock != null) {
					try {
						lock.release();
					} catch (ClosedChannelException e) {
						// do nothing
					} catch (IOException e) {
						Log.w(TAG, e);
					}
				}
			}

			Date now = Logic.getDate();
			Calendar calendar = Calendar.getInstance();
			calendar.clear();
			calendar.set(now.getYear(), now.getMonth(), now.getDay(), 3, 0); // TODO
			if (status.createdDate.before(calendar.getTime())) {
				status = new Status();
			}
		}
		status.file = file;
		status.token = preferences.getString("token", "");
		status.url = preferences.getString("url", WebAPIDataSource.DEFAULT_URL);
		return status;
	}

	private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock(
			true);

	private final Status status;

	public StatusAccess() {
		status = newStatusInstance();
	}

	public StatusAccess(Context context, Boolean isClear) {
		status = newStatusInstance(context, isClear);
	}

	public <T> T read(Reader<T> reader) {
		try {
			reentrantReadWriteLock.readLock().lock();
			return reader.read(status);
		} finally {
			reentrantReadWriteLock.readLock().unlock();
		}
	}

	public void read(VoidReader reader) {
		try {
			reentrantReadWriteLock.readLock().lock();
			reader.read(status);
		} finally {
			reentrantReadWriteLock.readLock().unlock();
		}
	}

	public <T> T readAndWrite(ReaderAndWriter<T> reader) {
		try {
			reentrantReadWriteLock.writeLock().lock();
			T result = reader.readAndWrite(status);
			save(status.file);
			return result;
		} finally {
			reentrantReadWriteLock.writeLock().unlock();
		}
	}

	private void save(final File file) {
		// ByteArrayへの変換を呼び出し元スレッドで行う
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			reentrantReadWriteLock.readLock().lock();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(status);
		} catch (NotSerializableException e) {
			Log.e(TAG, e.toString(), e);
			return;
		} catch (IOException e) {
			Log.w(TAG, e);
			return;
		} finally {
			reentrantReadWriteLock.readLock().unlock();
			Closeables.closeQuietly(objectOutputStream);
			Closeables.closeQuietly(byteArrayOutputStream);
		}

		// ByteArrayへの変換後は、呼び出し元スレッドでのファイルIOを避けるため新しいスレッドでデータを書き込む
		(new SaveThread(file, byteArrayOutputStream)).start();
	}

	public void write(Writer writer) {
		try {
			reentrantReadWriteLock.writeLock().lock();
			writer.write(status);
			save(status.file);
		} finally {
			reentrantReadWriteLock.writeLock().unlock();
		}
	}
}
