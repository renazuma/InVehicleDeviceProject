package com.kogasoftware.odt.invehicledevice.logic.datasource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebAPIDataSource implements DataSource {
	abstract static class WebAPICaller<T> {
		abstract public void call(WebAPICallback<T> wrappedCallback)
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
			WebAPICallback<OperationSchedule> callback) throws WebAPIException {
		return callWebAPISynchronously(new WebAPICaller<OperationSchedule>() {
			@Override
			public void call(WebAPICallback<OperationSchedule> wrappedCallback)
					throws WebAPIException, JSONException {
				api.arrivalOperationSchedule(os, wrappedCallback);
			}
		}, callback);
	}

	public <T> int callWebAPISynchronously(final WebAPICaller<T> caller,
			final WebAPICallback<T> extraCallback) throws WebAPIException {
		final AtomicInteger outputReqkey = new AtomicInteger(-1);
		final AtomicReference<Runnable> outputRunnable = new AtomicReference<Runnable>();
		final AtomicReference<WebAPIException> outputException = new AtomicReference<WebAPIException>();
		final CountDownLatch latch = new CountDownLatch(1);
		final WebAPICallback<T> synchronousCallback = new WebAPICallback<T>() {
			@Override
			public void onException(final int reqkey, final WebAPIException ex) {
				outputReqkey.set(reqkey);
				outputRunnable.set(new Runnable() {
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
				outputReqkey.set(reqkey);
				outputRunnable.set(new Runnable() {
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
				outputReqkey.set(reqkey);
				outputRunnable.set(new Runnable() {
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
					caller.call(synchronousCallback);
				} catch (JSONException e) {
					outputException.set(new WebAPIException(true, e));
				} catch (WebAPIException e) {
					outputException.set(e);
					latch.countDown();
				}
			}
		});

		if (!postResult) {
			return outputReqkey.get();
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new WebAPIException(true, e);
		}

		if (outputException.get() != null) {
			throw outputException.get();
		}

		if (outputRunnable.get() != null) {
			outputRunnable.get().run();
		}
		return outputReqkey.get();
	}

	public <T> int callWebAPISynchronusly(final WebAPICaller<T> caller)
			throws WebAPIException {
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
			WebAPICallback<OperationSchedule> callback) throws WebAPIException {
		return callWebAPISynchronously(new WebAPICaller<OperationSchedule>() {
			@Override
			public void call(WebAPICallback<OperationSchedule> wrappedCallback)
					throws WebAPIException, JSONException {
				api.departureOperationSchedule(os, wrappedCallback);
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
			WebAPICallback<PassengerRecord> callback) throws WebAPIException {
		return callWebAPISynchronously(new WebAPICaller<PassengerRecord>() {
			@Override
			public void call(WebAPICallback<PassengerRecord> wrappedCallback)
					throws WebAPIException, JSONException {
				api.getOffPassenger(operationSchedule, reservation,
						passengerRecord, wrappedCallback);
			}
		}, callback);
	}

	@Override
	public int getOnPassenger(final OperationSchedule operationSchedule,
			final Reservation reservation,
			final PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) throws WebAPIException {
		return callWebAPISynchronously(new WebAPICaller<PassengerRecord>() {
			@Override
			public void call(WebAPICallback<PassengerRecord> wrappedCallback)
					throws WebAPIException, JSONException {
				api.getOnPassenger(operationSchedule, reservation,
						passengerRecord, wrappedCallback);
			}
		}, callback);
	}

	@Override
	public List<OperationSchedule> getOperationSchedules()
			throws WebAPIException {
		final List<OperationSchedule> result = new LinkedList<OperationSchedule>();
		callWebAPISynchronusly(new WebAPICaller<List<OperationSchedule>>() {
			@Override
			public void call(
					WebAPICallback<List<OperationSchedule>> wrappedCallback)
					throws WebAPIException, JSONException {
				api.getOperationSchedules(wrappedCallback);
			}

			@Override
			public void onSucceed(final int reqkey, final int statusCode,
					final List<OperationSchedule> operationSchedules) {
				result.addAll(operationSchedules);
			}
		});
		if (result.isEmpty()) { // TODO
			throw new WebAPIException(true, "operation schedule not found");
		}
		return result;
	}

	@Override
	public List<VehicleNotification> getVehicleNotifications()
			throws WebAPIException {
		final List<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
		callWebAPISynchronusly(new WebAPICaller<List<VehicleNotification>>() {
			@Override
			public void call(WebAPICallback<List<VehicleNotification>> callback)
					throws WebAPIException {
				api.getVehicleNotifications(callback);
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

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}

		return new Reservation();
	}

	@Override
	public List<ReservationCandidate> postReservationCandidates(Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId)
			throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}

		DateFormat f = new SimpleDateFormat("HH:mm");
		List<ReservationCandidate> l = new LinkedList<ReservationCandidate>();
		try {
			ReservationCandidate c1 = new ReservationCandidate();
			c1.setDepartureTime(f.parse("12:35"));
			c1.setArrivalTime(f.parse("13:34"));
			Platform ap1 = new Platform();
			ap1.setName("駅1");
			Platform dp1 = new Platform();
			dp1.setName("駅2");
			c1.setArrivalPlatform(ap1);
			c1.setDeparturePlatform(dp1);
			l.add(c1);

			ReservationCandidate c2 = new ReservationCandidate();
			c2.setDepartureTime(f.parse("15:12"));
			c2.setArrivalTime(f.parse("16:45"));
			Platform ap2 = new Platform();
			ap2.setName("駅3");
			Platform dp2 = new Platform();
			dp2.setName("駅4");
			c2.setArrivalPlatform(ap2);
			c2.setDeparturePlatform(dp2);
			l.add(c2);

			ReservationCandidate c3 = new ReservationCandidate();
			c3.setDepartureTime(f.parse("17:39"));
			c3.setArrivalTime(f.parse("18:01"));
			Platform ap3 = new Platform();
			ap3.setName("駅5");
			Platform dp3 = new Platform();
			dp3.setName("駅6");
			c3.setArrivalPlatform(ap3);
			c3.setDeparturePlatform(dp3);
			l.add(c3);

			ReservationCandidate c4 = new ReservationCandidate();
			c4.setDepartureTime(f.parse("18:39"));
			c4.setArrivalTime(f.parse("18:41"));
			l.add(c4);

			ReservationCandidate c5 = new ReservationCandidate();
			c5.setDepartureTime(f.parse("19:01"));
			c5.setArrivalTime(f.parse("20:39"));
			l.add(c5);

			ReservationCandidate c6 = new ReservationCandidate();
			c6.setDepartureTime(f.parse("19:01"));
			c6.setArrivalTime(f.parse("20:39"));
			l.add(c6);

			ReservationCandidate c7 = new ReservationCandidate();
			c7.setDepartureTime(f.parse("19:01"));
			c7.setArrivalTime(f.parse("20:39"));
			l.add(c7);

			ReservationCandidate c8 = new ReservationCandidate();
			c8.setDepartureTime(f.parse("19:01"));
			c8.setArrivalTime(f.parse("20:39"));
			l.add(c8);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return l;
	}

	@Override
	public void putReservationTransferredAt(Integer id, Date transferredAt)
			throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}
	}

	@Override
	public void putVehicleNotificationReadAt(Integer id, Date readAt)
			throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}
	}

	@Override
	public int responseVehicleNotification(final VehicleNotification vn,
			final int response, WebAPICallback<VehicleNotification> callback)
			throws WebAPIException {
		return callWebAPISynchronously(new WebAPICaller<VehicleNotification>() {
			@Override
			public void call(WebAPICallback<VehicleNotification> wrappedCallback)
					throws WebAPIException, JSONException {
				api.responseVehicleNotification(vn, response, wrappedCallback);
			}
		}, callback);
	}

	@Override
	public int sendServiceUnitStatusLog(final ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback)
			throws WebAPIException, JSONException {
		return callWebAPISynchronously(
				new WebAPICaller<ServiceUnitStatusLog>() {
					@Override
					public void call(
							WebAPICallback<ServiceUnitStatusLog> wrappedCallback)
							throws WebAPIException, JSONException {
						api.sendServiceUnitStatusLog(log, wrappedCallback);
					}
				}, callback);
	}
}
