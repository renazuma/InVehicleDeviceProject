package com.kogasoftware.odt.invehicledevice.logic;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

import android.location.Location;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 現在の状態を保存しておくクラス
 */
public class Status implements Serializable {
	public enum Phase {
		DRIVE, FINISH, INITIAL, PLATFORM
	}

	private static final long serialVersionUID = 5617948505743183178L;
	private static final String TAG = Status.class.getSimpleName();

	public final Date createdDate = new Date();

	public Boolean initialized = false;
	public String token = "";
	public String url = "";
	public Integer currentOperationScheduleIndex = 0;
	public File file = new EmptyFile();
	public Boolean paused = false;
	public Phase phase = Phase.INITIAL;
	public Boolean stopped = false;
	public Optional<Location> location = Optional.absent();
	public Optional<Integer> orientation = Optional.absent();
	public Optional<Integer> temperature = Optional.absent();

	// Serializableにするため、LinkedListのままにしておく
	public final LinkedList<Reservation> missedReservations = new LinkedList<Reservation>();
	public final LinkedList<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
	public final LinkedList<PassengerRecord> unhandledPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<PassengerRecord> ridingPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<PassengerRecord> getOnPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<PassengerRecord> getOffPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<Reservation> ridingReservations = new LinkedList<Reservation>();
	public final LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
	public final LinkedList<VehicleNotification> repliedVehicleNotifications = new LinkedList<VehicleNotification>();
	public final LinkedList<OperationSchedule> arrivalOperationSchedules = new LinkedList<OperationSchedule>();
	public final LinkedList<OperationSchedule> departureOperationSchedules = new LinkedList<OperationSchedule>();
}
