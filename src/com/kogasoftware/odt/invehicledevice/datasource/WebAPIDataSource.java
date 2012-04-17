package com.kogasoftware.odt.invehicledevice.datasource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;

import org.json.JSONException;

import android.os.Handler;
import android.os.Looper;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
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

	private final Handler uiHandler = new Handler(Looper.getMainLooper());
	private final WebAPI api;

	public WebAPIDataSource(String url, String token) {
		api = new WebAPI(token);
	}

	public <T> void callWebAPISynchronously(final WebAPICaller<T> caller,
			final WebAPICallback<T> extraCallback) throws WebAPIException {
		final Set<Runnable> mutableRunnable = new CopyOnWriteArraySet<Runnable>();
		final Set<WebAPIException> mutableException = new CopyOnWriteArraySet<WebAPIException>();
		final CountDownLatch latch = new CountDownLatch(1);
		final WebAPICallback<T> synchronousCallback = new WebAPICallback<T>() {
			@Override
			public void onException(final int reqkey, final WebAPIException ex) {
				mutableRunnable.add(new Runnable() {
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
				mutableRunnable.add(new Runnable() {
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
				mutableRunnable.add(new Runnable() {
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
					mutableException.add(new WebAPIException(true, e));
				} catch (WebAPIException e) {
					mutableException.add(e);
					latch.countDown();
				}
			}
		});

		if (!postResult) {
			return;
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new WebAPIException(true, e);
		}

		if (!mutableException.isEmpty()) {
			throw mutableException.iterator().next();
		}

		if (!mutableRunnable.isEmpty()) {
			mutableRunnable.iterator().next().run();
		}
	}

	public <T> void callWebAPISynchronusly(final WebAPICaller<T> caller)
			throws WebAPIException {
		callWebAPISynchronously(caller, new WebAPICallback<T>() {
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
	public InVehicleDevice getInVehicleDevice() throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}

		InVehicleDevice model = new InVehicleDevice();
		model.setId(10);
		model.setTypeNumber("TESTNUMBER012345");
		model.setModelName("MODELNAME67890");
		return model;
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
	public void responseVehicleNotification(final VehicleNotification vn,
			final int response,
			final WebAPICallback<VehicleNotification> callback)
			throws WebAPIException {
		callWebAPISynchronously(new WebAPICaller<VehicleNotification>() {
			@Override
			public void call(WebAPICallback<VehicleNotification> wrappedCallback)
					throws WebAPIException, JSONException {
				api.responseVehicleNotification(vn, response, wrappedCallback);
			}
		}, callback);
	}
}