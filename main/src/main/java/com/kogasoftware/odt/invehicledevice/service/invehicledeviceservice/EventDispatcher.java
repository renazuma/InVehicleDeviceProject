package com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice;

import java.io.Closeable;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.Operation;

public class EventDispatcher implements Closeable {
	private static final String TAG = EventDispatcher.class.getSimpleName();
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final AtomicBoolean exited = new AtomicBoolean(false);

	protected static <T> Multimap<Handler, T> newListenerMultimap() {
		return LinkedHashMultimap.create();
	}

	public interface OnAlertUpdatedOperationScheduleListener {
		void onAlertUpdatedOperationSchedule();
	}

	public interface OnAlertVehicleNotificationReceiveListener {
		void onAlertVehicleNotificationReceive(
				List<VehicleNotification> vehicleNotifications);
	}

	public interface OnChangeLocationListener {
		void onChangeLocation(Optional<Location> location,
				Optional<Integer> satelliteCount);
	}

	public interface OnChangeOrientationListener {
		void onChangeOrientation(Double orientationDegree);
	}

	public interface OnChangeSignalStrengthListener {
		void onChangeSignalStrength(Integer signalStrengthPercentage);
	}

	public interface OnChangeTemperatureListener {
		void onChangeTemperature(Double celciusTemperature);
	}

	public interface OnOperationScheduleReceiveFailListener {
		void onOperationScheduleReceiveFail();
	}

	public interface OnUpdateOperationListener {
		void onUpdateOperation(Operation operation);
	}

	public interface OnExitListener {
		void onExit();
	}

	public interface OnMergeOperationSchedulesListener {
		void onMergeOperationSchedules(
				List<VehicleNotification> triggerVehicleNotifications);
	}

	public interface OnPauseActivityListener {
		void onPauseActivity();
	}

	public interface OnResumeActivityListener {
		void onResumeActivity();
	}

	public interface OnStartNewOperationListener {
		void onStartNewOperation();
	}

	public interface OnStartReceiveUpdatedOperationScheduleListener {
		void onStartReceiveUpdatedOperationSchedule();
	}

	protected final Multimap<Handler, OnAlertUpdatedOperationScheduleListener> onAlertUpdatedOperationScheduleListeners = newListenerMultimap();
	protected final Multimap<Handler, OnAlertVehicleNotificationReceiveListener> onAlertVehicleNotificationReceiveListeners = newListenerMultimap();
	protected final Multimap<Handler, OnChangeLocationListener> onChangeLocationListeners = newListenerMultimap();
	protected final Multimap<Handler, OnChangeOrientationListener> onChangeOrientationListeners = newListenerMultimap();
	protected final Multimap<Handler, OnChangeSignalStrengthListener> onChangeSignalStrengthListeners = newListenerMultimap();
	protected final Multimap<Handler, OnChangeTemperatureListener> onChangeTemperatureListeners = newListenerMultimap();
	protected final Multimap<Handler, OnExitListener> onExitListeners = newListenerMultimap();
	protected final Multimap<Handler, OnMergeOperationSchedulesListener> onMergeOperationSchedulesListeners = newListenerMultimap();
	protected final Multimap<Handler, OnStartNewOperationListener> onStartNewOperationListeners = newListenerMultimap();
	protected final Multimap<Handler, OnStartReceiveUpdatedOperationScheduleListener> onStartReceiveUpdatedOperationScheduleListeners = newListenerMultimap();
	protected final Multimap<Handler, OnPauseActivityListener> onPauseActivityListeners = newListenerMultimap();
	protected final Multimap<Handler, OnResumeActivityListener> onResumeActivityListeners = newListenerMultimap();
	protected final Multimap<Handler, OnOperationScheduleReceiveFailListener> onOperationScheduleReceiveFailListeners = newListenerMultimap();
	protected final Multimap<Handler, OnUpdateOperationListener> onUpdateOperationListeners = newListenerMultimap();

	private static interface Dispatcher<T> {
		void dispatch(T listener);
	}

