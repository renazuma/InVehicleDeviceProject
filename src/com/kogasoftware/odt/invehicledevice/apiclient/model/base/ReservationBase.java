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
public abstract class ReservationBase extends Model {
	private static final long serialVersionUID = 6221949246110673188L;
	public static final ResponseConverter<Reservation> RESPONSE_CONVERTER = new ResponseConverter<Reservation>() {
		@Override
		public Reservation convert(byte[] rawResponse) throws JSONException {
			return parse(ApiClients.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<Reservation>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<Reservation>>() {
		@Override
		public List<Reservation> convert(byte[] rawResponse) throws JSONException {
			return parseList(ApiClients.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setArrivalLock(parseOptionalString(jsonObject, "arrival_lock"));
		setArrivalPlatformId(parseOptionalInteger(jsonObject, "arrival_platform_id"));
		setArrivalScheduleId(parseOptionalInteger(jsonObject, "arrival_schedule_id"));
		setArrivalTime(parseDate(jsonObject, "arrival_time"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDemandId(parseOptionalInteger(jsonObject, "demand_id"));
		setDepartureLock(parseOptionalString(jsonObject, "departure_lock"));
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
		setStoppageTime(parseOptionalInteger(jsonObject, "stoppage_time"));
		setTransferredAt(parseOptionalDate(jsonObject, "transferred_at"));
		setUnitAssignmentId(parseInteger(jsonObject, "unit_assignment_id"));
		setUserId(parseOptionalInteger(jsonObject, "user_id"));
		setArrivalPlatform(Platform.parse(jsonObject, "arrival_platform"));
		setArrivalSchedule(OperationSchedule.parse(jsonObject, "arrival_schedule"));
		setDemand(Demand.parse(jsonObject, "demand"));
		setDeparturePlatform(Platform.parse(jsonObject, "departure_platform"));
		setDepartureSchedule(OperationSchedule.parse(jsonObject, "departure_schedule"));
		setFellowUsers(User.parseList(jsonObject, "fellow_users"));
		setOperator(Operator.parse(jsonObject, "operator"));
		setPassengerRecords(PassengerRecord.parseList(jsonObject, "passenger_records"));
		setReservationUsers(ReservationUser.parseList(jsonObject, "reservation_users"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));
		setUser(User.parse(jsonObject, "user"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<Reservation> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static Reservation parse(JSONObject jsonObject) throws JSONException {
		Reservation model = new Reservation();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<Reservation> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Reservation>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Reservation> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<Reservation> models = new LinkedList<Reservation>();
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
		jsonObject.put("arrival_lock", toJSON(getArrivalLock()));
		jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatformId()));
		jsonObject.put("arrival_schedule_id", toJSON(getArrivalScheduleId()));
		jsonObject.put("arrival_time", toJSON(getArrivalTime()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("demand_id", toJSON(getDemandId()));
		jsonObject.put("departure_lock", toJSON(getDepartureLock()));
		jsonObject.put("departure_platform_id", toJSON(getDeparturePlatformId()));
		jsonObject.put("departure_schedule_id", toJSON(getDepartureScheduleId()));
		jsonObject.put("departure_time", toJSON(getDepartureTime()));
		jsonObject.put("dummy", toJSON(getDummy()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("memo", toJSON(getMemo()));
		jsonObject.put("operator_id", toJSON(getOperatorId()));
		jsonObject.put("passenger_count", toJSON(getPassengerCount()));
		jsonObject.put("payment", toJSON(getPayment()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("status", toJSON(getStatus()));
		jsonObject.put("stoppage_time", toJSON(getStoppageTime()));
		jsonObject.put("transferred_at", toJSON(getTransferredAt()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_id", toJSON(getUserId()));
		if (getArrivalPlatform().isPresent()) {
			if (recursive) {
				jsonObject.put("arrival_platform", getArrivalPlatform().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatform().get().getId()));
			}
		}
		if (getArrivalSchedule().isPresent()) {
			if (recursive) {
				jsonObject.put("arrival_schedule", getArrivalSchedule().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("arrival_schedule_id", toJSON(getArrivalSchedule().get().getId()));
			}
		}
		if (getDemand().isPresent()) {
			if (recursive) {
				jsonObject.put("demand", getDemand().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("demand_id", toJSON(getDemand().get().getId()));
			}
		}
		if (getDeparturePlatform().isPresent()) {
			if (recursive) {
				jsonObject.put("departure_platform", getDeparturePlatform().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("departure_platform_id", toJSON(getDeparturePlatform().get().getId()));
			}
		}
		if (getDepartureSchedule().isPresent()) {
			if (recursive) {
				jsonObject.put("departure_schedule", getDepartureSchedule().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("departure_schedule_id", toJSON(getDepartureSchedule().get().getId()));
			}
		}
		if (getFellowUsers().size() > 0 && recursive) {
			jsonObject.put("fellow_users", toJSON(getFellowUsers(), true, nextDepth));
		}
		if (getOperator().isPresent()) {
			if (recursive) {
				jsonObject.put("operator", getOperator().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("operator_id", toJSON(getOperator().get().getId()));
			}
		}
		if (getPassengerRecords().size() > 0 && recursive) {
			jsonObject.put("passenger_records", toJSON(getPassengerRecords(), true, nextDepth));
		}
		if (getReservationUsers().size() > 0 && recursive) {
			jsonObject.put("reservation_users", toJSON(getReservationUsers(), true, nextDepth));
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getUnitAssignment().isPresent()) {
			if (recursive) {
				jsonObject.put("unit_assignment", getUnitAssignment().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("unit_assignment_id", toJSON(getUnitAssignment().get().getId()));
			}
		}
		if (getUser().isPresent()) {
			if (recursive) {
				jsonObject.put("user", getUser().get().toJSONObject(true, nextDepth));
			} else {
				jsonObject.put("user_id", toJSON(getUser().get().getId()));
			}
		}
		return jsonObject;
	}

	@Override
	public Reservation cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Optional<String> arrivalLock = Optional.absent();

	public Optional<String> getArrivalLock() {
		return wrapNull(arrivalLock);
	}

	public void setArrivalLock(Optional<String> arrivalLock) {
		refreshUpdatedAt();
		this.arrivalLock = wrapNull(arrivalLock);
	}

	public void setArrivalLock(String arrivalLock) {
		setArrivalLock(Optional.fromNullable(arrivalLock));
	}

	public void clearArrivalLock() {
		setArrivalLock(Optional.<String>absent());
	}

	private Optional<Integer> arrivalPlatformId = Optional.absent();

	public Optional<Integer> getArrivalPlatformId() {
		return wrapNull(arrivalPlatformId);
	}

	public void setArrivalPlatformId(Optional<Integer> arrivalPlatformId) {
		refreshUpdatedAt();
		this.arrivalPlatformId = wrapNull(arrivalPlatformId);
	}

	public void setArrivalPlatformId(Integer arrivalPlatformId) {
		setArrivalPlatformId(Optional.fromNullable(arrivalPlatformId));
	}

	public void clearArrivalPlatformId() {
		setArrivalPlatformId(Optional.<Integer>absent());
	}

	private Optional<Integer> arrivalScheduleId = Optional.absent();

	public Optional<Integer> getArrivalScheduleId() {
		return wrapNull(arrivalScheduleId);
	}

	public void setArrivalScheduleId(Optional<Integer> arrivalScheduleId) {
		refreshUpdatedAt();
		this.arrivalScheduleId = wrapNull(arrivalScheduleId);
	}

	public void setArrivalScheduleId(Integer arrivalScheduleId) {
		setArrivalScheduleId(Optional.fromNullable(arrivalScheduleId));
	}

	public void clearArrivalScheduleId() {
		setArrivalScheduleId(Optional.<Integer>absent());
	}

	private Date arrivalTime = new Date();

	public Date getArrivalTime() {
		return wrapNull(arrivalTime);
	}

	public void setArrivalTime(Date arrivalTime) {
		refreshUpdatedAt();
		this.arrivalTime = wrapNull(arrivalTime);
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

	private Optional<Integer> demandId = Optional.absent();

	public Optional<Integer> getDemandId() {
		return wrapNull(demandId);
	}

	public void setDemandId(Optional<Integer> demandId) {
		refreshUpdatedAt();
		this.demandId = wrapNull(demandId);
	}

	public void setDemandId(Integer demandId) {
		setDemandId(Optional.fromNullable(demandId));
	}

	public void clearDemandId() {
		setDemandId(Optional.<Integer>absent());
	}

	private Optional<String> departureLock = Optional.absent();

	public Optional<String> getDepartureLock() {
		return wrapNull(departureLock);
	}

	public void setDepartureLock(Optional<String> departureLock) {
		refreshUpdatedAt();
		this.departureLock = wrapNull(departureLock);
	}

	public void setDepartureLock(String departureLock) {
		setDepartureLock(Optional.fromNullable(departureLock));
	}

	public void clearDepartureLock() {
		setDepartureLock(Optional.<String>absent());
	}

	private Optional<Integer> departurePlatformId = Optional.absent();

	public Optional<Integer> getDeparturePlatformId() {
		return wrapNull(departurePlatformId);
	}

	public void setDeparturePlatformId(Optional<Integer> departurePlatformId) {
		refreshUpdatedAt();
		this.departurePlatformId = wrapNull(departurePlatformId);
	}

	public void setDeparturePlatformId(Integer departurePlatformId) {
		setDeparturePlatformId(Optional.fromNullable(departurePlatformId));
	}

	public void clearDeparturePlatformId() {
		setDeparturePlatformId(Optional.<Integer>absent());
	}

	private Optional<Integer> departureScheduleId = Optional.absent();

	public Optional<Integer> getDepartureScheduleId() {
		return wrapNull(departureScheduleId);
	}

	public void setDepartureScheduleId(Optional<Integer> departureScheduleId) {
		refreshUpdatedAt();
		this.departureScheduleId = wrapNull(departureScheduleId);
	}

	public void setDepartureScheduleId(Integer departureScheduleId) {
		setDepartureScheduleId(Optional.fromNullable(departureScheduleId));
	}

	public void clearDepartureScheduleId() {
		setDepartureScheduleId(Optional.<Integer>absent());
	}

	private Date departureTime = new Date();

	public Date getDepartureTime() {
		return wrapNull(departureTime);
	}

	public void setDepartureTime(Date departureTime) {
		refreshUpdatedAt();
		this.departureTime = wrapNull(departureTime);
	}

	private Optional<Boolean> dummy = Optional.absent();

	public Optional<Boolean> getDummy() {
		return wrapNull(dummy);
	}

	public void setDummy(Optional<Boolean> dummy) {
		refreshUpdatedAt();
		this.dummy = wrapNull(dummy);
	}

	public void setDummy(Boolean dummy) {
		setDummy(Optional.fromNullable(dummy));
	}

	public void clearDummy() {
		setDummy(Optional.<Boolean>absent());
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private Optional<String> memo = Optional.absent();

	public Optional<String> getMemo() {
		return wrapNull(memo);
	}

	public void setMemo(Optional<String> memo) {
		refreshUpdatedAt();
		this.memo = wrapNull(memo);
	}

	public void setMemo(String memo) {
		setMemo(Optional.fromNullable(memo));
	}

	public void clearMemo() {
		setMemo(Optional.<String>absent());
	}

	private Optional<Integer> operatorId = Optional.absent();

	public Optional<Integer> getOperatorId() {
		return wrapNull(operatorId);
	}

	public void setOperatorId(Optional<Integer> operatorId) {
		refreshUpdatedAt();
		this.operatorId = wrapNull(operatorId);
	}

	public void setOperatorId(Integer operatorId) {
		setOperatorId(Optional.fromNullable(operatorId));
	}

	public void clearOperatorId() {
		setOperatorId(Optional.<Integer>absent());
	}

	private Integer passengerCount = 0;

	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	public void setPassengerCount(Integer passengerCount) {
		refreshUpdatedAt();
		this.passengerCount = wrapNull(passengerCount);
	}

	private Integer payment = 0;

	public Integer getPayment() {
		return wrapNull(payment);
	}

	public void setPayment(Integer payment) {
		refreshUpdatedAt();
		this.payment = wrapNull(payment);
	}

	private Optional<Integer> serviceProviderId = Optional.absent();

	public Optional<Integer> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Integer> serviceProviderId) {
		refreshUpdatedAt();
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Integer serviceProviderId) {
		setServiceProviderId(Optional.fromNullable(serviceProviderId));
	}

	public void clearServiceProviderId() {
		setServiceProviderId(Optional.<Integer>absent());
	}

	private Integer status = 0;

	public Integer getStatus() {
		return wrapNull(status);
	}

	public void setStatus(Integer status) {
		refreshUpdatedAt();
		this.status = wrapNull(status);
	}

	private Optional<Integer> stoppageTime = Optional.absent();

	public Optional<Integer> getStoppageTime() {
		return wrapNull(stoppageTime);
	}

	public void setStoppageTime(Optional<Integer> stoppageTime) {
		refreshUpdatedAt();
		this.stoppageTime = wrapNull(stoppageTime);
	}

	public void setStoppageTime(Integer stoppageTime) {
		setStoppageTime(Optional.fromNullable(stoppageTime));
	}

	public void clearStoppageTime() {
		setStoppageTime(Optional.<Integer>absent());
	}

	private Optional<Date> transferredAt = Optional.absent();

	public Optional<Date> getTransferredAt() {
		return wrapNull(transferredAt);
	}

	public void setTransferredAt(Optional<Date> transferredAt) {
		refreshUpdatedAt();
		this.transferredAt = wrapNull(transferredAt);
	}

	public void setTransferredAt(Date transferredAt) {
		setTransferredAt(Optional.fromNullable(transferredAt));
	}

	public void clearTransferredAt() {
		setTransferredAt(Optional.<Date>absent());
	}

	private Integer unitAssignmentId = 0;

	public Integer getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Integer unitAssignmentId) {
		refreshUpdatedAt();
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<Integer> userId = Optional.absent();

	public Optional<Integer> getUserId() {
		return wrapNull(userId);
	}

	public void setUserId(Optional<Integer> userId) {
		refreshUpdatedAt();
		this.userId = wrapNull(userId);
	}

	public void setUserId(Integer userId) {
		setUserId(Optional.fromNullable(userId));
	}

	public void clearUserId() {
		setUserId(Optional.<Integer>absent());
	}

	private Optional<Platform> arrivalPlatform = Optional.<Platform>absent();

	public Optional<Platform> getArrivalPlatform() {
		return wrapNull(arrivalPlatform);
	}

	public void setArrivalPlatform(Optional<Platform> arrivalPlatform) {
		this.arrivalPlatform = wrapNull(arrivalPlatform);
	}

	public void setArrivalPlatform(Platform arrivalPlatform) {
		setArrivalPlatform(Optional.fromNullable(arrivalPlatform));
	}

	public void clearArrivalPlatform() {
		setArrivalPlatform(Optional.<Platform>absent());
	}

	private Optional<OperationSchedule> arrivalSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getArrivalSchedule() {
		return wrapNull(arrivalSchedule);
	}

	public void setArrivalSchedule(Optional<OperationSchedule> arrivalSchedule) {
		this.arrivalSchedule = wrapNull(arrivalSchedule);
	}

	public void setArrivalSchedule(OperationSchedule arrivalSchedule) {
		setArrivalSchedule(Optional.fromNullable(arrivalSchedule));
	}

	public void clearArrivalSchedule() {
		setArrivalSchedule(Optional.<OperationSchedule>absent());
	}

	private Optional<Demand> demand = Optional.<Demand>absent();

	public Optional<Demand> getDemand() {
		return wrapNull(demand);
	}

	public void setDemand(Optional<Demand> demand) {
		this.demand = wrapNull(demand);
	}

	public void setDemand(Demand demand) {
		setDemand(Optional.fromNullable(demand));
	}

	public void clearDemand() {
		setDemand(Optional.<Demand>absent());
	}

	private Optional<Platform> departurePlatform = Optional.<Platform>absent();

	public Optional<Platform> getDeparturePlatform() {
		return wrapNull(departurePlatform);
	}

	public void setDeparturePlatform(Optional<Platform> departurePlatform) {
		this.departurePlatform = wrapNull(departurePlatform);
	}

	public void setDeparturePlatform(Platform departurePlatform) {
		setDeparturePlatform(Optional.fromNullable(departurePlatform));
	}

	public void clearDeparturePlatform() {
		setDeparturePlatform(Optional.<Platform>absent());
	}

	private Optional<OperationSchedule> departureSchedule = Optional.<OperationSchedule>absent();

	public Optional<OperationSchedule> getDepartureSchedule() {
		return wrapNull(departureSchedule);
	}

	public void setDepartureSchedule(Optional<OperationSchedule> departureSchedule) {
		this.departureSchedule = wrapNull(departureSchedule);
	}

	public void setDepartureSchedule(OperationSchedule departureSchedule) {
		setDepartureSchedule(Optional.fromNullable(departureSchedule));
	}

	public void clearDepartureSchedule() {
		setDepartureSchedule(Optional.<OperationSchedule>absent());
	}

	private LinkedList<User> fellowUsers = new LinkedList<User>();

	public List<User> getFellowUsers() {
		return wrapNull(fellowUsers);
	}

	public void setFellowUsers(Iterable<User> fellowUsers) {
		this.fellowUsers = wrapNull(fellowUsers);
	}

	public void clearFellowUsers() {
		setFellowUsers(new LinkedList<User>());
	}

	private Optional<Operator> operator = Optional.<Operator>absent();

	public Optional<Operator> getOperator() {
		return wrapNull(operator);
	}

	public void setOperator(Optional<Operator> operator) {
		this.operator = wrapNull(operator);
	}

	public void setOperator(Operator operator) {
		setOperator(Optional.fromNullable(operator));
	}

	public void clearOperator() {
		setOperator(Optional.<Operator>absent());
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

	private LinkedList<ReservationUser> reservationUsers = new LinkedList<ReservationUser>();

	public List<ReservationUser> getReservationUsers() {
		return wrapNull(reservationUsers);
	}

	public void setReservationUsers(Iterable<ReservationUser> reservationUsers) {
		this.reservationUsers = wrapNull(reservationUsers);
	}

	public void clearReservationUsers() {
		setReservationUsers(new LinkedList<ReservationUser>());
	}

	private Optional<ServiceProvider> serviceProvider = Optional.<ServiceProvider>absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		setServiceProvider(Optional.fromNullable(serviceProvider));
	}

	public void clearServiceProvider() {
		setServiceProvider(Optional.<ServiceProvider>absent());
	}

	private Optional<UnitAssignment> unitAssignment = Optional.<UnitAssignment>absent();

	public Optional<UnitAssignment> getUnitAssignment() {
		return wrapNull(unitAssignment);
	}

	public void setUnitAssignment(Optional<UnitAssignment> unitAssignment) {
		this.unitAssignment = wrapNull(unitAssignment);
	}

	public void setUnitAssignment(UnitAssignment unitAssignment) {
		setUnitAssignment(Optional.fromNullable(unitAssignment));
	}

	public void clearUnitAssignment() {
		setUnitAssignment(Optional.<UnitAssignment>absent());
	}

	private Optional<User> user = Optional.<User>absent();

	public Optional<User> getUser() {
		return wrapNull(user);
	}

	public void setUser(Optional<User> user) {
		this.user = wrapNull(user);
	}

	public void setUser(User user) {
		setUser(Optional.fromNullable(user));
	}

	public void clearUser() {
		setUser(Optional.<User>absent());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(arrivalLock)
			.append(arrivalPlatformId)
			.append(arrivalScheduleId)
			.append(arrivalTime)
			.append(createdAt)
			.append(deletedAt)
			.append(demandId)
			.append(departureLock)
			.append(departurePlatformId)
			.append(departureScheduleId)
			.append(departureTime)
			.append(dummy)
			.append(id)
			.append(memo)
			.append(operatorId)
			.append(passengerCount)
			.append(payment)
			.append(serviceProviderId)
			.append(status)
			.append(stoppageTime)
			.append(transferredAt)
			.append(unitAssignmentId)
			.append(updatedAt)
			.append(userId)
			.append(arrivalPlatform)
			.append(arrivalSchedule)
			.append(demand)
			.append(departurePlatform)
			.append(departureSchedule)
			.append(fellowUsers)
			.append(operator)
			.append(passengerRecords)
			.append(reservationUsers)
			.append(serviceProvider)
			.append(unitAssignment)
			.append(user)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof ReservationBase)) {
			return false;
		}
		ReservationBase other = (ReservationBase) obj;
		return new EqualsBuilder()
			.append(arrivalLock, other.arrivalLock)
			.append(arrivalPlatformId, other.arrivalPlatformId)
			.append(arrivalScheduleId, other.arrivalScheduleId)
			.append(arrivalTime, other.arrivalTime)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(demandId, other.demandId)
			.append(departureLock, other.departureLock)
			.append(departurePlatformId, other.departurePlatformId)
			.append(departureScheduleId, other.departureScheduleId)
			.append(departureTime, other.departureTime)
			.append(dummy, other.dummy)
			.append(id, other.id)
			.append(memo, other.memo)
			.append(operatorId, other.operatorId)
			.append(passengerCount, other.passengerCount)
			.append(payment, other.payment)
			.append(serviceProviderId, other.serviceProviderId)
			.append(status, other.status)
			.append(stoppageTime, other.stoppageTime)
			.append(transferredAt, other.transferredAt)
			.append(unitAssignmentId, other.unitAssignmentId)
			.append(updatedAt, other.updatedAt)
			.append(userId, other.userId)
			.append(arrivalPlatform, other.arrivalPlatform)
			.append(arrivalSchedule, other.arrivalSchedule)
			.append(demand, other.demand)
			.append(departurePlatform, other.departurePlatform)
			.append(departureSchedule, other.departureSchedule)
			.append(fellowUsers, other.fellowUsers)
			.append(operator, other.operator)
			.append(passengerRecords, other.passengerRecords)
			.append(reservationUsers, other.reservationUsers)
			.append(serviceProvider, other.serviceProvider)
			.append(unitAssignment, other.unitAssignment)
			.append(user, other.user)
			.isEquals();
	}
}
