package com.kogasoftware.odt.invehicledevice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 現在の状態を保存しておくクラス
 */
public class InVehicleDeviceStatus implements Serializable {
	/**
	 * InVehicleDeviceStatusのアクセスに対し 書き込みがあったら自動で保存. 読み書き時synchronizedを実行を行う
	 */
	public static class Access {
		public interface Reader<T> {
			T read(InVehicleDeviceStatus status);
		}

		public interface Writer {
			void write(InVehicleDeviceStatus status);
		}

		public interface ReaderAndWriter<T> {
			T readAndWrite(InVehicleDeviceStatus status);
		}

		final InVehicleDeviceStatus status;

		public Access() {
			status = InVehicleDeviceStatus.newInstance();
		}

		public Access(File file) {
			status = InVehicleDeviceStatus.newInstance(file);
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

	public enum Status {
		DRIVE, PLATFORM, INITIAL, FINISH
	}

	private static final long serialVersionUID = 5617948505743182170L;

	private static final String TAG = InVehicleDeviceStatus.class
			.getSimpleName();

	private static InVehicleDeviceStatus newInstance() {
		return new InVehicleDeviceStatus();
	}

	private static InVehicleDeviceStatus newInstance(File file) {
		InVehicleDeviceStatus status = new InVehicleDeviceStatus();
		status.file = file;
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			objectInputStream = new ObjectInputStream(fileInputStream);
			Object object = objectInputStream.readObject();
			if (object == null) {
				return status;
			}
			status = (InVehicleDeviceStatus) object;
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
		}

		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(now.getYear(), now.getMonth(), now.getDay(), 3, 0); // TODO
		if (status.createdDate.before(calendar.getTime())) {
			status = new InVehicleDeviceStatus();
		}
		status.file = file;
		return status;
	}

	private final Object lock = new Serializable() {
		private static final long serialVersionUID = -8902504841122071697L;
	}; // synchronized用にシリアライズ可能なオブジェクトを持っておく

	public final ConcurrentLinkedQueue<VehicleNotification> vehicleNotifications = new ConcurrentLinkedQueue<VehicleNotification>();

	public final ConcurrentLinkedQueue<OperationSchedule> operationSchedules = new ConcurrentLinkedQueue<OperationSchedule>();

	public final AtomicBoolean initialized = new AtomicBoolean(false);
	public final Date createdDate = new Date();
	public final LinkedList<Reservation> ridingReservations = new LinkedList<Reservation>();
	public final LinkedList<Reservation> missedReservations = new LinkedList<Reservation>();
	public final LinkedList<Reservation> unexpectedReservations = new LinkedList<Reservation>();
	public Integer currentOperationScheduleIndex = 0;
	public File file = new EmptyFile();
	public Integer unexpectedReservationSequence = 1000;

	public Status status = Status.INITIAL;

	private InVehicleDeviceStatus() {
	}

	public void save() {
		save(file);
	}

	public void save(final File file) {
		// ByteArrayへの変換を呼び出し元スレッドで行う
		final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		synchronized (lock) {
			ObjectOutputStream objectOutputStream = null;
			try {
				if (file.exists() && !file.delete()) {
					throw new IOException("file.exists() && !file.delete()");
				}
				objectOutputStream = new ObjectOutputStream(
						byteArrayOutputStream);
				objectOutputStream.writeObject(this);
				objectOutputStream.flush();
			} catch (NotSerializableException e) {
				Log.e(TAG, e.toString(), e);
				return;
			} catch (IOException e) {
				Log.w(TAG, e);
				return;
			} finally {
				Closeables.closeQuietly(objectOutputStream);
				Closeables.closeQuietly(byteArrayOutputStream);
			}
		}
		// ByteArrayへの変換後は、呼び出し元スレッドでのファイルIOを避けるため新しいスレッドでデータを書き込む
		new Thread() {
			@Override
			public void run() {
				FileOutputStream fileOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(file);
					fileOutputStream.getChannel().lock();
					fileOutputStream.write(byteArrayOutputStream.toByteArray());
					fileOutputStream.close();
				} catch (FileNotFoundException e) {
					Log.w(TAG, e);
				} catch (IOException e) {
					Log.w(TAG, e);
				} finally {
					Closeables.closeQuietly(fileOutputStream);
				}
			}
		}.start();
	}

}
