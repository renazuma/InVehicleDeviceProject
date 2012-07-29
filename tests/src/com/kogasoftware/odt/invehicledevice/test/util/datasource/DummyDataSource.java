package com.kogasoftware.odt.invehicledevice.test.util.datasource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.invehicledevice.datasource.EmptyDataSource;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceProvider;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class DummyDataSource extends EmptyDataSource {

	private Date nextNotifyDate = new Date(new Date().getTime() + 60 * 1000);

	public List<ServiceUnitStatusLog> sendServiceUnitStatusLogArgs = new LinkedList<ServiceUnitStatusLog>();

	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		return 0;
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public InVehicleDevice getInVehicleDevice() throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(e);
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
			throw new WebAPIException(e);
		}

		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			String ru1 = "{user: {id: 1, last_name: 'ラストネーム', first_name: 'ファーストネーム', passenger_records: [{departure_operation_schedule_id: 1, get_on_time: '2000-01-01', updated_at: '2030-01-01'}, {updated_at: '1999-01-01'}]}}";
			String ru2 = "{user: {id: 2, last_name: '山田', first_name: '太郎'}}";
			String ru3 = "{user: {id: 3, last_name: '山口', first_name: '二郎'}}";
			String ru4 = "{user: {id: 4, last_name: '川田', first_name: 'さぶろう'}}";
			String ru5 = "{user: {id: 5, last_name: '川口', first_name: 'しろう'}}";
			String ru6 = "{user: {id: 6, last_name: '田口', first_name: 'ろくろう'}}";
			String ru7 = "{user: {id: 7, last_name: '田川', first_name: 'ななろう'}}";

			String rus1 = "[" + ru1 + ", " + ru2 + "]";
			String rus2 = "[" + ru3 + "]";
			String rus3 = "[" + ru4 + ", " + ru5 + ", " + ru6 + "]";
			String rus4 = "[]";
			String rus5 = "[" + ru7 + "]";

			String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, reservation_users: " + rus1 + ", memo: 'テストメモ1'}";
			String r2 = "{id: 52, passenger_count: 5, departure_schedule_id: 2, arrival_schedule_id: 3, payment:   0, reservation_users: " + rus2 + "}";
			String r3 = "{id: 53, passenger_count: 3, departure_schedule_id: 1, arrival_schedule_id: 3, payment: 500, reservation_users: " + rus3 + ", memo: 'テストメモ3'}";
			String r4 = "{id: 54, passenger_count: 2, departure_schedule_id: 4, arrival_schedule_id: 5, payment: 500, reservation_users: " + rus4 + "}";
			String r5 = "{id: 55, passenger_count: 1, departure_schedule_id: 4, arrival_schedule_id: 5, payment: 500, reservation_users: " + rus5 + "}";
			
			JSONObject j1 = new JSONObject(
					"{id:1, arrival_estimate: '2012-06-21T09:00:00+09:00', departure_estimate: '2012-06-21T09:15:00+09:00', "
							+ "platform: {name: '乗降場A', name_ruby: 'とくべつようごろうじんほーむあじさいのおかうしまどざいたくかいごしえんせんたーあじさい'}, "
							+ "reservations_as_departure: ["
							+ r1
							+ ","
							+ r3
							+ "]}");
			l.add(OperationSchedule.parse(j1));

			JSONObject j2 = new JSONObject(
					"{id:2, arrival_estimate: '2012-06-21T09:30:00+09:00', departure_estimate: '2012-06-21T09:35:00+09:00', "
							+ "platform: {name: '乗降場B', name_ruby: 'おかやまけんうしまどよっとはーばーおかやまけんせーりんぐれんめい'}, reservations_as_arrival: [ "
							+ r1
							+ " ], "
							+ "reservations_as_departure: ["
							+ r2
							+ "]" + "}");
			l.add(OperationSchedule.parse(j2));

			JSONObject j3 = new JSONObject(
					"{id:3, arrival_estimate: '2012-06-21T10:15:00+09:00', departure_estimate: '2012-06-21T10:20:00+09:00', "
							+ "platform: {name: '乗降場C', name_ruby: 'やきにくみらく'}, "
							+ "reservations_as_arrival: ["
							+ r2
							+ ","
							+ r3
							+ "]}");
			l.add(OperationSchedule.parse(j3));

			JSONObject j4 = new JSONObject(
					"{id:4, arrival_estimate: '2012-06-21T11:00:00+09:00', departure_estimate: '2012-06-21T11:05:00+09:00', "
							+ "platform: {name: '27_横尾入口停留所（尾張方面行き）', name_ruby: 'よこおいりぐちていりゅうじょ（おわりほうめんゆき）'}, "
							+ "reservations_as_departure: ["
							+ r5
							+ ","
							+ r4
							+ "]}");
			l.add(OperationSchedule.parse(j4));

			JSONObject j5 = new JSONObject(
					"{id:5, arrival_estimate: '2012-06-21T03:00:00+09:00', departure_estimate: '2012-06-21T02:00:00+09:00', "
							+ "platform: {name: '最終乗降場', name_ruby: 'せとうちしやくしょうしまどししょ、せとうちしりつびじゅつかん、せとうちしやくしょきょういくいいんかい'}, "
							+ "reservations_as_arrival: ["
							+ r4
							+ ","
							+ r5
							+ "]}");
			l.add(OperationSchedule.parse(j5));

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return l;
	}

	@Override
	public List<VehicleNotification> getVehicleNotifications()

	throws WebAPIException {

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(e);
		}

		List<VehicleNotification> l = new LinkedList<VehicleNotification>();
		if (nextNotifyDate.after(new Date())) {
			return l;
		}
		nextNotifyDate = new Date(new Date().getTime() + 10 * 1000);
		VehicleNotification n = new VehicleNotification();
		n.setBody("テスト通知が行われました " + new Date());
		// l.add(n);
		return l;

	}

	@Override
	public int responseVehicleNotification(VehicleNotification vn,
			int response, WebAPICallback<VehicleNotification> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback) {
		sendServiceUnitStatusLogArgs.add(log);
		return 0;
	}

	@Override
	public void close() {
	}

	@Override
	public int searchReservationCandidate(Demand demand,
			WebAPICallback<List<ReservationCandidate>> callback) {

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
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
		callback.onSucceed(0, 200, l);
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
	public int getServiceProvider(WebAPICallback<ServiceProvider> callback) {
		callback.onSucceed(0, 200, new ServiceProvider());
		return 0;
	}
}