	protected <T> void dispatchListener(final Multimap<Handler, T> rawMultimap,
			final Dispatcher<T> dispatcher) {
		Multimap<Handler, T> multimap = LinkedHashMultimap.create();
		synchronized (rawMultimap) {
			multimap.putAll(rawMultimap);
		}		
		for (final Entry<Handler, T> entry : multimap.entries()) {
			Handler handler = entry.getKey();
			final T listener = entry.getValue();
			handler.post(new Runnable() {
				@Override
				public void run() {
					synchronized (rawMultimap) {
						if (!rawMultimap.containsEntry(entry.getKey(),
								entry.getValue())) {
							return;
						}
					}
					dispatcher.dispatch(listener);
				}
			});
		}
	}

	public <T> void putListener(Multimap<Handler, T> multimap, T listener) {
		if (closed.get()) {
			Log.e(TAG, "\"" + this + "\" already closed listener=" + listener);
			return;
		}
		Handler handler = InVehicleDeviceService.getThreadHandler();
		synchronized (multimap) {
			multimap.put(handler, listener);
		}
	}

	public <T> void removeListener(Multimap<Handler, T> multimap, T listener) {
		synchronized (multimap) {
			if (!multimap.remove(InVehicleDeviceService.getThreadHandler(),
					listener)) {
				multimap.remove(InVehicleDeviceService.DEFAULT_HANDLER,
						listener);
			}
		}
	}

	public <T> void clearListener(Multimap<Handler, T> multimap) {
		synchronized (multimap) {
			multimap.clear();
		}
	}

	public void addOnAlertUpdatedOperationScheduleListener(
			OnAlertUpdatedOperationScheduleListener listener) {
		putListener(onAlertUpdatedOperationScheduleListeners, listener);
	}

	public void addOnUpdateOperationListener(OnUpdateOperationListener listener) {
		putListener(onUpdateOperationListeners, listener);
	}

	public void addOnAlertVehicleNotificationReceiveListener(
			OnAlertVehicleNotificationReceiveListener listener) {
		putListener(onAlertVehicleNotificationReceiveListeners, listener);
	}

	public void addOnChangeLocationListener(OnChangeLocationListener listener) {
		putListener(onChangeLocationListeners, listener);
	}

	public void addOnChangeOrientationListener(
			OnChangeOrientationListener listener) {
		putListener(onChangeOrientationListeners, listener);
	}

	public void addOnChangeSignalStrengthListener(
			OnChangeSignalStrengthListener listener) {
		putListener(onChangeSignalStrengthListeners, listener);
	}

	public void addOnChangeTemperatureListener(
			OnChangeTemperatureListener listener) {
		putListener(onChangeTemperatureListeners, listener);
	}

	public void addOnExitListener(final OnExitListener listener) {
		// 既にcloseされているか、exitされている場合は、登録せずにonExitを実行する
		if (closed.get() || exited.get()) {
			Log.w(TAG, "\"" + this + "\" already "
					+ (closed.get() ? "closed" : "exited") + " listener="
					+ listener + " call onExit() immediately");
			InVehicleDeviceService.getThreadHandler().post(new Runnable() {
				@Override
				public void run() {
					listener.onExit();
				}
			});
			return;
		}
		putListener(onExitListeners, listener);
	}

	public void addOnMergeOperationSchedulesListener(
			OnMergeOperationSchedulesListener listener) {
		putListener(onMergeOperationSchedulesListeners, listener);
	}

	public void addOnPauseActivityListener(OnPauseActivityListener listener) {
		putListener(onPauseActivityListeners, listener);
	}

	public void addOnResumeActivityListener(OnResumeActivityListener listener) {
		putListener(onResumeActivityListeners, listener);
	}

	public void addOnStartNewOperationListener(
			OnStartNewOperationListener listener) {
		putListener(onStartNewOperationListeners, listener);
	}

	public void addOnStartReceiveUpdatedOperationScheduleListener(
			OnStartReceiveUpdatedOperationScheduleListener listener) {
		putListener(onStartReceiveUpdatedOperationScheduleListeners, listener);
	}

	public void addOnOperationScheduleReceiveFailListener(
			OnOperationScheduleReceiveFailListener listener) {
		putListener(onOperationScheduleReceiveFailListeners, listener);
	}

