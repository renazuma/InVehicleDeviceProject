package com.kogasoftware.odt.invehicledevice.testapiclient;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.ApiClientCallback;
import com.kogasoftware.odt.apiclient.ApiClientException;
import com.kogasoftware.odt.apiclient.DefaultApiClient;
import com.kogasoftware.odt.invehicledevice.apiclient.model.base.Model;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Demand;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Driver;
import com.kogasoftware.odt.invehicledevice.apiclient.model.InVehicleDevice;
import com.kogasoftware.odt.invehicledevice.apiclient.model.OperationRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnmergedOperationSchedule;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Operator;
import com.kogasoftware.odt.invehicledevice.apiclient.model.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Platform;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Reservation;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ReservationCandidate;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ReservationUser;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceProvider;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnit;
import com.kogasoftware.odt.invehicledevice.apiclient.model.ServiceUnitStatusLog;
import com.kogasoftware.odt.invehicledevice.apiclient.model.User;
import com.kogasoftware.odt.invehicledevice.apiclient.model.UnitAssignment;
import com.kogasoftware.odt.invehicledevice.apiclient.model.VehicleNotification;
import com.kogasoftware.odt.invehicledevice.apiclient.model.Vehicle;

public class InVehicleDeviceTestApiClient extends DefaultApiClient {

	public InVehicleDeviceTestApiClient(String host) {
		super(host);
	}

	protected static final String TEST_PATH_CLEAN = "/clean";
	protected static final List<String> COMMON_EXCLUDES = Lists.newArrayList("id", "authentication_token", "fullname", "fullname_ruby", "remember_me", Model.JACKSON_IDENTITY_INFO_PROPERTY);

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
	protected static final String TEST_PATH_RESERVATION_USERS = "/reservation_users";
	protected static final String TEST_PATH_SERVICE_PROVIDERS = "/service_providers";
	protected static final String TEST_PATH_SERVICE_UNITS = "/service_units";
	protected static final String TEST_PATH_SERVICE_UNIT_STATUS_LOGS = "/service_unit_status_logs";
	protected static final String TEST_PATH_USERS = "/users";
	protected static final String TEST_PATH_UNIT_ASSIGNMENTS = "/unit_assignments";
	protected static final String TEST_PATH_VEHICLE_NOTIFICATIONS = "/vehicle_notifications";
	protected static final String TEST_PATH_VEHICLES = "/vehicles";

	protected static ObjectNode createObjectNode() {
		return Model.getObjectMapper().createObjectNode();
	}

