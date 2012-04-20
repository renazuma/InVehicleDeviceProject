package com.kogasoftware.odt.invehicledevice.logic;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.Utility;
import com.kogasoftware.odt.invehicledevice.arrayadapter.ReservationArrayAdapter;
import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;
import com.kogasoftware.odt.invehicledevice.event.AddUnexpectedReservationEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.event.UiEventBus;
import com.kogasoftware.odt.invehicledevice.event.UpdateOperationScheduleCompleteEvent;
import com.kogasoftware.odt.invehicledevice.logic.Status.Phase;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.VoiceThread.SpeakEvent;
import com.kogasoftware.odt.invehicledevice.modalview.ConfigModalView;
import com.kogasoftware.odt.invehicledevice.modalview.MemoModalView;
import com.kogasoftware.odt.invehicledevice.modalview.NotificationModalView;
import com.kogasoftware.odt.invehicledevice.modalview.PauseModalView;
import com.kogasoftware.odt.invehicledevice.modalview.ReturnPathModalView;
import com.kogasoftware.odt.invehicledevice.modalview.ScheduleChangedModalView;
import com.kogasoftware.odt.invehicledevice.modalview.ScheduleModalView;
import com.kogasoftware.odt.invehicledevice.modalview.StartCheckModalView;
import com.kogasoftware.odt.invehicledevice.modalview.StopCheckModalView;
import com.kogasoftware.odt.invehicledevice.modalview.StopModalView;
import com.kogasoftware.odt.invehicledevice.phaseview.PlatformPhaseView;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 車載機の内部ロジック
 */
public class Logic {
	private static Optional<Date> defaultDate = Optional.absent();
	private static final Object DEFAULT_DATE_LOCK = new Object();
	private static final Integer NUM_THREADS = 3;
	private static final Integer POLLING_PERIOD_MILLIS = 30 * 1000;
	private static final String TAG = Logic.class.getSimpleName();

	private static final AtomicBoolean willClearStatusFile = new AtomicBoolean(
			false);
	public static final Integer UNEXPECTED_RESERVATION_ID = -1;

	public static void clearStatusFile() {
		willClearStatusFile.set(true);
	}

	public static Date getDate() {
		if (!BuildConfig.DEBUG) {
			return new Date();
		}
		synchronized (DEFAULT_DATE_LOCK) {
			if (defaultDate.isPresent()) {
				return defaultDate.get();
			}
		}
		return new Date();
	}

	public static void setDate(Date date) {
		if (BuildConfig.DEBUG) {
			synchronized (DEFAULT_DATE_LOCK) {
				defaultDate = Optional.of(date);
			}
		}
	}

	private final DataSource dataSource;
	private final UiEventBus eventBus = new UiEventBus();
	private final ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(NUM_THREADS);
	private final StatusAccess statusAccess;
	private Thread voiceThread = new EmptyThread();
	private Thread looperThread = new EmptyThread();
	private VehicleNotificationReceiver vehicleNotificationReceiver = new VehicleNotificationReceiver();
	private VehicleNotificationSender vehicleNotificationSender = new VehicleNotificationSender();
	private ScheduleChangedReceiver scheduleChangedReceiver = new ScheduleChangedReceiver();
	private OperationScheduleSender operationScheduleSender = new OperationScheduleSender();
	private PassengerRecordSender passengerRecordSender = new PassengerRecordSender();

	public Logic() {
		this.statusAccess = new StatusAccess();
		this.dataSource = DataSourceFactory.newInstance("http://127.0.0.1", "");
	}

