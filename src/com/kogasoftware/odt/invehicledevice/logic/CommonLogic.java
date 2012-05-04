package com.kogasoftware.odt.invehicledevice.logic;

import java.math.BigDecimal;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.common.base.Optional;
import com.google.common.eventbus.Subscribe;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.logic.Status.Phase;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Writer;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.PauseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.StopEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.invehicledevice.logic.event.UnexpectedReservationAddedEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UpdatedOperationScheduleMergedEvent;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.User;

/**
 * 車載機の内部共通ロジック
 */
@UiEventBus.HighPriority
public class CommonLogic {
	public static enum PayTiming {
		GET_ON, GET_OFF,
	}

	private static final String TAG = CommonLogic.class.getSimpleName();
	private static final Object DEFAULT_DATE_LOCK = new Object();
	private static Optional<Date> defaultDate = Optional.absent();
	private static final AtomicBoolean willClearStatusFile = new AtomicBoolean(
			false);
	private static final Integer UNEXPECTED_RESERVATION_ID = -1;

	public static final Integer VEHICLE_NOTIFICATION_TYPE_SCHEDULE_CHANGED = 2;

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
	private final UiEventBus eventBus;
	private final StatusAccess statusAccess;

	public CommonLogic() {
		this.statusAccess = new StatusAccess();
		this.dataSource = DataSourceFactory.newInstance("http://127.0.0.1", "");
		this.eventBus = new UiEventBus();
	}

	public CommonLogic(Activity activity, Handler activityHandler) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(activity);

		dataSource = DataSourceFactory.newInstance(
				preferences.getString("url", "http://127.0.0.1"),
				preferences.getString("token", ""));
		statusAccess = new StatusAccess(activity,
				willClearStatusFile.getAndSet(false));
		eventBus = new UiEventBus(activityHandler);
		eventBus.register(this);
		eventBus.register(activity);
		for (Integer resourceId : new Integer[] { R.id.config_modal_view,
				R.id.start_check_modal_view, R.id.schedule_modal_view,
				R.id.memo_modal_view, R.id.pause_modal_view,
				R.id.return_path_modal_view, R.id.stop_check_modal_view,
				R.id.stop_modal_view, R.id.notification_modal_view,
				R.id.schedule_changed_modal_view, R.id.navigation_modal_view,
				R.id.phase_text_view, R.id.drive_phase_view,
				R.id.platform_phase_view, R.id.finish_phase_view }) {
			View view = activity.findViewById(resourceId);
			if (view != null) {
				eventBus.register(view);
			}
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
		final User user = new User();
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				user.setFirstName("飛び乗りユーザー"
						+ status.unexpectedReservationSequence);
				status.unexpectedReservationSequence++;
			}
		});
		reservation.setUser(user);
		final PassengerRecord passengerRecord = new PassengerRecord();
		passengerRecord.setReservationId(reservation.getId());
		passengerRecord.setReservation(reservation);
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.unexpectedPassengerRecords.add(passengerRecord);
				status.unhandledPassengerRecords.add(passengerRecord);
			}
		});
		eventBus.post(new UnexpectedReservationAddedEvent());
	}

	public void cancelPause() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog
						.setStatus(ServiceUnitStatusLogs.Status.OPERATION);
			}
		});
	}

	public void clearSelectedPassengerRecords() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.selectedPassengerRecords.clear();
			}
		});
	}

	public void dispose() {
		eventBus.dispose();
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
					Identifiables.merge(
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
				Identifiables.merge(status.sendLists.arrivalOperationSchedules,
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
			if (!getPayTiming().contains(PayTiming.GET_OFF)
					&& getPayTiming().contains(PayTiming.GET_ON)
					&& passengerRecord.getReservation().isPresent()) {
				passengerRecord.setPayment(passengerRecord.getReservation()
						.get().getPayment());
			}
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

	public EnumSet<PayTiming> getPayTiming() {
		return EnumSet.of(PayTiming.GET_ON);
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

	public List<PassengerRecord> getUnhandledPassengerRecords() {
		return statusAccess.read(new Reader<List<PassengerRecord>>() {
			@Override
			public List<PassengerRecord> read(Status status) {
				return new LinkedList<PassengerRecord>(
						status.unhandledPassengerRecords);
			}
		});
	}

	public Boolean isOperationScheduleInitialized() {
		return statusAccess.read(new Reader<Boolean>() {
			@Override
			public Boolean read(Status status) {
				return (status.operationScheduleInitializedSign
						.availablePermits() > 0);
			}
		});
	}

	public Boolean isUnexpectedPassengerRecord(
			final PassengerRecord passengerRecord) {
		return statusAccess.read(new Reader<Boolean>() {
			@Override
			public Boolean read(Status status) {
				return status.unexpectedPassengerRecords
						.contains(passengerRecord);
			}
		});
	}

	@Subscribe
	public void pause(PauseEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog
						.setStatus(ServiceUnitStatusLogs.Status.PAUSE);
			}
		});
	}

	public void postEvent(Object event) {
		eventBus.post(event);
	}

	public void registerEventListener(Object eventListener) {
		eventBus.register(eventListener);
	}

	public void restoreStatus() {
		switch (getPhase()) {
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
	public void restoreStatus(UpdatedOperationScheduleMergedEvent event) {
		restoreStatus();
	}

	public void setLocation(final Location location) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLogLocationEnabled = true;
				status.serviceUnitStatusLog.setLatitude(new BigDecimal(location
						.getLatitude()));
				status.serviceUnitStatusLog.setLongitude(new BigDecimal(
						location.getLongitude()));
			}
		});
	}

	public void setOperationScheduleInitialized() {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.operationScheduleInitializedSign.release();
			}
		});
	}

	@Subscribe
	public void stop(StopEvent e) {
		statusAccess.write(new Writer() {
			@Override
			public void write(Status status) {
				status.serviceUnitStatusLog
						.setStatus(ServiceUnitStatusLogs.Status.STOP);
			}
		});
	}

	public void waitForOperationScheduleInitialize()
			throws InterruptedException {
		Semaphore operationScheduleInitializedSign = statusAccess
				.read(new Reader<Semaphore>() {
					@Override
					public Semaphore read(Status status) {
						return status.operationScheduleInitializedSign;
					}
				});
		operationScheduleInitializedSign.acquire();
		operationScheduleInitializedSign.release();
	}
}
