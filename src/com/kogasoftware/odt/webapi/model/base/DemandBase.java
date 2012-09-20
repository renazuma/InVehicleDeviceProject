package com.kogasoftware.odt.webapi.model.base;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;
import com.kogasoftware.odt.webapi.WebAPI;
import com.kogasoftware.odt.webapi.WebAPI.ResponseConverter;
import com.kogasoftware.odt.webapi.model.*;

@SuppressWarnings("unused")
public abstract class DemandBase extends Model {
	private static final long serialVersionUID = 4866728639568179335L;
	public static final ResponseConverter<Demand> RESPONSE_CONVERTER = new ResponseConverter<Demand>() {
		@Override
		public Demand convert(byte[] rawResponse) throws JSONException {
			return parse(WebAPI.parseJSONObject(rawResponse));
		}
	};
	public static final ResponseConverter<List<Demand>> LIST_RESPONSE_CONVERTER = new ResponseConverter<List<Demand>>() {
		@Override
		public List<Demand> convert(byte[] rawResponse) throws JSONException {
			return parseList(WebAPI.parseJSONArray(rawResponse));
		}
	};
	protected void refreshUpdatedAt() {
		setUpdatedAt(new Date());
	}
	@Override
	public void fill(JSONObject jsonObject) throws JSONException {
		setArrivalPlatformId(parseOptionalInteger(jsonObject, "arrival_platform_id"));
		setArrivalTime(parseOptionalDate(jsonObject, "arrival_time"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDeparturePlatformId(parseOptionalInteger(jsonObject, "departure_platform_id"));
		setDepartureTime(parseOptionalDate(jsonObject, "departure_time"));
		setId(parseInteger(jsonObject, "id"));
		setMemo(parseOptionalString(jsonObject, "memo"));
		setPassengerCount(parseInteger(jsonObject, "passenger_count"));
		setRepeat(parseOptionalBoolean(jsonObject, "repeat"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setStoppageTime(parseOptionalInteger(jsonObject, "stoppage_time"));
		setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		setUserId(parseInteger(jsonObject, "user_id"));
		setArrivalPlatform(Platform.parse(jsonObject, "arrival_platform"));
		setDeparturePlatform(Platform.parse(jsonObject, "departure_platform"));
		setFellowUsers(User.parseList(jsonObject, "fellow_users"));
		setReservation(Reservation.parse(jsonObject, "reservation"));
		setReservationCandidates(ReservationCandidate.parseList(jsonObject, "reservation_candidates"));
		setReservationUsers(ReservationUser.parseList(jsonObject, "reservation_users"));
		setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));
		setUser(User.parse(jsonObject, "user"));

		setUpdatedAt(parseDate(jsonObject, "updated_at"));
	}

	public static Optional<Demand> parse(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return Optional.of(parse(jsonObject.getJSONObject(key)));
	}

	public static Demand parse(JSONObject jsonObject) throws JSONException {
		Demand model = new Demand();
		model.fill(jsonObject);
		return model;
	}

	public static LinkedList<Demand> parseList(JSONObject jsonObject, String key) throws JSONException {
		if (!jsonObject.has(key)) {
			return new LinkedList<Demand>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<Demand> parseList(JSONArray jsonArray) throws JSONException {
		LinkedList<Demand> models = new LinkedList<Demand>();
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
		jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatformId()));
		jsonObject.put("arrival_time", toJSON(getArrivalTime()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt()));
		jsonObject.put("departure_platform_id", toJSON(getDeparturePlatformId()));
		jsonObject.put("departure_time", toJSON(getDepartureTime()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("memo", toJSON(getMemo()));
		jsonObject.put("passenger_count", toJSON(getPassengerCount()));
		jsonObject.put("repeat", toJSON(getRepeat()));
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
		if (getReservation().isPresent() && recursive) {
			jsonObject.put("reservation", getReservation().get().toJSONObject(true, nextDepth));
		}
		if (getReservationCandidates().size() > 0 && recursive) {
			jsonObject.put("reservation_candidates", toJSON(getReservationCandidates(), true, nextDepth));
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
	public Demand cloneByJSON() throws JSONException {
		return parse(toJSONObject(true));
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

	private Optional<Date> arrivalTime = Optional.absent();

	public Optional<Date> getArrivalTime() {
		return wrapNull(arrivalTime);
	}

	public void setArrivalTime(Optional<Date> arrivalTime) {
		refreshUpdatedAt();
		this.arrivalTime = wrapNull(arrivalTime);
	}

	public void setArrivalTime(Date arrivalTime) {
		setArrivalTime(Optional.fromNullable(arrivalTime));
	}

	public void clearArrivalTime() {
		setArrivalTime(Optional.<Date>absent());
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

	private Optional<Date> departureTime = Optional.absent();

	public Optional<Date> getDepartureTime() {
		return wrapNull(departureTime);
	}

	public void setDepartureTime(Optional<Date> departureTime) {
		refreshUpdatedAt();
		this.departureTime = wrapNull(departureTime);
	}

	public void setDepartureTime(Date departureTime) {
		setDepartureTime(Optional.fromNullable(departureTime));
	}

	public void clearDepartureTime() {
		setDepartureTime(Optional.<Date>absent());
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

	private Integer passengerCount = 0;

	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	public void setPassengerCount(Integer passengerCount) {
		refreshUpdatedAt();
		this.passengerCount = wrapNull(passengerCount);
	}

	private Optional<Boolean> repeat = Optional.absent();

	public Optional<Boolean> getRepeat() {
		return wrapNull(repeat);
	}

	public void setRepeat(Optional<Boolean> repeat) {
		refreshUpdatedAt();
		this.repeat = wrapNull(repeat);
	}

	public void setRepeat(Boolean repeat) {
		setRepeat(Optional.fromNullable(repeat));
	}

	public void clearRepeat() {
		setRepeat(Optional.<Boolean>absent());
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

	private Integer userId = 0;

	public Integer getUserId() {
		return wrapNull(userId);
	}

	public void setUserId(Integer userId) {
		refreshUpdatedAt();
		this.userId = wrapNull(userId);
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

	private Optional<Reservation> reservation = Optional.<Reservation>absent();

	public Optional<Reservation> getReservation() {
		return wrapNull(reservation);
	}

	public void setReservation(Optional<Reservation> reservation) {
		this.reservation = wrapNull(reservation);
	}

	public void setReservation(Reservation reservation) {
		setReservation(Optional.fromNullable(reservation));
	}

	public void clearReservation() {
		setReservation(Optional.<Reservation>absent());
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
}
