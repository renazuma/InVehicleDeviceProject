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
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}

		DateFormat f = new SimpleDateFormat("mm:ss");
		List<ReservationCandidate> l = new LinkedList<ReservationCandidate>();
		try {
			ReservationCandidate c1 = new ReservationCandidate();
			c1.setDepartureTime(f.parse("12:35"));
			c1.setArrivalTime(f.parse("13:34"));
			l.add(c1);

			ReservationCandidate c2 = new ReservationCandidate();
			c2.setDepartureTime(f.parse("15:12"));
			c2.setArrivalTime(f.parse("16:45"));
			l.add(c2);

			ReservationCandidate c3 = new ReservationCandidate();
			c3.setDepartureTime(f.parse("17:39"));
			c3.setArrivalTime(f.parse("18:01"));
			l.add(c3);

			ReservationCandidate c4 = new ReservationCandidate();
			c4.setDepartureTime(f.parse("18:39"));
			c4.setArrivalTime(f.parse("18:41"));
			l.add(c4);

			ReservationCandidate c5 = new ReservationCandidate();
			c5.setDepartureTime(f.parse("19:01"));
			c5.setArrivalTime(f.parse("20:39"));
			l.add(c5);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return l;
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
	public List<OperationSchedule> getOperationSchedules() throws WebAPIException {
		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			String u1 = "user: {id: 1, last_name: '勅使河原', first_name: '恭三郎'}";
			String u2 = "user: {id: 2, last_name: '滝口', first_name: '遥奈'}";
			String u3 = "user: {id: 3, last_name: '下村', first_name: '誠一'}";
			String u4 = "user: {id: 4, last_name: '木本', first_name: '麻紀'}";
			String u5 = "user: {id: 5, last_name: '永瀬', first_name: '直治'}";
			String u6 = "user: {id: 6, last_name: '田川', first_name: '恭三郎'}";

			JSONObject j1 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: 'コガソフトウェア前', name_ruby: 'こがそふとうぇあまえ'}, "
					+ "reservations_as_arrival: ["
					+ "  {id: 1, passenger_count: 5, memo: 'テストメモ1', " + u1 + "}, "
					+ "  {id: 2, passenger_count: 6, " + u2 + "}, "
					+ "  {id: 3, passenger_count: 7, " + u3 + "}],"
					+ "reservations_as_departure: ["
					+ "  {id: 4, passenger_count: 15, " + u4 + "}, "
					+ "  {id: 5, passenger_count: 16, " + u5 + "}, "
					+ "  {id: 6, passenger_count: 17, " + u6 + "}]"
					+ "}");
			l.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: '上野御徒町駅前', name_ruby: 'うえのおかちまちえきまえ'}, "
					+ "reservations_as_arrival: ["
					+ "  {id: 7, passenger_count: 5, memo: 'テストメモ1', " + u1 + "}, "
					+ "  {id: 8, passenger_count: 6, " + u2 + "}, "
					+ "  {id: 9, passenger_count: 7, " + u3 + "}],"
					+ "reservations_as_departure: ["
					+ "  {id: 10, passenger_count: 15, " + u4 + "}, "
					+ "  {id: 11, passenger_count: 16, " + u5 + "}, "
					+ "  {id: 12, passenger_count: 17, " + u6 + "}]"
					+ "}");
			l.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: '上野広小路前', name_ruby: 'うえのひろこうじまえ'}, "
					+ "reservations_as_arrival: ["
					+ "  {id: 13, passenger_count: 5, memo: 'テストメモ1', " + u1 + "}, "
					+ "  {id: 14, passenger_count: 6, " + u2 + "}, "
					+ "  {id: 15, passenger_count: 7, " + u3 + "}],"
					+ "reservations_as_departure: ["
					+ "  {id: 16, passenger_count: 15, " + u4 + "}, "
					+ "  {id: 17, passenger_count: 16, " + u5 + "}, "
					+ "  {id: 18, passenger_count: 17, " + u6 + "}]"
					+ "}");
			l.add(new OperationSchedule(j3));

			JSONObject j4 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: 'ヨドバシアキバ前', name_ruby: 'よどばしあきばまえ'}, "
					+ "reservations_as_arrival: ["
					+ "  {id: 19, passenger_count: 5, memo: 'テストメモ1', " + u1 + "}, "
					+ "  {id: 20, passenger_count: 6, " + u2 + "}, "
					+ "  {id: 21, passenger_count: 7, " + u3 + "}],"
					+ "reservations_as_departure: ["
					+ "  {id: 22, passenger_count: 15, " + u4 + "}, "
					+ "  {id: 23, passenger_count: 16, " + u5 + "}, "
					+ "  {id: 24, passenger_count: 17, " + u6 + "}]"
					+ "}");
			l.add(new OperationSchedule(j4));

			JSONObject j5 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: '上野動物園前', name_ruby: 'うえのどうぶつえんまえ'}, "
					+ "reservations_as_arrival: ["
					+ "  {id: 25, passenger_count: 5, memo: 'テストメモ1', " + u1 + "}, "
					+ "  {id: 26, passenger_count: 6, " + u2 + "}, "
					+ "  {id: 100, passenger_count: 7, " + u3 + "}],"
					+ "reservations_as_departure: ["
					+ "  {id: 101, passenger_count: 15, " + u4 + "}, "
					+ "  {id: 102, passenger_count: 16, " + u5 + "}, "
					+ "  {id: 103, passenger_count: 17, " + u6 + "}]"
					+ "}");
			l.add(new OperationSchedule(j5));

			JSONObject j6 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: '国立科学博物館前', name_ruby: 'こくりつかがくはくぶつかんまえ'}, "
					+ "reservations_as_arrival: ["
					+ "  {id: 104, passenger_count: 5, memo: 'テストメモ1', " + u1 + "}, "
					+ "  {id: 200, passenger_count: 6, " + u2 + "}, "
					+ "  {id: 300, passenger_count: 7, " + u3 + "}],"
					+ "reservations_as_departure: ["
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

	private Date nextNotifyDate = new Date();

	@Override
	public List<VehicleNotification> getVehicleNotifications() throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}

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
	public void putVehicleNotificationReadAt(Integer id, Date readAt) throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}
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
}
