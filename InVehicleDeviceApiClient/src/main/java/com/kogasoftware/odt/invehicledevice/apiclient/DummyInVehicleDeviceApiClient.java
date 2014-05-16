package com.kogasoftware.odt.invehicledevice.apiclient;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;

public class DummyInVehicleDeviceApiClient extends
		EmptyInVehicleDeviceApiClient {
	// private final AtomicInteger state = new AtomicInteger(0);
	private final Random random = new Random();

	@Override
	public int getServiceProvider(ApiClientCallback<ServiceProvider> callback) {
		callback.onSucceed(0, 200, new ServiceProvider());
		return 0;
	}

	@Override
	public int getVehicleNotifications(
			ApiClientCallback<List<VehicleNotification>> callback) {
		// if (state.compareAndSet(0, 1)) {
		// callback.onFailed(0, 400, "");
		// return 0;
		// }
		try {
			Thread.sleep(20 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		VehicleNotification vn = new VehicleNotification();
		vn.setId(random.nextInt(1000000));
		vn.setNotificationKind(VehicleNotification.NotificationKind.RESERVATION_CHANGED);
		// vn.setNotificationKind(VehicleNotification.NotificationKind.FROM_OPERATOR);
		vn.setBody("a");
		callback.onSucceed(0, 200, Lists.newArrayList(vn));
		return 0;
	}

	@Override
	public int getOffPassenger(UnmergedOperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback) {
		callback.onSucceed(0, 200, null);
		return 0;
	}

	@Override
	public int getOnPassenger(UnmergedOperationSchedule operationSchedule,
			Reservation reservation, User user,
			PassengerRecord passengerRecord, ApiClientCallback<Void> callback) {
		callback.onSucceed(0, 200, null);
		return 0;
	}

	@Override
	public int cancelGetOffPassenger(UnmergedOperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback) {
		callback.onSucceed(0, 200, null);
		return 0;
	}

	@Override
	public int cancelGetOnPassenger(UnmergedOperationSchedule operationSchedule,
			Reservation reservation, User user, ApiClientCallback<Void> callback) {
		callback.onSucceed(0, 200, null);
		return 0;
	}

	@Override
	public int departureOperationSchedule(UnmergedOperationSchedule os,
			ApiClientCallback<UnmergedOperationSchedule> callback) {
		callback.onSucceed(0, 200, new UnmergedOperationSchedule());
		return 0;
	}

	@Override
	public int arrivalOperationSchedule(UnmergedOperationSchedule os,
			ApiClientCallback<UnmergedOperationSchedule> callback) {
		callback.onSucceed(0, 200, new UnmergedOperationSchedule());
		return 0;
	}

	@Override
	public int getOperationSchedules(
			ApiClientCallback<List<UnmergedOperationSchedule>> callback) {
		OperationRecord unhandled = new OperationRecord();
		OperationRecord arrived = new OperationRecord();
		arrived.setArrivedAt(new Date());
		OperationRecord departed = new OperationRecord();
		departed.setArrivedAt(new Date());
		departed.setDepartedAt(new Date());

		String u1 = "{id: 1, last_name: '山川', first_name: 'いちろう', memo: 'いちろうのめもめも'}";
		String u2 = "{id: 2, last_name: '山田', first_name: '次郎', memo: 'じろうのめもめも'}";
		String u3 = "{id: 3, last_name: '山口', first_name: 'じゅげむじゅげむじゅげむじゅげむ'}";
		String u4 = "{id: 4, last_name: '川田', first_name: '四郎'}";
		String u5 = "{id: 5, last_name: '川口', first_name: 'ごろう'}";
		String u6 = "{id: 6, last_name: '田口', first_name: 'ろくろう'}";
		String u7 = "{id: 7, last_name: '田川', first_name: 'ななろう'}";

		String pr1 = "{id: 201, user_id: 1, passenger_count: 10}";
		String pr2 = "{id: 202, user_id: 2, passenger_count: 20}";
		String pr3 = "{id: 203, user_id: 3, passenger_count: 30}";
		String pr4 = "{id: 204, user_id: 4, passenger_count: 40}";
		String pr5 = "{id: 205, user_id: 5, passenger_count: 50}";
		String pr6 = "{id: 206, user_id: 6, passenger_count: 60}";
		String pr7 = "{id: 207, user_id: 7, passenger_count: 70}";

		String r1 = String
				.format("{id: 101, departure_schedule_id: 1, arrival_schedule_id: 2, fellow_users: [%s], passenger_records: [%s]}",
						u1 + "," + u2, pr1 + "," + pr2);
		String r2 = String
				.format("{id: 102, departure_schedule_id: 1, arrival_schedule_id: 3, fellow_users: [%s], passenger_records: [%s]}",
						u3, pr3);
		String r3 = String
				.format("{id: 103, departure_schedule_id: 2, arrival_schedule_id: 3, fellow_users: [%s], passenger_records: [%s]}",
						u4 + "," + u5, pr4 + "," + pr5);
		String r4 = String
				.format("{id: 104, departure_schedule_id: 2, arrival_schedule_id: 3, fellow_users: [%s], passenger_records: [%s]}",
						u6 + "," + u7, pr6 + "," + pr7);

		String os1 = "{id:1, arrival_estimate: '2012-10-13T01:00:00+09:00', departure_estimate: '2012-10-13T02:00:00+09:00', "
				+ "platform: {name: '乗降場A123456789あいうえおかきくけこさしすせそたちつてとなにぬねの', name_ruby: 'のりおりばえー', latitude: 35.787996, longitude: 139.27583, memo: 'のりおりばめも'}, "
				+ "reservations_as_departure: [" + r1 + "," + r2 + "]}";
		String os2 = "{id:2, arrival_estimate: '2012-10-13T02:00:00+09:00', departure_estimate: '2012-10-13T02:00:00+09:00', "
				+ "platform: {name: '乗降場B', name_ruby: 'のりおりばびー'}, "
				+ "reservations_as_departure: [" + r3 + "," + r4 + "]}";
		String os3 = "{id:3, arrival_estimate: '2012-10-13T02:00:00+09:00', departure_estimate: '2012-10-13T02:00:00+09:00', "
				+ "platform: {name: '乗降場C', name_ruby: 'のりおりばしー'}}";

		List<UnmergedOperationSchedule> l = new LinkedList<UnmergedOperationSchedule>();

		try {
			l.add(UnmergedOperationSchedule.parse(os1));
			l.add(UnmergedOperationSchedule.parse(os2));
			l.add(UnmergedOperationSchedule.parse(os3));

			l.get(0).setOperationRecord(unhandled.clone());
			l.get(1).setOperationRecord(unhandled.clone());
			l.get(2).setOperationRecord(unhandled.clone());

			callback.onSucceed(0, 200, l);
		} catch (IOException e) {
			callback.onException(0, new ApiClientException(e));
		}
		return 0;
	}
}
