package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.ServiceUnit;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.UnitAssignment;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 現在の状態を保存しておくクラス
 */
public class LocalData implements Serializable {
	public static enum Phase {
		DRIVE, FINISH, INITIAL /* will delete */, PLATFORM, PLATFORM_GET_ON, PLATFORM_GET_OFF,
	}

	private static final long serialVersionUID = 1797801788123L;

	@Deprecated
	public final Semaphore operationScheduleInitializedSign = new Semaphore(0); // パーミットが0以上の場合は初期化済み。0以上になるまで待つためにacquireしたら必ずreleaseする。CountDownLatchがSerializableではないためこれを使用
	@Deprecated
	public final Semaphore serviceProviderInitializedSign = new Semaphore(0); // パーミットが0以上の場合は初期化済み。0以上になるまで待つためにacquireしたら必ずreleaseする。CountDownLatchがSerializableではないためこれを使用

	public Date updatedDate = InVehicleDeviceService.getDate();
	public File file = new EmptyFile();
	public Phase phase = Phase.INITIAL;

	public String token = "";
	public String url = "";
	public InVehicleDevice inVehicleDevice = new InVehicleDevice();
	public ServiceProvider serviceProvider = new ServiceProvider();
	public UnitAssignment unitAssignment = new UnitAssignment();
	public ServiceUnit serviceUnit = new ServiceUnit();
	public ServiceUnitStatusLog serviceUnitStatusLog = new ServiceUnitStatusLog();

	public Boolean completeGetOff = false;
	public final List<OperationSchedule> operationSchedules = Lists
			.newLinkedList();
	public final List<PassengerRecord> passengerRecords = Lists.newLinkedList();

	public static enum VehicleNotificationStatus {
		UNHANDLED, OPERATION_SCHEDULE_RECEIVED, REPLIED,
	}

	public final Multimap<VehicleNotificationStatus, VehicleNotification> vehicleNotifications = LinkedHashMultimap
			.create();
}
