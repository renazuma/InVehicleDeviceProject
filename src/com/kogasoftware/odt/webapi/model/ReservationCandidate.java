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

public class ReservationCandidate extends Model {
	private static final long serialVersionUID = 8592886958612134850L;

	public ReservationCandidate() {
	}

	public ReservationCandidate(JSONObject jsonObject) throws JSONException {
		try {
			fillMembers(this, jsonObject);
		} catch (ParseException e) {
			throw new JSONException(e.toString() + "\n" + ExceptionUtils.getStackTrace(e));
		}
	}

	public static void fillMembers(ReservationCandidate model, JSONObject jsonObject) throws JSONException, ParseException {
		model.setAccuracy(parseOptionalFloat(jsonObject, "accuracy"));
		model.setArrivalPlatformId(parseInteger(jsonObject, "arrival_platform_id"));
		model.setArrivalTime(parseDate(jsonObject, "arrival_time"));
		model.setCreatedAt(parseDate(jsonObject, "created_at"));
		model.setDeletedAt(parseOptionalDate(jsonObject, "deleted_at"));
		model.setDemandId(parseOptionalInteger(jsonObject, "demand_id"));
		model.setDeparturePlatformId(parseInteger(jsonObject, "departure_platform_id"));
		model.setDepartureTime(parseDate(jsonObject, "departure_time"));
		model.setId(parseInteger(jsonObject, "id"));
		model.setPassengerCount(parseInteger(jsonObject, "passenger_count"));
		model.setServiceProviderId(parseOptionalInteger(jsonObject, "service_provider_id"));
		model.setStoppageTime(parseOptionalInteger(jsonObject, "stoppage_time"));
		model.setUnitAssignmentId(parseOptionalInteger(jsonObject, "unit_assignment_id"));
		model.setUpdatedAt(parseDate(jsonObject, "updated_at"));
		model.setUserId(parseOptionalInteger(jsonObject, "user_id"));
		model.setArrivalPlatform(Platform.parse(jsonObject, "arrival_platform"));
		model.setDemand(Demand.parse(jsonObject, "demand"));
		model.setDeparturePlatform(Platform.parse(jsonObject, "departure_platform"));
		model.setServiceProvider(ServiceProvider.parse(jsonObject, "service_provider"));
		model.setUnitAssignment(UnitAssignment.parse(jsonObject, "unit_assignment"));
		model.setUser(User.parse(jsonObject, "user"));
	}