	public Logic(Activity activity) throws InterruptedException {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(activity);

		dataSource = DataSourceFactory.newInstance(
				preferences.getString("url", "http://127.0.0.1"),
				preferences.getString("token", ""));
		try {
			Thread.sleep(0); // interruption point
			this.statusAccess = new StatusAccess(activity,
					willClearStatusFile.getAndSet(false));

			try {
				executorService.scheduleWithFixedDelay(
						vehicleNotificationReceiver, 0, POLLING_PERIOD_MILLIS,
						TimeUnit.MILLISECONDS);
				executorService.scheduleWithFixedDelay(
						vehicleNotificationSender, 0, POLLING_PERIOD_MILLIS,
						TimeUnit.MILLISECONDS);
				executorService.scheduleWithFixedDelay(scheduleChangedReceiver,
						0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
				executorService.scheduleWithFixedDelay(operationScheduleSender,
						0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
				executorService.scheduleWithFixedDelay(passengerRecordSender,
						0, POLLING_PERIOD_MILLIS, TimeUnit.MILLISECONDS);
			} catch (RejectedExecutionException e) {
				Log.e(TAG, "Task Rejected", e);
			}

			Thread.sleep(0); // interruption point
			eventBus.register(this);
			Thread.sleep(0); // interruption point
			eventBus.register(activity);
			Thread.sleep(0); // interruption point
			voiceThread = new VoiceThread(activity);
			voiceThread.start();
			looperThread = new LooperThread(this, activity);
			looperThread.start();
			eventBus.register(voiceThread);
			Thread.sleep(0); // interruption point
			for (Integer resourceId : new Integer[] { R.id.config_modal_view,
					R.id.start_check_modal_view, R.id.schedule_modal_view,
					R.id.memo_modal_view, R.id.pause_modal_view,
					R.id.return_path_modal_view, R.id.stop_check_modal_view,
					R.id.stop_modal_view, R.id.notification_modal_view,
					R.id.schedule_changed_modal_view,
					R.id.navigation_modal_view, R.id.login_modal_view,
					R.id.phase_text_view, R.id.drive_phase_view,
					R.id.platform_phase_view, R.id.finish_phase_view }) {
				View view = activity.findViewById(resourceId);
				eventBus.register(view);
				Thread.sleep(0); // interruption point
			}

			for (LogicUser logicUser : new LogicUser[] {
					vehicleNotificationReceiver, vehicleNotificationSender,
					scheduleChangedReceiver, operationScheduleSender,
					passengerRecordSender }) {
				eventBus.register(logicUser);
			}

			if (getPhase() != Phase.FINISH
					&& getRemainingOperationSchedules().isEmpty()) {
				try {
					executorService.submit(new OperationScheduleReceiver(this))
							.get();
				} catch (ExecutionException e) {
					Log.w(TAG, e);
				}
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

		reservation.setId(UNEXPECTED_RESERVATION_ID);
		// 未予約乗車の予約情報はどうするか
		reservation.setDepartureScheduleId(operationSchedule.getId());
		reservation.setArrivalScheduleId(arrivalOperationScheduleId);

		eventBus.post(new AddUnexpectedReservationEvent(reservation));
	}

	public void cancelPause() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.paused = false;
			}
		});
	}

