package com.kogasoftware.odt.invehicledevice.logic;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyFile;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 現在の状態を保存しておくクラス リストのメンバははSerializableであることをわかりやすくするためLinkedListにしておく
 */
public class Status implements Serializable {
	public static enum Phase {
		DRIVE, FINISH, INITIAL, PLATFORM
	}

	private static final long serialVersionUID = 5617948559888181L;
	
	public final Semaphore operationScheduleInitializedSign = new Semaphore(0); // パーミットが0以上の場合は初期化済み。0以上になるまで待つためにacquireしたら必ずreleaseする。CountDownLatchがSerializableではないためこれを使用
	public Date updatedDate = new Date();
	public File file = new EmptyFile();
	public Phase phase = Phase.INITIAL;

	public String token = "";
	public String url = "";
	public InVehicleDevice inVehicleDevice;
	public ServiceProvider serviceProvider;
	public ServiceUnitStatusLog serviceUnitStatusLog = new ServiceUnitStatusLog();

	public final LinkedList<OperationSchedule> remainingOperationSchedules = new LinkedList<OperationSchedule>();
	public final LinkedList<OperationSchedule> finishedOperationSchedules = new LinkedList<OperationSchedule>();

	public final LinkedList<Reservation> reservations = new LinkedList<Reservation>();

	public final LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
	public final LinkedList<VehicleNotification> receivingOperationScheduleChangedVehicleNotifications = new LinkedList<VehicleNotification>();
	public final LinkedList<VehicleNotification> receivedOperationScheduleChangedVehicleNotifications = new LinkedList<VehicleNotification>();
	public final LinkedList<VehicleNotification> repliedVehicleNotifications = new LinkedList<VehicleNotification>();

}
