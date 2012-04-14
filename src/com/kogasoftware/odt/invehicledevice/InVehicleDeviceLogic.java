package com.kogasoftware.odt.invehicledevice;

import java.io.File;
import java.util.Date;
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

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceStatus.Access.Reader;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceStatus.Access.ReaderAndWriter;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceStatus.Access.Writer;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
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

	private static AtomicBoolean willClearStatusFile = new AtomicBoolean(false);

	private static final Object defaultDateLock = new Object();
	private static Optional<Date> defaultDate = Optional.<Date> absent();

	public static void setDate(Date date) {
		if (BuildConfig.DEBUG) {
			synchronized (defaultDateLock) {
				defaultDate = Optional.of(date);
			}
		}
	}

	public static Date getDate() {
		if (!BuildConfig.DEBUG) {
			return new Date();
		}
		synchronized (defaultDateLock) {
			if (defaultDate.isPresent()) {
				return defaultDate.get();
			}
		}
		return new Date();
	}

	public static void clearStatusFile() {
		willClearStatusFile.set(true);
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
			logic.statusAccess.write(new Writer() {
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
				logic.statusAccess.write(new Writer() {
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
				logic.statusAccess.write(new Writer() {
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
	private final DisposableEventBus eventBus = new DisposableEventBus();
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final DataSource dataSource = DataSourceFactory.newInstance();
	private final InVehicleDeviceStatus.Access statusAccess;
	private Thread voiceThread = new EmptyThread();

	public InVehicleDeviceLogic() {
		this.statusAccess = new InVehicleDeviceStatus.Access();
	}

	public InVehicleDeviceLogic(Activity activity, File statusFile)
			throws InterruptedException {
		try {
			Thread.sleep(0); // interruption point
			if (willClearStatusFile.getAndSet(false)) {
				this.statusAccess = new InVehicleDeviceStatus.Access();
				if (statusFile.exists() && !statusFile.delete()) {
					Log.e(TAG, "!(statusFile.exists() && !statusFile.delete())");
				}
			} else {
				this.statusAccess = new InVehicleDeviceStatus.Access(statusFile);
			}

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

		Integer id = statusAccess.readAndWrite(new ReaderAndWriter<Integer>() {
			@Override
			public Integer readAndWrite(InVehicleDeviceStatus status) {
				return status.unexpectedReservationSequence++;
			}
		});
		reservation.setId(id); // TODO
		// 未予約乗車の予約情報はどうするか
		reservation.setDepartureScheduleId(operationSchedule.getId());
		reservation.setArrivalScheduleId(arrivalOperationScheduleId);

		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.unexpectedReservations.add(reservation);
			}
		});
		eventBus.post(new AddUnexpectedReservationEvent(reservation));
	}

	public void enterDriveStatus() {
		statusAccess.write(new Writer() {
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
		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.status = InVehicleDeviceStatus.Status.FINISH;
			}
		});
		eventBus.post(new EnterFinishStatusEvent());
	}

	public void enterPlatformStatus() {
		statusAccess.write(new Writer() {
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
		return statusAccess.read(new Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(InVehicleDeviceStatus status) {
				return new LinkedList<Reservation>(status.missedReservations);
			}
		});
	}

	public List<OperationSchedule> getOperationSchedules() {
		return statusAccess.read(new Reader<List<OperationSchedule>>() {
			@Override
			public List<OperationSchedule> read(InVehicleDeviceStatus status) {
				return new LinkedList<OperationSchedule>(
						status.operationSchedules);
			}
		});
	}

	public List<OperationSchedule> getRemainingOperationSchedules() {
		return statusAccess.read(new Reader<List<OperationSchedule>>() {
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
		return statusAccess.read(new Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(InVehicleDeviceStatus status) {
				return new LinkedList<Reservation>(status.ridingReservations);
			}
		});
	}

	public List<Reservation> getUnexpectedReservations() {
		return statusAccess.read(new Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(InVehicleDeviceStatus status) {
				return new LinkedList<Reservation>(
						status.unexpectedReservations);
			}
		});
	}

	public Boolean isInitialized() {
		return statusAccess.read(new Reader<Boolean>() {
			@Override
			public Boolean read(InVehicleDeviceStatus status) {
				return status.initialized.get();
			}
		});
	}

	public List<VehicleNotification> pollVehicleNotifications() {
		return statusAccess
				.readAndWrite(new ReaderAndWriter<List<VehicleNotification>>() {
					@Override
					public List<VehicleNotification> readAndWrite(
							InVehicleDeviceStatus status) {
						LinkedList<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>(
								status.vehicleNotifications);
						status.processingVehicleNotifications
						.addAll(status.vehicleNotifications);
						status.vehicleNotifications.clear();
						return vehicleNotifications;
					}
				});
	}

	public void replyVehicleNotification(
			final VehicleNotification vehicleNotification, Boolean answer) {
		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.processingVehicleNotifications
				.remove(vehicleNotification);
			}
		});
	}

	public void getOnReservation(final List<Reservation> reservations) {
		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.ridingReservations.addAll(reservations);
			}
		});
	}

	public void getOutReservation(final List<Reservation> reservations) {
		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.ridingReservations.removeAll(reservations);
			}
		});
	}

	public void register(Object object) {
		eventBus.register(object);
	}

	public void restoreStatus() {
		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				if (status.status == InVehicleDeviceStatus.Status.PLATFORM) {
					enterPlatformStatus();
				} else {
					enterDriveStatus();
				}
				if (status.paused) {
					pause();
				}
				if (status.stopped) {
					stop();
				}

				status.processingVehicleNotifications
				.addAll(status.vehicleNotifications);
				status.vehicleNotifications.clear();
				status.vehicleNotifications
				.addAll(new LinkedList<VehicleNotification>(
						status.processingVehicleNotifications));
				status.processingVehicleNotifications.clear();
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

	public void pause() {
		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.paused = true;
			}
		});
		eventBus.post(new PauseModal.ShowEvent());
	}

	public void cancelPause() {
		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.paused = false;
			}
		});
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

	public void showStartCheckModal(ReservationArrayAdapter adapter) {
		eventBus.post(new StartCheckModal.ShowEvent(adapter));
	}

	public void showStopCheckModal() {
		eventBus.post(new StopCheckModal.ShowEvent());
	}

	public void stop() {
		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.stopped = true;
			}
		});
		eventBus.post(new StopModal.ShowEvent());
	}

	public void shutdown() {
		eventBus.dispose();
		executorService.shutdownNow();
		voiceThread.interrupt();
	}

	public void speak(String message) {
		eventBus.post(new SpeakEvent(message));
	}

	public void addMissedReservations(List<Reservation> reservations) {
		final List<Reservation> finalReservations = reservations;
		statusAccess.write(new Writer() {
			@Override
			public void write(InVehicleDeviceStatus status) {
				status.missedReservations.addAll(finalReservations);
			}
		});
	}
}

class DisposableEventBus extends EventBus {
	private static final String TAG = DisposableEventBus.class.getSimpleName();
	private final List<Object> registeredObjects = new LinkedList<Object>();
	private Boolean disposed = false;

	@Override
	public void register(Object object) {
		if (disposed) {
			return;
		}
		super.register(object);
		registeredObjects.add(object);
	}

	public void dispose() {
		for (Object object : registeredObjects) {
			try {
				unregister(object);
			} catch (IllegalArgumentException e) {
				Log.w(TAG, e);
			}
		}
		registeredObjects.clear();
	}
}
