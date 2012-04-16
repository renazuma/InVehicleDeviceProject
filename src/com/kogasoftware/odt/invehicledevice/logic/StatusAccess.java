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
		synchronized (status.lock) {
			return reader.read(status);
		}
	}

	public <T> T readAndWrite(ReaderAndWriter<T> reader) {
		synchronized (status.lock) {
			T result = reader.readAndWrite(status);
			status.save();
			return result;
		}
	}

	public void write(Writer writer) {
		synchronized (status.lock) {
			writer.write(status);
		}
		status.save();
	}
}
