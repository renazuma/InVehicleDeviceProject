package com.kogasoftware.odt.invehicledevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceStatus implements Serializable {
	private static final long serialVersionUID = 5617948505743182174L;
	public final ConcurrentLinkedQueue<VehicleNotification> vehicleNotifications = new ConcurrentLinkedQueue<VehicleNotification>();
	public final ConcurrentLinkedQueue<OperationSchedule> operationSchedules = new ConcurrentLinkedQueue<OperationSchedule>();
	public final AtomicBoolean initialized = new AtomicBoolean(false);
	public final Date createdDate = new Date();
	public Integer currentOperationScheduleIndex = 0;

	public enum Status {
		DRIVE, PLATFORM, INITIAL
	};

	public Status status = Status.INITIAL;

	public void save(File file) {
		FileOutputStream fileOutputStream = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(file);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(this);
		} catch (NotSerializableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Closeables.closeQuietly(objectOutputStream);
			Closeables.closeQuietly(fileOutputStream);
		}
	}

	public static InVehicleDeviceStatus load(File file) {
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
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return status;
	}
}
