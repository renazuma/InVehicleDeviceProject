package com.kogasoftware.odt.invehicledevice.logic;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyFile;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 現在の状態を保存しておくクラス
 */
public class Status implements Serializable {
	public static enum Phase {
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

	private static final long serialVersionUID = 5617948505743183986L;

	public final SendLists sendLists = new SendLists();

	public final Date createdDate = new Date();
	public final Semaphore operationScheduleInitializedSign = new Semaphore(0); // パーミットが0以上の場合は初期化済み。0以上になるまで待つためにacquireしたら必ずreleaseする。CountDownLatchがSerializableではないためこれを使用
	public String token = "";
	public String url = "";
	public File file = new EmptyFile();
	public Phase phase = Phase.INITIAL;
	public Boolean serviceUnitStatusLogLocationEnabled = false; // serviceUnitStatusLogの位置メンバが有効な場合true
	public Integer unexpectedReservationSequence = 1;

	public final ServiceUnitStatusLog serviceUnitStatusLog = new ServiceUnitStatusLog();
	public final LinkedList<OperationSchedule> remainingOperationSchedules = new LinkedList<OperationSchedule>();
	public final LinkedList<OperationSchedule> finishedOperationSchedules = new LinkedList<OperationSchedule>();
	public final LinkedList<PassengerRecord> unhandledPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<PassengerRecord> ridingPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<PassengerRecord> finishedPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<PassengerRecord> selectedPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<PassengerRecord> unexpectedPassengerRecords = new LinkedList<PassengerRecord>();
	public final LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
	public final LinkedList<VehicleNotification> receivingOperationScheduleChangedVehicleNotifications = new LinkedList<VehicleNotification>();
	public final LinkedList<VehicleNotification> receivedOperationScheduleChangedVehicleNotifications = new LinkedList<VehicleNotification>();

	public Status() {
		serviceUnitStatusLog.setStatus(ServiceUnitStatusLogs.Status.OPERATION);
	}
}
