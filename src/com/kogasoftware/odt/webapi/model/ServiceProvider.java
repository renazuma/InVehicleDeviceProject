package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class ServiceProvider extends Model {
	private static final long serialVersionUID = 2895510103478243224L;

	public ServiceProvider() {
	}

	public ServiceProvider(JSONObject jsonObject) throws JSONException, ParseException {
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setId(parseInteger(jsonObject, "id"));
		setLatitude(parseBigDecimal(jsonObject, "latitude"));
		setLongitude(parseBigDecimal(jsonObject, "longitude"));
		setMustContactGap(parseInteger(jsonObject, "must_contact_gap"));
		setName(parseString(jsonObject, "name"));
		setRecommend(parseBoolean(jsonObject, "recommend"));
		setReservationStartDate(parseInteger(jsonObject, "reservation_start_date"));
		setReservationTimeLimit(parseString(jsonObject, "reservation_time_limit"));
		setSemiDemand(parseBoolean(jsonObject, "semi_demand"));
		setSemiDemandExtentLimit(parseInteger(jsonObject, "semi_demand_extent_limit"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
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
	}

	public static Optional<ServiceProvider> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<ServiceProvider>absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<ServiceProvider> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.<ServiceProvider>of(new ServiceProvider(jsonObject));
	}

	public static LinkedList<ServiceProvider> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ServiceProvider>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ServiceProvider> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<ServiceProvider> models = new LinkedList<ServiceProvider>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new ServiceProvider(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("latitude", toJSON(getLatitude()));
		jsonObject.put("longitude", toJSON(getLongitude()));
		jsonObject.put("must_contact_gap", toJSON(getMustContactGap()));
		jsonObject.put("name", toJSON(getName()));
		jsonObject.put("recommend", toJSON(getRecommend()));
		jsonObject.put("reservation_start_date", toJSON(getReservationStartDate()));
		jsonObject.put("reservation_time_limit", toJSON(getReservationTimeLimit()));
		jsonObject.put("semi_demand", toJSON(getSemiDemand()));
		jsonObject.put("semi_demand_extent_limit", toJSON(getSemiDemandExtentLimit()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_login_length", toJSON(getUserLoginLength()));
		if (getDemands().size() > 0) {
	   		jsonObject.put("demands", toJSON(getDemands()));
		}

		if (getDrivers().size() > 0) {
	   		jsonObject.put("drivers", toJSON(getDrivers()));
		}

		if (getInVehicleDevices().size() > 0) {
	   		jsonObject.put("in_vehicle_devices", toJSON(getInVehicleDevices()));
		}

		if (getOperationSchedules().size() > 0) {
	   		jsonObject.put("operation_schedules", toJSON(getOperationSchedules()));
		}

		if (getOperators().size() > 0) {
	   		jsonObject.put("operators", toJSON(getOperators()));
		}

		if (getPassengerRecords().size() > 0) {
	   		jsonObject.put("passenger_records", toJSON(getPassengerRecords()));
		}

		if (getPlatforms().size() > 0) {
	   		jsonObject.put("platforms", toJSON(getPlatforms()));
		}

		if (getReservationCandidates().size() > 0) {
	   		jsonObject.put("reservation_candidates", toJSON(getReservationCandidates()));
		}

		if (getReservations().size() > 0) {
	   		jsonObject.put("reservations", toJSON(getReservations()));
		}

		if (getServiceUnits().size() > 0) {
	   		jsonObject.put("service_units", toJSON(getServiceUnits()));
		}

		if (getUnitAssignments().size() > 0) {
	   		jsonObject.put("unit_assignments", toJSON(getUnitAssignments()));
		}

		if (getUsers().size() > 0) {
	   		jsonObject.put("users", toJSON(getUsers()));
		}

		if (getVehicles().size() > 0) {
	   		jsonObject.put("vehicles", toJSON(getVehicles()));
		}

		return jsonObject;
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.<Date>absent();

	public Optional<Date> getDeletedAt() {
		return wrapNull(deletedAt);
	}

	public void setDeletedAt(Optional<Date> deletedAt) {
		this.deletedAt = wrapNull(deletedAt);
	}

	public void setDeletedAt(Date deletedAt) {
		this.deletedAt = Optional.fromNullable(deletedAt);
	}

	public void clearDeletedAt() {
		this.deletedAt = Optional.<Date>absent();
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private BigDecimal latitude = BigDecimal.ZERO;

	public BigDecimal getLatitude() {
		return wrapNull(latitude);
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = wrapNull(latitude);
	}

	private BigDecimal longitude = BigDecimal.ZERO;

	public BigDecimal getLongitude() {
		return wrapNull(longitude);
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = wrapNull(longitude);
	}

	private Integer mustContactGap = 0;

	public Integer getMustContactGap() {
		return wrapNull(mustContactGap);
	}

	public void setMustContactGap(Integer mustContactGap) {
		this.mustContactGap = wrapNull(mustContactGap);
	}

	private String name = "";

	public String getName() {
		return wrapNull(name);
	}

	public void setName(String name) {
		this.name = wrapNull(name);
	}

	private Boolean recommend = false;

	public Boolean getRecommend() {
		return wrapNull(recommend);
	}

	public void setRecommend(Boolean recommend) {
		this.recommend = wrapNull(recommend);
	}

	private Integer reservationStartDate = 0;

	public Integer getReservationStartDate() {
		return wrapNull(reservationStartDate);
	}

	public void setReservationStartDate(Integer reservationStartDate) {
		this.reservationStartDate = wrapNull(reservationStartDate);
	}

	private String reservationTimeLimit = "";

	public String getReservationTimeLimit() {
		return wrapNull(reservationTimeLimit);
	}

	public void setReservationTimeLimit(String reservationTimeLimit) {
		this.reservationTimeLimit = wrapNull(reservationTimeLimit);
	}

	private Boolean semiDemand = false;

	public Boolean getSemiDemand() {
		return wrapNull(semiDemand);
	}

	public void setSemiDemand(Boolean semiDemand) {
		this.semiDemand = wrapNull(semiDemand);
	}

	private Integer semiDemandExtentLimit = 0;

	public Integer getSemiDemandExtentLimit() {
		return wrapNull(semiDemandExtentLimit);
	}

	public void setSemiDemandExtentLimit(Integer semiDemandExtentLimit) {
		this.semiDemandExtentLimit = wrapNull(semiDemandExtentLimit);
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
		this.userLoginLength = wrapNull(userLoginLength);
	}

	private LinkedList<Demand> demands = new LinkedList<Demand>();

	public List<Demand> getDemands() {
		return new LinkedList<Demand>(wrapNull(demands));
	}

	public void setDemands(List<Demand> demands) {
		this.demands = new LinkedList<Demand>(wrapNull(demands));
	}

	public void clearDemands() {
		this.demands = new LinkedList<Demand>();
	}

	private LinkedList<Driver> drivers = new LinkedList<Driver>();

	public List<Driver> getDrivers() {
		return new LinkedList<Driver>(wrapNull(drivers));
	}

	public void setDrivers(List<Driver> drivers) {
		this.drivers = new LinkedList<Driver>(wrapNull(drivers));
	}

	public void clearDrivers() {
		this.drivers = new LinkedList<Driver>();
	}

	private LinkedList<InVehicleDevice> inVehicleDevices = new LinkedList<InVehicleDevice>();

	public List<InVehicleDevice> getInVehicleDevices() {
		return new LinkedList<InVehicleDevice>(wrapNull(inVehicleDevices));
	}

	public void setInVehicleDevices(List<InVehicleDevice> inVehicleDevices) {
		this.inVehicleDevices = new LinkedList<InVehicleDevice>(wrapNull(inVehicleDevices));
	}

	public void clearInVehicleDevices() {
		this.inVehicleDevices = new LinkedList<InVehicleDevice>();
	}

	private LinkedList<OperationSchedule> operationSchedules = new LinkedList<OperationSchedule>();

	public List<OperationSchedule> getOperationSchedules() {
		return new LinkedList<OperationSchedule>(wrapNull(operationSchedules));
	}

	public void setOperationSchedules(List<OperationSchedule> operationSchedules) {
		this.operationSchedules = new LinkedList<OperationSchedule>(wrapNull(operationSchedules));
	}

	public void clearOperationSchedules() {
		this.operationSchedules = new LinkedList<OperationSchedule>();
	}

	private LinkedList<Operator> operators = new LinkedList<Operator>();

	public List<Operator> getOperators() {
		return new LinkedList<Operator>(wrapNull(operators));
	}

	public void setOperators(List<Operator> operators) {
		this.operators = new LinkedList<Operator>(wrapNull(operators));
	}

	public void clearOperators() {
		this.operators = new LinkedList<Operator>();
	}

	private LinkedList<PassengerRecord> passengerRecords = new LinkedList<PassengerRecord>();

	public List<PassengerRecord> getPassengerRecords() {
		return new LinkedList<PassengerRecord>(wrapNull(passengerRecords));
	}

	public void setPassengerRecords(List<PassengerRecord> passengerRecords) {
		this.passengerRecords = new LinkedList<PassengerRecord>(wrapNull(passengerRecords));
	}

	public void clearPassengerRecords() {
		this.passengerRecords = new LinkedList<PassengerRecord>();
	}

	private LinkedList<Platform> platforms = new LinkedList<Platform>();

	public List<Platform> getPlatforms() {
		return new LinkedList<Platform>(wrapNull(platforms));
	}

	public void setPlatforms(List<Platform> platforms) {
		this.platforms = new LinkedList<Platform>(wrapNull(platforms));
	}

	public void clearPlatforms() {
		this.platforms = new LinkedList<Platform>();
	}

	private LinkedList<ReservationCandidate> reservationCandidates = new LinkedList<ReservationCandidate>();

	public List<ReservationCandidate> getReservationCandidates() {
		return new LinkedList<ReservationCandidate>(wrapNull(reservationCandidates));
	}

	public void setReservationCandidates(List<ReservationCandidate> reservationCandidates) {
		this.reservationCandidates = new LinkedList<ReservationCandidate>(wrapNull(reservationCandidates));
	}

	public void clearReservationCandidates() {
		this.reservationCandidates = new LinkedList<ReservationCandidate>();
	}

	private LinkedList<Reservation> reservations = new LinkedList<Reservation>();

	public List<Reservation> getReservations() {
		return new LinkedList<Reservation>(wrapNull(reservations));
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = new LinkedList<Reservation>(wrapNull(reservations));
	}

	public void clearReservations() {
		this.reservations = new LinkedList<Reservation>();
	}

	private LinkedList<ServiceUnit> serviceUnits = new LinkedList<ServiceUnit>();

	public List<ServiceUnit> getServiceUnits() {
		return new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void setServiceUnits(List<ServiceUnit> serviceUnits) {
		this.serviceUnits = new LinkedList<ServiceUnit>(wrapNull(serviceUnits));
	}

	public void clearServiceUnits() {
		this.serviceUnits = new LinkedList<ServiceUnit>();
	}

	private LinkedList<UnitAssignment> unitAssignments = new LinkedList<UnitAssignment>();

	public List<UnitAssignment> getUnitAssignments() {
		return new LinkedList<UnitAssignment>(wrapNull(unitAssignments));
	}

	public void setUnitAssignments(List<UnitAssignment> unitAssignments) {
		this.unitAssignments = new LinkedList<UnitAssignment>(wrapNull(unitAssignments));
	}

	public void clearUnitAssignments() {
		this.unitAssignments = new LinkedList<UnitAssignment>();
	}

	private LinkedList<User> users = new LinkedList<User>();

	public List<User> getUsers() {
		return new LinkedList<User>(wrapNull(users));
	}

	public void setUsers(List<User> users) {
		this.users = new LinkedList<User>(wrapNull(users));
	}

	public void clearUsers() {
		this.users = new LinkedList<User>();
	}

	private LinkedList<Vehicle> vehicles = new LinkedList<Vehicle>();

	public List<Vehicle> getVehicles() {
		return new LinkedList<Vehicle>(wrapNull(vehicles));
	}

	public void setVehicles(List<Vehicle> vehicles) {
		this.vehicles = new LinkedList<Vehicle>(wrapNull(vehicles));
	}

	public void clearVehicles() {
		this.vehicles = new LinkedList<Vehicle>();
	}
}
