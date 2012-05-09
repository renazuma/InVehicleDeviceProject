package com.kogasoftware.odt.invehicledevice.logic.datasource;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONException;

import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebAPIDataSource implements DataSource {
	abstract static class WebAPICaller<T> {
		abstract public int call(WebAPICallback<T> wrappedCallback)
				throws WebAPIException, JSONException;

		public void onException(int reqkey, WebAPIException ex) {
		}

		public void onFailed(int reqkey, int statusCode, String response) {
		}

		public void onSucceed(int reqkey, int statusCode, T result) {
		}
	}

	public static final String DEFAULT_URL = "http://127.0.0.1";

	private final Handler uiHandler = new Handler(Looper.getMainLooper());
	private final WebAPI api;

	public WebAPIDataSource(String url, String token) {
		api = new WebAPI(url, token);
	}

	@Override
	public int arrivalOperationSchedule(final OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		return callWebAPISynchronously(new WebAPICaller<OperationSchedule>() {
			@Override
			public int call(WebAPICallback<OperationSchedule> wrappedCallback)
					throws WebAPIException, JSONException {
				return api.arrivalOperationSchedule(os, wrappedCallback);
			}
		}, callback);
	}

	public <T> int callWebAPISynchronously(final WebAPICaller<T> caller,
			final WebAPICallback<T> extraCallback) {
		final AtomicInteger returnedReqkey = new AtomicInteger(-1);
		final AtomicReference<Runnable> outputRunnable = new AtomicReference<Runnable>(); // TODO:複数回コールバック仕様確認
		final AtomicReference<WebAPIException> outputException = new AtomicReference<WebAPIException>();
		final CountDownLatch latch = new CountDownLatch(1);
		final WebAPICallback<T> synchronousCallback = new WebAPICallback<T>() {
			@Override
			public void onException(final int reqkey, final WebAPIException ex) {
				if (returnedReqkey.get() != reqkey) {
					return;
				}
				outputRunnable.compareAndSet(null, new Runnable() {
					@Override
					public void run() {
						caller.onException(reqkey, ex);
						extraCallback.onException(reqkey, ex);
					}
				});
				latch.countDown();
			}

			@Override
			public void onFailed(final int reqkey, final int statusCode,
					final String response) {
				if (returnedReqkey.get() != reqkey) {
					return;
				}
				outputRunnable.compareAndSet(null, new Runnable() {
					@Override
					public void run() {
						caller.onFailed(reqkey, statusCode, response);
						extraCallback.onFailed(reqkey, statusCode, response);
					}
				});
				latch.countDown();
			}

			@Override
			public void onSucceed(final int reqkey, final int statusCode,
					final T result) {
				if (returnedReqkey.get() != reqkey) {
					return;
				}
				outputRunnable.compareAndSet(null, new Runnable() {
					@Override
					public void run() {
						caller.onSucceed(reqkey, statusCode, result);
						extraCallback.onSucceed(reqkey, statusCode, result);
					}
				});
				latch.countDown();
			}
		};
		Boolean postResult = uiHandler.post(new Runnable() {
			@Override
			public void run() {
				try {
					returnedReqkey.set(caller.call(synchronousCallback));
				} catch (JSONException e) {
					outputException.set(new WebAPIException(true, e));
					latch.countDown();
				} catch (WebAPIException e) {
					outputException.set(e);
					latch.countDown();
				}
			}
		});

		if (!postResult) {
			return returnedReqkey.get();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			synchronousCallback.onException(returnedReqkey.get(),
					new WebAPIException(false, e));
			Thread.currentThread().interrupt();
			return returnedReqkey.get();
		}

		if (outputException.get() != null) {
			synchronousCallback.onException(returnedReqkey.get(),
					new WebAPIException(false, outputException.get()));
			return returnedReqkey.get();
		}

		if (outputRunnable.get() != null) {
			outputRunnable.get().run();
		}
		return returnedReqkey.get();
	}

	public <T> int callWebAPISynchronusly(final WebAPICaller<T> caller) {
		return callWebAPISynchronously(caller, new WebAPICallback<T>() {
			@Override
			public void onException(int reqkey, WebAPIException ex) {
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
			}

			@Override
			public void onSucceed(int reqkey, int statusCode, T result) {
			}
		});
	}

	@Override
	public int departureOperationSchedule(final OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		return callWebAPISynchronously(new WebAPICaller<OperationSchedule>() {
			@Override
			public int call(WebAPICallback<OperationSchedule> wrappedCallback)
					throws WebAPIException, JSONException {
				return api.departureOperationSchedule(os, wrappedCallback);
			}
		}, callback);
	}

	@Override
	public InVehicleDevice getInVehicleDevice() throws WebAPIException {
		InVehicleDevice model = new InVehicleDevice();
		model.setId(10);
		model.setTypeNumber("TESTNUMBER012345");
		model.setModelName("MODELNAME67890");
		return model;
	}

	@Override
	public int getOffPassenger(final OperationSchedule operationSchedule,
			final Reservation reservation,
			final PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		return callWebAPISynchronously(new WebAPICaller<PassengerRecord>() {
			@Override
			public int call(WebAPICallback<PassengerRecord> wrappedCallback)
					throws WebAPIException, JSONException {
				return api.getOffPassenger(operationSchedule, reservation,
						passengerRecord, wrappedCallback);
			}
		}, callback);
	}

	@Override
	public int getOnPassenger(final OperationSchedule operationSchedule,
			final Reservation reservation,
			final PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		return callWebAPISynchronously(new WebAPICaller<PassengerRecord>() {
			@Override
			public int call(WebAPICallback<PassengerRecord> wrappedCallback)
					throws WebAPIException, JSONException {
				return api.getOnPassenger(operationSchedule, reservation,
						passengerRecord, wrappedCallback);
			}
		}, callback);
	}

	@Override
	public List<OperationSchedule> getOperationSchedules() {
		final List<OperationSchedule> result = new LinkedList<OperationSchedule>();
		callWebAPISynchronusly(new WebAPICaller<List<OperationSchedule>>() {
			@Override
			public int call(
					WebAPICallback<List<OperationSchedule>> wrappedCallback)
					throws WebAPIException, JSONException {
				return api.getOperationSchedules(wrappedCallback);
			}

			@Override
			public void onSucceed(final int reqkey, final int statusCode,
					final List<OperationSchedule> operationSchedules) {
				result.addAll(operationSchedules);
			}
		});
		return result;
	}

	@Override
	public List<VehicleNotification> getVehicleNotifications() {
		final List<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
		callWebAPISynchronusly(new WebAPICaller<List<VehicleNotification>>() {
			@Override
			public int call(WebAPICallback<List<VehicleNotification>> callback)
					throws WebAPIException {
				return api.getVehicleNotifications(callback);
			}

			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				vehicleNotifications.addAll(result);
			}
		});
		return vehicleNotifications;
	}

	@Override
	public Reservation postReservation(Integer reservationCandidateId)
			throws WebAPIException {
		throw new WebAPIException(false, "deprecated");
	}

	@Override
	public List<ReservationCandidate> postReservationCandidates(Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId)
			throws WebAPIException {
		throw new WebAPIException(false, "deprecated");
	}

	@Override
	public void putReservationTransferredAt(Integer id, Date transferredAt)
			throws WebAPIException {
		throw new WebAPIException(false, "deprecated");
	}

	@Override
	public void putVehicleNotificationReadAt(Integer id, Date readAt)
			throws WebAPIException {
		throw new WebAPIException(false, "deprecated");
	}

	@Override
	public int responseVehicleNotification(final VehicleNotification vn,
			final int response, WebAPICallback<VehicleNotification> callback) {
		return callWebAPISynchronously(new WebAPICaller<VehicleNotification>() {
			@Override
			public int call(WebAPICallback<VehicleNotification> wrappedCallback)
					throws WebAPIException, JSONException {
				return api.responseVehicleNotification(vn, response,
						wrappedCallback);
			}
		}, callback);
	}

	@Override
	public int sendServiceUnitStatusLog(final ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback) {
		return callWebAPISynchronously(
				new WebAPICaller<ServiceUnitStatusLog>() {
					@Override
					public int call(
							WebAPICallback<ServiceUnitStatusLog> wrappedCallback)
							throws WebAPIException, JSONException {
						return api.sendServiceUnitStatusLog(log,
								wrappedCallback);
					}
				}, callback);
	}
}