	public void removeOnAlertUpdatedOperationScheduleListener(
			OnAlertUpdatedOperationScheduleListener listener) {
		removeListener(onAlertUpdatedOperationScheduleListeners, listener);
	}

	public void removeOnUpdateOperationListener(OnUpdateOperationListener listener) {
		removeListener(onUpdateOperationListeners, listener);
	}

	public void removeOnAlertVehicleNotificationReceiveListener(
			OnAlertVehicleNotificationReceiveListener listener) {
		removeListener(onAlertVehicleNotificationReceiveListeners, listener);
	}

	public void removeOnChangeLocationListener(OnChangeLocationListener listener) {
		removeListener(onChangeLocationListeners, listener);
	}

	public void removeOnChangeOrientationListener(
			OnChangeOrientationListener listener) {
		removeListener(onChangeOrientationListeners, listener);
	}

	public void removeOnChangeSignalStrengthListener(
			OnChangeSignalStrengthListener listener) {
		removeListener(onChangeSignalStrengthListeners, listener);
	}

	public void removeOnChangeTemperatureListener(
			OnChangeTemperatureListener listener) {
		removeListener(onChangeTemperatureListeners, listener);
	}

	public void removeOnExitListener(OnExitListener listener) {
		removeListener(onExitListeners, listener);
	}

	public void removeOnMergeOperationSchedulesListener(
			OnMergeOperationSchedulesListener listener) {
		removeListener(onMergeOperationSchedulesListeners, listener);
	}

	public void removeOnPauseActivityListener(OnPauseActivityListener listener) {
		removeListener(onPauseActivityListeners, listener);
	}

	public void removeOnResumeActivityListener(OnResumeActivityListener listener) {
		removeListener(onResumeActivityListeners, listener);
	}

	public void removeOnStartNewOperationListener(
			OnStartNewOperationListener listener) {
		removeListener(onStartNewOperationListeners, listener);
	}

	public void removeOnStartReceiveUpdatedOperationScheduleListener(
			OnStartReceiveUpdatedOperationScheduleListener listener) {
		removeListener(onStartReceiveUpdatedOperationScheduleListeners,
				listener);
	}

	public void removeOnOperationScheduleReceiveFailListener(
			OnOperationScheduleReceiveFailListener listener) {
		removeListener(onOperationScheduleReceiveFailListeners, listener);
	}

	public void dispatchUpdateOperation(final Operation operation) {
		StringBuilder message = new StringBuilder("dispatchUpdateOperation phase="
				+ operation.getPhase());
		for (OperationSchedule os : OperationSchedule.getCurrent(
				operation.operationSchedules).asSet()) {
			message.append(" currentOperationScheduleId=" + os.getId());
		}

		Log.i(TAG, message.toString());
		dispatchListener(onUpdateOperationListeners,
				new Dispatcher<OnUpdateOperationListener>() {
					@Override
					public void dispatch(OnUpdateOperationListener listener) {
						listener.onUpdateOperation(operation);
					}
				});
	}

	public void dispatchAlertUpdatedOperationSchedule() {
		dispatchListener(onAlertUpdatedOperationScheduleListeners,
				new Dispatcher<OnAlertUpdatedOperationScheduleListener>() {
					@Override
					public void dispatch(
							OnAlertUpdatedOperationScheduleListener listener) {
						listener.onAlertUpdatedOperationSchedule();
					}
				});
	}

	public void dispatchAlertVehicleNotificationReceive(
			final List<VehicleNotification> vehicleNotifications) {
		dispatchListener(onAlertVehicleNotificationReceiveListeners,
				new Dispatcher<OnAlertVehicleNotificationReceiveListener>() {
					@Override
					public void dispatch(
							OnAlertVehicleNotificationReceiveListener listener) {
						listener.onAlertVehicleNotificationReceive(vehicleNotifications);
					}
				});
	}

	public void dispatchChangeLocation(final Optional<Location> location,
			final Optional<Integer> satellitesCount) {
		dispatchListener(onChangeLocationListeners,
				new Dispatcher<OnChangeLocationListener>() {
					@Override
					public void dispatch(OnChangeLocationListener listener) {
						listener.onChangeLocation(location, satellitesCount);
					}
				});
	}

