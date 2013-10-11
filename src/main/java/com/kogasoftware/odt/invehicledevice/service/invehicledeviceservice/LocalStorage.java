package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.SerializationException;

import com.kogasoftware.odt.apiclient.Serializations;

import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTimeUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

/**
 * LocalDataのアクセスに対し 書き込みがあったら自動で保存. 読み書き時にロックを実行を行う
 */
public class LocalStorage implements Closeable {
	private static final int NUM_THREADS = 5;
	private final ExecutorService executorService = Executors
			.newFixedThreadPool(NUM_THREADS);

	public interface BackgroundReader<T> {
		T readInBackground(LocalData localData);

		void onRead(T result);
	}

	public interface BackgroundWriter {
		void writeInBackground(LocalData localData);

		void onWrite();
	}
	
	public interface DeferredBackgroundWriter extends BackgroundWriter {
	}

	public interface Reader<T> {
		T read(LocalData localData);
	}

	private class SaveThread extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					periodicSaveSemaphore.acquire();
					periodicSaveSemaphore.drainPermits();
					save();
					if (periodicSaveWaitSemaphore.tryAcquire(savePeriodMillis,
							TimeUnit.MILLISECONDS)) {
						periodicSaveWaitSemaphore.drainPermits();
						Log.i(TAG, "Periodic save wait was cancelled");
					}
				}
			} catch (InterruptedException e) {
				// 正常終了
			} finally {
				if (periodicSaveSemaphore.tryAcquire()) {
					Log.i(TAG, "exit with saving");
					save(); // アプリ終了時、saveSemaphoreがacquire可能の場合は必ずsaveを行う
				} else {
					Log.i(TAG, "exit without saving");
				}
			}
		}
	}

	public interface Writer {
		void write(LocalData localData);
	}

	private static final Object FILE_ACCESS_LOCK = new Object(); // ファイルアクセス中のスレッドを一つに制限するためのロック。将来的にはロックの粒度をファイル毎にする必要があるかもしれない。

	private static final Integer DEFAULT_SAVE_PERIO_MILLIS = 180 * 1000;

	private static final AtomicBoolean WILL_CLEAR_SAVED_FILE = new AtomicBoolean(
			false);

	private static final String TAG = LocalStorage.class.getSimpleName();

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
	private static LocalData newLocalDataFromFile(Context context) {
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
					.putBoolean(SharedPreferencesKeys.CLEAR_STATUS_BACKUP,
							false).commit();
		}

		if (!file.exists()) {
			Log.i(TAG, "\"" + file + "\" not found");
		} else {
			Log.d(TAG, "\"" + file + "\" found ");
			try {
				LocalData readLocalData = Serializations.deserialize(file,
						LocalData.class);
				if (isClear) {
					localData.serviceUnitStatusLog = readLocalData.serviceUnitStatusLog;
				} else {
					localData = readLocalData;
				}
			} catch (SerializationException e) {
				Log.e(TAG, e.toString(), e);
			} catch (FileNotFoundException e) {
				Log.e(TAG, e.toString(), e);
			}
			if (isClear && !file.delete()) {
				Log.e(TAG, "!\"" + file + "\".delete()");
			}

			Calendar startCalendar = Calendar.getInstance();
			startCalendar.setTimeInMillis(DateTimeUtils.currentTimeMillis());
			startCalendar = DateUtils.truncate(startCalendar, Calendar.MINUTE);
			startCalendar.add(Calendar.HOUR_OF_DAY,
					-InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR);
			startCalendar.add(Calendar.MINUTE,
					-InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_MINUTE);
			startCalendar.set(Calendar.HOUR_OF_DAY,
					InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_HOUR);
			startCalendar.set(Calendar.MINUTE,
					InVehicleDeviceService.NEW_SCHEDULE_DOWNLOAD_MINUTE);
			Date startDate = startCalendar.getTime();
			if (localData.updatedDate.before(startDate)) {
				localData.operation.operationScheduleReceiveSequence = 0;
			}
		}

		localData.file = file;
		localData.token = preferences.getString(
				SharedPreferencesKeys.SERVER_IN_VEHICLE_DEVICE_TOKEN, "");
		localData.url = preferences.getString(SharedPreferencesKeys.SERVER_URL,
				InVehicleDeviceService.DEFAULT_URL);
		localData.rotateMap = preferences.getBoolean(
				SharedPreferencesKeys.ROTATE_MAP, true);
		localData.extraRotationDegreesClockwise = preferences.getInt(
				SharedPreferencesKeys.EXTRA_ROTATION_DEGREES_CLOCKWISE, 0);
		return localData;
	}

	private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

	private final Lock readLock = reentrantReadWriteLock.readLock();
	private final Lock writeLock = reentrantReadWriteLock.writeLock();
	private final LocalData localData;
	private final Semaphore periodicSaveSemaphore = new Semaphore(0);
	private final Semaphore periodicSaveWaitSemaphore = new Semaphore(0);
	private final Integer savePeriodMillis;
	private final Thread saveThread;

	public LocalStorage() {
		this(new LocalData());
	}

	public LocalStorage(Context context) {
		this(context, DEFAULT_SAVE_PERIO_MILLIS);
	}

	@VisibleForTesting
	public LocalStorage(LocalData localData) {
		this.localData = localData;
		this.saveThread = new EmptyThread();
		savePeriodMillis = DEFAULT_SAVE_PERIO_MILLIS;
	}

	@VisibleForTesting
	public LocalStorage(Context context, Integer savePeriodMillis) {
		this.savePeriodMillis = savePeriodMillis;
		synchronized (FILE_ACCESS_LOCK) {
			localData = newLocalDataFromFile(context);
		}
		saveThread = new SaveThread();
		saveThread.start();
	}

	@Override
	public void close() {
		saveThread.interrupt();
		executorService.shutdownNow();
	}

	/**
	 * 読み取りロックをした状態で、localDataにアクセスを行う
	 * 
	 * @param reader
	 */
	public <T extends Serializable> T withReadLock(Reader<T> reader) {
		if (Thread.currentThread().getId() == Looper.getMainLooper()
				.getThread().getId()) {
			String message = "withReadLock on main thread";
			Log.e(TAG, message, new RuntimeException(message));
		}
		long beginTime = System.currentTimeMillis();
		readLock.lock();
		long lockTime = System.currentTimeMillis();
		try {
			return Serializations.clone(reader.read(localData));
		} finally {
			readLock.unlock();
			long endTime = System.currentTimeMillis();
			long lockPeriod = lockTime - beginTime;
			if (lockPeriod > 2000) {
				String message = "lock period=" + lockPeriod + "ms\n";
				message += Throwables.getStackTraceAsString(new Throwable());
				Log.v(TAG, message);
			}
			long readPeriod = endTime - lockTime;
			if (readPeriod > 2000) {
				String message = "read period=" + readPeriod + "ms\n";
				message += Throwables.getStackTraceAsString(new Throwable());
				Log.v(TAG, message);
			}
		}
	}

	/**
	 * 書き込みロックをした状態で、localDataにアクセスを行う。処理が完了したら、localDataを永続化する。
	 * ただし、前回の永続化からsavePeriodMillis経過していない場合は、savePeriodMillisが経過するまで永続化は行わない。
	 * 
	 * @param writer
	 */
	public void withWriteLock(Writer writer) {
		withWriteLock(writer, false);
	}

	private void withWriteLock(Writer writer, Boolean deferred) {
		if (Thread.currentThread().getId() == Looper.getMainLooper()
				.getThread().getId()) {
			String message = "withWriteLock on main thread";
			Log.e(TAG, message, new RuntimeException(message));
		}
		writeLock.lock();
		try {
			writer.write(localData);
		} finally {
			writeLock.unlock();
		}
		if (deferred) {
			periodicSaveSemaphore.release();
		} else {
			save();
		}
	}

	public <T extends Serializable> void read(
			final BackgroundReader<T> backgroundReader) {
		final Handler handler = InVehicleDeviceService.getThreadHandler();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				final T result = withReadLock(new Reader<T>() {
					@Override
					public T read(LocalData localData) {
						return backgroundReader.readInBackground(localData);
					}
				});
				handler.post(new Runnable() {
					@Override
					public void run() {
						backgroundReader.onRead(result);
					}
				});
			}
		};
		try {
			executorService.submit(task);
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
		}
	}

	public void write(DeferredBackgroundWriter backgroundWriter) {
		write(backgroundWriter, true);
	}

	public void write(BackgroundWriter backgroundWriter) {
		write(backgroundWriter, false);
	}

	private void write(final BackgroundWriter backgroundWriter, final Boolean deferred) {
		final Handler handler = InVehicleDeviceService.getThreadHandler();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				withWriteLock(new Writer() {
					@Override
					public void write(LocalData localData) {
						backgroundWriter.writeInBackground(localData);
					}
				}, deferred);
				handler.post(new Runnable() {
					@Override
					public void run() {
						backgroundWriter.onWrite();
					}
				});
			}
		};
		try {
			executorService.submit(task);
		} catch (RejectedExecutionException e) {
			Log.w(TAG, e);
		}
	}

	public void flush() {
		periodicSaveWaitSemaphore.release();
		periodicSaveSemaphore.release();
	}

	private void save() {
		byte[] serialized = new byte[0];
		long beginTime = 0;
		long endTime = 0;
		File file = new EmptyFile();
		try {
			readLock.lock();
			try {
				file = localData.file;
				beginTime = System.currentTimeMillis();
				serialized = Serializations.serialize(localData);
				endTime = System.currentTimeMillis();
			} finally {
				readLock.unlock();
			}
		} catch (SerializationException e) {
			Log.e(TAG, e.toString(), e);
			return;
		} finally {
			long elapsed = endTime - beginTime;
			Log.d(TAG, "serialize() " + elapsed + "ms " + serialized.length
					+ "bytes");
		}

		synchronized (FILE_ACCESS_LOCK) {
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(file);
				fileOutputStream.getChannel().lock();
				fileOutputStream.write(serialized);
				fileOutputStream.flush();
				Log.d(TAG, "written: " + file);
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