	public void enterDrivePhase() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				if (status.remainingOperationSchedules.isEmpty()) {
					enterFinishPhase();
					return;
				}
				OperationSchedule operationSchedule = status.remainingOperationSchedules
						.get(0);
				if (status.phase == Status.Phase.PLATFORM) {
					status.remainingOperationSchedules
							.remove(operationSchedule);
					status.finishedOperationSchedules.add(operationSchedule);
					Utility.mergeById(
							status.sendLists.departureOperationSchedules,
							operationSchedule);
				}
				status.phase = Status.Phase.DRIVE;
			}
		});
		eventBus.post(new EnterDrivePhaseEvent());
	}

	public void enterFinishPhase() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.phase = Status.Phase.FINISH;
			}
		});
		eventBus.post(new EnterFinishPhaseEvent());
	}

	public void enterPlatformPhase() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.phase = Status.Phase.PLATFORM;
				if (status.remainingOperationSchedules.isEmpty()) {
					enterFinishPhase();
					return;
				}
				OperationSchedule operationSchedule = status.remainingOperationSchedules
						.get(0);
				Utility.mergeById(status.sendLists.arrivalOperationSchedules,
						operationSchedule);
			}
		});
		eventBus.post(new EnterPlatformPhaseEvent());
	}

	public Optional<OperationSchedule> getCurrentOperationSchedule() {
		return statusAccess.read(new Reader<Optional<OperationSchedule>>() {
			@Override
			public Optional<OperationSchedule> read(Status status) {
				if (status.remainingOperationSchedules.isEmpty()) {
					return Optional.absent();
				} else {
					return Optional.of(status.remainingOperationSchedules
							.get(0));
				}
			}
		});
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public UiEventBus getEventBus() {
		return eventBus;
	}

	public ScheduledExecutorService getExecutorService() {
		return executorService;
	}

	public List<OperationSchedule> getFinishedOperationSchedules() {
		return statusAccess.read(new Reader<List<OperationSchedule>>() {
			@Override
			public List<OperationSchedule> read(Status status) {
				return new LinkedList<OperationSchedule>(
						status.finishedOperationSchedules);
			}
		});
	}

	public void getOffPassengerRecords(OperationSchedule operationSchedule,
			final List<PassengerRecord> selectedGetOffPassengerRecords) {
		Date now = getDate();
		for (PassengerRecord passengerRecord : selectedGetOffPassengerRecords) {
			passengerRecord.setGetOffTime(now);
			passengerRecord.setArrivalOperationScheduleId(operationSchedule
					.getId());
			passengerRecord.setArrivalOperationSchedule(operationSchedule);
		}
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.sendLists.getOffPassengerRecords
						.addAll(selectedGetOffPassengerRecords);
				status.ridingPassengerRecords
						.removeAll(selectedGetOffPassengerRecords);
				status.finishedPassengerRecords
						.addAll(selectedGetOffPassengerRecords);
			}
		});
	}

	public void getOnPassengerRecords(OperationSchedule operationSchedule,
			final List<PassengerRecord> selectedGetOnPassengerRecords) {
		Date now = getDate();
		for (PassengerRecord passengerRecord : selectedGetOnPassengerRecords) {
			passengerRecord.setGetOnTime(now);
			passengerRecord.setDepartureOperationScheduleId(operationSchedule
					.getId());
			passengerRecord.setDepartureOperationSchedule(operationSchedule);
		}
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.sendLists.getOnPassengerRecords
						.addAll(selectedGetOnPassengerRecords);
				status.unhandledPassengerRecords
						.removeAll(selectedGetOnPassengerRecords);
				status.ridingPassengerRecords
						.addAll(selectedGetOnPassengerRecords);
			}
		});
	}

	public Phase getPhase() {
		return statusAccess.read(new Reader<Phase>() {
			@Override
			public Phase read(Status status) {
				return status.phase;
			}
		});
	}

	public List<OperationSchedule> getRemainingOperationSchedules() {
		return statusAccess.read(new Reader<List<OperationSchedule>>() {
			@Override
			public List<OperationSchedule> read(Status status) {
				return new LinkedList<OperationSchedule>(
						status.remainingOperationSchedules);
			}
		});
	}

	public StatusAccess getStatusAccess() {
		return statusAccess;
	}

	public String getToken() {
		return statusAccess.read(new Reader<String>() {
			@Override
			public String read(Status status) {
				return status.token;
			}
		});
	}

	public Boolean isInitialized() {
		return statusAccess.read(new Reader<Boolean>() {
			@Override
			public Boolean read(Status status) {
				return status.initialized;
			}
		});
	}

	public void pause() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.paused = true;
			}
		});
		eventBus.post(new PauseModalView.ShowEvent());
	}

	public void restoreStatus() {
		Phase phase = statusAccess.read(new Reader<Phase>() {
			@Override
			public Phase read(Status status) {
				return status.phase;
			}
		});
		switch (phase) {
		case INITIAL:
			enterDrivePhase();
			break;
		case DRIVE:
			enterDrivePhase();
			break;
		case PLATFORM:
			enterPlatformPhase();
			break;
		case FINISH:
			enterFinishPhase();
			break;
		}
	}

	@Subscribe
	public void restoreStatus(UpdateOperationScheduleCompleteEvent event) {
		restoreStatus();
	}

	public void setLocation(final Location location) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.location = Optional.of(location);
			}
		});
	}

	public void showConfigModalView() {
		eventBus.post(new ConfigModalView.ShowEvent());
	}

	public void showMemoModalView(Reservation reservation) {
		eventBus.post(new MemoModalView.ShowEvent(reservation));
	}

	public void showNotificationModalView(
			List<VehicleNotification> vehicleNotifications) {
		eventBus.post(new NotificationModalView.ShowEvent(vehicleNotifications));
	}

	public void showReturnPathModalView(Reservation reservation) {
		eventBus.post(new ReturnPathModalView.ShowEvent(reservation));
	}

	public void showScheduleChangedModalView(
			List<VehicleNotification> vehicleNotification) {
		eventBus.post(new ScheduleChangedModalView.ShowEvent(
				vehicleNotification));
	}

	public void showScheduleModalView() {
		eventBus.post(new ScheduleModalView.ShowEvent());
	}

	public void showStartCheckModalView() {
		eventBus.post(new PlatformPhaseView.StartCheckEvent());
	}

	public void showStartCheckModalView(ReservationArrayAdapter adapter) {
		eventBus.post(new StartCheckModalView.ShowEvent(adapter));
	}

	public void showStopCheckModalView() {
		eventBus.post(new StopCheckModalView.ShowEvent());
	}

	public void shutdown() {
		eventBus.dispose();
		executorService.shutdownNow();
		voiceThread.interrupt();
		looperThread.interrupt();
	}

	public void speak(String message) {
		eventBus.post(new SpeakEvent(message));
	}

	public void stop() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.stopped = true;
			}
		});
		eventBus.post(new StopModalView.ShowEvent());
	}
}
