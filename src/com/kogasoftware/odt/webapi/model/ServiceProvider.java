package com.kogasoftware.odt.webapi.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class ServiceProvider extends Model {
	private static final long serialVersionUID = 5916444169589530632L;

	public ServiceProvider() {
	}

	public ServiceProvider(JSONObject jsonObject) throws JSONException {
		try {
			fillMembers(this, jsonObject);
		} catch (ParseException e) {
			throw new JSONException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	public static void fillMembers(ServiceProvider model, JSONObject jsonObject) throws JSONException, ParseException {
		model.setCreatedAt(parseDate(jsonObject, "created_at"));
		model.setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		model.setId(parseInteger(jsonObject, "id"));
		model.setLatitude(parseBigDecimal(jsonObject, "latitude"));
		model.setLongitude(parseBigDecimal(jsonObject, "longitude"));
		model.setMustContactGap(parseInteger(jsonObject, "must_contact_gap"));
		model.setName(parseString(jsonObject, "name"));
		model.setRecommend(parseBoolean(jsonObject, "recommend"));
		model.setReservationStartDate(parseInteger(jsonObject, "reservation_start_date"));
		model.setReservationTimeLimit(parseString(jsonObject, "reservation_time_limit"));
		model.setSemiDemand(parseBoolean(jsonObject, "semi_demand"));
		model.setSemiDemandExtentLimit(parseInteger(jsonObject, "semi_demand_extent_limit"));
		model.setUpdatedAt(parseDate(jsonObject, "updated_at"));
		model.setUserLoginLength(parseInteger(jsonObject, "user_login_length"));
		model.setDemands(Demand.parseList(jsonObject, "demands"));
		model.setDrivers(Driver.parseList(jsonObject, "drivers"));
		model.setInVehicleDevices(InVehicleDevice.parseList(jsonObject, "in_vehicle_devices"));
		model.setOperationSchedules(OperationSchedule.parseList(jsonObject, "operation_schedules"));
		model.setOperators(Operator.parseList(jsonObject, "operators"));
		model.setPassengerRecords(PassengerRecord.parseList(jsonObject, "passenger_records"));
		model.setPlatforms(Platform.parseList(jsonObject, "platforms"));
		model.setReservationCandidates(ReservationCandidate.parseList(jsonObject, "reservation_candidates"));
		model.setReservations(Reservation.parseList(jsonObject, "reservations"));
		model.setServiceUnits(ServiceUnit.parseList(jsonObject, "service_units"));
		model.setUnitAssignments(UnitAssignment.parseList(jsonObject, "unit_assignments"));
		model.setUsers(User.parseList(jsonObject, "users"));
		model.setVehicles(Vehicle.parseList(jsonObject, "vehicles"));
	}

	public static Optional<ServiceProvider> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<ServiceProvider> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new ServiceProvider(jsonObject));
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
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		depth++;
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
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
		if (getDemands().size() > 0 && recursive) {
			jsonObject.put("demands", toJSON(getDemands(), true, depth));
		}
		if (getDrivers().size() > 0 && recursive) {
			jsonObject.put("drivers", toJSON(getDrivers(), true, depth));
		}
		if (getInVehicleDevices().size() > 0 && recursive) {
			jsonObject.put("in_vehicle_devices", toJSON(getInVehicleDevices(), true, depth));
		}
		if (getOperationSchedules().size() > 0 && recursive) {
			jsonObject.put("operation_schedules", toJSON(getOperationSchedules(), true, depth));
		}
		if (getOperators().size() > 0 && recursive) {
			jsonObject.put("operators", toJSON(getOperators(), true, depth));
		}
		if (getPassengerRecords().size() > 0 && recursive) {
			jsonObject.put("passenger_records", toJSON(getPassengerRecords(), true, depth));
		}
		if (getPlatforms().size() > 0 && recursive) {
			jsonObject.put("platforms", toJSON(getPlatforms(), true, depth));
		}
		if (getReservationCandidates().size() > 0 && recursive) {
			jsonObject.put("reservation_candidates", toJSON(getReservationCandidates(), true, depth));
		}
		if (getReservations().size() > 0 && recursive) {
			jsonObject.put("reservations", toJSON(getReservations(), true, depth));
		}
		if (getServiceUnits().size() > 0 && recursive) {
			jsonObject.put("service_units", toJSON(getServiceUnits(), true, depth));
		}
		if (getUnitAssignments().size() > 0 && recursive) {
			jsonObject.put("unit_assignments", toJSON(getUnitAssignments(), true, depth));
		}
		if (getUsers().size() > 0 && recursive) {
			jsonObject.put("users", toJSON(getUsers(), true, depth));
		}
		if (getVehicles().size() > 0 && recursive) {
			jsonObject.put("vehicles", toJSON(getVehicles(), true, depth));
		}
		return jsonObject;
	}

	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException {
		try {
			objectOutputStream.writeObject(toJSONObject(true).toString());
		} catch (JSONException e) {
			throw new IOException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	private void readObject(ObjectInputStream objectInputStream)
		throws IOException, ClassNotFoundException {
		Object object = objectInputStream.readObject();
		if (!(object instanceof String)) {
			return;
		}
		String jsonString = (String) object;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			fillMembers(this, jsonObject);
		} catch (JSONException e) {
			throw new IOException(e);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	@Override
	public ServiceProvider cloneByJSON() throws JSONException {
		return new ServiceProvider(toJSONObject(true));
	}

	private Date createdAt = new Date();

	public Date getCreatedAt() {
		return wrapNull(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = wrapNull(createdAt);
	}

	private Optional<Date> deletedAt = Optional.absent();

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
		this.deletedAt = Optional.absent();
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
