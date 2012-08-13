package com.kogasoftware.odt.webapi.test;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import android.test.AndroidTestCase;
import android.util.Log;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.WebAPIRequest;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationRecord;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.UnitAssignment;
import com.kogasoftware.odt.webapi.model.User;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webtestapi.GenerateMaster;
import com.kogasoftware.odt.webtestapi.GenerateRecord;
import com.kogasoftware.odt.webtestapi.SyncCall;

public class WebAPITestCase extends AndroidTestCase {
	public static final String SERVER_HOST = "http://10.0.2.2:3334";
	public static final String TEST_SERVER_HOST = "http://10.0.2.2:3333";
	private static final String TAG = WebAPITestCase.class.getSimpleName();

	class OfflineTestWebAPI extends WebAPI {
		public OfflineTestWebAPI(String serverHost) {
			super(serverHost);
		}

		public OfflineTestWebAPI(String serverHost, String authenticationToken) {
			super(serverHost, authenticationToken);
		}

		public OfflineTestWebAPI(String serverHost, String authenticationToken,
				File backupFile) {
			super(serverHost, authenticationToken, backupFile);
		}

		@Override
		protected boolean doHttpSessionAndCallback(WebAPIRequest<?> request) {
			if (offline) {
				request.onException(new WebAPIException("offline test"));
				return false;
			} else {
				return super.doHttpSessionAndCallback(request);
			}
		}
	}

	public static class EmptyWebAPICallback<T> implements WebAPICallback<T> {
		@Override
		public void onException(int reqkey, WebAPIException ex) {
		}

		@Override
		public void onFailed(int reqkey, int statusCode, String response) {
		}

		@Override
		public void onSucceed(int reqkey, int statusCode, T result) {
		}
	};

	WebAPI api;
	volatile boolean offline;
	CountDownLatch latch;
	CountDownLatch latch2;
	Semaphore semaphore;
	GenerateMaster master;
	GenerateRecord record;
	ServiceProvider serviceProvider;
	List<VehicleNotification> notifications;
	List<OperationSchedule> schedules;
	OperationSchedule schedule;
	List<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();
	ServiceUnitStatusLog serviceUnitStatusLog;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// マスタ生成
		master = new GenerateMaster(TEST_SERVER_HOST);
		master.cleanDatabase();
		serviceProvider = master.createServiceProvider();
		master.createDriver("もぎ", "しゅーまっは", "011");
		master.createVehicle("ちば90も 99-91", "F1");
		master.createInVehicleDevice();
		master.createOperator();

