package com.kogasoftware.odt.invehicledevice.datasource;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class MockDataSource implements DataSource {

	@Override
	public InVehicleDevice getInVehicleDevice() {
		InVehicleDevice model = new InVehicleDevice();
		model.setId(10L);
		model.setTypeNumber("TYPENUMBER012345");
		model.setModelName("MODELNAME67890");
		return model;
	}

	@Override
	public List<OperationSchedule> getOperationSchedules() {
		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			JSONObject j1 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T01:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T02:00:00.000+09:00', "
					+ "platform: {name: 'コガソフトウェア前'}, "
					+ "reservations_as_arrival: [{passenger_count: 5}, {passenger_count: 6}, {passenger_count: 7}] ,"
					+ "reservations_as_departure: [{passenger_count: 15}, {passenger_count: 16}, {passenger_count: 17}]}");
			l.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject("{"
					+ "arrival_estimate: '2012-01-01T03:00:00.000+09:00', "
					+ "departure_estimate: '2012-01-01T04:00:00.000+09:00', "
					+ "platform: {name: '上野御徒町駅前'}, "
					+ "reservations_as_arrival: [{passenger_count: 5}]}");
			l.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject("{"
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

			JSONObject j6 = new JSONObject("{"
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
	public void putVehicleNotificationReadAt(Long id, Date readAt) {
	}
}
