package com.kogasoftware.odt.invehicledevice.testutil.apiclient;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;

import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.invehicledevice.apiclient.EmptyInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.InVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class MockApiClient extends EmptyInVehicleDeviceApiClient {
	private List<OperationSchedule> lOperationSchedule = new LinkedList<OperationSchedule>();
	private boolean NotificationFlag = false;

	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			ApiClientCallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			ApiClientCallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public int responseVehicleNotification(VehicleNotification vn,
			ApiClientCallback<VehicleNotification> callback) {
		return 0;
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			ApiClientCallback<ServiceUnitStatusLog> callback) {
		return 0;
	}

	public void setNotificationFlag(boolean bNotificationFlag) {

		NotificationFlag = bNotificationFlag;

	}

	public void setOperationSchedules(Integer iOperationScheduleCount) {

		System.out.println("setOperationSchedules " + iOperationScheduleCount);

		lOperationSchedule = new LinkedList<OperationSchedule>();

		String ru1 = "{user: {id: 1, last_name: '名字a', first_name: '名前a'}}";
		String ru2 = "{user: {id: 2, last_name: '名字b', first_name: '名前b'}}";
		String ru3 = "{user: {id: 3, last_name: '名字c', first_name: '名前c'}}";
		String ru4 = "{user: {id: 4, last_name: '名字d', first_name: '名前d'}}";
		String ru5 = "{user: {id: 5, last_name: '名字e', first_name: '名前e'}}";
		String ru6 = "{user: {id: 6, last_name: '名字f', first_name: '名前f'}}";

		String rus1 = "[" + ru1 + "]";
		String rus2 = "[" + ru2 + "]";
		String rus3 = "[" + ru3 + "]";
		String rus4 = "[" + ru4 + "]";
		String rus5 = "[" + ru5 + ", " + ru6 + "]";

		String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, reservation_users: "
				+ rus1 + ", memo: 'テストメモ1'}";
		String r2 = "{id: 52, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 3, payment:   0, reservation_users: "
				+ rus2 + "}";
		String r3 = "{id: 53, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 4, payment: 500, reservation_users: "
				+ rus3 + ", memo: 'テストメモ3'}";
		String r4 = "{id: 54, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 5, payment: 500, reservation_users: "
				+ rus4 + "}";
		String r5 = "{id: 55, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 6, payment: 500, reservation_users: "
				+ rus5 + "}";

		String sOperationSchedule = new String();

		try {

			if (iOperationScheduleCount > 0) {
				sOperationSchedule = "{ id: 1,"
						+ "arrival_estimate: '2012-01-01T09:00:00+09:00', "
						+ "departure_estimate: '2012-01-01T09:15:00+09:00', "
						+ "platform: {name: '9_瀬戸内警察署前停留所（千手・尾張方面行き）', name_ruby: 'せとうちけいさつしょまえていりゅうじょ（せんず・おわりほうめんゆき）'}, "
						+ "reservations_as_arrival: [" + "],"
						+ "reservations_as_departure: [" + r1 + "," + r2 + ","
						+ r3 + "," + r4 + "," + r5 + ", ]}";
				String j1 = new String(sOperationSchedule);
				lOperationSchedule.add(OperationSchedule.parse(j1));
			}

			if (iOperationScheduleCount > 1) {
				String j2 = new String(
						"{ id: 2,"
								+ "arrival_estimate: '2012-01-01T09:30:00+09:00', "
								+ "departure_estimate: '2012-01-01T10:05:00+09:00', "
								+ "platform: {name: '85_特別養護老人ホームあじさいのおか牛窓／在宅介護支援センターＡＪＩＳＡＩ',name_ruby: 'とくべつようごろうじんほーむあじさいのおかうしまどざいたくかいごしえんせんたーあじさい'}, "
								+ "reservations_as_arrival: [{passenger_count: 5}]}");
				lOperationSchedule.add(OperationSchedule.parse(j2));
			}

			if (iOperationScheduleCount > 2) {
				String j3 = new String(
						"{ id: 3,"
								+ "arrival_estimate: '2012-01-01T10:30:00+09:00', "
								+ "departure_estimate: '2012-01-01T10:33:00+09:00', "
								+ "platform: {name: 'テスト上野動物園前', name_ruby: 'てすとうえのどうぶつえんまえ'}, "
								+ "reservations_as_departure: [{passenger_count: 5}, {passenger_count: 6}, {passenger_count: 7}]}");
				lOperationSchedule.add(OperationSchedule.parse(j3));
			}

			if (iOperationScheduleCount > 3) {
				String j4 = new String(
						"{ id: 4,"
								+ "arrival_estimate: '2012-01-01T11:10:00+09:00', "
								+ "departure_estimate: '2012-01-01T11:15:00+09:00', "
								+ "platform: {name: 'テスト上野広小路前', name_ruby: 'てすとうえのひろこうじまえ'}, "
								+ "reservations_as_arrival: [] ,"
								+ "reservations_as_departure: [{passenger_count: 7}]}");
				lOperationSchedule.add(OperationSchedule.parse(j4));
			}

			if (iOperationScheduleCount > 4) {
				String j5 = new String(
						"{ id: 5,"
								+ "arrival_estimate: '2012-01-01T12:00:00+09:00', "
								+ "departure_estimate: '2012-01-01T12:05:00+09:00', "
								+ "platform: {name: 'テスト湯島天神前', name_ruby: 'てすとゆしまてんじんまえ'}}");
				lOperationSchedule.add(OperationSchedule.parse(j5));
			}

			if (iOperationScheduleCount > 5) {
				String j6 = new String(
						"{ id: 6,"
								+ "arrival_estimate: '2012-01-01T13:03:00+09:00', "
								+ "departure_estimate: '2012-01-01T13:10:30+09:00', "
								+ "platform: {name: 'テストＪＲ御徒町駅前', name_ruby: 'てすとじぇいあーるおかちまちえきまえ'}, "
								+ "reservations_as_arrival: [{passenger_count: 50}, {passenger_count: 60}, {passenger_count: 70}] ,"
								+ "reservations_as_departure: [{passenger_count: 150}, {passenger_count: 160}, {passenger_count: 170}]}");
				lOperationSchedule.add(OperationSchedule.parse(j6));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setReservation(Integer iReservation) {

		System.out.println("setReservation " + iReservation);

		lOperationSchedule = new LinkedList<OperationSchedule>();

		String ru1 = "{user: {id: 1, last_name: '名字a', first_name: '名前a'}}";
		String ru2 = "{user: {id: 2, last_name: '名字b', first_name: '名前b'}}";
		String ru3 = "{user: {id: 3, last_name: '名字c', first_name: '名前c'}}";
		String ru4 = "{user: {id: 4, last_name: '名字d', first_name: '名前d'}}";
		String ru5 = "{user: {id: 5, last_name: '名字e', first_name: '名前e'}}";
		String ru6 = "{user: {id: 6, last_name: '名字f', first_name: '名前f'}}";
		String ru7 = "{user: {id: 7, last_name: '名字g', first_name: '名前g'}}";
		String ru8 = "{user: {id: 8, last_name: '名字h', first_name: '名前h'}}";
		String ru9 = "{user: {id: 9, last_name: '名字i', first_name: '名前i'}}";
		String ru10 = "{user: {id: 10, last_name: '名字j', first_name: '名前j'}}";
		String ru11 = "{user: {id: 11, last_name: '名字k', first_name: '名前k'}}";
		String ru12 = "{user: {id: 12, last_name: '名字l', first_name: '名前l'}}";
		String ru13 = "{user: {id: 13, last_name: '名字m', first_name: '名前m'}}";
		String ru14 = "{user: {id: 14, last_name: '名字n', first_name: '名前n'}}";
		String ru15 = "{user: {id: 15, last_name: '名字o', first_name: '名前o'}}";

		String rus1 = "[" + ru1 + "]";
		String rus2 = "[" + ru2 + "]";
		String rus3 = "[" + ru3 + "]";
		String rus4 = "[" + ru4 + "]";
		String rus5 = "[" + ru5 + "]";
		String rus6 = "[" + ru6 + "]";
		String rus7 = "[" + ru7 + "]";
		String rus8 = "[" + ru8 + "]";
		String rus9 = "[" + ru9 + "]";
		String rus10 = "[" + ru10 + "]";
		String rus11 = "[" + ru11 + "]";
		String rus12 = "[" + ru12 + "]";
		String rus13 = "[" + ru13 + "]";
		String rus14 = "[" + ru14 + "]";
		String rus15 = "[" + ru15 + "]";

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
			u1a = "{passenger_count: 1 ,id: 1011,departure_schedule_id: 101,arrival_schedule_id: 106,reservation_users: "
					+ rus1 + ",memo: 'テストメモ1'}";
		}
		if (iReservation > 1) {
			u1b = ", {passenger_count: 2,id: 1012,departure_schedule_id: 101,arrival_schedule_id: 104,reservation_users: "
					+ rus2 + "}";
		}
		if (iReservation > 2) {
			u1c = ", {passenger_count: 3,id: 1013,departure_schedule_id: 101,arrival_schedule_id: 103,reservation_users: "
					+ rus3 + "}";
		}
		if (iReservation > 3) {
			u1d = ", {passenger_count: 4,id: 1014,departure_schedule_id: 101,arrival_schedule_id: 102,reservation_users: "
					+ rus4 + "}";
		}
		if (iReservation > 4) {
			u1e = ", {passenger_count: 5,id: 1015,departure_schedule_id: 101,arrival_schedule_id: 105,reservation_users: "
					+ rus5 + "}";
		}
		if (iReservation > 5) {
			u1f = ", {passenger_count: 6,id: 1016,departure_schedule_id: 101,arrival_schedule_id: 104,reservation_users: "
					+ rus6 + "}";
		}

		u1g = ", {passenger_count: 1,id: 1017,departure_schedule_id: 102,arrival_schedule_id: 103,reservation_users: "
				+ rus7 + "}";
		u1h = ", {passenger_count: 1,id: 1018,departure_schedule_id: 103,arrival_schedule_id: 104,reservation_users: "
				+ rus8 + "}";
		u1i = ", {passenger_count: 1,id: 1019,departure_schedule_id: 104,arrival_schedule_id: 106,reservation_users: "
				+ rus9 + "}";
		u1j = ", {passenger_count: 1,id: 1020,departure_schedule_id: 102,arrival_schedule_id: 105,reservation_users: "
				+ rus10 + "}";
		u1k = ", {passenger_count: 1,id: 1021,departure_schedule_id: 103,arrival_schedule_id: 104,reservation_users: "
				+ rus11 + "}";
		u1l = ", {passenger_count: 1,id: 1022,departure_schedule_id: 102,arrival_schedule_id: 106,reservation_users: "
				+ rus12 + "}";
		u1m = ", {passenger_count: 1,id: 1023,departure_schedule_id: 103,arrival_schedule_id: 105,reservation_users: "
				+ rus13 + "}";
		u1n = ", {passenger_count: 1,id: 1024,departure_schedule_id: 104,arrival_schedule_id: 106,reservation_users: "
				+ rus14 + "}";
		u1o = ", {passenger_count: 1,id: 1025,departure_schedule_id: 104,arrival_schedule_id: 105,reservation_users: "
				+ rus15 + "}";

		try {

			String j1 = new String(
					"{"
							+ "id: 101, "
							+ "arrival_estimate: '2012-01-01T09:00:00+09:00', "
							+ "departure_estimate: '2012-01-01T09:15:00+09:00', "
							+ "platform: {name: 'テストコガソフトウェア前', name_ruby: 'てすとこがそふとうぇあまえ'}, "
							+ "reservations_as_arrival: [], "
							+ "reservations_as_departure: [" + u1a + "," + u1b
							+ "," + u1c + "," + u1d + "," + u1e + "," + u1f
							+ "]}");
			lOperationSchedule.add(OperationSchedule.parse(j1));

			String j2 = new String(
					"{"
							+ "id: 102, "
							+ "arrival_estimate: '2012-01-01T09:30:00+09:00', "
							+ "departure_estimate: '2012-01-01T09:35:00+09:00', "
							+ "platform: {name: 'テスト上野御徒町駅前',name_ruby: 'てすとうえのおかちまちえきまえ'}, "
							+ "reservations_as_arrival: [" + u1d + "], "
							+ "reservations_as_departure: [" + u1g + "," + u1j
							+ "," + u1l + "]}");
			lOperationSchedule.add(OperationSchedule.parse(j2));

			String j3 = new String(
					"{"
							+ "id: 103, "
							+ "arrival_estimate: '2012-01-01T10:30:00+09:00', "
							+ "departure_estimate: '2012-01-01T10:33:00+09:00', "
							+ "platform: {name: 'テスト上野動物園前', name_ruby: 'てすとうえのどうぶつえんまえ'}, "
							+ "reservations_as_arrival: [" + u1c + "," + u1g
							+ "," + "], " + "reservations_as_departure: ["
							+ u1h + "," + u1k + "," + u1m + "]}");
			lOperationSchedule.add(OperationSchedule.parse(j3));

			String j4 = new String(
					"{"
							+ "id: 104, "
							+ "arrival_estimate: '2012-01-01T11:10:00+09:00', "
							+ "departure_estimate: '2012-01-01T11:15:00+09:00', "
							+ "platform: {name: 'テスト上野広小路前', name_ruby: 'てすとうえのひろこうじまえ'}, "
							+ "reservations_as_arrival: [" + u1b + "," + u1f
							+ "," + u1h + "," + u1k + "], "
							+ "reservations_as_departure: [" + u1i + "," + u1n
							+ "," + u1o + "]}");
			lOperationSchedule.add(OperationSchedule.parse(j4));

			String j5 = new String(
					"{"
							+ "id: 105, "
							+ "arrival_estimate: '2012-01-01T12:00:00+09:00', "
							+ "departure_estimate: '2012-01-01T12:05:00+09:00', "
							+ "platform: {name: 'テスト湯島天神前', name_ruby: 'てすとゆしまてんじんまえ'}}"
							+ "reservations_as_arrival: [" + u1e + "," + u1j
							+ "," + u1m + "," + u1o + "], "
							+ "reservations_as_departure: []}");
			lOperationSchedule.add(OperationSchedule.parse(j5));

			String j6 = new String(
					"{"
							+ "id: 106, "
							+ "arrival_estimate: '2012-01-01T13:03:00+09:00', "
							+ "departure_estimate: '2012-01-01T13:10:30+09:00', "
							+ "platform: {name: 'テストＪＲ御徒町駅前', name_ruby: 'てすとじぇいあーるおかちまちえきまえ'}, "
							+ "reservations_as_arrival: [" + u1a + "," + u1i
							+ "," + u1l + "," + u1n + "], "
							+ "reservations_as_departure: []}");
			lOperationSchedule.add(OperationSchedule.parse(j6));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
	}

	@Override
	public int getMapTile(LatLng center, Integer zoom,
			ApiClientCallback<Bitmap> webAPICallback) {
		return 0;
	}

	@Override
	public int cancelGetOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback) {
		return 0;
	}

	@Override
	public int cancelGetOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback) {
		return 0;
	}

	@Override
	public InVehicleDeviceApiClient withSaveOnClose() {
		return this;
	}

	@Override
	public int getOperationSchedules(
			ApiClientCallback<List<OperationSchedule>> callback) {
		callback.onSucceed(0, 200, lOperationSchedule);
		return 0;
	}

	@Override
	public int getVehicleNotifications(
			ApiClientCallback<List<VehicleNotification>> callback) {
		List<VehicleNotification> l = new LinkedList<VehicleNotification>();
		if (NotificationFlag) {
			VehicleNotification n = new VehicleNotification();
			n.setBody("テスト通知が行われました " + new Date());
			l.add(n);
			callback.onSucceed(0, 200, l);
		}
		return 0;
	}

	@Override
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback) {
		return 0;
	}

	@Override
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback) {
		return 0;
	}

	@Override
	public int login(InVehicleDevice login,
			ApiClientCallback<InVehicleDevice> callback) {
		return 0;
	}

	@Override
	public int getServiceProvider(ApiClientCallback<ServiceProvider> callback) {
		callback.onSucceed(0, 200, new ServiceProvider());
		return 0;
	}

	@Override
	public InVehicleDeviceApiClient withSaveOnClose(boolean saveOnClose) {
		return this;
	}

	@Override
	public InVehicleDeviceApiClient withRetry(boolean retry) {
		return this;
	}
}
