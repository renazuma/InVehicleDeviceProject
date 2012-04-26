package com.kogasoftware.odt.invehicledevice.datasource;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.invehicledevice.CommonLogic;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class ScheduleChangedTestDataSource implements DataSource {
	private final AtomicInteger phase = new AtomicInteger(0);

	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) throws WebAPIException {
		throw new WebAPIException(true, "not implemented");
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) throws WebAPIException {
		throw new WebAPIException(true, "not implemented");
	}

	@Override
	public InVehicleDevice getInVehicleDevice() throws WebAPIException {
		throw new WebAPIException(true, "not implemented");
	}

	@Override
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) throws WebAPIException {
		throw new WebAPIException(true, "not implemented");
	}

	@Override
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) throws WebAPIException {
		throw new WebAPIException(true, "not implemented");
	}

	@Override
	public List<OperationSchedule> getOperationSchedules()
			throws WebAPIException {
		String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, user: {id: 1, last_name: 'ああああ', first_name: 'いちごう'}}";
		String r2 = "{id: 52, passenger_count: 5, departure_schedule_id: 1, arrival_schedule_id: 2, payment:   0, user: {id: 2, last_name: 'いいいい', first_name: 'にごう'}}";
		String r3 = "{id: 53, passenger_count: 0, departure_schedule_id: 1, arrival_schedule_id: 3, payment: 500, user: {id: 3, last_name: 'うううう', first_name: 'さんごう'}}";
		String r4 = "{id: 54, passenger_count: 5, departure_schedule_id: 1, arrival_schedule_id: 2, payment:   0, user: {id: 4, last_name: '木本', first_name: '麻紀'}}";
		String r5 = "{id: 55, departure_schedule_id: 1, arrival_schedule_id: 2, payment:   0, user: {id: 5, last_name: '永瀬', first_name: '直治'}}";
		String r6 = "{id: 56, departure_schedule_id: 1, arrival_schedule_id: 2, payment:   0, user: {id: 6, last_name: '田川', first_name: '恭三郎'}}";

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
				// 変更後のスケジュール
				JSONObject j1 = new JSONObject(
						"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
								+ "platform: {name: '乗降場A', name_ruby: 'のりおりばえー'}, "
								+ "reservations_as_departure: ["
								+ r1
								+ ","
								+ r3 + "]}");
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
		}

		throw new WebAPIException(true, "not implemented");

	}

	@Override
	public List<VehicleNotification> getVehicleNotifications()
			throws WebAPIException {
		List<VehicleNotification> l = new LinkedList<VehicleNotification>();
		if (phase.compareAndSet(1, 2)) {
			VehicleNotification v = new VehicleNotification();
			v.setBody("schedule changed");
			v.setNotificationType(CommonLogic.VEHICLE_NOTIFICATION_TYPE_SCHEDULE_CHANGED);
			l.add(v);
			return l;
		}
		return l;
	}

	@Override
	public Reservation postReservation(Integer reservationCandidateId)
			throws WebAPIException {

		throw new WebAPIException(true, "not implemented");
	}

	@Override
	public List<ReservationCandidate> postReservationCandidates(Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId)
			throws WebAPIException {
		throw new WebAPIException(true, "not implemented");
	}

	@Override
	public void putReservationTransferredAt(Integer id, Date transferredAt)
			throws WebAPIException {
	}

	@Override
	public void putVehicleNotificationReadAt(Integer id, Date readAt)
			throws WebAPIException {
	}

	@Override
	public int responseVehicleNotification(VehicleNotification vn,
			int response, WebAPICallback<VehicleNotification> callback)
			throws WebAPIException {
		throw new WebAPIException(true, "not implemented");
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback)
			throws WebAPIException, JSONException {
		// TODO Auto-generated method stub
		throw new WebAPIException(true, "not implemented");
	}
}
