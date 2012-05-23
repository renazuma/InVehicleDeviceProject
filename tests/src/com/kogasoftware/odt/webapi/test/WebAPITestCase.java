package com.kogasoftware.odt.webapi.test;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.test.ActivityInstrumentationTestCase2;

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
import com.kogasoftware.odt.webapi.model.PassengerRecords;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.UnitAssignment;
import com.kogasoftware.odt.webapi.model.User;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webtestapi.GenerateMaster;
import com.kogasoftware.odt.webtestapi.GenerateRecord;
import com.kogasoftware.odt.webtestapi.SyncCall;

public class WebAPITestCase extends
		ActivityInstrumentationTestCase2<DummyActivity> {
	public static final String SERVER_HOST = "http://10.0.2.2:3334";
	public static final String TEST_SERVER_HOST = "http://10.0.2.2:3333";
	private static final String TAG = WebAPITestCase.class.getSimpleName();

	public WebAPITestCase() {
		super("com.kogasoftware.odt.webapi.test", DummyActivity.class);
	}

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

		protected boolean doHttpSession(WebAPIRequest<?> request)
				throws WebAPIException {
			if (offline) {
				throw new WebAPIException(true, "offline test");
			}
			return super.doHttpSession(request);
		}
	}

	class EmptyWebAPICallback<T> implements WebAPICallback<T> {
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
	CountDownLatch latch;
	CountDownLatch latch2;
	Semaphore semaphore;
	volatile boolean offline;
	private GenerateMaster master;
	private GenerateRecord record;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// マスタ生成
		master = new GenerateMaster(TEST_SERVER_HOST);
		master.cleanDatabase();
		master.createServiceProvider();
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
		callTestPasswordLogin(false);
	}

	public void testPasswordLoginNoRetry() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST);
		callTestPasswordLogin(true);
	}

	public void callTestPasswordLogin(boolean retryTest) throws Exception {
		semaphore = new Semaphore(0);

		InVehicleDevice ivd = new InVehicleDevice();
		ivd.setLogin("ivd1");
		ivd.setPassword("ivdpass");

		final AtomicBoolean succeed = new AtomicBoolean(false);

		if (retryTest) {
			offline = true;
		}

		api.login(ivd, new WebAPICallback<InVehicleDevice>() {
			@Override
			public void onSucceed(int reqkey, int statusCode,
					InVehicleDevice result) {
				semaphore.release();
				succeed.set(true);
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

		assertTrue(semaphore.tryAcquire(20, TimeUnit.SECONDS));
		if (!retryTest) {
			assertTrue(succeed.get());
			assertNotNull(api.getAuthenticationToken());
			assertTrue(api.getAuthenticationToken().length() > 0);
		} else {
			assertFalse(succeed.get());
			offline = false;
			assertFalse(semaphore.tryAcquire(20, TimeUnit.SECONDS)); // エラーでも再送信しない
			assertFalse(succeed.get());
		}
	}

	List<VehicleNotification> notifications;

	public void testGetVehicleNotifications() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull());
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
				.getAuthenticationToken().orNull());
		callTestResponseVehicleNotification(false);
	}

	public void testResponseVehicleNotificationOffline() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull());
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

	List<OperationSchedule> schedules;
	protected OperationSchedule schedule;
	protected PassengerRecord passengerRecord;
	protected ServiceUnitStatusLog serviceUnitStatusLog;

	public void testGetOperationSchedules() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull());
		callTestGetOperationSchedules(false);
	}

	public void testGetOperationSchedulesOffline() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull());
		callTestGetOperationSchedules(true);
	}

	public void callTestGetOperationSchedules(boolean offlineTest)
			throws Exception {
		latch = new CountDownLatch(1);
		schedules = null;

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 20);
		Date dtArrival1 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 22);
		Date dtDeparture1 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 40);
		Date dtArrival2 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 45);
		Date dtDeparture2 = cal.getTime();

		User user = master.createUser("login1", "もぎ", "けんた");
		UnitAssignment ua = record.createUnitAssignment("1号車");
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, cal.getTime());

		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		OperationSchedule os1 = record.createOperationSchedule(ua, p1,
				dtArrival1, dtDeparture1);
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		OperationSchedule os2 = record.createOperationSchedule(ua, p2,
				dtArrival2, dtDeparture2);

		Demand demand = record.createDemand(user, ua, p1, dtDeparture1, p2,
				dtArrival2, 0);
		assertNotNull(record.createReservation(user, demand, ua, p1, os1,
				dtDeparture1, p2, os2, dtArrival2, 0));

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

		assertNotNull(schedules.get(1).getReservationsAsArrival().get(0)
				.getUser().orNull());

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
				.getAuthenticationToken().orNull());
		callTestPassengerGetOnAndOff(true);
	}

	public void testPassengerGetOnAndOff() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull());
		callTestPassengerGetOnAndOff(false);
	}

	protected void callTestPassengerGetOnAndOff(boolean offlineTest) throws Exception {
		latch = new CountDownLatch(1);
		schedules = null;
		createTestOperationSchedules();

		assertNotNull(schedules);
		assertEquals(2, schedules.size());

		OperationSchedule os1 = schedules.get(0);
		OperationSchedule os2 = schedules.get(1);
		Reservation res = os1.getReservationsAsDeparture().get(0);

		assertNotNull(schedules.get(1).getReservationsAsArrival().get(0)
				.getUser().orNull());

		passengerRecord = null;
		PassengerRecord prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		if (offlineTest) {
			offline = true;
		}
		api.getOnPassenger(os1, res, prec,
				new WebAPICallback<PassengerRecord>() {

					@Override
					public void onSucceed(int reqkey, int statusCode,
							PassengerRecord result) {
						passengerRecord = result;
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
		assertNotNull(passengerRecord);
		PassengerRecord serverPassengerRecord = new SyncCall<PassengerRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getPassengerRecord(
						passengerRecord.getId(), this);
			}
		}.getResult();
		assertNotNull(serverPassengerRecord);
		assertTrue(serverPassengerRecord.getGetOnTime().isPresent());
		assertEquals(os1.getId(), serverPassengerRecord
				.getDepartureOperationScheduleId().orNull());
		assertTrue(serverPassengerRecord.getGetOnTime().isPresent());
		assertTrue(PassengerRecords.isRiding(serverPassengerRecord));
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
		assertNotNull(schedule.getOperationRecord().orNull().getDepartedAt()
				.orNull());

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
		assertNotNull(schedule.getOperationRecord().orNull().getArrivedAt()
				.orNull());

		passengerRecord = null;
		prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		if (offlineTest) {
			offline = true;
		}
		api.getOffPassenger(os2, res, prec,
				new WebAPICallback<PassengerRecord>() {

					@Override
					public void onSucceed(int reqkey, int statusCode,
							PassengerRecord result) {
						passengerRecord = result;
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
		assertNotNull(passengerRecord);
		serverPassengerRecord = new SyncCall<PassengerRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getPassengerRecord(
						passengerRecord.getId(), this);
			}
		}.getResult();
		assertNotNull(serverPassengerRecord);
		assertTrue(serverPassengerRecord.getGetOffTime().isPresent());
		assertEquals(os1.getId(), serverPassengerRecord
				.getDepartureOperationScheduleId().orNull());
		assertEquals(os2.getId(), serverPassengerRecord
				.getArrivalOperationScheduleId().orNull());
		assertTrue(serverPassengerRecord.getGetOffTime().isPresent());
		assertTrue(PassengerRecords.isGotOff(serverPassengerRecord));

		if (offlineTest) {
			assertTrue(serverPassengerRecord.getGetOffTimeOffline().or(false));
		} else {
			assertFalse(serverPassengerRecord.getGetOffTimeOffline().or(false));
		}
	}

	public void testPassengerCancelGetOnAndOff() throws Exception {
		api = new WebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull());
		schedules = null;
		createTestOperationSchedules();

		assertNotNull(schedules);
		assertEquals(2, schedules.size());

		OperationSchedule os1 = schedules.get(0);
		OperationSchedule os2 = schedules.get(1);
		Reservation res = os1.getReservationsAsDeparture().get(0);

		assertNotNull(schedules.get(1).getReservationsAsArrival().get(0)
				.getUser().orNull());

		passengerRecord = null;
		PassengerRecord prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);

		api.getOnPassenger(os1, res, prec,
				new EmptyWebAPICallback<PassengerRecord>());
		api.cancelGetOnPassenger(os1, res,
				new EmptyWebAPICallback<PassengerRecord>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							PassengerRecord result) {
						passengerRecord = result;
						latch.countDown();
					}
				});

		assertTrue(latch.await(20, TimeUnit.SECONDS));
		assertNotNull(passengerRecord);
		PassengerRecord serverPassengerRecord = new SyncCall<PassengerRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getPassengerRecord(
						passengerRecord.getId(), this);
			}
		}.getResult();
		assertNotNull(serverPassengerRecord);
		assertFalse(serverPassengerRecord.getGetOnTime().isPresent());
		assertFalse(serverPassengerRecord.getDepartureOperationScheduleId()
				.isPresent());
		assertTrue(PassengerRecords.isUnhandled(serverPassengerRecord));

		latch = new CountDownLatch(1);
		api.getOnPassenger(os1, res, prec,
				new EmptyWebAPICallback<PassengerRecord>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							PassengerRecord result) {
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
		assertNotNull(schedule.getOperationRecord().orNull().getDepartedAt()
				.orNull());

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
		assertNotNull(schedule.getOperationRecord().orNull().getArrivedAt()
				.orNull());

		passengerRecord = null;
		prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		api.getOffPassenger(os2, res, prec,
				new EmptyWebAPICallback<PassengerRecord>());
		api.cancelGetOffPassenger(os2, res,
				new EmptyWebAPICallback<PassengerRecord>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							PassengerRecord result) {
						passengerRecord = result;
						latch.countDown();
					}
				});

		assertTrue(latch.await(20, TimeUnit.SECONDS));
		assertNotNull(passengerRecord);
		serverPassengerRecord = new SyncCall<PassengerRecord>() {
			@Override
			public int run() throws Exception {
				return master.getTestAPI().getPassengerRecord(
						passengerRecord.getId(), this);
			}
		}.getResult();
		assertNotNull(serverPassengerRecord);
		assertTrue(serverPassengerRecord.getGetOnTime().isPresent());
		assertFalse(serverPassengerRecord.getGetOffTime().isPresent());
		assertTrue(PassengerRecords.isRiding(serverPassengerRecord));
		assertEquals(os1.getId(), serverPassengerRecord
				.getDepartureOperationScheduleId().orNull());
		assertFalse(serverPassengerRecord.getArrivalOperationScheduleId()
				.isPresent());
	}

	public void testPassengerCancelGetOffOrdered() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull());
		schedules = null;
		createTestOperationSchedules();

		assertNotNull(schedules);
		assertEquals(2, schedules.size());

		OperationSchedule os1 = schedules.get(0);
		OperationSchedule os2 = schedules.get(1);
		Reservation res = os1.getReservationsAsDeparture().get(0);

		assertNotNull(schedules.get(1).getReservationsAsArrival().get(0)
				.getUser().orNull());

		passengerRecord = null;
		PassengerRecord prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		api.getOnPassenger(os1, res, prec,
				new EmptyWebAPICallback<PassengerRecord>() {
					@Override
					public void onSucceed(int reqkey, int statusCode,
							PassengerRecord result) {
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

		assertNotNull(schedule.getOperationRecord());
		assertNotNull(schedule.getOperationRecord().orNull().getArrivedAt()
				.orNull());

		passengerRecord = null;
		prec = new PassengerRecord();
		prec.setPayment(res.getPayment());
		prec.setPassengerCount(3);

		latch = new CountDownLatch(1);
		offline = true;
		final AtomicInteger order = new AtomicInteger(0);
		semaphore = new Semaphore(0);
		latch = new CountDownLatch(1);
		int max = 10;
		for (int i = 0; i < max; ++i) {
			final int fi = i;
			if (i % 2 == 0) {
				api.getOffPassenger(os2, res, prec,
						new EmptyWebAPICallback<PassengerRecord>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									PassengerRecord result) {
								order.compareAndSet(fi, fi + 1);
								semaphore.release();
							}
						});
			} else {
				api.cancelGetOffPassenger(os2, res,
						new EmptyWebAPICallback<PassengerRecord>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									PassengerRecord result) {
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
		assertTrue(latch.await(20, TimeUnit.SECONDS));
		assertFalse(semaphore.tryAcquire(max)); // 挟み込まれたgetVehicleNotifications()が先に完了することをチェック
		assertTrue(semaphore.tryAcquire(max, 100, TimeUnit.SECONDS));
		assertEquals(max, order.get());
	}

	public void testPassengerCancelGetOnOrdered() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull());
		schedules = null;
		createTestOperationSchedules();

		assertNotNull(schedules);
		assertEquals(2, schedules.size());

		OperationSchedule os1 = schedules.get(0);
		Reservation res = os1.getReservationsAsDeparture().get(0);
		Reservation res2 = os1.getReservationsAsDeparture().get(1);

		assertNotNull(schedules.get(1).getReservationsAsArrival().get(0)
				.getUser().orNull());

		passengerRecord = null;
		PassengerRecord prec = new PassengerRecord();

		latch = new CountDownLatch(1);
		offline = true;
		final AtomicInteger order = new AtomicInteger(0);
		semaphore = new Semaphore(0);
		int max = 10;
		for (int i = 0; i < max; ++i) {
			final int fi = i;
			if (i % 2 == 0) {
				api.getOnPassenger(os1, res, prec,
						new EmptyWebAPICallback<PassengerRecord>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									PassengerRecord result) {
								order.compareAndSet(fi, fi + 1);
								semaphore.release();
							}
						});
			} else {
				api.cancelGetOnPassenger(os1, res,
						new EmptyWebAPICallback<PassengerRecord>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									PassengerRecord result) {
								order.compareAndSet(fi, fi + 1);
								semaphore.release();
							}
						});
			}
			if (i == max / 2) {
				api.getOnPassenger(os1, res2, prec,
						new EmptyWebAPICallback<PassengerRecord>() {
							@Override
							public void onSucceed(int reqkey, int statusCode,
									PassengerRecord result) {
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
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 20);
		Date dtArrival1 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 22);
		Date dtDeparture1 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 40);
		Date dtArrival2 = cal.getTime();

		cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 45);
		Date dtDeparture2 = cal.getTime();

		User user = master.createUser("login1", "もぎ", "けんた");
		UnitAssignment ua = record.createUnitAssignment("1号車");
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		record.createServiceUnit(master.getDriver(), master.getVehicle(),
				master.getInVehicleDevice(), ua, cal.getTime());

		Platform p1 = master.createPlatform("乗降場1", "じょうこうじょう1");
		OperationSchedule os1 = record.createOperationSchedule(ua, p1,
				dtArrival1, dtDeparture1);
		Platform p2 = master.createPlatform("乗降場2", "じょうこうじょう2");
		OperationSchedule os2 = record.createOperationSchedule(ua, p2,
				dtArrival2, dtDeparture2);

		Demand demand = record.createDemand(user, ua, p1, dtDeparture1, p2,
				dtArrival2, 0);
		Reservation res = record.createReservation(user, demand, ua, p1, os1,
				dtDeparture1, p2, os2, dtArrival2, 500);
		Reservation res2 = record.createReservation(user, demand, ua, p1, os1,
				dtDeparture1, p2, os2, dtArrival2, 500);

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
				.getAuthenticationToken().orNull());
		callTestSendServiceUnitStatusLog(false);
	}

	public void testSendServiceUnitStatusLogOffline() throws Exception {
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull());
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
				new WebAPICallback<ServiceUnitStatusLog>() {

					@Override
					public void onSucceed(int reqkey, int statusCode,
							ServiceUnitStatusLog result) {
						serviceUnitStatusLog = result;
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

	public void testRestoreRequest() throws Exception {
		File backupFile = getInstrumentation().getContext().getFileStreamPath(
				"backup.serialized");
		offline = true;
		api = new OfflineTestWebAPI(SERVER_HOST, master.getInVehicleDevice()
				.getAuthenticationToken().orNull(), backupFile);
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

		api.sendServiceUnitStatusLog(log, null);
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
				.getAuthenticationToken().orNull(), backupFile);
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
}
