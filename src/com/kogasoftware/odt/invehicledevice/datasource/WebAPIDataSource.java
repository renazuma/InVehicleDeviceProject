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
import org.json.JSONObject;

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

	private Date nextNotifyDate = new Date(new Date().getTime() + 60 * 1000);

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
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}

		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			String u1 = "user: {id: 1, last_name: '河原', first_name: '恭三郎'}";
			String u2 = "user: {id: 2, last_name: '滝口', first_name: '遥奈'}";
			String u3 = "user: {id: 3, last_name: '下村', first_name: '誠一'}";
			String u4 = "user: {id: 4, last_name: '木本', first_name: '麻紀'}";
			String u5 = "user: {id: 5, last_name: '永瀬', first_name: '直治'}";
			String u6 = "user: {id: 6, last_name: '田川', first_name: '恭三郎'}";

			JSONObject j1 = new JSONObject(
					"{"
							+ "id: 1, "
							+ "arrival_estimate: '2012-01-01T01:00:00+09:00', "
							+ "departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: 'コガソフトウェア前', name_ruby: 'こがそふとうぇあまえ'}, "
							+ "reservations_as_arrival: ["
							+ "  {id: 1, passenger_count: 5, memo: 'テストメモ1', "
							+ u1 + "}, " + "  {id: 2, passenger_count: 6, "
							+ u2 + "}, " + "  {id: 3, passenger_count: 7, "
							+ u3 + "}]," + "reservations_as_departure: ["
							+ "  {id: 4, passenger_count: 15, " + u4 + "}, "
							+ "  {id: 5, passenger_count: 16, " + u5 + "}, "
							+ "  {id: 6, passenger_count: 17, " + u6 + "}]"
							+ "}");
			l.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject(
					"{"
							+ "id: 2, "
							+ "arrival_estimate: '2012-01-01T01:00:00+09:00', "
							+ "departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '上野御徒町駅前', name_ruby: 'うえのおかちまちえきまえ'}, "
							+ "reservations_as_arrival: ["
							+ "  {id: 7, passenger_count: 5, memo: 'テストメモ1', "
							+ u1 + "}, " + "  {id: 8, passenger_count: 6, "
							+ u2 + "}, " + "  {id: 9, passenger_count: 7, "
							+ u3 + "}]," + "reservations_as_departure: ["
							+ "  {id: 10, passenger_count: 15, " + u4 + "}, "
							+ "  {id: 11, passenger_count: 16, " + u5 + "}, "
							+ "  {id: 12, passenger_count: 17, " + u6 + "}]"
							+ "}");
			l.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject("{" + "id: 3, "
					+ "arrival_estimate: '2012-01-01T01:00:00+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00+09:00', "
					+ "platform: {name: '上野広小路前', name_ruby: 'うえのひろこうじまえ'}, "
					+ "reservations_as_arrival: ["
					+ "  {id: 13, passenger_count: 5, memo: 'テストメモ1', " + u1
					+ "}, " + "  {id: 14, passenger_count: 6, " + u2 + "}, "
					+ "  {id: 15, passenger_count: 7, " + u3 + "}],"
					+ "reservations_as_departure: ["
					+ "  {id: 16, passenger_count: 15, " + u4 + "}, "
					+ "  {id: 17, passenger_count: 16, " + u5 + "}, "
					+ "  {id: 18, passenger_count: 17, " + u6 + "}]" + "}");
			l.add(new OperationSchedule(j3));

			JSONObject j4 = new JSONObject("{" + "id: 4, "

			+ "arrival_estimate: '2012-01-01T01:00:00+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00+09:00', "
					+ "platform: {name: 'ヨドバシアキバ前', name_ruby: 'よどばしあきばまえ'}, "
					+ "reservations_as_arrival: ["
					+ "  {id: 19, passenger_count: 5, memo: 'テストメモ1', " + u1
					+ "}, " + "  {id: 20, passenger_count: 6, " + u2 + "}, "
					+ "  {id: 21, passenger_count: 7, " + u3 + "}],"
					+ "reservations_as_departure: ["
					+ "  {id: 22, passenger_count: 15, " + u4 + "}, "
					+ "  {id: 23, passenger_count: 16, " + u5 + "}, "
					+ "  {id: 24, passenger_count: 17, " + u6 + "}]" + "}");
			l.add(new OperationSchedule(j4));

			JSONObject j5 = new JSONObject("{" + "id: 5, "

			+ "arrival_estimate: '2012-01-01T01:00:00+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00+09:00', "
					+ "platform: {name: '上野動物園前', name_ruby: 'うえのどうぶつえんまえ'}, "
					+ "reservations_as_arrival: ["
					+ "  {id: 25, passenger_count: 5, memo: 'テストメモ1', " + u1
					+ "}, " + "  {id: 26, passenger_count: 6, " + u2 + "}, "
					+ "  {id: 100, passenger_count: 7, " + u3 + "}],"
					+ "reservations_as_departure: ["
					+ "  {id: 101, passenger_count: 15, " + u4 + "}, "
					+ "  {id: 102, passenger_count: 16, " + u5 + "}, "
					+ "  {id: 103, passenger_count: 17, " + u6 + "}]" + "}");
			l.add(new OperationSchedule(j5));

			JSONObject j6 = new JSONObject(
					"{"
							+ "id: 6, "
							+ "arrival_estimate: '2012-01-01T01:00:00+09:00', "
							+ "departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '国立科学博物館前', name_ruby: 'こくりつかがくはくぶつかんまえ'}, "
							+ "reservations_as_arrival: ["
							+ "  {id: 104, passenger_count: 5, memo: 'テストメモ1', "
							+ u1 + "}, " + "  {id: 200, passenger_count: 6, "
							+ u2 + "}, " + "  {id: 300, passenger_count: 7, "
							+ u3 + "}]," + "reservations_as_departure: ["
							+ "  {id: 400, passenger_count: 15, " + u4 + "}, "
							+ "  {id: 500, passenger_count: 16, " + u5 + "}, "
							+ "  {id: 600, passenger_count: 17, " + u6 + "}]"
							+ "}");
			l.add(new OperationSchedule(j6));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return l;
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
			final int response, WebAPICallback<VehicleNotification> callback)
			throws WebAPIException {
		callWebAPISynchronusly(new WebAPICaller<VehicleNotification>() {
			@Override
			public void call(WebAPICallback<VehicleNotification> wrappedCallback)
					throws WebAPIException, JSONException {
				api.responseVehicleNotification(vn, response, wrappedCallback);
			}
		});
	}
}
