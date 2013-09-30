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
	public static class Operation implements Serializable {
		private static final long serialVersionUID = ~LocalData.serialVersionUID;
		public static enum Phase {
			DRIVE, FINISH, PLATFORM_GET_ON, PLATFORM_GET_OFF,
		}

		public Boolean completeGetOff = false;
		public Integer operationScheduleReceiveSequence = 0;
		public final List<OperationSchedule> operationSchedules = Lists
				.newLinkedList();
		public final List<PassengerRecord> passengerRecords = Lists
				.newLinkedList();
		/**
		 * 現在の状況を得る
		 */
		public Phase getPhase() {
			for (OperationSchedule operationSchedule : OperationSchedule
					.getCurrent(operationSchedules).asSet()) {
				if (!operationSchedule.isArrived()) {
					return Phase.DRIVE;
				}
				if (operationSchedule.isDeparted()) {
					continue;
				}
				for (PassengerRecord passengerRecord : operationSchedule
						.getNoGetOffErrorPassengerRecords(passengerRecords)) {
					if (!passengerRecord.getIgnoreGetOffMiss()) {
						return Phase.PLATFORM_GET_OFF;
					}
				}
				if (operationSchedule.getGetOffScheduledPassengerRecords(
						passengerRecords).isEmpty()) {
					return Phase.PLATFORM_GET_ON;
				}
				if (completeGetOff) {
					return Phase.PLATFORM_GET_ON;
				} else {
					return Phase.PLATFORM_GET_OFF;
				}
			}
			return Phase.FINISH;
		}
	}

	private static final long serialVersionUID = 1797801788127L;

	public Boolean serviceProviderInitialized = false;

	public Date updatedDate = new Date(DateTimeUtils.currentTimeMillis());
	public File file = new EmptyFile();

	public String token = "";
	public String url = "";
	public Boolean rotateMap = true;
	public Integer extraRotationDegreesClockwise = 0;
	public ServiceProvider serviceProvider = new ServiceProvider();
	public ServiceUnitStatusLog serviceUnitStatusLog = new ServiceUnitStatusLog();

	public final Operation operation = new Operation();

	public static enum VehicleNotificationStatus {
		UNHANDLED, OPERATION_SCHEDULE_RECEIVED, REPLIED,
	}

	public final Multimap<VehicleNotificationStatus, VehicleNotification> vehicleNotifications = LinkedHashMultimap
			.create();
}
