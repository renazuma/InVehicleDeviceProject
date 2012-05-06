package com.kogasoftware.odt.invehicledevice.test.util.datasource;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.kogasoftware.odt.invehicledevice.logic.datasource.DataSource;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class DummyDataSource implements DataSource {

	private Date nextNotifyDate = new Date(new Date().getTime() + 60 * 1000);

	public List<ServiceUnitStatusLog> sendServiceUnitStatusLogArgs = new LinkedList<ServiceUnitStatusLog>();

	@Override
	public int arrivalOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) throws WebAPIException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int departureOperationSchedule(OperationSchedule os,
			WebAPICallback<OperationSchedule> callback) throws WebAPIException {
		// TODO Auto-generated method stub
		return 0;
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
	public int getOffPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) throws WebAPIException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOnPassenger(OperationSchedule operationSchedule,
			Reservation reservation, PassengerRecord passengerRecord,
			WebAPICallback<PassengerRecord> callback) throws WebAPIException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<OperationSchedule> getOperationSchedules()
			throws WebAPIException {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}

		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, user: {id: 1, last_name: 'ああああ', first_name: 'いちごう'}}";
			String r2 = "{id: 52, passenger_count: 5, departure_schedule_id: 2, arrival_schedule_id: 3, payment:   0, user: {id: 2, last_name: 'いいいい', first_name: 'にごう'}}";
			String r3 = "{id: 53, passenger_count: 0, departure_schedule_id: 1, arrival_schedule_id: 3, payment: 500, user: {id: 3, last_name: 'うううう', first_name: 'さんごう'}}";

			JSONObject j1 = new JSONObject(
					"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場A', name_ruby: 'のりおりばえー'}, "
							+ "reservations_as_departure: ["
							+ r1
							+ ","
							+ r3
							+ "]}");
			l.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject(
					"{id:2, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場C', name_ruby: 'のりおりばしー'}, reservations_as_arrival: [ "
							+ r1
							+ " ], "
							+ "reservations_as_departure: ["
							+ r2
							+ "]" + "}");
			l.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject(
					"{id:3, arrival_estimate: '2012-01-01T03:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, "
							+ "reservations_as_arrival: ["
							+ r2
							+ ","
							+ r3
							+ "]}");
			l.add(new OperationSchedule(j3));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			throw new WebAPIException(false, e);
		}

		List<VehicleNotification> l = new LinkedList<VehicleNotification>();
		if (nextNotifyDate.after(new Date())) {
			return l;
		}
		nextNotifyDate = new Date(new Date().getTime() + 10 * 1000);
		VehicleNotification n = new VehicleNotification();
		n.setBody("テスト通知が行われました " + new Date());
		// l.add(n); // TODO
		return l;
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
		return l;
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
	public void putVehicleNotificationReadAt(Integer id, Date readAt)
			throws WebAPIException {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new WebAPIException(false, e);
		}
	}

	@Override
	public int responseVehicleNotification(VehicleNotification vn,
			int response, WebAPICallback<VehicleNotification> callback)
			throws WebAPIException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int sendServiceUnitStatusLog(ServiceUnitStatusLog log,
			WebAPICallback<ServiceUnitStatusLog> callback)
			throws WebAPIException, JSONException {
		sendServiceUnitStatusLogArgs.add(log);
		return 0;
	}

}
