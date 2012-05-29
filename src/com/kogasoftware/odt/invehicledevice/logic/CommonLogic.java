package com.kogasoftware.odt.invehicledevice.logic;

import java.io.File;
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
import android.util.Log;
import android.view.View;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.BuildConfig;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.backgroundtask.CommonEventProcessor;
import com.kogasoftware.odt.invehicledevice.backgroundtask.VehicleNotificationEventProcessor;
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
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

/**
 * 車載機の内部共通ロジック
 */
public class CommonLogic {
	public static enum PayTiming {
		GET_ON, GET_OFF,
	}

	public static final Integer NEW_SCHEDULE_DOWNLOAD_HOUR = 3;
	private static final Object DEFAULT_DATE_LOCK = new Object();
	private static final String TAG = CommonLogic.class.getSimpleName();
	private static Optional<Long> mockDateOffset = Optional.absent();

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
			if (mockDateOffset.isPresent()) {
				return new Date(new Date().getTime() + mockDateOffset.get());
			}
			return new Date();
		}
	}

	public static void setDate(Date date) {
		if (!BuildConfig.DEBUG) {
			return;
		}
		synchronized (DEFAULT_DATE_LOCK) {
			Date now = new Date();
			mockDateOffset = Optional.of(date.getTime() - now.getTime());
		}
	}

	private final DataSource dataSource;
	private final UiEventBus eventBus;
	private final ReadOnlyStatusAccess statusAccess;
	private final CommonEventProcessor commonEventProcessor;
	private final VehicleNotificationEventProcessor vehicleNotificationEventProcessor;

	/**
	 * Nullオブジェクトパターン用のコンストラクタ
	 */
	public CommonLogic() {
		StatusAccess statusAccess = new StatusAccess();
		this.statusAccess = statusAccess.getReadOnlyStatusAccess();
		dataSource = DataSourceFactory.newInstance();
		eventBus = new UiEventBus();
		commonEventProcessor = new CommonEventProcessor(this, statusAccess);
		vehicleNotificationEventProcessor = new VehicleNotificationEventProcessor(
				this, statusAccess);

		dispose();
	}

	/**
	 * コンストラクタ
	 */
	public CommonLogic(Activity activity, Handler activityHandler,
			StatusAccess statusAccess) {
		commonEventProcessor = new CommonEventProcessor(this, statusAccess);
		vehicleNotificationEventProcessor = new VehicleNotificationEventProcessor(
				this, statusAccess);
		this.statusAccess = statusAccess.getReadOnlyStatusAccess();
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(activity);
		String url = preferences.getString(SharedPreferencesKey.SERVER_URL,
				WebAPIDataSource.DEFAULT_URL);
		String token = preferences.getString(
				SharedPreferencesKey.SERVER_IN_VEHICLE_DEVICE_TOKEN, "");
		File webAPIBackupFile = activity.getFileStreamPath("webapi.serialized");
		if (preferences.getBoolean(SharedPreferencesKey.CLEAR_WEBAPI_BACKUP,
				false)) {
			if (webAPIBackupFile.exists() && !webAPIBackupFile.delete()) {
				Log.w(TAG, "!\"" + webAPIBackupFile + "\".delete()");
			}
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(SharedPreferencesKey.CLEAR_WEBAPI_BACKUP, false);
			editor.commit();
		}

		dataSource = DataSourceFactory
				.newInstance(url, token, webAPIBackupFile);

		eventBus = new UiEventBus(activityHandler);
		for (Object object : new Object[] { activity, commonEventProcessor,
				vehicleNotificationEventProcessor }) {
			eventBus.register(object);
		}
		for (Integer resourceId : new Integer[] {
				R.id.departure_check_modal_view, R.id.schedule_modal_view,
				R.id.memo_modal_view, R.id.arrival_check_modal_view,
				R.id.return_path_modal_view, R.id.notification_modal_view,
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
		Closeables.closeQuietly(dataSource);
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

	public List<Reservation> getReservations() {
		return statusAccess.read(new Reader<List<Reservation>>() {
			@Override
			public List<Reservation> read(Status status) {
				return new LinkedList<Reservation>(status.reservations);
			}
		});
	}

	public ServiceUnitStatusLog getServiceUnitStatusLog() {
		return statusAccess.read(new Reader<ServiceUnitStatusLog>() {
			@Override
			public ServiceUnitStatusLog read(Status status) {
				return status.serviceUnitStatusLog;
			}
		});
	}

	public ReadOnlyStatusAccess getStatusAccess() {
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
