package com.kogasoftware.odt.invehicledevice.test.integration;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class MockDataSource implements DataSource {

	private List<OperationSchedule> lOperationSchedule = new LinkedList<OperationSchedule>();
	private final List<ReservationCandidate> lReservationCandidate = new LinkedList<ReservationCandidate>();

	private Date nextNotifyDate = new Date();
	private boolean NotificationFlag = false;

	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public InVehicleDevice getInVehicleDevice() {
		InVehicleDevice model = new InVehicleDevice();
		model.setId(10);
		model.setTypeNumber("TYPENUMBER543210");
		model.setModelName("MODELNAME09876");
		return model;
	}

	@Override
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		return 0;
	}

	@Override
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		return 0;
	}

	@Override
	public List<OperationSchedule> getOperationSchedules() {

		return lOperationSchedule;

	}

	@Override
	public List<VehicleNotification> getVehicleNotifications()
			throws WebAPIException {

		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(e);
		}

		List<VehicleNotification> l = new LinkedList<VehicleNotification>();

		if (NotificationFlag) {
			nextNotifyDate = new Date(new Date().getTime() + 10 * 1000);
			VehicleNotification n = new VehicleNotification();
			n.setBody("テスト通知が行われました " + new Date());
			l.add(n);
		}

		return l;

	}

	@Override
	public int responseVehicleNotification(VehicleNotification vn,
			int response, WebAPICallback<VehicleNotification> callback) {
		return 0;
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback) {
		return 0;
	}

	public void setNotificationFlag(boolean bNotificationFlag) {

		NotificationFlag = bNotificationFlag;

	}

	public void setOperationSchedules(Integer iOperationScheduleCount) {

		System.out.println("setOperationSchedules " + iOperationScheduleCount);

		lOperationSchedule = new LinkedList<OperationSchedule>();

		String u1a = "{id: 1011, departure_schedule_id: 1, arrival_schedule_id: 2 ,passenger_count:5, user: {first_name: '名前a', last_name: '名字a'},memo: 'テストメモ1'}";
		String u1b = "{id: 1012, departure_schedule_id: 1, arrival_schedule_id: 3 ,passenger_count:6, user: {first_name: '名前b', last_name: '名字b'},memo: 'テストメモ2'}";
		String u1c = "{id: 1013, departure_schedule_id: 1, arrival_schedule_id: 4 ,passenger_count:7, user: {first_name: '名前c', last_name: '名字c'}}";
		String u1d = "{id: 1014, departure_schedule_id: 1, arrival_schedule_id: 5 ,passenger_count:16, user: {first_name: '名前d', last_name: '名字d'}}";
		String u1e = "{id: 1015, departure_schedule_id: 1, arrival_schedule_id: 6 ,passenger_count:15, user: {first_name: '名前e', last_name: '名字e'}}";
		String u1f = "{id: 1016, departure_schedule_id: 1, arrival_schedule_id: 6 ,passenger_count:17, user: {first_name: '名前f', last_name: '名字f'}}";

		String sOperationSchedule = new String();

		try {

			if (iOperationScheduleCount > 0) {
				sOperationSchedule = "{ id: 1,"
						+ "arrival_estimate: '2012-01-01T09:00:00+09:00', "
						+ "departure_estimate: '2012-01-01T09:15:00+09:00', "
						+ "platform: {name: '9_瀬戸内警察署前停留所（千手・尾張方面行き）', name_ruby: 'せとうちけいさつしょまえていりゅうじょ（せんず・おわりほうめんゆき）'}, "
						+ "reservations_as_arrival: [" + "],"
						+ "reservations_as_departure: [" + u1a + "," + u1b
						+ "," + u1c + "," + u1d + "," + u1e + ", " + u1f + "]}";
				JSONObject j1 = new JSONObject(sOperationSchedule);
				lOperationSchedule.add(new OperationSchedule(j1));
			}

			if (iOperationScheduleCount > 1) {
				JSONObject j2 = new JSONObject(
						"{ id: 2,"
								+ "arrival_estimate: '2012-01-01T09:30:00+09:00', "
								+ "departure_estimate: '2012-01-01T10:05:00+09:00', "
								+ "platform: {name: '85_特別養護老人ホームあじさいのおか牛窓／在宅介護支援センターＡＪＩＳＡＩ',name_ruby: 'とくべつようごろうじんほーむあじさいのおかうしまどざいたくかいごしえんせんたーあじさい'}, "
								+ "reservations_as_arrival: [{passenger_count: 5}]}");
				lOperationSchedule.add(new OperationSchedule(j2));
			}

			if (iOperationScheduleCount > 2) {
				JSONObject j3 = new JSONObject(
						"{ id: 3,"
								+ "arrival_estimate: '2012-01-01T10:30:00+09:00', "
								+ "departure_estimate: '2012-01-01T10:33:00+09:00', "
								+ "platform: {name: 'テスト上野動物園前', name_ruby: 'てすとうえのどうぶつえんまえ'}, "
								+ "reservations_as_departure: [{passenger_count: 5}, {passenger_count: 6}, {passenger_count: 7}]}");
				lOperationSchedule.add(new OperationSchedule(j3));
			}

			if (iOperationScheduleCount > 3) {
				JSONObject j4 = new JSONObject(
						"{ id: 4,"
								+ "arrival_estimate: '2012-01-01T11:10:00+09:00', "
								+ "departure_estimate: '2012-01-01T11:15:00+09:00', "
								+ "platform: {name: 'テスト上野広小路前', name_ruby: 'てすとうえのひろこうじまえ'}, "
								+ "reservations_as_arrival: [] ,"
								+ "reservations_as_departure: [{passenger_count: 7}]}");
				lOperationSchedule.add(new OperationSchedule(j4));
			}

			if (iOperationScheduleCount > 4) {
				JSONObject j5 = new JSONObject(
						"{ id: 5,"
								+ "arrival_estimate: '2012-01-01T12:00:00+09:00', "
								+ "departure_estimate: '2012-01-01T12:05:00+09:00', "
								+ "platform: {name: 'テスト湯島天神前', name_ruby: 'てすとゆしまてんじんまえ'}}");
				lOperationSchedule.add(new OperationSchedule(j5));
			}

			if (iOperationScheduleCount > 5) {
				JSONObject j6 = new JSONObject(
						"{ id: 6,"
								+ "arrival_estimate: '2012-01-01T13:03:00+09:00', "
								+ "departure_estimate: '2012-01-01T13:10:30+09:00', "
								+ "platform: {name: 'テストＪＲ御徒町駅前', name_ruby: 'てすとじぇいあーるおかちまちえきまえ'}, "
								+ "reservations_as_arrival: [{passenger_count: 50}, {passenger_count: 60}, {passenger_count: 70}] ,"
								+ "reservations_as_departure: [{passenger_count: 150}, {passenger_count: 160}, {passenger_count: 170}]}");
				lOperationSchedule.add(new OperationSchedule(j6));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setReservation(Integer iReservation) {

		System.out.println("setReservation " + iReservation);

		lOperationSchedule = new LinkedList<OperationSchedule>();

		String u1a = "";
		String u1b = "";
		String u1c = "";
		String u1d = "";
		String u1e = "";
		String u1f = "";
		String u1g = "";
		String u1h = "";
		String u1i = "";
		String u1j = "";
		String u1k = "";
		String u1l = "";
		String u1m = "";
		String u1n = "";
		String u1o = "";
		if (iReservation > 0) {
			u1a = "{passenger_count: 1 ,id: 1011,departure_schedule_id: 101,arrival_schedule_id: 106,user: {first_name: 'テストa', last_name: '名字a'},memo: 'テストメモ1'}";
		}
		if (iReservation > 1) {
			u1b = ", {passenger_count: 2,id: 1012,departure_schedule_id: 101,arrival_schedule_id: 104,user: {first_name: 'テストb', last_name: '名字b'}}";
		}
		if (iReservation > 2) {
			u1c = ", {passenger_count: 3,id: 1013,departure_schedule_id: 101,arrival_schedule_id: 103,user: {first_name: 'テストc', last_name: '名字c'}}";
		}
		if (iReservation > 3) {
			u1d = ", {passenger_count: 4,id: 1014,departure_schedule_id: 101,arrival_schedule_id: 102,user: {first_name: 'テストd', last_name: '名字d'}}";
		}
		if (iReservation > 4) {
			u1e = ", {passenger_count: 5,id: 1015,departure_schedule_id: 101,arrival_schedule_id: 105,user: {first_name: 'テストe', last_name: '名字e'}}";
		}
		if (iReservation > 5) {
			u1f = ", {passenger_count: 6,id: 1016,departure_schedule_id: 101,arrival_schedule_id: 104,user: {first_name: 'テストf', last_name: '名字f'}}";
		}

		u1g = ", {passenger_count: 1,id: 1017,departure_schedule_id: 102,arrival_schedule_id: 103,user: {first_name: 'テストg', last_name: '名字g'}}";
		u1h = ", {passenger_count: 1,id: 1018,departure_schedule_id: 103,arrival_schedule_id: 104,user: {first_name: 'テストh', last_name: '名字h'}}";
		u1i = ", {passenger_count: 1,id: 1019,departure_schedule_id: 104,arrival_schedule_id: 106,user: {first_name: 'テストi', last_name: '名字i'}}";
		u1j = ", {passenger_count: 1,id: 1020,departure_schedule_id: 102,arrival_schedule_id: 105,user: {first_name: 'テストj', last_name: '名字j'}}";
		u1k = ", {passenger_count: 1,id: 1021,departure_schedule_id: 103,arrival_schedule_id: 104,user: {first_name: 'テストk', last_name: '名字k'}}";
		u1l = ", {passenger_count: 1,id: 1022,departure_schedule_id: 102,arrival_schedule_id: 106,user: {first_name: 'テストl', last_name: '名字l'}}";
		u1m = ", {passenger_count: 1,id: 1023,departure_schedule_id: 103,arrival_schedule_id: 105,user: {first_name: 'テストm', last_name: '名字m'}}";
		u1n = ", {passenger_count: 1,id: 1024,departure_schedule_id: 104,arrival_schedule_id: 106,user: {first_name: 'テストn', last_name: '名字n'}}";
		u1o = ", {passenger_count: 1,id: 1025,departure_schedule_id: 104,arrival_schedule_id: 105,user: {first_name: 'テストo', last_name: '名字o'}}";

		try {

			JSONObject j1 = new JSONObject(
					"{"
							+ "id: 101, "
							+ "arrival_estimate: '2012-01-01T09:00:00+09:00', "
							+ "departure_estimate: '2012-01-01T09:15:00+09:00', "
							+ "platform: {name: 'テストコガソフトウェア前', name_ruby: 'てすとこがそふとうぇあまえ'}, "
							+ "reservations_as_arrival: [], "
							+ "reservations_as_departure: [" + u1a + "," + u1b
							+ "," + u1c + "," + u1d + "," + u1e + "," + u1f
							+ "]}");
			lOperationSchedule.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject(
					"{"
							+ "id: 102, "
							+ "arrival_estimate: '2012-01-01T09:30:00+09:00', "
							+ "departure_estimate: '2012-01-01T09:35:00+09:00', "
							+ "platform: {name: 'テスト上野御徒町駅前',name_ruby: 'てすとうえのおかちまちえきまえ'}, "
							+ "reservations_as_arrival: [" + u1d + "], "
							+ "reservations_as_departure: [" + u1g + "," + u1j
							+ "," + u1l + "]}");
			lOperationSchedule.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject(
					"{"
							+ "id: 103, "
							+ "arrival_estimate: '2012-01-01T10:30:00+09:00', "
							+ "departure_estimate: '2012-01-01T10:33:00+09:00', "
							+ "platform: {name: 'テスト上野動物園前', name_ruby: 'てすとうえのどうぶつえんまえ'}, "
							+ "reservations_as_arrival: [" + u1c + "," + u1g
							+ "," + "], " + "reservations_as_departure: ["
							+ u1h + "," + u1k + "," + u1m + "]}");
			lOperationSchedule.add(new OperationSchedule(j3));

			JSONObject j4 = new JSONObject(
					"{"
							+ "id: 104, "
							+ "arrival_estimate: '2012-01-01T11:10:00+09:00', "
							+ "departure_estimate: '2012-01-01T11:15:00+09:00', "
							+ "platform: {name: 'テスト上野広小路前', name_ruby: 'てすとうえのひろこうじまえ'}, "
							+ "reservations_as_arrival: [" + u1b + "," + u1f
							+ "," + u1h + "," + u1k + "], "
							+ "reservations_as_departure: [" + u1i + "," + u1n
							+ "," + u1o + "]}");
			lOperationSchedule.add(new OperationSchedule(j4));

			JSONObject j5 = new JSONObject(
					"{"
							+ "id: 105, "
							+ "arrival_estimate: '2012-01-01T12:00:00+09:00', "
							+ "departure_estimate: '2012-01-01T12:05:00+09:00', "
							+ "platform: {name: 'テスト湯島天神前', name_ruby: 'てすとゆしまてんじんまえ'}}"
							+ "reservations_as_arrival: [" + u1e + "," + u1j
							+ "," + u1m + "," + u1o + "], "
							+ "reservations_as_departure: []}");
			lOperationSchedule.add(new OperationSchedule(j5));

			JSONObject j6 = new JSONObject(
					"{"
							+ "id: 106, "
							+ "arrival_estimate: '2012-01-01T13:03:00+09:00', "
							+ "departure_estimate: '2012-01-01T13:10:30+09:00', "
							+ "platform: {name: 'テストＪＲ御徒町駅前', name_ruby: 'てすとじぇいあーるおかちまちえきまえ'}, "
							+ "reservations_as_arrival: [" + u1a + "," + u1i
							+ "," + u1l + "," + u1n + "], "
							+ "reservations_as_departure: []}");
			lOperationSchedule.add(new OperationSchedule(j6));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setReservationCandidate(Integer iCount, Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId) {

		DateFormat f = new SimpleDateFormat("HH:mm");
		try {

			if (iCount > 0) {
				ReservationCandidate c1 = new ReservationCandidate();
				c1.setDepartureTime(f.parse("12:35"));
				c1.setArrivalTime(f.parse("13:34"));
				Platform ap1 = new Platform();
				ap1.setName("テスト駅1");
				Platform dp1 = new Platform();
				dp1.setName("テスト駅2");
				c1.setArrivalPlatform(ap1);
				c1.setDeparturePlatform(dp1);
				lReservationCandidate.add(c1);
			}

			if (iCount > 1) {
				ReservationCandidate c2 = new ReservationCandidate();
				c2.setDepartureTime(f.parse("15:12"));
				c2.setArrivalTime(f.parse("16:45"));
				Platform ap2 = new Platform();
				ap2.setName("テスト駅3");
				Platform dp2 = new Platform();
				dp2.setName("テスト駅4");
				c2.setArrivalPlatform(ap2);
				c2.setDeparturePlatform(dp2);
				lReservationCandidate.add(c2);
			}

			if (iCount > 2) {
				ReservationCandidate c3 = new ReservationCandidate();
				c3.setDepartureTime(f.parse("17:39"));
				c3.setArrivalTime(f.parse("18:01"));
				Platform ap3 = new Platform();
				ap3.setName("テスト駅5");
				Platform dp3 = new Platform();
				dp3.setName("テスト駅6");
				c3.setArrivalPlatform(ap3);
				c3.setDeparturePlatform(dp3);
				lReservationCandidate.add(c3);
			}

			if (iCount > 3) {
				ReservationCandidate c4 = new ReservationCandidate();
				c4.setDepartureTime(f.parse("17:39"));
				c4.setArrivalTime(f.parse("18:01"));
				Platform ap4 = new Platform();
				ap4.setName("テスト駅7");
				Platform dp4 = new Platform();
				dp4.setName("テスト駅8");
				c4.setArrivalPlatform(ap4);
				c4.setDeparturePlatform(dp4);
				lReservationCandidate.add(c4);
			}

			if (iCount > 4) {
				ReservationCandidate c5 = new ReservationCandidate();
				c5.setDepartureTime(f.parse("19:01"));
				c5.setArrivalTime(f.parse("19:15"));
				Platform ap5 = new Platform();
				ap5.setName("テスト駅9");
				Platform dp5 = new Platform();
				dp5.setName("テスト駅10");
				c5.setArrivalPlatform(ap5);
				c5.setDeparturePlatform(dp5);
				lReservationCandidate.add(c5);
			}

			if (iCount > 5) {
				ReservationCandidate c6 = new ReservationCandidate();
				c6.setDepartureTime(f.parse("19:16"));
				c6.setArrivalTime(f.parse("19:30"));
				Platform ap6 = new Platform();
				ap6.setName("テスト駅11");
				Platform dp6 = new Platform();
				dp6.setName("テスト駅12");
				c6.setArrivalPlatform(ap6);
				c6.setDeparturePlatform(dp6);
				lReservationCandidate.add(c6);
			}

			if (iCount > 6) {
				ReservationCandidate c7 = new ReservationCandidate();
				c7.setDepartureTime(f.parse("19:31"));
				c7.setArrivalTime(f.parse("20:39"));
				Platform ap7 = new Platform();
				ap7.setName("駅11");
				Platform dp7 = new Platform();
				dp7.setName("駅12");
				c7.setArrivalPlatform(ap7);
				c7.setDeparturePlatform(dp7);
				lReservationCandidate.add(c7);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
	}

	@Override
	public int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, WebAPICallback<PassengerRecord> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, WebAPICallback<PassengerRecord> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int searchReservationCandidate(Demand demand,
			WebAPICallback<List<ReservationCandidate>> callback) {
		callback.onSucceed(0, 200, lReservationCandidate);
		return 0;
	}

	@Override
	public int createReservation(ReservationCandidate reservationCandidate,
			WebAPICallback<Reservation> callback) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		callback.onSucceed(0, 200, new Reservation());
		return 0;
	}

	@Override
	public void cancel(int reqkey) {
	}

	@Override
	public int getMapTile(LatLng center, Integer zoom,
			WebAPICallback<Bitmap> webAPICallback) {
		return 0;
	}
}
