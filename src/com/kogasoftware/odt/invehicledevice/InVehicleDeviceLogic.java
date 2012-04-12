package com.kogasoftware.odt.invehicledevice;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.google.common.eventbus.EventBus;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceStatus.Access;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DummyDataSource;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
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
	public static class AddUnexpectedReservationEvent {
		public final Reservation reservation;

		public AddUnexpectedReservationEvent(Reservation reservation) {
			this.reservation = reservation;
		}
	}

	private static AtomicBoolean clearStatusFile = new AtomicBoolean(false);

	public static void clearStatusFile() {
		clearStatusFile.set(true);
	}

	public static class EnterDriveStatusEvent {
	}

	public static class EnterFinishStatusEvent {
	}

	public static class EnterPlatformStatusEvent {
	}

	public static class LoadThread extends Thread {
		public static class CompleteEvent {
			public final InVehicleDeviceLogic logic;

			public CompleteEvent(InVehicleDeviceLogic logic) {
				this.logic = logic;
			}
		}

		private final Activity activity;

		public LoadThread(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void run() {
			InVehicleDeviceLogic logic = null;
			try {
				logic = new InVehicleDeviceLogic(activity,
						InVehicleDeviceActivity.getSavedStatusFile(activity));
				final Semaphore semaphore = new Semaphore(0); // Runnable内容が実行される前にinterruptされた場合も確実にshutdownする用
				final InVehicleDeviceLogic finalLogic = logic;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						finalLogic.eventBus.post(new CompleteEvent(finalLogic));
						semaphore.release();
					}
				});
				semaphore.acquire();
			} catch (InterruptedException e) {
				if (logic != null) {
					logic.shutdown();
				}
			}
		}
	}

	private static class OperationScheduleReceiver implements Runnable {
		private final InVehicleDeviceLogic logic;

		public OperationScheduleReceiver(InVehicleDeviceLogic logic) {
			this.logic = logic;
		}

		@Override
		public void run() {
			final List<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();
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
			logic.statusAccess.write(new Access.Writer() {
				@Override
				public void write(InVehicleDeviceStatus status) {
					status.operationSchedules.clear();
					status.operationSchedules.addAll(operationSchedules);
					status.initialized.set(true);
				}
			});
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
				final List<VehicleNotification> vehicleNotification = logic
						.getDataSource().getVehicleNotifications();
				logic.statusAccess.write(new Access.Writer() {
					@Override
					public void write(InVehicleDeviceStatus status) {
						status.vehicleNotifications.addAll(vehicleNotification);
					}
				});

			} catch (WebAPIException e) {
			}
		}
	}

	public static class SpeakEvent {
		public final String message;

		public SpeakEvent(String message) {
			this.message = message;
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
				final List<VehicleNotification> vehicleNotification = logic
						.getDataSource().getVehicleNotifications();
				logic.statusAccess.write(new Access.Writer() {
					@Override
					public void write(InVehicleDeviceStatus status) {
						status.vehicleNotifications.addAll(vehicleNotification);
					}
				});
			} catch (WebAPIException e) {
			}
		}
	}

	private static final String TAG = InVehicleDeviceLogic.class
			.getSimpleName();
	private static final Integer POLLING_PERIOD_MILLIS = 30 * 1000;
	private static final Integer NUM_THREADS = 10;
	private final EventBus eventBus = new EventBus();
	private final List<WeakReference<Object>> registeredObjectReferences = new LinkedList<WeakReference<Object>>();
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final DataSource dataSource = new DummyDataSource();
	private final InVehicleDeviceStatus.Access statusAccess;
	private Thread voiceThread = new EmptyThread();

	public InVehicleDeviceLogic() {
		this.statusAccess = new InVehicleDeviceStatus.Access();
	}

	public InVehicleDeviceLogic(Activity activity, File statusFile)
			throws InterruptedException {
		if (clearStatusFile.getAndSet(false)) {
			if (statusFile.exists() && !statusFile.delete()) {
				Log.e(TAG, "!(statusFile.exists() && !statusFile.delete())");
			}
		}
		try {
			Thread.sleep(0); // interruption point
			this.statusAccess = new InVehicleDeviceStatus.Access(statusFile);

			try {
				executorService.submit(new OperationScheduleReceiver(this));
				executorService.scheduleWithFixedDelay(
						new VehicleNotificationReceiver(this), 0,
						POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
				executorService.scheduleWithFixedDelay(
						new ScheduleChangedReceiver(this), 0,
						POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
			} catch (RejectedExecutionException e) {
				Log.e(TAG, "Task Rejected", e);
			}

			register(activity);
			Thread.sleep(0); // interruption point
			voiceThread.interrupt();
			voiceThread = new VoiceThread(activity);
			voiceThread.start();
			register(voiceThread);
			Thread.sleep(0); // interruption point
			for (int resourceId : new int[] { R.id.config_modal,
					R.id.start_check_modal, R.id.schedule_modal,
					R.id.memo_modal, R.id.pause_modal, R.id.return_path_modal,
					R.id.stop_check_modal, R.id.stop_modal,
					R.id.notification_modal, R.id.schedule_changed_modal,
					R.id.navigation_modal, R.id.login_modal }) {
				View view = activity.findViewById(resourceId);
				register(view);
				Thread.sleep(0); // interruption point
			}
		} catch (InterruptedException e) {
			shutdown();
			throw e;
		}
	}

	public void addUnexpectedReservation(Integer arrivalOperationScheduleId) {
		List<OperationSchedule> operationSchedules = getRemainingOperationSchedules();
		if (operationSchedules.isEmpty()) {
			Log.w(TAG, "operationSchedules.isEmpty()", new Exception());
			return;
		}
		OperationSchedule operationSchedule = operationSchedules.get(0);
		final Reservation reservation = new Reservation();

		Integer id = statusAccess
				.readAndWrite(new Access.ReaderAndWriter<Integer>() {
					@Override
					public Integer readAndWrite(InVehicleDeviceStatus status) {
						return status.unexpectedReservationSequence++;
					}
				});
		reservation.setId(id); // TODO
		// 未予約乗車の予約情報はどうするか
		reservation.setDepartureScheduleId(operationSchedule.getId());
		reservation.setArrivalScheduleId(arrivalOperationScheduleId);

		statusAccess.write(new Access.Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.unexpectedReservations.add(reservation);
			}
		});
		eventBus.post(new AddUnexpectedReservationEvent(reservation));
	}

	public void enterDriveStatus() {
		statusAccess.write(new Access.Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				if (status.status == InVehicleDeviceStatus.Status.PLATFORM) {
					status.currentOperationScheduleIndex++;
				}
				status.status = InVehicleDeviceStatus.Status.DRIVE;
			}
		});
		eventBus.post(new EnterDriveStatusEvent());
	}

	public void enterFinishStatus() {
		statusAccess.write(new Access.Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.status = InVehicleDeviceStatus.Status.FINISH;
			}
		});
		eventBus.post(new EnterFinishStatusEvent());
	}

	public void enterNextStatus() {
		statusAccess.write(new Access.Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				if (status.status == InVehicleDeviceStatus.Status.DRIVE) {
					enterPlatformStatus();
				} else {
					showStartCheckModal();
				}
			}
		});
	}

	public void enterPlatformStatus() {
		statusAccess.write(new Access.Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.status = InVehicleDeviceStatus.Status.PLATFORM;
			}
		});
		eventBus.post(new EnterPlatformStatusEvent());
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public List<Reservation> getMissedReservations() {
		return statusAccess.read(new Access.Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(InVehicleDeviceStatus status) {
				return new LinkedList<Reservation>(status.missedReservations);
			}
		});
	}

	public List<OperationSchedule> getOperationSchedules() {
		return statusAccess.read(new Access.Reader<List<OperationSchedule>>() {
			@Override
			public List<OperationSchedule> read(InVehicleDeviceStatus status) {
				return new LinkedList<OperationSchedule>(
						status.operationSchedules);
			}
		});
	}

	public List<OperationSchedule> getRemainingOperationSchedules() {
		return statusAccess.read(new Access.Reader<List<OperationSchedule>>() {
			@Override
			public List<OperationSchedule> read(InVehicleDeviceStatus status) {
				try {
					List<OperationSchedule> remainings = new LinkedList<OperationSchedule>(
							status.operationSchedules);
					return remainings.subList(
							status.currentOperationScheduleIndex,
							remainings.size());
				} catch (ArrayIndexOutOfBoundsException e) {
				} catch (IllegalArgumentException e) {
				}
				return new LinkedList<OperationSchedule>();
			}
		});
	}

	public List<Reservation> getRidingReservations() {
		return statusAccess.read(new Access.Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(InVehicleDeviceStatus status) {

				return new LinkedList<Reservation>(status.ridingReservations);
			}
		});
	}

	public List<Reservation> getUnexpectedReservations() {
		return statusAccess.read(new Access.Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(InVehicleDeviceStatus status) {
				return new LinkedList<Reservation>(
						status.unexpectedReservations);
			}
		});
	}

	public Boolean isInitialized() {
		return statusAccess.read(new Access.Reader<Boolean>() {
			@Override
			public Boolean read(InVehicleDeviceStatus status) {
				return status.initialized.get();
			}
		});
	}

	public List<VehicleNotification> pollVehicleNotifications() {
		return statusAccess
				.read(new Access.Reader<List<VehicleNotification>>() {
					@Override
					public List<VehicleNotification> read(
							InVehicleDeviceStatus status) {
						LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>(
								status.vehicleNotifications);
						status.vehicleNotifications.clear();
						return vehicleNotifications;
					}
				});
	}

	public void register(Object object) {
		eventBus.register(object);
		registeredObjectReferences.add(new WeakReference<Object>(object));
	}

	public void restoreStatus() {
		statusAccess.write(new Access.Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				if (status.status == InVehicleDeviceStatus.Status.PLATFORM) {
					enterPlatformStatus();
				} else {
					enterDriveStatus();
				}
			}
		});
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
		executorService.shutdownNow();
		voiceThread.interrupt();
	}

	public void speak(String message) {
		eventBus.post(new SpeakEvent(message));
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
