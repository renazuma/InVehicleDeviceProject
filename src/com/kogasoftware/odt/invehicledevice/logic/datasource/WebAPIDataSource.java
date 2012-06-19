package com.kogasoftware.odt.invehicledevice.logic.datasource;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.common.io.Closeables;
import com.javadocmd.simplelatlng.LatLng;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.webapi.WebAPI;
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

public class WebAPIDataSource implements DataSource {
	private static final String TAG = WebAPIDataSource.class.getSimpleName();
	public static final String DEFAULT_URL = "http://127.0.0.1";
	private final WebAPI api;

	public WebAPIDataSource() {
		api = new WebAPI(DEFAULT_URL);
	}

	public WebAPIDataSource(String url, String token, File file) {
		api = new WebAPI(url, token, file);
	}

	interface WebAPICaller {
		int call() throws JSONException, WebAPIException;
	}

	@Override
	public int arrivalOperationSchedule(final OperationSchedule os,
			final WebAPICallback<OperationSchedule> callback) {
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException, JSONException {
				return api.arrivalOperationSchedule(os, callback);
			}
		}, callback);
	}

	public <T> int callWebAPI(WebAPICaller caller, WebAPICallback<T> callback) {
		try {
			return caller.call();
		} catch (JSONException e) {
			Log.w(TAG, e);
			callback.onException(-1, new WebAPIException(e));
		} catch (WebAPIException e) {
			Log.w(TAG, e);
			callback.onException(-1, e);
		}
		return -1;
	}

	public <T> int callWebAPI(WebAPICaller caller) {
		return callWebAPI(caller, new EmptyWebAPICallback<T>());
	}

	@Override
	public int departureOperationSchedule(final OperationSchedule os,
			final WebAPICallback<OperationSchedule> callback) {
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException, JSONException {
				return api.departureOperationSchedule(os, callback);
			}
		}, callback);
	}

	@Override
	public InVehicleDevice getInVehicleDevice() throws WebAPIException {
		InVehicleDevice model = new InVehicleDevice();
		model.setId(10);
		model.setTypeNumber("TESTNUMBER012345");
		model.setModelName("MODELNAME67890");
		return model;
	}

	@Override
	public int getOffPassenger(final OperationSchedule operationSchedule,
			final Reservation reservation,
			final PassengerRecord passengerRecord,
			final WebAPICallback<PassengerRecord> callback) {
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException, JSONException {
				return api.getOffPassenger(operationSchedule, reservation,
						passengerRecord, callback);
			}
		}, callback);
	}

	@Override
	public int getOnPassenger(final OperationSchedule operationSchedule,
			final Reservation reservation,
			final PassengerRecord passengerRecord,
			final WebAPICallback<PassengerRecord> callback) {
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException, JSONException {
				return api.getOnPassenger(operationSchedule, reservation,
						passengerRecord, callback);
			}
		}, callback);
	}

	@Override
	public List<OperationSchedule> getOperationSchedules() {
		String r1 = "{id: 51, passenger_count: 1, departure_schedule_id: 1, arrival_schedule_id: 2, payment: 100, user: {id: 1, last_name: 'ああああ', first_name: 'いちごう'}}";
		String r2 = "{id: 52, passenger_count: 5, departure_schedule_id: 1, arrival_schedule_id: 2, payment:   0, user: {id: 2, last_name: 'いいいい', first_name: 'にごう'}}";
		String r3 = "{id: 53, passenger_count: 0, departure_schedule_id: 1, arrival_schedule_id: 3, payment: 500, user: {id: 3, last_name: 'うううう', first_name: 'さんごう'}}";
		List<OperationSchedule> l = new LinkedList<OperationSchedule>();
		try {
			// 変更前のスケジュール
			JSONObject j1;
			j1 = new JSONObject(
					"{id:1, arrival_estimate: '2012-01-01T01:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場1', name_ruby: 'のりおりば1', latitude: 34.664887, longitude: 134.092846}, "
							+ "reservations_as_departure: ["
							+ r1
							+ ","
							+ r2
							+ "]}");
			l.add(new OperationSchedule(j1));

			JSONObject j2 = new JSONObject(
					"{id:2, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場2', name_ruby: 'のりおりば2'}, "
							+ "reservations_as_arrival: ["
							+ r1
							+ ","
							+ r2
							+ "]}");
			l.add(new OperationSchedule(j2));

			JSONObject j3 = new JSONObject(
					"{id:3, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場3', name_ruby: 'のりおりば3'}}");
			l.add(new OperationSchedule(j3));
			JSONObject j4 = new JSONObject(
					"{id:4, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場4', name_ruby: 'のりおりば4'}}");
			l.add(new OperationSchedule(j4));
			JSONObject j5 = new JSONObject(
					"{id:5, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場5', name_ruby: 'のりおりば5'}}");
			l.add(new OperationSchedule(j5));
			JSONObject j6 = new JSONObject(
					"{id:6, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場6', name_ruby: 'のりおりば6'}}");
			l.add(new OperationSchedule(j6));
			JSONObject j7 = new JSONObject(
					"{id:7, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場7', name_ruby: 'のりおりば7'}}");
			l.add(new OperationSchedule(j7));
			JSONObject j8 = new JSONObject(
					"{id:8, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場8', name_ruby: 'のりおりば8'}}");
			l.add(new OperationSchedule(j8));
			JSONObject j9 = new JSONObject(
					"{id:9, arrival_estimate: '2012-01-01T02:00:00+09:00', departure_estimate: '2012-01-01T02:00:00+09:00', "
							+ "platform: {name: '乗降場9', name_ruby: 'のりおりば9'}}");
			l.add(new OperationSchedule(j9));
		} catch (JSONException e) {
		}
		return l;
	}

	@Override
	public List<VehicleNotification> getVehicleNotifications() {
		final List<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
		return vehicleNotifications;
	}

	@Override
	public int responseVehicleNotification(final VehicleNotification vn,
			final int response,
			final WebAPICallback<VehicleNotification> callback) {
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException, JSONException {
				return api.responseVehicleNotification(vn, response, callback);
			}
		}, callback);
	}

	@Override
	public int sendServiceUnitStatusLog(
			final ServiceUnitStatusLog serviceUnitStatusLog,
			final WebAPICallback<ServiceUnitStatusLog> callback) {
		return 0;
	}

	@Override
	public void close() {
		Closeables.closeQuietly(api);
	}

	@Override
	public int cancelGetOnPassenger(final OperationSchedule operationSchedule,
			final Reservation reservation,
			final WebAPICallback<PassengerRecord> callback) {
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException, JSONException {
				return api.cancelGetOnPassenger(operationSchedule, reservation,
						callback);
			}
		}, callback);
	}

	@Override
	public int cancelGetOffPassenger(final OperationSchedule operationSchedule,
			final Reservation reservation,
			final WebAPICallback<PassengerRecord> callback) {
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException, JSONException {
				return api.cancelGetOffPassenger(operationSchedule,
						reservation, callback);
			}
		}, callback);
	}

	@Override
	public int searchReservationCandidate(final Demand demand,
			final WebAPICallback<List<ReservationCandidate>> callback) {
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws JSONException, WebAPIException {
				return api.searchReservationCandidate(demand, callback);
			}
		});
	}

	@Override
	public int createReservation(
			final ReservationCandidate reservationCandidate,
			final WebAPICallback<Reservation> callback) {
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws JSONException, WebAPIException {
				return api.createReservation(reservationCandidate, callback);
			}
		});
	}

	@Override
	public void cancel(int reqkey) {
		api.abort(reqkey);
	}

	@Override
	public int getMapTile(LatLng center, Integer zoom,
			WebAPICallback<Bitmap> callback) {
		return api.getMapTile(center, zoom, callback);
	}
}
