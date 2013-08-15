package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTimeUtils;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;

/**
 * 現在の状態を保存しておくクラス
 */
public class LocalData implements Serializable {
	public static enum Phase {
		DRIVE, FINISH, PLATFORM_GET_ON, PLATFORM_GET_OFF,
	}

	private static final long serialVersionUID = 1797801788126L;

	public Boolean serviceProviderInitialized = false;

	public Date updatedDate = new Date(DateTimeUtils.currentTimeMillis());
	public File file = new EmptyFile();

	public String token = "";
	public String url = "";
	public ServiceProvider serviceProvider = new ServiceProvider();
	public ServiceUnitStatusLog serviceUnitStatusLog = new ServiceUnitStatusLog();

	public Boolean completeGetOff = false;
	public Integer operationScheduleReceiveSequence = 0;
	public final List<OperationSchedule> operationSchedules = Lists
			.newLinkedList();
	public final List<PassengerRecord> passengerRecords = Lists.newLinkedList();

	public static enum VehicleNotificationStatus {
		UNHANDLED, OPERATION_SCHEDULE_RECEIVED, REPLIED,
	}

	public final Multimap<VehicleNotificationStatus, VehicleNotification> vehicleNotifications = LinkedHashMultimap
			.create();
}