	/**
	 * DatabaseCleaner を呼び出してDBを全クリアする
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int cleanDatabase(ApiClientCallback<Void> callback) throws ApiClientException {
		return post(TEST_PATH_CLEAN, createObjectNode(), UNIQUE_GROUP, callback, VOID_RESPONSE_CONVERTER);
	}	

	/**
	 * 予約条件をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllDemands(ApiClientCallback<List<Demand>> callback) throws ApiClientException {
		return get(TEST_PATH_DEMANDS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Demand.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 予約条件をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getDemand(int id, ApiClientCallback<Demand> callback) throws ApiClientException {
		return get(TEST_PATH_DEMANDS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Demand.RESPONSE_CONVERTER);
	}

	/**
	 * 予約条件を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createDemand(Demand obj, ApiClientCallback<Demand> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(Demand.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_DEMANDS, param, UNIQUE_GROUP, callback, Demand.RESPONSE_CONVERTER);
	}

	/**
	 * 予約条件をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteDemand(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_DEMANDS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 運転手をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllDrivers(ApiClientCallback<List<Driver>> callback) throws ApiClientException {
		return get(TEST_PATH_DRIVERS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Driver.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 運転手をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getDriver(int id, ApiClientCallback<Driver> callback) throws ApiClientException {
		return get(TEST_PATH_DRIVERS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Driver.RESPONSE_CONVERTER);
	}

	/**
	 * 運転手を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createDriver(Driver obj, ApiClientCallback<Driver> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(Driver.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_DRIVERS, param, UNIQUE_GROUP, callback, Driver.RESPONSE_CONVERTER);
	}

	/**
	 * 運転手をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteDriver(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_DRIVERS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 車載器をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllInVehicleDevices(ApiClientCallback<List<InVehicleDevice>> callback) throws ApiClientException {
		return get(TEST_PATH_IN_VEHICLE_DEVICES, new TreeMap<String, String>(), UNIQUE_GROUP, callback, InVehicleDevice.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 車載器をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getInVehicleDevice(int id, ApiClientCallback<InVehicleDevice> callback) throws ApiClientException {
		return get(TEST_PATH_IN_VEHICLE_DEVICES + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, InVehicleDevice.RESPONSE_CONVERTER);
	}

	/**
	 * 車載器を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createInVehicleDevice(InVehicleDevice obj, ApiClientCallback<InVehicleDevice> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(InVehicleDevice.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_IN_VEHICLE_DEVICES, param, UNIQUE_GROUP, callback, InVehicleDevice.RESPONSE_CONVERTER);
	}

	/**
	 * 車載器をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteInVehicleDevice(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_IN_VEHICLE_DEVICES + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 運行記録をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllOperationRecords(ApiClientCallback<List<OperationRecord>> callback) throws ApiClientException {
		return get(TEST_PATH_OPERATION_RECORDS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, OperationRecord.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 運行記録をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getOperationRecord(int id, ApiClientCallback<OperationRecord> callback) throws ApiClientException {
		return get(TEST_PATH_OPERATION_RECORDS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, OperationRecord.RESPONSE_CONVERTER);
	}

	/**
	 * 運行記録を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createOperationRecord(OperationRecord obj, ApiClientCallback<OperationRecord> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(OperationRecord.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_OPERATION_RECORDS, param, UNIQUE_GROUP, callback, OperationRecord.RESPONSE_CONVERTER);
	}

	/**
	 * 運行記録をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteOperationRecord(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_OPERATION_RECORDS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 運行予定をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllOperationSchedules(ApiClientCallback<List<UnmergedOperationSchedule>> callback) throws ApiClientException {
		return get(TEST_PATH_OPERATION_SCHEDULES, new TreeMap<String, String>(), UNIQUE_GROUP, callback, UnmergedOperationSchedule.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 運行予定をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getOperationSchedule(int id, ApiClientCallback<UnmergedOperationSchedule> callback) throws ApiClientException {
		return get(TEST_PATH_OPERATION_SCHEDULES + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, UnmergedOperationSchedule.RESPONSE_CONVERTER);
	}

	/**
	 * 運行予定を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createOperationSchedule(UnmergedOperationSchedule obj, ApiClientCallback<UnmergedOperationSchedule> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(UnmergedOperationSchedule.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_OPERATION_SCHEDULES, param, UNIQUE_GROUP, callback, UnmergedOperationSchedule.RESPONSE_CONVERTER);
	}

	/**
	 * 運行予定をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteOperationSchedule(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_OPERATION_SCHEDULES + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * オペレーターをすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllOperators(ApiClientCallback<List<Operator>> callback) throws ApiClientException {
		return get(TEST_PATH_OPERATORS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Operator.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * オペレーターをひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getOperator(int id, ApiClientCallback<Operator> callback) throws ApiClientException {
		return get(TEST_PATH_OPERATORS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Operator.RESPONSE_CONVERTER);
	}

	/**
	 * オペレーターを生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createOperator(Operator obj, ApiClientCallback<Operator> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(Operator.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_OPERATORS, param, UNIQUE_GROUP, callback, Operator.RESPONSE_CONVERTER);
	}

	/**
	 * オペレーターをひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteOperator(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_OPERATORS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 乗車記録をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllPassengerRecords(ApiClientCallback<List<PassengerRecord>> callback) throws ApiClientException {
		return get(TEST_PATH_PASSENGER_RECORDS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, PassengerRecord.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 乗車記録をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getPassengerRecord(int id, ApiClientCallback<PassengerRecord> callback) throws ApiClientException {
		return get(TEST_PATH_PASSENGER_RECORDS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, PassengerRecord.RESPONSE_CONVERTER);
	}

	/**
	 * 乗車記録を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createPassengerRecord(PassengerRecord obj, ApiClientCallback<PassengerRecord> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(PassengerRecord.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_PASSENGER_RECORDS, param, UNIQUE_GROUP, callback, PassengerRecord.RESPONSE_CONVERTER);
	}

	/**
	 * 乗車記録をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deletePassengerRecord(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_PASSENGER_RECORDS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 乗降場をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllPlatforms(ApiClientCallback<List<Platform>> callback) throws ApiClientException {
		return get(TEST_PATH_PLATFORMS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Platform.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 乗降場をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getPlatform(int id, ApiClientCallback<Platform> callback) throws ApiClientException {
		return get(TEST_PATH_PLATFORMS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Platform.RESPONSE_CONVERTER);
	}

	/**
	 * 乗降場を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createPlatform(Platform obj, ApiClientCallback<Platform> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(Platform.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_PLATFORMS, param, UNIQUE_GROUP, callback, Platform.RESPONSE_CONVERTER);
	}

	/**
	 * 乗降場をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deletePlatform(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_PLATFORMS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 予約をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllReservations(ApiClientCallback<List<Reservation>> callback) throws ApiClientException {
		return get(TEST_PATH_RESERVATIONS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Reservation.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 予約をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getReservation(int id, ApiClientCallback<Reservation> callback) throws ApiClientException {
		return get(TEST_PATH_RESERVATIONS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Reservation.RESPONSE_CONVERTER);
	}

	/**
	 * 予約を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createReservation(Reservation obj, ApiClientCallback<Reservation> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(Reservation.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_RESERVATIONS, param, UNIQUE_GROUP, callback, Reservation.RESPONSE_CONVERTER);
	}

	/**
	 * 予約をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteReservation(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_RESERVATIONS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 予約候補をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllReservationCandidates(ApiClientCallback<List<ReservationCandidate>> callback) throws ApiClientException {
		return get(TEST_PATH_RESERVATION_CANDIDATES, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ReservationCandidate.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 予約候補をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getReservationCandidate(int id, ApiClientCallback<ReservationCandidate> callback) throws ApiClientException {
		return get(TEST_PATH_RESERVATION_CANDIDATES + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ReservationCandidate.RESPONSE_CONVERTER);
	}

	/**
	 * 予約候補を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createReservationCandidate(ReservationCandidate obj, ApiClientCallback<ReservationCandidate> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(ReservationCandidate.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_RESERVATION_CANDIDATES, param, UNIQUE_GROUP, callback, ReservationCandidate.RESPONSE_CONVERTER);
	}

	/**
	 * 予約候補をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteReservationCandidate(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_RESERVATION_CANDIDATES + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 予約とユーザの関連をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllReservationUsers(ApiClientCallback<List<ReservationUser>> callback) throws ApiClientException {
		return get(TEST_PATH_RESERVATION_USERS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ReservationUser.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 予約とユーザの関連をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getReservationUser(int id, ApiClientCallback<ReservationUser> callback) throws ApiClientException {
		return get(TEST_PATH_RESERVATION_USERS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ReservationUser.RESPONSE_CONVERTER);
	}

	/**
	 * 予約とユーザの関連を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createReservationUser(ReservationUser obj, ApiClientCallback<ReservationUser> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(ReservationUser.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_RESERVATION_USERS, param, UNIQUE_GROUP, callback, ReservationUser.RESPONSE_CONVERTER);
	}

	/**
	 * 予約とユーザの関連をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteReservationUser(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_RESERVATION_USERS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 自治体をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllServiceProviders(ApiClientCallback<List<ServiceProvider>> callback) throws ApiClientException {
		return get(TEST_PATH_SERVICE_PROVIDERS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ServiceProvider.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 自治体をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getServiceProvider(int id, ApiClientCallback<ServiceProvider> callback) throws ApiClientException {
		return get(TEST_PATH_SERVICE_PROVIDERS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ServiceProvider.RESPONSE_CONVERTER);
	}

	/**
	 * 自治体を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createServiceProvider(ServiceProvider obj, ApiClientCallback<ServiceProvider> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(ServiceProvider.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_SERVICE_PROVIDERS, param, UNIQUE_GROUP, callback, ServiceProvider.RESPONSE_CONVERTER);
	}

	/**
	 * 自治体をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteServiceProvider(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_SERVICE_PROVIDERS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 時点号車をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllServiceUnits(ApiClientCallback<List<ServiceUnit>> callback) throws ApiClientException {
		return get(TEST_PATH_SERVICE_UNITS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ServiceUnit.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 時点号車をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getServiceUnit(int id, ApiClientCallback<ServiceUnit> callback) throws ApiClientException {
		return get(TEST_PATH_SERVICE_UNITS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ServiceUnit.RESPONSE_CONVERTER);
	}

	/**
	 * 時点号車を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createServiceUnit(ServiceUnit obj, ApiClientCallback<ServiceUnit> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(ServiceUnit.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_SERVICE_UNITS, param, UNIQUE_GROUP, callback, ServiceUnit.RESPONSE_CONVERTER);
	}

	/**
	 * 時点号車をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteServiceUnit(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_SERVICE_UNITS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 号車ログをすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllServiceUnitStatusLogs(ApiClientCallback<List<ServiceUnitStatusLog>> callback) throws ApiClientException {
		return get(TEST_PATH_SERVICE_UNIT_STATUS_LOGS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ServiceUnitStatusLog.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 号車ログをひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getServiceUnitStatusLog(int id, ApiClientCallback<ServiceUnitStatusLog> callback) throws ApiClientException {
		return get(TEST_PATH_SERVICE_UNIT_STATUS_LOGS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, ServiceUnitStatusLog.RESPONSE_CONVERTER);
	}

	/**
	 * 号車ログを生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createServiceUnitStatusLog(ServiceUnitStatusLog obj, ApiClientCallback<ServiceUnitStatusLog> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(ServiceUnitStatusLog.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_SERVICE_UNIT_STATUS_LOGS, param, UNIQUE_GROUP, callback, ServiceUnitStatusLog.RESPONSE_CONVERTER);
	}

	/**
	 * 号車ログをひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteServiceUnitStatusLog(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_SERVICE_UNIT_STATUS_LOGS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * ユーザをすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllUsers(ApiClientCallback<List<User>> callback) throws ApiClientException {
		return get(TEST_PATH_USERS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, User.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * ユーザをひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getUser(int id, ApiClientCallback<User> callback) throws ApiClientException {
		return get(TEST_PATH_USERS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, User.RESPONSE_CONVERTER);
	}

	/**
	 * ユーザを生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createUser(User obj, ApiClientCallback<User> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(User.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_USERS, param, UNIQUE_GROUP, callback, User.RESPONSE_CONVERTER);
	}

	/**
	 * ユーザをひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteUser(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_USERS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 号車をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllUnitAssignments(ApiClientCallback<List<UnitAssignment>> callback) throws ApiClientException {
		return get(TEST_PATH_UNIT_ASSIGNMENTS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, UnitAssignment.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 号車をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getUnitAssignment(int id, ApiClientCallback<UnitAssignment> callback) throws ApiClientException {
		return get(TEST_PATH_UNIT_ASSIGNMENTS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, UnitAssignment.RESPONSE_CONVERTER);
	}

	/**
	 * 号車を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createUnitAssignment(UnitAssignment obj, ApiClientCallback<UnitAssignment> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(UnitAssignment.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_UNIT_ASSIGNMENTS, param, UNIQUE_GROUP, callback, UnitAssignment.RESPONSE_CONVERTER);
	}

	/**
	 * 号車をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteUnitAssignment(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_UNIT_ASSIGNMENTS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 車載器への通知をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllVehicleNotifications(ApiClientCallback<List<VehicleNotification>> callback) throws ApiClientException {
		return get(TEST_PATH_VEHICLE_NOTIFICATIONS, new TreeMap<String, String>(), UNIQUE_GROUP, callback, VehicleNotification.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 車載器への通知をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getVehicleNotification(int id, ApiClientCallback<VehicleNotification> callback) throws ApiClientException {
		return get(TEST_PATH_VEHICLE_NOTIFICATIONS + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, VehicleNotification.RESPONSE_CONVERTER);
	}

	/**
	 * 車載器への通知を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createVehicleNotification(VehicleNotification obj, ApiClientCallback<VehicleNotification> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(VehicleNotification.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_VEHICLE_NOTIFICATIONS, param, UNIQUE_GROUP, callback, VehicleNotification.RESPONSE_CONVERTER);
	}

	/**
	 * 車載器への通知をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteVehicleNotification(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_VEHICLE_NOTIFICATIONS + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

	/**
	 * 車両をすべて取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getAllVehicles(ApiClientCallback<List<Vehicle>> callback) throws ApiClientException {
		return get(TEST_PATH_VEHICLES, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Vehicle.LIST_RESPONSE_CONVERTER);
	}

	/**
	 * 車両をひとつ取得
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int getVehicle(int id, ApiClientCallback<Vehicle> callback) throws ApiClientException {
		return get(TEST_PATH_VEHICLES + "/" + id, new TreeMap<String, String>(), UNIQUE_GROUP, callback, Vehicle.RESPONSE_CONVERTER);
	}

	/**
	 * 車両を生成
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int createVehicle(Vehicle obj, ApiClientCallback<Vehicle> callback) throws ApiClientException, IOException {
		JsonNode param = createObjectNode().set(Vehicle.UNDERSCORE, obj.toJsonNode(false).without(COMMON_EXCLUDES));
		return post(TEST_PATH_VEHICLES, param, UNIQUE_GROUP, callback, Vehicle.RESPONSE_CONVERTER);
	}

	/**
	 * 車両をひとつ削除
	 * @param callback
	 * @return reqkey
	 * @throws ApiClientException
	 */
	public int deleteVehicle(int id, ApiClientCallback<Void> callback) throws ApiClientException {
		return delete(TEST_PATH_VEHICLES + "/" + id, callback, VOID_RESPONSE_CONVERTER);
	}

}