		record = new GenerateRecord(master);
		offline = false;
	}

	@Override
	protected void tearDown() throws Exception {
		Closeables.closeQuietly(api);
		super.tearDown();
	}

	public void testPasswordLogin() throws Exception {
		api = new WebAPI(SERVER_HOST);
		callTestPasswordLogin(false, true);
	}

	public void testPasswordLoginRetry() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST);
		callTestPasswordLogin(true, true);
	}

	public void testPasswordLoginFail() throws Exception {
		api = new WebAPI(SERVER_HOST);
		callTestPasswordLogin(false, false);
	}

	public void callTestPasswordLogin(boolean retryTest, boolean validAuth)
			throws Exception {
		semaphore = new Semaphore(0);

		InVehicleDevice ivd = new InVehicleDevice();
		ivd.setLogin("ivd1");
		ivd.setPassword("ivdpass");
		if (!validAuth) {
			ivd.setPassword(ivd.getPassword() + "x");
		}

		final AtomicBoolean succeed = new AtomicBoolean(false);

		if (retryTest) {
			offline = true;
		}

		api.login(ivd, new WebAPICallback<InVehicleDevice>() {
			@Override
			public void onSucceed(int reqkey, int statusCode,
					InVehicleDevice result) {
				succeed.set(true);
				semaphore.release();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				semaphore.release();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				semaphore.release();
			}
		});

		assertTrue(semaphore.tryAcquire(2000, TimeUnit.SECONDS));
		if (!retryTest) {
			if (validAuth) {
				assertTrue(succeed.get());
				assertNotNull(api.getAuthenticationToken());
				assertTrue(api.getAuthenticationToken().length() > 0);
			} else {
				assertFalse(succeed.get());
			}
		} else {
			assertFalse(succeed.get());
			semaphore.drainPermits();
			offline = false;
			Thread.sleep(10 * 1000);
			assertTrue(semaphore.tryAcquire(20, TimeUnit.SECONDS));
			assertTrue(succeed.get());
		}
	}

	public void testGetVehicleNotifications() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		latch = new CountDownLatch(1);
		notifications = null;

		UnitAssignment ua = record.createUnitAssignment("3号車");
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, new Date());
		record.createVehicleNotification("テスト通知メッセージ1です。");
		record.createVehicleNotification("テスト通知メッセージ2です。");

		api.getVehicleNotifications(new WebAPICallback<List<VehicleNotification>>() {
			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				notifications = result;
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});
		assertTrue(latch.await(100, TimeUnit.SECONDS));

		assertNotNull(notifications);
		assertEquals(2, notifications.size());
		assertEquals("テスト通知メッセージ2です。", notifications.get(1).getBody());
	}

	public void testResponseVehicleNotification() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		callTestResponseVehicleNotification(false);
	}

	public void testResponseVehicleNotificationOffline() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		callTestResponseVehicleNotification(true);
	}

	public void callTestResponseVehicleNotification(boolean offlineTest)
			throws Exception {
		latch = new CountDownLatch(1);
		notifications = null;

		UnitAssignment ua = record.createUnitAssignment("3号車");
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, new Date());

		record.createVehicleNotification("テスト通知メッセージ1です。");
		record.createVehicleNotification("テスト通知メッセージ2です。");

		api.getVehicleNotifications(new WebAPICallback<List<VehicleNotification>>() {
			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				notifications = result;
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});
		assertTrue(latch.await(100, TimeUnit.SECONDS));

		assertNotNull(notifications);
		assertEquals(2, notifications.size());

		if (offlineTest) {
			offline = true;
		}

		latch = new CountDownLatch(1);
		api.responseVehicleNotification(notifications.get(0), 1,
				new WebAPICallback<VehicleNotification>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							VehicleNotification result) {
						latch.countDown();
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
					}

					@Override
					public void onException(int reqkey, WebAPIException ex) {
					}
				});
		if (offlineTest) {
			assertFalse(latch.await(20, TimeUnit.SECONDS));
			offline = false;
		}
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		VehicleNotification serverNotification = new SyncCall<VehicleNotification>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getVehicleNotification(
						notifications.get(0).getId(), this);
			}
		}.getResult();
		assertNotNull(serverNotification);
		assertTrue(serverNotification.getReadAt().isPresent());
		if (offlineTest) {
			assertTrue(serverNotification.getOffline().or(false));
		} else {
			assertFalse(serverNotification.getOffline().or(false));
		}

		latch = new CountDownLatch(1);
		api.getVehicleNotifications(new WebAPICallback<List<VehicleNotification>>() {
			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				notifications = result;
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});
		assertTrue(latch.await(100, TimeUnit.SECONDS));

		assertNotNull(notifications);
		assertEquals(1, notifications.size());
		assertEquals("テスト通知メッセージ2です。", notifications.get(0).getBody());
	}

	public void testGetOperationSchedules() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		callTestGetOperationSchedules(false);
	}

	public void testGetOperationSchedulesOffline() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		callTestGetOperationSchedules(true);
	}

	public void callTestGetOperationSchedules(boolean offlineTest)
			throws Exception {
		latch = new CountDownLatch(1);
		schedules = null;

		Date now = new Date();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 22);
		Date dtDeparture1 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 40);
		Date dtArrival2 = cal.getTime();

		User user = master.createUser("login1", "もぎ", "けんた");
		UnitAssignment ua = record.createUnitAssignment("1号車");
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, cal.getTime());

		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		OperationSchedule os1 = record.createOperationSchedule(ua, p1, now);
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		OperationSchedule os2 = record.createOperationSchedule(ua, p2, now);

		Demand demand = record.createDemand(user, ua, p1, dtDeparture1, p2,
				dtArrival2, 0);
		Reservation r = record.createReservation(user, demand, ua, p1, os1,
				dtDeparture1, p2, os2, dtArrival2, 0);
		assertNotNull(r);
		assertNotNull(record.createReservationUser(r, user));

		api.getOperationSchedules(new WebAPICallback<List<OperationSchedule>>() {
			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<OperationSchedule> result) {
				schedules = result;
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});
		assertTrue(latch.await(100, TimeUnit.SECONDS));

		assertNotNull(schedules);
		assertEquals(2, schedules.size());

		assertEquals(1, schedules.get(0).getReservationsAsDeparture().get(0)
				.getFellowUsers().size());

		assertEquals(1, schedules.get(1).getReservationsAsArrival().get(0)
				.getFellowUsers().size());

		if (offlineTest) {
			offline = true;
		}

		latch = new CountDownLatch(1);
		api.departureOperationSchedule(schedules.get(0),
				new WebAPICallback<OperationSchedule>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							OperationSchedule result) {
						schedule = result;
						latch.countDown();
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
					}

					@Override
					public void onException(int reqkey, WebAPIException ex) {
					}
				});
		if (offlineTest) {
			assertFalse(latch.await(20, TimeUnit.SECONDS));
			offline = false;
		}
		assertTrue(latch.await(20, TimeUnit.SECONDS));

		assertTrue(schedule.getOperationRecord().isPresent());
		assertTrue(schedule.getOperationRecord().get().getDepartedAt()
				.isPresent());
		OperationRecord serverOperationRecord = new SyncCall<OperationRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getOperationRecord(
						schedule.getOperationRecord().get().getId(), this);
			}
		}.getResult();
		assertNotNull(serverOperationRecord);
		if (offlineTest) {
			assertTrue(serverOperationRecord.getDepartedAtOffline().or(false));
			assertTrue(schedule.getOperationRecord().get()
					.getDepartedAtOffline().or(false));
		} else {
			assertFalse(serverOperationRecord.getDepartedAtOffline().or(false));
			assertFalse(schedule.getOperationRecord().get()
					.getDepartedAtOffline().or(false));
		}

		if (offlineTest) {
			offline = true;
		}
		latch = new CountDownLatch(1);
		api.arrivalOperationSchedule(schedules.get(1),
				new WebAPICallback<OperationSchedule>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							OperationSchedule result) {
						schedule = result;
						latch.countDown();
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
					}

					@Override
					public void onException(int reqkey, WebAPIException ex) {
					}
				});
		if (offlineTest) {
			assertFalse(latch.await(20, TimeUnit.SECONDS));
			offline = false;
		}
		assertTrue(latch.await(20, TimeUnit.SECONDS));

		assertNotNull(schedule.getOperationRecord());
		serverOperationRecord = new SyncCall<OperationRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getOperationRecord(
						schedule.getOperationRecord().get().getId(), this);
			}
		}.getResult();
		assertNotNull(serverOperationRecord);
		assertTrue(serverOperationRecord.getArrivedAt().isPresent());
		if (offlineTest) {
			assertTrue(serverOperationRecord.getArrivedAtOffline().or(false));
			assertTrue(schedule.getOperationRecord().get()
					.getArrivedAtOffline().or(false));
		} else {
			assertFalse(serverOperationRecord.getArrivedAtOffline().or(false));
			assertFalse(schedule.getOperationRecord().get()
					.getArrivedAtOffline().or(false));
		}
	}

	public void testPassengerGetOnAndOffOffline() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		callTestPassengerGetOnAndOff(true);
	}

	public void testPassengerGetOnAndOff() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		callTestPassengerGetOnAndOff(false);
	}

	protected void callTestPassengerGetOnAndOff(boolean offlineTest)
			throws Exception {
		latch = new CountDownLatch(1);
		schedules = null;
		createTestOperationSchedules();

		assertNotNull(schedules);
		assertEquals(2, schedules.size());

		OperationSchedule os1 = schedules.get(0);
		OperationSchedule os2 = schedules.get(1);
		Reservation res = os1.getReservationsAsDeparture().get(0);
		User user = master.createUser("login2", "もぎ", "たろう");
		assertNotNull(record.createReservationUser(res, user));

		assertFalse(schedules.get(1).getReservationsAsArrival().get(0)
				.getFellowUsers().isEmpty());

		PassengerRecord prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		if (offlineTest) {
			offline = true;
		}
		api.getOnPassenger(os1, res, res.getFellowUsers().get(0), prec,
				new EmptyWebAPICallback<Void>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							Void result) {
						latch.countDown();
					}
				});
		if (offlineTest) {
			assertFalse(latch.await(20, TimeUnit.SECONDS));
			offline = false;
		}
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		PassengerRecord serverPassengerRecord = new SyncCall<PassengerRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getPassengerRecord(
						passengerRecords.get(0).getId(), this);
			}
		}.getResult();
		assertNotNull(serverPassengerRecord);
		assertTrue(serverPassengerRecord.getGetOnTime().isPresent());
		// TODO:決められた乗車降車場以外で乗降した場合の処理が実装されたらコメントを外す
		// assertEquals(os1.getId(), serverPassengerRecord
		//		.getDepartureOperationScheduleId().get());
		assertTrue(serverPassengerRecord.getGetOnTime().isPresent());
		assertTrue(serverPassengerRecord.isRiding());
		if (offlineTest) {
			assertTrue(serverPassengerRecord.getGetOnTimeOffline().or(false));
		} else {
			assertFalse(serverPassengerRecord.getGetOnTimeOffline().or(false));
		}

		latch = new CountDownLatch(1);
		api.departureOperationSchedule(schedules.get(0),
				new WebAPICallback<OperationSchedule>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							OperationSchedule result) {
						schedule = result;
						latch.countDown();
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						latch.countDown();
					}

					@Override
					public void onException(int reqkey, WebAPIException ex) {
						latch.countDown();
					}
				});
		assertTrue(latch.await(100, TimeUnit.SECONDS));

		assertNotNull(schedule.getOperationRecord());
		assertTrue(schedule.getOperationRecord().get().getDepartedAt()
				.isPresent());

		latch = new CountDownLatch(1);
		api.arrivalOperationSchedule(schedules.get(1),
				new WebAPICallback<OperationSchedule>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							OperationSchedule result) {
						schedule = result;
						latch.countDown();
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						latch.countDown();
					}

					@Override
					public void onException(int reqkey, WebAPIException ex) {
						latch.countDown();
					}
				});
		assertTrue(latch.await(100, TimeUnit.SECONDS));

		assertNotNull(schedule.getOperationRecord());
		assertTrue(schedule.getOperationRecord().get().getArrivedAt()
				.isPresent());

		prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		if (offlineTest) {
			offline = true;
		}
		api.getOffPassenger(os2, res, res.getFellowUsers().get(0), prec,
				new EmptyWebAPICallback<Void>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							Void result) {
						latch.countDown();
					}
				});
		if (offlineTest) {
			assertFalse(latch.await(20, TimeUnit.SECONDS));
			offline = false;
		}
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		serverPassengerRecord = new SyncCall<PassengerRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getPassengerRecord(
						passengerRecords.get(0).getId(), this);
			}
		}.getResult();
		assertNotNull(serverPassengerRecord);
		assertTrue(serverPassengerRecord.getGetOffTime().isPresent());
		assertEquals(os1.getId(), serverPassengerRecord
				.getDepartureOperationScheduleId().get());
		assertEquals(os2.getId(), serverPassengerRecord
				.getArrivalOperationScheduleId().get());
		assertTrue(serverPassengerRecord.getGetOffTime().isPresent());
		assertTrue(serverPassengerRecord.isGotOff());

		if (offlineTest) {
			assertTrue(serverPassengerRecord.getGetOffTimeOffline().or(false));
		} else {
			assertFalse(serverPassengerRecord.getGetOffTimeOffline().or(false));
		}
	}

	public void testPassengerCancelGetOnAndOff() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		schedules = null;
		createTestOperationSchedules();

		assertNotNull(schedules);
		assertEquals(2, schedules.size());

		OperationSchedule os1 = schedules.get(0);
		OperationSchedule os2 = schedules.get(1);
		Reservation res = os1.getReservationsAsDeparture().get(0);

		assertFalse(schedules.get(1).getReservationsAsArrival().get(0)
				.getFellowUsers().isEmpty());

		PassengerRecord prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);

		api.getOnPassenger(os1, res, res.getFellowUsers().get(0), prec,
				new EmptyWebAPICallback<Void>());
		api.cancelGetOnPassenger(os1, res, res.getFellowUsers().get(0),
				new EmptyWebAPICallback<Void>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							Void result) {
						latch.countDown();
					}
				});

		assertTrue(latch.await(20, TimeUnit.SECONDS));
		PassengerRecord serverPassengerRecord = new SyncCall<PassengerRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getPassengerRecord(
						passengerRecords.get(0).getId(), this);
			}
		}.getResult();
		assertNotNull(serverPassengerRecord);
		assertFalse(serverPassengerRecord.getGetOnTime().isPresent());
		// TODO:決められた乗車降車場以外で乗降した場合の処理が実装されたらコメントを外す
		// assertFalse(serverPassengerRecord.getDepartureOperationScheduleId()
		//		.isPresent());
		assertTrue(serverPassengerRecord.isUnhandled());

		latch = new CountDownLatch(1);
		api.getOnPassenger(os1, res, res.getFellowUsers().get(0), prec,
				new EmptyWebAPICallback<Void>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							Void result) {
						latch.countDown();
					}
				});
		latch.await();

		latch = new CountDownLatch(1);
		api.departureOperationSchedule(schedules.get(0),
				new WebAPICallback<OperationSchedule>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							OperationSchedule result) {
						schedule = result;
						latch.countDown();
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						latch.countDown();
					}

					@Override
					public void onException(int reqkey, WebAPIException ex) {
						latch.countDown();
					}
				});
		assertTrue(latch.await(200, TimeUnit.SECONDS));

		assertNotNull(schedule.getOperationRecord());
		assertTrue(schedule.getOperationRecord().get().getDepartedAt()
				.isPresent());

		latch = new CountDownLatch(1);
		api.arrivalOperationSchedule(schedules.get(1),
				new WebAPICallback<OperationSchedule>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							OperationSchedule result) {
						schedule = result;
						latch.countDown();
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
						latch.countDown();
					}

					@Override
					public void onException(int reqkey, WebAPIException ex) {
						latch.countDown();
					}
				});
		assertTrue(latch.await(100, TimeUnit.SECONDS));

		assertNotNull(schedule.getOperationRecord());
		assertTrue(schedule.getOperationRecord().get().getArrivedAt()
				.isPresent());

		prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		api.getOffPassenger(os2, res, res.getFellowUsers().get(0), prec,
				new EmptyWebAPICallback<Void>());
		api.cancelGetOffPassenger(os2, res, res.getFellowUsers().get(0),
				new EmptyWebAPICallback<Void>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							Void result) {
						latch.countDown();
					}
				});

		assertTrue(latch.await(20, TimeUnit.SECONDS));
		serverPassengerRecord = new SyncCall<PassengerRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getPassengerRecord(
						passengerRecords.get(0).getId(), this);
			}
		}.getResult();
		assertNotNull(serverPassengerRecord);
		assertTrue(serverPassengerRecord.getGetOnTime().isPresent());
		assertFalse(serverPassengerRecord.getGetOffTime().isPresent());
		assertTrue(serverPassengerRecord.isRiding());
		assertEquals(os1.getId(), serverPassengerRecord
				.getDepartureOperationScheduleId().get());
		// TODO:決められた乗車降車場以外で乗降した場合の処理が実装されたらコメントを外す
		// assertFalse(serverPassengerRecord.getArrivalOperationScheduleId()
		//		.isPresent());
	}

	public void testPassengerCancelGetOffOrdered() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		schedules = null;
		createTestOperationSchedules();

		assertNotNull(schedules);
		assertEquals(2, schedules.size());

		OperationSchedule os1 = schedules.get(0);
		OperationSchedule os2 = schedules.get(1);
		Reservation res = os1.getReservationsAsDeparture().get(0);

		assertFalse(schedules.get(1).getReservationsAsArrival().get(0)
				.getFellowUsers().isEmpty());

		PassengerRecord prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		api.getOnPassenger(os1, res, res.getFellowUsers().get(0), prec,
				new EmptyWebAPICallback<Void>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							Void result) {
						latch.countDown();
					}
				});
		latch.await();

		latch = new CountDownLatch(1);
		api.departureOperationSchedule(schedules.get(0),
				new EmptyWebAPICallback<OperationSchedule>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							OperationSchedule result) {
						schedule = result;
						latch.countDown();
					}
				});
		assertTrue(latch.await(200, TimeUnit.SECONDS));

		latch = new CountDownLatch(1);
		api.arrivalOperationSchedule(schedules.get(1),
				new EmptyWebAPICallback<OperationSchedule>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							OperationSchedule result) {
						schedule = result;
						latch.countDown();
					}
				});
		assertTrue(latch.await(100, TimeUnit.SECONDS));

		assertTrue(schedule.getOperationRecord().isPresent());
		assertTrue(schedule.getOperationRecord().get().getArrivedAt()
				.isPresent());

		prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		offline = true;
		final AtomicInteger order = new AtomicInteger(0);
		semaphore = new Semaphore(0);
		latch = new CountDownLatch(1);
		int max = 50;
		for (int i = 0; i < max; ++i) {
			final int fi = i;
			if (i % 2 == 0) {
				api.getOffPassenger(os2, res, res.getFellowUsers().get(0),
						prec, new EmptyWebAPICallback<Void>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									Void result) {
								order.compareAndSet(fi, fi + 1);
								semaphore.release();
							}
						});
			} else {
				api.cancelGetOffPassenger(os2, res,
						res.getFellowUsers().get(0),
						new EmptyWebAPICallback<Void>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									Void result) {
								order.compareAndSet(fi, fi + 1);
								semaphore.release();
							}
						});
			}
			if (i == max / 2) {
				api.getVehicleNotifications(new EmptyWebAPICallback<List<VehicleNotification>>() {
					public void onSucceed(int reqkey, int statusCode,
							List<VehicleNotification> result) {
						latch.countDown();
					}
				});
			}
		}

		assertFalse(semaphore.tryAcquire(20, TimeUnit.SECONDS));
		offline = false;
		assertTrue(semaphore.tryAcquire(10, 100, TimeUnit.SECONDS));
		assertEquals(0, latch.getCount()); // 挟み込まれたgetVehicleNotifications()が先に完了していることをチェック
		assertTrue(semaphore.tryAcquire(max - 10, 1000, TimeUnit.SECONDS));
		assertEquals(max, order.get());
	}

	public void testPassengerCancelGetOnOrdered() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		schedules = null;
		createTestOperationSchedules();

		assertNotNull(schedules);
		assertEquals(2, schedules.size());

		OperationSchedule os1 = schedules.get(0);
		Reservation res = os1.getReservationsAsDeparture().get(0);
		Reservation res2 = os1.getReservationsAsDeparture().get(1);

		assertFalse(schedules.get(1).getReservationsAsArrival().get(0)
				.getFellowUsers().isEmpty());

		PassengerRecord prec = new PassengerRecord();

		latch = new CountDownLatch(1);
		offline = true;
		final AtomicInteger order = new AtomicInteger(0);
		semaphore = new Semaphore(0);
		int max = 10;
		for (int i = 0; i < max; ++i) {
			final int fi = i;
			if (i % 2 == 0) {
				api.getOnPassenger(os1, res, res.getFellowUsers().get(0), prec,
						new EmptyWebAPICallback<Void>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									Void result) {
								order.compareAndSet(fi, fi + 1);
								semaphore.release();
							}
						});
			} else {
				api.cancelGetOnPassenger(os1, res, res.getFellowUsers().get(0),
						new EmptyWebAPICallback<Void>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									Void result) {
								order.compareAndSet(fi, fi + 1);
								semaphore.release();
							}
						});
			}
			if (i == max / 2) {
				api.getOnPassenger(os1, res2, res2.getFellowUsers().get(0),
						prec, new EmptyWebAPICallback<Void>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									Void result) {
								latch.countDown();
							}
						});
			}
		}

		assertFalse(semaphore.tryAcquire(20, TimeUnit.SECONDS));
		offline = false;
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		assertFalse(semaphore.tryAcquire(max)); // 挟み込まれたgetOnPassenger(res2)が先に完了することをチェック
		assertTrue(semaphore.tryAcquire(max, 100, TimeUnit.SECONDS));
		assertEquals(max, order.get());
	}

	private void createTestOperationSchedules() throws Exception {
		Date now = new Date();

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 22);
		Date dtDeparture = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 40);
		Date dtArrival = cal.getTime();

		User user = master.createUser("login1", "もぎ", "けんた");
		UnitAssignment ua = record.createUnitAssignment("1号車");
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, cal.getTime());

		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		OperationSchedule os1 = record.createOperationSchedule(ua, p1, now);
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		OperationSchedule os2 = record.createOperationSchedule(ua, p2, now);

		Demand demand = record.createDemand(user, ua, p1, dtDeparture, p2,
				dtArrival, 0);
		Reservation res = record.createReservation(user, demand, ua, p1, os1,
				dtDeparture, p2, os2, dtArrival, 500);
		Reservation res2 = record.createReservation(user, demand, ua, p1, os1,
				dtDeparture, p2, os2, dtArrival, 500);

		assertNotNull(record.createReservationUser(res, user));
		assertNotNull(record.createReservationUser(res2, user));

		PassengerRecord pr1 = record.createPassengerRecord(res, user, os1, os2,
				0);
		PassengerRecord pr2 = record.createPassengerRecord(res2, user, os1,
				os2, 0);

		assertNotNull(pr1);
		assertNotNull(pr2);

		passengerRecords.add(pr1);
		passengerRecords.add(pr2);

		latch = new CountDownLatch(1);
		api.getOperationSchedules(new WebAPICallback<List<OperationSchedule>>() {
			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<OperationSchedule> result) {
				schedules = result;
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				latch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}
		});
		assertTrue(latch.await(100, TimeUnit.SECONDS));
	}

	public void testSendServiceUnitStatusLog() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		callTestSendServiceUnitStatusLog(false);
	}

	public void testSendServiceUnitStatusLogOffline() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		callTestSendServiceUnitStatusLog(true);
	}

	protected void callTestSendServiceUnitStatusLog(boolean offlineTest)
			throws Exception {
		latch = new CountDownLatch(1);
		schedules = null;

		UnitAssignment ua = record.createUnitAssignment("1号車");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, cal.getTime());

		ServiceUnitStatusLog log = new ServiceUnitStatusLog();
		log.setLatitude(new BigDecimal(35.2));
		log.setLongitude(new BigDecimal(135.8));
		if (offlineTest) {
			offline = true;
		}

		api.sendServiceUnitStatusLog(log,
				new EmptyWebAPICallback<ServiceUnitStatusLog>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							ServiceUnitStatusLog result) {
						serviceUnitStatusLog = result;
						latch.countDown();
					}
				});
		if (offlineTest) {
			assertFalse(latch.await(20, TimeUnit.SECONDS));
			offline = false;
		}
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		assertNotNull(serviceUnitStatusLog);
		assertEquals(new BigDecimal(35.2), serviceUnitStatusLog.getLatitude());
		ServiceUnitStatusLog serverServiceUnitStatusLog = new SyncCall<ServiceUnitStatusLog>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getServiceUnitStatusLog(
						serviceUnitStatusLog.getId(), this);
			}
		}.getResult();
		assertNotNull(serverServiceUnitStatusLog);
		assertTrue(serverServiceUnitStatusLog.getOfflineTime().isPresent());
		if (offlineTest) {
			assertTrue(serverServiceUnitStatusLog.getOffline().or(false));
		} else {
			assertFalse(serverServiceUnitStatusLog.getOffline().or(false));
		}
	}

	public void testSendServiceUnitStatusLogUseOnlyOneThread() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());

		UnitAssignment ua = record.createUnitAssignment("1号車");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, cal.getTime());

		ServiceUnitStatusLog log = new ServiceUnitStatusLog();
		log.setLatitude(new BigDecimal(35.2));
		log.setLongitude(new BigDecimal(135.8));

		latch = new CountDownLatch(2);
		List<Integer> keys = new LinkedList<Integer>();
		final Integer MAX = 1000;
		final AtomicInteger c = new AtomicInteger(0);
		offline = true;
		for (Integer i = 0; i < MAX; ++i) {
			int k = api.sendServiceUnitStatusLog(log,
					new EmptyWebAPICallback<ServiceUnitStatusLog>() {
						@Override
						public void onSucceed(int reqkey, int statusCode,
								ServiceUnitStatusLog result) {
							c.incrementAndGet();
						}
					});
			keys.add(k);
			if (i.equals(MAX / 2) || i.equals(0)) {
				api.getVehicleNotifications(new EmptyWebAPICallback<List<VehicleNotification>>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							List<VehicleNotification> result) {
						latch.countDown();
					}
				});
			}
		}
		offline = false;
		try {
			assertTrue(latch.await(200, TimeUnit.SECONDS));
			assertTrue(c.get() < MAX / 50);
		} finally {
			for (Integer key : keys) {
				api.abort(key);
			}
		}
	}

	public void testRestoreRequest() throws Exception {
		File backupFile = getContext().getFileStreamPath(
				"backup.serialized");
		offline = true;
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get(), backupFile);
		latch = new CountDownLatch(1);
		schedules = null;

		UnitAssignment ua = record.createUnitAssignment("1号車");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, cal.getTime());

		ServiceUnitStatusLog log = new ServiceUnitStatusLog();
		log.setOrientation(10);
		log.setTemperature(20);

		api.saveOnClose(api.sendServiceUnitStatusLog(log, null));
		api.close();
		Thread.sleep(10 * 1000);

		List<ServiceUnitStatusLog> serverServiceUnitStatusLogs = new SyncCall<List<ServiceUnitStatusLog>>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getAllServiceUnitStatusLogs(this);
			}
		}.getResult();
		assertTrue(serverServiceUnitStatusLogs.isEmpty());

		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get(), backupFile);
		Thread.sleep(10 * 1000);
		serverServiceUnitStatusLogs = new SyncCall<List<ServiceUnitStatusLog>>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getAllServiceUnitStatusLogs(this);
			}
		}.getResult();
		assertEquals(1, serverServiceUnitStatusLogs.size());
		assertEquals(10, serverServiceUnitStatusLogs.get(0).getOrientation()
				.get().intValue());
		assertEquals(20, serverServiceUnitStatusLogs.get(0).getTemperature()
				.get().intValue());
	}

	public void xtestSearchReservationCandidateAndCreateReservation()
			throws Exception {
		api = new WebAPI(TEST_SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());

		final List<ReservationCandidate> rcs = new LinkedList<ReservationCandidate>();
		latch = new CountDownLatch(1);
		Demand d = new Demand();
		d.setDepartureTime(new Date());
		api.searchReservationCandidate(d,
				new EmptyWebAPICallback<List<ReservationCandidate>>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							List<ReservationCandidate> result) {
						rcs.addAll(result);
						latch.countDown();
					}
				});
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		assertTrue(rcs.size() > 0);
		for (ReservationCandidate rc : rcs) {
			assertTrue(rc.getArrivalPlatform().isPresent());
			assertTrue(rc.getDeparturePlatform().isPresent());
		}

		final AtomicReference<Reservation> r = new AtomicReference<Reservation>();
		latch = new CountDownLatch(1);
		api.createReservation(rcs.get(0),
				new EmptyWebAPICallback<Reservation>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							Reservation result) {
						latch.countDown();
						r.set(result);
					}
				});
		assertTrue(latch.await(2000, TimeUnit.SECONDS));
		assertNotNull(r.get());
		assertTrue(r.get().getArrivalPlatform().isPresent());
		assertTrue(r.get().getDeparturePlatform().isPresent());
	}

	public void testAbort() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		UnitAssignment ua = record.createUnitAssignment("1号車");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, cal.getTime());
		offline = true;

		final Semaphore numSucceed = new Semaphore(0);
		List<Integer> keys = new LinkedList<Integer>();
		for (Integer i = 0; i < 10; ++i) {
			int k = api.sendServiceUnitStatusLog(new ServiceUnitStatusLog(),
					new WebAPICallback<ServiceUnitStatusLog>() {
						@Override
						public void onException(int reqkey, WebAPIException ex) {
						}

						@Override
						public void onFailed(int reqkey, int statusCode,
								String response) {
						}

						@Override
						public void onSucceed(int reqkey, int statusCode,
								ServiceUnitStatusLog result) {
							numSucceed.release();
						}
					});
			keys.add(k);
		}
		api.abort(keys.get(0));
		api.abort(keys.get(1));
		api.abort(keys.get(2));
		offline = false;
		assertTrue(numSucceed.tryAcquire(7, 60, TimeUnit.SECONDS));
		assertFalse(numSucceed.tryAcquire(10, TimeUnit.SECONDS));
		keys.clear();

		offline = true;
		final AtomicInteger i = new AtomicInteger(0);
		api.sendServiceUnitStatusLog(new ServiceUnitStatusLog(),
				new WebAPICallback<ServiceUnitStatusLog>() {
					@Override
					public void onException(int reqkey, WebAPIException ex) {
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
					}

					@Override
					public void onSucceed(int reqkey, int statusCode,
							ServiceUnitStatusLog result) {
						i.addAndGet(1);
					}
				});
		int k = api.sendServiceUnitStatusLog(new ServiceUnitStatusLog(),
				new WebAPICallback<ServiceUnitStatusLog>() {
					@Override
					public void onException(int reqkey, WebAPIException ex) {
					}

					@Override
					public void onFailed(int reqkey, int statusCode,
							String response) {
					}

					@Override
					public void onSucceed(int reqkey, int statusCode,
							ServiceUnitStatusLog result) {
						i.addAndGet(10);
					}
				});
		api.abort(k);
		offline = false;
		Thread.sleep(20 * 1000);
		assertEquals(i.get(), 1);
	}

	public void testOnException() throws Exception {
		api = new WebAPI("https://localhost:12345");
		final AtomicBoolean unexpected = new AtomicBoolean(false);
		latch = new CountDownLatch(10);
		api.getVehicleNotifications(new WebAPICallback<List<VehicleNotification>>() {
			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				unexpected.set(true);
			}

			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				unexpected.set(true);
			}
		});
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		assertFalse(unexpected.get());
	}

	public void testNewRequestIsPrior() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		UnitAssignment ua = record.createUnitAssignment("1号車");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, cal.getTime());

		offline = true;
		final Integer MAX = 100;
		final AtomicInteger numSucceed = new AtomicInteger(0);
		for (Integer i = 0; i < MAX; ++i) {
			if (i % 2 == 0) {
				api.sendServiceUnitStatusLog(new ServiceUnitStatusLog(),
						new EmptyWebAPICallback<ServiceUnitStatusLog>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									ServiceUnitStatusLog result) {
								numSucceed.incrementAndGet();
							}
						});
			} else {
				api.getVehicleNotifications(new EmptyWebAPICallback<List<VehicleNotification>>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							List<VehicleNotification> result) {
						numSucceed.incrementAndGet();
					}
				});
			}
		}
		offline = false;
		latch = new CountDownLatch(1);
		api.getVehicleNotifications(new EmptyWebAPICallback<List<VehicleNotification>>() {
			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				latch.countDown();
			}
		});
		assertTrue(latch.await(200, TimeUnit.SECONDS));
		Log.i(TAG, "testNewRequestIsPrior() numSucceed=" + numSucceed.get());
		assertTrue(numSucceed.get() < MAX / 10);
	}

	public void testGetServiceProvider() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().get());
		latch = new CountDownLatch(1);
		final AtomicReference<ServiceProvider> outputServiceProvider = new AtomicReference<ServiceProvider>();
		api.getServicePrivider(new WebAPICallback<ServiceProvider>() {
			@Override
			public void onException(int reqkey, WebAPIException ex) {
				latch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				latch.countDown();
			}

			@Override
			public void onSucceed(int reqkey, int statusCode,
					ServiceProvider result) {
				outputServiceProvider.set(result);
				latch.countDown();
			}
		});
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		assertNotNull(outputServiceProvider.get());
		assertEquals(serviceProvider.getId(), outputServiceProvider.get()
				.getId());
	}
}
