package com.kogasoftware.odt.invehicledevice;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DummyDataSource;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceLogic {
	private static final Integer NUM_THREADS = 3;
	private static final Integer POLLING_PERIOD_MILLIS = 10 * 1000;
	private final InVehicleDeviceStatus status = new InVehicleDeviceStatus();
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final DataSource dataSource = new DummyDataSource();
	private final AtomicBoolean initialized = new AtomicBoolean(false);
	public Boolean isInitialized() {
		return initialized.get();
	}

	public InVehicleDeviceLogic() {
		// 初期データを受信する
		final Runnable receiveOperationSchedule = new Runnable() {
			@Override
			public void run() {
				List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
				while (true) {
					try {
						operationSchedules = dataSource.getOperationSchedules();
						break;
					} catch (WebAPIException e) {
						e.printStackTrace(); // TODO
					}
					try {
						Thread.sleep(POLLING_PERIOD_MILLIS);
					} catch (InterruptedException e) {
						return;
					}
				}
				synchronized (status) {
					status.operationSchedules.clear();
					status.operationSchedules.addAll(operationSchedules);
				}
				initialized.set(true);
			}
		};

		try { // TODO
			executorService.submit(receiveOperationSchedule).get(); // wait
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 通知を一定時間ごとに受信する
		final Runnable receiveVehicleNotification = new Runnable() {
			@Override
			public void run() {
				try {
					List<VehicleNotification> vehicleNotification = dataSource
							.getVehicleNotifications();
					synchronized (status) {
						status.vehicleNotifications.addAll(vehicleNotification);
					}
				} catch (WebAPIException e) {
					return;
				}
			}
		};
		executorService.scheduleWithFixedDelay(receiveVehicleNotification, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);

		// スケジュール変更を一定時間ごとに受信する
		final Runnable receiveScheduleChanged = new Runnable() {
			@Override
			public void run() {
				try {
					List<VehicleNotification> vehicleNotification = dataSource
							.getVehicleNotifications();
					synchronized (status) {
						status.vehicleNotifications.addAll(vehicleNotification);
					}
				} catch (WebAPIException e) {
					return;
				}
			}
		};
		executorService.scheduleWithFixedDelay(receiveScheduleChanged, 0,
				POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
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
			LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>(status.vehicleNotifications);
			status.vehicleNotifications.clear();
			return vehicleNotifications;
		}
	}
}
