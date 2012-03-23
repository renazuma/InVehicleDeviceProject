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
import com.kogasoftware.odt.webapi.WebAPI;

public class ReservationCandidate extends Model {
	private static final long serialVersionUID = 2513887995030438761L;
	public static final String JSON_NAME = "reservation_candidate";
	public static final String CONTROLLER_NAME = "reservation_candidates";

	public static class URL {
		public static final String ROOT = "/" + CONTROLLER_NAME;
	}

	public ReservationCandidate() {
	}

	public ReservationCandidate(JSONObject jsonObject) throws JSONException, ParseException {
		setArrivalPlatformId(parseLong(jsonObject, "arrival_platform_id"));
		setArrivalTime(parseDate(jsonObject, "arrival_time"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDemandId(parseOptionalLong(jsonObject, "demand_id"));
		setDeparturePlatformId(parseLong(jsonObject, "departure_platform_id"));
		setDepartureTime(parseDate(jsonObject, "departure_time"));
		setId(parseLong(jsonObject, "id"));
		setPassengerCount(parseLong(jsonObject, "passenger_count"));
		setServiceProviderId(parseOptionalLong(jsonObject, "service_provider_id"));
		setUnitAssignmentId(parseOptionalLong(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setUserId(parseOptionalLong(jsonObject, "user_id"));
		setArrivalPlatform(Platform.parse(jsonObject, "arrival_platform"));
		if (getArrivalPlatform().isPresent()) {
			setArrivalPlatformId(getArrivalPlatform().get().getId());
		}
		setDemand(Demand.parse(jsonObject, "demand"));
		if (getDemand().isPresent()) {
			setDemandId(getDemand().get().getId());
		}
		setDeparturePlatform(Platform.parse(jsonObject, "departure_platform"));
		if (getDeparturePlatform().isPresent()) {
			setDeparturePlatformId(getDeparturePlatform().get().getId());
		}
		setUser(User.parse(jsonObject, "user"));
		if (getUser().isPresent()) {
			setUserId(getUser().get().getId());
		}
	}

	public static Optional<ReservationCandidate> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.<ReservationCandidate>absent();
		}
		return Optional.<ReservationCandidate>of(new ReservationCandidate(jsonObject.getJSONObject(key)));
	}

	public static List<ReservationCandidate> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ReservationCandidate>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		List<ReservationCandidate> models = new LinkedList<ReservationCandidate>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new ReservationCandidate(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	public static class ResponseConverter implements
			WebAPI.ResponseConverter<ReservationCandidate> {
		@Override
		public ReservationCandidate convert(byte[] rawResponse) throws JSONException, ParseException {
			return new ReservationCandidate(new JSONObject(new String(rawResponse)));
		}
	}

	public static class ListResponseConverter implements
			WebAPI.ResponseConverter<List<ReservationCandidate>> {
		@Override
		public List<ReservationCandidate> convert(byte[] rawResponse) throws JSONException,
				ParseException {
			JSONArray array = new JSONArray(new String(rawResponse));
			List<ReservationCandidate> models = new LinkedList<ReservationCandidate>();
			for (Integer i = 0; i < array.length(); ++i) {
				if (array.isNull(i)) {
					continue;
				}
				JSONObject object = array.getJSONObject(i);
				models.add(new ReservationCandidate(object));
			}
			return models;
		}
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatformId()));
		jsonObject.put("arrival_time", toJSON(getArrivalTime()));
		jsonObject.put("created_at", toJSON(getCreatedAt()));
		jsonObject.put("deleted_at", toJSON(getDeletedAt().orNull()));
		jsonObject.put("demand_id", toJSON(getDemandId().orNull()));
		jsonObject.put("departure_platform_id", toJSON(getDeparturePlatformId()));
		jsonObject.put("departure_time", toJSON(getDepartureTime()));
		jsonObject.put("id", toJSON(getId()));
		jsonObject.put("passenger_count", toJSON(getPassengerCount()));
		jsonObject.put("service_provider_id", toJSON(getServiceProviderId().orNull()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_id", toJSON(getUserId().orNull()));
		jsonObject.put("arrival_platform", toJSON(getArrivalPlatform()));
		if (getArrivalPlatform().isPresent()) {
			jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatform().get().getId()));
		}
		jsonObject.put("demand", toJSON(getDemand()));
		if (getDemand().isPresent()) {
			jsonObject.put("demand_id", toJSON(getDemand().get().getId()));
		}
		jsonObject.put("departure_platform", toJSON(getDeparturePlatform()));
		if (getDeparturePlatform().isPresent()) {
			jsonObject.put("departure_platform_id", toJSON(getDeparturePlatform().get().getId()));
		}
		jsonObject.put("user", toJSON(getUser()));
		if (getUser().isPresent()) {
			jsonObject.put("user_id", toJSON(getUser().get().getId()));
		}
		return jsonObject;
	}

	private Long arrivalPlatformId = 0L;

	public Long getArrivalPlatformId() {
		return wrapNull(arrivalPlatformId);
	}

	public void setArrivalPlatformId(Long arrivalPlatformId) {
		this.arrivalPlatformId = wrapNull(arrivalPlatformId);
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

	private Optional<Long> demandId = Optional.<Long>absent();

	public Optional<Long> getDemandId() {
		return wrapNull(demandId);
	}

	public void setDemandId(Optional<Long> demandId) {
		this.demandId = wrapNull(demandId);
	}

	public void setDemandId(Long demandId) {
		this.demandId = Optional.fromNullable(demandId);
	}

	public void clearDemandId() {
		this.demandId = Optional.<Long>absent();
	}

	private Long departurePlatformId = 0L;

	public Long getDeparturePlatformId() {
		return wrapNull(departurePlatformId);
	}

	public void setDeparturePlatformId(Long departurePlatformId) {
		this.departurePlatformId = wrapNull(departurePlatformId);
	}

	private Date departureTime = new Date();

	public Date getDepartureTime() {
		return wrapNull(departureTime);
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = wrapNull(departureTime);
	}

	private Long id = 0L;

	public Long getId() {
		return wrapNull(id);
	}

	public void setId(Long id) {
		this.id = wrapNull(id);
	}

	private Long passengerCount = 0L;

	public Long getPassengerCount() {
		return wrapNull(passengerCount);
	}

	public void setPassengerCount(Long passengerCount) {
		this.passengerCount = wrapNull(passengerCount);
	}

	private Optional<Long> serviceProviderId = Optional.<Long>absent();

	public Optional<Long> getServiceProviderId() {
		return wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Optional<Long> serviceProviderId) {
		this.serviceProviderId = wrapNull(serviceProviderId);
	}

	public void setServiceProviderId(Long serviceProviderId) {
		this.serviceProviderId = Optional.fromNullable(serviceProviderId);
	}

	public void clearServiceProviderId() {
		this.serviceProviderId = Optional.<Long>absent();
	}

	private Optional<Long> unitAssignmentId = Optional.<Long>absent();

	public Optional<Long> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Long> unitAssignmentId) {
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Long unitAssignmentId) {
		this.unitAssignmentId = Optional.fromNullable(unitAssignmentId);
	}

	public void clearUnitAssignmentId() {
		this.unitAssignmentId = Optional.<Long>absent();
	}

	private Date updatedAt = new Date();

	public Date getUpdatedAt() {
		return wrapNull(updatedAt);
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = wrapNull(updatedAt);
	}

	private Optional<Long> userId = Optional.<Long>absent();

	public Optional<Long> getUserId() {
		return wrapNull(userId);
	}

	public void setUserId(Optional<Long> userId) {
		this.userId = wrapNull(userId);
	}

	public void setUserId(Long userId) {
		this.userId = Optional.fromNullable(userId);
	}

	public void clearUserId() {
		this.userId = Optional.<Long>absent();
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
