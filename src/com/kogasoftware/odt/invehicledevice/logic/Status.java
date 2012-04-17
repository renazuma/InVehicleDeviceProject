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
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 現在の状態を保存しておくクラス
 */
public class Status implements Serializable {
	public enum Phase {
		DRIVE, FINISH, INITIAL, PLATFORM
	}

	private static final long serialVersionUID = 5617948505743182173L;

	private static final String TAG = Status.class.getSimpleName();

	public static Status newInstance() {
		return new Status();
	}

	public static Status newInstance(Context context, Boolean isClear) {
		File file = new File(context.getFilesDir() + File.separator
				+ Status.class.getCanonicalName() + ".serialized");
		Status status = new Status();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (!isClear && !preferences.getBoolean("update", false)) {
			preferences.edit().putBoolean("update", false).commit();
			FileInputStream fileInputStream = null;
			ObjectInputStream objectInputStream = null;
			try {
				fileInputStream = new FileInputStream(file);
				objectInputStream = new ObjectInputStream(fileInputStream);
				Object object = objectInputStream.readObject();
				if (object == null) {
					return status;
				}
				status = (Status) object;
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
		status.url = preferences.getString("url", "");

		return status;
	}

	public final Date createdDate = new Date();

	public Integer currentOperationScheduleIndex = 0;
	public File file = new EmptyFile();
	public final AtomicBoolean initialized = new AtomicBoolean(false);
	public final Object lock = new Serializable() {
		private static final long serialVersionUID = -8902504841122071697L;
	}; // synchronized用にシリアライズ可能なオブジェクトを持っておく

	public final LinkedList<Reservation> missedReservations = new LinkedList<Reservation>();
	public final LinkedList<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
	public Boolean paused = false;
	public final LinkedList<Reservation> ridingReservations = new LinkedList<Reservation>();
	public Phase phase = Phase.INITIAL;
	public Boolean stopped = false;
	public final LinkedList<Reservation> unexpectedReservations = new LinkedList<Reservation>();
	public Integer unexpectedReservationSequence = 1000;
	public Optional<Location> location = Optional.<Location> absent();

	// Serializableにするため、LinkedListのままにしておく
	public final LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
	public final LinkedList<VehicleNotification> repliedVehicleNotifications = new LinkedList<VehicleNotification>();
	public final LinkedList<OperationSchedule> arrivalOperationSchedule = new LinkedList<OperationSchedule>();
	public final LinkedList<OperationSchedule> departureOperationSchedule = new LinkedList<OperationSchedule>();
	public String token = "";
	public String url = "";

	private Status() {
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
				if (!file.equals(new EmptyFile()) && file.exists()
						&& !file.delete()) {
					throw new IOException(
							"file.exists() && !file.delete(), file=" + file);
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
