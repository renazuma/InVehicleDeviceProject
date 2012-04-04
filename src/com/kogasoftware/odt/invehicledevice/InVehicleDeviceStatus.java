package com.kogasoftware.odt.invehicledevice;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.UTFDataFormatException;
import java.nio.channels.FileLock;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceStatus implements Serializable {
	public enum Status {
		DRIVE, PLATFORM, INITIAL, FINISH
	}

	private static final long serialVersionUID = 5617948505743182175L;
	private static final String TAG = InVehicleDeviceStatus.class
			.getSimpleName();

	public static InVehicleDeviceStatus load(File file) {
		return new InVehicleDeviceStatus();
	}

	public static InVehicleDeviceStatus load2(File file) {
		InVehicleDeviceStatus status = new InVehicleDeviceStatus();
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
		} catch (UTFDataFormatException e) {
			Log.w(TAG, e);
		} catch (EOFException e) {
			Log.w(TAG, e);
		} catch (InvalidClassException e) {
			Log.w(TAG, e);
		} catch (StreamCorruptedException e) {
			Log.w(TAG, e);
		} catch (ClassCastException e) {
			Log.w(TAG, e);
		} catch (FileNotFoundException e) {
			Log.w(TAG, e);
		} catch (OptionalDataException e) {
			Log.w(TAG, e);
		} catch (ClassNotFoundException e) {
			Log.w(TAG, e);
		} catch (IOException e) {
			Log.w(TAG, e);
		} catch (RuntimeException e) {
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
			return new InVehicleDeviceStatus();
		}
		// return status;
		return new InVehicleDeviceStatus(); // TODO
	}

	public final ConcurrentLinkedQueue<VehicleNotification> vehicleNotifications = new ConcurrentLinkedQueue<VehicleNotification>();
	public final ConcurrentLinkedQueue<OperationSchedule> operationSchedules = new ConcurrentLinkedQueue<OperationSchedule>();
	public final AtomicBoolean initialized = new AtomicBoolean(false);;
	public final Date createdDate = new Date();
	public Integer currentOperationScheduleIndex = 0;

	public Status status = Status.INITIAL;

	public void save(File file) {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		FileLock fileLock = null;
		try {
			file.delete();
			fileOutputStream = new FileOutputStream(file);
			fileLock = fileOutputStream.getChannel().lock();
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(this);
			objectOutputStream.flush();
			fileOutputStream.flush();
		} catch (NotSerializableException e) {
			Log.e(TAG, e.toString(), e);
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.toString(), e);
		} catch (IOException e) {
			Log.e(TAG, e.toString(), e);
		} finally {
			Closeables.closeQuietly(objectOutputStream);
			Closeables.closeQuietly(fileOutputStream);
			if (fileLock != null) {
				try {
					fileLock.release();
				} catch (IOException e) {
					// Log.w(TAG, e);
				}
			}
		}
	}
}
