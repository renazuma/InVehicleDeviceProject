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

public class Reservation extends Model {
	private static final long serialVersionUID = 1612039563158938679L;

	public Reservation() {
	}

	public Reservation(JSONObject jsonObject) throws JSONException, ParseException {
		setArrivalLock(parseOptionalBoolean(jsonObject, "arrival_lock"));
		setArrivalPlatformId(parseOptionalInteger(jsonObject, "arrival_platform_id"));
		setArrivalScheduleId(parseOptionalInteger(jsonObject, "arrival_schedule_id"));
		setArrivalTime(parseDate(jsonObject, "arrival_time"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDemandId(parseOptionalInteger(jsonObject, "demand_id"));
		setDepartureLock(parseOptionalBoolean(jsonObject, "departure_lock"));
		setDeparturePlatformId(parseOptionalInteger(jsonObject, "departure_platform_id"));
		setDepartureScheduleId(parseOptionalInteger(jsonObject, "departure_schedule_id"));
		setDepartureTime(parseDate(jsonObject, "departure_time"));
		setDummy(parseOptionalBoolean(jsonObject, "dummy"));
		setId(parseInteger(jsonObject, "id"));
		setMemo(parseOptionalString(jsonObject, "memo"));
		setOperatorId(parseOptionalInteger(jsonObject, "operator_id"));
		setPassengerCount(parseInteger(jsonObject, "passenger_count"));
		setPayment(parseInteger(jsonObject, "payment"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setStatus(parseInteger(jsonObject, "status"));
		setTransferredAt(parseOptionalDate(jsonObject, "transferred_at"));
		setUnitAssignmentId(parseInteger(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setUserId(parseOptionalInteger(jsonObject, "user_id"));
		setArrivalPlatform(Platform.parse(jsonObject, "arrival_platform"));
		if (getArrivalPlatform().isPresent()) {
			setArrivalPlatformId(getArrivalPlatform().get().getId());
		}
		setArrivalSchedule(OperationSchedule.parse(jsonObject, "arrival_schedule"));
		if (getArrivalSchedule().isPresent()) {
			setArrivalScheduleId(getArrivalSchedule().get().getId());
		}
		setDemand(Demand.parse(jsonObject, "demand"));
		if (getDemand().isPresent()) {
			setDemandId(getDemand().get().getId());
		}
		setDeparturePlatform(Platform.parse(jsonObject, "departure_platform"));
		if (getDeparturePlatform().isPresent()) {
			setDeparturePlatformId(getDeparturePlatform().get().getId());
		}
		setDepartureSchedule(OperationSchedule.parse(jsonObject, "departure_schedule"));
		if (getDepartureSchedule().isPresent()) {
			setDepartureScheduleId(getDepartureSchedule().get().getId());
		}
		setOperator(Operator.parse(jsonObject, "operator"));
		if (getOperator().isPresent()) {
			setOperatorId(getOperator().get().getId());
		}
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		if (getServiceProvider().isPresent()) {
			setServiceProviderId(getServiceProvider().get().getId());
		}
		setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));
		if (getUnitAssignment().isPresent()) {
			setUnitAssignmentId(getUnitAssignment().get().getId());
		}
		setUser(User.parse(jsonObject, "user"));
		if (getUser().isPresent()) {
			setUserId(getUser().get().getId());
		}
	}

	public static Optional<Reservation> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<Reservation>absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<Reservation> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.<Reservation>of(new Reservation(jsonObject));
	}

	public static LinkedList<Reservation> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Reservation>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Reservation> parseList(JSONArray jsonArray) throws JSONException, ParseException {
		LinkedList<Reservation> models = new LinkedList<Reservation>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new Reservation(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("arrival_lock", toJSON(getArrivalLock().orNull()));
		jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatformId().orNull()));
		jsonObject.put("arrival_schedule_id", toJSON(getArrivalScheduleId().orNull()));
		jsonObject.put("arrival_time", toJSON(getArrivalTime()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("demand_id", toJSON(getDemandId().orNull()));
		jsonObject.put("departure_lock", toJSON(getDepartureLock().orNull()));
		jsonObject.put("departure_platform_id", toJSON(getDeparturePlatformId().orNull()));
		jsonObject.put("departure_schedule_id", toJSON(getDepartureScheduleId().orNull()));
		jsonObject.put("departure_time", toJSON(getDepartureTime()));
		jsonObject.put("dummy", toJSON(getDummy().orNull()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("memo", toJSON(getMemo().orNull()));
		jsonObject.put("operator_id", toJSON(getOperatorId().orNull()));
		jsonObject.put("passenger_count", toJSON(getPassengerCount()));
		jsonObject.put("payment", toJSON(getPayment()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("status", toJSON(getStatus()));
		jsonObject.put("transferred_at", toJSON(getTransferredAt().orNull()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_id", toJSON(getUserId().orNull()));

		if (getArrivalPlatform().isPresent()) {
			jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatform().get().getId()));
		}

		if (getArrivalSchedule().isPresent()) {
			jsonObject.put("arrival_schedule_id", toJSON(getArrivalSchedule().get().getId()));
		}

		if (getDemand().isPresent()) {
			jsonObject.put("demand_id", toJSON(getDemand().get().getId()));
		}

		if (getDeparturePlatform().isPresent()) {
			jsonObject.put("departure_platform_id", toJSON(getDeparturePlatform().get().getId()));
		}

		if (getDepartureSchedule().isPresent()) {
			jsonObject.put("departure_schedule_id", toJSON(getDepartureSchedule().get().getId()));
		}

		if (getOperator().isPresent()) {
			jsonObject.put("operator_id", toJSON(getOperator().get().getId()));
		}

		if (getServiceProvider().isPresent()) {
			jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
		}

		if (getUnitAssignment().isPresent()) {
			jsonObject.put("unit_assignment_id", toJSON(getUnitAssignment().get().getId()));
		}

		if (getUser().isPresent()) {
			jsonObject.put("user_id", toJSON(getUser().get().getId()));
		}
		return jsonObject;
	}

	private Optional<Boolean> arrivalLock = Optional.<Boolean>absent();

	public Optional<Boolean> getArrivalLock() {
		return wrapNull(arrivalLock);
	}

	public void setArrivalLock(Optional<Boolean> arrivalLock) {
		this.arrivalLock = wrapNull(arrivalLock);
	}

	public void setArrivalLock(Boolean arrivalLock) {
		this.arrivalLock = Optional.fromNullable(arrivalLock);
	}

	public void clearArrivalLock() {
		this.arrivalLock = Optional.<Boolean>absent();
	}

	private Optional<Integer> arrivalPlatformId = Optional.<Integer>absent();

	public Optional<Integer> getArrivalPlatformId() {
		return wrapNull(arrivalPlatformId);
	}

	public void setArrivalPlatformId(Optional<Integer> arrivalPlatformId) {
		this.arrivalPlatformId = wrapNull(arrivalPlatformId);
	}

	public void setArrivalPlatformId(Integer arrivalPlatformId) {
		this.arrivalPlatformId = Optional.fromNullable(arrivalPlatformId);
	}

	public void clearArrivalPlatformId() {
		this.arrivalPlatformId = Optional.<Integer>absent();
	}

	private Optional<Integer> arrivalScheduleId = Optional.<Integer>absent();

	public Optional<Integer> getArrivalScheduleId() {
		return wrapNull(arrivalScheduleId);
	}

	public void setArrivalScheduleId(Optional<Integer> arrivalScheduleId) {
		this.arrivalScheduleId = wrapNull(arrivalScheduleId);
	}

	public void setArrivalScheduleId(Integer arrivalScheduleId) {
		this.arrivalScheduleId = Optional.fromNullable(arrivalScheduleId);
	}

	public void clearArrivalScheduleId() {
		this.arrivalScheduleId = Optional.<Integer>absent();
	}

	private Date arrivalTime = new Date();

	public Date getArrivalTime() {
		return wrapNull(arrivalTime);
	}

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = wrapNull(arrivalTime);
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

	private Optional<Integer> demandId = Optional.<Integer>absent();

	public Optional<Integer> getDemandId() {
		return wrapNull(demandId);
	}

	public void setDemandId(Optional<Integer> demandId) {
		this.demandId = wrapNull(demandId);
	}

	public void setDemandId(Integer demandId) {
		this.demandId = Optional.fromNullable(demandId);
	}

	public void clearDemandId() {
		this.demandId = Optional.<Integer>absent();
	}

	private Optional<Boolean> departureLock = Optional.<Boolean>absent();

	public Optional<Boolean> getDepartureLock() {
		return wrapNull(departureLock);
	}

	public void setDepartureLock(Optional<Boolean> departureLock) {
		this.departureLock = wrapNull(departureLock);
	}

	public void setDepartureLock(Boolean departureLock) {
		this.departureLock = Optional.fromNullable(departureLock);
	}

	public void clearDepartureLock() {
		this.departureLock = Optional.<Boolean>absent();
	}

	private Optional<Integer> departurePlatformId = Optional.<Integer>absent();

	public Optional<Integer> getDeparturePlatformId() {
		return wrapNull(departurePlatformId);
	}

	public void setDeparturePlatformId(Optional<Integer> departurePlatformId) {
		this.departurePlatformId = wrapNull(departurePlatformId);
	}

	public void setDeparturePlatformId(Integer departurePlatformId) {
		this.departurePlatformId = Optional.fromNullable(departurePlatformId);
	}

	public void clearDeparturePlatformId() {
		this.departurePlatformId = Optional.<Integer>absent();
	}

	private Optional<Integer> departureScheduleId = Optional.<Integer>absent();

	public Optional<Integer> getDepartureScheduleId() {
		return wrapNull(departureScheduleId);
	}

	public void setDepartureScheduleId(Optional<Integer> departureScheduleId) {
		this.departureScheduleId = wrapNull(departureScheduleId);
	}

	public void setDepartureScheduleId(Integer departureScheduleId) {
		this.departureScheduleId = Optional.fromNullable(departureScheduleId);
	}

	public void clearDepartureScheduleId() {
		this.departureScheduleId = Optional.<Integer>absent();
	}

	private Date departureTime = new Date();

	public Date getDepartureTime() {
		return wrapNull(departureTime);
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = wrapNull(departureTime);
	}

	private Optional<Boolean> dummy = Optional.<Boolean>absent();

	public Optional<Boolean> getDummy() {
		return wrapNull(dummy);
	}

	public void setDummy(Optional<Boolean> dummy) {
		this.dummy = wrapNull(dummy);
	}

	public void setDummy(Boolean dummy) {
		this.dummy = Optional.fromNullable(dummy);
	}

	public void clearDummy() {
		this.dummy = Optional.<Boolean>absent();
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Optional<String> memo = Optional.<String>absent();

	public Optional<String> getMemo() {
		return wrapNull(memo);
	}

	public void setMemo(Optional<String> memo) {
		this.memo = wrapNull(memo);
	}

	public void setMemo(String memo) {
		this.memo = Optional.fromNullable(memo);
	}

	public void clearMemo() {
		this.memo = Optional.<String>absent();
	}

	private Optional<Integer> operatorId = Optional.<Integer>absent();

	public Optional<Integer> getOperatorId() {
		return wrapNull(operatorId);
	}

	public void setOperatorId(Optional<Integer> operatorId) {
		this.operatorId = wrapNull(operatorId);
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = Optional.fromNullable(operatorId);
	}

	public void clearOperatorId() {
		this.operatorId = Optional.<Integer>absent();
	}

	private Integer passengerCount = 0;

	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	public void setPassengerCount(Integer passengerCount) {
		this.passengerCount = wrapNull(passengerCount);
	}

	private Integer payment = 0;

	public Integer getPayment() {
		return wrapNull(payment);
	}

	public void setPayment(Integer payment) {
		this.payment = wrapNull(payment);
	}

	private Optional<Integer> serviceProviderId = Optional.<Integer>absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		this.serviceProviderId = Optional.fromNullable(serviceProviderId);
	}

	public void clearServiceProviderId() {
		this.serviceProviderId = Optional.<Integer>absent();
	}

	private Integer status = 0;

	public Integer getStatus() {
		return wrapNull(status);
	}

	public void setStatus(Integer status) {
		this.status = wrapNull(status);
	}

	private Optional<Date> transferredAt = Optional.<Date>absent();

	public Optional<Date> getTransferredAt() {
		return wrapNull(transferredAt);
	}

	public void setTransferredAt(Optional<Date> transferredAt) {
		this.transferredAt = wrapNull(transferredAt);
	}

	public void setTransferredAt(Date transferredAt) {
		this.transferredAt = Optional.fromNullable(transferredAt);
	}

	public void clearTransferredAt() {
		this.transferredAt = Optional.<Date>absent();
	}

	private Integer unitAssignmentId = 0;

	public Integer getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Integer unitAssignmentId) {
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<Integer> userId = Optional.<Integer>absent();

	public Optional<Integer> getUserId() {
		return wrapNull(userId);
	}

	public void setUserId(Optional<Integer> userId) {
		this.userId = wrapNull(userId);
	}

	public void setUserId(Integer userId) {
		this.userId = Optional.fromNullable(userId);
	}

	public void clearUserId() {
		this.userId = Optional.<Integer>absent();
	}

	private Optional<Platform> arrivalPlatform = Optional.<Platform>absent();

	public Optional<Platform> getArrivalPlatform() {
		return wrapNull(arrivalPlatform);
	}

	public void setArrivalPlatform(Optional<Platform> arrivalPlatform) {
		this.arrivalPlatform = wrapNull(arrivalPlatform);
	}

	public void setArrivalPlatform(Platform arrivalPlatform) {
		this.arrivalPlatform = Optional.<Platform>fromNullable(arrivalPlatform);
	}

	public void clearArrivalPlatform() {
		this.arrivalPlatform = Optional.<Platform>absent();
	}

	private Optional<OperationSchedule> arrivalSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getArrivalSchedule() {
		return wrapNull(arrivalSchedule);
	}

	public void setArrivalSchedule(Optional<OperationSchedule> arrivalSchedule) {
		this.arrivalSchedule = wrapNull(arrivalSchedule);
	}

	public void setArrivalSchedule(OperationSchedule arrivalSchedule) {
		this.arrivalSchedule = Optional.<OperationSchedule>fromNullable(arrivalSchedule);
	}

	public void clearArrivalSchedule() {
		this.arrivalSchedule = Optional.<OperationSchedule>absent();
	}

	private Optional<Demand> demand = Optional.<Demand>absent();

	public Optional<Demand> getDemand() {
		return wrapNull(demand);
	}

	public void setDemand(Optional<Demand> demand) {
		this.demand = wrapNull(demand);
	}

	public void setDemand(Demand demand) {
		this.demand = Optional.<Demand>fromNullable(demand);
	}

	public void clearDemand() {
		this.demand = Optional.<Demand>absent();
	}

	private Optional<Platform> departurePlatform = Optional.<Platform>absent();

	public Optional<Platform> getDeparturePlatform() {
		return wrapNull(departurePlatform);
	}

	public void setDeparturePlatform(Optional<Platform> departurePlatform) {
		this.departurePlatform = wrapNull(departurePlatform);
	}

	public void setDeparturePlatform(Platform departurePlatform) {
		this.departurePlatform = Optional.<Platform>fromNullable(departurePlatform);
	}

	public void clearDeparturePlatform() {
		this.departurePlatform = Optional.<Platform>absent();
	}

	private Optional<OperationSchedule> departureSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getDepartureSchedule() {
		return wrapNull(departureSchedule);
	}

	public void setDepartureSchedule(Optional<OperationSchedule> departureSchedule) {
		this.departureSchedule = wrapNull(departureSchedule);
	}

	public void setDepartureSchedule(OperationSchedule departureSchedule) {
		this.departureSchedule = Optional.<OperationSchedule>fromNullable(departureSchedule);
	}

	public void clearDepartureSchedule() {
		this.departureSchedule = Optional.<OperationSchedule>absent();
	}

	private Optional<Operator> operator = Optional.<Operator>absent();

	public Optional<Operator> getOperator() {
		return wrapNull(operator);
	}

	public void setOperator(Optional<Operator> operator) {
		this.operator = wrapNull(operator);
	}

	public void setOperator(Operator operator) {
		this.operator = Optional.<Operator>fromNullable(operator);
	}

	public void clearOperator() {
		this.operator = Optional.<Operator>absent();
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.<ServiceProvider>fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.<ServiceProvider>absent();
	}

	private Optional<UnitAssignment> unitAssignment = Optional.<UnitAssignment>absent();

	public Optional<UnitAssignment> getUnitAssignment() {
		return wrapNull(unitAssignment);
	}

	public void setUnitAssignment(Optional<UnitAssignment> unitAssignment) {
		this.unitAssignment = wrapNull(unitAssignment);
	}

	public void setUnitAssignment(UnitAssignment unitAssignment) {
		this.unitAssignment = Optional.<UnitAssignment>fromNullable(unitAssignment);
	}

	public void clearUnitAssignment() {
		this.unitAssignment = Optional.<UnitAssignment>absent();
	}

	private Optional<User> user = Optional.<User>absent();

	public Optional<User> getUser() {
		return wrapNull(user);
	}

	public void setUser(Optional<User> user) {
		this.user = wrapNull(user);
	}

	public void setUser(User user) {
		this.user = Optional.<User>fromNullable(user);
	}

	public void clearUser() {
		this.user = Optional.<User>absent();
	}
}