	public static Optional<ReservationCandidate> parse(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return Optional.absent();
		}
		return parse(jsonObject.getJSONObject(key));
	}

	public static Optional<ReservationCandidate> parse(JSONObject jsonObject) throws JSONException, ParseException {
		return Optional.of(new ReservationCandidate(jsonObject));
	}

	public static LinkedList<ReservationCandidate> parseList(JSONObject jsonObject, String key) throws JSONException, ParseException {
		if (!jsonObject.has(key)) {
			return new LinkedList<ReservationCandidate>();
		}
		JSONArray jsonArray = jsonObject.getJSONArray(key);
		return parseList(jsonArray);
	}

	public static LinkedList<ReservationCandidate> parseList(JSONArray jsonArray) throws JSONException, ParseException {
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
	protected JSONObject toJSONObject(Boolean recursive, Integer depth) throws JSONException {
		depth++;
		if (depth > MAX_RECURSE_DEPTH) {
			return new JSONObject();
		}
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
		jsonObject.put("stoppage_time", toJSON(getStoppageTime().orNull()));
		jsonObject.put("unit_assignment_id", toJSON(getUnitAssignmentId().orNull()));
		jsonObject.put("updated_at", toJSON(getUpdatedAt()));
		jsonObject.put("user_id", toJSON(getUserId().orNull()));
		if (getArrivalPlatform().isPresent()) {
			if (recursive) {
				jsonObject.put("arrival_platform", getArrivalPlatform().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("arrival_platform_id", toJSON(getArrivalPlatform().get().getId()));
			}
		}
		if (getDemand().isPresent()) {
			if (recursive) {
				jsonObject.put("demand", getDemand().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("demand_id", toJSON(getDemand().get().getId()));
			}
		}
		if (getDeparturePlatform().isPresent()) {
			if (recursive) {
				jsonObject.put("departure_platform", getDeparturePlatform().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("departure_platform_id", toJSON(getDeparturePlatform().get().getId()));
			}
		}
		if (getServiceProvider().isPresent()) {
			if (recursive) {
				jsonObject.put("service_provider", getServiceProvider().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("service_provider_id", toJSON(getServiceProvider().get().getId()));
			}
		}
		if (getUnitAssignment().isPresent()) {
			if (recursive) {
				jsonObject.put("unit_assignment", getUnitAssignment().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("unit_assignment_id", toJSON(getUnitAssignment().get().getId()));
			}
		}
		if (getUser().isPresent()) {
			if (recursive) {
				jsonObject.put("user", getUser().get().toJSONObject(true, depth));
			} else {
				jsonObject.put("user_id", toJSON(getUser().get().getId()));
			}
		}
		return jsonObject;
	}

	private void writeObject(ObjectOutputStream objectOutputStream)
			throws IOException {
		try {
			objectOutputStream.writeObject(toJSONObject(true).toString());
		} catch (JSONException e) {
			throw new IOException(e);
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
	public ReservationCandidate cloneByJSON() throws JSONException {
		return new ReservationCandidate(toJSONObject(true));
	}

	private Optional<Float> accuracy = Optional.absent();

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
		this.accuracy = Optional.absent();
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

	private Optional<Integer> demandId = Optional.absent();

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
		this.demandId = Optional.absent();
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

	private Optional<Integer> serviceProviderId = Optional.absent();

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
		this.serviceProviderId = Optional.absent();
	}

	private Optional<Integer> stoppageTime = Optional.absent();

	public Optional<Integer> getStoppageTime() {
		return wrapNull(stoppageTime);
	}

	public void setStoppageTime(Optional<Integer> stoppageTime) {
		this.stoppageTime = wrapNull(stoppageTime);
	}

	public void setStoppageTime(Integer stoppageTime) {
		this.stoppageTime = Optional.fromNullable(stoppageTime);
	}

	public void clearStoppageTime() {
		this.stoppageTime = Optional.absent();
	}

	private Optional<Integer> unitAssignmentId = Optional.absent();

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
		this.unitAssignmentId = Optional.absent();
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
		this.userId = wrapNull(userId);
	}

	public void setUserId(Integer userId) {
		this.userId = Optional.fromNullable(userId);
	}

	public void clearUserId() {
		this.userId = Optional.absent();
	}

	private Optional<Platform> arrivalPlatform = Optional.absent();

	public Optional<Platform> getArrivalPlatform() {
		return wrapNull(arrivalPlatform);
	}

	public void setArrivalPlatform(Optional<Platform> arrivalPlatform) {
		this.arrivalPlatform = wrapNull(arrivalPlatform);
	}

	public void setArrivalPlatform(Platform arrivalPlatform) {
		this.arrivalPlatform = Optional.fromNullable(arrivalPlatform);
	}

	public void clearArrivalPlatform() {
		this.arrivalPlatform = Optional.absent();
	}

	private Optional<Demand> demand = Optional.absent();

	public Optional<Demand> getDemand() {
		return wrapNull(demand);
	}

	public void setDemand(Optional<Demand> demand) {
		this.demand = wrapNull(demand);
	}

	public void setDemand(Demand demand) {
		this.demand = Optional.fromNullable(demand);
	}

	public void clearDemand() {
		this.demand = Optional.absent();
	}

	private Optional<Platform> departurePlatform = Optional.absent();

	public Optional<Platform> getDeparturePlatform() {
		return wrapNull(departurePlatform);
	}

	public void setDeparturePlatform(Optional<Platform> departurePlatform) {
		this.departurePlatform = wrapNull(departurePlatform);
	}

	public void setDeparturePlatform(Platform departurePlatform) {
		this.departurePlatform = Optional.fromNullable(departurePlatform);
	}

	public void clearDeparturePlatform() {
		this.departurePlatform = Optional.absent();
	}

	private Optional<ServiceProvider> serviceProvider = Optional.absent();

	public Optional<ServiceProvider> getServiceProvider() {
		return wrapNull(serviceProvider);
	}

	public void setServiceProvider(Optional<ServiceProvider> serviceProvider) {
		this.serviceProvider = wrapNull(serviceProvider);
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = Optional.fromNullable(serviceProvider);
	}

	public void clearServiceProvider() {
		this.serviceProvider = Optional.absent();
	}

	private Optional<UnitAssignment> unitAssignment = Optional.absent();

	public Optional<UnitAssignment> getUnitAssignment() {
		return wrapNull(unitAssignment);
	}

	public void setUnitAssignment(Optional<UnitAssignment> unitAssignment) {
		this.unitAssignment = wrapNull(unitAssignment);
	}

	public void setUnitAssignment(UnitAssignment unitAssignment) {
		this.unitAssignment = Optional.fromNullable(unitAssignment);
	}

	public void clearUnitAssignment() {
		this.unitAssignment = Optional.absent();
	}

	private Optional<User> user = Optional.absent();

	public Optional<User> getUser() {
		return wrapNull(user);
	}

	public void setUser(Optional<User> user) {
		this.user = wrapNull(user);
	}

	public void setUser(User user) {
		this.user = Optional.fromNullable(user);
	}

	public void clearUser() {
		this.user = Optional.absent();
	}
}
