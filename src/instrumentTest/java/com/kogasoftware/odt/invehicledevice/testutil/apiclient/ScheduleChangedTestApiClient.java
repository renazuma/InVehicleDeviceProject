package com.kogasoftware.odt.invehicledevice.testutil.apiclient;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;

import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.EmptyInVehicleDeviceApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class ScheduleChangedTestApiClient extends EmptyInVehicleDeviceApiClient {
	private static final String TAG = ScheduleChangedTestApiClient.class
			.getSimpleName();
	private final AtomicInteger phase = new AtomicInteger(0);

	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			ApiClientCallback<OperationSchedule> callback) {
		Log.w(TAG, "not implemented");
		return 0;
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			ApiClientCallback<OperationSchedule> callback) {
		Log.w(TAG, "not implemented");
		return 0;
	}

	public List<OperationSchedule> getOperationSchedules2()
			throws ApiClientException {
		String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, user: {id: 1, last_name: 'ああああ', first_name: 'いちごう'}}";
		String r2 = "{id: 52, passenger_count: 5, departure_schedule_id: 1, arrival_schedule_id: 2, payment:   0, user: {id: 2, last_name: 'いいいい', first_name: 'にごう'}}";
		String r3 = "{id: 53, passenger_count: 0, departure_schedule_id: 1, arrival_schedule_id: 3, payment: 500, user: {id: 3, last_name: 'うううう', first_name: 'さんごう'}}";
		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			if (phase.compareAndSet(0, 1)) {
				// 変更前のスケジュール
				String j1 = new String(
						"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場A', name_ruby: 'のりおりばえー', latitude: 90, longitude: 45}, "
								+ "reservations_as_departure: ["
								+ r1
								+ ","
								+ r2 + "]}");
				l.add(OperationSchedule.parse(j1));

				String j2 = new String(
						"{id:2, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, "
								+ "reservations_as_arrival: ["
								+ r1
								+ ","
								+ r2
								+ "]}");
				l.add(OperationSchedule.parse(j2));
				return l;
			} else if (phase.compareAndSet(2, 3)) {
				Thread.sleep(10000);
				// 変更後のスケジュール
				String j1 = new String(
						"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場A', name_ruby: 'のりおりばえー', latitude: 43.064615, longitude: 141.346807}, "
								+ "reservations_as_departure: ["
								+ r1
								+ ","
								+ r3
								+ "], operation_record: {arrived_at: '2012-01-01T01:00:01+09:00', departed_at: '2012-01-01T01:00:02+09:00'}}");
				l.add(OperationSchedule.parse(j1));

				String j2 = new String(
						"{id:3, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場C', name_ruby: 'のりおりばしー', latitude: -9.189967, longitude: -75.015152}, reservations_as_arrival: [ "
								+ r3 + " ]}");
				l.add(OperationSchedule.parse(j2));

				String j3 = new String(
						"{id:2, arrival_estimate: '2012-01-01T03:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, "
								+ "reservations_as_arrival: [" + r1 + "]}");
				l.add(OperationSchedule.parse(j3));
				return l;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new ApiClientException("not implemented");

	}

	public List<VehicleNotification> getVehicleNotifications2()
			throws ApiClientException {
		List<VehicleNotification> l = new LinkedList<VehicleNotification>();
		if (phase.compareAndSet(1, 2)) {
			VehicleNotification v = new VehicleNotification();
			v.setId(1);
			v.setBody("運行スケジュールが変更されました");
			v.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
			l.add(v);
			return l;
		} else if (phase.compareAndSet(30, 4)) {
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				return l;
			}
			VehicleNotification v = new VehicleNotification();
			v.setId(2);
			v.setBody("のてぃふぃけーしょん1");
			l.add(v);
			return l;
		} else if (phase.compareAndSet(4, 5)) {
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				return l;
			}
			VehicleNotification v1 = new VehicleNotification();
			v1.setId(3);
			v1.setBody("のてぃふぃけーしょん2");
			l.add(v1);
			VehicleNotification v2 = new VehicleNotification();
			v2.setId(4);
			v2.setBody("のてぃふぃけーしょん3");
			l.add(v2);
			return l;
		}
		return l;
	}

	@Override
	public int responseVehicleNotification(VehicleNotification vn,
			int response, ApiClientCallback<VehicleNotification> callback) {
		Log.w(TAG, "not implemented");
		return 0;
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			ApiClientCallback<ServiceUnitStatusLog> callback) {
		// TODO Auto-generated method stub
		Log.w(TAG, "not implemented");
		return 0;
	}

	@Override
	public void close() {
	}

	@Override
	public int getServiceProvider(ApiClientCallback<ServiceProvider> callback) {
		callback.onSucceed(0, 200, new ServiceProvider());
		return 0;
	}
}
