package com.kogasoftware.odt.invehicledevice.test;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.invehicledevice.datasource.DataSource;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class MockDataSourceTest implements DataSource {

	private List<OperationSchedule> l = new LinkedList<OperationSchedule>();

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
//		n.setBody("テスト通知が行われました");
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

	public void setOperationSchedules(Integer i) {

		l = new LinkedList<OperationSchedule>();

		String u1a = "user: {last_name: '名前a', family_name: '名字a'}";
		String u1b = "user: {last_name: '名前b', family_name: '名字b'}";
		String u1c = "user: {last_name: '名前c', family_name: '名字c'}";
		String u1d = "user: {last_name: '名前d', family_name: '名字d'}";
		String u1e = "user: {last_name: '名前e', family_name: '名字e'}";
		String u1f = "user: {last_name: '名前f', family_name: '名字f'}";
		
		System.out.println("Index:::: " + i);

		try {

			if (i > 0) {
				JSONObject j1 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
						+ "platform: {name: 'テストコガソフトウェア前'}, "
						+ "reservations_as_arrival: [{passenger_count: 5, " + u1a + "}, {passenger_count: 6, " + u1b + "}, {passenger_count: 7, " + u1c + "}] ,"
						+ "reservations_as_departure: [{passenger_count: 15, " + u1d + "}, {passenger_count: 16, " + u1e + "}, {passenger_count: 17, " + u1f + "}]}");
				l.add(new OperationSchedule(j1));
			}

			if (i > 1) {
				JSONObject j2 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T03:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T04:00:00.000+09:00', "
						+ "platform: {name: 'テスト上野御徒町駅前'}, "
						+ "reservations_as_arrival: [{passenger_count: 5}]}");
				l.add(new OperationSchedule(j2));

			}

			if (i > 2) {
				JSONObject j3 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T05:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T06:00:00.000+09:00', "
						+ "platform: {name: 'テスト上野動物園前'}, "
						+ "reservations_as_departure: [{passenger_count: 5}, {passenger_count: 6}, {passenger_count: 7}]}");
				l.add(new OperationSchedule(j3));
			}

			if (i > 3) {
				JSONObject j4 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T07:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T08:00:00.000+09:00', "
						+ "platform: {name: 'テスト上野広小路前'}, "
						+ "reservations_as_arrival: [] ,"
						+ "reservations_as_departure: [{passenger_count: 7}]}");
				l.add(new OperationSchedule(j4));
			}

			if (i > 4) {
				JSONObject j5 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T09:00:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T09:01:00.000+09:00', "
						+ "platform: {name: 'テスト湯島天神前'}}");
				l.add(new OperationSchedule(j5));
			}

			if (i > 5) {
				JSONObject j6 = new JSONObject("{"
						+ "arrival_estimate: '2012-01-01T09:03:00.000+09:00', "
						+ "departure_estimate: '2012-01-01T09:03:30.000+09:00', "
						+ "platform: {name: 'テストＪＲ御徒町駅前'}, "
						+ "reservations_as_arrival: [{passenger_count: 50}, {passenger_count: 60}, {passenger_count: 70}] ,"
						+ "reservations_as_departure: [{passenger_count: 150}, {passenger_count: 160}, {passenger_count: 170}]}");
				l.add(new OperationSchedule(j6));
			}
			
			System.out.println("Count:::: " + l.size());

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
