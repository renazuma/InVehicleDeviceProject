package com.kogasoftware.odt.invehicledevice.logic.datasource;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.json.JSONException;

import android.util.Log;

import com.google.common.io.Closeables;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyWebAPICallback;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.WebAPICallback;
import com.kogasoftware.odt.webapi.WebAPIException;
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
			callback.onException(-1, new WebAPIException(true, e));
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
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		final List<OperationSchedule> result = new LinkedList<OperationSchedule>();
		final WebAPICallback<List<OperationSchedule>> callback = new WebAPICallback<List<OperationSchedule>>() {
			@Override
			public void onSucceed(final int reqkey, final int statusCode,
					final List<OperationSchedule> operationSchedules) {
				result.addAll(operationSchedules);
				countDownLatch.countDown();
			}

			@Override
			public void onException(int reqkey, WebAPIException ex) {
				countDownLatch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				countDownLatch.countDown();
			}
		};
		callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException, JSONException {
				return api.getOperationSchedules(callback);
			}
		});
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return result;
	}

	@Override
	public List<VehicleNotification> getVehicleNotifications() {
		final CountDownLatch countDownLatch = new CountDownLatch(1);
		final List<VehicleNotification> vehicleNotifications = new LinkedList<VehicleNotification>();
		final WebAPICallback<List<VehicleNotification>> callback = new WebAPICallback<List<VehicleNotification>>() {
			@Override
			public void onException(int reqkey, WebAPIException ex) {
				countDownLatch.countDown();
			}

			@Override
			public void onFailed(int reqkey, int statusCode, String response) {
				countDownLatch.countDown();
			}

			@Override
			public void onSucceed(int reqkey, int statusCode,
					List<VehicleNotification> result) {
				vehicleNotifications.addAll(result);
				countDownLatch.countDown();
			}
		};
		callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException {
				return api.getVehicleNotifications(callback);
			}
		});
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		return vehicleNotifications;
	}

	@Override
	public Reservation postReservation(Integer reservationCandidateId)
			throws WebAPIException {
		throw new WebAPIException(false, "deprecated");
	}

	@Override
	public List<ReservationCandidate> postReservationCandidates(Integer userId,
			Integer departurePlatformId, Integer arrivalPlatformId)
			throws WebAPIException {
		throw new WebAPIException(false, "deprecated");
	}

	@Override
	public void putReservationTransferredAt(Integer id, Date transferredAt)
			throws WebAPIException {
		throw new WebAPIException(false, "deprecated");
	}

	@Override
	public void putVehicleNotificationReadAt(Integer id, Date readAt)
			throws WebAPIException {
		throw new WebAPIException(false, "deprecated");
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
		return callWebAPI(new WebAPICaller() {
			@Override
			public int call() throws WebAPIException, JSONException {
				return api.sendServiceUnitStatusLog(serviceUnitStatusLog,
						callback);
			}
		}, callback);
	}

	@Override
	public void close() {
		Closeables.closeQuietly(api);
	}

	@Override
	public void cancelGetOnPassenger(Reservation reservation,
			EmptyWebAPICallback<PassengerRecord> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelGetOffPassenger(Reservation reservation,
			EmptyWebAPICallback<PassengerRecord> callback) {
		// TODO Auto-generated method stub

	}
}
