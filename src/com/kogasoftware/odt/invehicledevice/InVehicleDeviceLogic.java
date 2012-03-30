package com.kogasoftware.odt.invehicledevice;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceLogic {
	public static final Integer POLLING_PERIOD_MILLIS = 10 * 1000;
	private static final Integer NUM_THREADS = 3;
	private final File statusFile;
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final DataSource dataSource = new DummyDataSource();
	private final InVehicleDeviceStatus status; // この変数を利用するときはロックする

	class OperationScheduleReceiver implements Runnable {
		@Override
		public void run() {
			List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
			while (true) {
				try {
					operationSchedules.addAll(dataSource.getOperationSchedules());
					break;
				} catch (WebAPIException e) {
					e.printStackTrace(); // TODO
				}
				try {
					Thread.sleep(InVehicleDeviceLogic.POLLING_PERIOD_MILLIS);
				} catch (InterruptedException e) {
					return;
				}
			}
			synchronized (status) {
				status.operationSchedules.clear();
				status.operationSchedules.addAll(operationSchedules);
				status.initialized.set(true);
				saveStatus();
			}
		}
	}

	class VehicleNotificationReceiver implements Runnable {
		@Override
		public void run() {
			try {
				List<VehicleNotification> vehicleNotification = dataSource
						.getVehicleNotifications();
				synchronized (status) {
					status.vehicleNotifications.addAll(vehicleNotification);
					saveStatus();
				}
			} catch (WebAPIException e) {
				return;
			}
		}
	};

	class ScheduleChangedReceiver implements Runnable {
		@Override
		public void run() {
			try {
				List<VehicleNotification> vehicleNotification = dataSource
						.getVehicleNotifications();
				synchronized (status) {
					status.vehicleNotifications.addAll(vehicleNotification);
					saveStatus();
				}
			} catch (WebAPIException e) {
				return;
			}
		}
	}

	private void saveStatus() {
		synchronized (status) {
			status.save(statusFile);
		}
	}

	public Boolean isInitialized() {
		synchronized (status) {
			return status.initialized.get();
		}
	}

	public InVehicleDeviceLogic() {
		this.statusFile = new EmptyFile();
		this.status = new InVehicleDeviceStatus();
	}

	public InVehicleDeviceLogic(File statusFile) {
		this.statusFile = statusFile;
		this.status = InVehicleDeviceStatus.load(statusFile);

		executorService.submit(new OperationScheduleReceiver());
		executorService.scheduleWithFixedDelay(
				new VehicleNotificationReceiver(), 0, POLLING_PERIOD_MILLIS,
				TimeUnit.MILLISECONDS);
		executorService.scheduleWithFixedDelay(new ScheduleChangedReceiver(),
				0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
	}

	public void shutdown() {
		executorService.shutdown();
	}

	@Override
	protected void finalize() {
		shutdown();
		try {
			super.finalize();
		} catch (Throwable e) {
		}
	}

	public InVehicleDeviceStatus.Status getStatus() {
		synchronized (status) {
			return status.status;
		}
	}

	public List<OperationSchedule> getOperationSchedules() {
		synchronized (status) {
			return new LinkedList<OperationSchedule>(status.operationSchedules);
		}
	}

	public List<OperationSchedule> getRestOperationSchedules() {
		try {
			synchronized (status) {
				List<OperationSchedule> rests = new LinkedList<OperationSchedule>(
						status.operationSchedules);
				return rests.subList(status.currentOperationScheduleIndex,
						rests.size());
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		} catch (IllegalArgumentException e) {
		}
		return new LinkedList<OperationSchedule>();
	}

	public void enterPlatformStatus() {
		synchronized (status) {
			if (status.status == InVehicleDeviceStatus.Status.PLATFORM) {
				return;
			}
			status.status = InVehicleDeviceStatus.Status.PLATFORM;
		}
	}

	public void enterDriveStatus() {
		synchronized (status) {
			if (status.status == InVehicleDeviceStatus.Status.DRIVE) {
				return;
			}
			if (status.status == InVehicleDeviceStatus.Status.PLATFORM) {
				status.currentOperationScheduleIndex++;
			}
			status.status = InVehicleDeviceStatus.Status.DRIVE;
		}
	}

	public List<VehicleNotification> pollVehicleNotifications() {
		synchronized (status) {
			LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>(
					status.vehicleNotifications);
			status.vehicleNotifications.clear();
			return vehicleNotifications;
		}
	}
}
