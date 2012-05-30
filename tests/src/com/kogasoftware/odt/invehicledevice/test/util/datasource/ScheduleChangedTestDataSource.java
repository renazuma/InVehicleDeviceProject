package com.kogasoftware.odt.invehicledevice.test.util.datasource;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;
import com.kogasoftware.odt.webapi.model.VehicleNotifications;

public class ScheduleChangedTestDataSource implements DataSource {
	private static final String TAG = ScheduleChangedTestDataSource.class
			.getSimpleName();
	private final AtomicInteger phase = new AtomicInteger(0);

	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		Log.w(TAG, "not implemented");
		return 0;
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) {
		Log.w(TAG, "not implemented");
		return 0;
	}

	@Override
	public InVehicleDevice getInVehicleDevice() throws WebAPIException {
		throw new WebAPIException("not implemented");
	}

	@Override
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		Log.w(TAG, "not implemented");
		return 0;
	}

	@Override
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) {
		Log.w(TAG, "not implemented");
		return 0;
	}

	@Override
	public List<OperationSchedule> getOperationSchedules()
			throws WebAPIException {
		String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, user: {id: 1, last_name: 'ああああ', first_name: 'いちごう'}}";
		String r2 = "{id: 52, passenger_count: 5, departure_schedule_id: 1, arrival_schedule_id: 2, payment:   0, user: {id: 2, last_name: 'いいいい', first_name: 'にごう'}}";
		String r3 = "{id: 53, passenger_count: 0, departure_schedule_id: 1, arrival_schedule_id: 3, payment: 500, user: {id: 3, last_name: 'うううう', first_name: 'さんごう'}}";
		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			if (phase.compareAndSet(0, 1)) {
				// 変更前のスケジュール
				JSONObject j1 = new JSONObject(
						"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場A', name_ruby: 'のりおりばえー'}, "
								+ "reservations_as_departure: ["
								+ r1
								+ ","
								+ r2 + "]}");
				l.add(new OperationSchedule(j1));

				JSONObject j2 = new JSONObject(
						"{id:2, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, "
								+ "reservations_as_arrival: ["
								+ r1
								+ ","
								+ r2
								+ "]}");
				l.add(new OperationSchedule(j2));
				return l;
			} else if (phase.compareAndSet(2, 3)) {
				Thread.sleep(10000);
				// 変更後のスケジュール
				JSONObject j1 = new JSONObject(
						"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場A', name_ruby: 'のりおりばえー'}, "
								+ "reservations_as_departure: ["
								+ r1
								+ ","
								+ r3 + "], operation_record: {arrived_at: '2012-01-01T01:00:01+09:00', departed_at: '2012-01-01T01:00:02+09:00'}}");
				l.add(new OperationSchedule(j1));

				JSONObject j2 = new JSONObject(
						"{id:3, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場C', name_ruby: 'のりおりばしー'}, reservations_as_arrival: [ "
								+ r3 + " ]}");
				l.add(new OperationSchedule(j2));

				JSONObject j3 = new JSONObject(
						"{id:2, arrival_estimate: '2012-01-01T03:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, "
								+ "reservations_as_arrival: [" + r1 + "]}");
				l.add(new OperationSchedule(j3));
				return l;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		throw new WebAPIException("not implemented");

	}

	@Override
	public List<VehicleNotification> getVehicleNotifications()
			throws WebAPIException {
		List<VehicleNotification> l = new LinkedList<VehicleNotification>();
		if (phase.compareAndSet(1, 2)) {
			VehicleNotification v = new VehicleNotification();
			v.setId(1);
			v.setBody("運行スケジュールが変更されました");
			v.setNotificationKind(VehicleNotifications.NotificationKind.SCHEDULE_CHANGED);
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
			int response, WebAPICallback<VehicleNotification> callback) {
		Log.w(TAG, "not implemented");
		return 0;
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback) {
		// TODO Auto-generated method stub
		Log.w(TAG, "not implemented");
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int createReservation(ReservationCandidate reservationCandidate,
			WebAPICallback<Reservation> callback) {
		// TODO Auto-generated method stub
		return 0;
	}

}