	public void dispatchChangeOrientation(final Double orientationDegree) {
		dispatchListener(onChangeOrientationListeners,
				new Dispatcher<OnChangeOrientationListener>() {
					@Override
					public void dispatch(OnChangeOrientationListener listener) {
						listener.onChangeOrientation(orientationDegree);
					}
				});
	}

	public void dispatchChangeSignalStrength(
			final Integer signalStrengthPercentage) {
		dispatchListener(onChangeSignalStrengthListeners,
				new Dispatcher<OnChangeSignalStrengthListener>() {
					@Override
					public void dispatch(OnChangeSignalStrengthListener listener) {
						listener.onChangeSignalStrength(signalStrengthPercentage);
					}
				});
	}

	public void dispatchChangeTemperature(final Double celciusTemperature) {
		dispatchListener(onChangeTemperatureListeners,
				new Dispatcher<OnChangeTemperatureListener>() {
					@Override
					public void dispatch(OnChangeTemperatureListener listener) {
						listener.onChangeTemperature(celciusTemperature);
					}
				});
	}

	public void dispatchMergeOperationSchedules(
			final List<OperationSchedule> operationSchedules,
			final List<VehicleNotification> triggerVehicleNotifications) {
		dispatchListener(onMergeOperationSchedulesListeners,
				new Dispatcher<OnMergeOperationSchedulesListener>() {
					@Override
					public void dispatch(
							OnMergeOperationSchedulesListener listener) {
						listener.onMergeOperationSchedules(triggerVehicleNotifications);
					}
				});
	}

	public void dispatchStartNewOperation() {
		dispatchListener(onStartNewOperationListeners,
				new Dispatcher<OnStartNewOperationListener>() {
					@Override
					public void dispatch(OnStartNewOperationListener listener) {
						listener.onStartNewOperation();
					}
				});
	}

	public void dispatchStartReceiveUpdatedOperationSchedule() {
		dispatchListener(
				onStartReceiveUpdatedOperationScheduleListeners,
				new Dispatcher<OnStartReceiveUpdatedOperationScheduleListener>() {
					@Override
					public void dispatch(
							OnStartReceiveUpdatedOperationScheduleListener listener) {
						listener.onStartReceiveUpdatedOperationSchedule();
					}
				});
	}

	public void dispatchOperationScheduleReceiveFail() {
		dispatchListener(onOperationScheduleReceiveFailListeners,
				new Dispatcher<OnOperationScheduleReceiveFailListener>() {
					@Override
					public void dispatch(
							OnOperationScheduleReceiveFailListener listener) {
						listener.onOperationScheduleReceiveFail();
					}
				});
	}

	public void dispatchPauseActivity() {
		dispatchListener(onPauseActivityListeners,
				new Dispatcher<OnPauseActivityListener>() {
					@Override
					public void dispatch(OnPauseActivityListener listener) {
						listener.onPauseActivity();
					}
				});
	}

	public void dispatchResumeActivity() {
		dispatchListener(onResumeActivityListeners,
				new Dispatcher<OnResumeActivityListener>() {
					@Override
					public void dispatch(OnResumeActivityListener listener) {
						listener.onResumeActivity();
					}
				});
	}

	public void dispatchExit() {
		exited.set(true);
		dispatchListener(onExitListeners, new Dispatcher<OnExitListener>() {
			@Override
			public void dispatch(OnExitListener listener) {
				listener.onExit();
			}
		});
	}

	@Override
	public void close() {
		clearListener(onAlertUpdatedOperationScheduleListeners);
		clearListener(onAlertVehicleNotificationReceiveListeners);
		clearListener(onChangeLocationListeners);
		clearListener(onChangeOrientationListeners);
		clearListener(onChangeSignalStrengthListeners);
		clearListener(onChangeTemperatureListeners);
		clearListener(onExitListeners);
		clearListener(onMergeOperationSchedulesListeners);
		clearListener(onStartNewOperationListeners);
		clearListener(onStartReceiveUpdatedOperationScheduleListeners);
		clearListener(onPauseActivityListeners);
		clearListener(onResumeActivityListeners);
		clearListener(onOperationScheduleReceiveFailListeners);
		clearListener(onUpdateOperationListeners);
	}
}
