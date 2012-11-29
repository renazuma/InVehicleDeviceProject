package com.kogasoftware.odt.invehicledevice.test.util.apiclient;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.kogasoftware.odt.invehicledevice.apiclient.EmptyInVehicleDeviceApiClient;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class DummyApiClient extends EmptyInVehicleDeviceApiClient {

	private Date nextNotifyDate = new Date(new Date().getTime() + 60 * 1000);

	public List<ServiceUnitStatusLog> sendServiceUnitStatusLogArgs = new LinkedList<ServiceUnitStatusLog>();

	public List<OperationSchedule> getOperationSchedules2()
			throws ApiClientException {
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

			String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, reservation_users: "
					+ rus1 + ", memo: 'テストメモ1'}";
			String r2 = "{id: 52, passenger_count: 5, departure_schedule_id: 2, arrival_schedule_id: 3, payment:   0, reservation_users: "
					+ rus2 + "}";
			String r3 = "{id: 53, passenger_count: 3, departure_schedule_id: 1, arrival_schedule_id: 3, payment: 500, reservation_users: "
					+ rus3 + ", memo: 'テストメモ3'}";
			String r4 = "{id: 54, passenger_count: 2, departure_schedule_id: 4, arrival_schedule_id: 5, payment: 500, reservation_users: "
					+ rus4 + "}";
			String r5 = "{id: 55, passenger_count: 1, departure_schedule_id: 4, arrival_schedule_id: 5, payment: 500, reservation_users: "
					+ rus5 + "}";

			String j1 = 
					"{id:1, arrival_estimate: '2012-06-21T09:00:00+09:00', departure_estimate: '2012-06-21T09:15:00+09:00', "
							+ "platform: {name: '乗降場A', name_ruby: 'とくべつようごろうじんほーむあじさいのおかうしまどざいたくかいごしえんせんたーあじさい'}, "
							+ "reservations_as_departure: ["
							+ r1
							+ ","
							+ r3
							+ "]}";
			l.add(OperationSchedule.parse(j1));

			String j2 = new String(
					"{id:2, arrival_estimate: '2012-06-21T09:30:00+09:00', departure_estimate: '2012-06-21T09:35:00+09:00', "
							+ "platform: {name: '乗降場B', name_ruby: 'おかやまけんうしまどよっとはーばーおかやまけんせーりんぐれんめい'}, reservations_as_arrival: [ "
							+ r1
							+ " ], "
							+ "reservations_as_departure: ["
							+ r2
							+ "]" + "}");
			l.add(OperationSchedule.parse(j2));

			String j3 = new String(
					"{id:3, arrival_estimate: '2012-06-21T10:15:00+09:00', departure_estimate: '2012-06-21T10:20:00+09:00', "
							+ "platform: {name: '乗降場C', name_ruby: 'やきにくみらく'}, "
							+ "reservations_as_arrival: ["
							+ r2
							+ ","
							+ r3
							+ "]}");
			l.add(OperationSchedule.parse(j3));

			String j4 = new String(
					"{id:4, arrival_estimate: '2012-06-21T11:00:00+09:00', departure_estimate: '2012-06-21T11:05:00+09:00', "
							+ "platform: {name: '27_横尾入口停留所（尾張方面行き）', name_ruby: 'よこおいりぐちていりゅうじょ（おわりほうめんゆき）'}, "
							+ "reservations_as_departure: ["
							+ r5
							+ ","
							+ r4
							+ "]}");
			l.add(OperationSchedule.parse(j4));

			String j5 = new String(
					"{id:5, arrival_estimate: '2012-06-21T03:00:00+09:00', departure_estimate: '2012-06-21T02:00:00+09:00', "
							+ "platform: {name: '最終乗降場', name_ruby: 'せとうちしやくしょうしまどししょ、せとうちしりつびじゅつかん、せとうちしやくしょきょういくいいんかい'}, "
							+ "reservations_as_arrival: ["
							+ r4
							+ ","
							+ r5
							+ "]}");
			l.add(OperationSchedule.parse(j5));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return l;
	}

	public List<VehicleNotification> getVehicleNotifications2() throws ApiClientException {

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ApiClientException(e);
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
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			ApiClientCallback<ServiceUnitStatusLog> callback) {
		sendServiceUnitStatusLogArgs.add(log);
		return 0;
	}

	@Override
	public int getServiceProvider(ApiClientCallback<ServiceProvider> callback) {
		callback.onSucceed(0, 200, new ServiceProvider());
		return 0;
	}
}
