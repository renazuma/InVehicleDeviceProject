package com.kogasoftware.odt.invehicledevice.logic;

import android.content.Context;

/**
 * InVehicleDeviceStatusのアクセスに対し 書き込みがあったら自動で保存. 読み書き時synchronizedを実行を行う
 */
public class StatusAccess {
	public interface Reader<T> {
		T read(Status status);
	}

	public interface ReaderAndWriter<T> {
		T readAndWrite(Status status);
	}

	public interface Writer {
		void write(Status status);
	}

	final Status status;

	public StatusAccess() {
		status = Status.newInstance();
	}

	public StatusAccess(Context context, Boolean isClear) {
		status = Status.newInstance(context, isClear);
	}

	public <T> T read(Reader<T> reader) {
		try {
			status.reentrantReadWriteLock.readLock().lock();
			return reader.read(status);
		} finally {
			status.reentrantReadWriteLock.readLock().unlock();
		}
	}

	public <T> T readAndWrite(ReaderAndWriter<T> reader) {
		try {
			status.reentrantReadWriteLock.writeLock().lock();
			T result = reader.readAndWrite(status);
			status.save();
			return result;
		} finally {
			status.reentrantReadWriteLock.writeLock().unlock();
		}
	}

	public void write(Writer writer) {
		try {
			status.reentrantReadWriteLock.writeLock().lock();
			writer.write(status);
			status.save();
		} finally {
			status.reentrantReadWriteLock.writeLock().unlock();
		}
	}
}
