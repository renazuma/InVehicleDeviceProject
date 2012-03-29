package com.kogasoftware.odt.invehicledevice.test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class MockDataSourceTest implements DataSource {

	private List<OperationSchedule> lOperationSchedule = new LinkedList<OperationSchedule>();
	private List<ReservationCandidate> lReservationCandidate = new LinkedList<ReservationCandidate>();

	@Override
	public List<ReservationCandidate> postReservationCandidates(Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId)
					throws WebAPIException {
		return lReservationCandidate;
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
	public List<OperationSchedule> getOperationSchedules() {

		return lOperationSchedule;

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
		//n.setBody("テスト通知が行われました");
		l.add(n);
		return l;
	}

	@Override
	public void putVehicleNotificationReadAt(Integer id, Date readAt) {
	}

	@Override
	public void putReservationTransferredAt(Integer id, Date transferredAt)
			throws WebAPIException {
		// TODO Auto-generated method stub

	}

	public void setOperationSchedules(Integer iOperationScheduleCount) {

		lOperationSchedule = new LinkedList<OperationSchedule>();

		String u1a = "id: 11,user: {last_name: '名前a', family_name: '名字a'},memo: 'テストメモ1'";
		String u1b = "id: 12,user: {last_name: '名前b', family_name: '名字b'},memo: 'テストメモ2'";
		String u1c = "id: 13,user: {last_name: '名前c', family_name: '名字c'}";
		String u1d = "id: 14,user: {last_name: '名前d', family_name: '名字d'}";
		String u1e = "id: 15,user: {last_name: '名前e', family_name: '名字e'}";
		String u1f = "id: 16,user: {last_name: '名前f', family_name: '名字f'}";

		String sOperationSchedule = new String();

		try {

			if (iOperationScheduleCount > 0) {

				sOperationSchedule = "{"
						+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
						+ "platform: {name: 'テストコガソフトウェア前'}, "
						+ "reservations_as_arrival: [{passenger_count: 5, " + u1a + "}, {passenger_count: 6, " + u1b + "}, {passenger_count: 7, " + u1c + "}] ,"
						+ "reservations_as_departure: [{passenger_count: 15, " + u1d + "}, {passenger_count: 16, " + u1e + "}, {passenger_count: 17, " + u1f + "}]}";

				JSONObject j1 = new JSONObject(sOperationSchedule);
				lOperationSchedule.add(new OperationSchedule(j1));
			}

			if (iOperationScheduleCount > 1) {
				JSONObject j2 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T03:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T04:00:00.000+09:00', "
						+ "platform: {name: 'テスト上野御徒町駅前'}, "
						+ "reservations_as_arrival: [{passenger_count: 5}]}");
				lOperationSchedule.add(new OperationSchedule(j2));

			}

			if (iOperationScheduleCount > 2) {
				JSONObject j3 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T05:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T06:00:00.000+09:00', "
						+ "platform: {name: 'テスト上野動物園前'}, "
						+ "reservations_as_departure: [{passenger_count: 5}, {passenger_count: 6}, {passenger_count: 7}]}");
				lOperationSchedule.add(new OperationSchedule(j3));
			}

			if (iOperationScheduleCount > 3) {
				JSONObject j4 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T07:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T08:00:00.000+09:00', "
						+ "platform: {name: 'テスト上野広小路前'}, "
						+ "reservations_as_arrival: [] ,"
						+ "reservations_as_departure: [{passenger_count: 7}]}");
				lOperationSchedule.add(new OperationSchedule(j4));
			}

			if (iOperationScheduleCount > 4) {
				JSONObject j5 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T09:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T09:01:00.000+09:00', "
						+ "platform: {name: 'テスト湯島天神前'}}");
				lOperationSchedule.add(new OperationSchedule(j5));
			}

			if (iOperationScheduleCount > 5) {
				JSONObject j6 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T09:03:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T09:03:30.000+09:00', "
						+ "platform: {name: 'テストＪＲ御徒町駅前'}, "
						+ "reservations_as_arrival: [{passenger_count: 50}, {passenger_count: 60}, {passenger_count: 70}] ,"
						+ "reservations_as_departure: [{passenger_count: 150}, {passenger_count: 160}, {passenger_count: 170}]}");
				lOperationSchedule.add(new OperationSchedule(j6));
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setReservation(Integer iReservation) {

		lOperationSchedule = new LinkedList<OperationSchedule>();

		String u1a = "";
		String u1b = "";
		String u1c = "";
		String u1d = "";
		String u1e = "";
		String u1f = "";

		if (iReservation > 0) {
			u1a = "{passenger_count: 1 ,id: 11,user: {last_name: 'テストa', family_name: '名字a'},memo: 'テストメモ1'}";
		}
		if (iReservation > 1) {
			u1b = ", {passenger_count: 6,id: 12,user: {last_name: 'テストb', family_name: '名字b'}}";
		}
		if (iReservation > 2) {
			u1c = ", {passenger_count: 7 ,id: 13,user: {last_name: 'テストc', family_name: '名字c'},memo: 'テストメモ3'}";
		}
		if (iReservation > 3) {
			u1d = " {passenger_count: 15 ,id: 14,user: {last_name: 'テストd', family_name: '名字d'}}";
		}
		if (iReservation > 4) {
			u1e = ", {passenger_count: 16 ,id: 15,user: {last_name: 'テストe', family_name: '名字e'}}";
		}
		if (iReservation > 5) {
			u1f = ", {passenger_count: 17 ,id: 16,user: {last_name: 'テストf', family_name: '名字f'}}";
		}
		try {

			JSONObject j1 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: 'テストコガソフトウェア前'}, "
					+ "reservations_as_arrival: [" + u1a  + u1b  + u1c + "] ,"
					+ "reservations_as_departure: [ " + u1d + u1e + u1f + "]}");
			lOperationSchedule.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T03:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T04:00:00.000+09:00', "
					+ "platform: {name: 'テスト上野御徒町駅前'}, "
					+ "reservations_as_arrival: [{passenger_count: 5}]}");
			lOperationSchedule.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T05:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T06:00:00.000+09:00', "
					+ "platform: {name: 'テスト上野動物園前'}, "
					+ "reservations_as_departure: [{passenger_count: 5}, {passenger_count: 6}, {passenger_count: 7}]}");
			lOperationSchedule.add(new OperationSchedule(j3));

			JSONObject j4 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T07:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T08:00:00.000+09:00', "
					+ "platform: {name: 'テスト上野広小路前'}, "
					+ "reservations_as_arrival: [] ,"
					+ "reservations_as_departure: [{passenger_count: 7}]}");
			lOperationSchedule.add(new OperationSchedule(j4));

			JSONObject j5 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T09:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T09:01:00.000+09:00', "
					+ "platform: {name: 'テスト湯島天神前'}}");
			lOperationSchedule.add(new OperationSchedule(j5));

			JSONObject j6 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T09:03:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T09:03:30.000+09:00', "
					+ "platform: {name: 'テストＪＲ御徒町駅前'}, "
					+ "reservations_as_arrival: [{passenger_count: 50}, {passenger_count: 60}, {passenger_count: 70}] ,"
					+ "reservations_as_departure: [{passenger_count: 150}, {passenger_count: 160}, {passenger_count: 170}]}");
			lOperationSchedule.add(new OperationSchedule(j6));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setReservationCandidate(Integer iCount,Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId)
					 {
		DateFormat f = new SimpleDateFormat("mm:ss");
		try {
			ReservationCandidate c1 = new ReservationCandidate();
			c1.setDepartureTime(f.parse("12:34"));
			c1.setArrivalTime(f.parse("12:35"));
			lReservationCandidate.add(c1);

			ReservationCandidate c2 = new ReservationCandidate();
			c2.setDepartureTime(f.parse("13:45"));
			c2.setArrivalTime(f.parse("15:12"));
			lReservationCandidate.add(c2);

			ReservationCandidate c3 = new ReservationCandidate();
			c3.setDepartureTime(f.parse("16:01"));
			c3.setArrivalTime(f.parse("17:39"));
			lReservationCandidate.add(c3);

			ReservationCandidate c4 = new ReservationCandidate();
			c4.setDepartureTime(f.parse("18:01"));
			c4.setArrivalTime(f.parse("18:39"));
			lReservationCandidate.add(c4);

			ReservationCandidate c5 = new ReservationCandidate();
			c5.setDepartureTime(f.parse("19:01"));
			c5.setArrivalTime(f.parse("20:39"));
			lReservationCandidate.add(c5);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}


	@Override
	public Reservation postReservation(Integer reservationCandidateId)
			throws WebAPIException {

		return new Reservation();
	}

}
