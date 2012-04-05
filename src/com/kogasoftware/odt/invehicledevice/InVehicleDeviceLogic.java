package com.kogasoftware.odt.invehicledevice;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.google.common.eventbus.EventBus;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyFile;
import com.kogasoftware.odt.invehicledevice.modal.ConfigModal;
import com.kogasoftware.odt.invehicledevice.modal.MemoModal;
import com.kogasoftware.odt.invehicledevice.modal.NotificationModal;
import com.kogasoftware.odt.invehicledevice.modal.PauseModal;
import com.kogasoftware.odt.invehicledevice.modal.ReturnPathModal;
import com.kogasoftware.odt.invehicledevice.modal.ScheduleChangedModal;
import com.kogasoftware.odt.invehicledevice.modal.ScheduleModal;
import com.kogasoftware.odt.invehicledevice.modal.StartCheckModal;
import com.kogasoftware.odt.invehicledevice.modal.StopCheckModal;
import com.kogasoftware.odt.invehicledevice.modal.StopModal;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceLogic {
	public static class EnterDriveStatusEvent {
	}

	public static class EnterFinishStatusEvent {
	}

	public static class EnterPlatformStatusEvent {
	}

	private static class OperationScheduleReceiver implements Runnable {
		private final InVehicleDeviceLogic logic;

		public OperationScheduleReceiver(InVehicleDeviceLogic logic) {
			this.logic = logic;
		}

		@Override
		public void run() {
			List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
			while (true) {
				try {
					operationSchedules.addAll(logic.getDataSource()
							.getOperationSchedules());
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
			synchronized (logic.status) {
				logic.status.operationSchedules.clear();
				logic.status.operationSchedules.addAll(operationSchedules);
				logic.status.initialized.set(true);
				logic.saveStatus();
			}
		}
	}

	private static class ScheduleChangedReceiver implements Runnable {
		private final InVehicleDeviceLogic logic;

		public ScheduleChangedReceiver(InVehicleDeviceLogic logic) {
			this.logic = logic;
		}

		@Override
		public void run() {
			try {
				List<VehicleNotification> vehicleNotification = logic
						.getDataSource().getVehicleNotifications();
				synchronized (logic.status) {
					logic.status.vehicleNotifications
							.addAll(vehicleNotification);
					logic.saveStatus();
				}
			} catch (WebAPIException e) {
				Log.i(TAG, "ScheduleChangedReceiver exit", e);
				return;
			}
		}
	}

	private static class VehicleNotificationReceiver implements Runnable {
		private final InVehicleDeviceLogic logic;

		public VehicleNotificationReceiver(InVehicleDeviceLogic logic) {
			this.logic = logic;
		}

		@Override
		public void run() {
			try {
				List<VehicleNotification> vehicleNotification = logic
						.getDataSource().getVehicleNotifications();
				synchronized (logic.status) {
					logic.status.vehicleNotifications
							.addAll(vehicleNotification);
					logic.saveStatus();
				}
			} catch (WebAPIException e) {
				Log.i(TAG, "VehicleNotificationReceiver exit", e);
				return;
			}
		}
	}

	private static final String TAG = InVehicleDeviceLogic.class
			.getSimpleName();
	private static final Integer POLLING_PERIOD_MILLIS = 30 * 1000;
	private static final Integer NUM_THREADS = 10;
	private final File statusFile;
	private final EventBus eventBus = new EventBus();
	private final List<WeakReference<Object>> registeredObjectReferences = new LinkedList<WeakReference<Object>>();
	// private final ScheduledExecutorService executorService = Executors
	// .newScheduledThreadPool(NUM_THREADS);
	private final DataSource dataSource = new DummyDataSource();
	private final InVehicleDeviceStatus status; // この変数を利用するときはロックする

	public InVehicleDeviceLogic() {
		this.statusFile = new EmptyFile();
		this.status = new InVehicleDeviceStatus();
	}

	public InVehicleDeviceLogic(File statusFile) {
		this.statusFile = statusFile;
		this.status = InVehicleDeviceStatus.load(statusFile);
		try {
			this.status.operationSchedules.addAll(getDataSource()
					.getOperationSchedules());
			this.status.initialized.set(true);
		} catch (WebAPIException e) {
			Log.e(TAG, "", e);
			return;
		}

		// try {
		// executorService.submit(new OperationScheduleReceiver(this));
		// executorService.scheduleWithFixedDelay(
		// new VehicleNotificationReceiver(this), 0,
		// POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		// executorService.scheduleWithFixedDelay(new ScheduleChangedReceiver(
		// this), 0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
		// } catch (RejectedExecutionException e) {
		// Log.e(TAG, "Task Rejected", e);
		// }
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
		eventBus.post(new EnterDriveStatusEvent());
	}

	public void enterFinishStatus() {
		synchronized (status) {
			if (status.status == InVehicleDeviceStatus.Status.FINISH) {
				return;
			}
			status.status = InVehicleDeviceStatus.Status.FINISH;
		}
		eventBus.post(new EnterFinishStatusEvent());
	}

	public void enterPlatformStatus() {
		synchronized (status) {
			if (status.status == InVehicleDeviceStatus.Status.PLATFORM) {
				return;
			}
			status.status = InVehicleDeviceStatus.Status.PLATFORM;
		}
		eventBus.post(new EnterPlatformStatusEvent());
	}

	@Override
	protected void finalize() {
		try {
			shutdown();
		} finally {
			try {
				super.finalize();
			} catch (Throwable e) {
			}
		}
	}

	public DataSource getDataSource() {
		return dataSource;
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

	public InVehicleDeviceStatus.Status getStatus() {
		synchronized (status) {
			return status.status;
		}
	}

	public Boolean isInitialized() {
		synchronized (status) {
			return status.initialized.get();
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

	public void register(Object object) {
		// eventBus.register(object);
		// registeredObjectReferences.add(new WeakReference<Object>(object));
	}

	private void saveStatus() {
		synchronized (status) {
			status.save(statusFile);
		}
	}

	public void showConfigModal() {
		eventBus.post(new ConfigModal.ShowEvent());
	}

	public void showMemoModal(Reservation reservation) {
		eventBus.post(new MemoModal.ShowEvent(reservation));
	}

	public void showNotificationModal(
			List<VehicleNotification> vehicleNotifications) {
		eventBus.post(new NotificationModal.ShowEvent(vehicleNotifications));
	}

	public void showPauseModal() {
		eventBus.post(new PauseModal.ShowEvent());
	}

	public void showReturnPathModal(Reservation reservation) {
		eventBus.post(new ReturnPathModal.ShowEvent(reservation));
	}

	public void showScheduleChangedModal(
			List<VehicleNotification> vehicleNotification) {
		eventBus.post(new ScheduleChangedModal.ShowEvent(vehicleNotification));
	}

	public void showScheduleModal() {
		eventBus.post(new ScheduleModal.ShowEvent());
	}

	public void showStartCheckModal() {
		eventBus.post(new StartCheckModal.ShowEvent());
	}

	public void showStopCheckModal() {
		eventBus.post(new StopCheckModal.ShowEvent());
	}

	public void showStopModal() {
		eventBus.post(new StopModal.ShowEvent());
	}

	public void shutdown() {
		unregisterAll();
		// executorService.shutdownNow();
	}

	public void unregisterAll() {
		for (WeakReference<Object> objectReference : registeredObjectReferences) {
			Object object = objectReference.get();
			if (object != null) {
				eventBus.unregister(object);
			}
		}
		registeredObjectReferences.clear();
	}
}
