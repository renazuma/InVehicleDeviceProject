package com.kogasoftware.odt.webapi.test;

import java.util.List;

import org.json.JSONException;

import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPIException;
import com.kogasoftware.odt.webapi.model.Demand;
import com.kogasoftware.odt.webapi.model.Driver;
import com.kogasoftware.odt.webapi.model.InVehicleDevice;
import com.kogasoftware.odt.webapi.model.OperationRecord;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.Operator;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.Platform;
import com.kogasoftware.odt.webapi.model.Reservation;
import com.kogasoftware.odt.webapi.model.ReservationCandidate;
import com.kogasoftware.odt.webapi.model.ServiceUnit;
import com.kogasoftware.odt.webapi.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.webapi.model.User;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class WebTestAPI extends WebAPI {

	protected static final String TEST_SERVER_HOST = "http://192.168.104.63:3333";
	protected static final String TEST_PATH_CLEAN = "/clean";

	protected static final String TEST_PATH_DEMANDS = "/demands";
	protected static final String TEST_PATH_DRIVERS = "/drivers";
	protected static final String TEST_PATH_IN_VEHICLE_DEVICES = "/in_vehicle_devices";
	protected static final String TEST_PATH_OPERATION_RECORDS = "/operation_records";
	protected static final String TEST_PATH_OPERATION_SCHEDULES = "/operation_schedules";
	protected static final String TEST_PATH_OPERATORS = "/operators";
	protected static final String TEST_PATH_PASSENGER_RECORDS = "/passenger_records";
	protected static final String TEST_PATH_PLATFORMS = "/platforms";
	protected static final String TEST_PATH_RESERVATIONS = "/reservations";
	protected static final String TEST_PATH_RESERVATION_CANDIDATES = "/reservation_candidates";
	protected static final String TEST_PATH_SERVICE_UNITS = "/service_units";
	protected static final String TEST_PATH_SERVICE_UNIT_STATUS_LOGS = "/service_unit_status_logs";
	protected static final String TEST_PATH_USERS = "/users";
	protected static final String TEST_PATH_VEHICLE_NOTIFICATIONS = "/vehicle_notifications";

	@Override
	protected String getServerHost() {
		return TEST_SERVER_HOST;
	}

	/**
	 * DatabaseCleaner を呼び出してDBを全クリアする
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int cleanDatabase(WebAPICallback<Void> callback) throws WebAPIException {
		return post(TEST_PATH_CLEAN, null, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}	

	/**
	 * 予約条件をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllDemands(WebAPICallback<List<Demand>> callback) throws WebAPIException {
		return get(TEST_PATH_DEMANDS, callback, new ResponseConverter<List<Demand>>() {
			@Override
			public List<Demand> convert(byte[] rawResponse)
					throws Exception {
				return Demand.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 予約条件をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getDemand(int id, WebAPICallback<Demand> callback) throws WebAPIException {
		return get(TEST_PATH_DEMANDS + "/" + id, callback, new ResponseConverter<Demand>() {
			@Override
			public Demand convert(byte[] rawResponse)
					throws Exception {
				return Demand.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 予約条件を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createDemand(Demand obj, WebAPICallback<Demand> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_DEMANDS, obj.toJSONObject(), callback, new ResponseConverter<Demand>() {
			@Override
			public Demand convert(byte[] rawResponse)
					throws Exception {
				return Demand.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 予約条件をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteDemand(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_DEMANDS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 運転手をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllDrivers(WebAPICallback<List<Driver>> callback) throws WebAPIException {
		return get(TEST_PATH_DRIVERS, callback, new ResponseConverter<List<Driver>>() {
			@Override
			public List<Driver> convert(byte[] rawResponse)
					throws Exception {
				return Driver.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 運転手をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getDriver(int id, WebAPICallback<Driver> callback) throws WebAPIException {
		return get(TEST_PATH_DRIVERS + "/" + id, callback, new ResponseConverter<Driver>() {
			@Override
			public Driver convert(byte[] rawResponse)
					throws Exception {
				return Driver.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 運転手を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createDriver(Driver obj, WebAPICallback<Driver> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_DRIVERS, obj.toJSONObject(), callback, new ResponseConverter<Driver>() {
			@Override
			public Driver convert(byte[] rawResponse)
					throws Exception {
				return Driver.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 運転手をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteDriver(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_DRIVERS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 車載器をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllInVehicleDevices(WebAPICallback<List<InVehicleDevice>> callback) throws WebAPIException {
		return get(TEST_PATH_IN_VEHICLE_DEVICES, callback, new ResponseConverter<List<InVehicleDevice>>() {
			@Override
			public List<InVehicleDevice> convert(byte[] rawResponse)
					throws Exception {
				return InVehicleDevice.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 車載器をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getInVehicleDevice(int id, WebAPICallback<InVehicleDevice> callback) throws WebAPIException {
		return get(TEST_PATH_IN_VEHICLE_DEVICES + "/" + id, callback, new ResponseConverter<InVehicleDevice>() {
			@Override
			public InVehicleDevice convert(byte[] rawResponse)
					throws Exception {
				return InVehicleDevice.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 車載器を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createInVehicleDevice(InVehicleDevice obj, WebAPICallback<InVehicleDevice> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_IN_VEHICLE_DEVICES, obj.toJSONObject(), callback, new ResponseConverter<InVehicleDevice>() {
			@Override
			public InVehicleDevice convert(byte[] rawResponse)
					throws Exception {
				return InVehicleDevice.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 車載器をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteInVehicleDevice(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_IN_VEHICLE_DEVICES + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 運行記録をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllOperationRecords(WebAPICallback<List<OperationRecord>> callback) throws WebAPIException {
		return get(TEST_PATH_OPERATION_RECORDS, callback, new ResponseConverter<List<OperationRecord>>() {
			@Override
			public List<OperationRecord> convert(byte[] rawResponse)
					throws Exception {
				return OperationRecord.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 運行記録をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getOperationRecord(int id, WebAPICallback<OperationRecord> callback) throws WebAPIException {
		return get(TEST_PATH_OPERATION_RECORDS + "/" + id, callback, new ResponseConverter<OperationRecord>() {
			@Override
			public OperationRecord convert(byte[] rawResponse)
					throws Exception {
				return OperationRecord.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 運行記録を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createOperationRecord(OperationRecord obj, WebAPICallback<OperationRecord> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_OPERATION_RECORDS, obj.toJSONObject(), callback, new ResponseConverter<OperationRecord>() {
			@Override
			public OperationRecord convert(byte[] rawResponse)
					throws Exception {
				return OperationRecord.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 運行記録をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteOperationRecord(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_OPERATION_RECORDS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 運行予定をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllOperationSchedules(WebAPICallback<List<OperationSchedule>> callback) throws WebAPIException {
		return get(TEST_PATH_OPERATION_SCHEDULES, callback, new ResponseConverter<List<OperationSchedule>>() {
			@Override
			public List<OperationSchedule> convert(byte[] rawResponse)
					throws Exception {
				return OperationSchedule.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 運行予定をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getOperationSchedule(int id, WebAPICallback<OperationSchedule> callback) throws WebAPIException {
		return get(TEST_PATH_OPERATION_SCHEDULES + "/" + id, callback, new ResponseConverter<OperationSchedule>() {
			@Override
			public OperationSchedule convert(byte[] rawResponse)
					throws Exception {
				return OperationSchedule.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 運行予定を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createOperationSchedule(OperationSchedule obj, WebAPICallback<OperationSchedule> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_OPERATION_SCHEDULES, obj.toJSONObject(), callback, new ResponseConverter<OperationSchedule>() {
			@Override
			public OperationSchedule convert(byte[] rawResponse)
					throws Exception {
				return OperationSchedule.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 運行予定をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteOperationSchedule(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_OPERATION_SCHEDULES + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * オペレーターをすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllOperators(WebAPICallback<List<Operator>> callback) throws WebAPIException {
		return get(TEST_PATH_OPERATORS, callback, new ResponseConverter<List<Operator>>() {
			@Override
			public List<Operator> convert(byte[] rawResponse)
					throws Exception {
				return Operator.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * オペレーターをひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getOperator(int id, WebAPICallback<Operator> callback) throws WebAPIException {
		return get(TEST_PATH_OPERATORS + "/" + id, callback, new ResponseConverter<Operator>() {
			@Override
			public Operator convert(byte[] rawResponse)
					throws Exception {
				return Operator.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * オペレーターを生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createOperator(Operator obj, WebAPICallback<Operator> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_OPERATORS, obj.toJSONObject(), callback, new ResponseConverter<Operator>() {
			@Override
			public Operator convert(byte[] rawResponse)
					throws Exception {
				return Operator.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * オペレーターをひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteOperator(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_OPERATORS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 乗車記録をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllPassengerRecords(WebAPICallback<List<PassengerRecord>> callback) throws WebAPIException {
		return get(TEST_PATH_PASSENGER_RECORDS, callback, new ResponseConverter<List<PassengerRecord>>() {
			@Override
			public List<PassengerRecord> convert(byte[] rawResponse)
					throws Exception {
				return PassengerRecord.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 乗車記録をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getPassengerRecord(int id, WebAPICallback<PassengerRecord> callback) throws WebAPIException {
		return get(TEST_PATH_PASSENGER_RECORDS + "/" + id, callback, new ResponseConverter<PassengerRecord>() {
			@Override
			public PassengerRecord convert(byte[] rawResponse)
					throws Exception {
				return PassengerRecord.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 乗車記録を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createPassengerRecord(PassengerRecord obj, WebAPICallback<PassengerRecord> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_PASSENGER_RECORDS, obj.toJSONObject(), callback, new ResponseConverter<PassengerRecord>() {
			@Override
			public PassengerRecord convert(byte[] rawResponse)
					throws Exception {
				return PassengerRecord.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 乗車記録をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deletePassengerRecord(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_PASSENGER_RECORDS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 乗降場をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllPlatforms(WebAPICallback<List<Platform>> callback) throws WebAPIException {
		return get(TEST_PATH_PLATFORMS, callback, new ResponseConverter<List<Platform>>() {
			@Override
			public List<Platform> convert(byte[] rawResponse)
					throws Exception {
				return Platform.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 乗降場をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getPlatform(int id, WebAPICallback<Platform> callback) throws WebAPIException {
		return get(TEST_PATH_PLATFORMS + "/" + id, callback, new ResponseConverter<Platform>() {
			@Override
			public Platform convert(byte[] rawResponse)
					throws Exception {
				return Platform.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 乗降場を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createPlatform(Platform obj, WebAPICallback<Platform> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_PLATFORMS, obj.toJSONObject(), callback, new ResponseConverter<Platform>() {
			@Override
			public Platform convert(byte[] rawResponse)
					throws Exception {
				return Platform.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 乗降場をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deletePlatform(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_PLATFORMS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 予約をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllReservations(WebAPICallback<List<Reservation>> callback) throws WebAPIException {
		return get(TEST_PATH_RESERVATIONS, callback, new ResponseConverter<List<Reservation>>() {
			@Override
			public List<Reservation> convert(byte[] rawResponse)
					throws Exception {
				return Reservation.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 予約をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getReservation(int id, WebAPICallback<Reservation> callback) throws WebAPIException {
		return get(TEST_PATH_RESERVATIONS + "/" + id, callback, new ResponseConverter<Reservation>() {
			@Override
			public Reservation convert(byte[] rawResponse)
					throws Exception {
				return Reservation.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 予約を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createReservation(Reservation obj, WebAPICallback<Reservation> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_RESERVATIONS, obj.toJSONObject(), callback, new ResponseConverter<Reservation>() {
			@Override
			public Reservation convert(byte[] rawResponse)
					throws Exception {
				return Reservation.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 予約をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteReservation(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_RESERVATIONS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 予約候補をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllReservationCandidates(WebAPICallback<List<ReservationCandidate>> callback) throws WebAPIException {
		return get(TEST_PATH_RESERVATION_CANDIDATES, callback, new ResponseConverter<List<ReservationCandidate>>() {
			@Override
			public List<ReservationCandidate> convert(byte[] rawResponse)
					throws Exception {
				return ReservationCandidate.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 予約候補をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getReservationCandidate(int id, WebAPICallback<ReservationCandidate> callback) throws WebAPIException {
		return get(TEST_PATH_RESERVATION_CANDIDATES + "/" + id, callback, new ResponseConverter<ReservationCandidate>() {
			@Override
			public ReservationCandidate convert(byte[] rawResponse)
					throws Exception {
				return ReservationCandidate.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 予約候補を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createReservationCandidate(ReservationCandidate obj, WebAPICallback<ReservationCandidate> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_RESERVATION_CANDIDATES, obj.toJSONObject(), callback, new ResponseConverter<ReservationCandidate>() {
			@Override
			public ReservationCandidate convert(byte[] rawResponse)
					throws Exception {
				return ReservationCandidate.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 予約候補をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteReservationCandidate(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_RESERVATION_CANDIDATES + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 時点号車をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllServiceUnits(WebAPICallback<List<ServiceUnit>> callback) throws WebAPIException {
		return get(TEST_PATH_SERVICE_UNITS, callback, new ResponseConverter<List<ServiceUnit>>() {
			@Override
			public List<ServiceUnit> convert(byte[] rawResponse)
					throws Exception {
				return ServiceUnit.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 時点号車をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getServiceUnit(int id, WebAPICallback<ServiceUnit> callback) throws WebAPIException {
		return get(TEST_PATH_SERVICE_UNITS + "/" + id, callback, new ResponseConverter<ServiceUnit>() {
			@Override
			public ServiceUnit convert(byte[] rawResponse)
					throws Exception {
				return ServiceUnit.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 時点号車を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createServiceUnit(ServiceUnit obj, WebAPICallback<ServiceUnit> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_SERVICE_UNITS, obj.toJSONObject(), callback, new ResponseConverter<ServiceUnit>() {
			@Override
			public ServiceUnit convert(byte[] rawResponse)
					throws Exception {
				return ServiceUnit.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 時点号車をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteServiceUnit(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_SERVICE_UNITS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 号車ログをすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllServiceUnitStatusLogs(WebAPICallback<List<ServiceUnitStatusLog>> callback) throws WebAPIException {
		return get(TEST_PATH_SERVICE_UNIT_STATUS_LOGS, callback, new ResponseConverter<List<ServiceUnitStatusLog>>() {
			@Override
			public List<ServiceUnitStatusLog> convert(byte[] rawResponse)
					throws Exception {
				return ServiceUnitStatusLog.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 号車ログをひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getServiceUnitStatusLog(int id, WebAPICallback<ServiceUnitStatusLog> callback) throws WebAPIException {
		return get(TEST_PATH_SERVICE_UNIT_STATUS_LOGS + "/" + id, callback, new ResponseConverter<ServiceUnitStatusLog>() {
			@Override
			public ServiceUnitStatusLog convert(byte[] rawResponse)
					throws Exception {
				return ServiceUnitStatusLog.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 号車ログを生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createServiceUnitStatusLog(ServiceUnitStatusLog obj, WebAPICallback<ServiceUnitStatusLog> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_SERVICE_UNIT_STATUS_LOGS, obj.toJSONObject(), callback, new ResponseConverter<ServiceUnitStatusLog>() {
			@Override
			public ServiceUnitStatusLog convert(byte[] rawResponse)
					throws Exception {
				return ServiceUnitStatusLog.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 号車ログをひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteServiceUnitStatusLog(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_SERVICE_UNIT_STATUS_LOGS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * ユーザをすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllUsers(WebAPICallback<List<User>> callback) throws WebAPIException {
		return get(TEST_PATH_USERS, callback, new ResponseConverter<List<User>>() {
			@Override
			public List<User> convert(byte[] rawResponse)
					throws Exception {
				return User.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * ユーザをひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getUser(int id, WebAPICallback<User> callback) throws WebAPIException {
		return get(TEST_PATH_USERS + "/" + id, callback, new ResponseConverter<User>() {
			@Override
			public User convert(byte[] rawResponse)
					throws Exception {
				return User.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * ユーザを生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createUser(User obj, WebAPICallback<User> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_USERS, obj.toJSONObject(), callback, new ResponseConverter<User>() {
			@Override
			public User convert(byte[] rawResponse)
					throws Exception {
				return User.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * ユーザをひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteUser(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_USERS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
	/**
	 * 車載器への通知をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getAllVehicleNotifications(WebAPICallback<List<VehicleNotification>> callback) throws WebAPIException {
		return get(TEST_PATH_VEHICLE_NOTIFICATIONS, callback, new ResponseConverter<List<VehicleNotification>>() {
			@Override
			public List<VehicleNotification> convert(byte[] rawResponse)
					throws Exception {
				return VehicleNotification.parseList(parseJSONArray(rawResponse));
			}
		});
	}

	/**
	 * 車載器への通知をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int getVehicleNotification(int id, WebAPICallback<VehicleNotification> callback) throws WebAPIException {
		return get(TEST_PATH_VEHICLE_NOTIFICATIONS + "/" + id, callback, new ResponseConverter<VehicleNotification>() {
			@Override
			public VehicleNotification convert(byte[] rawResponse)
					throws Exception {
				return VehicleNotification.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 車載器への通知を生成
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 * @throws JSONException 
	 */
	public int createVehicleNotification(VehicleNotification obj, WebAPICallback<VehicleNotification> callback) throws WebAPIException, JSONException {
		return post(TEST_PATH_VEHICLE_NOTIFICATIONS, obj.toJSONObject(), callback, new ResponseConverter<VehicleNotification>() {
			@Override
			public VehicleNotification convert(byte[] rawResponse)
					throws Exception {
				return VehicleNotification.parse(parseJSONObject(rawResponse)).orNull();
			}
		});
	}

	/**
	 * 車載器への通知をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws WebAPIException
	 */
	public int deleteVehicleNotification(int id, WebAPICallback<Void> callback) throws WebAPIException {
		return delete(TEST_PATH_VEHICLE_NOTIFICATIONS + "/" + id, callback, new ResponseConverter<Void>() {
			@Override
			public Void convert(byte[] rawResponse)
					throws Exception {
				return null;
			}
		});
	}
}
