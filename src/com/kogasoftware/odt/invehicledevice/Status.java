package com.kogasoftware.odt.invehicledevice;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import com.google.common.base.Optional;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 現在の状態を保存しておくクラス
 */
public class Status implements Serializable {
	public enum Phase {
		DRIVE, FINISH, INITIAL, PLATFORM
	}

	public static class SendLists implements Serializable {
		private static final long serialVersionUID = Status.serialVersionUID ^ 1;
		public final LinkedList<PassengerRecord> getOnPassengerRecords = new LinkedList<PassengerRecord>();
		public final LinkedList<PassengerRecord> getOffPassengerRecords = new LinkedList<PassengerRecord>();
		public final LinkedList<VehicleNotification> repliedVehicleNotifications = new LinkedList<VehicleNotification>();
		public final LinkedList<OperationSchedule> arrivalOperationSchedules = new LinkedList<OperationSchedule>();
		public final LinkedList<OperationSchedule> departureOperationSchedules = new LinkedList<OperationSchedule>();
	}

	private static final long serialVersionUID = 5617948505743183179L;

	public final SendLists sendLists = new SendLists();
	public final Date createdDate = new Date();
	public Boolean initialized = false;
	public String token = "";
	public String url = "";
	public File file = new EmptyFile();
	public Boolean paused = false;
	public Phase phase = Phase.INITIAL;
	public Boolean stopped = false;
	public Optional<BigDecimal> latitude = Optional.absent();
	public Optional<BigDecimal> longitude = Optional.absent();
	public Optional<Integer> orientation = Optional.absent();
	public Optional<Integer> temperature = Optional.absent();
	public final LinkedList<OperationSchedule> remainingOperationSchedules = new LinkedList<OperationSchedule>();
	public final LinkedList<OperationSchedule> finishedOperationSchedules = new LinkedList<OperationSchedule>();
	public final LinkedList<PassengerRecord> unhandledPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<PassengerRecord> ridingPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<PassengerRecord> finishedPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();;
	public final Semaphore operationScheduleChanged = new Semaphore(0);
}
