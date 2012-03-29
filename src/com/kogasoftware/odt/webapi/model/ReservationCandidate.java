package com.kogasoftware.odt.webapi.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Optional;

public class ReservationCandidate extends Model {
	private static final long serialVersionUID = 7747157152042923142L;

	public ReservationCandidate() {
	}

	public ReservationCandidate(JSONObject jsonObject) throws JSONException, ParseException {
		setAccuracy(parseOptionalFloat(jsonObject, "accuracy"));
		setArrivalPlatformId(parseInteger(jsonObject, "arrival_platform_id"));
		setArrivalTime(parseDate(jsonObject, "arrival_time"));
		setCreatedAt(parseDate(jsonObject, "created_at"));
		setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		setDemandId(parseOptionalInteger(jsonObject, "demand_id"));
		setDeparturePlatformId(parseInteger(jsonObject, "departure_platform_id"));
		setDepartureTime(parseDate(jsonObject, "departure_time"));
		setId(parseInteger(jsonObject, "id"));
		setPassengerCount(parseInteger(jsonObject, "passenger_count"));
		setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		setUpdatedAt(parseDate(jsonObject, "updated_at"));
		setUserId(parseOptionalInteger(jsonObject, "user_id"));
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

	public static LinkedList<ReservationCandidate> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ReservationCandidate>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		LinkedList<ReservationCandidate> models = new LinkedList<ReservationCandidate>();
		for (Integer i = 0; i < jsonArray.length(); ++i) {
			if (jsonArray.isNull(i)) {
				continue;
			}
			models.add(new ReservationCandidate(jsonArray.getJSONObject(i)));
		}
		return models;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("accuracy", toJSON(getAccuracy().orNull()));
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

	private Optional<Float> accuracy = Optional.<Float>absent();

	public Optional<Float> getAccuracy() {
		return wrapNull(accuracy);
	}

	public void setAccuracy(Optional<Float> accuracy) {
		this.accuracy = wrapNull(accuracy);
	}

	public void setAccuracy(Float accuracy) {
		this.accuracy = Optional.fromNullable(accuracy);
	}

	public void clearAccuracy() {
		this.accuracy = Optional.<Float>absent();
	}

	private Integer arrivalPlatformId = 0;

	public Integer getArrivalPlatformId() {
		return wrapNull(arrivalPlatformId);
	}

	public void setArrivalPlatformId(Integer arrivalPlatformId) {
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

	private Integer departurePlatformId = 0;

	public Integer getDeparturePlatformId() {
		return wrapNull(departurePlatformId);
	}

	public void setDeparturePlatformId(Integer departurePlatformId) {
		this.departurePlatformId = wrapNull(departurePlatformId);
	}

	private Date departureTime = new Date();

	public Date getDepartureTime() {
		return wrapNull(departureTime);
	}

	public void setDepartureTime(Date departureTime) {
		this.departureTime = wrapNull(departureTime);
	}

	private Integer id = 0;

	public Integer getId() {
		return wrapNull(id);
	}

	public void setId(Integer id) {
		this.id = wrapNull(id);
	}

	private Integer passengerCount = 0;

	public Integer getPassengerCount() {
		return wrapNull(passengerCount);
	}

	public void setPassengerCount(Integer passengerCount) {
		this.passengerCount = wrapNull(passengerCount);
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

	private Optional<Integer> unitAssignmentId = Optional.<Integer>absent();

	public Optional<Integer> getUnitAssignmentId() {
		return wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Optional<Integer> unitAssignmentId) {
		this.unitAssignmentId = wrapNull(unitAssignmentId);
	}

	public void setUnitAssignmentId(Integer unitAssignmentId) {
		this.unitAssignmentId = Optional.fromNullable(unitAssignmentId);
	}

	public void clearUnitAssignmentId() {
		this.unitAssignmentId = Optional.<Integer>absent();
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
