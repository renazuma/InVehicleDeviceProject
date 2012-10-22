package com.kogasoftware.odt.invehicledevice.apiclient.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.apiclient.ApiClients;
import com.kogasoftware.odt.apiclient.ApiClient.ResponseConverter;
import com.kogasoftware.odt.invehicledevice.apiclient.model.*;

@SuppressWarnings("unused")
public abstract class ServiceProviderBase extends Model {
	private static final long serialVersionUID = 8695386415207338764L;
	public static final ResponseConverter<ServiceProvider> RESPONSE_CONVERTER = new ResponseConverter<ServiceProvider>() {
		@Override
		public ServiceProvider convert(byte[] rawResponse) throws JSONException {
			return parse(ApiClients.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<ServiceProvider>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<ServiceProvider>>() {
		@Override
		public List<ServiceProvider> convert(byte[] rawResponse) throws JSONException {
			return parseList(ApiClients.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDomain(parseString(jsonObject, "domain"));
		setId(parseInteger(jsonObject, "id"));
		setLatitude(parseBigDecimal(jsonObject, "latitude"));
		setLogAccessKeyIdAws(parseOptionalString(jsonObject, "log_access_key_id_aws"));
		setLogSecretAccessKeyAws(parseOptionalString(jsonObject, "log_secret_access_key_aws"));
		setLongitude(parseBigDecimal(jsonObject, "longitude"));
		setMustContactGap(parseInteger(jsonObject, "must_contact_gap"));
		setName(parseString(jsonObject, "name"));
		setRecommend(parseBoolean(jsonObject, "recommend"));
		setReservationStartDate(parseInteger(jsonObject, "reservation_start_date"));
		setReservationTimeLimit(parseString(jsonObject, "reservation_time_limit"));
		setSemiDemand(parseBoolean(jsonObject, "semi_demand"));
		setSemiDemandExtentLimit(parseInteger(jsonObject, "semi_demand_extent_limit"));
		setTimeBufferRatio(parseString(jsonObject, "time_buffer_ratio"));
		setUserLoginLength(parseInteger(jsonObject, "user_login_length"));
		setDemands(Demand.parseList(jsonObject, "demands"));
		setDrivers(Driver.parseList(jsonObject, "drivers"));
		setInVehicleDevices(InVehicleDevice.parseList(jsonObject, "in_vehicle_devices"));
		setOperationSchedules(OperationSchedule.parseList(jsonObject, "operation_schedules"));
		setOperators(Operator.parseList(jsonObject, "operators"));
		setPassengerRecords(PassengerRecord.parseList(jsonObject, "passenger_records"));
		setPlatforms(Platform.parseList(jsonObject, "platforms"));
		setReservationCandidates(ReservationCandidate.parseList(jsonObject, "reservation_candidates"));
		setReservations(Reservation.parseList(jsonObject, "reservations"));
		setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));
		setUnitAssignments(UnitAssignment.parseList(jsonObject, "unit_assignments"));
		setUsers(User.parseList(jsonObject, "users"));
		setVehicles(Vehicle.parseList(jsonObject, "vehicles"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<ServiceProvider> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static ServiceProvider parse(JSONObject jsonObject) throws JSONException {
		ServiceProvider model = new ServiceProvider();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<ServiceProvider> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ServiceProvider>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ServiceProvider> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<ServiceProvider> models = new LinkedList<ServiceProvider>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(parse(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
		Integer nextDepth = depth + 1;
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("domain", toJSON(getDomain()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("latitude", toJSON(getLatitude()));
		jsonObject.put("log_access_key_id_aws", toJSON(getLogAccessKeyIdAws()));
		jsonObject.put("log_secret_access_key_aws", toJSON(getLogSecretAccessKeyAws()));
		jsonObject.put("longitude", toJSON(getLongitude()));
		jsonObject.put("must_contact_gap", toJSON(getMustContactGap()));
		jsonObject.put("name", toJSON(getName()));
		jsonObject.put("recommend", toJSON(getRecommend()));
		jsonObject.put("reservation_start_date", toJSON(getReservationStartDate()));
		jsonObject.put("reservation_time_limit", toJSON(getReservationTimeLimit()));
		jsonObject.put("semi_demand", toJSON(getSemiDemand()));
		jsonObject.put("semi_demand_extent_limit", toJSON(getSemiDemandExtentLimit()));
		jsonObject.put("time_buffer_ratio", toJSON(getTimeBufferRatio()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_login_length", toJSON(getUserLoginLength()));
		if (getDemands().size() > 0 && recursive) {
			jsonObject.put("demands", toJSON(getDemands(), true, nextDepth));
		}
		if (getDrivers().size() > 0 && recursive) {
			jsonObject.put("drivers", toJSON(getDrivers(), true, nextDepth));
		}
		if (getInVehicleDevices().size() > 0 && recursive) {
			jsonObject.put("in_vehicle_devices", toJSON(getInVehicleDevices(), true, nextDepth));
		}
		if (getOperationSchedules().size() > 0 && recursive) {
			jsonObject.put("operation_schedules", toJSON(getOperationSchedules(), true, nextDepth));
		}
		if (getOperators().size() > 0 && recursive) {
			jsonObject.put("operators", toJSON(getOperators(), true, nextDepth));
		}
		if (getPassengerRecords().size() > 0 && recursive) {
			jsonObject.put("passenger_records", toJSON(getPassengerRecords(), true, nextDepth));
		}
		if (getPlatforms().size() > 0 && recursive) {
			jsonObject.put("platforms", toJSON(getPlatforms(), true, nextDepth));
		}
		if (getReservationCandidates().size() > 0 && recursive) {
			jsonObject.put("reservation_candidates", toJSON(getReservationCandidates(), true, nextDepth));
		}
		if (getReservations().size() > 0 && recursive) {
			jsonObject.put("reservations", toJSON(getReservations(), true, nextDepth));
		}
		if (getServiceUnits().size() > 0 && recursive) {
			jsonObject.put("service_units", toJSON(getServiceUnits(), true, nextDepth));
		}
		if (getUnitAssignments().size() > 0 && recursive) {
			jsonObject.put("unit_assignments", toJSON(getUnitAssignments(), true, nextDepth));
		}
		if (getUsers().size() > 0 && recursive) {
			jsonObject.put("users", toJSON(getUsers(), true, nextDepth));
		}
		if (getVehicles().size() > 0 && recursive) {
			jsonObject.put("vehicles", toJSON(getVehicles(), true, nextDepth));
		}
		return jsonObject;
	}

	@Override
	public ServiceProvider cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		refreshUpdatedAt();
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	public void setDeletedAt(Optional<Date> deletedAt) {
		refreshUpdatedAt();
		this.deletedAt = wrapNull(deletedAt);
	}

	public void setDeletedAt(Date deletedAt) {
		setDeletedAt(Optional.fromNullable(deletedAt));
	}

	public void clearDeletedAt() {
		setDeletedAt(Optional.<Date>absent());
	}

	private String domain = "";

	public String getDomain() {
		return wrapNull(domain);
	}

	public void setDomain(String domain) {
		refreshUpdatedAt();
		this.domain = wrapNull(domain);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private BigDecimal latitude = BigDecimal.ZERO;

	public BigDecimal getLatitude() {
		return wrapNull(latitude);
	}

	public void setLatitude(BigDecimal latitude) {
		refreshUpdatedAt();
		this.latitude = wrapNull(latitude);
	}

	private Optional<String> logAccessKeyIdAws = Optional.absent();

	public Optional<String> getLogAccessKeyIdAws() {
		return wrapNull(logAccessKeyIdAws);
	}

	public void setLogAccessKeyIdAws(Optional<String> logAccessKeyIdAws) {
		refreshUpdatedAt();
		this.logAccessKeyIdAws = wrapNull(logAccessKeyIdAws);
	}

	public void setLogAccessKeyIdAws(String logAccessKeyIdAws) {
		setLogAccessKeyIdAws(Optional.fromNullable(logAccessKeyIdAws));
	}

	public void clearLogAccessKeyIdAws() {
		setLogAccessKeyIdAws(Optional.<String>absent());
	}

	private Optional<String> logSecretAccessKeyAws = Optional.absent();

	public Optional<String> getLogSecretAccessKeyAws() {
		return wrapNull(logSecretAccessKeyAws);
	}

	public void setLogSecretAccessKeyAws(Optional<String> logSecretAccessKeyAws) {
		refreshUpdatedAt();
		this.logSecretAccessKeyAws = wrapNull(logSecretAccessKeyAws);
	}

	public void setLogSecretAccessKeyAws(String logSecretAccessKeyAws) {
		setLogSecretAccessKeyAws(Optional.fromNullable(logSecretAccessKeyAws));
	}

	public void clearLogSecretAccessKeyAws() {
		setLogSecretAccessKeyAws(Optional.<String>absent());
	}

	private BigDecimal longitude = BigDecimal.ZERO;

	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	public void setLongitude(BigDecimal longitude) {
		refreshUpdatedAt();
		this.longitude = wrapNull(longitude);
	}

	private Integer mustContactGap = 0;

	public Integer getMustContactGap() {
		return wrapNull(mustContactGap);
	}

	public void setMustContactGap(Integer mustContactGap) {
		refreshUpdatedAt();
		this.mustContactGap = wrapNull(mustContactGap);
	}

	private String name = "";

	public String getName() {
		return wrapNull(name);
	}

	public void setName(String name) {
		refreshUpdatedAt();
		this.name = wrapNull(name);
	}

	private Boolean recommend = false;

	public Boolean getRecommend() {
		return wrapNull(recommend);
	}

	public void setRecommend(Boolean recommend) {
		refreshUpdatedAt();
		this.recommend = wrapNull(recommend);
	}

	private Integer reservationStartDate = 0;

	public Integer getReservationStartDate() {
		return wrapNull(reservationStartDate);
	}

	public void setReservationStartDate(Integer reservationStartDate) {
		refreshUpdatedAt();
		this.reservationStartDate = wrapNull(reservationStartDate);
	}

	private String reservationTimeLimit = "";

	public String getReservationTimeLimit() {
		return wrapNull(reservationTimeLimit);
	}

	public void setReservationTimeLimit(String reservationTimeLimit) {
		refreshUpdatedAt();
		this.reservationTimeLimit = wrapNull(reservationTimeLimit);
	}

	private Boolean semiDemand = false;

	public Boolean getSemiDemand() {
		return wrapNull(semiDemand);
	}

	public void setSemiDemand(Boolean semiDemand) {
		refreshUpdatedAt();
		this.semiDemand = wrapNull(semiDemand);
	}

	private Integer semiDemandExtentLimit = 0;

	public Integer getSemiDemandExtentLimit() {
		return wrapNull(semiDemandExtentLimit);
	}

	public void setSemiDemandExtentLimit(Integer semiDemandExtentLimit) {
		refreshUpdatedAt();
		this.semiDemandExtentLimit = wrapNull(semiDemandExtentLimit);
	}

	private String timeBufferRatio = "";

	public String getTimeBufferRatio() {
		return wrapNull(timeBufferRatio);
	}

	public void setTimeBufferRatio(String timeBufferRatio) {
		refreshUpdatedAt();
		this.timeBufferRatio = wrapNull(timeBufferRatio);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Integer userLoginLength = 0;

	public Integer getUserLoginLength() {
		return wrapNull(userLoginLength);
	}

	public void setUserLoginLength(Integer userLoginLength) {
		refreshUpdatedAt();
		this.userLoginLength = wrapNull(userLoginLength);
	}

	private LinkedList<Demand> demands = new LinkedList<Demand>();

	public List<Demand> getDemands() {
		return wrapNull(demands);
	}

	public void setDemands(Iterable<Demand> demands) {
		this.demands = wrapNull(demands);
	}

	public void clearDemands() {
		setDemands(new LinkedList<Demand>());
	}

	private LinkedList<Driver> drivers = new LinkedList<Driver>();

	public List<Driver> getDrivers() {
		return wrapNull(drivers);
	}

	public void setDrivers(Iterable<Driver> drivers) {
		this.drivers = wrapNull(drivers);
	}

	public void clearDrivers() {
		setDrivers(new LinkedList<Driver>());
	}

	private LinkedList<InVehicleDevice> inVehicleDevices = new LinkedList<InVehicleDevice>();

	public List<InVehicleDevice> getInVehicleDevices() {
		return wrapNull(inVehicleDevices);
	}

	public void setInVehicleDevices(Iterable<InVehicleDevice> inVehicleDevices) {
		this.inVehicleDevices = wrapNull(inVehicleDevices);
	}

	public void clearInVehicleDevices() {
		setInVehicleDevices(new LinkedList<InVehicleDevice>());
	}

	private LinkedList<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();

	public List<OperationSchedule> getOperationSchedules() {
		return wrapNull(operationSchedules);
	}

	public void setOperationSchedules(Iterable<OperationSchedule> operationSchedules) {
		this.operationSchedules = wrapNull(operationSchedules);
	}

	public void clearOperationSchedules() {
		setOperationSchedules(new LinkedList<OperationSchedule>());
	}

	private LinkedList<Operator> operators = new LinkedList<Operator>();

	public List<Operator> getOperators() {
		return wrapNull(operators);
	}

	public void setOperators(Iterable<Operator> operators) {
		this.operators = wrapNull(operators);
	}

	public void clearOperators() {
		setOperators(new LinkedList<Operator>());
	}

	private LinkedList<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();

	public List<PassengerRecord> getPassengerRecords() {
		return wrapNull(passengerRecords);
	}

	public void setPassengerRecords(Iterable<PassengerRecord> passengerRecords) {
		this.passengerRecords = wrapNull(passengerRecords);
	}

	public void clearPassengerRecords() {
		setPassengerRecords(new LinkedList<PassengerRecord>());
	}

	private LinkedList<Platform> platforms = new LinkedList<Platform>();

	public List<Platform> getPlatforms() {
		return wrapNull(platforms);
	}

	public void setPlatforms(Iterable<Platform> platforms) {
		this.platforms = wrapNull(platforms);
	}

	public void clearPlatforms() {
		setPlatforms(new LinkedList<Platform>());
	}

	private LinkedList<ReservationCandidate> reservationCandidates = new LinkedList<ReservationCandidate>();

	public List<ReservationCandidate> getReservationCandidates() {
		return wrapNull(reservationCandidates);
	}

	public void setReservationCandidates(Iterable<ReservationCandidate> reservationCandidates) {
		this.reservationCandidates = wrapNull(reservationCandidates);
	}

	public void clearReservationCandidates() {
		setReservationCandidates(new LinkedList<ReservationCandidate>());
	}

	private LinkedList<Reservation> reservations = new LinkedList<Reservation>();

	public List<Reservation> getReservations() {
		return wrapNull(reservations);
	}

	public void setReservations(Iterable<Reservation> reservations) {
		this.reservations = wrapNull(reservations);
	}

	public void clearReservations() {
		setReservations(new LinkedList<Reservation>());
	}

	private LinkedList<ServiceUnit> serviceUnits = new LinkedList<ServiceUnit>();

	public List<ServiceUnit> getServiceUnits() {
		return wrapNull(serviceUnits);
	}

	public void setServiceUnits(Iterable<ServiceUnit> serviceUnits) {
		this.serviceUnits = wrapNull(serviceUnits);
	}

	public void clearServiceUnits() {
		setServiceUnits(new LinkedList<ServiceUnit>());
	}

	private LinkedList<UnitAssignment> unitAssignments = new LinkedList<UnitAssignment>();

	public List<UnitAssignment> getUnitAssignments() {
		return wrapNull(unitAssignments);
	}

	public void setUnitAssignments(Iterable<UnitAssignment> unitAssignments) {
		this.unitAssignments = wrapNull(unitAssignments);
	}

	public void clearUnitAssignments() {
		setUnitAssignments(new LinkedList<UnitAssignment>());
	}

	private LinkedList<User> users = new LinkedList<User>();

	public List<User> getUsers() {
		return wrapNull(users);
	}

	public void setUsers(Iterable<User> users) {
		this.users = wrapNull(users);
	}

	public void clearUsers() {
		setUsers(new LinkedList<User>());
	}

	private LinkedList<Vehicle> vehicles = new LinkedList<Vehicle>();

	public List<Vehicle> getVehicles() {
		return wrapNull(vehicles);
	}

	public void setVehicles(Iterable<Vehicle> vehicles) {
		this.vehicles = wrapNull(vehicles);
	}

	public void clearVehicles() {
		setVehicles(new LinkedList<Vehicle>());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(createdAt)
			.append(deletedAt)
			.append(domain)
			.append(id)
			.append(latitude)
			.append(logAccessKeyIdAws)
			.append(logSecretAccessKeyAws)
			.append(longitude)
			.append(mustContactGap)
			.append(name)
			.append(recommend)
			.append(reservationStartDate)
			.append(reservationTimeLimit)
			.append(semiDemand)
			.append(semiDemandExtentLimit)
			.append(timeBufferRatio)
			.append(updatedAt)
			.append(userLoginLength)
			.append(demands)
			.append(drivers)
			.append(inVehicleDevices)
			.append(operationSchedules)
			.append(operators)
			.append(passengerRecords)
			.append(platforms)
			.append(reservationCandidates)
			.append(reservations)
			.append(serviceUnits)
			.append(unitAssignments)
			.append(users)
			.append(vehicles)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof ServiceProviderBase)) {
			return false;
		}
		ServiceProviderBase other = (ServiceProviderBase) obj;
		return new EqualsBuilder()
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(domain, other.domain)
			.append(id, other.id)
			.append(latitude, other.latitude)
			.append(logAccessKeyIdAws, other.logAccessKeyIdAws)
			.append(logSecretAccessKeyAws, other.logSecretAccessKeyAws)
			.append(longitude, other.longitude)
			.append(mustContactGap, other.mustContactGap)
			.append(name, other.name)
			.append(recommend, other.recommend)
			.append(reservationStartDate, other.reservationStartDate)
			.append(reservationTimeLimit, other.reservationTimeLimit)
			.append(semiDemand, other.semiDemand)
			.append(semiDemandExtentLimit, other.semiDemandExtentLimit)
			.append(timeBufferRatio, other.timeBufferRatio)
			.append(updatedAt, other.updatedAt)
			.append(userLoginLength, other.userLoginLength)
			.append(demands, other.demands)
			.append(drivers, other.drivers)
			.append(inVehicleDevices, other.inVehicleDevices)
			.append(operationSchedules, other.operationSchedules)
			.append(operators, other.operators)
			.append(passengerRecords, other.passengerRecords)
			.append(platforms, other.platforms)
			.append(reservationCandidates, other.reservationCandidates)
			.append(reservations, other.reservations)
			.append(serviceUnits, other.serviceUnits)
			.append(unitAssignments, other.unitAssignments)
			.append(users, other.users)
			.append(vehicles, other.vehicles)
			.isEquals();
	}
}
