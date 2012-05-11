package com.kogasoftware.odt.invehicledevice.logic;

import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.backgroundtask.CommonEventSubscriber;
import com.kogasoftware.odt.invehicledevice.backgroundtask.PassengerRecordEventSubscriber;
import com.kogasoftware.odt.invehicledevice.logic.Status.Phase;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.ReadOnlyStatusAccess;
import com.kogasoftware.odt.invehicledevice.logic.StatusAccess.Reader;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSourceFactory;
import com.kogasoftware.odt.invehicledevice.logic.datasource.WebAPIDataSource;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterDrivePhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterFinishPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.EnterPlatformPhaseEvent;
import com.kogasoftware.odt.invehicledevice.logic.event.UiEventBus;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 車載機の内部共通ロジック
 */
public class CommonLogic {
	public static enum PayTiming {
		GET_ON, GET_OFF,
	}

	private static final Object DEFAULT_DATE_LOCK = new Object();
	private static Optional<Date> defaultDate = Optional.absent();

	public static Handler getActivityHandler(Activity activity)
			throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Handler> handler = new AtomicReference<Handler>();
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				handler.set(new Handler());
				latch.countDown();
			}
		});
		latch.await();
		Preconditions.checkNotNull(handler.get());
		return handler.get();

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
	@Deprecated
	private final StatusAccess statusAccessDeprecated;
	private final ReadOnlyStatusAccess statusAccess;
	private final CommonEventSubscriber commonEventSubscriber;
	private final PassengerRecordEventSubscriber passengerRecordEventSubscriber;

	public CommonLogic() {
		statusAccessDeprecated = new StatusAccess();
		this.statusAccess = statusAccessDeprecated.getReadOnlyStatusAccess();
		dataSource = DataSourceFactory.newInstance("http://127.0.0.1", "");
		eventBus = new UiEventBus();
		commonEventSubscriber = new CommonEventSubscriber(this,
				statusAccessDeprecated);
		passengerRecordEventSubscriber = new PassengerRecordEventSubscriber(
				this, statusAccessDeprecated);
	}

	public CommonLogic(Activity activity, Handler activityHandler,
			StatusAccess statusAccess) {
		this.statusAccessDeprecated = statusAccess;
		this.statusAccess = statusAccess.getReadOnlyStatusAccess();
		commonEventSubscriber = new CommonEventSubscriber(this, statusAccess);
		passengerRecordEventSubscriber = new PassengerRecordEventSubscriber(
				this, statusAccess);
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(activity);
		String url = preferences.getString(SharedPreferencesKey.SERVER_URL,
				WebAPIDataSource.DEFAULT_URL);
		String token = preferences.getString(
				SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN, "");
		dataSource = DataSourceFactory.newInstance(url, token);

		eventBus = new UiEventBus(activityHandler);
		for (Object object : new Object[] { activity, commonEventSubscriber,
				passengerRecordEventSubscriber }) {
			eventBus.register(object);
		}
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

	public Integer countRegisteredClass(Class<?> c) {
		return eventBus.countRegisteredClass(c);
	}

	public void dispose() {
		eventBus.dispose();
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

	public List<PassengerRecord> getGetOffPassengerRecords() {
		return statusAccess.read(new Reader<List<PassengerRecord>>() {
			@Override
			public List<PassengerRecord> read(Status status) {
				return new LinkedList<PassengerRecord>(
						status.sendLists.getOffPassengerRecords);
			}
		});
	}

	public List<PassengerRecord> getGetOnPassengerRecords() {
		return statusAccess.read(new Reader<List<PassengerRecord>>() {
			@Override
			public List<PassengerRecord> read(Status status) {
				return new LinkedList<PassengerRecord>(
						status.sendLists.getOnPassengerRecords);
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

	public List<VehicleNotification> getReceivingOperationScheduleChangedVehicleNotifications() {
		return statusAccess.read(new Reader<List<VehicleNotification>>() {
			@Override
			public List<VehicleNotification> read(Status status) {
				return new LinkedList<VehicleNotification>(
						status.receivingOperationScheduleChangedVehicleNotifications);
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

	public List<PassengerRecord> getRidingPassengerRecords() {
		return statusAccess.read(new Reader<List<PassengerRecord>>() {
			@Override
			public List<PassengerRecord> read(Status status) {
				return new LinkedList<PassengerRecord>(
						status.ridingPassengerRecords);
			}
		});
	}

	public Optional<ServiceUnitStatusLog> getServiceUnitStatusLog() {
		return statusAccess.read(new Reader<Optional<ServiceUnitStatusLog>>() {
			@Override
			public Optional<ServiceUnitStatusLog> read(Status status) {
				if (!status.serviceUnitStatusLogLocationEnabled) {
					return Optional.absent();
				}
				return Optional.of(status.serviceUnitStatusLog);
			}
		});
	}

	public ReadOnlyStatusAccess getStatusAccess() {
		return statusAccess;
	}

	@Deprecated
	public StatusAccess getStatusAccessDeprecated() {
		return statusAccessDeprecated;
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

	public List<VehicleNotification> getVehicleNotifications() {
		return statusAccess.read(new Reader<List<VehicleNotification>>() {
			@Override
			public List<VehicleNotification> read(Status status) {
				return new LinkedList<VehicleNotification>(
						status.vehicleNotifications);
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

	public void postEvent(Object event) {
		eventBus.post(event);
	}

	public void registerEventListener(Object eventListener) {
		eventBus.register(eventListener);
	}

	public void restoreStatus() {
		switch (getPhase()) {
		case INITIAL:
			postEvent(new EnterDrivePhaseEvent());
			break;
		case DRIVE:
			postEvent(new EnterDrivePhaseEvent());
			break;
		case PLATFORM:
			postEvent(new EnterPlatformPhaseEvent());
			break;
		case FINISH:
			postEvent(new EnterFinishPhaseEvent());
			break;
		}
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