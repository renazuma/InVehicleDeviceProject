package com.kogasoftware.odt.invehicledevice.datasource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

@Deprecated
public class DummyDataSource implements DataSource {

	@Override
	public List<ReservationCandidate> postReservationCandidates(Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId)
					throws WebAPIException {
		DateFormat f = new SimpleDateFormat("mm:ss");
		List<ReservationCandidate> l = new LinkedList<ReservationCandidate>();
		try {
			ReservationCandidate c1 = new ReservationCandidate();
			c1.setArrivalTime(f.parse("12:34"));
			c1.setDepartureTime(f.parse("12:35"));
			l.add(c1);

			ReservationCandidate c2 = new ReservationCandidate();
			c2.setArrivalTime(f.parse("13:45"));
			c2.setDepartureTime(f.parse("15:12"));
			l.add(c2);

			ReservationCandidate c3 = new ReservationCandidate();
			c3.setArrivalTime(f.parse("16:01"));
			c3.setDepartureTime(f.parse("17:39"));
			l.add(c3);

			ReservationCandidate c4 = new ReservationCandidate();
			c4.setArrivalTime(f.parse("18:01"));
			c4.setDepartureTime(f.parse("18:39"));
			l.add(c4);

			ReservationCandidate c5 = new ReservationCandidate();
			c5.setArrivalTime(f.parse("19:01"));
			c5.setDepartureTime(f.parse("20:39"));
			l.add(c5);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return l;
	}

	@Override
	public InVehicleDevice getInVehicleDevice() {
		InVehicleDevice model = new InVehicleDevice();
		model.setId(10);
		model.setTypeNumber("TESTNUMBER012345");
		model.setModelName("MODELNAME67890");
		return model;
	}

	@Override
	public List<OperationSchedule> getOperationSchedules() {
		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			String u1a = "user: {last_name: '名前a', family_name: '名字a'}";
			String u1b = "user: {last_name: '名前b', family_name: '名字b'}";
			String u1c = "user: {last_name: '名前c', family_name: '名字c'}";
			String u1d = "user: {last_name: '名前d', family_name: '名字d'}";
			String u1e = "user: {last_name: '名前e', family_name: '名字e'}";
			String u1f = "user: {last_name: '名前f', family_name: '名字f'}";

			JSONObject j1 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: 'コガソフトウェア前'}, "
					+ "reservations_as_arrival: [{passenger_count: 5, memo: 'テストメモ1', " + u1a
					+ "}, {passenger_count: 6, " + u1b
					+ "}, {passenger_count: 7, " + u1c + "}] ,"
					+ "reservations_as_departure: [{passenger_count: 15, "
					+ u1d + "}, {passenger_count: 16, " + u1e
					+ "}, {passenger_count: 17, " + u1f + "}]}");
			l.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T03:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T04:00:00.000+09:00', "
					+ "platform: {name: '上野御徒町駅前'}, "
					+ "reservations_as_arrival: [{passenger_count: 5, memo: 'テストメモ2'}]}");
			l.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject(
					"{"
							+ "arrival_estimate: '2012-01-01T05:00:00.000+09:00', "
							+ "departure_estimate: '2012-01-01T06:00:00.000+09:00', "
							+ "platform: {name: '上野動物園前'}, "
							+ "reservations_as_departure: [{passenger_count: 5}, {passenger_count: 6}, {passenger_count: 7}]}");
			l.add(new OperationSchedule(j3));

			JSONObject j4 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T07:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T08:00:00.000+09:00', "
					+ "platform: {name: '上野広小路前'}, "
					+ "reservations_as_arrival: [] ,"
					+ "reservations_as_departure: [{passenger_count: 7}]}");
			l.add(new OperationSchedule(j4));

			JSONObject j5 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T09:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T09:01:00.000+09:00', "
					+ "platform: {name: '湯島天神前'}}");
			l.add(new OperationSchedule(j5));

			JSONObject j6 = new JSONObject(
					"{"
							+ "arrival_estimate: '2012-01-01T09:03:00.000+09:00', "
							+ "departure_estimate: '2012-01-01T09:03:30.000+09:00', "
							+ "platform: {name: 'コガソフトウェア前'}, "
							+ "reservations_as_arrival: [{passenger_count: 50}, {passenger_count: 60}, {passenger_count: 70}] ,"
							+ "reservations_as_departure: [{passenger_count: 150}, {passenger_count: 160}, {passenger_count: 170}]}");
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

	private Date nextNotifyDate = new Date();

	@Override
	public List<VehicleNotification> getVehicleNotifications() {
		List<VehicleNotification> l = new LinkedList<VehicleNotification>();
		if (nextNotifyDate.before(new Date())) {
			return l;
		}
		nextNotifyDate = new Date(new Date().getTime() + 6 * 1000);
		VehicleNotification n = new VehicleNotification();
		n.setBody("テスト通知が行われました");
		l.add(n);
		return l;
	}

	@Override
	public void putVehicleNotificationReadAt(Integer id, Date readAt) {
	}

	@Override
	public void putReservationTransferredAt(Integer id, Date transferredAt)
			throws WebAPIException {
	}

	@Override
	public Reservation postReservation(Integer reservationCandidateId)
			throws WebAPIException {

		return new Reservation();
	}
}
