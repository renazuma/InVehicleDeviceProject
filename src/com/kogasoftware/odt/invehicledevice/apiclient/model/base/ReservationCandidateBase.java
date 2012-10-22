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
public abstract class ReservationCandidateBase extends Model {
	private static final long serialVersionUID = 6717105935208408219L;
	public static final ResponseConverter<ReservationCandidate> RESPONSE_CONVERTER = new ResponseConverter<ReservationCandidate>() {
		@Override
		public ReservationCandidate convert(byte[] rawResponse) throws JSONException {
			return parse(ApiClients.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<ReservationCandidate>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<ReservationCandidate>>() {
		@Override
		public List<ReservationCandidate> convert(byte[] rawResponse) throws JSONException {
			return parseList(ApiClients.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setAccuracy(parseOptionalFloat(jsonObject, "accuracy"));
		setArrivalLock(parseOptionalString(jsonObject, "arrival_lock"));
		setArrivalPlatformId(parseInteger(jsonObject, "arrival_platform_id"));
		setArrivalTime(parseDate(jsonObject, "arrival_time"));
		setCharacteristic(parseOptionalString(jsonObject, "characteristic"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDemandId(parseOptionalInteger(jsonObject, "demand_id"));
		setDepartureLock(parseOptionalString(jsonObject, "departure_lock"));
		setDeparturePlatformId(parseInteger(jsonObject, "departure_platform_id"));
		setDepartureTime(parseDate(jsonObject, "departure_time"));
		setId(parseInteger(jsonObject, "id"));
		setPassengerCount(parseInteger(jsonObject, "passenger_count"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setStoppageTime(parseOptionalInteger(jsonObject, "stoppage_time"));
		setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		setUserId(parseOptionalInteger(jsonObject, "user_id"));
		setArrivalPlatform(Platform.parse(jsonObject, "arrival_platform"));
		setDemand(Demand.parse(jsonObject, "demand"));
		setDeparturePlatform(Platform.parse(jsonObject, "departure_platform"));
		setFellowUsers(User.parseList(jsonObject, "fellow_users"));
		setReservationUsers(ReservationUser.parseList(jsonObject, "reservation_users"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));
		setUser(User.parse(jsonObject, "user"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<ReservationCandidate> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static ReservationCandidate parse(JSONObject jsonObject) throws JSONException {
		ReservationCandidate model = new ReservationCandidate();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<ReservationCandidate> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ReservationCandidate>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ReservationCandidate> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<ReservationCandidate> models = new LinkedList<ReservationCandidate>();
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
		jsonObject.put("accuracy", toJSON(getAccuracy()));
		jsonObject.put("arrival_lock", toJSON(getArrivalLock()));
		jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatformId()));
		jsonObject.put("arrival_time", toJSON(getArrivalTime()));
		jsonObject.put("characteristic", toJSON(getCharacteristic()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("demand_id", toJSON(getDemandId()));
		jsonObject.put("departure_lock", toJSON(getDepartureLock()));
		jsonObject.put("departure_platform_id", toJSON(getDeparturePlatformId()));
		jsonObject.put("departure_time", toJSON(getDepartureTime()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("passenger_count", toJSON(getPassengerCount()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId()));
		jsonObject.put("stoppage_time", toJSON(getStoppageTime()));
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
		if (getFellowUsers().size() > 0 && recursive) {
			jsonObject.put("fellow_users", toJSON(getFellowUsers(), true, nextDepth));
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
	public ReservationCandidate cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
	}

	private Optional<Float> accuracy = Optional.absent();

	public Optional<Float> getAccuracy() {
		return wrapNull(accuracy);
	}

	public void setAccuracy(Optional<Float> accuracy) {
		refreshUpdatedAt();
		this.accuracy = wrapNull(accuracy);
	}

	public void setAccuracy(Float accuracy) {
		setAccuracy(Optional.fromNullable(accuracy));
	}

	public void clearAccuracy() {
		setAccuracy(Optional.<Float>absent());
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

	private Integer arrivalPlatformId = 0;

	public Integer getArrivalPlatformId() {
		return wrapNull(arrivalPlatformId);
	}

	public void setArrivalPlatformId(Integer arrivalPlatformId) {
		refreshUpdatedAt();
		this.arrivalPlatformId = wrapNull(arrivalPlatformId);
	}

	private Date arrivalTime = new Date();

	public Date getArrivalTime() {
		return wrapNull(arrivalTime);
	}

	public void setArrivalTime(Date arrivalTime) {
		refreshUpdatedAt();
		this.arrivalTime = wrapNull(arrivalTime);
	}

	private Optional<String> characteristic = Optional.absent();

	public Optional<String> getCharacteristic() {
		return wrapNull(characteristic);
	}

	public void setCharacteristic(Optional<String> characteristic) {
		refreshUpdatedAt();
		this.characteristic = wrapNull(characteristic);
	}

	public void setCharacteristic(String characteristic) {
		setCharacteristic(Optional.fromNullable(characteristic));
	}

	public void clearCharacteristic() {
		setCharacteristic(Optional.<String>absent());
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

	private Integer departurePlatformId = 0;

	public Integer getDeparturePlatformId() {
		return wrapNull(departurePlatformId);
	}

	public void setDeparturePlatformId(Integer departurePlatformId) {
		refreshUpdatedAt();
		this.departurePlatformId = wrapNull(departurePlatformId);
	}

	private Date departureTime = new Date();

	public Date getDepartureTime() {
		return wrapNull(departureTime);
	}

	public void setDepartureTime(Date departureTime) {
		refreshUpdatedAt();
		this.departureTime = wrapNull(departureTime);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		refreshUpdatedAt();
		this.id = wrapNull(id);
	}

	private Integer passengerCount = 0;

	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	public void setPassengerCount(Integer passengerCount) {
		refreshUpdatedAt();
		this.passengerCount = wrapNull(passengerCount);
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

	private Optional<Integer> unitAssignmentId = Optional.absent();

	public Optional<Integer> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Integer> unitAssignmentId) {
		refreshUpdatedAt();
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Integer unitAssignmentId) {
		setUnitAssignmentId(Optional.fromNullable(unitAssignmentId));
	}

	public void clearUnitAssignmentId() {
		setUnitAssignmentId(Optional.<Integer>absent());
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
			.append(accuracy)
			.append(arrivalLock)
			.append(arrivalPlatformId)
			.append(arrivalTime)
			.append(characteristic)
			.append(createdAt)
			.append(deletedAt)
			.append(demandId)
			.append(departureLock)
			.append(departurePlatformId)
			.append(departureTime)
			.append(id)
			.append(passengerCount)
			.append(serviceProviderId)
			.append(stoppageTime)
			.append(unitAssignmentId)
			.append(updatedAt)
			.append(userId)
			.append(arrivalPlatform)
			.append(demand)
			.append(departurePlatform)
			.append(fellowUsers)
			.append(reservationUsers)
			.append(serviceProvider)
			.append(unitAssignment)
			.append(user)
			.toHashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if(!(obj instanceof ReservationCandidateBase)) {
			return false;
		}
		ReservationCandidateBase other = (ReservationCandidateBase) obj;
		return new EqualsBuilder()
			.append(accuracy, other.accuracy)
			.append(arrivalLock, other.arrivalLock)
			.append(arrivalPlatformId, other.arrivalPlatformId)
			.append(arrivalTime, other.arrivalTime)
			.append(characteristic, other.characteristic)
			.append(createdAt, other.createdAt)
			.append(deletedAt, other.deletedAt)
			.append(demandId, other.demandId)
			.append(departureLock, other.departureLock)
			.append(departurePlatformId, other.departurePlatformId)
			.append(departureTime, other.departureTime)
			.append(id, other.id)
			.append(passengerCount, other.passengerCount)
			.append(serviceProviderId, other.serviceProviderId)
			.append(stoppageTime, other.stoppageTime)
			.append(unitAssignmentId, other.unitAssignmentId)
			.append(updatedAt, other.updatedAt)
			.append(userId, other.userId)
			.append(arrivalPlatform, other.arrivalPlatform)
			.append(demand, other.demand)
			.append(departurePlatform, other.departurePlatform)
			.append(fellowUsers, other.fellowUsers)
			.append(reservationUsers, other.reservationUsers)
			.append(serviceProvider, other.serviceProvider)
			.append(unitAssignment, other.unitAssignment)
			.append(user, other.user)
			.isEquals();
	}
}
